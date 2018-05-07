package org.jgrapht.intervalgraph;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.intervalgraph.interval.Interval;
import org.jgrapht.intervalgraph.interval.IntervalVertex;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *  @author Abdallah Atouani
 *  @since  02 May 2018
 */
public class IntervalGraphTest {

    private IntervalGraph<IntervalVertex, DefaultEdge> intervalGraph;

    @Before
    public void setUp() {
        EdgeFactory<IntervalVertex, DefaultEdge> edgeFactory = new ClassBasedEdgeFactory<>(DefaultEdge.class);
        intervalGraph = new IntervalGraph<>(
                edgeFactory, false, false, false, false
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void addEdge() {
        Interval<Integer> interval1 = new Interval<>(1, 2);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(1, 10);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);

        intervalGraph.addEdge(vertex1, vertex2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeEdge() {
        Interval<Integer> interval1 = new Interval<>(1, 2);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(1, 10);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);

        intervalGraph.removeEdge(vertex1, vertex2);
    }

    @Test
    public void containsEdge() {
        Interval<Integer> interval1 = new Interval<>(5, 19);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(9, 100);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);

        Interval<Integer> interval3 = new Interval<>(6, 11);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(3, interval3);
        intervalGraph.addVertex(vertex3);

        Interval<Integer> interval4 = new Interval<>(1000, 1001);
        IntervalVertex<Integer, Integer> vertex4 = IntervalVertex.of(4, interval4);
        intervalGraph.addVertex(vertex4);

        assertTrue(intervalGraph.containsEdge(vertex2, vertex1));
        assertTrue(intervalGraph.containsEdge(vertex1, vertex2));
        assertTrue(intervalGraph.containsEdge(vertex1, vertex3));
        assertTrue(intervalGraph.containsEdge(vertex3, vertex1));
        assertTrue(intervalGraph.containsEdge(vertex2, vertex3));
        assertTrue(intervalGraph.containsEdge(vertex3, vertex2));
        assertFalse(intervalGraph.containsEdge(vertex1, vertex4));
        assertFalse(intervalGraph.containsEdge(vertex2, vertex4));
        assertFalse(intervalGraph.containsEdge(vertex3, vertex4));

        /*
        assertFalse(intervalGraph.containsEdge(vertex1, vertex1));
        assertFalse(intervalGraph.containsEdge(vertex2, vertex2));
        assertFalse(intervalGraph.containsEdge(vertex3, vertex3));
        assertFalse(intervalGraph.containsEdge(vertex4, vertex4));
        */
    }

    @Test
    public void addVertex() {
        Interval<Integer> interval1 = new Interval<>(1, 2);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);

        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex2);

        assertTrue(intervalGraph.vertexSet().contains(vertex1));
        assertTrue(intervalGraph.vertexSet().contains(vertex2));
        assertEquals(intervalGraph.vertexSet().size(), 1);

        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(2, interval1);
        intervalGraph.addVertex(vertex3);

        assertTrue(intervalGraph.vertexSet().contains(vertex3));
        assertEquals(intervalGraph.vertexSet().size(), 2);

        Interval<Integer> interval2 = new Interval<>(2, 10);
        IntervalVertex<Integer, Integer> vertex4 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex4);

        assertTrue(intervalGraph.vertexSet().contains(vertex4));
        assertEquals(intervalGraph.vertexSet().size(), 3);
    }

    @Test
    public void removeVertex() {
        Interval<Integer> interval1 = new Interval<>(3, 20);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(4, 100);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);

        assertTrue(intervalGraph.vertexSet().contains(vertex1));
        assertTrue(intervalGraph.vertexSet().contains(vertex2));

        intervalGraph.removeVertex(vertex1);

        assertTrue(intervalGraph.vertexSet().contains(vertex2));
        assertFalse(intervalGraph.vertexSet().contains(vertex1));

        assertEquals(intervalGraph.vertexSet().size(), 1);

        intervalGraph.removeVertex(vertex2);

        assertFalse(intervalGraph.vertexSet().contains(vertex1));
        assertFalse(intervalGraph.vertexSet().contains(vertex2));

        assertEquals(intervalGraph.vertexSet().size(), 0);
    }

    @Test
    public void containsVertex() {
        Interval<Integer> interval1 = new Interval<>(8, 9);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(27, 56);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);

        assertTrue(intervalGraph.containsVertex(vertex1));
        assertTrue(intervalGraph.containsVertex(vertex2));
        assertEquals(intervalGraph.vertexSet().size(), 2);

        intervalGraph.removeVertex(vertex1);

        assertFalse(intervalGraph.containsVertex(vertex1));
        assertTrue(intervalGraph.containsVertex(vertex2));
        assertEquals(intervalGraph.vertexSet().size(), 1);
    }

    @Test
    public void getEdgeSource() {
        Interval<Integer> interval1 = new Interval<>(10, 14);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(2, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(1, 18);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(1, interval2);
        intervalGraph.addVertex(vertex2);

        Interval<Integer> interval3 = new Interval<>(-3, 2);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(3, interval3);
        intervalGraph.addVertex(vertex3);

        DefaultEdge edge = intervalGraph.getEdge(vertex1, vertex2);
        DefaultEdge edge1 = intervalGraph.getEdge(vertex2, vertex3);

        assertEquals(intervalGraph.getEdgeSource(edge), vertex2);
        assertEquals(intervalGraph.getEdgeSource(edge1), vertex3);
    }

    @Test
    public void getEdgeTarget() {
        Interval<Integer> interval1 = new Interval<>(2, 4);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(1, 7);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(3, interval2);
        intervalGraph.addVertex(vertex2);

        Interval<Integer> interval3 = new Interval<>(0, 4);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(2, interval3);
        intervalGraph.addVertex(vertex3);

        DefaultEdge edge = intervalGraph.getEdge(vertex1, vertex2);
        DefaultEdge edge1 = intervalGraph.getEdge(vertex2, vertex3);

        assertEquals(intervalGraph.getEdgeTarget(edge), vertex1);
        assertEquals(intervalGraph.getEdgeTarget(edge1), vertex2);

    }

    @Test
    public void edgeSet() {
        Interval<Integer> interval1 = new Interval<>(29, 30);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(3, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(27, 56);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(1, interval2);
        intervalGraph.addVertex(vertex2);

        assertEquals(intervalGraph.edgeSet().size(), 2);

    }

    @Test
    public void vertexSet() {
        Interval<Integer> interval1 = new Interval<>(-10, 1);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(29, interval1);
        intervalGraph.addVertex(vertex1);

        assertTrue(intervalGraph.vertexSet().contains(vertex1));

        Interval<Integer> interval2 = new Interval<>(-38, 0);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(1, interval2);
        intervalGraph.addVertex(vertex2);

        assertTrue(intervalGraph.vertexSet().contains(vertex1));
        assertTrue(intervalGraph.vertexSet().contains(vertex2));

        Interval<Integer> interval3 = new Interval<>(100, 293);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(1, interval3);
        intervalGraph.addVertex(vertex3);

        assertTrue(intervalGraph.vertexSet().contains(vertex1));
        assertTrue(intervalGraph.vertexSet().contains(vertex2));
        assertTrue(intervalGraph.vertexSet().contains(vertex3));

        intervalGraph.removeVertex(vertex2);
        intervalGraph.removeVertex(vertex3);

        assertTrue(intervalGraph.vertexSet().contains(vertex1));
        assertFalse(intervalGraph.vertexSet().contains(vertex2));
        assertFalse(intervalGraph.vertexSet().contains(vertex3));
    }

    @Test
    public void getEdgeWeight() {
        Interval<Integer> interval1 = new Interval<>(-3, 1);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(29, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(0, 3);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(7, interval2);
        intervalGraph.addVertex(vertex2);

        Interval<Integer> interval3 = new Interval<>(1, 29);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(9, interval3);
        intervalGraph.addVertex(vertex3);

        DefaultEdge edge = intervalGraph.getEdge(vertex3, vertex1);
        DefaultEdge edge1 = intervalGraph.getEdge(vertex2, vertex1);

        assertEquals(intervalGraph.getEdgeWeight(edge), 1, 0);
        assertEquals(intervalGraph.getEdgeWeight(edge1), 1, 0);
    }
}
