package com.sudoku.model;

public class Cell {
    private int value;
    private final boolean preFilled;

    public Cell() {
        this.value = 0;
        this.preFilled = false;
    }

    public Cell(int value, boolean preFilled) {
        this.value = value;
        this.preFilled = preFilled;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public boolean isPreFilled() {
        return preFilled;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (preFilled) {
            throw new IllegalStateException("Cannot modify a pre-filled cell");
        }
        this.value = value;
    }

    public void clear() {
        if (preFilled) {
            throw new IllegalStateException("Cannot clear a pre-filled cell");
        }
        this.value = 0;
    }
}
