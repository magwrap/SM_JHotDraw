# Lab 3 — Impact Analysis and Continuous Integration

## Selected Feature: Bezier Path Tool

Change request: Add a `PathTool` to the draw application so that each mouse click
places a control point (`BezierPath.Node`) on a bezier path, without curve-fitting.

---

## 1. Impact Analysis (AnalysisLab1)

### Process

Starting from the initial concept set identified in L02 (concept location), static
and dynamic analysis was applied to trace class dependencies. For each class in the
initial set, inbound and outbound references were followed to find all impacted
classes. The propagation stops when a class has no further dependencies on or from
the bezier feature.

### Initial Concept Set (from L02)

| # | Class | Package | Module |
|---|-------|---------|--------|
| 1 | `BezierPath` | `org.jhotdraw.geom` | jhotdraw-utils |
| 2 | `BezierPath.Node` | `org.jhotdraw.geom` | jhotdraw-utils |
| 3 | `BezierFigure` | `org.jhotdraw.draw.figure` | jhotdraw-core |
| 4 | `BezierTool` | `org.jhotdraw.draw.tool` | jhotdraw-core |
| 5 | `PathTool` | (to be created in `org.jhotdraw.draw.tool`) | jhotdraw-core |
| 6 | `DrawingPanel` | `org.jhotdraw.samples.draw` | jhotdraw-samples-misc |

### Estimated Impact Set

| Package name | # of classes | Comments |
|---|---|---|
| `org.jhotdraw.geom` | 4 | Core bezier data structures. `BezierPath` (and inner `Node`) define the control-point model. `Bezier` provides curve-fitting (disabled in new tool). `BezierPathIterator` enables shape operations. Directly impacted — the new PathTool creates and manipulates bezier paths. |
| `org.jhotdraw.draw.figure` | 5 | `BezierFigure` is the base figure for bezier paths. `LineFigure` and `LineConnectionFigure` extend it. `ConnectionFigure` uses `BezierPath` for routing. `TriangleFigure` constructs its shape from bezier nodes. Impacted because the new PathTool creates instances of these figures. |
| `org.jhotdraw.draw.tool` | 1 | `BezierTool` is the parent class of the new `PathTool`. Directly impacted — the new tool overrides `finishCreation()` and disables curve-fitting. |
| `org.jhotdraw.draw.handle` | 5 | Handles for interactive editing of bezier figures: `BezierNodeHandle`, `BezierControlPointHandle`, `BezierOutlineHandle`, `BezierScaleHandle`, and `AbstractConnectionHandle` which references `BezierFigure`. These are indirectly impacted — they are used to edit the figures created by PathTool. |
| `org.jhotdraw.draw.connector` | 1 | `ChopBezierConnector` provides connection points for bezier figures. Impacted because bezier figures created by PathTool may be connected to by other figures. |
| `org.jhotdraw.draw.locator` | 2 | `BezierLabelLocator` and `BezierPointLocator` position labels and references on bezier paths. Impacted if users add labels to bezier figures created by PathTool. |
| `org.jhotdraw.draw.event` | 1 | `BezierNodeEdit` provides undo/redo for bezier node changes. Impacted because the new tool's undo events depend on this. |
| `org.jhotdraw.draw.liner` | 4 | `Liner` interface and its implementations (`CurvedLiner`, `ElbowLiner`, `SlantedLiner`) all manipulate `BezierPath` instances to route connection lines. Impacted if connection figures are used alongside PathTool-created bezier shapes. |
| `org.jhotdraw.samples.draw` | 3 | `DrawingPanel` and `DrawApplicationModel` are the integration points where the new PathTool will be registered. `DrawFigureFactory` handles XML serialization of bezier figures. Directly impacted — these files must be modified to register the new tool. |
| `org.jhotdraw.samples.svg.figures` | 4 | `SVGBezierFigure` (extends `BezierFigure`) and `SVGPathFigure` (composite container) serve as the design precedent for the new PathTool. `SVGPathOutlineHandle` and `ConvexHullOutlineHandle` are handles for the composite figure. |
| `org.jhotdraw.samples.svg` | 5 | `PathTool` (SVG version) is the direct precedent for the new PathTool. `ToolsToolBar` registers it. `CombineAction` and `SplitAction` operate on composite path figures. `SVGDrawingPanel` hosts the toolbar. |
| `org.jhotdraw.samples.svg.io` | 7 | SVG I/O classes (`SVGInputFormat`, `SVGOutputFormat`, `SVGZInputFormat`, `SVGZOutputFormat`, `SVGFigureFactory`, `DefaultSVGFigureFactory`, `ImageMapOutputFormat`) all serialize or deserialize bezier paths. Indirectly impacted — any new PathTool figure type must also be serializable. |
| `org.jhotdraw.samples.odg.figures` | 3 | `ODGBezierFigure` and `ODGPathFigure` are ODG-specific parallels to the SVG composite pattern. `ODGPathOutlineHandle` provides editing handles. |
| `org.jhotdraw.samples.odg` | 4 | `PathTool` (ODG, another precedent), `CombineAction`, `SplitAction`, and `ODGDrawingPanel` follow the same pattern as SVG. |
| `org.jhotdraw.samples.odg.io` | 1 | `ODGInputFormat` reads bezier paths from ODG files. |
| `org.jhotdraw.samples.mini` | 2 | `BezierDemo` demonstrates the curve-fitting algorithm. `LabeledLineConnectionFigureSample` shows label locators on bezier paths. |

**Total: 52 classes across 16 packages in 4 modules.**

### Impact Propagation Diagram

```
Initial set (6 classes)
  |
  +-- org.jhotdraw.geom (4 classes)
  |     BezierPath <-- BezierPath.Node
  |     Bezier <-- BezierPathIterator
  |
  +-- org.jhotdraw.draw.figure (5 classes)
  |     BezierFigure <-- LineFigure <-- LineConnectionFigure
  |     ConnectionFigure <-- TriangleFigure
  |
  +-- org.jhotdraw.draw.tool (1 class)
  |     BezierTool <-- [PathTool] (new)
  |
  +-- org.jhotdraw.draw.handle (5 classes)
  |     BezierNodeHandle, BezierControlPointHandle,
  |     BezierOutlineHandle, BezierScaleHandle,
  |     AbstractConnectionHandle
  |
  +-- org.jhotdraw.draw.connector (1 class)
  |     ChopBezierConnector
  |
  +-- org.jhotdraw.draw.locator (2 classes)
  |     BezierLabelLocator, BezierPointLocator
  |
  +-- org.jhotdraw.draw.event (1 class)
  |     BezierNodeEdit
  |
  +-- org.jhotdraw.draw.liner (4 classes)
  |     Liner, CurvedLiner, ElbowLiner, SlantedLiner
  |
  +-- org.jhotdraw.samples.draw (3 classes)
  |     DrawingPanel, DrawApplicationModel, DrawFigureFactory
  |
  +-- org.jhotdraw.samples.svg.figures (4 classes)
  |     SVGBezierFigure, SVGPathFigure (precedent)
  |     SVGPathOutlineHandle, ConvexHullOutlineHandle
  |
  +-- org.jhotdraw.samples.svg (5 classes)
  |     PathTool, ToolsToolBar (integration points)
  |     CombineAction, SplitAction, SVGDrawingPanel
  |
  +-- org.jhotdraw.samples.svg.io (7 classes)
  |     SVGInputFormat, SVGOutputFormat, SVGZ variants
  |     SVGFigureFactory, DefaultSVGFigureFactory, ImageMapOutputFormat
  |
  +-- org.jhotdraw.samples.odg.figures (3 classes)
  |     ODGBezierFigure, ODGPathFigure, ODGPathOutlineHandle
  |
  +-- org.jhotdraw.samples.odg (4 classes)
  |     PathTool, CombineAction, SplitAction, ODGDrawingPanel
  |
  +-- org.jhotdraw.samples.odg.io (1 class)
  |     ODGInputFormat
  |
  +-- org.jhotdraw.samples.mini (2 classes)
        BezierDemo, LabeledLineConnectionFigureSample
```

---

## 2. Continuous Integration (CILab)

### CI Pipeline Configuration

A GitHub Actions workflow was created at `.github/workflows/maven.yml` to
automatically build the project and run tests on every pull request.

### Workflow File: `.github/workflows/maven.yml`

```yaml
name: Java CI with Maven

on:
  pull_request:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: maven
    - name: Build with Maven
      run: mvn -B compile --file pom.xml
    - name: Run tests
      run: mvn -B test --file pom.xml
```

### Key Design Decisions

- **Trigger:** The workflow runs on pull requests targeting `main`, following CI
  best practices of verifying every integration before merge.
- **JDK 21:** The project source uses modern Java features; JDK 21 provides
  broad compatibility.
- **Maven caching:** The `cache: maven` option speeds up subsequent builds by
  reusing downloaded dependencies.
- **Separate compile and test steps:** This provides clearer failure output —
  compilation errors are reported separately from test failures.

### Verification

To verify the CI pipeline works:
1. Create a feature branch and push changes
2. Open a pull request against `main`
3. GitHub Actions automatically triggers the workflow
4. The status check appears in the PR — green (pass) or red (fail)
