/*
 * Copyright (C) 2026 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import org.jhotdraw.draw.figure.AbstractFigure;
import static org.junit.Assert.*;
import org.junit.Test;

public class AttributeKeysFontPositionTest {

    @Test
    public void getPositionedFont_returnsSameFont_whenNoPositionSet() {
        StubFigure f = new StubFigure();
        f.set(AttributeKeys.FONT_FACE, new Font("Arial", Font.PLAIN, 12));
        f.set(AttributeKeys.FONT_SIZE, 12.0);
        f.set(AttributeKeys.FONT_SUPERSCRIPT, false);
        f.set(AttributeKeys.FONT_SUBSCRIPT, false);
        Font result = AttributeKeys.getPositionedFont(f);
        assertEquals(12.0, result.getSize2D(), 1e-9);
    }

    @Test
    public void getPositionedFont_scalesDown_whenSuperscript() {
        StubFigure f = new StubFigure();
        f.set(AttributeKeys.FONT_FACE, new Font("Arial", Font.PLAIN, 12));
        f.set(AttributeKeys.FONT_SIZE, 12.0);
        f.set(AttributeKeys.FONT_SUPERSCRIPT, true);
        f.set(AttributeKeys.FONT_SUBSCRIPT, false);
        Font result = AttributeKeys.getPositionedFont(f);
        assertEquals(8.4f, result.getSize2D(), 1e-6);
    }

    @Test
    public void getPositionedFont_scalesDown_whenSubscript() {
        StubFigure f = new StubFigure();
        f.set(AttributeKeys.FONT_FACE, new Font("Arial", Font.PLAIN, 12));
        f.set(AttributeKeys.FONT_SIZE, 12.0);
        f.set(AttributeKeys.FONT_SUPERSCRIPT, false);
        f.set(AttributeKeys.FONT_SUBSCRIPT, true);
        Font result = AttributeKeys.getPositionedFont(f);
        assertEquals(8.4f, result.getSize2D(), 1e-6);
    }

    @Test
    public void getPositionedFont_returnsNull_whenNullFontFace() {
        StubFigure f = new StubFigure();
        f.set(AttributeKeys.FONT_FACE, null);
        f.set(AttributeKeys.FONT_SIZE, 12.0);
        Font result = AttributeKeys.getPositionedFont(f);
        assertNull(result);
    }

    @Test
    public void getPositionedFont_handlesZeroFontSize() {
        StubFigure f = new StubFigure();
        f.set(AttributeKeys.FONT_FACE, new Font("Arial", Font.PLAIN, 0));
        f.set(AttributeKeys.FONT_SIZE, 0.0);
        f.set(AttributeKeys.FONT_SUPERSCRIPT, true);
        f.set(AttributeKeys.FONT_SUBSCRIPT, false);
        Font result = AttributeKeys.getPositionedFont(f);
        assertEquals(0.0, result.getSize2D(), 1e-9);
    }

    @Test
    public void getFontBaselineOffset_returnsZero_whenNoPositionSet() {
        StubFigure f = new StubFigure();
        f.set(AttributeKeys.FONT_SIZE, 12.0);
        f.set(AttributeKeys.FONT_SUPERSCRIPT, false);
        f.set(AttributeKeys.FONT_SUBSCRIPT, false);
        assertEquals(0.0f, AttributeKeys.getFontBaselineOffset(f), 1e-9);
    }

    @Test
    public void getFontBaselineOffset_returnsNegative_whenSuperscript() {
        StubFigure f = new StubFigure();
        f.set(AttributeKeys.FONT_SIZE, 12.0);
        f.set(AttributeKeys.FONT_SUPERSCRIPT, true);
        f.set(AttributeKeys.FONT_SUBSCRIPT, false);
        assertEquals(-12.0f * 0.35f, AttributeKeys.getFontBaselineOffset(f), 1e-9);
    }

    @Test
    public void getFontBaselineOffset_returnsPositive_whenSubscript() {
        StubFigure f = new StubFigure();
        f.set(AttributeKeys.FONT_SIZE, 12.0);
        f.set(AttributeKeys.FONT_SUPERSCRIPT, false);
        f.set(AttributeKeys.FONT_SUBSCRIPT, true);
        assertEquals(12.0f * 0.25f, AttributeKeys.getFontBaselineOffset(f), 1e-9);
    }

    @Test
    public void getFontBaselineOffset_returnsZero_whenZeroFontSize() {
        StubFigure f = new StubFigure();
        f.set(AttributeKeys.FONT_SIZE, 0.0);
        f.set(AttributeKeys.FONT_SUPERSCRIPT, true);
        f.set(AttributeKeys.FONT_SUBSCRIPT, false);
        assertEquals(0.0f, AttributeKeys.getFontBaselineOffset(f), 1e-9);
    }

    @Test
    public void supportedAttributes_containsFontSuperscript() {
        assertTrue(AttributeKeys.SUPPORTED_ATTRIBUTES.contains(AttributeKeys.FONT_SUPERSCRIPT));
    }

    @Test
    public void supportedAttributes_containsFontSubscript() {
        assertTrue(AttributeKeys.SUPPORTED_ATTRIBUTES.contains(AttributeKeys.FONT_SUBSCRIPT));
    }

    @Test
    public void fontSuperscript_defaultValue() {
        assertFalse(AttributeKeys.FONT_SUPERSCRIPT.getDefaultValue());
    }

    @Test
    public void fontSubscript_defaultValue() {
        assertFalse(AttributeKeys.FONT_SUBSCRIPT.getDefaultValue());
    }

    private static class StubFigure extends AbstractFigure {

        private final Map<AttributeKey<?>, Object> attributes = new HashMap<>();

        @Override
        public <T> T get(AttributeKey<T> key) {
            if (attributes.containsKey(key)) {
                @SuppressWarnings("unchecked")
                T value = (T) attributes.get(key);
                return value;
            }
            return key.getDefaultValue();
        }

        @Override
        public <T> void set(AttributeKey<T> key, T value) {
            attributes.put(key, value);
        }

        @Override
        public void draw(Graphics2D g) {
        }

        @Override
        public Rectangle2D.Double getBounds() {
            return null;
        }

        @Override
        public Rectangle2D.Double getDrawingArea() {
            return null;
        }

        @Override
        public boolean contains(Point2D.Double p) {
            return false;
        }

        @Override
        public Object getTransformRestoreData() {
            return null;
        }

        @Override
        public void restoreTransformTo(Object restoreData) {
        }

        @Override
        public void transform(AffineTransform tx) {
        }

        @Override
        public Map<AttributeKey<?>, Object> getAttributes() {
            return null;
        }

        @Override
        public Object getAttributesRestoreData() {
            return null;
        }

        @Override
        public void restoreAttributesTo(Object restoreData) {
        }

        @Override
        public Rectangle2D.Double getDrawingArea(double factor) {
            return null;
        }
    }
}
