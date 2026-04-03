# Lab 2 — Bezier Path Tool

## Selected Feature: Bezier

The Bezier feature in JHotDraw allows users to draw open and closed bezier paths.
Currently the draw sample provides two tools via `BezierTool`:
- **Scribble** (`BezierFigure()` — open path, free-form)
- **Polygon** (`BezierFigure(true)` — closed path, straight segments)

Both use the same `BezierTool` class. Each mouse click adds a `BezierPath.Node` (a control point) to the figure.

---

## 1. User Story (ChangeReqLab)

**Title:** Add a dedicated Path Tool for Bezier control-point drawing

**As a** user of the JHotDraw drawing application,
**I want** a dedicated Path Tool that creates bezier paths where each click places a control point,
**so that** I can construct precise bezier paths with explicit control over every node.

**Acceptance criteria:**
- A new Path Tool button appears in the drawing palette alongside the existing Scribble and Polygon tools.
- Each mouse click on the canvas adds a bezier node at that coordinate.
- Dragging the mouse adds additional nodes.
- Double-clicking or clicking near the start point closes the path.
- The resulting figure can be selected and its nodes edited via BezierNodeHandles.

---

## 2. Concept Location (CLLab1)

The following table lists the initial set of domain classes involved in implementing the Path Tool change request. Classes were located by tracing the existing Bezier feature from the controller layer (tools) down to the domain model.

| Domain Class | Responsibility |
|---|---|
| `BezierPath` (jhotdraw-utils) | Geometric data model; stores the ordered list of `BezierPath.Node`s, each with three control points (C0, C1, C2). Provides path operations (split, join, find segment). |
| `BezierPath.Node` (jhotdraw-utils) | Represents a single control point on a bezier path. Contains three (x,y) coordinates (C0, C1, C2) and a mask indicating which control points are active. |
| `BezierFigure` (jhotdraw-core) | Attributed figure wrapping a `BezierPath`. Handles drawing, hit-testing, serialization, and handle creation for bezier paths. |
| `BezierTool` (jhotdraw-core) | Tool that creates `BezierFigure` instances through mouse interaction. Adds nodes on click/drag, optionally fits a curve on release. |
| `PathTool` (jhotdraw-core, to be created) | New tool extending `BezierTool` with `calculateFittedCurveAfterCreation = false`, so each user-placed point remains a distinct control point. |
| `DrawingPanel` (jhotdraw-samples-misc) | Registers creation tools into the toolbar via `ButtonFactory.addToolTo()`. Entry point for making the new Path Tool available in the UI. |
