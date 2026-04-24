package com.sudoku.generator;

import com.sudoku.model.Grid;
import com.sudoku.model.PuzzleBundle;
import com.sudoku.solver.SudokuSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PuzzleGenerator {
    private static final int TARGET_PRE_FILLED = 30;

    private final SudokuSolver solver;
    private final Random random;

    public PuzzleGenerator(SudokuSolver solver) {
        this(solver, new Random());
    }

    public PuzzleGenerator(SudokuSolver solver, Random random) {
        this.solver = solver;
        this.random = random;
    }

    public PuzzleBundle generate() {
        // Step 1: generate a complete, valid solution board
        Grid solution = new Grid();
        solver.solve(solution);

        // Step 2: save the solution (all cells non-pre-filled — it's a reference board)
        Grid savedSolution = solution.deepCopy();

        // Step 3: build the puzzle starting from a fully pre-filled copy
        Grid puzzle = copyAsPreFilled(solution);

        // Step 4: remove cells while maintaining a unique solution
        List<int[]> positions = allPositions();
        Collections.shuffle(positions, random);

        int preFilled = 81;
        for (int[] pos : positions) {
            if (preFilled <= TARGET_PRE_FILLED) break;

            int row = pos[0], col = pos[1];
            int savedValue = puzzle.getCell(row, col).getValue();

            puzzle.removeCell(row, col);
            preFilled--;

            if (countSolutions(puzzle) != 1) {
                puzzle.restoreCell(row, col, savedValue);
                preFilled++;
            }
        }

        return new PuzzleBundle(puzzle, savedSolution);
    }

    private Grid copyAsPreFilled(Grid source) {
        Grid copy = new Grid();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                copy.initCell(r, c, source.getCell(r, c).getValue(), true);
            }
        }
        return copy;
    }

    private int countSolutions(Grid grid) {
        return countSolutions(grid.deepCopy(), 0);
    }

    private int countSolutions(Grid grid, int count) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid.getCell(row, col).isEmpty()) {
                    for (int digit = 1; digit <= 9; digit++) {
                        if (isSafe(grid, row, col, digit)) {
                            grid.getCell(row, col).setValue(digit);
                            count = countSolutions(grid, count);
                            grid.getCell(row, col).clear();
                            if (count >= 2) return count;
                        }
                    }
                    return count;
                }
            }
        }
        return count + 1;
    }

    private boolean isSafe(Grid grid, int row, int col, int digit) {
        for (int c = 0; c < 9; c++) {
            if (grid.getCell(row, c).getValue() == digit) return false;
        }
        for (int r = 0; r < 9; r++) {
            if (grid.getCell(r, col).getValue() == digit) return false;
        }
        int sr = (row / 3) * 3, sc = (col / 3) * 3;
        for (int r = sr; r < sr + 3; r++) {
            for (int c = sc; c < sc + 3; c++) {
                if (grid.getCell(r, c).getValue() == digit) return false;
            }
        }
        return true;
    }

    private List<int[]> allPositions() {
        List<int[]> positions = new ArrayList<>(81);
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                positions.add(new int[]{r, c});
            }
        }
        return positions;
    }
}
