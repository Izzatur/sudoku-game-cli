package com.sudoku.solver;

import com.sudoku.model.Grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuSolver {
    private final Random random;

    public SudokuSolver() {
        this(new Random());
    }

    public SudokuSolver(Random random) {
        this.random = random;
    }

    public boolean solve(Grid grid) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid.getCell(row, col).isEmpty()) {
                    List<Integer> digits = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
                    Collections.shuffle(digits, random);
                    for (int digit : digits) {
                        if (isSafe(grid, row, col, digit)) {
                            grid.getCell(row, col).setValue(digit);
                            if (solve(grid)) {
                                return true;
                            }
                            grid.getCell(row, col).clear();
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSafe(Grid grid, int row, int col, int digit) {
        for (int c = 0; c < 9; c++) {
            if (grid.getCell(row, c).getValue() == digit) return false;
        }
        for (int r = 0; r < 9; r++) {
            if (grid.getCell(r, col).getValue() == digit) return false;
        }
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (grid.getCell(r, c).getValue() == digit) return false;
            }
        }
        return true;
    }
}
