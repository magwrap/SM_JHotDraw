package org.jhotdraw.draw.tool;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.geom.BezierPath;


public class PathTool extends BezierTool {

    private static final long serialVersionUID = 1L;

    private enum Phase { ANCHOR, C2, C1 }

    private Phase phase = Phase.C2;
    private Point2D.Double pendingC1;

    public PathTool(BezierFigure prototype) {
        super(prototype, null, null, false);
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
    }

    @Override
    protected void addPointToFigure(Point2D.Double newPoint) {
        switch (phase) {
            case C2:
                setLastNodeC2(newPoint);
                phase = Phase.C1;
                break;
            case C1:
                pendingC1 = newPoint;
                phase = Phase.ANCHOR;
                break;
            case ANCHOR:
                addNodeWithC1(newPoint);
                phase = Phase.C2;
                break;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        if (createdFigure == null || getView() == null) {
            return;
        }
        DrawingView view = getView();
        g.setStroke(new BasicStroke(1.5f));
        Font oldFont = g.getFont();
        g.setFont(oldFont.deriveFont(Font.BOLD, 12f));
        for (int i = 0; i < createdFigure.getNodeCount(); i++) {
            BezierPath.Node node = createdFigure.getNode(i);
            Point p0 = view.drawingToView(node.getControlPoint(0));
            g.setColor(Color.RED);
            g.fillRect(p0.x - 4, p0.y - 4, 8, 8);
            boolean hasC2 = (node.getMask() & BezierPath.C2_MASK) != 0;
            boolean hasC1 = (node.getMask() & BezierPath.C1_MASK) != 0;
            if (hasC2) {
                Point p2 = view.drawingToView(node.getControlPoint(2));
                g.setColor(new Color(0, 180, 0));
                g.drawLine(p0.x, p0.y, p2.x, p2.y);
                g.fillOval(p2.x - 5, p2.y - 5, 10, 10);
                g.drawString("C2", p2.x + 6, p2.y + 4);
            }
            if (hasC1) {
                Point p1 = view.drawingToView(node.getControlPoint(1));
                g.setColor(new Color(0, 180, 0));
                g.drawLine(p0.x, p0.y, p1.x, p1.y);
                g.fillRect(p1.x - 5, p1.y - 5, 10, 10);
                g.drawString("C1", p1.x + 6, p1.y + 4);
            }
        }
        if (phase == Phase.ANCHOR && pendingC1 != null) {
            Point pp = view.drawingToView(pendingC1);
            g.setColor(new Color(0, 180, 0));
            g.drawOval(pp.x - 6, pp.y - 6, 12, 12);
            g.drawLine(pp.x - 3, pp.y - 3, pp.x + 3, pp.y + 3);
            g.drawLine(pp.x - 3, pp.y + 3, pp.x + 3, pp.y - 3);
            g.drawString("C1", pp.x + 8, pp.y + 4);
        }
        g.setFont(oldFont);
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        super.mouseMoved(evt);
        if (createdFigure != null && getView() != null) {
            DrawingView v = getView();
            Rectangle r = new Rectangle(evt.getPoint());
            r.grow(4, 4);
            for (int i = 0; i < createdFigure.getNodeCount(); i++) {
                BezierPath.Node node = createdFigure.getNode(i);
                r.add(v.drawingToView(node.getControlPoint(0)));
                if ((node.getMask() & BezierPath.C2_MASK) != 0) {
                    r.add(v.drawingToView(node.getControlPoint(2)));
                }
                if ((node.getMask() & BezierPath.C1_MASK) != 0) {
                    r.add(v.drawingToView(node.getControlPoint(1)));
                }
            }
            if (phase == Phase.ANCHOR && pendingC1 != null) {
                r.add(v.drawingToView(pendingC1));
            }
            fireAreaInvalidated(r);
        }
    }

    private void setLastNodeC2(Point2D.Double p) {
        int idx = createdFigure.getNodeCount() - 1;
        createdFigure.willChange();
        BezierPath.Node oldNode = createdFigure.getNode(idx);
        Point2D.Double oldC2 = oldNode.getControlPoint(2);
        oldNode.setControlPoint(2, p);
        oldNode.setMask(oldNode.getMask() | BezierPath.C2_MASK);
        createdFigure.setNode(idx, oldNode);
        createdFigure.changed();
        if (getView() != null) {
            Rectangle r = new Rectangle(getView().drawingToView(oldC2));
            r.add(getView().drawingToView(p));
            r.grow(5, 5);
            fireAreaInvalidated(r);
        }
    }

    private void addNodeWithC1(Point2D.Double p) {
        createdFigure.willChange();
        BezierPath.Node newNode = new BezierPath.Node(p);
        newNode.setControlPoint(1, pendingC1);
        newNode.setMask(newNode.getMask() | BezierPath.C1_MASK);
        createdFigure.addNode(newNode);
        createdFigure.changed();
        if (getView() != null) {
            Rectangle r = new Rectangle(getView().drawingToView(newNode.getControlPoint(0)));
            r.add(getView().drawingToView(newNode.getControlPoint(1)));
            r.grow(5, 5);
            fireAreaInvalidated(r);
        }
    }
}
