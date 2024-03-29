package com.psas;

import com.psas.cbws.CBWS;
import com.psas.function.Function;
import com.psas.translator.Translator;

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
                    3: Analyze a specific function.
                    4: Modify a specific function.
                    5: Remove a specific function.
                    6: Exit the program.
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
            case 3 -> {
                int index = promptIntegerResponse("Enter function index: ");
                cbws.printFunctionInfo(index);
            }
            case 4 -> {
                int index = promptIntegerResponse("Enter function index: ");
                cbws.printFunctionInfo(index);
                System.out.println();
                final Function function = cbws.getFunction(index);
                index = promptIntegerResponse("Enter attribute index: ");
                function.modifyAttribute(index);
            }
            case 5 -> {
                int index = promptIntegerResponse("Enter function index: ");
                final Function function = cbws.getFunction(index);
                function.removeFunction();
            }
            case 6 -> System.exit(0);
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