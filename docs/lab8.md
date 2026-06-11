# Lab 8

## Behavior-Driven Development Testing

Selected feature: font palette (superscript/subscript) from Lab 5.

This lab adds BDD scenarios using JGiven for the font-position domain logic, using AssertJ for fluent assertions.

## Changes Made

| Area | Change |
| --- | --- |
| Test dependencies | Added JGiven 0.15.0 (junit, html5-report) and AssertJ 3.24.2 to `jhotdraw-core/pom.xml`. |
| Refactoring | Extracted `StubFigure` from inner class to top-level `org.jhotdraw.draw.StubFigure` for reuse across test suites. |
| Stage classes | Created `GivenFontPosition`, `WhenFontPosition`, `ThenFontPosition` — JGiven stage classes in `org.jhotdraw.draw`. |
| BDD test class | Created `FontPositionBDDTest` with 6 scenarios extending `ScenarioTest`. |
| Build config | Added `maven-surefire-plugin` with `--add-opens java.base/java.lang=ALL-UNNAMED` (required for JGiven's ByteBuddy stage creation on Java 11+). |
| Documentation | This file. |

## User Stories

| Story | Scenario |
| --- | --- |
| As a user, I want superscript text to appear raised above the baseline | Superscript scales font down & shifts baseline upward |
| As a user, I want subscript text to appear lowered below the baseline | Subscript scales font down & shifts baseline downward |
| As a user, I want only one position style active at a time | Superscript and subscript are mutually exclusive |
| As a user, I want normal text when no position is selected | Normal text renders with original font and zero offset |
| As a user, I want null font face handled gracefully | Null font face returns null font |
| As a user, I want zero font size handled without errors | Zero font size is handled without exception |

## Scenario Descriptions

Each scenario follows the Given-When-Then pattern via JGiven stage classes:

| Scenario | Given | When | Then |
| --- | --- | --- | --- |
| Normal text | a figure with font Arial 12 | no font position is set | font size 12.0, offset 0.0 |
| Superscript | a figure with font Arial 12 | superscript is applied | font size 8.4, offset −4.2 |
| Subscript | a figure with font Arial 12 | subscript is applied | font size 8.4, offset +3.0 |
| Mutual exclusivity | a figure with font Arial 12 | superscript then subscript | sup=false, sub=true |
| Null font face | a figure with null font | no font position is set | font is null |
| Zero font size | a figure with zero font size | superscript is applied | font size 0.0 |

## Stage Classes

Three stage classes share the `StubFigure` instance via `@ProvidedScenarioState` / `@ScenarioState` annotations:

```
GivenFontPosition  ──[ProvidedScenarioState]──>  WhenFontPosition
                                                      │
                                                      ├──[ScenarioState]──>  ThenFontPosition
                                                      └──[ScenarioState]──>  ThenFontPosition
```

- **GivenFontPosition**: sets up a `StubFigure` with font face and size
- **WhenFontPosition**: applies (or clears) superscript/subscript attributes
- **ThenFontPosition**: asserts font size via `getPositionedFont()` and baseline offset via `getFontBaselineOffset()` using AssertJ's fluent `assertThat(…).isCloseTo(…)`

## AssertJ Usage

AssertJ replaces the raw `assertEquals` / `assertNull` / `assertTrue` calls used in Lab 7:

```java
assertThat(result.getSize2D()).isCloseTo(expectedSize, within(0.001f));
assertThat(offset).isCloseTo(expectedOffset, within(0.001f));
assertThat(result).isNull();
assertThat(figure.get(AttributeKeys.FONT_SUPERSCRIPT)).isEqualTo(true);
```

## SOLID Discussion

| Principle | Application |
| --- | --- |
| Single Responsibility | Each stage class has one concern (setup, action, assertion). Each scenario tests one behaviour. |
| Open/Closed | New scenarios can be added by writing new test methods and (if needed) new stage methods without changing existing ones. |
| Liskov Substitution | `StubFigure` is substitutable for any `Figure` in the context of attribute lookups. |
| Interface Segregation | Stages depend only on `AttributeKeys` static methods, not on the full `Figure` interface. |
| Dependency Inversion | Scenarios depend on the abstract `AttributeKeys` API and the `Figure` interface, not on concrete implementations. |

## Clean Architecture Discussion

| Layer | Role |
| --- | --- |
| Core model | `AttributeKeys.getPositionedFont` and `getFontBaselineOffset` are exercised through the same public API used by renderers. |
| Test infrastructure | `StubFigure` stays in the core-model test package; no UI, drawing, or toolkit dependencies. |
| BDD layer | JGiven stage classes orchestrate state without leaking test infrastructure into scenario methods. |

## Verification

```sh
mvn -B -s .maven-settings.xml -pl jhotdraw-core test
```

All 21 tests pass (13 JUnit 4 + 2 TestNG + 6 JGiven BDD scenarios).
