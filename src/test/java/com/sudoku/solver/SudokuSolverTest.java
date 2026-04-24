package com.sudoku.solver;

import com.sudoku.model.Grid;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class SudokuSolverTest {
    private final SudokuSolver solver = new SudokuSolver(new Random(42));

    @Test
    void solvesEmptyGrid() {
        Grid grid = new Grid();
        boolean solved = solver.solve(grid);
        assertThat(solved).isTrue();
        assertThat(grid.isSolved()).isTrue();
    }

    @Test
    void solvedGridHasNoRowDuplicates() {
        Grid grid = new Grid();
        solver.solve(grid);
        for (int r = 0; r < 9; r++) {
            assertThat(uniqueValues(rowValues(grid, r))).isEqualTo(9);
        }
    }

    @Test
    void solvedGridHasNoColumnDuplicates() {
        Grid grid = new Grid();
        solver.solve(grid);
        for (int c = 0; c < 9; c++) {
            assertThat(uniqueValues(colValues(grid, c))).isEqualTo(9);
        }
    }

    @Test
    void solvedGridHasNoSubgridDuplicates() {
        Grid grid = new Grid();
        solver.solve(grid);
        for (int br = 0; br < 3; br++) {
            for (int bc = 0; bc < 3; bc++) {
                assertThat(uniqueValues(subgridValues(grid, br * 3, bc * 3))).isEqualTo(9);
            }
        }
    }

    @Test
    void returnsFalseForUnsolvableGrid() {
        // Fill every cell except (4,4). Row 4 is missing only 5, but col 4 already
        // has 5 (placed at row 0) — so (4,4) has no valid digit. Solver returns false instantly.
        Grid grid = new Grid();
        int[][] values = {
            {5, 3, 4, 6, 5, 8, 9, 1, 2},  // (0,4)=5 deliberately blocks col 4 for (4,4)
            {6, 7, 2, 1, 9, 4, 3, 8, 3},  // all non-zero; solver skips this entire row
            {1, 9, 8, 3, 4, 2, 6, 5, 7},
            {8, 2, 9, 7, 6, 1, 4, 3, 6},  // all non-zero
            {4, 2, 6, 8, 0, 3, 7, 9, 1},  // (4,4) is the ONLY empty cell
            {7, 1, 3, 9, 2, 4, 8, 6, 5},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (values[r][c] != 0) {
                    grid.setCell(r, c, values[r][c]);
                }
            }
        }
        // (4,4) is empty; row 4 needs 5 but col 4 already has 5 at row 0
        assertThat(solver.solve(grid)).isFalse();
    }

    private int[] rowValues(Grid grid, int row) {
        int[] values = new int[9];
        for (int c = 0; c < 9; c++) values[c] = grid.getCell(row, c).getValue();
        return values;
    }

    private int[] colValues(Grid grid, int col) {
        int[] values = new int[9];
        for (int r = 0; r < 9; r++) values[r] = grid.getCell(r, col).getValue();
        return values;
    }

    private int[] subgridValues(Grid grid, int startRow, int startCol) {
        int[] values = new int[9];
        int i = 0;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                values[i++] = grid.getCell(r, c).getValue();
            }
        }
        return values;
    }

    private int uniqueValues(int[] values) {
        Set<Integer> set = new HashSet<>();
        for (int v : values) set.add(v);
        return set.size();
    }
}
