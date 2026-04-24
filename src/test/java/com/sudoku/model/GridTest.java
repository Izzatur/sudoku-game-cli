package com.sudoku.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GridTest {

    @Test
    void newGridHasAllCellsEmpty() {
        Grid grid = new Grid();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                assertThat(grid.getCell(r, c).isEmpty()).isTrue();
            }
        }
    }

    @Test
    void setCellUpdatesValue() {
        Grid grid = new Grid();
        grid.setCell(0, 0, 5);
        assertThat(grid.getCell(0, 0).getValue()).isEqualTo(5);
    }

    @Test
    void clearCellResetsValue() {
        Grid grid = new Grid();
        grid.setCell(2, 3, 7);
        grid.clearCell(2, 3);
        assertThat(grid.getCell(2, 3).isEmpty()).isTrue();
    }

    @Test
    void isSolvedReturnsFalseWhenAnyCellIsEmpty() {
        Grid grid = new Grid();
        assertThat(grid.isSolved()).isFalse();
    }

    @Test
    void isSolvedReturnsTrueWhenAllCellsNonZero() {
        Grid grid = new Grid();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                grid.setCell(r, c, (r * 9 + c) % 9 + 1);
            }
        }
        assertThat(grid.isSolved()).isTrue();
    }

    @Test
    void deepCopyIsIndependent() {
        Grid grid = new Grid();
        grid.setCell(0, 0, 3);
        Grid copy = grid.deepCopy();
        copy.setCell(0, 0, 9);
        assertThat(grid.getCell(0, 0).getValue()).isEqualTo(3);
    }

    @Test
    void deepCopyPreservesPreFilledFlag() {
        Grid grid = new Grid();
        grid.initCell(0, 0, 5, true);
        Grid copy = grid.deepCopy();
        assertThat(copy.getCell(0, 0).isPreFilled()).isTrue();
        assertThat(copy.getCell(0, 0).getValue()).isEqualTo(5);
    }
}
