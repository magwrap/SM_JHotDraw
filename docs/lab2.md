# Lab 2

## Change Request Lab

Selected feature: Font palette

Existing subfeatures:

| Subfeature | Selected |
| --- | --- |
| Choose font | Yes |
| Font size | Yes |
| Bold | Yes |
| Italic | Yes |
| Underline | Yes |

New requested subfeatures:

| Subfeature | Description |
| --- | --- |
| Subscript | Allow text figures to render selected text lower than the normal baseline. |
| Superscript | Allow text figures to render selected text higher than the normal baseline. |

User story:

As a drawing application user, I want the font palette to support subscript and superscript formatting so that I can create text labels with mathematical notation, chemical formulas, and footnote-style text.

## Concept Location Lab

Initial set of classes for the font palette change request:

| Domain Class | Responsibility |
| --- | --- |
| `org.jhotdraw.draw.AttributeKeys` | Defines text/font attributes such as font face, size, bold, italic, and underline. This is the likely place to add font subscript and superscript attributes. |
| `org.jhotdraw.gui.action.ButtonFactory` | Creates generic font palette toolbar buttons and connects font style buttons to figure attributes through actions. |
| `org.jhotdraw.draw.action.AttributeToggler` | Toggles boolean attributes on the currently selected figures and records undoable edits. |
| `org.jhotdraw.samples.svg.gui.FontToolBar` | Builds the SVG sample font palette UI with font face, font size, bold, italic, and underline controls. |
| `org.jhotdraw.draw.figure.TextHolderFigure` | Interface for editable text figures; used to identify figures that should expose font palette controls. |
| `org.jhotdraw.draw.figure.TextFigure` | Renders single-line text using font-related attributes. |
| `org.jhotdraw.draw.figure.TextAreaFigure` | Renders multi-line text using font-related attributes. |
| `org.jhotdraw.samples.svg.figures.SVGTextFigure` | SVG-specific single-line text figure that uses font attributes when calculating and rendering text shape. |
| `org.jhotdraw.samples.svg.figures.SVGTextAreaFigure` | SVG-specific multi-line text figure that uses font attributes when calculating and rendering text shape. |
| `org.jhotdraw.samples.odg.ODGApplicationModel` | Installs the generic font buttons into the ODG application toolbar. |
| `org.jhotdraw.samples.svg.SVGDrawingPanel` | Instantiates the SVG `FontToolBar` in the SVG drawing panel. |
