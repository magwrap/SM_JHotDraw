package org.jhotdraw.undo;

import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

public class UndoRedoScenarioTest extends SimpleScenarioTest<UndoRedoStage> {

    @Test
    public void user_can_undo_the_latest_action() {
        given().an_undo_redo_manager_with_one_edit();
        when().undo_is_called();
        then().the_action_is_undone()
            .and().redo_is_available();
    }

    @Test
    public void user_can_redo_an_undone_action() {
        given().an_undo_redo_manager_with_one_undone_edit();
        when().redo_is_called();
        then().undo_is_available()
            .and().the_undo_redo_cycle_completes();
    }

    @Test
    public void user_can_undo_multiple_actions() {
        given().an_undo_redo_manager_with_multiple_edits();
        when().undo_is_called();
        then().all_edits_can_be_undone()
            .and().all_edits_can_be_redone();
    }

    @Test
    public void manager_resets_state_after_discard() {
        given().an_undo_redo_manager_with_edits();
        when().discard_all_edits_is_called();
        then().state_is_reset();
    }
}
