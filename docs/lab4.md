# Lab 4

## Refactoring Lab

Selected change request: add subscript and superscript formatting to the font palette.

The refactoring was performed before implementing the new subfeatures. The purpose was to remove duplication in the existing font palette code so that adding more font-style controls later will require less copied code.

## Code Smell

Code smell: duplicated code.

Location:

`jhotdraw-gui/src/main/java/org/jhotdraw/gui/action/ButtonFactory.java`

Affected methods:

| Method | Responsibility |
| --- | --- |
| `createFontStyleBoldButton` | Creates the toolbar button that toggles the `FONT_BOLD` attribute. |
| `createFontStyleItalicButton` | Creates the toolbar button that toggles the `FONT_ITALIC` attribute. |
| `createFontStyleUnderlineButton` | Creates the toolbar button that toggles the `FONT_UNDERLINE` attribute. |

These methods had the same structure:

| Repeated step | Variation between methods |
| --- | --- |
| Create a `JButton`. | None. |
| Configure the toolbar button from resource labels. | Label key: bold, italic, or underline. |
| Disable focus on the button. | None. |
| Create an `AttributeToggler`. | Attribute key: `FONT_BOLD`, `FONT_ITALIC`, or `FONT_UNDERLINE`. |
| Set the undo presentation name. | Label text key: bold, italic, or underline. |
| Add the action listener. | None. |
| Return the button. | None. |

This smell is relevant to the selected change request because subscript and superscript would naturally become two more font-style controls. Without refactoring, adding them would likely duplicate the same button-construction code two more times.

## Planned Change

The external behavior should not change. The public factory methods should still exist and callers should continue using the same API.

The internal structure should change by moving the shared font-style button construction into one private helper method. The public bold, italic, and underline methods should delegate to the helper and pass only the values that vary.

## Refactoring Strategy

The strategy was to make the smallest behavior-preserving change that removes the duplication.

Steps:

| Step | Description | Reasoning |
| --- | --- | --- |
| 1 | Identify the repeated code in the three font-style button methods. | The duplicate structure was clear and local to one class. |
| 2 | Identify the varying values: attribute key, label key, and compatible text action. | These values become parameters of the extracted helper. |
| 3 | Extract the shared construction into `createFontStyleButton`. | This centralizes button creation and reduces future duplication. |
| 4 | Change the existing public methods to delegate to the helper. | This preserves the public API and keeps callers unchanged. |
| 5 | Avoid behavioral fixes in the refactoring commit. | Refactoring should preserve behavior; bug fixes should be separate. |

## Applied Refactorings

| Refactoring | Source | How it was applied |
| --- | --- | --- |
| Extract Method | Fowler | The repeated button setup was extracted into the private helper `createFontStyleButton`. |
| Parameterize Method | Fowler | The helper receives the changing values as parameters: `AttributeKey<Boolean> key`, `String labelKey`, and `Action compatibleTextAction`. |

## Reasoning

The duplicated code made the font palette harder to extend. Each new style button would require copying the same construction sequence and changing only a few values. That increases the chance of inconsistent setup, wrong labels, or missing undo metadata.

The extracted helper makes the common workflow explicit:

1. Create the button.
2. Configure it from labels.
3. Create the attribute toggling action.
4. Configure the undo name.
5. Attach the action to the button.

After the refactoring, adding a future subscript or superscript button can reuse the same helper instead of duplicating the full setup sequence.

## Behavior Preservation

The refactoring intentionally keeps the existing public methods:

| Public method | Preserved behavior |
| --- | --- |
| `createFontStyleBoldButton` | Still creates a bold font-style button using `FONT_BOLD`. |
| `createFontStyleItalicButton` | Still creates an italic font-style button using `FONT_ITALIC`. |
| `createFontStyleUnderlineButton` | Still creates an underline font-style button using `FONT_UNDERLINE`. |

The refactoring also intentionally keeps the existing compatible text actions unchanged. The italic and underline methods already used `StyledEditorKit.BoldAction`. That looks suspicious, but changing it would be a behavior change or bug fix, not a pure refactoring. It should be handled separately if needed.

## Purpose

The purpose of this refactoring is prefactoring: improving the local structure before adding the requested subscript and superscript feature. It reduces duplicated code and makes the next change smaller, clearer, and less risky.
