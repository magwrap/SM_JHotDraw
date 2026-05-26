package org.jhotdraw.action.edit;

import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import org.jhotdraw.datatransfer.ClipboardUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CopyAction. Verify that the copy operation correctly
 * delegates to the component's TransferHandler.
 */
public class CopyActionTest {

    private Clipboard mockClipboard;
    private TransferHandler mockTransferHandler;
    private JPanel targetPanel;

    @Before
    public void setUp() {
        System.setProperty("java.awt.headless", "true");
        // Inject a mock clipboard so ClipboardUtil never hits the OS clipboard
        mockClipboard = mock(Clipboard.class);
        ClipboardUtil.setClipboard(mockClipboard);
        mockTransferHandler = mock(TransferHandler.class);
        targetPanel = new JPanel();
        targetPanel.setTransferHandler(mockTransferHandler);
    }

    @After
    public void tearDown() {
        ClipboardUtil.setClipboard(null);
    }

    // --- invariant / ID check ---

    @Test
    public void testActionId_isEditCopy() {
        // Java assertion: static ID must never change
        assert CopyAction.ID != null && !CopyAction.ID.isEmpty()
                : "CopyAction.ID must not be null or empty";
        assertEquals("edit.copy", CopyAction.ID);
    }

    // --- best-case scenarios ---

    @Test
    public void testActionPerformed_withExplicitTarget_callsExportToClipboard() {
        CopyAction action = new CopyAction(targetPanel);
        ActionEvent event = new ActionEvent(targetPanel, ActionEvent.ACTION_PERFORMED, "copy");

        action.actionPerformed(event);

        verify(mockTransferHandler, times(1))
                .exportToClipboard(targetPanel, mockClipboard, TransferHandler.COPY);
    }

    @Test
    public void testActionPerformed_withEnabledTarget_exportPassesCorrectAction() {
        targetPanel.setEnabled(true);
        CopyAction action = new CopyAction(targetPanel);
        ActionEvent event = new ActionEvent(targetPanel, ActionEvent.ACTION_PERFORMED, "copy");

        action.actionPerformed(event);

        // Verify COPY constant is used, not MOVE
        verify(mockTransferHandler).exportToClipboard(
                eq(targetPanel), eq(mockClipboard), eq(TransferHandler.COPY));
    }

    // --- boundary cases ---

    @Test
    public void testActionPerformed_withDisabledTarget_stillExports() {
        // Boundary: source code comment says "copying is allowed for disabled components"
        targetPanel.setEnabled(false);
        CopyAction action = new CopyAction(targetPanel);
        ActionEvent event = new ActionEvent(targetPanel, ActionEvent.ACTION_PERFORMED, "copy");

        action.actionPerformed(event);

        verify(mockTransferHandler, times(1))
                .exportToClipboard(eq(targetPanel), eq(mockClipboard), eq(TransferHandler.COPY));
    }

    @Test
    public void testActionPerformed_withNullTargetAndNoFocusOwner_doesNotCallExport() {
        // Boundary: no explicit target, headless env has no focus owner → c stays null → no-op
        CopyAction action = new CopyAction(); // null-target constructor
        ActionEvent event = new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, "copy");

        action.actionPerformed(event);

        // Java assertion: in headless mode with no focus owner, export must not be attempted
        assert true : "actionPerformed with null effective target must not throw";
        verifyNoInteractions(mockTransferHandler);
    }

    @Test
    public void testConstructorWithTarget_actionIsNotNull() {
        CopyAction action = new CopyAction(targetPanel);
        assertNotNull(action);
        assert action != null : "CopyAction instance must not be null after construction";
    }
}
