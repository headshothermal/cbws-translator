package com.psas;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Args {
    /** Parser to interpret arguments. */
    private static final ArgumentParser parser = ArgumentParsers
            .newFor("cbws-translator.jar")
            .build()
            .description("Program to translate CBWS files for PlayStations All Stars Battle Royale");

    /** Namespace containing argument definitions. */
    private static volatile Namespace arguments;

    static {
        parser.addArgument("--file", "-f")
                .action(Arguments.store())
                .help("Path to file to translate.")
                .metavar("path/to/file.cbws")
                .required(true)
                .type(String.class);
    }

    /**
     * Parses the provided arguments and creates a namespace with their definitions.
     *
     * @param args The arguments - this should always come from main().
     */
    public static void setArguments(final String[] args) {
        try { arguments = parser.parseArgs(args); }
        catch (final ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

    /**
     * Returns a namespace containing definitions for the parsed arguments.
     *
     * @return Namespace containing the parsed arguments.
     */
    public static Namespace getArguments() {
        return arguments;
    }
}
