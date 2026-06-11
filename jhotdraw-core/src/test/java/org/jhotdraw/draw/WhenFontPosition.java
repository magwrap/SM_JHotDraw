package org.jhotdraw.draw;

import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.Stage;

public class WhenFontPosition extends Stage<WhenFontPosition> {

    @ScenarioState
    StubFigure figure;

    public WhenFontPosition no_font_position_is_set() {
        figure.set(AttributeKeys.FONT_SUPERSCRIPT, false);
        figure.set(AttributeKeys.FONT_SUBSCRIPT, false);
        return this;
    }

    public WhenFontPosition superscript_is_applied() {
        figure.set(AttributeKeys.FONT_SUPERSCRIPT, true);
        figure.set(AttributeKeys.FONT_SUBSCRIPT, false);
        return this;
    }

    public WhenFontPosition subscript_is_applied() {
        figure.set(AttributeKeys.FONT_SUBSCRIPT, true);
        figure.set(AttributeKeys.FONT_SUPERSCRIPT, false);
        return this;
    }
}
