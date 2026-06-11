package org.jhotdraw.action.edit.bdd;

import com.tngtech.jgiven.junit.ScenarioTest;
import org.jhotdraw.datatransfer.ClipboardUtil;
import org.junit.After;
import org.junit.Test;

public class CopyPasteScenarioTest
        extends ScenarioTest<GivenDrawingState, WhenUserAction, ThenClipboardBehavior> {

    @After
    public void resetClipboard() {
        ClipboardUtil.setClipboard(null);
    }

    @Test
    public void copy_selected_figures_exports_them_to_clipboard() {
        given().a_drawing_component_with_figures_selected();
        when().the_user_invokes_copy();
        then().the_figures_are_exported_to_clipboard();
    }

    @Test
    public void copy_always_uses_the_COPY_action_type_not_MOVE() {
        given().a_drawing_component_with_figures_selected();
        when().the_user_invokes_copy();
        then().the_export_uses_copy_action_type();
    }

    @Test
    public void copy_from_a_disabled_component_still_exports() {
        given().a_disabled_drawing_component();
        when().the_user_invokes_copy();
        then().export_still_occurs_despite_component_being_disabled();
    }

    @Test
    public void paste_with_clipboard_content_imports_figures_into_drawing() {
        given().a_drawing_component_with_figures_selected()
               .and().the_clipboard_contains_figures();
        when().the_user_invokes_paste();
        then().the_figures_are_imported_into_the_drawing();
    }

    @Test
    public void paste_with_empty_clipboard_does_nothing() {
        given().a_drawing_component_with_figures_selected()
               .and().the_clipboard_is_empty();
        when().the_user_invokes_paste();
        then().nothing_is_imported();
    }

    @Test
    public void paste_into_a_disabled_component_is_blocked() {
        given().a_disabled_drawing_component()
               .and().the_clipboard_contains_figures();
        when().the_user_invokes_paste();
        then().nothing_is_imported();
    }
}
