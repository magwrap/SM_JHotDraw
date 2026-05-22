Lab 5 — Portfolio: SOLID Principles and Clean Architecture in JHotDraw
Case Study Feature: Undo / Redo Functionality

Part 1: SOLID Principles in JHotDraw
S — Single Responsibility Principle

A class should have only one reason to change.

UndoRedoManager (org.jhotdraw.undo)
Sole responsibility: manage undo and redo operations and store the history of edits. Only changes if the undo/redo strategy changes. It does not handle GUI events, render figures, or manage selection.

UndoAction (org.jhotdraw.action.edit)
Sole responsibility: handle user requests for undo operations from the GUI. Only changes if the undo trigger mechanism changes. It does not manage the undo history or store edits.

RedoAction (org.jhotdraw.action.edit)
Sole responsibility: handle user requests for redo operations from the GUI. Only changes if the redo trigger mechanism changes. It does not manage the undo history or store edits.

O — Open/Closed Principle

Software entities should be open for extension, but closed for modification.

UndoableEdit interface (javax.swing.undo)
Closed for modification — existing code calling edit.undo() or edit.redo() never changes when a new edit type is added. Open for extension — new edit classes can implement UndoableEdit without touching the interface or existing edits.

AbstractViewAction
Closed for modification — the base view access logic is stable. Open for extension — UndoAction, RedoAction, and other view-related actions all extend it without modifying it.

UndoManager (javax.swing.undo)
New undo/redo behaviors are added by extending UndoManager; existing undo logic does not need to change.

L — Liskov Substitution Principle

Subtypes must be substitutable for their base types.

UndoManager ← UndoRedoManager
UndoRedoManager is passed wherever UndoManager is expected. Swapping in another UndoManager implementation does not break callers that depend on the base type.

UndoableEdit implementations
Any class implementing UndoableEdit can be stored in the UndoRedoManager history. The manager only calls undo() and redo() — it does not need to know concrete edit types, and all implementations honor the contract.

UndoAction / RedoAction
Both extend AbstractViewAction and are substitutable in any context expecting an AbstractViewAction. The GUI can treat them uniformly as view actions.

I — Interface Segregation Principle

Many client-specific interfaces are better than one general-purpose interface.

UndoAction vs RedoAction
These are separate interfaces/classes for distinct operations. A client that only needs undo does not depend on redo logic. UndoRedoManager exposes both operations because managing the full history is its stated responsibility.

UndoableEdit vs CompoundEdit
Clients that just want to undo a single edit depend on UndoableEdit. Only clients that need to group multiple edits depend on CompoundEdit. UndoAction never touches CompoundEdit.

View vs DrawingView
The base view interface is separate from the drawing-specific view. Actions that only need generic view access do not depend on drawing-specific methods.

D — Dependency Inversion Principle

Depend upon abstractions, not concretions.

UndoAction depends on AbstractViewAction abstraction
UndoAction extends AbstractViewAction and calls methods on the abstract View interface — it never imports DrawingView or any concrete view class. The actual view access is hidden behind the abstraction.

UndoRedoManager depends on UndoableEdit interface
The manager stores and processes UndoableEdit objects — it does not depend on concrete edit classes. New edit types plug in without the manager changing.

UndoManager hides javax.swing.undo specifics
Callers depend on the UndoManager abstraction. UndoRedoManager extends it and adds JHotDraw-specific behavior — the decision is made at the boundary, not in business logic.

Part 2: Clean Architecture in JHotDraw
The Four Layers (mapped to JHotDraw for Undo/Redo)
┌─────────────────────────────────────────────────────┐
│  Frameworks & Drivers  (outermost)                   │
│  jhotdraw-samples: SVGMain, ODGApplicationModel      │
│  Swing GUI (Undo/Redo menu items, toolbar buttons)   │
│  Nix dev shell, Maven build                          │
├─────────────────────────────────────────────────────┤
│  Interface Adapters  (Controllers / Gateways)        │
│  UndoAction, RedoAction  ← Controllers               │
│  AbstractViewAction  ← Presenter/Controller base     │
│  DefaultMenuBuilder, ButtonFactory  ← Presenters     │
├─────────────────────────────────────────────────────┤
│  Use Cases  (Application Business Rules)             │
│  UndoRedoManager                                     │
│  View interface + DrawingView                        │
│  UndoableEdit handling logic                         │
├─────────────────────────────────────────────────────┤
│  Entities  (Enterprise Business Rules)  ← innermost  │
│  UndoableEdit interface                              │
│  UndoManager (javax.swing.undo)                      │
│  CompoundEdit interface                              │
└─────────────────────────────────────────────────────┘
The Dependency Rule

The Dependency Rule states: source code dependencies must point inward — toward higher-level policy.

In JHotDraw this is enforced by the Maven module dependency graph:

text
jhotdraw-samples  →  jhotdraw-app  →  jhotdraw-actions  →  jhotdraw-core  →  jhotdraw-api
jhotdraw-api (Entities) has zero JHotDraw dependencies — it is the stable core.

jhotdraw-core (Use Cases + some Entities) depends on jhotdraw-api only.

jhotdraw-actions (Interface Adapters / Controllers) depends on jhotdraw-core.

jhotdraw-samples (Frameworks & Drivers) depends on everything but is depended on by nothing.

The UndoableEdit interface never imports UndoAction. UndoManager never imports Swing GUI classes. This is the Dependency Rule in practice.

Undo/Redo as a Clean Architecture Use Case

Following the request flow:

User presses Ctrl+Z → Swing event reaches the Delivery Mechanism (the SVG editor window).

Controller (UndoAction.actionPerformed) translates the event into a call on UndoRedoManager.undo().

Use Case layer (UndoRedoManager) retrieves the last edit from the history and invokes edit.undo().

Entities (UndoableEdit instance) performs the undo operation — the innermost layer does its work.

The Drawing state is updated, and the GUI reflects the restored through the Frameworks & Drivers layer (Swing view updates).

Where JHotDraw Diverges from Clean Architecture

UndoAction and RedoAction live in jhotdraw-actions but directly interact with UndoRedoManager in jhotdraw-core. A strict Clean Architecture would introduce a separate application layer between controllers and use cases.

The Swing GUI (menu items, toolbar buttons) is tightly coupled to action classes. A cleaner separation would inject actions through a dependency injection mechanism rather than hard-wiring them.

JHotDraw predates Clean Architecture terminology but aligns with its spirit: the UndoableEdit / UndoManager entity layer is completely isolated from Swing and GUI concerns.
