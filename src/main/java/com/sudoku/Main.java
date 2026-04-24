package com.sudoku;

import com.sudoku.command.Command;
import com.sudoku.engine.GameEngine;
import com.sudoku.engine.GameResponse;
import com.sudoku.generator.PuzzleGenerator;
import com.sudoku.hint.HintProvider;
import com.sudoku.model.GameState;
import com.sudoku.model.PuzzleBundle;
import com.sudoku.parser.CommandParser;
import com.sudoku.parser.InvalidCommandException;
import com.sudoku.solver.SudokuSolver;
import com.sudoku.ui.ConsoleInputReader;
import com.sudoku.ui.ConsoleRenderer;
import com.sudoku.validator.BoardValidator;
import com.sudoku.validator.MoveValidator;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        CommandParser parser = new CommandParser();
        ConsoleRenderer renderer = new ConsoleRenderer();

        try (ConsoleInputReader reader = new ConsoleInputReader()) {
            boolean playAgain = true;
            while (playAgain) {
                Random random = new Random();
                SudokuSolver solver = new SudokuSolver(random);
                PuzzleGenerator generator = new PuzzleGenerator(solver, random);
                PuzzleBundle bundle = generator.generate();
                GameEngine engine = new GameEngine(
                        bundle,
                        new MoveValidator(),
                        new BoardValidator(),
                        new HintProvider(random));

                renderer.renderWelcome();
                renderer.renderGrid(bundle.puzzle());

                while (engine.getState() == GameState.ACTIVE) {
                    renderer.renderPrompt();
                    String input = reader.readLine();
                    try {
                        Command command = parser.parse(input);
                        GameResponse response = engine.process(command);
                        renderer.renderMessage(response.message());
                        if (response.state() != GameState.QUIT) {
                            renderer.renderGrid(response.grid());
                        }
                        if (response.state() == GameState.WON) {
                            renderer.renderWin();
                            renderer.renderPlayAgainPrompt();
                            reader.waitForKeyPress();
                        }
                    } catch (InvalidCommandException e) {
                        renderer.renderError("Unknown command: \"" + e.getInput() + "\". Try: A3 4, C5 clear, hint, check, quit");
                    }
                }

                playAgain = engine.getState() == GameState.WON;
            }
        }
    }
}
