package org.jgrapht.graph;

import org.jgrapht.EdgeFactory;

import org.jgrapht.Graph;
import org.jgrapht.util.interval.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 *  @author Abdallah Atouani
 *  @since  02 May 2018
 */
public class IntervalGraphTest {

    private IntervalGraph<IntervalVertexInterface<Integer, Integer>, DefaultEdge, Integer, Integer> intervalGraph;
    private IntervalGraph<IntervalVertexInterface<Integer, Integer>, DefaultWeightedEdge, Integer, Integer> weightedIntervalGraph;

    @Before
    public void setUp() {
        EdgeFactory<IntervalVertexInterface<Integer, Integer>, DefaultEdge> edgeFactory1 = new ClassBasedEdgeFactory<>(DefaultEdge.class);
        EdgeFactory<IntervalVertexInterface<Integer, Integer>, DefaultWeightedEdge> edgeFactory2 = new ClassBasedEdgeFactory<>(DefaultWeightedEdge.class);

        intervalGraph = new IntervalGraph<>(edgeFactory1, false);

        weightedIntervalGraph = new IntervalGraph<>(edgeFactory2, true);
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
        weightedIntervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(9, 100);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);
        weightedIntervalGraph.addVertex(vertex2);

        Interval<Integer> interval3 = new Interval<>(6, 11);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(3, interval3);
        intervalGraph.addVertex(vertex3);
        weightedIntervalGraph.addVertex(vertex3);

        Interval<Integer> interval4 = new Interval<>(1000, 1001);
        IntervalVertex<Integer, Integer> vertex4 = IntervalVertex.of(4, interval4);
        intervalGraph.addVertex(vertex4);
        weightedIntervalGraph.addVertex(vertex4);

        assertTrue(intervalGraph.containsEdge(vertex2, vertex1));
        assertTrue(intervalGraph.containsEdge(vertex1, vertex2));
        assertTrue(intervalGraph.containsEdge(vertex1, vertex3));
        assertTrue(intervalGraph.containsEdge(vertex3, vertex1));
        assertTrue(intervalGraph.containsEdge(vertex2, vertex3));
        assertTrue(intervalGraph.containsEdge(vertex3, vertex2));
        assertFalse(intervalGraph.containsEdge(vertex1, vertex4));
        assertFalse(intervalGraph.containsEdge(vertex2, vertex4));
        assertFalse(intervalGraph.containsEdge(vertex3, vertex4));

        assertFalse(intervalGraph.containsEdge(vertex1, vertex1));
        assertFalse(intervalGraph.containsEdge(vertex2, vertex2));
        assertFalse(intervalGraph.containsEdge(vertex3, vertex3));
        assertFalse(intervalGraph.containsEdge(vertex4, vertex4));

        assertTrue(weightedIntervalGraph.containsEdge(vertex2, vertex1));
        assertTrue(weightedIntervalGraph.containsEdge(vertex1, vertex2));
        assertTrue(weightedIntervalGraph.containsEdge(vertex1, vertex3));
        assertTrue(weightedIntervalGraph.containsEdge(vertex3, vertex1));
        assertTrue(weightedIntervalGraph.containsEdge(vertex2, vertex3));
        assertTrue(weightedIntervalGraph.containsEdge(vertex3, vertex2));
        assertFalse(weightedIntervalGraph.containsEdge(vertex1, vertex4));
        assertFalse(weightedIntervalGraph.containsEdge(vertex2, vertex4));
        assertFalse(weightedIntervalGraph.containsEdge(vertex3, vertex4));

        assertFalse(weightedIntervalGraph.containsEdge(vertex1, vertex1));
        assertFalse(weightedIntervalGraph.containsEdge(vertex2, vertex2));
        assertFalse(weightedIntervalGraph.containsEdge(vertex3, vertex3));
        assertFalse(weightedIntervalGraph.containsEdge(vertex4, vertex4));
    }

    @Test
    public void getAllEdges() {
        Interval<Integer> interval1 = new Interval<>(5, 19);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);
        weightedIntervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(9, 100);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);
        weightedIntervalGraph.addVertex(vertex2);

        Interval<Integer> interval3 = new Interval<>(6, 11);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(3, interval3);
        intervalGraph.addVertex(vertex3);
        weightedIntervalGraph.addVertex(vertex3);

        Interval<Integer> interval4 = new Interval<>(1000, 1001);
        IntervalVertex<Integer, Integer> vertex4 = IntervalVertex.of(4, interval4);
        intervalGraph.addVertex(vertex4);
        weightedIntervalGraph.addVertex(vertex4);

        Set<DefaultEdge> edgeSet = intervalGraph.getAllEdges(vertex1, vertex2);
        assertTrue(edgeSet.contains(intervalGraph.getEdge(vertex1, vertex2)));
        assertEquals(1, edgeSet.size());

        edgeSet = intervalGraph.getAllEdges(vertex1, vertex4);
        assertTrue(edgeSet.isEmpty());

        Set<DefaultEdge> weightedEdgeSet =  weightedIntervalGraph.getAllEdges(vertex1, vertex2);
        assertTrue(weightedEdgeSet.contains(weightedIntervalGraph.getEdge(vertex1, vertex2)));
        assertEquals(1, weightedEdgeSet.size());

        weightedEdgeSet = weightedIntervalGraph.getAllEdges(vertex1, vertex4);
        assertTrue(weightedEdgeSet.isEmpty());
    }

    @Test
    public void addVertex() {
        Interval<Integer> interval1 = new Interval<>(1, 2);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);
        weightedIntervalGraph.addVertex(vertex1);

        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex2);
        weightedIntervalGraph.addVertex(vertex2);

        assertTrue(intervalGraph.vertexSet().contains(vertex1));
        assertTrue(intervalGraph.vertexSet().contains(vertex2));
        assertEquals(1, intervalGraph.vertexSet().size());
        assertTrue(weightedIntervalGraph.vertexSet().contains(vertex1));
        assertTrue(weightedIntervalGraph.vertexSet().contains(vertex2));
        assertEquals(1, weightedIntervalGraph.vertexSet().size());

        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(2, interval1);
        intervalGraph.addVertex(vertex3);
        weightedIntervalGraph.addVertex(vertex3);

        assertTrue(intervalGraph.vertexSet().contains(vertex3));
        assertEquals(2, intervalGraph.vertexSet().size());
        assertTrue(weightedIntervalGraph.vertexSet().contains(vertex3));
        assertEquals(2, weightedIntervalGraph.vertexSet().size());

        Interval<Integer> interval2 = new Interval<>(2, 10);
        IntervalVertex<Integer, Integer> vertex4 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex4);
        weightedIntervalGraph.addVertex(vertex4);

        assertTrue(intervalGraph.vertexSet().contains(vertex4));
        assertEquals(3, intervalGraph.vertexSet().size());
        assertTrue(weightedIntervalGraph.vertexSet().contains(vertex4));
        assertEquals(3, weightedIntervalGraph.vertexSet().size());
    }

    @Test
    public void removeVertex() {
        Interval<Integer> interval1 = new Interval<>(3, 20);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);
        weightedIntervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(4, 100);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);
        weightedIntervalGraph.addVertex(vertex2);

        assertTrue(intervalGraph.vertexSet().contains(vertex1));
        assertTrue(intervalGraph.vertexSet().contains(vertex2));
        assertTrue(weightedIntervalGraph.vertexSet().contains(vertex1));
        assertTrue(weightedIntervalGraph.vertexSet().contains(vertex2));

        intervalGraph.removeVertex(vertex1);
        weightedIntervalGraph.removeVertex(vertex1);

        assertTrue(intervalGraph.vertexSet().contains(vertex2));
        assertFalse(intervalGraph.vertexSet().contains(vertex1));
        assertTrue(weightedIntervalGraph.vertexSet().contains(vertex2));
        assertFalse(weightedIntervalGraph.vertexSet().contains(vertex1));

        assertEquals(1, intervalGraph.vertexSet().size());
        assertEquals(1, weightedIntervalGraph.vertexSet().size());

        intervalGraph.removeVertex(vertex2);
        weightedIntervalGraph.removeVertex(vertex2);

        assertFalse(intervalGraph.vertexSet().contains(vertex1));
        assertFalse(intervalGraph.vertexSet().contains(vertex2));
        assertFalse(weightedIntervalGraph.vertexSet().contains(vertex1));
        assertFalse(weightedIntervalGraph.vertexSet().contains(vertex2));

        assertEquals(0, intervalGraph.vertexSet().size());
        assertEquals(0, weightedIntervalGraph.vertexSet().size());
    }

    @Test
    public void containsVertex() {
        Interval<Integer> interval1 = new Interval<>(8, 9);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);
        weightedIntervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(27, 56);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);
        weightedIntervalGraph.addVertex(vertex2);

        assertTrue(intervalGraph.containsVertex(vertex1));
        assertTrue(intervalGraph.containsVertex(vertex2));
        assertEquals(2, intervalGraph.vertexSet().size());
        assertTrue(weightedIntervalGraph.containsVertex(vertex1));
        assertTrue(weightedIntervalGraph.containsVertex(vertex2));
        assertEquals(2, weightedIntervalGraph.vertexSet().size());

        intervalGraph.removeVertex(vertex1);
        weightedIntervalGraph.removeVertex(vertex1);

        assertFalse(intervalGraph.containsVertex(vertex1));
        assertTrue(intervalGraph.containsVertex(vertex2));
        assertEquals(1, intervalGraph.vertexSet().size());
        assertFalse(weightedIntervalGraph.containsVertex(vertex1));
        assertTrue(weightedIntervalGraph.containsVertex(vertex2));
        assertEquals(1, weightedIntervalGraph.vertexSet().size());
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

        assertEquals(vertex2, intervalGraph.getEdgeSource(edge));
        assertEquals(vertex3, intervalGraph.getEdgeSource(edge1));
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

        assertEquals(vertex1, intervalGraph.getEdgeTarget(edge));
        assertEquals(vertex2, intervalGraph.getEdgeTarget(edge1));

    }

    @Test
    public void edgeSet() {
        Interval<Integer> interval1 = new Interval<>(29, 30);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(3, interval1);
        intervalGraph.addVertex(vertex1);
        weightedIntervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(27, 56);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(1, interval2);
        intervalGraph.addVertex(vertex2);
        weightedIntervalGraph.addVertex(vertex2);

        assertEquals(1, intervalGraph.edgeSet().size());
        assertEquals(1, weightedIntervalGraph.edgeSet().size());
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
        assertEquals(2, intervalGraph.vertexSet().size());

        Interval<Integer> interval3 = new Interval<>(100, 293);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(1, interval3);
        intervalGraph.addVertex(vertex3);

        assertTrue(intervalGraph.vertexSet().contains(vertex1));
        assertTrue(intervalGraph.vertexSet().contains(vertex2));
        assertTrue(intervalGraph.vertexSet().contains(vertex3));
        assertEquals(3, intervalGraph.vertexSet().size());

        intervalGraph.removeVertex(vertex2);
        intervalGraph.removeVertex(vertex3);

        assertTrue(intervalGraph.vertexSet().contains(vertex1));
        assertFalse(intervalGraph.vertexSet().contains(vertex2));
        assertFalse(intervalGraph.vertexSet().contains(vertex3));
        assertEquals(1, intervalGraph.vertexSet().size());

        intervalGraph.removeVertex(vertex1);

        assertTrue(intervalGraph.vertexSet().isEmpty());
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

        assertEquals(1, intervalGraph.getEdgeWeight(edge), 0);
        assertEquals(1, intervalGraph.getEdgeWeight(edge1), 0);

        weightedIntervalGraph.addVertex(vertex1);
        weightedIntervalGraph.addVertex(vertex2);
        weightedIntervalGraph.addVertex(vertex3);

        DefaultWeightedEdge edge2 = weightedIntervalGraph.getEdge(vertex1, vertex3);
        DefaultWeightedEdge edge3 = weightedIntervalGraph.getEdge(vertex2, vertex1);
        weightedIntervalGraph.setEdgeWeight(edge2, 45);
        weightedIntervalGraph.setEdgeWeight(edge3, 87);

        assertEquals(45, weightedIntervalGraph.getEdgeWeight(edge2), 0);
        assertEquals(87, weightedIntervalGraph.getEdgeWeight(edge3), 0);
    }

    @Test
    public void cloneIntervalGraph() {
        IntervalGraph<IntervalVertexInterface<Integer, Integer>, DefaultEdge, Integer, Integer> clonedIntervalGraph =
                (IntervalGraph<IntervalVertexInterface<Integer,Integer>, DefaultEdge, Integer, Integer>) intervalGraph.clone();

        assertEquals(clonedIntervalGraph.vertexSet(), intervalGraph.vertexSet());
        assertEquals(clonedIntervalGraph.edgeSet(), intervalGraph.edgeSet());
        assertEquals(clonedIntervalGraph.getEdgeFactory(), intervalGraph.getEdgeFactory());
        assertEquals(clonedIntervalGraph.isWeighted(), intervalGraph.isWeighted());
        assertTrue(clonedIntervalGraph.equals(intervalGraph));
    }

    @Test
    public void degreeOf() {
        Interval<Integer> interval1 = new Interval<>(-3, 1);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(0, 4);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);

        Interval<Integer> interval3 = new Interval<>(2, 29);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(3, interval3);
        intervalGraph.addVertex(vertex3);

        Interval<Integer> interval4 = new Interval<>(348, 2394);
        IntervalVertex<Integer, Integer> vertex4 = IntervalVertex.of(4, interval4);
        intervalGraph.addVertex(vertex4);

        assertEquals(2, intervalGraph.degreeOf(vertex2));
        assertEquals(1, intervalGraph.degreeOf(vertex1));
        assertEquals(1, intervalGraph.degreeOf(vertex3));
        assertEquals(0, intervalGraph.degreeOf(vertex4));
    }

    @Test
    public void edgesOf() {
        Interval<Integer> interval1 = new Interval<>(-10, 19);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(9, 100);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraph.addVertex(vertex2);

        Interval<Integer> interval3 = new Interval<>(12, 39);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(3, interval3);
        intervalGraph.addVertex(vertex3);

        Interval<Integer> interval4 = new Interval<>(1000, 1001);
        IntervalVertex<Integer, Integer> vertex4 = IntervalVertex.of(4, interval4);
        intervalGraph.addVertex(vertex4);

        Set<DefaultEdge> edgesOfVertex1 = intervalGraph.edgesOf(vertex1);
        assertTrue(edgesOfVertex1.contains(intervalGraph.getEdge(vertex1, vertex2)));
        assertTrue(edgesOfVertex1.contains(intervalGraph.getEdge(vertex2, vertex1)));
        assertTrue(edgesOfVertex1.contains(intervalGraph.getEdge(vertex1, vertex3)));
        assertEquals(2, edgesOfVertex1.size());

        Set<DefaultEdge> edgesOfVertex2 = intervalGraph.edgesOf(vertex2);
        assertTrue(edgesOfVertex2.contains(intervalGraph.getEdge(vertex1, vertex2)));
        assertTrue(edgesOfVertex2.contains(intervalGraph.getEdge(vertex2, vertex1)));
        assertTrue(edgesOfVertex2.contains(intervalGraph.getEdge(vertex2, vertex3)));
        assertEquals(2, edgesOfVertex1.size());

        Set<DefaultEdge> edgesOfVertex3 = intervalGraph.edgesOf(vertex3);
        assertTrue(edgesOfVertex3.contains(intervalGraph.getEdge(vertex2, vertex3)));
        assertTrue(edgesOfVertex3.contains(intervalGraph.getEdge(vertex3, vertex2)));
        assertTrue(edgesOfVertex3.contains(intervalGraph.getEdge(vertex1, vertex3)));
        assertEquals(2, edgesOfVertex1.size());

        Set<DefaultEdge> edgesOfVertex4 = intervalGraph.edgesOf(vertex4);
        assertTrue(edgesOfVertex4.isEmpty());
    }

    @Test
    public void isWeighted() {
        assertFalse(intervalGraph.isWeighted());
        assertTrue(weightedIntervalGraph.isWeighted());
    }

    @Test
    public void setEdgeWeight() {
        Interval<Integer> interval1 = new Interval<>(2, 19);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(2, interval1);
        weightedIntervalGraph.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(-4, 8);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(1, interval2);
        weightedIntervalGraph.addVertex(vertex2);

        Interval<Integer> interval3 = new Interval<>(11, 198);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(3, interval3);
        weightedIntervalGraph.addVertex(vertex3);

        DefaultWeightedEdge edge1 = weightedIntervalGraph.getEdge(vertex1, vertex2);
        DefaultWeightedEdge edge2 = weightedIntervalGraph.getEdge(vertex1, vertex3);

        weightedIntervalGraph.setEdgeWeight(edge1, 3.0);
        weightedIntervalGraph.setEdgeWeight(edge2, 93.0);

        assertEquals(3.0, weightedIntervalGraph.getEdgeWeight(edge1), 0.001);
        assertEquals(93.0, weightedIntervalGraph.getEdgeWeight(edge2),0.001);

    }

    @Test
    public void initialiseWithVertices() {
        Interval<Integer> interval1 = new Interval<>(2, 19);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(2, interval1);

        Interval<Integer> interval2 = new Interval<>(-4, 8);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(1, interval2);

        Interval<Integer> interval3 = new Interval<>(11, 18);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(3, interval3);

        Interval<Integer> interval4 = new Interval<>(100, 198);
        IntervalVertex<Integer, Integer> vertex4 = IntervalVertex.of(4, interval4);

        ArrayList<IntervalVertex<Integer, Integer>> vertices = new ArrayList<>();
        vertices.add(vertex1);
        vertices.add(vertex2);
        vertices.add(vertex3);
        vertices.add(vertex4);

        EdgeFactory<IntervalVertexInterface<Integer, Integer>, DefaultEdge> edgeFactory = new ClassBasedEdgeFactory<>(DefaultEdge.class);
        IntervalGraph<IntervalVertexInterface<Integer, Integer>, DefaultEdge, Integer, Integer> graph =
                new IntervalGraph(vertices, edgeFactory, false);

        assertTrue(graph.containsVertex(vertex1));
        assertTrue(graph.containsVertex(vertex2));
        assertTrue(graph.containsVertex(vertex3));
        assertTrue(graph.containsVertex(vertex4));
        assertEquals(4, graph.vertexSet().size());

        DefaultEdge edge1 = graph.getEdge(vertex1, vertex2);
        DefaultEdge edge2 = graph.getEdge(vertex1, vertex3);

        assertTrue(graph.containsEdge(edge1));
        assertTrue(graph.containsEdge(edge2));
        assertEquals(2, graph.edgeSet().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void asIntervalGraphForDirectedGraph() {
        Graph<Integer, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        directedGraph.addVertex(1);
        directedGraph.addVertex(2);
        directedGraph.addVertex(3);
        directedGraph.addVertex(4);
        directedGraph.addEdge(1,2);
        directedGraph.addEdge(2,3);

        assertNull(IntervalGraph.asIntervalGraph(directedGraph));
    }

    @Test
    public void asIntervalGraph() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addEdge(1,2);
        graph.addEdge(2,3);

        IntervalGraph<IntervalVertexInterface<Integer, Integer>, DefaultEdge, Integer, Integer> intervalGraph =
                IntervalGraph.asIntervalGraph(graph);

        assertEquals(4, intervalGraph.vertexSet().size());
        assertEquals(2, intervalGraph.edgeSet().size());
        Set<IntervalVertexInterface<Integer, Integer>> vertexSet = intervalGraph.vertexSet();

        List<Integer> verticesList = new ArrayList<>();
        IntervalVertexInterface<Integer, Integer>[] vertices = new IntervalVertex[4];

        for (IntervalVertexInterface<Integer, Integer> vertex: vertexSet) {
            verticesList.add(vertex.getVertex());
            vertices[vertex.getVertex() - 1] = vertex;
        }

        assertTrue(verticesList.contains(1));
        assertTrue(verticesList.contains(2));
        assertTrue(verticesList.contains(3));
        assertTrue(verticesList.contains(4));
        assertEquals(4, verticesList.size());

        DefaultEdge edge1 = intervalGraph.getEdge(vertices[1], vertices[0]);
        DefaultEdge edge2 = intervalGraph.getEdge(vertices[1], vertices[2]);

        assertTrue(intervalGraph.containsEdge(edge1));
        assertTrue(intervalGraph.containsEdge(edge2));
        assertEquals(2, intervalGraph.edgeSet().size());
    }

}
