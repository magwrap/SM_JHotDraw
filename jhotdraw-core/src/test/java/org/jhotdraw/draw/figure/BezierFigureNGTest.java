package org.jhotdraw.draw.figure;

import java.awt.geom.Point2D;
import org.jhotdraw.geom.BezierPath;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class BezierFigureNGTest {

    @Test
    public void testAddNodeIncreasesNodeCount() {
        BezierFigure f = new BezierFigure(false);
        assertEquals(f.getNodeCount(), 0);
        f.addNode(new BezierPath.Node(10, 20));
        assertEquals(f.getNodeCount(), 1);
        f.addNode(new BezierPath.Node(30, 40));
        assertEquals(f.getNodeCount(), 2);
    }

    @Test
    public void testGetNodeReturnsClone() {
        BezierFigure f = new BezierFigure(false);
        f.addNode(new BezierPath.Node(10, 20));
        BezierPath.Node n1 = f.getNode(0);
        BezierPath.Node n2 = f.getNode(0);
        assertNotSame(n1, n2);
        assertEquals(n1.getControlPoint(0), n2.getControlPoint(0));
    }

    @Test
    public void testSetNodePersistsChanges() {
        BezierFigure f = new BezierFigure(false);
        f.addNode(new BezierPath.Node(10, 20));
        BezierPath.Node n = f.getNode(0);
        n.setControlPoint(2, new Point2D.Double(100, 200));
        n.setMask(n.getMask() | BezierPath.C2_MASK);
        f.setNode(0, n);
        BezierPath.Node r = f.getNode(0);
        assertEquals(r.getControlPoint(2), new Point2D.Double(100, 200));
        assertTrue((r.getMask() & BezierPath.C2_MASK) != 0);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testGetNodeOnEmptyFigureThrows() {
        BezierFigure f = new BezierFigure(false);
        f.getNode(0);
    }

    @Test
    public void testSetPointByIndex() {
        BezierFigure f = new BezierFigure(false);
        f.addNode(new BezierPath.Node(10, 20));
        f.setPoint(0, 2, new Point2D.Double(50, 60));
        Point2D.Double p = f.getPoint(0, 2);
        assertEquals(p, new Point2D.Double(50, 60));
    }

    @Test
    public void testGetNodeMaskPreservedThroughSetNode() {
        BezierFigure f = new BezierFigure(false);
        f.addNode(new BezierPath.Node(10, 20));
        BezierPath.Node n = f.getNode(0);
        n.setMask(BezierPath.C1_MASK | BezierPath.C2_MASK);
        f.setNode(0, n);
        BezierPath.Node r = f.getNode(0);
        assertEquals(r.getMask(), BezierPath.C1_MASK | BezierPath.C2_MASK);
    }

    @Test
    public void testNodeConstructorCopiesCoordinates() {
        BezierPath.Node n = new BezierPath.Node(10, 20);
        assertEquals(n.getControlPoint(0), new Point2D.Double(10, 20));
        assertEquals(n.getControlPoint(1), new Point2D.Double(10, 20));
        assertEquals(n.getControlPoint(2), new Point2D.Double(10, 20));
    }

    @Test
    public void testSetControlPointModifiesSingleCoordinate() {
        BezierPath.Node n = new BezierPath.Node(10, 20);
        n.setControlPoint(1, new Point2D.Double(15, 25));
        assertEquals(n.getControlPoint(0), new Point2D.Double(10, 20));
        assertEquals(n.getControlPoint(1), new Point2D.Double(15, 25));
        assertEquals(n.getControlPoint(2), new Point2D.Double(10, 20));
    }

    @Test
    public void testMaskFlags() {
        BezierPath.Node n = new BezierPath.Node(10, 20);
        assertEquals(n.getMask(), 0);
        n.setMask(n.getMask() | BezierPath.C2_MASK);
        assertTrue((n.getMask() & BezierPath.C2_MASK) != 0);
        assertFalse((n.getMask() & BezierPath.C1_MASK) != 0);
        n.setMask(n.getMask() | BezierPath.C1_MASK);
        assertTrue((n.getMask() & BezierPath.C1_MASK) != 0);
        assertTrue((n.getMask() & BezierPath.C2_MASK) != 0);
    }

    @Test
    public void testFigureSetPointMovesAllControlPointsWhenCoord0() {
        BezierFigure f = new BezierFigure(false);
        Point2D.Double c0 = new Point2D.Double(10, 20);
        f.addNode(new BezierPath.Node(c0));
        f.setPoint(0, new Point2D.Double(30, 40));
        assertEquals(f.getPoint(0, 0), new Point2D.Double(30, 40));
        assertEquals(f.getPoint(0, 1), new Point2D.Double(30, 40));
        assertEquals(f.getPoint(0, 2), new Point2D.Double(30, 40));
    }

    @Test
    public void testRemoveNodeDecreasesCount() {
        BezierFigure f = new BezierFigure(false);
        f.addNode(new BezierPath.Node(1, 2));
        f.addNode(new BezierPath.Node(3, 4));
        assertEquals(f.getNodeCount(), 2);
        f.removeNode(0);
        assertEquals(f.getNodeCount(), 1);
        assertEquals(f.getNode(0).getControlPoint(0), new Point2D.Double(3, 4));
    }
}
