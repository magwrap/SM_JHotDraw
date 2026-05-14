package org.jhotdraw.draw;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.Stage;
import java.awt.Font;

public class ThenFontPosition extends Stage<ThenFontPosition> {

    @ScenarioState
    StubFigure figure;

    public ThenFontPosition the_font_size_is(double expectedSize) {
        Font result = AttributeKeys.getPositionedFont(figure);
        assertThat(result.getSize2D()).isCloseTo((float) expectedSize, within(0.001f));
        return this;
    }

    public ThenFontPosition the_baseline_offset_is(float expectedOffset) {
        float offset = AttributeKeys.getFontBaselineOffset(figure);
        assertThat(offset).isCloseTo(expectedOffset, within(0.001f));
        return this;
    }

    public ThenFontPosition font_is_null() {
        Font result = AttributeKeys.getPositionedFont(figure);
        assertThat(result).isNull();
        return this;
    }

    public ThenFontPosition superscript_is(boolean expected) {
        assertThat(figure.get(AttributeKeys.FONT_SUPERSCRIPT)).isEqualTo(expected);
        return this;
    }

    public ThenFontPosition subscript_is(boolean expected) {
        assertThat(figure.get(AttributeKeys.FONT_SUBSCRIPT)).isEqualTo(expected);
        return this;
    }
}
