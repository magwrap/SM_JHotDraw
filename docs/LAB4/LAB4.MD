Lab 4 — Refactoring Portfolio
Feature Context: Undo / Redo Functionality

Code Smell Identified: Verbose Anonymous Class / Boilerplate Listener Code

Location: org.jhotdraw.action.edit.UndoAction

The identified smell is the use of an anonymous inner class implementing PropertyChangeListener in order to react to property updates. This implementation is longer than necessary and makes the class harder to read and maintain. In addition, the listener field may raise serialization concerns if it is stored as a normal non-transient instance field, especially when it represents UI-related behavior rather than essential object state.

Why this is a smell

The anonymous class adds unnecessary boilerplate for a functional interface that only defines one method, so the intent of the code is less visible than it could be. In Java, such single-method interfaces can be expressed more clearly with lambda expressions, which reduces clutter without changing behavior. Marking listener-related references as transient is also appropriate when they should not be part of serialized state, because transient fields are excluded from Java's default serialization process.

Refactoring Plan
What to change

Replace the anonymous inner class used for the property change listener with a lambda expression, and mark the listener field as transient.

Strategy

Replace Anonymous Class with Lambda — appropriate because PropertyChangeListener is a single-method functional interface, so the existing implementation can be simplified directly into a lambda form.

Improve serialization safety with transient — appropriate because listener objects are runtime/UI infrastructure rather than core persistent state, and transient fields are ignored during serialization.

Refactoring Applied
Step 1 — Replace anonymous listener with lambda

Before:
The code used a full anonymous inner class implementation of PropertyChangeListener, including an overridden method body.

After:
The listener logic is expressed as a lambda, making the code shorter and easier to understand while preserving the same event-handling behavior.

Example form:

java
propertyChangeListener = evt -> {
    updateView();
};
Step 2 — Mark listener field as transient

The listener field was changed to:
java
private transient PropertyChangeListener propertyChangeListener;
This ensures the listener is not included in serialization, which is appropriate for a UI-related callback object that does not represent persistent business state.
Reasoning
Using a lambda is the correct refactoring because the original code implements only one abstract method, which is exactly the situation Java lambdas were designed for. The refactoring removes syntactic noise, improves readability, and makes the actual purpose of the code more visible to the reader.
Adding transient is also justified because listeners are typically runtime-only objects tied to UI behavior, and Java's serialization mechanism excludes transient fields by default. This reduces the risk of serializing references that should not be persisted and keeps the class safer with respect to object serialization.
Refactoring Validation
After the refactoring, the project should be rebuilt and executed to confirm that the external behavior remains unchanged. The Undo and Redo actions should still work correctly through the GUI, and property updates should continue to enable or disable actions exactly as before, since the refactoring changes only the internal form of the listener implementation and not its logic.
