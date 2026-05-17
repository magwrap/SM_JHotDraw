# Lab 2 — Portfolio Artifacts

## Part 1: Change Request (ChangeReqLab)

### Selected Feature: Copy / Paste Figures

**User Story:**

> As a graphic designer using the JHotDraw SVG editor,
> I want to copy selected figures to the clipboard and paste them back into the drawing,
> so that I can quickly duplicate or reuse shapes without redrawing them from scratch.

**Acceptance criteria:**
- User can select one or more figures and invoke Copy (Ctrl+C / Edit > Copy).
- The selected figures are stored on the system clipboard.
- User can invoke Paste (Ctrl+V / Edit > Paste) to insert copies of the clipboard figures into the drawing.
- Pasted figures appear slightly offset from the originals so they are visually distinct.
- Cut (Ctrl+X) removes the selected figures and places them on the clipboard.

---

## Part 2: Concept Location (CLLab)

### Methodology

Static dependency search (equivalent to dynamic IDE debugger trace):  
Starting from the controller entry points (`CopyAction`, `PasteAction`) and following class dependencies through `DrawingView` → `Drawing` → `Figure`, using grep across the source tree.

### Domain Classes

| # | Domain Class | Responsibility |
|---|--------------|----------------|
| 1 | `CopyAction` | Controller (Action) — triggered by Ctrl+C / menu; exports selected figures to the system clipboard via the Swing `TransferHandler` |
| 2 | `CutAction` | Controller (Action) — triggered by Ctrl+X; combines copy + delete of selected figures |
| 3 | `PasteAction` | Controller (Action) — triggered by Ctrl+V / menu; retrieves `Transferable` from clipboard and imports figures into the active drawing |
| 4 | `DrawingView` | View + Selection model — maintains the set of currently selected `Figure` instances; `getSelectedFigures()` is queried by copy/cut to determine what to transfer |
| 5 | `Drawing` | Model (figure container) — receives newly pasted figures via `addAll()`; notifies listeners after modification |
| 6 | `Figure` | Core domain object — the entity being copied/pasted; implements `Cloneable` so copies can be created without knowing concrete subtypes |
| 7 | `ClipboardUtil` | Infrastructure — provides platform-neutral access to the system `Clipboard` singleton (AWT, JNLP, or OSX implementation selected at runtime) |
| 8 | `DOMStorableInputOutputFormat` | Serialization — converts a list of `Figure` objects into a `Transferable` for clipboard storage (`createTransferable`), and reads figures back from clipboard content (`read(Transferable, ...)`) |

### Concept Location Walk-through (Dependency Search)

```
Start: CopyAction / PasteAction / CutAction   [Controllers — entry points]
  ├─ calls ClipboardUtil.getClipboard()        [Infrastructure — clipboard access]
  ├─ calls component.getTransferHandler()
  │     └─ uses DOMStorableInputOutputFormat   [Serialization — Transferable creation]
  │           └─ iterates Figure list          [Domain — objects being transferred]
  └─ queries DrawingView.getSelectedFigures()  [Selection model]
        └─ DrawingView references Drawing      [Model — figure container]
              └─ contains Figure instances     [Domain objects]
```
