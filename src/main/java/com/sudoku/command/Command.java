package com.sudoku.command;

public sealed interface Command
        permits PlaceNumberCommand, ClearCellCommand,
                HintCommand, CheckCommand, QuitCommand {}
