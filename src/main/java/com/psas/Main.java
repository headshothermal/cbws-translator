package com.psas;

import com.psas.cbws.CBWS;
import com.psas.function.Function;

import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

import static com.psas.Args.getArguments;
import static com.psas.Args.setArguments;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(final String[] args) throws FileNotFoundException {
        // Parse command line args.
        setArguments(args);

        // Get file path from args.
        final String path = getArguments().getString("file");

        // Initialize parsed CBWS.
        final CBWS cbws = new CBWS(path);

        while (true) promptGlobalOptions(cbws);
    }

    private static void promptGlobalOptions(final CBWS cbws) {
        System.out.println();
        System.out.println("""
                Options:
                     1: Display file hex.
                     2: Display translated file info.
                     3: Display file header.
                     4: Display frame one functions.
                     5: Display intermediate functions.
                     6: Display final frame functions.
                     7: Display impact frame functions (there may be none).
                     8: Modify a frame one function.
                     9: Modify an intermediate function.
                    10: Modify a final frame function.
                    11: Modify an impact frame function.
                    12: Add a frame one function (not implemented).
                    13: Add an intermediate function (not implemented).
                    14: Add a final frame function (not implemented).
                    15: Add an impact frame function (not implemented).
                    16: Modify frame delay.
                    
                    98: Write changes to file.
                    99: Exit the program.
                """
        );

        int selection;
        while (true) {
            try {
                System.out.print("Enter a selection: ");
                selection = SCANNER.nextInt();
                System.out.println();
                break;
            }
            catch (final InputMismatchException e) {
                SCANNER.next();
                System.out.println();
            }
        }

        switch (selection) {
            case 1 -> System.out.println(cbws.getHex());
            case 2 -> cbws.printFileInfo();
            case 3 -> cbws.printFileHeader();
            case 4 -> cbws.printFirstFrameFunctions();
            case 5 -> cbws.printIntermediateFunctions();
            case 6 -> cbws.printFinalFrameFunctions();
            case 7 -> cbws.printImpactFrameFunctions();
            case 8 -> modifyFirstFrameFunction(cbws);
            case 9 -> modifyIntermediateFunction(cbws);
            case 10 -> modifyFinalFrameFunction(cbws);
            case 11 -> modifyImpactFrameFunction(cbws);

            case 16 -> {
                final int frameDelay = promptIntegerResponse("Enter new frame delay: ");
                cbws.setFrameDelay(frameDelay);
            }

            case 98 -> cbws.write();
            case 99 -> System.exit(0);
        }
    }

    public static int promptIntegerResponse(final String message) {
        int input;
        while (true) {
            try {
                System.out.print(message);
                input = SCANNER.nextInt();
                System.out.println();
                break;
            }
            catch (final InputMismatchException e) {
                SCANNER.next();
                System.out.println();
            }
        }
        return input;
    }

    public static float promptFloatResponse(final String message) {
        float input;
        while (true) {
            try {
                System.out.print(message);
                input = SCANNER.nextFloat();
                System.out.println();
                break;
            }
            catch (final InputMismatchException e) {
                SCANNER.next();
                System.out.println();
            }
        }
        return input;
    }

    /**
     * Prompts the user to modify a first frame function.
     *
     * @param cbws Reference to the CBWS object.
     */
    private static void modifyFirstFrameFunction(final CBWS cbws) {
        cbws.printFirstFrameFunctions();
        int index = promptIntegerResponse("Enter function index: ");

        final Function function = cbws.getFirstFrameFunction(index);
        System.out.println(function);
        index = promptIntegerResponse("Enter attribute index: ");
        function.modifyAttribute(index);
    }

    /**
     * Prompts the user to modify an intermediate function.
     *
     * @param cbws Reference to the CBWS object.
     */
    private static void modifyIntermediateFunction(final CBWS cbws) {
        cbws.printIntermediateFunctions();
        final int functionIndex = promptIntegerResponse("Enter function index: ");
        final Function function = cbws.getIntermediateFunction(functionIndex);
        System.out.println(function);

        System.out.println("""
                Options
                    1: Modify function attributes.
                    2: Modify function frame.
                    3: Change function index.
                    4: Remove this function (not implemented).
                """
        );
        final int selection = promptIntegerResponse("Enter a selection: ");

        switch (selection) {
            case 1 -> {
                final int attributeIndex = promptIntegerResponse("Enter attribute index: ");
                function.modifyAttribute(attributeIndex);
            }
            case 2 -> {
                System.out.println("""
                            Note: If the new frame does not respect the frame of the previous & next function, the game \
                            will simply execute it on the previous function's frame."""
                );
                System.out.println("Note: Functions do not store frame with PlayRate in mind.");
                while (true) {
                    System.out.printf("Current frame: %d%n", function.getFrame());
                    final int frame = promptIntegerResponse("Enter new frame (0-255): ");
                    if (frame < 0 || frame > 255) continue;
                    function.setFrame((byte) frame);
                    break;
                }
            }
            case 3 -> {
                final int newIndex = promptIntegerResponse("Enter new function index: ");
                cbws.changeFunctionIndex(functionIndex, newIndex);
            }
        }
    }

    /**
     * Prompts the user to modify a final frame function.
     *
     * @param cbws Reference to the CBWS object.
     */
    private static void modifyFinalFrameFunction(final CBWS cbws) {
        cbws.printFinalFrameFunctions();
        int index = promptIntegerResponse("Enter function index: ");

        final Function function = cbws.getFinalFrameFunction(index);
        System.out.println(function);
        index = promptIntegerResponse("Enter attribute index: ");
        function.modifyAttribute(index);
    }

    /**
     * Prompts the user to modify an impact frame function.
     *
     * @param cbws Reference to the CBWS object.
     */
    private static void modifyImpactFrameFunction(final CBWS cbws) {
        cbws.printImpactFrameFunctions();
        int index = promptIntegerResponse("Enter function index: ");

        final Function function = cbws.getImpactFrameFunction(index);
        System.out.println(function);
        index = promptIntegerResponse("Enter attribute index: ");
        function.modifyAttribute(index);
    }
}