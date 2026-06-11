# Lab 4 — Refactoring

## Selected Feature: Bezier Path Tool

## 1. Code Smell

**Duplicated Code** (Chapter 4, Fowler) in `BezierFigure.drawCaps()` and `BezierFigure.getCappedPath()`. The start- and end-decoration
branches are structurally identical in each method, differing only in array indices and mask constants. This duplication
makes the class harder to maintain — any change to the decoration logic must be applied in two places, and the
long duplicated blocks obscure the method's intent.

## 2. Planned Change

Extract the duplicated decoration-drawing logic from `drawCaps` into a private helper `drawDecoration`, and extract
the duplicated path-capping logic from `getCappedPath` into a private helper `capPathEnd`.

## 3. Strategy

Apply **Extract Method** (Fowler, p. 110) twice:
- Identify the common block in `drawCaps` (parameterized by `LineDecoration`, node index, and neighbor index),
  and factor it into `drawDecoration(Graphics2D, LineDecoration, int nodeIndex, int neighborIndex)`.
- Identify the common block in `getCappedPath` (parameterized by `LineDecoration`, node/neighbor indices, and
  mask constants `C1_MASK`/`C2_MASK`), and factor it into
  `capPathEnd(BezierPath, LineDecoration, int nodeIndex, int neighborIndex, int nodeMask, int neighborMask)`.

Both refactorings are behavior-preserving: the decorated figure renders identically, and the capped path
is computed identically. The internal structure is clearer and more maintainable for the upcoming PathTool
change request.

## 4. Applied Refactorings

| Refactoring | Target | Purpose |
|---|---|---|
| Extract Method | `drawCaps` → `drawDecoration` | Consolidate duplicated decoration-drawing logic |
| Extract Method | `getCappedPath` → `capPathEnd` | Consolidate duplicated capping logic |
