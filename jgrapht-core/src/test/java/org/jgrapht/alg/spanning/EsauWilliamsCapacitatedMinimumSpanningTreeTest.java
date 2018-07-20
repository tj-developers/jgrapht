package org.jgrapht.alg.spanning;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EsauWilliamsCapacitatedMinimumSpanningTreeTest {

    /**
     * This example is presented here: http://www.pitt.edu/~dtipper/2110/CMST_example.pdf
     */
    @Test
    public void testInstance1() {
        Graph<Integer, DefaultWeightedEdge> graph = new DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        for(int i = 0; i < 7; ++i) {
            graph.addVertex(i);
        }
        for(int i = 0; i < 7; ++i) {
            for(int j = i + 1; j < 7; ++j) {
                graph.addEdge(i, j);
            }
        }
        graph.setEdgeWeight(graph.getEdge(0, 1), 5);
        graph.setEdgeWeight(graph.getEdge(0, 2), 6);
        graph.setEdgeWeight(graph.getEdge(0, 3), 9);
        graph.setEdgeWeight(graph.getEdge(0, 4), 10);
        graph.setEdgeWeight(graph.getEdge(0, 5), 11);
        graph.setEdgeWeight(graph.getEdge(0, 6), 15);
        graph.setEdgeWeight(graph.getEdge(1, 2), 9);
        graph.setEdgeWeight(graph.getEdge(1, 3), 6);
        graph.setEdgeWeight(graph.getEdge(1, 4), 6);
        graph.setEdgeWeight(graph.getEdge(1, 5), 8);
        graph.setEdgeWeight(graph.getEdge(1, 6), 17);
        graph.setEdgeWeight(graph.getEdge(2, 3), 7);
        graph.setEdgeWeight(graph.getEdge(2, 4), 9);
        graph.setEdgeWeight(graph.getEdge(2, 5), 8);
        graph.setEdgeWeight(graph.getEdge(2, 6), 12);
        graph.setEdgeWeight(graph.getEdge(3, 4), 10);
        graph.setEdgeWeight(graph.getEdge(3, 5), 5);
        graph.setEdgeWeight(graph.getEdge(3, 6), 11);
        graph.setEdgeWeight(graph.getEdge(4, 5), 14);
        graph.setEdgeWeight(graph.getEdge(4, 6), 9);
        graph.setEdgeWeight(graph.getEdge(5, 6), 8);

        Map<Integer, Double> weights = new HashMap<>();
        weights.put(1, 1.0);
        weights.put(2, 1.0);
        weights.put(3, 2.0);
        weights.put(4, 1.0);
        weights.put(5, 1.0);
        weights.put(6, 1.0);

        SpanningTreeAlgorithm.SpanningTree<DefaultWeightedEdge> cmst = new EsauWilliamsCapacitatedMinimumSpanningTree<>(graph, 0, 3, weights, 1).getSpanningTree();

        assertNotNull(cmst);
        assertEquals(42.0, cmst.getWeight(), 0.0000001);
        for(DefaultWeightedEdge e : cmst.getEdges()) {
            assertTrue(
                    e == graph.getEdge(0, 1)
                || e == graph.getEdge(0, 2)
                || e == graph.getEdge(0, 3)
                || e == graph.getEdge(1, 4)
                || e == graph.getEdge(2, 5)
                || e == graph.getEdge(5, 6)
            );
        }
    }

    @Test
    public void testInstance2() {
        Graph<Integer, DefaultWeightedEdge> graph = new DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        for(int i = 0; i < 6; ++i) {
            graph.addVertex(i);
        }
        for(int i = 0; i < 6; ++i) {
            for(int j = i + 1; j < 6; ++j) {
                graph.addEdge(i, j);
            }
        }
        graph.setEdgeWeight(graph.getEdge(0, 1), 7);
        graph.setEdgeWeight(graph.getEdge(0, 2), 5);
        graph.setEdgeWeight(graph.getEdge(0, 3), 1);
        graph.setEdgeWeight(graph.getEdge(0, 4), 2);
        graph.setEdgeWeight(graph.getEdge(0, 5), 8);
        graph.setEdgeWeight(graph.getEdge(1, 2), 8);
        graph.setEdgeWeight(graph.getEdge(1, 3), 5);
        graph.setEdgeWeight(graph.getEdge(1, 4), 2);
        graph.setEdgeWeight(graph.getEdge(1, 5), 2);
        graph.setEdgeWeight(graph.getEdge(2, 3), 2);
        graph.setEdgeWeight(graph.getEdge(2, 4), 5);
        graph.setEdgeWeight(graph.getEdge(2, 5), 6);
        graph.setEdgeWeight(graph.getEdge(3, 4), 9);
        graph.setEdgeWeight(graph.getEdge(3, 5), 5);
        graph.setEdgeWeight(graph.getEdge(4, 5), 1);

        Map<Integer, Double> weights = new HashMap<>();
        weights.put(1, 2.0);
        weights.put(2, 1.0);
        weights.put(3, 2.0);
        weights.put(4, 3.0);
        weights.put(5, 2.0);

        SpanningTreeAlgorithm.SpanningTree<DefaultWeightedEdge> cmst = new EsauWilliamsCapacitatedMinimumSpanningTree<>(graph, 0, 4, weights, 1).getSpanningTree();

        assertNotNull(cmst);
        assertEquals(14.0, cmst.getWeight(), 0.0000001);
        for(DefaultWeightedEdge e : cmst.getEdges()) {
            assertTrue(
                    e == graph.getEdge(0, 1)
                            || e == graph.getEdge(0, 3)
                            || e == graph.getEdge(0, 4)
                            || e == graph.getEdge(1, 5)
                            || e == graph.getEdge(3, 2)
            );
        }
    }
}
