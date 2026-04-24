package com.sudoku.parser;

import com.sudoku.command.*;
import com.sudoku.model.Position;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    private static final Pattern PLACE_OR_CLEAR =
            Pattern.compile("^([a-iA-I])([1-9])\\s+([1-9]|clear)$");
    private static final Pattern SIMPLE =
            Pattern.compile("^(hint|check|quit)$");

    public Command parse(String input) {
        if (input == null || input.isBlank()) {
            throw new InvalidCommandException("");
        }
        String trimmed = input.trim();

        Matcher simple = SIMPLE.matcher(trimmed);
        if (simple.matches()) {
            return switch (simple.group(1)) {
                case "hint"  -> new HintCommand();
                case "check" -> new CheckCommand();
                case "quit"  -> new QuitCommand();
                default      -> throw new InvalidCommandException(trimmed);
            };
        }

        Matcher placeOrClear = PLACE_OR_CLEAR.matcher(trimmed);
        if (placeOrClear.matches()) {
            char rowChar = placeOrClear.group(1).charAt(0);
            int colNumber = Integer.parseInt(placeOrClear.group(2));
            String valueToken = placeOrClear.group(3);
            Position position = Position.fromInput(rowChar, colNumber);
            if (valueToken.equals("clear")) {
                return new ClearCellCommand(position);
            }
            return new PlaceNumberCommand(position, Integer.parseInt(valueToken));
        }

        throw new InvalidCommandException(trimmed);
    }
}
