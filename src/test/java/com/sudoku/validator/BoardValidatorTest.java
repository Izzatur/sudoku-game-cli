package com.sudoku.validator;

import com.sudoku.model.Grid;
import com.sudoku.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class BoardValidatorTest {
    private BoardValidator validator;
    private Grid grid;

    @BeforeEach
    void setUp() {
        validator = new BoardValidator();
        grid = new Grid();
    }

    @Test
    void emptyGridHasNoViolations() {
        assertThat(validator.validate(grid)).isEmpty();
    }

    @Test
    void detectsRowDuplicate() {
        grid.setCell(0, 0, 3);
        grid.setCell(0, 5, 3);
        List<Violation> violations = validator.validate(grid);
        assertThat(violations).anyMatch(v -> v.message().contains("Row A"));
    }

    @Test
    void rowViolationMessageFormat() {
        grid.setCell(0, 0, 3);
        grid.setCell(0, 1, 3);
        List<Violation> violations = validator.validate(grid);
        assertThat(violations).anyMatch(v -> v.message().equals("Number 3 already exists in Row A."));
    }

    @Test
    void detectsColumnDuplicate() {
        grid.setCell(0, 0, 5);
        grid.setCell(4, 0, 5);
        List<Violation> violations = validator.validate(grid);
        assertThat(violations).anyMatch(v -> v.message().contains("Column 1"));
    }

    @Test
    void columnViolationMessageFormat() {
        grid.setCell(0, 2, 7);
        grid.setCell(3, 2, 7);
        List<Violation> violations = validator.validate(grid);
        assertThat(violations).anyMatch(v -> v.message().equals("Number 7 already exists in Column 3."));
    }

    @Test
    void detectsSubgridDuplicate() {
        grid.setCell(0, 0, 8);
        grid.setCell(2, 2, 8);
        List<Violation> violations = validator.validate(grid);
        assertThat(violations).anyMatch(v -> v.message().contains("3×3 subgrid"));
    }

    @Test
    void subgridViolationUsesUnicodeMultiplicationSign() {
        grid.setCell(0, 0, 8);
        grid.setCell(1, 1, 8);
        List<Violation> violations = validator.validate(grid);
        assertThat(violations).anyMatch(v ->
                v.message().equals("Number 8 already exists in the same 3×3 subgrid."));
    }

    @Test
    void validBoardReturnsNoViolations() {
        // Set up a valid row
        for (int c = 0; c < 9; c++) {
            grid.setCell(0, c, c + 1);
        }
        assertThat(validator.validate(grid)).isEmpty();
    }

    @Test
    void multipleViolationsAllReported() {
        grid.setCell(0, 0, 1);
        grid.setCell(0, 1, 1); // row A duplicate
        grid.setCell(3, 3, 2);
        grid.setCell(4, 3, 2); // col 4 duplicate
        List<Violation> violations = validator.validate(grid);
        assertThat(violations.size()).isGreaterThanOrEqualTo(2);
    }
}
