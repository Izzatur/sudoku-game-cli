package com.sudoku.engine;

import com.sudoku.model.GameState;
import com.sudoku.model.Grid;

public record GameResponse(String message, GameState state, Grid grid) {}
