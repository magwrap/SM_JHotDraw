package org.jhotdraw.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import static org.assertj.core.api.Assertions.assertThat;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class UndoRedoStage extends Stage<UndoRedoStage> {

    @ProvidedScenarioState
    private UndoRedoManager manager;

    @ProvidedScenarioState
    private UndoableEdit edit1;

    @ProvidedScenarioState
    private UndoableEdit edit2;

    @ProvidedScenarioState
    private UndoableEdit edit3;

    public UndoRedoStage an_undo_redo_manager_with_one_edit() {
        manager = new UndoRedoManager();
        edit1 = new AbstractUndoableEdit() {};
        manager.addEdit(edit1);
        return self();
    }

    public UndoRedoStage an_undo_redo_manager_with_one_undone_edit() {
        an_undo_redo_manager_with_one_edit();
        manager.undo();
        return self();
    }

    public UndoRedoStage an_undo_redo_manager_with_multiple_edits() {
        manager = new UndoRedoManager();
        edit1 = new AbstractUndoableEdit() {};
        edit2 = new AbstractUndoableEdit() {};
        edit3 = new AbstractUndoableEdit() {};
        manager.addEdit(edit1);
        manager.addEdit(edit2);
        manager.addEdit(edit3);
        return self();
    }

    public UndoRedoStage an_undo_redo_manager_with_edits() {
        an_undo_redo_manager_with_multiple_edits();
        return self();
    }

    public UndoRedoStage undo_is_called() {
        manager.undo();
        return self();
    }

    public UndoRedoStage redo_is_called() {
        manager.redo();
        return self();
    }

    public UndoRedoStage discard_all_edits_is_called() {
        manager.discardAllEdits();
        return self();
    }

    public UndoRedoStage the_action_is_undone() {
        assertThat(manager.canUndo()).isFalse();
        return self();
    }

    public UndoRedoStage redo_is_available() {
        assertThat(manager.canRedo()).isTrue();
        return self();
    }

    public UndoRedoStage undo_is_available() {
        assertThat(manager.canUndo()).isTrue();
        return self();
    }

    public UndoRedoStage the_undo_redo_cycle_completes() {
        assertThat(manager.canUndo()).isTrue();
        assertThat(manager.canRedo()).isFalse();
        manager.undo();
        assertThat(manager.canUndo()).isFalse();
        assertThat(manager.canRedo()).isTrue();
        manager.redo();
        assertThat(manager.canUndo()).isTrue();
        assertThat(manager.canRedo()).isFalse();
        return self();
    }

    public UndoRedoStage all_edits_can_be_undone() {
        assertThat(manager.canUndo()).isTrue();
        manager.undo();
        assertThat(manager.canUndo()).isTrue();
        manager.undo();
        assertThat(manager.canUndo()).isTrue();
        manager.undo();
        assertThat(manager.canUndo()).isFalse();
        return self();
    }

    public UndoRedoStage all_edits_can_be_redone() {
        assertThat(manager.canRedo()).isTrue();
        manager.redo();
        assertThat(manager.canRedo()).isTrue();
        manager.redo();
        assertThat(manager.canRedo()).isTrue();
        manager.redo();
        assertThat(manager.canRedo()).isFalse();
        return self();
    }

    public UndoRedoStage state_is_reset() {
        assertThat(manager.canUndo()).isFalse();
        assertThat(manager.canRedo()).isFalse();
        assertThat(manager.hasSignificantEdits()).isFalse();
        return self();
    }
}
