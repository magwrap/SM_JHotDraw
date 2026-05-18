/*
 * Copyright (C) 2026 JHotDraw.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jhotdraw.draw.figure;

import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.jhotdraw.draw.AttributeKeys.Alignment;
import static org.jhotdraw.draw.AttributeKeys.TEXT_ALIGNMENT;
import org.junit.Test;

/**
 * JGiven BDD test for the alignment strategy selection refactoring.
 */
public class TextAreaFigureAlignmentJGivenTest
        extends SimpleScenarioTest<TextAreaFigureAlignmentJGivenTest.Stages> {

    @Test
    public void center_alignment_strategy_is_selected() {
        given().a_text_area_figure_with_$_alignment("CENTER");
        when().the_alignment_strategy_is_determined();
        then().the_strategy_should_be("CenterAlignment");
    }

    public static class Stages {
        @ExpectedScenarioState
        TextAreaFigure figure;

        @ProvidedScenarioState
        String strategyName;

        public void a_text_area_figure_with_$_alignment(String alignment) {
            figure = new TextAreaFigure();
            figure.set(TEXT_ALIGNMENT, Alignment.valueOf(alignment));
        }

        public void the_alignment_strategy_is_determined() {
            strategyName = figure.getTextAlignment().getClass().getSimpleName();
        }

        public void the_strategy_should_be(String expected) {
            org.junit.Assert.assertEquals(expected, strategyName);
        }
    }
}
