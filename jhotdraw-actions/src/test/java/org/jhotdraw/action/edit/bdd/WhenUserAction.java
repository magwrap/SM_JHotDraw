package org.jhotdraw.action.edit.bdd;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import org.jhotdraw.action.edit.CopyAction;
import org.jhotdraw.action.edit.PasteAction;

public class WhenUserAction extends Stage<WhenUserAction> {

    @ExpectedScenarioState
    JPanel drawingComponent;

    public WhenUserAction the_user_invokes_copy() {
        CopyAction action = new CopyAction(drawingComponent);
        ActionEvent event = new ActionEvent(drawingComponent, ActionEvent.ACTION_PERFORMED, "copy");
        action.actionPerformed(event);
        return self();
    }

    public WhenUserAction the_user_invokes_paste() {
        PasteAction action = new PasteAction(drawingComponent);
        ActionEvent event = new ActionEvent(drawingComponent, ActionEvent.ACTION_PERFORMED, "paste");
        action.actionPerformed(event);
        return self();
    }
}
