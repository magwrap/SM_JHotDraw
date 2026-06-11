package org.jhotdraw.draw;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.Stage;
import java.awt.Font;

public class GivenFontPosition extends Stage<GivenFontPosition> {

    @ProvidedScenarioState
    StubFigure figure;

    public GivenFontPosition a_figure_with_font_Arial_12() {
        figure = new StubFigure();
        figure.set(AttributeKeys.FONT_FACE, new Font("Arial", Font.PLAIN, 12));
        figure.set(AttributeKeys.FONT_SIZE, 12.0);
        return this;
    }

    public GivenFontPosition a_figure_with_null_font() {
        figure = new StubFigure();
        figure.set(AttributeKeys.FONT_FACE, null);
        figure.set(AttributeKeys.FONT_SIZE, 12.0);
        return this;
    }

    public GivenFontPosition a_figure_with_zero_font_size() {
        figure = new StubFigure();
        figure.set(AttributeKeys.FONT_FACE, new Font("Arial", Font.PLAIN, 12));
        figure.set(AttributeKeys.FONT_SIZE, 0.0);
        return this;
    }
}
