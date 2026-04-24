package com.sudoku.validator;

import com.sudoku.model.Grid;
import com.sudoku.model.MoveResult;
import com.sudoku.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MoveValidatorTest {
    private MoveValidator validator;
    private Grid grid;

    @BeforeEach
    void setUp() {
        validator = new MoveValidator();
        grid = new Grid();
    }

    @Test
    void acceptsEmptyCell() {
        MoveResult result = validator.validate(grid, new Position(0, 0));
        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Move accepted.");
    }

    @Test
    void rejectsPreFilledCell() {
        grid.initCell(0, 0, 5, true);
        MoveResult result = validator.validate(grid, new Position(0, 0));
        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("Invalid move. A1 is pre-filled.");
    }

    @Test
    void errorMessageContainsCorrectPosition() {
        grid.initCell(2, 4, 7, true);
        MoveResult result = validator.validate(grid, new Position(2, 4));
        assertThat(result.message()).isEqualTo("Invalid move. C5 is pre-filled.");
    }
}
