package org.jhotdraw.draw.figure;

import java.awt.geom.Point2D;
import org.jhotdraw.geom.BezierPath;
import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Test;

public class BezierFigureBDDTest {

    // --- Scenario: Adding nodes increases node count ---
    // Given a new figure
    // When a node is added at (10, 20)
    // And another node is added at (30, 40)
    // Then the figure has node count 2
    @Test
    public void adding_nodes_increases_node_count() {
        // Given
        BezierFigure fig = new BezierFigure(false);

        // When
        fig.addNode(new BezierPath.Node(10, 20));
        fig.addNode(new BezierPath.Node(30, 40));

        // Then
        assertThat(fig.getNodeCount()).isEqualTo(2);
    }

    // --- Scenario: Adding and removing nodes ---
    // Given a figure with two nodes
    // When the first node is removed
    // Then the figure has node count 1
    // And the remaining node is at (30, 40)
    @Test
    public void adding_and_removing_nodes() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        fig.addNode(new BezierPath.Node(10, 20));
        fig.addNode(new BezierPath.Node(30, 40));

        // When
        fig.removeNode(0);

        // Then
        assertThat(fig.getNodeCount()).isEqualTo(1);
        assertThat(fig.getNode(0).getControlPoint(0))
                .isEqualTo(new Point2D.Double(30, 40));
    }

    // --- Scenario: Setting C2 control point ---
    // Given a figure with one node at (10, 20)
    // When the C2 control point is set to (100, 200)
    // Then the C2 control point is at (100, 200)
    // And the C2 mask is set
    @Test
    public void setting_c2_control_point() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        fig.addNode(new BezierPath.Node(10, 20));

        // When
        int idx = fig.getNodeCount() - 1;
        BezierPath.Node node = fig.getNode(idx);
        node.setControlPoint(2, new Point2D.Double(100, 200));
        node.setMask(node.getMask() | BezierPath.C2_MASK);
        fig.setNode(idx, node);

        // Then
        assertThat(fig.getNode(idx).getControlPoint(2))
                .isEqualTo(new Point2D.Double(100, 200));
        assertThat(fig.getNode(idx).getMask() & BezierPath.C2_MASK)
                .isNotZero();
    }

    // --- Scenario: Setting C1 control point ---
    // Given a figure with one node at (10, 20)
    // When the C1 control point is set to (50, 60)
    // Then the C1 control point is at (50, 60)
    // And the C1 mask is set
    @Test
    public void setting_c1_control_point() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        fig.addNode(new BezierPath.Node(10, 20));

        // When
        int idx = fig.getNodeCount() - 1;
        BezierPath.Node node = fig.getNode(idx);
        node.setControlPoint(1, new Point2D.Double(50, 60));
        node.setMask(node.getMask() | BezierPath.C1_MASK);
        fig.setNode(idx, node);

        // Then
        assertThat(fig.getNode(idx).getControlPoint(1))
                .isEqualTo(new Point2D.Double(50, 60));
        assertThat(fig.getNode(idx).getMask() & BezierPath.C1_MASK)
                .isNotZero();
    }

    // --- Scenario: Node control points are independent ---
    // Given a figure with one node at (10, 20)
    // When the C1 control point is set to (50, 60)
    // Then C0 remains at (10, 20)
    // And C2 remains at (10, 20)
    @Test
    public void node_control_points_are_independent() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        fig.addNode(new BezierPath.Node(10, 20));

        // When
        int idx = 0;
        BezierPath.Node node = fig.getNode(idx);
        node.setControlPoint(1, new Point2D.Double(50, 60));
        node.setMask(node.getMask() | BezierPath.C1_MASK);
        fig.setNode(idx, node);

        // Then
        assertThat(fig.getNode(0).getControlPoint(0))
                .isEqualTo(new Point2D.Double(10, 20));
        assertThat(fig.getNode(0).getControlPoint(2))
                .isEqualTo(new Point2D.Double(10, 20));
    }

    // --- Scenario: getNode() returns defensive copy ---
    // Given a figure with one node at (10, 20)
    // When the retrieved node is mutated
    // Then the stored node is unchanged
    @Test
    public void get_node_returns_defensive_copy() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        fig.addNode(new BezierPath.Node(10, 20));

        // When
        BezierPath.Node n = fig.getNode(0);
        n.setControlPoint(0, new Point2D.Double(999, 999));

        // Then
        assertThat(fig.getNode(0).getControlPoint(0))
                .isEqualTo(new Point2D.Double(10, 20));
    }

    // --- Scenario: Removing last node empties figure ---
    // Given a figure with one node at (10, 20)
    // When the only node is removed
    // Then the figure is empty
    @Test
    public void removing_last_node_empties_figure() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        fig.addNode(new BezierPath.Node(10, 20));

        // When
        fig.removeNode(0);

        // Then
        assertThat(fig.getNodeCount()).isZero();
    }

    // --- Scenario: getNode() on empty figure throws ---
    // Given an empty figure
    // When a node is retrieved
    // Then an IndexOutOfBoundsException is thrown
    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void get_node_on_empty_figure_throws() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        // When
        fig.getNode(0);
        // Then (expected exception)
    }

    // --- Scenario: Figure closes and opens ---
    // Given a figure with two nodes
    // When the figure is closed
    // Then the figure is closed and has node count 2
    // When the figure is opened
    // Then the figure is not closed
    @Test
    public void figure_closes_and_opens() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        fig.addNode(new BezierPath.Node(10, 20));
        fig.addNode(new BezierPath.Node(30, 40));

        // When
        fig.setClosed(true);

        // Then
        assertThat(fig.isClosed()).isTrue();
        assertThat(fig.getNodeCount()).isEqualTo(2);

        // When
        fig.setClosed(false);

        // Then
        assertThat(fig.isClosed()).isFalse();
    }

    // --- Scenario: setPoint() moves anchor and all controls ---
    // Given a figure with one node at (10, 20)
    // When the anchor is moved to (30, 40)
    // Then all control points are at (30, 40)
    @Test
    public void set_point_moves_anchor_and_all_controls() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        fig.addNode(new BezierPath.Node(10, 20));

        // When
        fig.setPoint(0, new Point2D.Double(30, 40));

        // Then
        Point2D.Double expected = new Point2D.Double(30, 40);
        assertThat(fig.getNode(0).getControlPoint(0)).isEqualTo(expected);
        assertThat(fig.getNode(0).getControlPoint(1)).isEqualTo(expected);
        assertThat(fig.getNode(0).getControlPoint(2)).isEqualTo(expected);
    }

    // --- Scenario: Masks accumulate correctly ---
    // Given a figure with one node at (10, 20)
    // When the C2 control point is set to (100, 200)
    // And the C1 control point is set to (50, 60)
    // Then both masks are set
    @Test
    public void masks_accumulate_correctly() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        fig.addNode(new BezierPath.Node(10, 20));

        // When
        int idx = 0;
        BezierPath.Node node = fig.getNode(idx);
        node.setControlPoint(2, new Point2D.Double(100, 200));
        node.setMask(node.getMask() | BezierPath.C2_MASK);
        fig.setNode(idx, node);

        node = fig.getNode(idx);
        node.setControlPoint(1, new Point2D.Double(50, 60));
        node.setMask(node.getMask() | BezierPath.C1_MASK);
        fig.setNode(idx, node);

        // Then
        int mask = fig.getNode(0).getMask();
        assertThat(mask & BezierPath.C1_MASK).isNotZero();
        assertThat(mask & BezierPath.C2_MASK).isNotZero();
    }

    // --- Scenario: setNode() persists through write-back ---
    // Given a figure with one node at (10, 20)
    // When the node is retrieved, modified, and written back
    // Then the modifications are persisted
    @Test
    public void set_node_persists_through_write_back() {
        // Given
        BezierFigure fig = new BezierFigure(false);
        fig.addNode(new BezierPath.Node(10, 20));

        // When
        BezierPath.Node n = fig.getNode(0);
        n.setControlPoint(2, new Point2D.Double(99, 199));
        n.setMask(n.getMask() | BezierPath.C2_MASK);
        fig.setNode(0, n);

        // Then
        assertThat(fig.getNode(0).getControlPoint(2))
                .isEqualTo(new Point2D.Double(99, 199));
        assertThat(fig.getNode(0).getMask() & BezierPath.C2_MASK).isNotZero();
    }
}
