package com.sudoku.ui;

import com.sudoku.model.Grid;

public class ConsoleRenderer {

    public void renderWelcome() {
        System.out.println("Welcome to Sudoku!");
        System.out.println();
        System.out.println("Here is your puzzle:");
    }

    public void renderGrid(Grid grid) {
        System.out.println("    1 2 3 4 5 6 7 8 9");
        for (int r = 0; r < 9; r++) {
            char rowLabel = (char) ('A' + r);
            StringBuilder row = new StringBuilder("  ");
            row.append(rowLabel).append(' ');
            for (int c = 0; c < 9; c++) {
                int value = grid.getCell(r, c).getValue();
                row.append(value == 0 ? "_" : String.valueOf(value));
                if (c < 8) row.append(' ');
            }
            System.out.println(row);
        }
        System.out.println();
    }

    public void renderMessage(String message) {
        System.out.println(message);
        System.out.println();
    }

    public void renderError(String message) {
        System.out.println(message);
        System.out.println();
    }

    public void renderPrompt() {
        System.out.print("Enter command (e.g., A3 4, C5 clear, hint, check, quit): ");
    }

    public void renderWin() {
        System.out.println("You have successfully completed the Sudoku puzzle!");
        System.out.println();
    }

    public void renderPlayAgainPrompt() {
        System.out.print("Press any key to play again...");
    }
}
