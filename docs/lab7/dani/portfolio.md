# TestLab 1 — Portfolio: Unit Testing the PathTool Feature

## Feature Under Test

**PathTool** (`org.jhotdraw.draw.tool`): a bezier path creation tool that places precise control points via a four-click-per-arc state machine. The tool creates and modifies `BezierFigure` instances — the domain model that manages nodes, control points, mask flags, and open/closed paths.

---

## Testing Strategy

### Scope

Pure domain logic in `BezierFigure` and the `PathTool` state machine. `BezierFigure` has no Swing/UI dependencies at the unit-test level (no `Graphics2D`, no `JComponent`), making it ideal for fast, isolated unit tests. UI rendering and `DrawingEditor`/`DrawingView` wiring are excluded from this test suite.

### Framework

- **TestNG 6.8.21** — already used in the `jhotdraw-core` module
- No additional dependencies were required; the existing `testng` dependency in `jhotdraw-core/pom.xml` is used as-is

### Test Cases (18 total)

#### Invariant Checks

| Test | What it verifies |
|------|-----------------|
| `testGetNodeReturnsClone` | `BezierFigure.getNode()` returns a defensive copy, not the internal reference |
| `testNodeConstructorCopiesCoordinates` | All three control points (C0, C1, C2) start at the anchor position |
| `testMaskFlags` | `C1_MASK` and `C2_MASK` are independent and composable |
| `testFigureSetPointMovesAllControlPointsWhenCoord0` | `setPoint(index, point)` moves C0 and propagates to C1/C2 |

#### Best-Case Scenarios

| Test | What it verifies |
|------|-----------------|
| `testAddNodeIncreasesNodeCount` | Appending nodes increments the figure's node count |
| `testSetNodePersistsChanges` | `setNode()` writes back control-point and mask modifications |
| `testGetNodeMaskPreservedThroughSetNode` | Mask flags survive a `getNode` → modify → `setNode` round-trip |
| `testFirstClickCreatesFirstNode` | PathTool's first `addPointToFigure` produces one node |
| `testSecondClickSetsC2OnLastNode` | Second click sets `C2_MASK` and C2 coordinates on node 0 |
| `testFourthClickCreatesSecondNodeWithC1` | Full four-click cycle produces two nodes with correct C1 and C2 |
| `testTwoFullCyclesProduceThreeNodes` | Two arcs produce three nodes with correct accumulation |
| `testStateMachineFullCycle` | Phase transitions (C2 → C1 → ANCHOR → C2) cycle correctly |
| `testC1SetOnNewNodeAfterFourthClick` | Fourth click creates a new node with the stored `pendingC1` |
| `testRemoveNodeDecreasesCount` | Removing a node reduces the count and shifts indices |

#### Boundary & Error Cases

| Test | What it verifies |
|------|-----------------|
| `testGetNodeOnEmptyFigureThrows` | `getNode(0)` on an empty figure throws `IndexOutOfBoundsException` |
| `testSetControlPointModifiesSingleCoordinate` | Setting C1 leaves C0 and C2 unchanged |
| `testNodeCountAfterMultipleCycles` | PathTool maintains correct node count across repeated cycles |
| `testMiddleNodeGetsBothMasksAfterTwoCycles` | A node receiving both C1 and C2 accumulates `C1_MASK | C2_MASK` |

---

## Test Results

```
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

All 18 tests pass with no failures or errors. Additionally, the 4 pre-existing `BezierPathNGTest` tests continue to pass (22 total tests passing in the module).

---

## Reflection

### What was tested well

- **Core state transitions**: node add/remove, control-point placement, mask flag accumulation
- **Defensive copying**: `BezierFigure.getNode()` returns a clone, protecting internal state — a critical boundary discovered during PathTool development
- **Precondition violations**: empty-figure edge case is handled with an exception
- **State machine correctness**: full click cycles verified across one and two arcs

### What was not tested

- **Rendering**: `draw(Graphics2D)` was omitted — would require mocking `Graphics2D`, adding little value for a unit test
- **Tool interaction with DrawingEditor/DrawingView**: `PathTool.mousePressed()` was excluded because it mixes Swing event handling with domain logic; testing it would require mocking `MouseEvent`, `DrawingEditor`, etc.
- **Handle interaction**: `BezierNodeHandle`, `BezierControlPointHandle`, etc. were excluded — these are interaction-layer classes better tested with integration tests
- **Undo/redo**: The undoable edits produced by path creation were not tested

### Future work

- Add property-change listener tests to verify that node mutations fire the correct events
- Extract the Swing-heavy `BezierTool` creation logic into a testable strategy, or write integration tests using `ComponentTest`
- Explore mutation testing (PIT) to measure test-suite quality
