package com.sudoku.validator;

import com.sudoku.model.Grid;
import com.sudoku.model.MoveResult;
import com.sudoku.model.Position;

public interface IMoveValidator {
    MoveResult validate(Grid puzzle, Position position);
}
