# Sudoku Game CLI

A command-line Sudoku game built with **Java 21**, following clean code, SOLID principles, and TDD practices.

---

## 🎮 Game Overview

A fully playable Sudoku game on the command line. The program supports puzzle generation, user interaction, and validation.

### Features
- Displays a 9×9 Sudoku grid with 30 pre-filled numbers and empty cells (`_`)
- Place a number into a specific cell (e.g., `A3 4`)
- Clear a cell (e.g., `C5 clear`)
- Request a hint (e.g., `hint`) — reveals one correct number
- Check the current grid for validity (e.g., `check`)
- Quit the game at any time (e.g., `quit`)
- Detects row, column, and 3×3 subgrid violations
- Celebrates when the puzzle is correctly completed

---

## 🖥️ Environment Requirements

| Requirement | Version |
|---|---|
| Java (JDK) | **21 LTS or higher** |
| Maven | **3.9 or higher** |
| OS | Windows / macOS / Linux |

---

## 🚀 How to Build

```bash
mvn clean package
```

This will compile the source code, run all tests, and produce a JAR file at:

```
target/sudoku-game-cli.jar
```

---

## ▶️ How to Run

```bash
java -jar target/sudoku-game-cli.jar
```

---

## 🧪 How to Test

```bash
mvn test
```

All unit tests are written with **JUnit 5**, **AssertJ**, and **Mockito**.

---

## 📂 Project Structure

```
sudoku-game-cli/
├── src/
│   ├── main/java/com/sudoku/
│   │   ├── Main.java
│   │   ├── model/
│   │   ├── command/
│   │   ├── parser/
│   │   ├── solver/
│   │   ├── generator/
│   │   ├── validator/
│   │   ├── hint/
│   │   ├── engine/
│   │   └── ui/
│   └── test/java/com/sudoku/
├── pom.xml
├── README.md
└── PLAN.md
```

---

## 🎯 How to Play

Once the game starts, you will see:

```
Welcome to Sudoku!

Here is your puzzle:
    1 2 3 4 5 6 7 8 9
  A 5 3 _ _ 7 _ _ _ _
  B 6 _ _ 1 9 5 _ _ _
  C _ 9 8 _ _ _ _ 6 _
  D 8 _ _ _ 6 _ _ _ 3
  E 4 _ _ 8 _ 3 _ _ 1
  F 7 _ _ _ 2 _ _ _ 6
  G _ 6 _ _ _ _ 2 8 _
  H _ _ _ 4 1 9 _ _ 5
  I _ _ _ _ 8 _ _ 7 9

Enter command (e.g., A3 4, C5 clear, hint, check, quit):
```

### Available Commands

| Command | Description | Example |
|---|---|---|
| `{Row}{Col} {value}` | Place a number (1–9) in a cell | `A3 4` |
| `{Row}{Col} clear` | Clear a user-placed cell | `C5 clear` |
| `hint` | Reveal one correct cell | `hint` |
| `check` | Check grid for rule violations | `check` |
| `quit` | Exit the game | `quit` |

### Rules
- Rows are labeled **A–I**, columns are labeled **1–9**
- You cannot modify pre-filled cells
- Numbers must be between **1–9**
- The game ends when all cells are correctly filled

---

## 🏗️ Design Overview

### Architecture
The project follows a **layered architecture** with clear separation of concerns:

| Layer | Package | Responsibility |
|---|---|---|
| Model | `model/` | Core data structures (Cell, Grid, Position, DTOs) |
| Command | `command/` | Sealed command hierarchy (Java 21) |
| Parser | `parser/` | Parses raw string input into typed commands |
| Solver | `solver/` | Backtracking algorithm to generate valid solutions |
| Generator | `generator/` | Produces puzzles with exactly 30 pre-filled cells |
| Validator | `validator/` | Move validation and board rule checking |
| Hint | `hint/` | Reveals one correct cell from the solution |
| Engine | `engine/` | Central game orchestrator |
| UI | `ui/` | Console rendering and input reading |

### Key Java 21 Features Used
- **Sealed interfaces** — `Command` hierarchy is closed and exhaustive
- **Records** — Immutable DTOs (`Position`, `GameResponse`, `Hint`, etc.)
- **Pattern matching switch** — Clean, compiler-checked command dispatch in `GameEngine`

### SOLID Principles
- **Single Responsibility** — Each class has one clearly defined job
- **Open/Closed** — New commands added without modifying `GameEngine`
- **Liskov Substitution** — Interfaces for all major components
- **Interface Segregation** — Move validation and board validation are separate
- **Dependency Inversion** — `GameEngine` depends on interfaces, injected via constructor

---

## 🧰 Tech Stack

| Concern | Choice |
|---|---|
| Language | Java 21 LTS |
| Build Tool | Maven 3.9+ |
| Test Framework | JUnit 5 (Jupiter) |
| Assertions | AssertJ |
| Mocking | Mockito 5 |

---

## 📋 Assumptions

- Java 21 is required (uses sealed interfaces, records, pattern matching switch)
- Single-player CLI game
- One puzzle is generated per session
- Puzzle always has exactly 30 pre-filled cells
- The generated puzzle always has a unique valid solution

---

## 👤 Author

Built as a mid-level full stack developer assessment submission.
