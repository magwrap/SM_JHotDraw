package org.jhotdraw.draw.tool;

import java.awt.geom.Point2D;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.geom.BezierPath;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class PathToolNGTest {

    private PathTool tool;
    private BezierFigure figure;

    @org.testng.annotations.BeforeMethod
    public void setUp() {
        tool = new PathTool(new BezierFigure(false));
        figure = new BezierFigure(false);
        tool.createdFigure = figure;
        tool.editor = new DefaultDrawingEditor();
    }

    @Test
    public void testFirstClickCreatesFirstNode() {
        figure.addNode(new BezierPath.Node(10, 20));
        assertEquals(figure.getNodeCount(), 1);
        assertEquals(figure.getNode(0).getControlPoint(0), new Point2D.Double(10, 20));
    }

    @Test
    public void testSecondClickSetsC2OnLastNode() {
        figure.addNode(new BezierPath.Node(10, 20));
        tool.addPointToFigure(new Point2D.Double(30, 40));
        BezierPath.Node node0 = figure.getNode(0);
        assertEquals(node0.getControlPoint(2), new Point2D.Double(30, 40));
        assertTrue((node0.getMask() & BezierPath.C2_MASK) != 0);
    }

    @Test
    public void testFourthClickCreatesSecondNodeWithC1() {
        figure.addNode(new BezierPath.Node(10, 20));
        tool.addPointToFigure(new Point2D.Double(30, 40));
        tool.addPointToFigure(new Point2D.Double(50, 60));
        tool.addPointToFigure(new Point2D.Double(70, 80));
        BezierPath.Node node0 = figure.getNode(0);
        BezierPath.Node node1 = figure.getNode(1);
        assertEquals(node0.getControlPoint(0), new Point2D.Double(10, 20));
        assertEquals(node0.getControlPoint(2), new Point2D.Double(30, 40));
        assertTrue((node0.getMask() & BezierPath.C2_MASK) != 0);
        assertEquals(node1.getControlPoint(0), new Point2D.Double(70, 80));
        assertEquals(node1.getControlPoint(1), new Point2D.Double(50, 60));
        assertTrue((node1.getMask() & BezierPath.C1_MASK) != 0);
    }

    @Test
    public void testTwoFullCyclesProduceThreeNodes() {
        figure.addNode(new BezierPath.Node(10, 20));
        tool.addPointToFigure(new Point2D.Double(20, 30));
        tool.addPointToFigure(new Point2D.Double(30, 40));
        tool.addPointToFigure(new Point2D.Double(40, 50));
        tool.addPointToFigure(new Point2D.Double(50, 60));
        tool.addPointToFigure(new Point2D.Double(60, 70));
        tool.addPointToFigure(new Point2D.Double(70, 80));
        tool.addPointToFigure(new Point2D.Double(80, 90));
        assertEquals(figure.getNodeCount(), 3);
        BezierPath.Node node0 = figure.getNode(0);
        assertTrue((node0.getMask() & BezierPath.C2_MASK) != 0);
        assertEquals(node0.getControlPoint(2), new Point2D.Double(20, 30));
        BezierPath.Node node1 = figure.getNode(1);
        assertTrue((node1.getMask() & (BezierPath.C1_MASK | BezierPath.C2_MASK)) != 0);
        assertEquals(node1.getControlPoint(1), new Point2D.Double(30, 40));
        assertEquals(node1.getControlPoint(2), new Point2D.Double(50, 60));
        BezierPath.Node node2 = figure.getNode(2);
        assertEquals(node2.getControlPoint(1), new Point2D.Double(60, 70));
    }

    @Test
    public void testMiddleNodeGetsBothMasksAfterTwoCycles() {
        figure.addNode(new BezierPath.Node(10, 20));
        tool.addPointToFigure(new Point2D.Double(20, 30));
        tool.addPointToFigure(new Point2D.Double(30, 40));
        tool.addPointToFigure(new Point2D.Double(40, 50));
        tool.addPointToFigure(new Point2D.Double(50, 60));
        tool.addPointToFigure(new Point2D.Double(60, 70));
        tool.addPointToFigure(new Point2D.Double(70, 80));
        tool.addPointToFigure(new Point2D.Double(80, 90));
        BezierPath.Node node1 = figure.getNode(1);
        assertTrue((node1.getMask() & BezierPath.C1_MASK) != 0);
        assertTrue((node1.getMask() & BezierPath.C2_MASK) != 0);
    }
}
