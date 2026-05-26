package org.jhotdraw.action.edit;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import org.jhotdraw.api.gui.EditableComponent;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for AbstractSelectionAction. Cover the enable/disable logic for the
 * copy/paste/cut family of editing actions.
 */
public class AbstractSelectionActionTest {

    /** Minimal concrete subclass. */
    private static class ConcreteSelectionAction extends AbstractSelectionAction {
        ConcreteSelectionAction(javax.swing.JComponent target) {
            super(target);
        }
        @Override
        public void actionPerformed(ActionEvent evt) { /* no-op */ }
    }

    /** JPanel that also implements EditableComponent for controlled selection-state tests. */
    private static class EditablePanel extends JPanel implements EditableComponent {
        private final boolean selectionEmpty;
        EditablePanel(boolean selectionEmpty) { this.selectionEmpty = selectionEmpty; }
        @Override public void delete() {}
        @Override public void duplicate() {}
        @Override public void selectAll() {}
        @Override public void clearSelection() {}
        @Override public boolean isSelectionEmpty() { return selectionEmpty; }
        @Override public void addPropertyChangeListener(PropertyChangeListener l) { super.addPropertyChangeListener(l); }
        @Override public void removePropertyChangeListener(PropertyChangeListener l) { super.removePropertyChangeListener(l); }
    }

    @Before
    public void setUp() {
        System.setProperty("java.awt.headless", "true");
    }

    // --- best-case scenarios ---

    @Test
    public void testUpdateEnabled_enabledNonEditableTarget_enablesAction() {
        JPanel panel = new JPanel();
        panel.setEnabled(true);
        ConcreteSelectionAction action = new ConcreteSelectionAction(panel);
        action.updateEnabled();
        assertTrue(action.isEnabled());
    }

    @Test
    public void testUpdateEnabled_editableComponentNonEmptySelection_enablesAction() {
        EditablePanel panel = new EditablePanel(false); // non-empty selection
        panel.setEnabled(true);
        ConcreteSelectionAction action = new ConcreteSelectionAction(panel);
        action.updateEnabled();
        assertTrue(action.isEnabled());
    }

    // --- boundary cases ---

    @Test
    public void testUpdateEnabled_disabledTarget_disablesAction() {
        JPanel panel = new JPanel();
        panel.setEnabled(false);
        ConcreteSelectionAction action = new ConcreteSelectionAction(panel);
        action.updateEnabled();
        assertFalse(action.isEnabled());
    }

    @Test
    public void testUpdateEnabled_editableComponentEmptySelection_disablesAction() {
        EditablePanel panel = new EditablePanel(true); // empty selection
        panel.setEnabled(true);
        ConcreteSelectionAction action = new ConcreteSelectionAction(panel);
        action.updateEnabled();
        assertFalse(action.isEnabled());
    }

    @Test
    public void testUpdateEnabled_nullTarget_doesNotThrow() {
        // Boundary: null target, updateEnabled() is documented as a no-op
        ConcreteSelectionAction action = new ConcreteSelectionAction(null);
        action.updateEnabled(); // must not throw
    }

    // --- invariant assertions ---

    @Test
    public void testConstructor_nonNullTarget_setsTargetField() {
        JPanel panel = new JPanel();
        ConcreteSelectionAction action = new ConcreteSelectionAction(panel);
        // Java assertion: target invariant (active when JVM is run with -ea)
        assert action.target != null : "target must not be null after construction with non-null argument";
        assertSame(panel, action.target);
    }

    @Test
    public void testConstructor_nullTarget_targetFieldIsNull() {
        ConcreteSelectionAction action = new ConcreteSelectionAction(null);
        assert action.target == null : "target must be null when constructed with null";
        assertNull(action.target);
    }
}
