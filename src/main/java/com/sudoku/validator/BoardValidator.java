package com.sudoku.validator;

import com.sudoku.model.Grid;
import com.sudoku.model.Violation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardValidator implements IBoardValidator {

    @Override
    public List<Violation> validate(Grid grid) {
        List<Violation> violations = new ArrayList<>();
        violations.addAll(checkRows(grid));
        violations.addAll(checkColumns(grid));
        violations.addAll(checkSubgrids(grid));
        return violations;
    }

    private List<Violation> checkRows(Grid grid) {
        List<Violation> violations = new ArrayList<>();
        for (int r = 0; r < 9; r++) {
            Set<Integer> seen = new HashSet<>();
            for (int c = 0; c < 9; c++) {
                int value = grid.getCell(r, c).getValue();
                if (value != 0 && !seen.add(value)) {
                    char rowLabel = (char) ('A' + r);
                    violations.add(new Violation(
                            "Number " + value + " already exists in Row " + rowLabel + "."));
                    break;
                }
            }
        }
        return violations;
    }

    private List<Violation> checkColumns(Grid grid) {
        List<Violation> violations = new ArrayList<>();
        for (int c = 0; c < 9; c++) {
            Set<Integer> seen = new HashSet<>();
            for (int r = 0; r < 9; r++) {
                int value = grid.getCell(r, c).getValue();
                if (value != 0 && !seen.add(value)) {
                    violations.add(new Violation(
                            "Number " + value + " already exists in Column " + (c + 1) + "."));
                    break;
                }
            }
        }
        return violations;
    }

    private List<Violation> checkSubgrids(Grid grid) {
        List<Violation> violations = new ArrayList<>();
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                Set<Integer> seen = new HashSet<>();
                int startR = boxRow * 3, startC = boxCol * 3;
                for (int r = startR; r < startR + 3; r++) {
                    for (int c = startC; c < startC + 3; c++) {
                        int value = grid.getCell(r, c).getValue();
                        if (value != 0 && !seen.add(value)) {
                            violations.add(new Violation(
                                    "Number " + value + " already exists in the same 3×3 subgrid."));
                            break;
                        }
                    }
                }
            }
        }
        return violations;
    }
}
