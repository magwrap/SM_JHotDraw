# Lab 4: refactoring portfolio

## Feature context: copy and paste figures

## Code smell: duplicated code (Fowler #1)

Location: `org.jhotdraw.action.edit`, three sibling classes:

- `CopyAction.actionPerformed` (lines 59 to 64)
- `CutAction.actionPerformed` (lines 59 to 64)
- `PasteAction.actionPerformed` (lines 60 to 65)

The duplicated fragment is identical in all three:

```java
JComponent c = target;
if (c == null && (KeyboardFocusManager.getCurrentKeyboardFocusManager().
        getPermanentFocusOwner() instanceof JComponent)) {
    c = (JComponent) KeyboardFocusManager.getCurrentKeyboardFocusManager().
            getPermanentFocusOwner();
}
```

Why this is a smell: the same 4-line block (resolving the effective target component from either the fixed `target` field or the current keyboard focus owner) was copy-pasted into every action subclass. Adding a fourth action (something like `DuplicateAction`) would require copying it again. Changing the resolution strategy, e.g. to support virtual focus, would require the same change in three places, which is exactly where divergence tends to creep in.

## Refactoring plan

What to change: extract the duplicated fragment into a named method and move it up to the parent class.

Strategy:

1. Extract Method (Fowler p.110). Wrap the fragment in a method called `getTargetComponent()`. The name makes the intent explicit: "give me the component this action should operate on."
2. Pull Up Method (Fowler p.322). The method belongs in `AbstractSelectionAction` because that class owns the `target` field the logic depends on. Moving it there eliminates the duplication at its root.

## Refactoring applied

### Step 1: add `getTargetComponent()` to `AbstractSelectionAction`

```java
protected JComponent getTargetComponent() {
    if (target != null) {
        return target;
    }
    KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    if (fm.getPermanentFocusOwner() instanceof JComponent) {
        return (JComponent) fm.getPermanentFocusOwner();
    }
    return null;
}
```

### Step 2: simplify each `actionPerformed`

Before (`CopyAction`):

```java
JComponent c = target;
if (c == null && (KeyboardFocusManager.getCurrentKeyboardFocusManager().
        getPermanentFocusOwner() instanceof JComponent)) {
    c = (JComponent) KeyboardFocusManager.getCurrentKeyboardFocusManager().
            getPermanentFocusOwner();
}
// Note: copying is allowed for disabled components
if (c != null) { ... }
```

After:

```java
JComponent c = getTargetComponent();
// Note: copying is allowed for disabled components
if (c != null) { ... }
```

The same transformation was applied to `CutAction` and `PasteAction`.

## Reasoning

Extract Method is the right tool when a code fragment can be grouped and named. The block is self-contained, and its purpose ("get the component to act on") is clearly separate from the surrounding action logic.

Pull Up Method is correct because the extracted method's only dependency is `target`, which is a field in `AbstractSelectionAction`. Placing the method there respects single responsibility and avoids re-duplication if more sibling actions get added later.

Behavior is identical: same null check, same `instanceof` guard, same keyboard focus fallback. Only the location of the code changed.
