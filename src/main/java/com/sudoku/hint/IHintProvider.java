package com.sudoku.hint;

import com.sudoku.model.Grid;
import com.sudoku.model.Hint;

import java.util.Optional;

public interface IHintProvider {
    Optional<Hint> provide(Grid puzzle, Grid solution);
}
