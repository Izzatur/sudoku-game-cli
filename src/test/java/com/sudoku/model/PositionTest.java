package com.sudoku.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PositionTest {

    @Test
    void fromInputMapsAOneToZeroZero() {
        Position pos = Position.fromInput('A', 1);
        assertThat(pos.row()).isEqualTo(0);
        assertThat(pos.col()).isEqualTo(0);
    }

    @Test
    void fromInputMapsINineToEightEight() {
        Position pos = Position.fromInput('I', 9);
        assertThat(pos.row()).isEqualTo(8);
        assertThat(pos.col()).isEqualTo(8);
    }

    @Test
    void fromInputAcceptsLowercaseRow() {
        Position pos = Position.fromInput('a', 1);
        assertThat(pos.row()).isEqualTo(0);
    }

    @Test
    void fromInputInvalidRowThrows() {
        assertThatThrownBy(() -> Position.fromInput('Z', 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fromInputColumnZeroThrows() {
        assertThatThrownBy(() -> Position.fromInput('A', 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fromInputColumnTenThrows() {
        assertThatThrownBy(() -> Position.fromInput('A', 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void toDisplayStringConvertsCorrectly() {
        assertThat(new Position(0, 0).toDisplayString()).isEqualTo("A1");
        assertThat(new Position(8, 8).toDisplayString()).isEqualTo("I9");
        assertThat(new Position(4, 4).toDisplayString()).isEqualTo("E5");
    }

    @Test
    void constructorRejectsOutOfBoundsRow() {
        assertThatThrownBy(() -> new Position(-1, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Position(9, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
