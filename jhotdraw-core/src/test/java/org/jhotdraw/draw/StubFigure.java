package org.jhotdraw.draw;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import org.jhotdraw.draw.figure.AbstractFigure;

public class StubFigure extends AbstractFigure {

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
