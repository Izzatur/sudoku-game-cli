package com.sudoku.parser;

import com.sudoku.command.*;
import com.sudoku.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CommandParserTest {
    private CommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
    }

    @Test
    void parsesPlaceCommand() {
        Command cmd = parser.parse("A3 4");
        assertThat(cmd).isInstanceOf(PlaceNumberCommand.class);
        PlaceNumberCommand place = (PlaceNumberCommand) cmd;
        assertThat(place.position()).isEqualTo(new Position(0, 2));
        assertThat(place.value()).isEqualTo(4);
    }

    @Test
    void parsesPlaceCommandMaxCell() {
        Command cmd = parser.parse("I9 9");
        PlaceNumberCommand place = (PlaceNumberCommand) cmd;
        assertThat(place.position()).isEqualTo(new Position(8, 8));
        assertThat(place.value()).isEqualTo(9);
    }

    @Test
    void parsesLowercaseRowLetter() {
        Command cmd = parser.parse("a3 4");
        PlaceNumberCommand place = (PlaceNumberCommand) cmd;
        assertThat(place.position()).isEqualTo(new Position(0, 2));
    }

    @Test
    void parsesClearCommand() {
        Command cmd = parser.parse("C5 clear");
        assertThat(cmd).isInstanceOf(ClearCellCommand.class);
        ClearCellCommand clear = (ClearCellCommand) cmd;
        assertThat(clear.position()).isEqualTo(new Position(2, 4));
    }

    @Test
    void parsesHintCommand() {
        assertThat(parser.parse("hint")).isInstanceOf(HintCommand.class);
    }

    @Test
    void parsesCheckCommand() {
        assertThat(parser.parse("check")).isInstanceOf(CheckCommand.class);
    }

    @Test
    void parsesQuitCommand() {
        assertThat(parser.parse("quit")).isInstanceOf(QuitCommand.class);
    }

    @Test
    void rejectsUppercaseCommands() {
        assertThatThrownBy(() -> parser.parse("HINT"))
                .isInstanceOf(InvalidCommandException.class);
    }

    @Test
    void rejectsEmptyInput() {
        assertThatThrownBy(() -> parser.parse(""))
                .isInstanceOf(InvalidCommandException.class);
    }

    @Test
    void rejectsBlankInput() {
        assertThatThrownBy(() -> parser.parse("   "))
                .isInstanceOf(InvalidCommandException.class);
    }

    @Test
    void rejectsInvalidRow() {
        assertThatThrownBy(() -> parser.parse("J3 4"))
                .isInstanceOf(InvalidCommandException.class);
    }

    @Test
    void rejectsValueZero() {
        assertThatThrownBy(() -> parser.parse("A3 0"))
                .isInstanceOf(InvalidCommandException.class);
    }

    @Test
    void rejectsMissingSpace() {
        assertThatThrownBy(() -> parser.parse("A3clear"))
                .isInstanceOf(InvalidCommandException.class);
    }

    @Test
    void rejectsUnknownInput() {
        assertThatThrownBy(() -> parser.parse("foobar"))
                .isInstanceOf(InvalidCommandException.class);
    }

    @Test
    void invalidCommandExceptionStoresInput() {
        try {
            parser.parse("bad input");
            fail("Expected InvalidCommandException");
        } catch (InvalidCommandException e) {
            assertThat(e.getInput()).isEqualTo("bad input");
        }
    }
}
