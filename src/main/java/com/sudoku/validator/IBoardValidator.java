package com.sudoku.validator;

import com.sudoku.model.Grid;
import com.sudoku.model.Violation;

import java.util.List;

public interface IBoardValidator {
    List<Violation> validate(Grid grid);
}
