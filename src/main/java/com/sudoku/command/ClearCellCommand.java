package com.sudoku.command;

import com.sudoku.model.Position;

public record ClearCellCommand(Position position) implements Command {}
