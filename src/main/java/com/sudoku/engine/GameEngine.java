package com.sudoku.engine;

import com.sudoku.command.*;
import com.sudoku.hint.IHintProvider;
import com.sudoku.model.*;
import com.sudoku.validator.IBoardValidator;
import com.sudoku.validator.IMoveValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameEngine {
    private final Grid puzzle;
    private final Grid solution;
    private final IMoveValidator moveValidator;
    private final IBoardValidator boardValidator;
    private final IHintProvider hintProvider;
    private GameState state;

    public GameEngine(PuzzleBundle bundle,
                      IMoveValidator moveValidator,
                      IBoardValidator boardValidator,
                      IHintProvider hintProvider) {
        this.puzzle = bundle.puzzle();
        this.solution = bundle.solution();
        this.moveValidator = moveValidator;
        this.boardValidator = boardValidator;
        this.hintProvider = hintProvider;
        this.state = GameState.ACTIVE;
    }

    public GameState getState() {
        return state;
    }

    public GameResponse process(Command command) {
        return switch (command) {
            case PlaceNumberCommand c -> handlePlace(c);
            case ClearCellCommand   c -> handleClear(c);
            case HintCommand        c -> handleHint();
            case CheckCommand       c -> handleCheck();
            case QuitCommand        c -> handleQuit();
        };
    }

    private GameResponse handlePlace(PlaceNumberCommand cmd) {
        MoveResult result = moveValidator.validate(puzzle, cmd.position());
        if (!result.success()) {
            return new GameResponse(result.message(), state, puzzle);
        }
        puzzle.getCell(cmd.position().row(), cmd.position().col()).setValue(cmd.value());
        checkWin();
        return new GameResponse(result.message(), state, puzzle);
    }

    private GameResponse handleClear(ClearCellCommand cmd) {
        MoveResult result = moveValidator.validate(puzzle, cmd.position());
        if (!result.success()) {
            return new GameResponse(result.message(), state, puzzle);
        }
        puzzle.getCell(cmd.position().row(), cmd.position().col()).clear();
        return new GameResponse("Cell cleared.", state, puzzle);
    }

    private GameResponse handleHint() {
        Optional<Hint> hint = hintProvider.provide(puzzle, solution);
        if (hint.isEmpty()) {
            return new GameResponse("No hints available.", state, puzzle);
        }
        Hint h = hint.get();
        puzzle.getCell(h.position().row(), h.position().col()).setValue(h.value());
        checkWin();
        String message = "Hint: Cell " + h.position().toDisplayString() + " = " + h.value();
        return new GameResponse(message, state, puzzle);
    }

    private GameResponse handleCheck() {
        List<Violation> violations = boardValidator.validate(puzzle);
        if (violations.isEmpty()) {
            return new GameResponse("No rule violations detected.", state, puzzle);
        }
        String message = violations.stream()
                .map(Violation::message)
                .collect(Collectors.joining("\n"));
        return new GameResponse(message, state, puzzle);
    }

    private GameResponse handleQuit() {
        state = GameState.QUIT;
        return new GameResponse("Goodbye!", state, puzzle);
    }

    private void checkWin() {
        if (puzzle.isSolved() && boardValidator.validate(puzzle).isEmpty()) {
            state = GameState.WON;
        }
    }
}
