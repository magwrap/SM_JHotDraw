# Lab 3: portfolio artifacts

## Part 1: Impact analysis (AnalysisLab)

### Feature: basic editing (cut, copy, paste subfeatures)

The impact analysis covers the three clipboard subfeatures (cut/copy/paste)
of the **basic editing** feature picked in lab 2. Delete and duplicate
subfeatures are out of scope. The three controllers share the same parent
class (`AbstractSelectionAction`) and therefore the same impact set.

Algorithm used: Fig 7.9 from [Raj13], iterative BFS starting from the initial CHANGED set.

### Initial impact set (from Lab 2 concept location)

All 8 classes marked CHANGED:

| Class | Package |
|-------|---------|
| `CopyAction` | `org.jhotdraw.action.edit` |
| `CutAction` | `org.jhotdraw.action.edit` |
| `PasteAction` | `org.jhotdraw.action.edit` |
| `DrawingView` | `org.jhotdraw.draw` |
| `Drawing` | `org.jhotdraw.draw` |
| `Figure` | `org.jhotdraw.draw.figure` |
| `ClipboardUtil` | `org.jhotdraw.datatransfer` |
| `DOMStorableInputOutputFormat` | `org.jhotdraw.draw.io` |

### Iteration: marking neighbors

From `CopyAction / PasteAction / CutAction`:

- `AbstractSelectionAction` (same package, parent class): CHANGED. The base class holds the `JComponent` reference, so any change to how the actions get the drawing view will flow through it.
- `DefaultMenuBuilder` (`org.jhotdraw.app`): PROPAGATING. Wires Copy/Paste/Cut into the Edit menu by name. It compiles against the actions but contains no clipboard logic of its own.
- `DefaultDrawingEditor` (`org.jhotdraw.draw`): PROPAGATING. Registers copy/paste in the `ActionMap`. A signature change on the action would require an update here.
- `ButtonFactory` (`org.jhotdraw.gui.action`): PROPAGATING. Creates toolbar buttons that reference these actions.

From `DrawingView`:

- `DefaultDrawingViewTransferHandler` (`org.jhotdraw.draw`): CHANGED. This is the actual Swing `TransferHandler` attached to the view, and it implements `exportToClipboard` and `importData`. Directly part of copy/paste execution.
- `FigureSelectionListener` (`org.jhotdraw.draw.event`): UNCHANGED. Observer interface for selection events. Copy/paste does not change how selection is notified.
- `Handle` (`org.jhotdraw.draw.handle`): UNCHANGED. Handles are for resize and move, not related to the clipboard.

From `Drawing`:

- `AbstractDrawing` (`org.jhotdraw.draw`): PROPAGATING. Base implementation. `addAll()` is called during paste and propagates through it.
- `DefaultDrawing` (`org.jhotdraw.draw`): CHANGED. The concrete model that actually stores the figures. Paste inserts figures here via `addAll()`.

From `ClipboardUtil`:

- `AbstractClipboard` (`org.jhotdraw.datatransfer`): PROPAGATING. Shared base for the platform clipboard implementations.
- `AWTClipboard`, `OSXClipboard`, `JNLPClipboard` (`org.jhotdraw.datatransfer`): PROPAGATING. Platform adapters. A change to the clipboard contract propagates to each of them.

From `DOMStorableInputOutputFormat`:

- `InputFormat` interface (`org.jhotdraw.draw.io`): UNCHANGED. The contract `read(Transferable,...)` already exists.
- `OutputFormat` interface (`org.jhotdraw.draw.io`): UNCHANGED. `createTransferable(...)` already exists.
- `SerializationInputOutputFormat` (`org.jhotdraw.draw.io`): UNCHANGED. Alternate format with no role in copy/paste.

### Estimated impact set (Table 1)

| Package | Classes visited | Comments |
|---|---|---|
| `org.jhotdraw.action.edit` | 4 | CopyAction, CutAction, PasteAction (CHANGED); AbstractSelectionAction (CHANGED) because it exposes the drawing editor reference. |
| `org.jhotdraw.draw` | 4 | DrawingView, Drawing (CHANGED); DefaultDrawingViewTransferHandler (CHANGED), which is the most critical; DefaultDrawing (CHANGED); DefaultDrawingEditor and AbstractDrawing are PROPAGATING. |
| `org.jhotdraw.datatransfer` | 4 | ClipboardUtil (CHANGED); AbstractClipboard, AWTClipboard, OSXClipboard (PROPAGATING). Platform adapters that relay clipboard calls. |
| `org.jhotdraw.draw.io` | 1 | DOMStorableInputOutputFormat (CHANGED); InputFormat / OutputFormat (UNCHANGED). |
| `org.jhotdraw.draw.figure` | 1 | Figure (CHANGED) because `Cloneable` is used during clone-on-paste. |
| `org.jhotdraw.app` | 2 | DefaultMenuBuilder, DefaultApplicationModel (PROPAGATING). Wire the actions into the application menus. |
| `org.jhotdraw.gui.action` | 1 | ButtonFactory (PROPAGATING). Toolbar buttons use the action keys. |

Summary: 17 classes visited across 7 packages. 6 CHANGED, 6 PROPAGATING, 5 UNCHANGED.

## Part 2: Continuous integration (CILab)

CI pipeline added at `.github/workflows/ci.yml`.

### Pipeline configuration

Triggers on push or PR to `develop` and `main`.

Steps:

1. Checkout the code.
2. Set up JDK 17 (Temurin) with Maven cache.
3. `mvn clean install -DskipTests` to verify the build.
4. `mvn test` to run the tests.

### Maven settings

`.maven-settings.xml` was added at the project root for GitHub Packages auth. It uses the `GITHUB_ACTOR` and `GITHUB_TOKEN` environment variables that GitHub Actions provides automatically, and the server id is `github`.
