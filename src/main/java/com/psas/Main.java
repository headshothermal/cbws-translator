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
        System.out.println("""
                Options:
                    1: Display file hex.
                    2: Display translated file info.
                    3: Analyze a specific function.
                    4: Modify a specific function.
                    5: Exit the program.
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
                final Function function = cbws.getFunction(index);
                index = promptIntegerResponse("Enter attribute index: ");
                function.modifyAttribute(index);
            }
            case 5 -> System.exit(0);
        }
    }

    public static int promptIntegerResponse(final String message) {
        int index;
        while (true) {
            try {
                System.out.print(message);
                index = SCANNER.nextInt();
                System.out.println();
                break;
            }
            catch (final InputMismatchException e) {
                System.out.println();
            }
        }
        return index;
    }
}