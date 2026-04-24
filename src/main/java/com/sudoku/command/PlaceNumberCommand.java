package com.sudoku.command;

import com.sudoku.model.Position;

public record PlaceNumberCommand(Position position, int value) implements Command {}
