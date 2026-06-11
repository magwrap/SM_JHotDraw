# Lab 5 — SOLID and Clean Architecture in JHotDraw

## Selected Feature: Bezier Path Tool

## 1. Actualized Change

A new `PathTool` class was added to `org.jhotdraw.draw.tool`, extending `BezierTool` with a four-click-per-arc state machine (Phase.C2 → Phase.C1 → Phase.ANCHOR → repeat) that places precise control points without curve fitting. The same tool class is registered in both the Draw sample (via `DrawingPanel`) and the SVG sample (via `ToolsToolBar`), demonstrating cross-app reusability.

| Area | Change |
|---|---|
| New class | `PathTool` in `jhotdraw-core` extends `BezierTool` with state machine |
| Toolbar | "Path" button registered in `DrawingPanel` (Draw) and `ToolsToolBar` (SVG) |
| Labels | `edit.createPath` entries added to `Labels.properties`, `Labels_de.properties`, and SVG `Labels.properties` |

## 2. SOLID Principles

### S — Single Responsibility Principle

`PathTool` has a single responsibility: creating `BezierFigure` instances from precise control-point clicks. The phase enum (`C2`, `C1`, `ANCHOR`) cleanly separates the state machine logic from `BezierTool`'s click-and-drag behavior. `BezierTool` retains its responsibility for curve-fitting Scribble creation. `DrawingPanel`/`ToolsToolBar` handle only tool registration, not creation logic.

### O — Open/Closed Principle

`BezierTool` is closed for modification — its existing Scribble and Polygon creation behavior is unchanged. It is open for extension — `PathTool` extends it by overriding `addPointToFigure` with a state machine and passing `false` for `calculateFittedCurveAfterCreation`, without touching any `BezierTool` code. The same extension point serves the SVG app without further changes to the core tool.

### L — Liskov Substitution Principle

`PathTool` is substitutable for `BezierTool` wherever a `BezierTool` is expected. `ButtonFactory.addToolTo()` accepts any `Tool` and works identically for `PathTool` and `BezierTool`. Both `DrawingPanel` and `ToolsToolBar` register the tool through the `Tool` abstraction without special-casing.

### I — Interface Segregation Principle

`PathTool` depends only on the `BezierFigure` constructor argument — it does not need to know about SVG path wrapping, connection figures, or text creation. The `Tool` interface is narrow enough that adding a new tool requires no changes to unrelated tool interfaces.

### D — Dependency Inversion Principle

`DrawingPanel` and `ToolsToolBar` both depend on the `Tool` abstraction, not on `PathTool` concretely. `ButtonFactory.addToolTo()` accepts a `Tool` parameter, so neither panel ever imports `PathTool`'s internals. `PathTool` depends on `BezierFigure` (an abstraction in the figure hierarchy) rather than on concrete drawing logic. The fact that the same `PathTool` class works in two different sample apps without modification confirms the abstraction is well-chosen.

## 3. Clean Architecture

### The four layers (mapped to JHotDraw for the PathTool feature)

| Layer | Components |
|---|---|
| Frameworks and Drivers | `DrawingPanel`, `ToolsToolBar`, `Labels.properties`, Swing toolbar, Nix/Maven build |
| Interface Adapters | `ButtonFactory`, `PathTool` (controller), `ResourceBundleUtil` |
| Use Cases | `BezierTool` (creation orchestration), `DrawingView` (selection), `AbstractTool` |
| Entities | `BezierFigure`, `BezierPath`, `BezierPath.Node`, `Figure` interface, `Tool` interface |

### Dependency Rule

The feature respects the Maven dependency direction `jhotdraw-samples → jhotdraw-core → jhotdraw-api`:

- `PathTool` (in `jhotdraw-core`) extends `BezierTool` and depends only on `BezierFigure` from the figure hierarchy — no dependency on Swing or sample code.
- `DrawingPanel` and `ToolsToolBar` (both in `jhotdraw-samples`) depend on `PathTool` through the `Tool` abstraction via `ButtonFactory`, not the reverse. The same core tool is reused across apps.
- `BezierPath` and `BezierPath.Node` (in `jhotdraw-utils`) are pure geometric data with no UI dependencies. An important API boundary: `BezierFigure.getNode()` returns a clone of the stored node, so callers must use `setNode()` to persist modifications — the entity layer controls its own internal representation.
- Both sample apps depend on the core layer, never the other way around.

### Use-case flow

1. User clicks the "Path" button → `ButtonFactory` creates a `JToggleButton` wired to a `PathTool` instance.
2. User clicks the canvas → `PathTool.mousePressed()` (inherited from `BezierTool`) creates a `BezierFigure` with one node.
3. Each subsequent click → `addPointToFigure` advances through the state machine (C2 → C1 → anchor), placing one control point per click with no dragging.
4. `BezierFigure` stores control points in `BezierPath` — the entity layer manages its own state, with `getNode`/`setNode` controlling access to internal nodes.
5. The view repaints the figure through `BezierFigure.draw()` — the flow returns to the outermost layer.
