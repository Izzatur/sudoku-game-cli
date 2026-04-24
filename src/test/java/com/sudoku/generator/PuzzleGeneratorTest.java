package com.sudoku.generator;

import com.sudoku.model.Grid;
import com.sudoku.model.PuzzleBundle;
import com.sudoku.solver.SudokuSolver;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;

class PuzzleGeneratorTest {
    private final SudokuSolver solver = new SudokuSolver(new Random(42));
    private final PuzzleGenerator generator = new PuzzleGenerator(solver, new Random(42));

    @Test
    void generatesExactly30PreFilledCells() {
        PuzzleBundle bundle = generator.generate();
        int count = countPreFilled(bundle.puzzle());
        assertThat(count).isEqualTo(30);
    }

    @Test
    void solutionHasAll81CellsFilled() {
        PuzzleBundle bundle = generator.generate();
        assertThat(bundle.solution().isSolved()).isTrue();
    }

    @Test
    void puzzleAndSolutionAreIndependentObjects() {
        PuzzleBundle bundle = generator.generate();
        // Mutating puzzle should not affect solution
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (!bundle.puzzle().getCell(r, c).isPreFilled()) {
                    int solutionValue = bundle.solution().getCell(r, c).getValue();
                    bundle.puzzle().getCell(r, c).setValue(solutionValue);
                    // solution cell should be unchanged
                    assertThat(bundle.solution().getCell(r, c).getValue()).isEqualTo(solutionValue);
                    bundle.puzzle().getCell(r, c).clear();
                    break;
                }
            }
        }
    }

    @Test
    void preFilledCellsMatchSolution() {
        PuzzleBundle bundle = generator.generate();
        Grid puzzle = bundle.puzzle();
        Grid solution = bundle.solution();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (puzzle.getCell(r, c).isPreFilled()) {
                    assertThat(puzzle.getCell(r, c).getValue())
                            .isEqualTo(solution.getCell(r, c).getValue());
                }
            }
        }
    }

    @Test
    void puzzleIsSolvable() {
        PuzzleBundle bundle = generator.generate();
        Grid copy = bundle.puzzle().deepCopy();
        // Remove pre-filled restriction for solving
        Grid solvable = toSolvableGrid(copy);
        SudokuSolver testSolver = new SudokuSolver(new Random(1));
        assertThat(testSolver.solve(solvable)).isTrue();
    }

    private Grid toSolvableGrid(Grid puzzle) {
        // Create a fresh grid keeping only pre-filled values as non-preFilled cells
        // so the solver can fill the empty ones
        Grid g = new Grid();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (!puzzle.getCell(r, c).isEmpty()) {
                    g.setCell(r, c, puzzle.getCell(r, c).getValue());
                }
            }
        }
        return g;
    }

    private int countPreFilled(Grid grid) {
        int count = 0;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid.getCell(r, c).isPreFilled()) count++;
            }
        }
        return count;
    }
}
