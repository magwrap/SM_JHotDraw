package org.jhotdraw.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class UndoRedoManagerTest {

    private UndoRedoManager manager;

    @Before
    public void setUp() {
        manager = new UndoRedoManager();
    }

    @Test
    public void testAddEdit_EnablesUndo() {
        assertFalse(manager.canUndo());
        UndoableEdit edit = new AbstractUndoableEdit() {};
        manager.addEdit(edit);
        assertTrue(manager.canUndo());
    }

    @Test
    public void testUndoThenRedo_TogglesAvailability() {
        UndoableEdit edit = new AbstractUndoableEdit() {};
        manager.addEdit(edit);
        assertTrue(manager.canUndo());
        assertFalse(manager.canRedo());
        manager.undo();
        assertFalse(manager.canUndo());
        assertTrue(manager.canRedo());
        manager.redo();
        assertTrue(manager.canUndo());
        assertFalse(manager.canRedo());
    }

    @Test(expected = CannotUndoException.class)
    public void testUndo_OnEmptyManager_ThrowsException() {
        manager.undo();
    }

    @Test
    public void testDiscardAllEdits_ResetsState() {
        UndoableEdit edit = new AbstractUndoableEdit() {};
        manager.addEdit(edit);
        manager.discardAllEdits();
        assertFalse(manager.canUndo());
        assertFalse(manager.canRedo());
        assertFalse(manager.hasSignificantEdits());
    }
}
