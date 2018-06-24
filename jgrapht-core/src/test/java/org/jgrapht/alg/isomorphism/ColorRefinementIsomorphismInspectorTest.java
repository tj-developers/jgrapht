package org.jgrapht.alg.isomorphism;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        GraphMapping<Integer, DefaultEdge> graphMapping = isomorphismInspector.getMappings().next();

        int mappingOfVertex1 = graphMapping.getVertexCorrespondence(1, false);
        int mappingOfVertex2 = graphMapping.getVertexCorrespondence(2, false);
        int mappingOfVertex3 = graphMapping.getVertexCorrespondence(3, false);
        int mappingOfVertex4 = graphMapping.getVertexCorrespondence(4, false);
        int mappingOfVertex5 = graphMapping.getVertexCorrespondence(5, false);
        int mappingOfVertex6 = graphMapping.getVertexCorrespondence(6, false);
        Set<Integer> mappedVertices = new HashSet<>();
        mappedVertices.addAll(Arrays.asList(mappingOfVertex1, mappingOfVertex2, mappingOfVertex3, mappingOfVertex4,
                mappingOfVertex5, mappingOfVertex6));

        assertEquals(6, mappedVertices.stream().distinct().count());
    }

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

        assertTrue(isomorphismInspector.isomorphismExists());
        assertTrue(isomorphismInspector.isColoringDiscrete());

        GraphMapping<Integer, DefaultEdge> graphMapping = isomorphismInspector.getMappings().next();

        int mappingOfVertex1 = graphMapping.getVertexCorrespondence(1, true);
        int mappingOfVertex2 = graphMapping.getVertexCorrespondence(2, true);
        int mappingOfVertex4 = graphMapping.getVertexCorrespondence(4, true);

        assertNotEquals(mappingOfVertex1, mappingOfVertex2);
        assertNotEquals(mappingOfVertex1, mappingOfVertex4);
        assertNotEquals(mappingOfVertex2, mappingOfVertex4);

        assertEquals(mappingOfVertex2, graphMapping.getVertexCorrespondence(3, true).doubleValue(), 0);
        assertEquals(mappingOfVertex4, graphMapping.getVertexCorrespondence(5, true).doubleValue(), 0);
        assertEquals(mappingOfVertex4, graphMapping.getVertexCorrespondence(6, true).doubleValue(), 0);
        assertEquals(mappingOfVertex4, graphMapping.getVertexCorrespondence(7, true).doubleValue(), 0);
    }
}