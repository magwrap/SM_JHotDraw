Lab 7 — Portfolio: Unit Testing in JHotDraw
Introduction

I wrote unit tests for UndoRedoManager, the class that manages undo and redo history in JHotDraw. This class is critical domain logic because every user action (drawing shapes, moving elements, deleting figures) is recorded as an edit through it. If the undo/redo history is incorrect, users lose the ability to correct mistakes, which breaks a core feature of the application.

How I Verified It (4 Tests)

Test 1 — testAddEdit_EnablesUndo (Best Case)

Purpose: Verifies the normal happy path — when a user performs an action, it gets added as an edit, and the Undo button becomes available.

What I checked:

Before any edit is added, canUndo() returns false

After calling addEdit(), canUndo() returns true

The edit count increases from 0 to 1

This confirms the manager correctly tracks state and enables undo only when there is something to undo.

Test 2 — testUndoThenRedo_TogglesAvailability (Best Case)

Purpose: Verifies the full undo → redo cycle that users rely on daily.

What I checked:

After adding an edit, canUndo() is true and canRedo() is false

After calling undo(), canUndo() becomes false and canRedo() becomes true

After calling redo(), canUndo() becomes true and canRedo() becomes false

The edit remains in the history throughout the cycle

This is the core behavior users expect: undo reverses an action, redo restores it, and availability toggles correctly.

Test 3 — testUndo_OnEmptyManager_ThrowsException (Boundary Case)

Purpose: Tests the boundary where there is nothing to undo.

What I checked:

Calling undo() on an empty UndoRedoManager throws CannotUndoException

The exception is the expected type (not a generic NullPointerException or silent failure)

This protects the application from illegal states. Users and GUI code can catch the specific exception and disable the Undo button appropriately instead of crashing unexpectedly.

Test 4 — testDiscardAllEdits_ResetsState (Boundary Case)

Purpose: Tests the "clean slate" boundary — when all edits are discarded (e.g., when a file is saved or closed).

What I checked (5 assertions):

canUndo() returns false after discarding

canRedo() returns false after discarding

Edit count resets to 0

hasSignificantEdits() returns false

The internal edit list is empty

All five assertions passed, confirming the full reset works correctly and the manager returns to its initial state.

Technical Choices

JUnit 4 — I used JUnit 4 because it integrates best with the existing Swing-based JHotDraw project. The project's build configuration and test infrastructure are already set up for JUnit 4, so using a different version would require additional setup.

AbstractUndoableEdit — I used Java's built-in AbstractUndoableEdit class as a real edit object instead of creating mocks. This keeps the tests simple, readable, and focused on the manager's behavior rather than mock setup.

Labels.properties — I added a Labels.properties file to the test resources directory to satisfy the ResourceBundle that the UndoAction constructor requires. This prevents the tests from failing due to missing configuration files.

Why These Tests Matter

These four tests cover both best cases (normal usage) and boundary cases (edge conditions), which is essential for robust testing:

Best cases confirm the feature works as users expect

Boundary cases confirm the feature handles errors gracefully

Together, they ensure UndoRedoManager behaves correctly in normal usage and protects the application from illegal states when users try to undo/redo when nothing is available.
