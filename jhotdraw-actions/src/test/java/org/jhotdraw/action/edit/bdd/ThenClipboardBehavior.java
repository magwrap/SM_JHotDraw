package org.jhotdraw.action.edit.bdd;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ThenClipboardBehavior extends Stage<ThenClipboardBehavior> {

    @ExpectedScenarioState
    JPanel drawingComponent;

    @ExpectedScenarioState
    TransferHandler mockTransferHandler;

    @ExpectedScenarioState
    Clipboard mockClipboard;

    @ExpectedScenarioState
    Transferable mockTransferable;

    public ThenClipboardBehavior the_figures_are_exported_to_clipboard() {
        verify(mockTransferHandler, times(1))
                .exportToClipboard(drawingComponent, mockClipboard, TransferHandler.COPY);
        return self();
    }

    public ThenClipboardBehavior the_export_uses_copy_action_type() {
        verify(mockTransferHandler)
                .exportToClipboard(any(), any(), org.mockito.ArgumentMatchers.eq(TransferHandler.COPY));
        assertThat(TransferHandler.COPY).isNotEqualTo(TransferHandler.MOVE);
        return self();
    }

    public ThenClipboardBehavior the_figures_are_imported_into_the_drawing() {
        verify(mockTransferHandler, times(1)).importData(drawingComponent, mockTransferable);
        assertThat(mockTransferable).isNotNull();
        return self();
    }

    public ThenClipboardBehavior nothing_is_imported() {
        verify(mockTransferHandler, never()).importData(any(), any());
        return self();
    }

    public ThenClipboardBehavior export_still_occurs_despite_component_being_disabled() {
        assertThat(drawingComponent.isEnabled()).isFalse();
        verify(mockTransferHandler, times(1))
                .exportToClipboard(drawingComponent, mockClipboard, TransferHandler.COPY);
        return self();
    }
}
