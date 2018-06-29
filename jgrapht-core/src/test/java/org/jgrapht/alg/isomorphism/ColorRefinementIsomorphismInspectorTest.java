package org.jgrapht.alg.isomorphism;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ColorRefinementIsomorphismInspectorTest {

    @Test
    public void testGetMappingsForRegularGraphs() {
        Graph<Integer, DefaultEdge> graph1 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graph<Integer, DefaultEdge> graph2 = new DefaultUndirectedGraph<>(DefaultEdge.class);

        Graphs.addAllVertices(graph1, Arrays.asList(1, 2, 3, 4, 5, 6));
        graph1.addEdge(1, 2);
        graph1.addEdge(2, 3);
        graph1.addEdge(3, 1);
        graph1.addEdge(4, 5);
        graph1.addEdge(5, 6);
        graph1.addEdge(6, 4);

        Graphs.addAllVertices(graph2, Arrays.asList(1, 2, 3, 4, 5, 6));
        graph2.addEdge(1, 2);
        graph2.addEdge(2, 3);
        graph2.addEdge(3, 4);
        graph2.addEdge(4, 5);
        graph2.addEdge(5, 6);
        graph2.addEdge(6, 1);

        ColorRefinementIsomorphismInspector<Integer, DefaultEdge> isomorphismInspector =
                new ColorRefinementIsomorphismInspector<>(graph1, graph2);

        assertNull(isomorphismInspector.getMappings());
        assertFalse(isomorphismInspector.isColoringDiscrete());
        assertFalse(isomorphismInspector.isomorphismExists().isPresent());
        assertFalse(isomorphismInspector.isForest());
    }

    /**
     * test for two complete binary trees of size 7
     */
    @Test
    public void testGetMappingsForTrees() {
        Graph<Integer, DefaultEdge> graph1 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graph<Integer, DefaultEdge> graph2 = new DefaultUndirectedGraph<>(DefaultEdge.class);

        Graphs.addAllVertices(graph1, Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        graph1.addEdge(1, 2);
        graph1.addEdge(1, 3);
        graph1.addEdge(2, 4);
        graph1.addEdge(2, 5);
        graph1.addEdge(3, 6);
        graph1.addEdge(3, 7);

        Graphs.addAllVertices(graph2, Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        graph2.addEdge(1, 2);
        graph2.addEdge(2, 3);
        graph2.addEdge(2, 4);
        graph2.addEdge(4, 5);
        graph2.addEdge(5, 6);
        graph2.addEdge(5, 7);

        ColorRefinementIsomorphismInspector<Integer, DefaultEdge> isomorphismInspector =
                new ColorRefinementIsomorphismInspector<>(graph1, graph2);

        assertTrue(isomorphismInspector.isomorphismExists().isPresent());
        assertTrue(isomorphismInspector.isomorphismExists().get());
        assertFalse(isomorphismInspector.isColoringDiscrete());
        assertTrue(isomorphismInspector.isForest());

        GraphMapping<Integer, DefaultEdge> graphMapping = isomorphismInspector.getMappings().next();

        assertEquals(4, graphMapping.getVertexCorrespondence(1, true).intValue());

        assertTrue(graphMapping.getVertexCorrespondence(2, true) == 2
                || graphMapping.getVertexCorrespondence(2, true) == 5);
        assertTrue(graphMapping.getVertexCorrespondence(3, true) == 2
                || graphMapping.getVertexCorrespondence(3, true) == 5);

        assertTrue(graphMapping.getVertexCorrespondence(4, true) == 1
                || graphMapping.getVertexCorrespondence(4, true) == 3
                || graphMapping.getVertexCorrespondence(4, true) == 6
                || graphMapping.getVertexCorrespondence(4, true) == 7);
        assertTrue(graphMapping.getVertexCorrespondence(5, true) == 1
                || graphMapping.getVertexCorrespondence(5, true) == 3
                || graphMapping.getVertexCorrespondence(5, true) == 6
                || graphMapping.getVertexCorrespondence(5, true) == 7);
        assertTrue(graphMapping.getVertexCorrespondence(6, true) == 1
                || graphMapping.getVertexCorrespondence(6, true) == 3
                || graphMapping.getVertexCorrespondence(6, true) == 6
                || graphMapping.getVertexCorrespondence(6, true) == 7);
        assertTrue(graphMapping.getVertexCorrespondence(7, true) == 1
                || graphMapping.getVertexCorrespondence(7, true) == 3
                || graphMapping.getVertexCorrespondence(7, true) == 6
                || graphMapping.getVertexCorrespondence(7, true) == 7);

        assertEquals(1, graphMapping.getVertexCorrespondence(4, true).intValue());

        assertTrue(graphMapping.getVertexCorrespondence(2, false) == 2
                || graphMapping.getVertexCorrespondence(2, false) == 3);
        assertTrue(graphMapping.getVertexCorrespondence(5, false) == 2
                || graphMapping.getVertexCorrespondence(5, false) == 3);

        assertTrue(graphMapping.getVertexCorrespondence(1, false) == 4
                || graphMapping.getVertexCorrespondence(1, false) == 5
                || graphMapping.getVertexCorrespondence(1, false) == 6
                || graphMapping.getVertexCorrespondence(1, false) == 7);
        assertTrue(graphMapping.getVertexCorrespondence(3, false) == 4
                || graphMapping.getVertexCorrespondence(3, false) == 5
                || graphMapping.getVertexCorrespondence(3, false) == 6
                || graphMapping.getVertexCorrespondence(3, false) == 7);
        assertTrue(graphMapping.getVertexCorrespondence(6, false) == 4
                || graphMapping.getVertexCorrespondence(6, false) == 5
                || graphMapping.getVertexCorrespondence(6, false) == 6
                || graphMapping.getVertexCorrespondence(6, false) == 7);
        assertTrue(graphMapping.getVertexCorrespondence(7, false) == 4
                || graphMapping.getVertexCorrespondence(7, false) == 5
                || graphMapping.getVertexCorrespondence(7, false) == 6
                || graphMapping.getVertexCorrespondence(7, false) == 7);

        for(int i = 1; i <= 7; ++i) {
            for(int j = i + 1; j <= 7; ++j) {
                assertNotEquals(graphMapping.getVertexCorrespondence(i, true).intValue(), graphMapping.getVertexCorrespondence(j, true).intValue());
                assertNotEquals(graphMapping.getVertexCorrespondence(i, false).intValue(), graphMapping.getVertexCorrespondence(j, false).intValue());
            }
        }
    }

    @Test
    public void testGetMappingsForIsomorphicGraphsOfSize6() {
        Graph<Integer, DefaultEdge> graph1 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graph<Integer, DefaultEdge> graph2 = new DefaultUndirectedGraph<>(DefaultEdge.class);

        Graphs.addAllVertices(graph1, Arrays.asList(1, 2, 3, 4, 5, 6));
        graph1.addEdge(1, 2);
        graph1.addEdge(1, 3);
        graph1.addEdge(1, 6);
        graph1.addEdge(2, 3);
        graph1.addEdge(3, 4);
        graph1.addEdge(4, 5);

        Graphs.addAllVertices(graph2, Arrays.asList(1, 2, 3, 4, 5, 6));
        graph2.addEdge(1, 2);
        graph2.addEdge(1, 3);
        graph2.addEdge(1, 5);
        graph2.addEdge(2, 6);
        graph2.addEdge(3, 5);
        graph2.addEdge(3, 4);

        ColorRefinementIsomorphismInspector<Integer, DefaultEdge> isomorphismInspector =
                new ColorRefinementIsomorphismInspector<>(graph1, graph2);

        GraphMapping<Integer, DefaultEdge> graphMapping = isomorphismInspector.getMappings().next();

        assertTrue(isomorphismInspector.isomorphismExists().get());
        assertTrue(isomorphismInspector.isColoringDiscrete());
        assertFalse(isomorphismInspector.isForest());

        assertEquals(graphMapping.getVertexCorrespondence(1, true).intValue(), 3);
        assertEquals(graphMapping.getVertexCorrespondence(2, true).intValue(), 5);
        assertEquals(graphMapping.getVertexCorrespondence(3, true).intValue(), 1);
        assertEquals(graphMapping.getVertexCorrespondence(4, true).intValue(), 2);
        assertEquals(graphMapping.getVertexCorrespondence(5, true).intValue(), 6);
        assertEquals(graphMapping.getVertexCorrespondence(6, true).intValue(), 4);

        assertEquals(graphMapping.getVertexCorrespondence(1, false).intValue(), 3);
        assertEquals(graphMapping.getVertexCorrespondence(2, false).intValue(), 4);
        assertEquals(graphMapping.getVertexCorrespondence(3, false).intValue(), 1);
        assertEquals(graphMapping.getVertexCorrespondence(4, false).intValue(), 6);
        assertEquals(graphMapping.getVertexCorrespondence(5, false).intValue(), 2);
        assertEquals(graphMapping.getVertexCorrespondence(6, false).intValue(), 5);
    }

    @Test
    public void testGetMappingForGraphWithDifferentNumberOfNodes() {
        Graph<Integer, DefaultEdge> graph1 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graph<Integer, DefaultEdge> graph2 = new DefaultUndirectedGraph<>(DefaultEdge.class);

        Graphs.addAllVertices(graph1, Arrays.asList(1, 2, 3, 4, 5));
        graph1.addEdge(1, 3);
        graph1.addEdge(2, 5);
        graph1.addEdge(3, 4);
        graph1.addEdge(4, 5);

        Graphs.addAllVertices(graph2, Arrays.asList(1, 2, 3, 4, 5, 6));
        graph2.addEdge(1, 3);
        graph2.addEdge(2, 5);
        graph2.addEdge(3, 4);
        graph2.addEdge(4, 5);

        ColorRefinementIsomorphismInspector<Integer, DefaultEdge> isomorphismInspector =
                new ColorRefinementIsomorphismInspector<>(graph1, graph2);

        assertFalse(isomorphismInspector.isomorphismExists().get());
        assertFalse(isomorphismInspector.isColoringDiscrete());
    }

    @Test
    public void testGetMappingsForGraphWithDifferentNumberOfColorClasses() {
        Graph<Integer, DefaultEdge> graph1 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graph<Integer, DefaultEdge> graph2 = new DefaultUndirectedGraph<>(DefaultEdge.class);

        Graphs.addAllVertices(graph1, Arrays.asList(1, 2, 3, 4));
        graph1.addEdge(1, 2);
        graph1.addEdge(1, 3);
        graph1.addEdge(2, 3);
        graph1.addEdge(3, 4);

        Graphs.addAllVertices(graph2, Arrays.asList(1, 2, 3, 4));
        graph1.addEdge(1, 2);
        graph1.addEdge(1, 3);
        graph1.addEdge(2, 4);
        graph1.addEdge(3, 4);

        ColorRefinementIsomorphismInspector<Integer, DefaultEdge> isomorphismInspector =
                new ColorRefinementIsomorphismInspector<>(graph1, graph2);

        assertFalse(isomorphismInspector.isomorphismExists().get());
        assertFalse(isomorphismInspector.isColoringDiscrete());
    }

    @Test
    public void testGetMappingsForIsomorphicForests() {
        Graph<Integer, DefaultEdge> graph1 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graph<Integer, DefaultEdge> graph2 = new DefaultUndirectedGraph<>(DefaultEdge.class);

        Graphs.addAllVertices(graph1, Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        graph1.addEdge(1, 2);
        graph1.addEdge(1, 3);
        graph1.addEdge(4, 5);
        graph1.addEdge(5, 6);
        graph1.addEdge(6, 7);

        Graphs.addAllVertices(graph2, Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        graph2.addEdge(1, 2);
        graph2.addEdge(1, 3);
        graph2.addEdge(3, 4);
        graph2.addEdge(5, 6);
        graph2.addEdge(6, 7);

        ColorRefinementIsomorphismInspector<Integer, DefaultEdge> isomorphismInspector =
                new ColorRefinementIsomorphismInspector<>(graph1, graph2);

        assertTrue(isomorphismInspector.isForest());
        assertTrue(isomorphismInspector.isomorphismExists().get());
        assertFalse(isomorphismInspector.isColoringDiscrete());

        GraphMapping<Integer, DefaultEdge> graphMapping = isomorphismInspector.getMappings().next();

        System.out.println(graphMapping);

        assertTrue((graphMapping.getVertexCorrespondence(1, true) == 5) ||
                (graphMapping.getVertexCorrespondence(1, true) == 6) ||
                (graphMapping.getVertexCorrespondence(1, true) == 7)
        );
        assertTrue((graphMapping.getVertexCorrespondence(2, true) == 5) ||
                (graphMapping.getVertexCorrespondence(2, true) == 6) ||
                (graphMapping.getVertexCorrespondence(2, true) == 7)
        );
        assertTrue((graphMapping.getVertexCorrespondence(3, true) == 5) ||
                (graphMapping.getVertexCorrespondence(3, true) == 6) ||
                (graphMapping.getVertexCorrespondence(3, true) == 7)
        );
        assertTrue((graphMapping.getVertexCorrespondence(4, true) == 2) ||
                (graphMapping.getVertexCorrespondence(4, true) == 4)
        );
        assertTrue((graphMapping.getVertexCorrespondence(5, true) == 1) ||
                (graphMapping.getVertexCorrespondence(5, true) == 3)
        );
        assertTrue((graphMapping.getVertexCorrespondence(6, true) == 1) ||
                (graphMapping.getVertexCorrespondence(6, true) == 3)
        );
        assertTrue((graphMapping.getVertexCorrespondence(7, true) == 2) ||
                (graphMapping.getVertexCorrespondence(7, true) == 4)
        );

        for (int i = 1; i < graph1.vertexSet().size(); i++) {
            for (int j = i+1; j <= graph1.vertexSet().size(); j++) {
                assertNotEquals(graphMapping.getVertexCorrespondence(i, true), (graphMapping.getVertexCorrespondence(j ,true)));
                assertNotEquals(graphMapping.getVertexCorrespondence(i, false), (graphMapping.getVertexCorrespondence(j, false)));
            }
        }
    }

    @Test
    public void testGetMappingsForNotIsomorphicForests() {
        Graph<Integer, DefaultEdge> graph1 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graph<Integer, DefaultEdge> graph2 = new DefaultUndirectedGraph<>(DefaultEdge.class);

        Graphs.addAllVertices(graph1, Arrays.asList(1, 2, 3, 4));
        graph1.addEdge(1, 2);
        graph1.addEdge(1, 3);
        graph1.addEdge(1, 4);


        Graphs.addAllVertices(graph2, Arrays.asList(1, 2, 3, 4));
        graph2.addEdge(1, 2);
        graph2.addEdge(1, 3);
        graph2.addEdge(2, 4);

        ColorRefinementIsomorphismInspector<Integer, DefaultEdge> isomorphismInspector =
                new ColorRefinementIsomorphismInspector<>(graph1, graph2);

        assertTrue(isomorphismInspector.isForest());
        assertFalse(isomorphismInspector.isomorphismExists().isPresent());

    }
}