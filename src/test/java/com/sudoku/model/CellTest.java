package com.sudoku.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CellTest {

    @Test
    void defaultCellIsEmpty() {
        Cell cell = new Cell();
        assertThat(cell.isEmpty()).isTrue();
        assertThat(cell.getValue()).isEqualTo(0);
        assertThat(cell.isPreFilled()).isFalse();
    }

    @Test
    void preFilledCellHasCorrectState() {
        Cell cell = new Cell(5, true);
        assertThat(cell.getValue()).isEqualTo(5);
        assertThat(cell.isPreFilled()).isTrue();
        assertThat(cell.isEmpty()).isFalse();
    }

    @Test
    void setValueUpdatesEmptyCell() {
        Cell cell = new Cell();
        cell.setValue(7);
        assertThat(cell.getValue()).isEqualTo(7);
        assertThat(cell.isEmpty()).isFalse();
    }

    @Test
    void clearResetsValueToZero() {
        Cell cell = new Cell();
        cell.setValue(3);
        cell.clear();
        assertThat(cell.getValue()).isEqualTo(0);
        assertThat(cell.isEmpty()).isTrue();
    }

    @Test
    void setValueOnPreFilledThrows() {
        Cell cell = new Cell(4, true);
        assertThatThrownBy(() -> cell.setValue(9))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void clearOnPreFilledThrows() {
        Cell cell = new Cell(4, true);
        assertThatThrownBy(cell::clear)
                .isInstanceOf(IllegalStateException.class);
    }
}
