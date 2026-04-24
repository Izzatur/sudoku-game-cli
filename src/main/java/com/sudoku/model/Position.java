package com.sudoku.model;

public record Position(int row, int col) {

    public Position {
        if (row < 0 || row > 8) {
            throw new IllegalArgumentException("Row must be 0-8, got: " + row);
        }
        if (col < 0 || col > 8) {
            throw new IllegalArgumentException("Col must be 0-8, got: " + col);
        }
    }

    public static Position fromInput(char rowChar, int colNumber) {
        char normalized = Character.toUpperCase(rowChar);
        if (normalized < 'A' || normalized > 'I') {
            throw new IllegalArgumentException("Invalid row: " + rowChar);
        }
        if (colNumber < 1 || colNumber > 9) {
            throw new IllegalArgumentException("Invalid column: " + colNumber);
        }
        return new Position(normalized - 'A', colNumber - 1);
    }

    public String toDisplayString() {
        return String.valueOf((char) ('A' + row)) + (col + 1);
    }
}
