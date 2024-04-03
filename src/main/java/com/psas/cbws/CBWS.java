package com.psas.cbws;

import com.psas.function.Function;
import org.apache.commons.codec.DecoderException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.codec.binary.Hex.decodeHex;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

public class CBWS {
    /** Generic strings for error handling. */
    private static final String UNKNOWN_FILE = "Unknown File Type";

    /** Strings for common hex values. */
    private static final String
        FOUR_NULL_BYTES = "00000000",
        FUNCTION_START_BYTES = "00000003";

    /**
     * Converts hex string to Float.
     *
     * @param hex The hex string to convert.
     *
     * @return Float value.
     */
    public static float getHexFloat(final String hex) {
        final long longValue = Long.parseLong(hex, 16);
        return Float.intBitsToFloat((int) longValue);
    }

    /**
     * Converts hex string to Integer.
     *
     * @param hex The hex string to convert.
     *
     * @return Integer value.
     */
    public static int getHexInt(final String hex) {
        return Integer.parseInt(hex, 16);
    }

    /**
     * Converts Float to hex string.
     *
     * @param value The float value to convert.
     *
     * @return Hex string.
     */
    public static String getFloatHex(final float value) {
        return String.format("%08X", Float.floatToIntBits(value));
    }

    /**
     * Converts Integer to hex string.
     *
     * @param value The integer value to convert.
     *
     * @return Hex string.
     */
    public static String getIntHex(final int value) {
        return String.format("%08X", value);
    }

    /**
     * Finds the nth occurrence of a substring in a string.
     *
     * @param string The string to search.
     * @param substring The substring to find.
     * @param n The occurrence to find.
     *
     * @return The index of the nth occurrence of the substring in the string or -1 if there is no nth occurrence.
     */
    protected static int findNthOccurrence(final String string, final String substring, final int n) {
        int lastIndex = 0;
        for (int i = 0; i < n; i++) {
            lastIndex = string.indexOf(substring, lastIndex);
            if (lastIndex < 0) return -1;
            lastIndex++;
        }
        return lastIndex;
    }

    /** File reference for CBWS script. */
    private final File cbws;

    /** File contents as a string of hex characters. */
    private String hex;

    /** String to store file type from file header. */
    private String fileType;

    /** Integer to store the 1st numerical value in the file header. The value's purpose is unknown. */
    private int unknownHeaderValue1;

    /** Integer to store 2nd numerical value in the file header. It represents the number of functions in the file. */
    private int functionCount;

    /** Integer to store the 3rd numerical value in the file header. The value's purpose is unknown. */
    private int unknownHeaderValue2;

    /** Lists to store functions contained in the file. */
    private final ArrayList<Function>
            firstFrameFunctions = new ArrayList<>(),
            intermediateFunctions = new ArrayList<>(),
            finalFrameFunctions = new ArrayList<>(),
            impactFrameFunctions = new ArrayList<>();

    /**
     * Constructs a new reference to a CBWS file.
     *
     * @param path The path to the CBWS script.
     *
     * @throws FileNotFoundException Indicates an invalid file path was provided.
     */
    public CBWS(final String path) throws FileNotFoundException {
        cbws = new File(path);
        if (!cbws.exists()) throw new FileNotFoundException(String.format("File \"%s\" not found!", path));
        read();
    }

    public final String getHex() {
        return hex;
    }

    /** Parses the file header to get the file type, function count, and unknown header values. */
    private void parseFileHeader() {
        // Get file type from header.
        try {  fileType = new String(decodeHex(hex.substring(0, 8)), StandardCharsets.UTF_8); }
        catch (final DecoderException e) { fileType = UNKNOWN_FILE; }

        // Get second header value. Its type is integer & its purpose is unknown. Altering the value seemingly has no effect.
        unknownHeaderValue1 = getHexInt(hex.substring(8, 16));

        /*
        Get function count from header. It is an integer value representing the number of functions in the file.
        Note that the 1st frame & final frame functions combined only add 1 to this count.
         */
        functionCount = getHexInt(hex.substring(16, 24));

        // Get third header value. Its type is integer & its purpose is unknown. Altering the value seemingly has no effect.
        unknownHeaderValue2 = getHexInt(hex.substring(24, 32));
    }

    /**
     * Returns the file header from the CBWS file.
     *
     * @return The file header.
     */
    private String getFileHeader() {
        return hex.substring(0, 32);
    }

    private void parseFunctions() {
        // Clear function list.
        intermediateFunctions.clear();

        // Copy file hex contents.
        String hex = new String(this.hex);

        // Remove file header from hex.
        hex = hex.substring(32);

        /*
        Regex pattern for identifying end of intermediate function.

        An intermediate function ends with a string of 15 null bytes (0x00) followed by a non-null byte. This non-null
        byte is an 8-bit integer representing the number of frames to wait before executing the next function in the file.

        Intermediate functions are executed in top-to-bottom order as they appear in the file.
         */
        final Pattern intermediateFunctionEndPattern = Pattern.compile("0{30}[A-F|0-9][A-F|1-9]$");

        /*
        Regex pattern for identifying end of first/final frame function.

        A first/final frame function ends with a string of 16 null bytes (0x00) in most cases. Exceptions are outlined below.

        First frame functions are executed before intermediate functions & final frame functions are executed after.

        First/final frame functions are all groups together at the end of the file. If reading top-to-bottom, a single final
        frame action will appear first, often EnableBreakout which allows the animation to cancel early. This function will
        have the typical end sequence of 15 null bytes followed by a non-null byte. In this case, the final byte does not
        represent a frame count & instead represents the number of functions that will be executed on frame 1.

        Following this function count, there will by 4 null bytes, then the sequence of frame-1 functions will begin.
        Frame-1 functions will be separated by 16 null bytes. The final frame-1 function will have the typical end
        sequence of 15 null bytes followed by a non-null byte. The final byte will represent the number of final-frame
        functions.

        Like before, 4 null bytes will follow the function count, then the sequence of final-frame functions will begin.
        Final-frame functions will also be separated by 16 null bytes. The last final-frame function can simply end with
        16 null bytes, leading to the EOF. It can also lead into another function chain for actions to take on successful
        hit. If there are impact actions, the last final-frame function will end with 15 null bytes followed by a non-null
        byte. Like before, he final byte will represent the number of impact functions to execute.

        If there are impact functions, 4 null bytes will follow the function count, then the sequence of impact functions
        will begin. Impact functions will be separated by 16 null bytes. The last impact function, however, will end with
        12 null bytes.
         */
        final Pattern firstFinalFrameFunctionEndPattern = Pattern.compile("0{32}$");
        final Pattern impactFrameFinalFunctionEndPattern = Pattern.compile("0{24}");

        // Booleans to track function parsing progress.
        boolean intermediate = true, firstFrame = false, finalFrame = false, impactFrame = false;

        // Parse functions.
        while (true) {
            final StringBuilder builder = new StringBuilder();
            int index = 0;
            while (true) {
                final String nextFourBytes;
                try { nextFourBytes = hex.substring(index, index + 8); }
                catch (final StringIndexOutOfBoundsException e) {
                    // Exception occurs when there are less than 8 bytes left in the hex string. Add final function.
                    builder.append(hex, index, hex.length());
                    if (finalFrame) finalFrameFunctions.add(new Function(builder.toString(), this));
                    else if (impactFrame) impactFrameFunctions.add(new Function(builder.toString(), this));
                    return;
                }

                // Check if current string ends with 15 null bytes followed by a non-null byte.
                final Matcher intermediateMatcher = intermediateFunctionEndPattern.matcher(builder.toString());
                if (intermediateMatcher.find()) {
                    if (intermediate) {
                        // If 4 null bytes follow, this is the first final-frame function.
                        if (nextFourBytes.equals(FOUR_NULL_BYTES)) {
                            finalFrameFunctions.add(new Function(builder.toString(), this));
                            intermediate = false;
                            firstFrame = true;
                            break;
                        }
                        else if (nextFourBytes.equals(FUNCTION_START_BYTES)) {
                            intermediateFunctions.add(new Function(builder.toString(), this));
                            break;
                        }
                    }
                    else if (firstFrame) {
                        if (nextFourBytes.equals(FOUR_NULL_BYTES)) {
                            firstFrameFunctions.add(new Function(builder.toString(), this));
                            firstFrame = false;
                            finalFrame = true;
                            break;
                        }
                    }
                    else if (finalFrame) {
                        if (nextFourBytes.equals(FOUR_NULL_BYTES)) {
                            finalFrameFunctions.add(new Function(builder.toString(), this));
                            finalFrame = false;
                            impactFrame = true;
                            break;
                        }
                    }
                }

                // Check if current string ends with 16 null bytes.
                final Matcher firstFinalMatcher = firstFinalFrameFunctionEndPattern.matcher(builder.toString());
                if (firstFinalMatcher.find()) {
                    if (firstFrame) {
                        if (nextFourBytes.equals(FUNCTION_START_BYTES)) {
                            firstFrameFunctions.add(new Function(builder.toString(), this));
                            break;
                        }
                    }
                    else if (finalFrame) {
                        if (nextFourBytes.equals(FUNCTION_START_BYTES)) {
                            finalFrameFunctions.add(new Function(builder.toString(), this));
                            break;
                        }
                    }
                    else if (impactFrame) {
                        if (nextFourBytes.equals(FUNCTION_START_BYTES)) {
                            impactFrameFunctions.add(new Function(builder.toString(), this));
                            break;
                        }
                    }
                }

                // Append current byte to function hex.
                builder.append(hex, index, index + 2);
                index += 2;
            }
            hex = hex.substring(index);
        }
    }

    /**
     * Gets the function at the specified index.
     *
     * @param index The index of the function.
     *
     * @return The function at the specified index or the 1st function in the file if an invalid index was specified.
     */
    public final Function getFunction(final int index) {
        if (index >= 0 && index < intermediateFunctions.size())
            return intermediateFunctions.get(index);
        return intermediateFunctions.get(0);
    }

    /**
     * Reads the CBWS file contents and converts it to a hex string. The file contents are then parsed to get header
     * information as well as the functions contained in the CBWS file.
     */
    private void read() {
        // Open file.
        try (final FileInputStream stream = new FileInputStream(cbws)) {
            // Get raw bytes from file & store as encoded hex string.
            final byte[] bytes = new byte[(int) cbws.length()];
            stream.read(bytes);
            hex = encodeHexString(bytes).toUpperCase();

            // Parse file contents.
            parseFileHeader();
            parseFunctions();
        }
        catch (final IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Replaces the specified text in the CBWS hex string and writes the new value to the file.
     *
     * @param original The original hex string.
     * @param replacement The replacement hex string.
     */
    public void replace(final String original, final String replacement) {
        // Verify replacement string exists in file.
        if (!hex.contains(original)) {
            System.out.printf("File \"%s\" does not contain hex string \"%s\".%n", cbws.getAbsolutePath(), original);
            return;
        }

        // Update hex string with replacement text.
        hex = hex.replace(original, replacement);

        // Open file.
        try (final FileOutputStream stream = new FileOutputStream(cbws, false)) {
            // Convert hex string to raw bytes & write to file.
            final byte[] bytes = decodeHex(hex);
            stream.write(bytes);
        }
        catch (final IOException | DecoderException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // After writing, read & parse file again.
        read();
    }

    /**
     * Replaces the nth occurrence of the specified text in the CBWS hex string and writes the new value to the file.
     *
     * @param original The original hex string.
     * @param replacement The replacement hex string.
     * @param n The occurrence to replace.
     */
    public void replaceNthOccurrence(final String original, final String replacement, final int n) {
        // Find nth occurrence of original string.
        final int index = findNthOccurrence(hex, original, n);
        if (index < 0) {
            System.out.printf("File \"%s\" does not contain %d occurrences of hex string \"%s\".%n", cbws.getAbsolutePath(), n, original);
            return;
        }

        // Update hex string with replacement text.
        hex = hex.substring(0, index) + replacement + hex.substring(index + original.length());

        // Open file.
        try (final FileOutputStream stream = new FileOutputStream(cbws, false)) {
            // Convert hex string to raw bytes & write to file.
            final byte[] bytes = decodeHex(hex);
            stream.write(bytes);
        }
        catch (final IOException | DecoderException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // After writing, read & parse file again.
        read();
    }

    /**
     * Replaces the function count in the CBWS hex string and writes the new value to the file.
     *
     * @param functionCount The new function count.
     */
    private void setFunctionCount(final int functionCount) {
        final String oldHex = getIntHex(this.functionCount);
        final String newHex = getIntHex(functionCount);
        final String newHeader = getFileHeader().replace(oldHex, newHex);
        hex = newHeader + hex.substring(32);
        this.functionCount = functionCount;
    }

    /** Increments the function count in the CBWS hex string and writes the new value to the file. */
    public void incrementFunctionCount() {
        setFunctionCount(functionCount + 1);
    }

    /** Decrements the function count in the CBWS hex string and writes the new value to the file. */
    public void decrementFunctionCount() {
        setFunctionCount(functionCount - 1);
    }

    /** Prints CBWS file info to terminal. */
    public final void printFileInfo() {
        printFileHeader();
        printFirstFrameFunctions();
        printIntermediateFunctions();
        printFinalFrameFunctions();
        printImpactFrameFunctions();
    }

    /** Prints file header to terminal. */
    public final void printFileHeader() {
        System.out.printf("""
                Header Info
                    File Type: %s
                    Unknown Header Value: %d
                    Function Count: %d
                    Unknown Header Value: %d%n""",
                fileType, unknownHeaderValue1, functionCount, unknownHeaderValue2
        );
    }

    /** Prints first frame functions to terminal. */
    public final void printFirstFrameFunctions() {
        System.out.println("First Frame Functions");
        for (int i = 0; i < firstFrameFunctions.size(); i++)
            System.out.printf("    %2d. %s", i, firstFrameFunctions.get(i));
    }

    /** Prints intermediate functions to terminal. */
    public final void printIntermediateFunctions() {
        System.out.println("Intermediate Functions");
        if (intermediateFunctions.isEmpty()) return;

        // Track frame data.
        int currentFrame = intermediateFunctions.get(0).getFrame();

        // Keep track of play rate to ensure accurate frame data.
        float playRate = 1.0f;

        for (int i = 0; i < intermediateFunctions.size(); i++) {
            // Determine current frame.
            final Function currentFunction = intermediateFunctions.get(i);
            final Function previousFunction = i > 0 ? intermediateFunctions.get(i - 1) : null;

            if (currentFunction.getLabel().equals("PlayRate"))
                playRate = Float.parseFloat(currentFunction.getAttributes().get(0).value());

            if (previousFunction != null)
                if (currentFunction.getFrame() != previousFunction.getFrame()) {
                    final int frameDifference = currentFunction.getFrame() - previousFunction.getFrame();
                    currentFrame += Math.round(frameDifference / playRate);
                }

            // Print function info.
            System.out.printf("    %2d. Frame %d:  %s", i, currentFrame, currentFunction);
        }
    }

    /**  Prints final frame functions to terminal. */
    public final void printFinalFrameFunctions() {
        System.out.println("Final Frame Functions");
        for (int i = 0; i < finalFrameFunctions.size(); i++)
            System.out.printf("    %2d. %s", i, finalFrameFunctions.get(i));
    }

    /** Prints impact frame functions to terminal. */
    public final void printImpactFrameFunctions() {
        System.out.println("Impact Frame Functions");
        for (int i = 0; i < impactFrameFunctions.size(); i++)
            System.out.printf("    %2d. %s", i, impactFrameFunctions.get(i));
    }
}
