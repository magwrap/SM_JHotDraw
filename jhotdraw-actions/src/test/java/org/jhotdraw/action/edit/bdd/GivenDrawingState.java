package org.jhotdraw.action.edit.bdd;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import org.jhotdraw.datatransfer.ClipboardUtil;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

/**
 * JGiven Given-stage: sets up the drawing component and clipboard state
 * for copy/paste BDD scenarios.
 */
public class GivenDrawingState extends Stage<GivenDrawingState> {

    @ProvidedScenarioState
    JPanel drawingComponent;

    @ProvidedScenarioState
    TransferHandler mockTransferHandler;

    @ProvidedScenarioState
    Clipboard mockClipboard;

    @ProvidedScenarioState
    Transferable mockTransferable;

    public GivenDrawingState a_drawing_component_with_figures_selected() {
        System.setProperty("java.awt.headless", "true");
        mockClipboard = mock(Clipboard.class);
        ClipboardUtil.setClipboard(mockClipboard);
        mockTransferHandler = mock(TransferHandler.class);
        drawingComponent = new JPanel();
        drawingComponent.setEnabled(true);
        drawingComponent.setTransferHandler(mockTransferHandler);
        return self();
    }

    public GivenDrawingState a_disabled_drawing_component() {
        a_drawing_component_with_figures_selected();
        drawingComponent.setEnabled(false);
        return self();
    }

    public GivenDrawingState the_clipboard_contains_figures() {
        mockTransferable = mock(Transferable.class);
        Mockito.when(mockClipboard.getContents(drawingComponent)).thenReturn(mockTransferable);
        return self();
    }

    public GivenDrawingState the_clipboard_is_empty() {
        Mockito.when(mockClipboard.getContents(any())).thenReturn(null);
        return self();
    }
}
