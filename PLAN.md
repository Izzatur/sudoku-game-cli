# Sudoku CLI Game — Java 21 Development Plan

## 🏗️ Project Structure

```
sudoku-game-cli/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/sudoku/
│   │           ├── Main.java
│   │           ├── model/
│   │           │   ├── Cell.java
│   │           │   ├── Grid.java
│   │           │   ├── Position.java
│   │           │   ├── GameState.java
│   │           │   ├── Hint.java
│   │           │   ├── Violation.java
│   │           │   ├── MoveResult.java
│   │           │   └── PuzzleBundle.java
│   │           ├── command/
│   │           │   ├── Command.java               # sealed interface
│   │           │   ├── PlaceNumberCommand.java    # record
│   │           │   ├── ClearCellCommand.java      # record
│   │           │   ├── HintCommand.java           # record
│   │           │   ├── CheckCommand.java          # record
│   │           │   └── QuitCommand.java           # record
│   │           ├── parser/
│   │           │   ├── CommandParser.java
│   │           │   └── InvalidCommandException.java
│   │           ├── solver/
│   │           │   └── SudokuSolver.java
│   │           ├── generator/
│   │           │   └── PuzzleGenerator.java
│   │           ├── validator/
│   │           │   ├── IMoveValidator.java        # interface
│   │           │   ├── IBoardValidator.java       # interface
│   │           │   ├── MoveValidator.java
│   │           │   └── BoardValidator.java
│   │           ├── hint/
│   │           │   ├── IHintProvider.java         # interface
│   │           │   └── HintProvider.java
│   │           ├── engine/
│   │           │   ├── GameEngine.java
│   │           │   └── GameResponse.java
│   │           └── ui/
│   │               ├── ConsoleRenderer.java
│   │               └── ConsoleInputReader.java
│   └── test/
│       └── java/
│           └── com/sudoku/
│               ├── model/
│               │   ├── CellTest.java
│               │   ├── GridTest.java
│               │   └── PositionTest.java
│               ├── parser/
│               │   └── CommandParserTest.java
│               ├── solver/
│               │   └── SudokuSolverTest.java
│               ├── generator/
│               │   └── PuzzleGeneratorTest.java
│               ├── validator/
│               │   ├── MoveValidatorTest.java
│               │   └── BoardValidatorTest.java
│               ├── hint/
│               │   └── HintProviderTest.java
│               └── engine/
│                   └── GameEngineTest.java
├── pom.xml
└── README.md
```

---

## 🧩 Model Layer

### `Cell.java`
- `int value` — 0 means empty
- `boolean isPreFilled` — set at puzzle generation, immutable
- Methods: `isEmpty()`, `isPreFilled()`, `getValue()`, `setValue()`, `clear()`
- `setValue()` and `clear()` throw `IllegalStateException` if cell is pre-filled (programming guard — `MoveValidator` catches this before the call)

### `Grid.java`
- `Cell[][] cells` — 9×9 board
- Methods: `getCell(row, col)`, `setCell(row, col, value)`, `clearCell(row, col)`, `isSolved()`, `deepCopy()`

### `Position.java` — Record
- `int row` (0–8 internally, A–I displayed)
- `int col` (0–8 internally, 1–9 displayed)
- Factory: `fromInput(char rowChar, int colNumber)`
- Display: `toDisplayString()` → "A3", "E5"
- Compact constructor validates bounds

### `GameState.java` — Enum
- `ACTIVE`, `WON`, `QUIT`

### DTOs — Records
- `Hint(Position position, int value)`
- `Violation(String message)`
- `MoveResult(boolean success, String message)` with static factories
- `PuzzleBundle(Grid puzzle, Grid solution)`
- `GameResponse(String message, GameState state, Grid grid)`

---

## 🔌 Command Layer (Sealed Interface + Records — Java 21)

```java
public sealed interface Command
    permits PlaceNumberCommand, ClearCellCommand,
            HintCommand, CheckCommand, QuitCommand {}

public record PlaceNumberCommand(Position position, int value) implements Command {}
public record ClearCellCommand(Position position)              implements Command {}
public record HintCommand()                                    implements Command {}
public record CheckCommand()                                   implements Command {}
public record QuitCommand()                                    implements Command {}
```

---

## 🔍 Parser Layer

### Input → Command Mapping

| Raw Input   | Parsed Output                        |
|-------------|--------------------------------------|
| `A3 4`      | `PlaceNumberCommand(Position(0,2), 4)` |
| `C5 clear`  | `ClearCellCommand(Position(2,4))`    |
| `hint`      | `HintCommand()`                      |
| `check`     | `CheckCommand()`                     |
| `quit`      | `QuitCommand()`                      |
| other       | throws `InvalidCommandException`     |

Regex patterns:
- Place/Clear: `^([a-iA-I])([1-9])\s+([1-9]|clear)$` — accepts upper and lowercase row letters, normalized to uppercase internally
- Simple commands: `^(hint|check|quit)$`
- Both patterns compiled as `static final Pattern` constants (not recompiled per call)

---

## ♟️ Solver & Generator

### `SudokuSolver.java` — Backtracking Algorithm
1. Find next empty cell
2. Shuffle digits [1–9] for randomness
3. For each digit: if safe → place → recurse → backtrack on failure
4. Return `true` when solved

### `PuzzleGenerator.java`
1. Create empty `Grid`
2. `solver.solve(grid)` → complete valid solution (mutates in-place, so `deepCopy()` before this if you need the original)
3. `deepCopy()` → save as solution; second `deepCopy()` → working puzzle (all cells pre-filled initially)
4. Shuffle all 81 positions; for each candidate removal: un-pre-fill the cell, run `countSolutions(puzzle)` (stops counting at 2); if count ≠ 1, revert
5. Continue until exactly **30 pre-filled cells remain**
6. Return `PuzzleBundle(puzzle, solution)`

**Uniqueness guarantee:** `countSolutions` uses the same backtracking solver on a deep copy and stops as soon as 2 solutions are found, keeping runtime fast (milliseconds per puzzle).

---

## ✅ Validator Layer

### `IMoveValidator.java` — Interface
```java
public interface IMoveValidator {
    MoveResult validate(Grid puzzle, Position position);
}
```

### `MoveValidator.java` — implements `IMoveValidator`
- Rejects pre-filled cells → `"Invalid move. {pos} is pre-filled."` (e.g., `"Invalid move. A1 is pre-filled."`)
- Value range 1–9 enforced by parser; no re-validation needed here

### `IBoardValidator.java` — Interface
```java
public interface IBoardValidator {
    List<Violation> validate(Grid grid);
}
```

### `BoardValidator.java` — implements `IBoardValidator`
- Checks all 9 rows → `"Number X already exists in Row A."`
- Checks all 9 columns → `"Number X already exists in Column 1."`
- Checks all 9 subgrids → `"Number X already exists in the same 3×3 subgrid."` — **unicode `×` (U+00D7), not ASCII `x`**
- Uses `Set.add()` returning `false` to detect duplicates; skips empty cells (value 0)
- Returns `List<Violation>` (empty = no violations)

---

## 💡 Hint Provider

### `IHintProvider.java` — Interface
```java
public interface IHintProvider {
    Optional<Hint> provide(Grid puzzle, Grid solution);
}
```

### `HintProvider.java` — implements `IHintProvider`
1. Collect all positions where `puzzle.getCell(r,c).getValue() == 0`
2. If none → `Optional.empty()`
3. Shuffle list (inject `Random` via constructor for testability), pick the first
4. Return `Optional.of(new Hint(position, solution.getCell(...).getValue()))`

**Important:** The `GameEngine` both **applies** the hint to the puzzle grid (fills the cell) AND displays `"Hint: Cell E5 = 5"`. The hint is not just a display-only suggestion.

---

## ⚙️ Engine Layer

### `GameEngine.java` — Java 21 Pattern Matching Switch

```java
public GameResponse process(Command command) {
    return switch (command) {
        case PlaceNumberCommand c -> handlePlace(c);
        case ClearCellCommand   c -> handleClear(c);
        case HintCommand        c -> handleHint();
        case CheckCommand       c -> handleCheck();
        case QuitCommand        c -> handleQuit();
    };
}
```

- Injected via constructor: `IMoveValidator`, `IBoardValidator`, `IHintProvider`, `PuzzleBundle` (concrete implementations wired in `Main`)
- Win check only after `PlaceNumberCommand` and `HintCommand` (the two value-setting operations)
- `isWon()` = `puzzle.isSolved() && boardValidator.validate(puzzle).isEmpty()`
- `handleCheck()` with no violations → `"No rule violations detected."` (exact string)
- `handleHint()` with no empty cells → `"No hints available."`

---

## 🖥️ UI Layer

### `ConsoleRenderer.java`
- `renderWelcome()`, `renderGrid(Grid)`, `renderMessage(String)`
- `renderPrompt()`, `renderWin()`, `renderPlayAgainPrompt()`
- Grid format: column headers `1–9`, row labels `A–I`, empty = `_`

### `ConsoleInputReader.java`
- Single `Scanner(System.in)` instance created in constructor — never create multiple instances on `System.in`
- `readLine()` trims whitespace, `waitForKeyPress()`, implements `AutoCloseable`

---

## 🔄 Game Flow

```
Main.main()
    │
    ▼
PuzzleGenerator.generate() → PuzzleBundle
    │
    ▼
renderWelcome() + renderGrid(puzzle)
    │
    ▼
┌─────────────────────────────────────┐
│ renderPrompt()                      │
│ input = readLine()                  │
│ command = CommandParser.parse(input)│
│ response = GameEngine.process()     │
│   ├─ PlaceNumber → validate + set   │
│   ├─ ClearCell   → validate + clear │
│   ├─ Hint        → reveal cell      │
│   ├─ Check       → violations list  │
│   └─ Quit        → QUIT state       │
│ renderMessage() + renderGrid()      │
│ WON?  ──YES──► win screen           │
│ QUIT? ──YES──► exit                 │
└──────────────── NO ─────────────────┘
         ▲ loop back
```

---

## ✅ Testing Plan (JUnit 5 + AssertJ + Mockito)

| Test Class            | Scenarios Covered                                                    |
|-----------------------|----------------------------------------------------------------------|
| `CellTest`            | Default empty, pre-filled flag, clear resets value                   |
| `GridTest`            | set/get/clear, `isSolved()` true/false, `deepCopy()` independence    |
| `PositionTest`        | Valid A1→I9, out-of-bounds throws, `toDisplayString()`               |
| `CommandParserTest`   | All valid inputs, invalid inputs, edge cases, exception thrown       |
| `SudokuSolverTest`    | Fills empty grid, no duplicates in rows/cols/subgrids                |
| `PuzzleGeneratorTest` | Exactly 30 pre-fills, puzzle differs from solution, solution valid   |
| `MoveValidatorTest`   | Pre-filled rejection, valid move accepted                            |
| `BoardValidatorTest`  | Row/column/subgrid duplicate, clean board = empty violations         |
| `HintProviderTest`    | Returns correct value, empty Optional when grid is full              |
| `GameEngineTest`      | All commands dispatched, win detected, quit handled                  |

---

## 📐 SOLID Principles Applied

| Principle | How Applied |
|---|---|
| **S**ingle Responsibility | Each class has exactly one job |
| **O**pen/Closed | New `Command` types added without modifying `GameEngine` |
| **L**iskov Substitution | `IMoveValidator`, `IBoardValidator`, `IHintProvider`, `IPuzzleGenerator` interfaces |
| **I**nterface Segregation | Move validation and board validation split into separate interfaces |
| **D**ependency Inversion | `GameEngine` depends on interfaces; implementations injected via constructor |

---

## 🛠️ Tech Stack

| Concern        | Choice              |
|----------------|---------------------|
| Language       | Java 21 LTS         |
| Java 21 Used   | Sealed interfaces, records, pattern matching switch, record patterns |
| Build Tool     | Maven 3.9+          |
| Test Framework | JUnit 5 (Jupiter)   |
| Assertions     | AssertJ             |
| Mocking        | Mockito 5           |
| Platform       | Windows/macOS/Linux |

---

## 📦 `pom.xml` Key Config

```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<dependencies>
    <!-- No runtime dependencies — zero external libs that solve Sudoku -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.25.3</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.11.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- CRITICAL: surefire 2.x silently skips JUnit 5 — must use 3.x -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.5</version>
        </plugin>
        <!-- Produces executable thin jar (no runtime deps to bundle) -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>3.3.0</version>
            <configuration>
                <archive>
                    <manifest>
                        <mainClass>com.sudoku.Main</mainClass>
                    </manifest>
                </archive>
                <finalName>sudoku-game-cli</finalName>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 🚀 TDD Development Order

| Step | Task                                          | Tests Written First                  |
|------|-----------------------------------------------|--------------------------------------|
| 1    | `Cell`, `Grid`, `Position`, DTOs              | `CellTest`, `GridTest`, `PositionTest` |
| 2    | `Command` sealed hierarchy                    | — (pure data, no logic)              |
| 3    | `CommandParser`                               | `CommandParserTest`                  |
| 4    | `SudokuSolver` (backtracking)                 | `SudokuSolverTest`                   |
| 5    | `PuzzleGenerator` (30 pre-fills)              | `PuzzleGeneratorTest`                |
| 6    | `MoveValidator`                               | `MoveValidatorTest`                  |
| 7    | `BoardValidator` (row/col/subgrid)            | `BoardValidatorTest`                 |
| 8    | `HintProvider`                                | `HintProviderTest`                   |
| 9    | `GameEngine` + `GameResponse`                 | `GameEngineTest`                     |
| 10   | `ConsoleRenderer` + `ConsoleInputReader`      | Manual / integration                 |
| 11   | `Main.java` — wire everything                 | End-to-end smoke test                |
| 12   | `README.md`                                   | —                                    |
