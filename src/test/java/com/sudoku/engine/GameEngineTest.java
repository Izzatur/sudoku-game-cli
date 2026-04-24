package com.sudoku.engine;

import com.sudoku.command.*;
import com.sudoku.hint.IHintProvider;
import com.sudoku.model.*;
import com.sudoku.validator.IBoardValidator;
import com.sudoku.validator.IMoveValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameEngineTest {

    @Mock private IMoveValidator moveValidator;
    @Mock private IBoardValidator boardValidator;
    @Mock private IHintProvider hintProvider;

    private Grid puzzle;
    private Grid solution;
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        puzzle = new Grid();
        solution = new Grid();
        // Fill solution with known values
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                solution.setCell(r, c, (r * 9 + c) % 9 + 1);
            }
        }
        engine = new GameEngine(
                new PuzzleBundle(puzzle, solution),
                moveValidator, boardValidator, hintProvider);
    }

    @Test
    void initialStateIsActive() {
        assertThat(engine.getState()).isEqualTo(GameState.ACTIVE);
    }

    @Test
    void placeCommandOnValidCellUpdatesGrid() {
        when(moveValidator.validate(any(), any())).thenReturn(MoveResult.success("Move accepted."));

        Position pos = new Position(0, 0);
        GameResponse response = engine.process(new PlaceNumberCommand(pos, 5));

        assertThat(puzzle.getCell(0, 0).getValue()).isEqualTo(5);
        assertThat(response.message()).isEqualTo("Move accepted.");
        assertThat(response.state()).isEqualTo(GameState.ACTIVE);
    }

    @Test
    void placeCommandOnPreFilledCellRejectsMove() {
        when(moveValidator.validate(any(), any()))
                .thenReturn(MoveResult.failure("Invalid move. A1 is pre-filled."));

        Position pos = new Position(0, 0);
        GameResponse response = engine.process(new PlaceNumberCommand(pos, 5));

        assertThat(puzzle.getCell(0, 0).isEmpty()).isTrue();
        assertThat(response.message()).isEqualTo("Invalid move. A1 is pre-filled.");
    }

    @Test
    void clearCommandClearsCell() {
        puzzle.setCell(1, 1, 7);
        when(moveValidator.validate(any(), any())).thenReturn(MoveResult.success("Move accepted."));

        GameResponse response = engine.process(new ClearCellCommand(new Position(1, 1)));

        assertThat(puzzle.getCell(1, 1).isEmpty()).isTrue();
        assertThat(response.message()).isEqualTo("Cell cleared.");
    }

    @Test
    void clearCommandOnPreFilledCellRejects() {
        when(moveValidator.validate(any(), any()))
                .thenReturn(MoveResult.failure("Invalid move. B2 is pre-filled."));

        GameResponse response = engine.process(new ClearCellCommand(new Position(1, 1)));

        assertThat(response.message()).isEqualTo("Invalid move. B2 is pre-filled.");
    }

    @Test
    void hintCommandAppliesHintToGrid() {
        Position hintPos = new Position(3, 3);
        int hintValue = solution.getCell(3, 3).getValue();
        when(hintProvider.provide(any(), any())).thenReturn(Optional.of(new Hint(hintPos, hintValue)));

        GameResponse response = engine.process(new HintCommand());

        assertThat(puzzle.getCell(3, 3).getValue()).isEqualTo(hintValue);
        assertThat(response.message()).isEqualTo("Hint: Cell D4 = " + hintValue);
    }

    @Test
    void hintCommandWhenNoHintsAvailable() {
        when(hintProvider.provide(any(), any())).thenReturn(Optional.empty());

        GameResponse response = engine.process(new HintCommand());

        assertThat(response.message()).isEqualTo("No hints available.");
    }

    @Test
    void checkCommandWithNoViolations() {
        when(boardValidator.validate(any())).thenReturn(List.of());

        GameResponse response = engine.process(new CheckCommand());

        assertThat(response.message()).isEqualTo("No rule violations detected.");
        assertThat(response.state()).isEqualTo(GameState.ACTIVE);
    }

    @Test
    void checkCommandWithViolations() {
        when(boardValidator.validate(any())).thenReturn(List.of(
                new Violation("Number 3 already exists in Row A."),
                new Violation("Number 5 already exists in Column 1.")));

        GameResponse response = engine.process(new CheckCommand());

        assertThat(response.message()).contains("Number 3 already exists in Row A.");
        assertThat(response.message()).contains("Number 5 already exists in Column 1.");
    }

    @Test
    void quitCommandSetsQuitState() {
        GameResponse response = engine.process(new QuitCommand());
        assertThat(response.state()).isEqualTo(GameState.QUIT);
        assertThat(engine.getState()).isEqualTo(GameState.QUIT);
    }

    @Test
    void winDetectedWhenGridSolvedAndNoViolations() {
        when(moveValidator.validate(any(), any())).thenReturn(MoveResult.success("Move accepted."));
        // Fill all but one cell
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (r == 8 && c == 8) continue;
                puzzle.setCell(r, c, 1);
            }
        }
        // Placing the last value triggers win check
        when(boardValidator.validate(any())).thenReturn(List.of());

        GameResponse response = engine.process(new PlaceNumberCommand(new Position(8, 8), 5));

        assertThat(response.state()).isEqualTo(GameState.WON);
    }
}
