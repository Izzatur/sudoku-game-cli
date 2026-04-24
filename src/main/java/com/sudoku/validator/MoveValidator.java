package com.sudoku.validator;

import com.sudoku.model.Grid;
import com.sudoku.model.MoveResult;
import com.sudoku.model.Position;

public class MoveValidator implements IMoveValidator {

    @Override
    public MoveResult validate(Grid puzzle, Position position) {
        if (puzzle.getCell(position.row(), position.col()).isPreFilled()) {
            return MoveResult.failure(
                    "Invalid move. " + position.toDisplayString() + " is pre-filled.");
        }
        return MoveResult.success("Move accepted.");
    }
}
