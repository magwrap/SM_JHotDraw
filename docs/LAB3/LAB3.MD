Lab 3 — Portfolio Artifacts

Part 1: Impact Analysis 
Feature: Undo / Redo Functionality
Algorithm: Figure 7.9 from [Raj13] — iterative breadth-first search (BFS) starting from the initial CHANGED set.

Initial Impact Set (from Lab 2 Concept Location)
The following core classes are initially marked as CHANGED:
UndoRedoManager — org.jhotdraw.undo
UndoAction — org.jhotdraw.action.edit
RedoAction — org.jhotdraw.action.edit
AbstractViewAction — org.jhotdraw.action
DrawingView — org.jhotdraw.draw
Drawing — org.jhotdraw.draw
UndoableEdit — javax.swing.undo
Iteration: Marking Neighbors

From UndoAction / RedoAction:
AbstractViewAction → CHANGED — provides access to the active DrawingView; any behavioral change propagates through this abstraction
DefaultDrawingEditor (org.jhotdraw.draw) → PROPAGATING — manages action registration and view coordination
DefaultMenuBuilder (org.jhotdraw.app) → PROPAGATING — integrates undo/redo into the application menu
ButtonFactory (org.jhotdraw.gui.action) → PROPAGATING — binds undo/redo actions to toolbar buttons

From UndoRedoManager:
UndoManager (javax.swing.undo) → PROPAGATING — superclass providing core undo/redo stack behavior
UndoableEdit (javax.swing.undo) → CHANGED — represents reversible operations; central to undo/redo logic
CompoundEdit (javax.swing.undo) → PROPAGATING — groups multiple edits into a single undoable action

From DrawingView:
DefaultDrawingView (org.jhotdraw.draw) → PROPAGATING — concrete implementation of the view
FigureSelectionListener (org.jhotdraw.draw.event) → UNCHANGED — selection events are unaffected by undo/redo

From Drawing:
AbstractDrawing (org.jhotdraw.draw) → PROPAGATING — base implementation of drawing behavior
DefaultDrawing (org.jhotdraw.draw) → CHANGED — actual storage of figures; state changes must be reversible

Estimated Impact Set (Table 1)
org.jhotdraw.undo — 3 classes
Contains core undo/redo logic (UndoRedoManager, related edits); central to feature implementation
org.jhotdraw.action.edit — 2 classes
Includes UndoAction and RedoAction; entry points for user-triggered operations
org.jhotdraw.action — 1 class
AbstractViewAction; shared abstraction for accessing the active view
org.jhotdraw.draw — 4 classes
Includes Drawing, DrawingView, and their implementations; updated during undo/redo
org.jhotdraw.app — 1 class
Menu integration via DefaultMenuBuilder; propagates UI-level changes
org.jhotdraw.gui.action — 1 class
ButtonFactory; connects undo/redo actions to toolbar components
javax.swing.undo — 3 classes
Provides the underlying undo framework (UndoManager, UndoableEdit, CompoundEdit)

Part 2: Continuous Integration (CILab)

A Continuous Integration (CI) pipeline was implemented using GitHub Actions to automatically build and test the project.
Pipeline Configuration
The workflow is triggered on:
Push to main development branches
Pull requests
CI Workflow Steps
Checkout repository
Set up Java Development Kit (JDK 11, Temurin distribution)
Cache Maven dependencies
Build project using Maven
Run automated tests
GitHub Actions Workflow (.github/workflows/maven.yml)

text
name: Java CI with Maven

on:
  push:
    branches: [ "main", "develop" ]
  pull_request:
    branches: [ "main", "develop" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: maven

      - name: Build project
        run: mvn -B package --file pom.xml

      - name: Run tests
        run: mvn test
CI Outcome

The project is automatically built and tested for every code change.

Errors are detected early during development.

Ensures stability of undo/redo functionality and overall system behavior.
