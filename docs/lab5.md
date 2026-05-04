# Lab 5

## Actualization Lab

Selected change request: add subscript and superscript formatting to the font palette.

This lab actualizes the change request from the earlier concept location and impact analysis work. The feature is implemented as two new font-position controls in the existing font palette.

## Implemented Change

| Area | Change |
| --- | --- |
| Attribute model | Added `FONT_SUPERSCRIPT` and `FONT_SUBSCRIPT` to `AttributeKeys` and registered them in `SUPPORTED_ATTRIBUTES`. |
| Generic toolbar | Added `createFontStyleSuperscriptButton` and `createFontStyleSubscriptButton` to `ButtonFactory`. |
| SVG toolbar | Added subscript and superscript buttons to both disclosed states of `FontToolBar`. |
| Rendering | Applied `TextAttribute.SUPERSCRIPT` in `TextFigure`, `TextAreaFigure`, `SVGTextFigure`, and `SVGTextAreaFigure`. |
| Labels | Added text and tooltip labels for the new controls. |

The implementation intentionally keeps the scope small. It adds UI and rendering support for selected figures, but does not extend SVG import/export persistence. That can be handled as a separate change if saving the formatting in SVG files becomes required.

## Actualization Steps

| Step | Result |
| --- | --- |
| 1 | Added the new attributes so figures can store subscript and superscript state. |
| 2 | Updated renderers so the stored attributes change the visible text baseline. |
| 3 | Added toolbar buttons so users can toggle the attributes from the font palette. |
| 4 | Added documentation explaining the design and scope of the actualized change. |

This follows the impact path identified in Lab 3: model attributes first, then rendering, then toolbar integration.

## SOLID Discussion

| Principle | Application in this change |
| --- | --- |
| Single Responsibility | `AttributeKeys` defines shared figure attributes, renderers translate attributes into drawing behavior, and toolbar classes expose UI controls. The change keeps these responsibilities separate. |
| Open/Closed | The existing font style button creation was extended by adding new factory methods that reuse the Lab 4 helper, instead of rewriting existing bold, italic, or underline behavior. |
| Liskov Substitution | The new attributes are normal `AttributeKey<Boolean>` values and do not change the expected behavior of existing figure types. Existing text figures remain usable through the same interfaces. |
| Interface Segregation | No new methods were added to broad figure interfaces. The feature uses the existing attribute mechanism instead of forcing every figure interface to know about subscript and superscript. |
| Dependency Inversion | Toolbar actions depend on the abstract `DrawingEditor` and attribute system, not concrete text figure implementations. Rendering classes consume attributes from the figure model. |

## Clean Architecture Discussion

The implementation keeps the dependency direction consistent with the existing JHotDraw architecture.

| Layer | Role in this change |
| --- | --- |
| Core model | `AttributeKeys` defines the font-position state independently from UI classes. |
| Application/action layer | `AttributeToggler` and `ButtonFactory` connect user actions to attribute updates. |
| Interface/UI layer | `FontToolBar` exposes the new controls in the SVG sample palette. |
| Rendering/infrastructure layer | Text figure classes translate the stored state into Java2D `TextAttribute` values. |

The toolbar does not directly manipulate rendering internals. It toggles attributes through the existing action mechanism. The renderers do not depend on toolbar classes; they only read attributes from figures. This keeps the change localized and avoids coupling UI controls to drawing implementation details.

## Verification

Compile command used after each implementation slice:

```sh
mvn -B -s .maven-settings.xml -DskipTests compile
```

The compile phase succeeds. Full test execution is not used as the main verification for this lab because the repository already has a known Surefire/JGiven test execution failure unrelated to this change.
