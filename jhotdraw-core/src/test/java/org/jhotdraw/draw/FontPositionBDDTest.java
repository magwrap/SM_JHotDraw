package org.jhotdraw.draw;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

public class FontPositionBDDTest extends ScenarioTest<GivenFontPosition, WhenFontPosition, ThenFontPosition> {

    @Test
    @Description("Normal text renders with original font size and zero baseline offset")
    public void normal_text_when_no_position_set() {
        given().a_figure_with_font_Arial_12();
        when().no_font_position_is_set();
        then().the_font_size_is(12.0)
            .and().the_baseline_offset_is(0.0f);
    }

    @Test
    @Description("Superscript scales font down and shifts baseline upward")
    public void superscript_scales_font_down() {
        given().a_figure_with_font_Arial_12();
        when().superscript_is_applied();
        then().the_font_size_is(12.0 * 0.7)
            .and().the_baseline_offset_is(-12.0f * 0.35f);
    }

    @Test
    @Description("Subscript scales font down and shifts baseline downward")
    public void subscript_scales_font_down() {
        given().a_figure_with_font_Arial_12();
        when().subscript_is_applied();
        then().the_font_size_is(12.0 * 0.7)
            .and().the_baseline_offset_is(12.0f * 0.25f);
    }

    @Test
    @Description("Superscript and subscript are mutually exclusive")
    public void superscript_and_subscript_are_mutually_exclusive() {
        given().a_figure_with_font_Arial_12();

        when().superscript_is_applied();
        then().superscript_is(true)
            .and().subscript_is(false);

        when().subscript_is_applied();
        then().superscript_is(false)
            .and().subscript_is(true);
    }

    @Test
    @Description("Null font face returns null font from getPositionedFont")
    public void null_font_face_returns_null() {
        given().a_figure_with_null_font();
        when().no_font_position_is_set();
        then().font_is_null();
    }

    @Test
    @Description("Zero font size is handled without exception")
    public void zero_font_size_is_handled() {
        given().a_figure_with_zero_font_size();
        when().superscript_is_applied();
        then().the_font_size_is(0.0);
    }
}
