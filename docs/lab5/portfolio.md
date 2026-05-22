# Lab 5: SOLID principles and Clean Architecture in JHotDraw

## Case study feature: copy and paste figures

## Part 1: SOLID principles in JHotDraw

### S: Single Responsibility Principle

A class should have only one reason to change.

`CopyAction` (`org.jhotdraw.action.edit`) is responsible for exporting selected figures to the system clipboard via the component's TransferHandler. It only needs to change if the copy operation's interaction with the clipboard changes. It does not parse figures, manage selection, or render anything.

`ClipboardUtil` (`org.jhotdraw.datatransfer`) is responsible for providing a platform-neutral singleton `Clipboard`. It only changes if the strategy for choosing between AWT, OSX, and JNLP implementations changes.

`DOMStorableInputOutputFormat` (`org.jhotdraw.draw.io`) is responsible for converting `Figure` objects to and from a `Transferable` for the clipboard, or to and from a stream for a file. It only changes when the serialization format changes, not when clipboard mechanics change.

### O: Open/Closed Principle

Software entities should be open for extension but closed for modification.

The `Figure` interface (`org.jhotdraw.draw.figure`) is closed for modification: existing code calling `figure.draw(g)` does not change when a new figure type is added. It is open for extension: `SVGRectFigure`, `SVGEllipseFigure`, `SVGImageFigure` all add new figure behavior without touching the interface or any existing figures.

`AbstractSelectionAction` is closed for modification: the focus-resolution logic and property listener are stable. It is open for extension: `CopyAction`, `CutAction`, `PasteAction`, `DeleteAction`, `DuplicateAction` all extend it without modifying it.

The `InputFormat` and `OutputFormat` interfaces (`org.jhotdraw.draw.io`) let new formats (SVG, ODG, serialization) be added by implementing the interfaces. No existing format class needs to change.

### L: Liskov Substitution Principle

Subtypes must be substitutable for their base types.

`Drawing` and `DefaultDrawing`: `DefaultDrawing` is passed wherever a `Drawing` is expected, for example `DrawingView.setDrawing(Drawing)` and `DOMStorableInputOutputFormat.read(Transferable, Drawing, ...)`. Swapping in another `Drawing` implementation like `QuadTreeDrawing` does not break callers.

`Figure` implementations: `Drawing.add(Figure)` accepts any `Figure`. `SVGRectFigure`, `SVGGroupFigure`, and `SVGImageFigure` can all be pasted without the paste logic knowing their concrete type. Only `clone()` and `draw()` are required, and all implementations honor those contracts.

Platform clipboard implementations: `AWTClipboard`, `OSXClipboard`, and `JNLPClipboard` all extend `AbstractClipboard` and are substitutable in `ClipboardUtil.getClipboard()`. Callers never know which platform implementation is active.

### I: Interface Segregation Principle

Many client-specific interfaces are better than one general-purpose interface.

`Drawing` vs `DrawingView`: the model and the view are separate interfaces. `CopyAction` accesses the component (the view) only, it never needs `Drawing.add()`. A class that manages figure storage does not depend on selection or rendering methods.

`InputFormat` vs `OutputFormat`: read-only clients implement only `InputFormat`, write-only clients implement only `OutputFormat`. `DOMStorableInputOutputFormat` implements both because it handles both directions, but that is its stated responsibility, not interface leakage.

`Figure` vs `CompositeFigure`: a client that just wants to draw or clone a figure depends on `Figure`. Only clients that need to iterate children depend on `CompositeFigure`. `CopyAction` never touches `CompositeFigure`.

### D: Dependency Inversion Principle

Depend on abstractions, not concretions.

`CopyAction` depends on the `JComponent` abstraction. `CopyAction.actionPerformed` calls `c.getTransferHandler()` on `JComponent`. It never imports `DefaultDrawingViewTransferHandler` or any concrete drawing class. The actual export mechanism is hidden behind the Swing `TransferHandler`.

`Drawing` depends on the `Figure` interface. `Drawing.add(Figure figure)` means the model layer depends on the `Figure` abstraction, not on `SVGRectFigure` or any concrete subclass. New figure types plug in without the model changing.

`ClipboardUtil` hides platform specifics. All callers depend on `java.awt.datatransfer.Clipboard` (the JDK abstraction). `ClipboardUtil` selects between `AWTClipboard`, `OSXClipboard`, and `JNLPClipboard` at runtime. The decision is made once at the boundary and never in business logic.

## Part 2: Clean Architecture in JHotDraw

### The four layers (mapped to JHotDraw)

```
┌─────────────────────────────────────────────────────┐
│  Frameworks and drivers (outermost)                  │
│  jhotdraw-samples: SVGMain, ODGApplicationModel      │
│  ClipboardUtil + AWTClipboard/OSXClipboard/JNLP      │
│  Nix dev shell, Maven build                          │
├─────────────────────────────────────────────────────┤
│  Interface adapters (controllers, gateways)          │
│  CopyAction, PasteAction, CutAction   (controllers)  │
│  DefaultDrawingViewTransferHandler   (gateway)       │
│  DOMStorableInputOutputFormat        (gateway)       │
│  DefaultMenuBuilder, ButtonFactory   (presenters)    │
├─────────────────────────────────────────────────────┤
│  Use cases (application business rules)              │
│  AbstractSelectionAction                             │
│  Tool interface + SelectionTool                      │
│  DrawingView (selection management)                  │
├─────────────────────────────────────────────────────┤
│  Entities (enterprise business rules, innermost)     │
│  Figure interface                                    │
│  Drawing interface                                   │
│  Handle interface                                    │
│  CompositeFigure interface                           │
└─────────────────────────────────────────────────────┘
```

### The Dependency Rule

The Dependency Rule says that source code dependencies must point inward, toward higher-level policy.

In JHotDraw this is enforced by the Maven module dependency graph:

```
jhotdraw-samples  →  jhotdraw-app  →  jhotdraw-actions  →  jhotdraw-core  →  jhotdraw-api
```

`jhotdraw-api` (entities) has zero JHotDraw dependencies. It is the stable core.

`jhotdraw-core` (use cases plus some entities) depends on `jhotdraw-api` only.

`jhotdraw-actions` (interface adapters and controllers) depends on `jhotdraw-core` and `jhotdraw-datatransfer`.

`jhotdraw-samples` (frameworks and drivers) depends on everything but is depended on by nothing.

The `Figure` interface never imports `CopyAction`. `Drawing` never imports `ClipboardUtil`. That is the Dependency Rule in practice.

### Copy/paste as a Clean Architecture use case

Following the request flow (similar to Clean Architecture Fig 1.1 to 1.6):

1. User presses Ctrl+C. The Swing event reaches the delivery mechanism (the SVG editor window).
2. The controller (`CopyAction.actionPerformed`) translates the event into a call on the Swing `TransferHandler`.
3. The gateway (`DefaultDrawingViewTransferHandler.exportToClipboard`) receives the request, queries `DrawingView.getSelectedFigures()` from the use case layer, then delegates serialization to `DOMStorableInputOutputFormat`, which is another gateway.
4. Entities (`Figure` instances) are iterated and serialized. The innermost layer does its work.
5. The serialized `Transferable` flows back out through the gateway to the frameworks and drivers layer (`ClipboardUtil.getClipboard()`), where it ends up on the OS clipboard.

### Where JHotDraw diverges from Clean Architecture

`DefaultDrawingViewTransferHandler` lives in `jhotdraw-core` but acts as a gateway (an interface adapter). A strict Clean Architecture would put it in a separate adapter module between core and the clipboard driver.

The samples modules contain both the outermost framework wiring and some application configuration. A cleaner separation would isolate startup and configuration.

JHotDraw predates the Clean Architecture terminology but aligns with its spirit. The `Figure` and `Drawing` entity layer is completely isolated from Swing, the clipboard, and file I/O concerns.
