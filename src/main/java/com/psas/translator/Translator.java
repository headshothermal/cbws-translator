package com.psas.translator;

import com.psas.cbws.CBWS;
import com.psas.function.Function;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.codec.binary.Hex.decodeHex;

public class Translator {
    /** Generic string for unknown functions. */
    private static final String UNKNOWN_FUNCTION = "Unknown function";

    // Known function labels.
    private static final String
        FUNCTION_START = "00000003",
        ENABLE_HIT_VOLUME = "0F456E61626C65486974566F6C756D65",
        PLAY_RATE = "506C617952617465",
        SET_ARMOR = "53657441726D6F72";

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
     * Identifies the function label by converting the hex to ASCII and using a regex pattern to find the label.
     *
     * @param hex The hex to parse.
     *
     * @return The function label.
     */
    protected static String identifyFunctionLabel(final String hex) {
        // Initialize pattern to find function label.
        // The regex specifies that the label must contain only letters in UpperCamelCase format.
        final Pattern pattern = Pattern.compile("([A-Z][a-z]+)+");

        // Convert hex string to ASCII.
        final String ascii;
        try { ascii = new String(decodeHex(hex), StandardCharsets.UTF_8); }
        catch (final DecoderException e) {
            e.printStackTrace();
            System.exit(1);
            return UNKNOWN_FUNCTION;  // Unreachable but compiler still requires a return to us ascii var later.
        }

        // Iterate over matches until a function label is found.
        final Matcher matcher = pattern.matcher(ascii);
        while (matcher.find()) {
            // If match length is less than 4, assume it is not a function label.
            if (matcher.end() - matcher.start() < 4) continue;
            return ascii.substring(matcher.start(), matcher.end());
        }
        return UNKNOWN_FUNCTION;
    }

    /** Reference to file being translated. */
    private final CBWS cbws;

    /**
     * Constructs a new translator for the specified CBWS file.
     *
     * @param cbws The file to translate.
     */
    public Translator(final CBWS cbws) {
        this.cbws = cbws;
    }

    /**
     * Decodes the file type label from the file header.
     *
     * @param hex The file hex string.
     *
     * @return The decoded string.
     */
    public final String getFileType(final String hex) {
        try {  return new String(decodeHex(hex.substring(0, 8)), StandardCharsets.UTF_8); }
        catch (final DecoderException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return "";
    }

    /**
     * Parses file header to determine values. Headers contain 3 values, not including the file type.
     *
     * @param hex The file hex string.
     *
     * @return List containing the decoded values.
     */
    public final ArrayList<Integer> parseFileHeader(final String hex) {
        final ArrayList<Integer> values = new ArrayList<>(3);
        values.add(getHexInt(hex.substring(8, 16)));
        values.add(getHexInt(hex.substring(16, 24)));
        values.add(getHexInt(hex.substring(24, 32)));
        return values;
    }

    /**
     * Parses file hex to identify header values and functions contained in the file.
     *
     * @param hex The hex contents to parse.
     */
    public final ArrayList<Function> parseFunctions(String hex) {
        // Allocate list to store functions.
        final ArrayList<Function> functions = new ArrayList<>();

        // Remove file header from hex.
        hex = hex.substring(32);

        // Parse functions.
        while (true) {
            final StringBuilder builder = new StringBuilder();
            int index = 0;
            while (true) {
                // If EOF reached, append final byte & match remaining hex.
                if (index + 2 >= hex.length()) {
                    builder.append(hex, index, index + 2);
                    addFunction(functions, builder.toString());

                    // Functions are execute in reverse order of how they appear in the file. Reverse list to reflect true order.
                    Collections.reverse(functions);
                    return functions;
                }

                // Check if next 4 bytes are 00 00 00 03. If current index is 0, this can be ignored.
                if (hex.startsWith(FUNCTION_START, index) && index > 0) {
                    // Check if this happens to be ending byte string. If it is, bytes 5-8 will be identical.
                    if (hex.startsWith(FUNCTION_START, index + FUNCTION_START.length())) {
                        // If initial 4 bytes are an end string, append them to current function hex.
                        builder.append(hex, index, index + 8);

                        // Match current function & move on to next.
                        addFunction(functions, builder.toString());
                        index += 8;
                        break;
                    }
                    // Otherwise, the end of the current function has been reached.
                    addFunction(functions, builder.toString());
                    break;
                }

                // Append current byte to function hex.
                builder.append(hex, index, index + 2);
                index += 2;
            }
            hex = hex.substring(index);
        }
    }

    private void addFunction(final ArrayList<Function> functions, final String hex) {
        final String label = identifyFunctionLabel(hex);
        functions.add(new Function(label, hex, cbws));
    }
}
