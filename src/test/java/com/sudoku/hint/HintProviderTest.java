package com.sudoku.hint;

import com.sudoku.model.Grid;
import com.sudoku.model.Hint;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

class HintProviderTest {
    private final HintProvider provider = new HintProvider(new Random(42));

    @Test
    void returnsHintForEmptyCell() {
        Grid solution = buildFullSolution();
        Grid puzzle = new Grid(); // all empty
        Optional<Hint> hint = provider.provide(puzzle, solution);
        assertThat(hint).isPresent();
    }

    @Test
    void hintValueMatchesSolution() {
        Grid solution = buildFullSolution();
        Grid puzzle = new Grid();
        Optional<Hint> hint = provider.provide(puzzle, solution);
        assertThat(hint).isPresent();
        Hint h = hint.get();
        int expectedValue = solution.getCell(h.position().row(), h.position().col()).getValue();
        assertThat(h.value()).isEqualTo(expectedValue);
    }

    @Test
    void returnsEmptyWhenGridIsFull() {
        Grid solution = buildFullSolution();
        Grid puzzle = buildFullSolution();
        Optional<Hint> hint = provider.provide(puzzle, solution);
        assertThat(hint).isEmpty();
    }

    @Test
    void ignoresCellsAlreadyFilledInPuzzle() {
        Grid solution = buildFullSolution();
        Grid puzzle = new Grid();
        // Fill all but last cell
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (r == 8 && c == 8) continue;
                puzzle.setCell(r, c, solution.getCell(r, c).getValue());
            }
        }
        Optional<Hint> hint = provider.provide(puzzle, solution);
        assertThat(hint).isPresent();
        assertThat(hint.get().position().row()).isEqualTo(8);
        assertThat(hint.get().position().col()).isEqualTo(8);
    }

    private Grid buildFullSolution() {
        Grid g = new Grid();
        int[][] values = {
            {5,3,4,6,7,8,9,1,2},
            {6,7,2,1,9,5,3,4,8},
            {1,9,8,3,4,2,5,6,7},
            {8,5,9,7,6,1,4,2,3},
            {4,2,6,8,5,3,7,9,1},
            {7,1,3,9,2,4,8,5,6},
            {9,6,1,5,3,7,2,8,4},
            {2,8,7,4,1,9,6,3,5},
            {3,4,5,2,8,6,1,7,9}
        };
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                g.setCell(r, c, values[r][c]);
            }
        }
        return g;
    }
}
