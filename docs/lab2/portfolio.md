# Lab 2: portfolio artifacts

## Part 1: Change request (ChangeReqLab)

### Selected feature: basic editing (cut, copy, paste subfeatures)

From the course feature list, the **basic editing** feature was selected.
This group is registered for the three clipboard subfeatures marked with
1 in the feature list: **cut**, **copy**, **paste**. The remaining basic
editing subfeatures (delete, duplicate; marked 2) are taken by another
group and are out of scope.

**User stories**

*US-1 Copy*

> As a graphic designer using the JHotDraw SVG editor,
> I want to copy selected figures to the clipboard,
> so I can duplicate or reuse them without redrawing.

*US-2 Paste*

> As a graphic designer using the JHotDraw SVG editor,
> I want to paste figures from the clipboard into the drawing,
> so I can place duplicates anywhere in the drawing.

*US-3 Cut*

> As a graphic designer using the JHotDraw SVG editor,
> I want to cut selected figures (copy them to the clipboard and remove
> them from the drawing in one action),
> so I can move them somewhere else without doing two separate steps.

**Acceptance criteria**

- User can select one or more figures and invoke Copy (Ctrl+C or Edit > Copy).
- The selected figures end up on the system clipboard.
- User can invoke Paste (Ctrl+V or Edit > Paste) to insert the clipboard figures into the drawing.
- Pasted figures appear slightly offset from the originals so they are visually distinct.
- Cut (Ctrl+X or Edit > Cut) removes the selected figures and places them on the clipboard in one action.
- Copy is allowed on a disabled component (documented JHotDraw behaviour); paste on a disabled component is a no-op; paste from an empty clipboard is a no-op.

## Part 2: Concept location (CLLab)

### Methodology

I used a static dependency search, which is the rough equivalent of stepping through with a debugger but done by reading code. Starting from the controller entry points (`CopyAction`, `PasteAction`), I followed class dependencies through `DrawingView`, `Drawing`, and `Figure`, using grep across the source tree to find call sites.

### Domain classes

| # | Class | Responsibility |
|---|-------|---------------|
| 1 | `CopyAction` | Action triggered by Ctrl+C or the Edit menu. Exports the selected figures to the system clipboard through the Swing `TransferHandler`. |
| 2 | `CutAction` | Action triggered by Ctrl+X. Combines copy with delete of the selected figures. |
| 3 | `PasteAction` | Action triggered by Ctrl+V or the Edit menu. Reads the `Transferable` from the clipboard and imports the figures into the active drawing. |
| 4 | `DrawingView` | View and selection model. Holds the set of currently selected `Figure` instances. `getSelectedFigures()` is what copy/cut queries to find out what to transfer. |
| 5 | `Drawing` | Figure container (the model). Pasted figures get added through `addAll()`, which then notifies listeners. |
| 6 | `Figure` | The core domain object. Implements `Cloneable`, so copies can be made without the action knowing about concrete subtypes. |
| 7 | `ClipboardUtil` | Provides a platform-neutral handle on the system `Clipboard`. Picks between AWT, JNLP, or OSX implementations at runtime. |
| 8 | `DOMStorableInputOutputFormat` | Serialization. Converts a list of `Figure` objects into a `Transferable` (`createTransferable`), and reads figures back from clipboard content via `read(Transferable, ...)`. |

### Concept location walk-through

```
Start: CopyAction, PasteAction, CutAction       (controllers, entry points)
  ├─ calls ClipboardUtil.getClipboard()         (clipboard access)
  ├─ calls component.getTransferHandler()
  │     └─ uses DOMStorableInputOutputFormat    (transferable creation)
  │           └─ iterates Figure list           (domain objects being transferred)
  └─ queries DrawingView.getSelectedFigures()   (selection model)
        └─ DrawingView references Drawing       (figure container)
              └─ contains Figure instances      (domain objects)
```
