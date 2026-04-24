package com.sudoku.model;

public record MoveResult(boolean success, String message) {

    public static MoveResult success(String message) {
        return new MoveResult(true, message);
    }

    public static MoveResult failure(String message) {
        return new MoveResult(false, message);
    }
}
