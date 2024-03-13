package com.psas.cbws;

import com.psas.function.Function;
import com.psas.translator.Translator;
import org.apache.commons.codec.DecoderException;

import java.io.*;
import java.util.ArrayList;

import static org.apache.commons.codec.binary.Hex.decodeHex;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

public class CBWS {
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

    /** Translator to parse file contents. */
    private final Translator translator;

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

    /** List to store functions contained in the file. */
    private final ArrayList<Function> functions = new ArrayList<>();

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
        translator = new Translator(this);
        read();
    }

    public final String getHex() {
        return hex;
    }

    public final Function getFunction(final int index) {
        if (index >= 0 && index < functions.size())
            return functions.get(index);
        return functions.get(0);
    }

    /**
     * Reads the CBWS file contents and converts it to a hex string. The file contents are then parsed to get header
     * information as well as teh functions contained in the CBWS file.
     */
    private void read() {
        // Open file.
        try (final FileInputStream stream = new FileInputStream(cbws)) {
            // Get raw bytes from file & store as encoded hex string.
            final byte[] bytes = new byte[(int) cbws.length()];
            stream.read(bytes);
            hex = encodeHexString(bytes).toUpperCase();

            // Translate file header.
            fileType = translator.getFileType(hex);
            final ArrayList<Integer> headerValues = translator.parseFileHeader(hex);
            unknownHeaderValue1 = headerValues.get(0);
            functionCount = headerValues.get(1);
            unknownHeaderValue2 = headerValues.get(2);

            // Ensure current function list is empty.
            functions.clear();

            // Translate function bytes & populate function list.
            functions.addAll(translator.parseFunctions(hex));
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

    /** Prints CBWS file info to terminal. */
    public final void printFileInfo() {
        System.out.printf("""
                Header Info
                    File Type: %s
                    Unknown Header Value: %d
                    Function Count: %d
                    Unknown Header Value: %d%n""",
                fileType, unknownHeaderValue1, functionCount, unknownHeaderValue2
        );
        for (int i = 0; i < functions.size(); i++) printFunctionInfo(i);
    }

    /**
     * Prints function info to terminal.
     *
     * @param index The index of the function.
     *
     * @see #functions
     */
    public final void printFunctionInfo(final int index) {
        if (index < 0 || index >= functions.size()) return;

        final Function function = functions.get(index);
        System.out.printf("%2d. %s", index, function);
    }
}
