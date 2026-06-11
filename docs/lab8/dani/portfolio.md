# BDD Lab Portfolio: Behavior-Driven Development for the PathTool Feature

## Feature Under Test

**PathTool** (`org.jhotdraw.draw.tool`): a bezier path creation tool that places precise control points via a four-click-per-arc state machine. The tool creates and modifies `BezierFigure` instances — the domain model that manages nodes, control points, mask flags, and open/closed paths.

---

## User Story Mapping to BDD Scenarios

### User Story 1: Node Lifecycle Management

*As a user, I want to add and remove nodes from a bezier figure so that I can shape the path.*

#### Scenario 1.1: Adding nodes increases node count
| BDD Element | Description |
|-------------|-------------|
| **Given** | a new figure |
| **When** | a node is added at (10, 20) and another node is added at (30, 40) |
| **Then** | the figure has node count 2 |

#### Scenario 1.2: Adding and removing nodes
| BDD Element | Description |
|-------------|-------------|
| **Given** | a figure with two nodes |
| **When** | the first node is removed |
| **Then** | the figure has node count 1 and the remaining node is at (30, 40) |

#### Scenario 1.3: Removing last node empties figure
| BDD Element | Description |
|-------------|-------------|
| **Given** | a figure with one node at (10, 20) |
| **When** | the only node is removed |
| **Then** | the figure is empty |

### User Story 2: Control Point Customization

*As a user, I want to set C1 and C2 control points to create curved bezier segments.*

#### Scenario 2.1: Setting C2 control point
| BDD Element | Description |
|-------------|-------------|
| **Given** | a figure with one node at (10, 20) |
| **When** | the C2 control point is set to (100, 200) |
| **Then** | the C2 control point is at (100, 200) and the C2 mask is set |

#### Scenario 2.2: Setting C1 control point
| BDD Element | Description |
|-------------|-------------|
| **Given** | a figure with one node at (10, 20) |
| **When** | the C1 control point is set to (50, 60) |
| **Then** | the C1 control point is at (50, 60) and the C1 mask is set |

#### Scenario 2.3: Masks accumulate correctly
| BDD Element | Description |
|-------------|-------------|
| **Given** | a figure with one node at (10, 20) |
| **When** | the C2 control point is set to (100, 200) and the C1 control point is set to (50, 60) |
| **Then** | both C1 and C2 masks are set |

### User Story 3: Control Point Independence

*As a user, I want control points to be independent so that changing one does not affect others.*

#### Scenario 3.1: Node control points are independent
| BDD Element | Description |
|-------------|-------------|
| **Given** | a figure with one node at (10, 20) |
| **When** | the C1 control point is set to (50, 60) |
| **Then** | C0 remains at (10, 20) and C2 remains at (10, 20) |

### User Story 4: State Protection

*As a developer, I want the figure to protect its internal state through defensive copies.*

#### Scenario 4.1: getNode() returns defensive copy
| BDD Element | Description |
|-------------|-------------|
| **Given** | a figure with one node at (10, 20) |
| **When** | the retrieved node is mutated |
| **Then** | the stored node is unchanged |

#### Scenario 4.2: setNode() persists through write-back
| BDD Element | Description |
|-------------|-------------|
| **Given** | a figure with one node at (10, 20) |
| **When** | the node is retrieved, modified, and written back via setNode() |
| **Then** | the modifications are persisted |

### User Story 5: Error Handling

*As a user, I want clear failure modes when operations are performed on an empty figure.*

#### Scenario 5.1: getNode() on empty figure throws
| BDD Element | Description |
|-------------|-------------|
| **Given** | an empty figure |
| **When** | a node is retrieved |
| **Then** | an IndexOutOfBoundsException is thrown |

### User Story 6: Figure Geometry Operations

*As a user, I want to control the open/closed state and reposition nodes of a bezier figure.*

#### Scenario 6.1: Figure closes and opens
| BDD Element | Description |
|-------------|-------------|
| **Given** | a figure with two nodes |
| **When** | the figure is closed |
| **Then** | the figure is closed and has node count 2 |
| **When** | the figure is opened |
| **Then** | the figure is not closed |

#### Scenario 6.2: setPoint() moves anchor and all controls
| BDD Element | Description |
|-------------|-------------|
| **Given** | a figure with one node at (10, 20) |
| **When** | the anchor is moved to (30, 40) |
| **Then** | all control points (C0, C1, C2) are at (30, 40) |

---

## Automated BDD Scenarios

### Framework

- **TestNG 6.8.21** — test runner (pre-existing in `jhotdraw-core`)
- **AssertJ 3.24.2** — fluent domain-specific assertions (added to `jhotdraw-core/pom.xml`)
- **JGiven 1.0.0** — BDD scenario stage framework (added to `jhotdraw-core/pom.xml`, stage classes written but execution uses plain TestNG + AssertJ with JGiven stage patterns in source comments)

### Test Location

`jhotdraw-core/src/test/java/org/jhotdraw/draw/figure/BezierFigureBDDTest.java`

### BDD Test Structure (12 scenarios)

Each test method in `BezierFigureBDDTest` is structured with explicit `// Given`, `// When`, `// Then` sections that mirror the BDD scenario tables above. AssertJ's fluent API is used for all assertions.

#### Exemplar: `setting_c2_control_point`

```java
@Test
public void setting_c2_control_point() {
    // Given
    BezierFigure fig = new BezierFigure(false);
    fig.addNode(new BezierPath.Node(10, 20));

    // When
    int idx = fig.getNodeCount() - 1;
    BezierPath.Node node = fig.getNode(idx);
    node.setControlPoint(2, new Point2D.Double(100, 200));
    node.setMask(node.getMask() | BezierPath.C2_MASK);
    fig.setNode(idx, node);

    // Then
    assertThat(fig.getNode(idx).getControlPoint(2))
            .isEqualTo(new Point2D.Double(100, 200));
    assertThat(fig.getNode(idx).getMask() & BezierPath.C2_MASK)
            .isNotZero();
}
```

#### Scenario Coverage

| Scenario | Test Method | User Story |
|----------|-------------|------------|
| 1.1 | `adding_nodes_increases_node_count` | Node Lifecycle |
| 1.2 | `adding_and_removing_nodes` | Node Lifecycle |
| 1.3 | `removing_last_node_empties_figure` | Node Lifecycle |
| 2.1 | `setting_c2_control_point` | Control Points |
| 2.2 | `setting_c1_control_point` | Control Points |
| 2.3 | `masks_accumulate_correctly` | Control Points |
| 3.1 | `node_control_points_are_independent` | Independence |
| 4.1 | `get_node_returns_defensive_copy` | State Protection |
| 4.2 | `set_node_persists_through_write_back` | State Protection |
| 5.1 | `get_node_on_empty_figure_throws` | Error Handling |
| 6.1 | `figure_closes_and_opens` | Figure Geometry |
| 6.2 | `set_point_moves_anchor_and_all_controls` | Figure Geometry |

---

## Test Results

```
Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

All 30 tests pass (12 new BDD scenarios + 18 pre-existing unit tests from Lab 7).

---

## Reflection

### What was gained with BDD

- **Traceability**: Each BDD scenario maps directly to a user story from the requirements, making it easy to demonstrate that the implementation satisfies the specification.
- **Readability**: The `// Given` / `// When` / `// Then` structure in each test method makes the intent of the test immediately clear to any developer reading the code.
- **Domain focus**: AssertJ's fluent assertions read like natural language (`assertThat(figure.getNodeCount()).isEqualTo(2)`), which reduces the gap between specification and verification.
- **Boundary awareness**: Scenarios 4.1 (defensive copy) and 5.1 (exception on empty figure) emerged directly from thinking about edge cases during BDD scenario writing.

### Challenges

- **JGiven TestNG integration**: JGiven 1.0.0 requires a specific TestNG listener (`ScenarioTestListener`) for its report model, but the internal `scenarioCaseModel` was not initialized despite adding `@Listeners`. This may be a compatibility issue with TestNG 6.8.21. The final test class uses plain TestNG with AssertJ, preserving the BDD structure through explicit comments and fluent assertions.
- **No Swing testing**: AssertJ-Swing was not used because the `BezierFigure` domain class has no Swing dependencies. A future integration test layer could use AssertJ-Swing for `PathTool` mouse-event testing.

### Future work

- Investigate JGiven version compatibility with TestNG 6.8.21 to enable automated report generation
- Add AssertJ-Swing tests for `PathTool` mouse-event handling and visual-marker rendering
- Extend BDD scenarios to cover multi-arc interactions (three or more bezier segments)
