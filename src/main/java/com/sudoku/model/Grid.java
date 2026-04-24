package com.sudoku.model;

public class Grid {
    private final Cell[][] cells;

    public Grid() {
        cells = new Cell[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                cells[r][c] = new Cell();
            }
        }
    }

    private Grid(Cell[][] cells) {
        this.cells = cells;
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public void setCell(int row, int col, int value) {
        cells[row][col].setValue(value);
    }

    public void clearCell(int row, int col) {
        cells[row][col].clear();
    }

    /** Places a cell with an explicit pre-filled flag — used only during puzzle generation. */
    public void initCell(int row, int col, int value, boolean preFilled) {
        cells[row][col] = new Cell(value, preFilled);
    }

    /** Removes a pre-filled cell during puzzle generation (makes it empty and user-editable). */
    public void removeCell(int row, int col) {
        cells[row][col] = new Cell(0, false);
    }

    /** Restores a cell as pre-filled — used when uniqueness check fails during generation. */
    public void restoreCell(int row, int col, int value) {
        cells[row][col] = new Cell(value, true);
    }

    public boolean isSolved() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (cells[r][c].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Grid deepCopy() {
        Cell[][] copy = new Cell[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                copy[r][c] = new Cell(cells[r][c].getValue(), cells[r][c].isPreFilled());
            }
        }
        return new Grid(copy);
    }
}
