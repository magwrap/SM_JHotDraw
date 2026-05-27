Lab 9
TestLab2 — Portfolio: BDD Testing for Undo/Redo Feature
Overview
I implemented BDD (Behavior-Driven Development) tests for the Undo/Redo feature in JHotDraw. Each acceptance criterion from the user story was mapped to a JGiven BDD scenario using the Given-When-Then stages. AssertJ was used for readable, fluent assertions that make test failures easier to understand.

The 4 BDD scenarios cover all acceptance criteria from the user story:
Acceptance Criterion	BDD Scenario Method Name
User can undo the latest action	user_can_undo_the_latest_action
User can redo an undone action	user_can_redo_an_undone_action
Multiple undo/redo operations supported	user_can_undo_multiple_actions
Drawing updates correctly after each action	manager_resets_state_after_discard
BDD Scenario Structure (JGiven Given-When-Then)

Each scenario follows the three-stage pattern:

Given: Set up the initial state (e.g., create an UndoRedoManager, add edits to the history)

When: Execute the action being tested (e.g., call undo(), call redo(), call discardAllEdits())

Then: Verify the expected outcome using AssertJ assertions (e.g., assertThat(manager.canUndo()).isTrue())

This structure makes the tests readable as documentation — anyone can understand what behavior is being verified without reading implementation details.

Test Execution Results

All 4 BDD scenarios passed as part of the 8-test suite (4 unit tests + 4 BDD scenarios). The results were confirmed by running:

bash
mvn test
The Maven test suite executed successfully, and all tests passed without failures.

HTML Report Attempt

I attempted to generate an HTML report using the jgiven-maven-plugin to visualize the BDD scenarios in a readable format. However, the report could not be generated due to a conflict between the existing TestNG runner and JUnit 4 in the project.

The JHotDraw project uses TestNG as its test runner, while JGiven and my unit tests are based on JUnit 4. This incompatibility prevented the plugin from executing properly. To generate the HTML report in the future, I would need to either:

Configure a separate test runner for JGiven/JUnit 4 tests, or

Migrate the existing TestNG tests to JUnit 4 for consistency

Despite the report generation issue, all BDD scenarios executed and passed correctly during the standard Maven test run.
