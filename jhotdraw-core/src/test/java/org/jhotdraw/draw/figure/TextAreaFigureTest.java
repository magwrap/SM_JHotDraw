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

import java.awt.geom.Point2D;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit tests for {@link TextAreaFigure}.
 */
public class TextAreaFigureTest {

    @Test
    public void setBoundsSamePointEnforcesMinimumSize() {
        TextAreaFigure figure = new TextAreaFigure();
        Point2D.Double anchor = new Point2D.Double(10.0, 20.0);
        Point2D.Double lead = new Point2D.Double(10.0, 20.0);

        figure.setBounds(anchor, lead);

        // Invariants: width and height must never be zero or negative
        assert figure.getBounds().width > 0 : "width must be positive";
        assert figure.getBounds().height > 0 : "height must be positive";

        assertEquals(1.0, figure.getBounds().width, 0.001);
        assertEquals(1.0, figure.getBounds().height, 0.001);
    }

    @Test
    public void getTextColumnsReturnsDefaultWhenTextIsNull() {
        TextAreaFigure figure = new TextAreaFigure(null);

        // Invariant: result must never be less than 4
        int result = figure.getTextColumns();
        assert result >= 4 : "text columns must never be less than 4";

        assertEquals(4, result);
    }
}
