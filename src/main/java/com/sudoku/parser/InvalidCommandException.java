package com.sudoku.parser;

public class InvalidCommandException extends RuntimeException {
    private final String input;

    public InvalidCommandException(String input) {
        super("Invalid command: \"" + input + "\"");
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}
