package com.sudoku.ui;

import java.util.Scanner;

public class ConsoleInputReader implements AutoCloseable {
    private final Scanner scanner;

    public ConsoleInputReader() {
        this.scanner = new Scanner(System.in);
    }

    public String readLine() {
        return scanner.nextLine().trim();
    }

    public void waitForKeyPress() {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    @Override
    public void close() {
        scanner.close();
    }
}
