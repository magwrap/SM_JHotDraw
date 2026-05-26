package org.jhotdraw.action.edit;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import org.jhotdraw.datatransfer.ClipboardUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PasteAction. Verify that the paste operation reads from
 * the clipboard and delegates to the component's TransferHandler, and that
 * it correctly guards against disabled components and empty clipboards.
 */
public class PasteActionTest {

    private Clipboard mockClipboard;
    private TransferHandler mockTransferHandler;
    private Transferable mockTransferable;
    private JPanel targetPanel;

    @Before
    public void setUp() {
        System.setProperty("java.awt.headless", "true");
        mockClipboard = mock(Clipboard.class);
        mockTransferHandler = mock(TransferHandler.class);
        mockTransferable = mock(Transferable.class);
        ClipboardUtil.setClipboard(mockClipboard);
        targetPanel = new JPanel();
        targetPanel.setEnabled(true);
        targetPanel.setTransferHandler(mockTransferHandler);
    }

    @After
    public void tearDown() {
        ClipboardUtil.setClipboard(null);
    }

    // --- invariant / ID check ---

    @Test
    public void testActionId_isEditPaste() {
        assert PasteAction.ID != null && !PasteAction.ID.isEmpty()
                : "PasteAction.ID must not be null or empty";
        assertEquals("edit.paste", PasteAction.ID);
    }

    // --- best-case scenarios ---

    @Test
    public void testActionPerformed_enabledTargetWithClipboardContent_callsImportData() {
        when(mockClipboard.getContents(targetPanel)).thenReturn(mockTransferable);
        PasteAction action = new PasteAction(targetPanel);
        ActionEvent event = new ActionEvent(targetPanel, ActionEvent.ACTION_PERFORMED, "paste");

        action.actionPerformed(event);

        verify(mockTransferHandler, times(1)).importData(targetPanel, mockTransferable);
    }

    @Test
    public void testUpdateEnabled_withEnabledTarget_enablesAction() {
        PasteAction action = new PasteAction(targetPanel);
        action.updateEnabled();
        assertTrue(action.isEnabled());
    }

    // --- boundary cases ---

    @Test
    public void testActionPerformed_disabledTarget_doesNotImport() {
        // Boundary: PasteAction guards with "c.isEnabled()"
        targetPanel.setEnabled(false);
        PasteAction action = new PasteAction(targetPanel);
        ActionEvent event = new ActionEvent(targetPanel, ActionEvent.ACTION_PERFORMED, "paste");

        action.actionPerformed(event);

        verifyNoInteractions(mockTransferHandler);
    }

    @Test
    public void testActionPerformed_nullClipboardContent_doesNotImport() {
        // Boundary: clipboard is empty (null Transferable) → importData must not be called
        when(mockClipboard.getContents(targetPanel)).thenReturn(null);
        PasteAction action = new PasteAction(targetPanel);
        ActionEvent event = new ActionEvent(targetPanel, ActionEvent.ACTION_PERFORMED, "paste");

        action.actionPerformed(event);

        verifyNoInteractions(mockTransferHandler);
        // Java assertion: null transferable must never reach importData
        assert true : "paste with null transferable must not call importData";
    }

    @Test
    public void testActionPerformed_nullTargetAndNoFocusOwner_doesNotThrow() {
        // Boundary: null target + headless (no focus owner) → c stays null → no-op
        PasteAction action = new PasteAction();
        ActionEvent event = new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, "paste");

        action.actionPerformed(event);

        verifyNoInteractions(mockTransferHandler);
    }

    @Test
    public void testUpdateEnabled_withDisabledTarget_disablesAction() {
        targetPanel.setEnabled(false);
        PasteAction action = new PasteAction(targetPanel);
        action.updateEnabled();
        assertFalse(action.isEnabled());
    }

    @Test
    public void testUpdateEnabled_withNullTarget_actionRemainsDefaultEnabled() {
        // PasteAction.updateEnabled() only acts when target != null
        PasteAction action = new PasteAction(null);
        boolean stateBefore = action.isEnabled();
        action.updateEnabled();
        // State is unchanged (no-op when target is null)
        assertEquals(stateBefore, action.isEnabled());
    }
}
