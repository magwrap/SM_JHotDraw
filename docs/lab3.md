# Lab 3

## Impact Analysis Lab

Selected change request: add subscript and superscript formatting to the font palette.

The initial impact set comes from the concept location work in Lab 2. The change starts in the font attribute model, then propagates to toolbar actions and text rendering classes that consume those attributes.

| Package name | # of classes visited | Comments |
| --- | ---: | --- |
| `org.jhotdraw.draw` | 1 | Contains `AttributeKeys`, the shared model for figure attributes. The existing font palette attributes for font face, size, bold, italic, and underline are defined here, so subscript and superscript would likely be added here as new font attributes. |
| `org.jhotdraw.draw.action` | 1 | Contains `AttributeToggler`, which toggles figure attributes from toolbar actions and records undoable edits. This package contributes the action behavior needed by boolean font style controls. |
| `org.jhotdraw.gui.action` | 2 | Contains `ButtonFactory` and `FontChooserHandler`. `ButtonFactory` creates the generic font palette buttons and connects them to drawing attributes, while `FontChooserHandler` handles choosing the font face. |
| `org.jhotdraw.draw.figure` | 3 | Contains `TextHolderFigure`, `TextFigure`, and `TextAreaFigure`. These classes represent editable text figures and render text using font-related attributes, so they are part of the estimated impacted set for baseline-shifting text. |
| `org.jhotdraw.draw.event` | 1 | Contains `FigureAttributeEditorHandler`, which binds attribute editor UI components to the selected figures. This package helps propagate toolbar edits to selected text figures. |
| `org.jhotdraw.samples.svg.gui` | 2 | Contains `FontToolBar` and related toolbar infrastructure for the SVG sample application. `FontToolBar` builds the visible font palette with font face, size, bold, italic, and underline controls. |
| `org.jhotdraw.samples.svg.figures` | 2 | Contains `SVGTextFigure` and `SVGTextAreaFigure`. These SVG-specific text figures calculate and render text shapes using font attributes, so they would need to account for subscript and superscript rendering. |
| `org.jhotdraw.samples.odg` | 1 | Contains `ODGApplicationModel`, which installs the generic font buttons into the ODG application toolbar. This package contributes application-level toolbar assembly. |
| `org.jhotdraw.samples.svg` | 1 | Contains `SVGDrawingPanel`, which instantiates the SVG `FontToolBar`. This package contributes the SVG application entry point for the font palette. |

Estimated impacted set summary:

| Area | Impact |
| --- | --- |
| Attribute model | Add attributes representing subscript and superscript formatting. |
| Toolbar actions | Add controls that toggle those attributes for selected text figures. |
| Text rendering | Apply Java text attributes so rendered text appears as subscript or superscript. |
| Sample application toolbars | Expose the new controls where the existing font palette is assembled. |

## Continuous Integration Lab

The repository now includes a GitHub Actions workflow for Maven-based continuous integration.

Workflow file:

`.github/workflows/maven.yml`

The workflow runs for each pull request. It checks out the repository, installs Java 8, enables Maven dependency caching, and runs the Maven test phase.

The repository also includes `.maven-settings.xml` because the root `pom.xml` declares a GitHub Packages Maven repository with the id `github`. The settings file maps that server id to GitHub Actions credentials through `GITHUB_ACTOR` and `GITHUB_TOKEN`.

CI command used by the workflow:

```sh
mvn -B -s .maven-settings.xml test
```
