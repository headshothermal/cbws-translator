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
                     8: Modify a frame one function (not implemented).
                     9: Modify an intermediate function (not implemented).
                    10: Modify a final frame function (not implemented).
                    11: Modify an impact frame function (not implemented).
                    12: Remove a frame one function (not implemented).
                    13: Remove an intermediate function (not implemented).
                    14: Remove a final frame function (not implemented).
                    15: Remove an impact frame function (not implemented).
                    
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
}