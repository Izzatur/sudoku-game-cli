package com.sudoku.hint;

import com.sudoku.model.Grid;
import com.sudoku.model.Hint;
import com.sudoku.model.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class HintProvider implements IHintProvider {
    private final Random random;

    public HintProvider() {
        this(new Random());
    }

    public HintProvider(Random random) {
        this.random = random;
    }

    @Override
    public Optional<Hint> provide(Grid puzzle, Grid solution) {
        List<Position> emptyCells = new ArrayList<>();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (puzzle.getCell(r, c).isEmpty()) {
                    emptyCells.add(new Position(r, c));
                }
            }
        }
        if (emptyCells.isEmpty()) {
            return Optional.empty();
        }
        Collections.shuffle(emptyCells, random);
        Position chosen = emptyCells.get(0);
        int value = solution.getCell(chosen.row(), chosen.col()).getValue();
        return Optional.of(new Hint(chosen, value));
    }
}
