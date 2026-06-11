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

    @Test
    public void testActionId_isEditPaste() {
        assert PasteAction.ID != null && !PasteAction.ID.isEmpty()
                : "PasteAction.ID must not be null or empty";
        assertEquals("edit.paste", PasteAction.ID);
    }

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

    @Test
    public void testActionPerformed_disabledTarget_doesNotImport() {
        targetPanel.setEnabled(false);
        PasteAction action = new PasteAction(targetPanel);
        ActionEvent event = new ActionEvent(targetPanel, ActionEvent.ACTION_PERFORMED, "paste");

        action.actionPerformed(event);

        verifyNoInteractions(mockTransferHandler);
    }

    @Test
    public void testActionPerformed_nullClipboardContent_doesNotImport() {
        when(mockClipboard.getContents(targetPanel)).thenReturn(null);
        PasteAction action = new PasteAction(targetPanel);
        ActionEvent event = new ActionEvent(targetPanel, ActionEvent.ACTION_PERFORMED, "paste");

        action.actionPerformed(event);

        verifyNoInteractions(mockTransferHandler);
        assert true : "paste with null transferable must not call importData";
    }

    @Test
    public void testActionPerformed_nullTargetAndNoFocusOwner_doesNotThrow() {
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
        PasteAction action = new PasteAction(null);
        boolean stateBefore = action.isEnabled();
        action.updateEnabled();
        assertEquals(stateBefore, action.isEnabled());
    }
}
