# Lab 7

## Testing Lab

Selected feature: font palette (superscript/subscript) from Lab 5.

This lab adds unit tests for the font-position domain logic introduced in Lab 5. The tests cover the two key static methods in `AttributeKeys` — `getPositionedFont` and `getFontBaselineOffset` — and verify that the font-position attributes are properly registered in `SUPPORTED_ATTRIBUTES`.

## Changes Made

| Area | Change |
| --- | --- |
| Test dependency | Added JUnit 4.13.2 to `jhotdraw-core/pom.xml` (existing TestNG kept for backward compatibility). |
| Test class | Created `AttributeKeysFontPositionTest.java` in `jhotdraw-core/src/test/.../draw/`. |
| Production code | Added two Java `assert` statements in `AttributeKeys` to enforce invariant checks. |

## Test Strategy

Each test targets a single code-path through a single method. Dependencies on the `Figure` interface are satisfied entirely by a lightweight stub inner class that stores attribute values in a `HashMap<AttributeKey<?>, Object>`. This stub uses `AttributeKey.getDefaultValue()` for unset keys, avoiding null-pointer exceptions without needing a mocking framework. The approach follows the existing `AbstractFigureImpl` pattern from `AbstractFigureNGTest`.

### Best-case scenarios

| Test | Input | Expected result |
| --- | --- | --- |
| `getPositionedFont_returnsSameFont_whenNoPositionSet` | Sup=false, Sub=false, Arial 12pt | Font unchanged (12pt) |
| `getPositionedFont_scalesDown_whenSuperscript` | Sup=true, Sub=false, Arial 12pt | Font scaled to 8.4pt |
| `getPositionedFont_scalesDown_whenSubscript` | Sup=false, Sub=true, Arial 12pt | Font scaled to 8.4pt |
| `getFontBaselineOffset_returnsZero_whenNoPositionSet` | Sup=false, Sub=false, size=12 | 0.0f |
| `getFontBaselineOffset_returnsNegative_whenSuperscript` | Sup=true, Sub=false, size=12 | −4.2f |
| `getFontBaselineOffset_returnsPositive_whenSubscript` | Sup=false, Sub=true, size=12 | +3.0f |

### Boundary cases

| Test | Input | Expected result |
| --- | --- | --- |
| `getPositionedFont_returnsNull_whenNullFontFace` | FONT_FACE=null | null |
| `getPositionedFont_handlesZeroFontSize` | Size=0, Sup=true | Font with size 0 |
| `getFontBaselineOffset_returnsZero_whenZeroFontSize` | Size=0, Sup=true | 0.0f |
| `fontSuperscript_defaultValue` | No attributes set | false |
| `fontSubscript_defaultValue` | No attributes set | false |

### Registration tests

| Test | Expected result |
| --- | --- |
| `supportedAttributes_containsFontSuperscript` | `SUPPORTED_ATTRIBUTES` contains `FONT_SUPERSCRIPT` |
| `supportedAttributes_containsFontSubscript` | `SUPPORTED_ATTRIBUTES` contains `FONT_SUBSCRIPT` |

## Java Assertions

Two `assert` statements were added to `AttributeKeys.java` to enforce invariants that should never be violated during correct execution:

| Method | Assertion | Rationale |
| --- | --- | --- |
| `getPositionedFont` | `assert font.getSize2D() >= 0 : "Font size must not be negative"` | A negative font size indicates a corrupted model state; execution should stop immediately. |
| `getFontBaselineOffset` | `assert fontSize >= 0 : "Font size must not be negative"` | Same invariant checked before the offset calculation. |

These follow the lab specification: assertions check conditions that should never happen (and halt the program if they do), while exceptions (e.g., null font face) are handled as expected error conditions.

## SOLID Discussion

| Principle | Application in this testing change |
| --- | --- |
| Single Responsibility | Each test verifies exactly one code-path through one method. The stub `Figure` has no UI or rendering concerns. |
| Open/Closed | New tests for superscript/subscript were added without modifying any existing test class or production method signature. |
| Liskov Substitution | The stub `Figure` satisfies the `Figure` interface and can substitute for any concrete figure in the context of attribute lookups. |
| Interface Segregation | Tests depend only on `Figure.get(AttributeKey)` and `Figure.set(AttributeKey, Object)`, not on the full interface. |
| Dependency Inversion | Tests depend on the abstract `AttributeKeys` API and the `Figure` interface, not on concrete figure implementations like `TextFigure`. |

## Clean Architecture Discussion

Testing stays at the core-model layer and does not reach into UI or rendering infrastructure:

| Layer | Role in this testing change |
| --- | --- |
| Core model | `AttributeKeys.getPositionedFont` and `getFontBaselineOffset` are tested directly. |
| Test infrastructure | The stub `Figure` provides controlled attribute values without involving `Drawing`, `DrawingView`, or any toolkit class. |

Tests exercise the same public API that renderers (`TextFigure`, `SVGTextFigure`, etc.) use at runtime, but in isolation. This keeps test failures localised to the model logic rather than being masked by rendering-side issues.

## Verification

Compile and test command:

```sh
mvn -B -s .maven-settings.xml -DskipTests compile
mvn -B -s .maven-settings.xml -pl jhotdraw-core test
```

All 13 JUnit 4 test methods pass, and the existing TestNG test (`AbstractFigureNGTest`) still passes. The sample SVG drawing application launches and runs without regressions. Full project tests are not run because the repository has a known Surefire/JGiven test execution failure in `jhotdraw-gui` unrelated to this change.
