package org.jgrapht.alg.spanning;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.CapacitatedSpanningTreeAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class AhujaOrlinSharmaCapacitatedMinimumSpanningTreeTest {

    /**
     * This example is presented here: http://www.pitt.edu/~dtipper/2110/CMST_example.pdf
     */
    @Test
    public void testInstance1() {
        Graph<Integer, DefaultWeightedEdge> graph = new DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        for(int i = 0; i < 7; ++i) {
            graph.addVertex(i);
        }
        graph.setEdgeWeight(graph.addEdge(0, 1), 1);
        graph.setEdgeWeight(graph.addEdge(0, 2), 1);
        graph.setEdgeWeight(graph.addEdge(0, 3), 1);
        graph.setEdgeWeight(graph.addEdge(1, 4), 1);
        graph.setEdgeWeight(graph.addEdge(1, 5), 2);
        graph.setEdgeWeight(graph.addEdge(2, 4), 1);
        graph.setEdgeWeight(graph.addEdge(2, 5), 2);
        graph.setEdgeWeight(graph.addEdge(3, 6), 1);

        Map<Integer, Double> weights = new HashMap<>();
        weights.put(1, 1.0);
        weights.put(2, 1.0);
        weights.put(3, 1.0);
        weights.put(4, 1.0);
        weights.put(5, 1.0);
        weights.put(6, 1.0);

        Map<Integer, Integer> labels = new HashMap<>();
        labels.put(1, 0);
        labels.put(2, 1);
        labels.put(3, 2);
        labels.put(4, 0);
        labels.put(5, 1);
        labels.put(6, 2);

        Map<Integer, Pair<Set<Integer>, Double>> partition = new HashMap<>();
        partition.put(0, Pair.of(new HashSet<>(Arrays.asList(1, 4)), 2.0));
        partition.put(1, Pair.of(new HashSet<>(Arrays.asList(2, 5)), 2.0));
        partition.put(2, Pair.of(new HashSet<>(Arrays.asList(3, 6)), 2.0));

        Set<DefaultWeightedEdge> edges = new HashSet<>();
        edges.add(graph.getEdge(0, 1));
        edges.add(graph.getEdge(0, 2));
        edges.add(graph.getEdge(0, 3));
        edges.add(graph.getEdge(1, 4));
        edges.add(graph.getEdge(2, 5));
        edges.add(graph.getEdge(3, 6));

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> initialSolution =
                new CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTreeImpl<>(0, 2.0, weights, labels, partition, edges, 8);

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> cmst
                = new AhujaOrlinSharmaCapacitatedMinimumSpanningTree<>(initialSolution, graph, 0, 2.0, weights, 2).getCapacitatedSpanningTree();

        assertNotNull(cmst);
        assertEquals(6.0, cmst.getWeight(), 0.0000001);
        assertEquals(0, cmst.getRoot(), 0);
        assertEquals(2.0, cmst.getCapacity(), 0.00000001);
        assertEquals(weights, cmst.getDemands());

        assertEquals(Pair.of(new HashSet<>(Arrays.asList(1, 5)), 2.0), cmst.getPartition().get(cmst.getLabels().get(1)));
        assertEquals(Pair.of(new HashSet<>(Arrays.asList(2, 4)), 2.0), cmst.getPartition().get(cmst.getLabels().get(2)));
        assertEquals(Pair.of(new HashSet<>(Arrays.asList(3, 6)), 2.0), cmst.getPartition().get(cmst.getLabels().get(3)));

        assertEquals(cmst.getLabels().get(1), cmst.getLabels().get(5), 0);
        assertEquals(cmst.getLabels().get(2), cmst.getLabels().get(4), 0);
        assertEquals(cmst.getLabels().get(3), cmst.getLabels().get(6), 0);
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(2));
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(3));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(3));

        for(DefaultWeightedEdge e : cmst.getEdges()) {
            assertTrue(
                    e == graph.getEdge(0, 1)
                            || e == graph.getEdge(0, 2)
                            || e == graph.getEdge(0, 3)
                            || e == graph.getEdge(1, 5)
                            || e == graph.getEdge(2, 4)
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

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> cmst =
                new AhujaOrlinSharmaCapacitatedMinimumSpanningTree<>(graph, 0, 4, weights, 7, 1).getCapacitatedSpanningTree();

        assertNotNull(cmst);
        assertEquals(14.0, cmst.getWeight(), 0.0000001);
        assertEquals(0, cmst.getRoot(), 0);
        assertEquals(4.0, cmst.getCapacity(), 0.00000001);
        assertEquals(weights, cmst.getDemands());

        assertEquals(cmst.getPartition().get(cmst.getLabels().get(1)), Pair.of(new HashSet<>(Arrays.asList(1, 5)), 4.0));
        assertEquals(cmst.getPartition().get(cmst.getLabels().get(2)), Pair.of(new HashSet<>(Arrays.asList(2, 3)), 3.0));
        assertEquals(cmst.getPartition().get(cmst.getLabels().get(4)), Pair.of(new HashSet<>(Collections.singletonList(4)), 3.0));

        assertEquals(cmst.getLabels().get(1), cmst.getLabels().get(5), 0);
        assertEquals(cmst.getLabels().get(2), cmst.getLabels().get(3), 0);
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(3));
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(4));
        assertNotEquals(cmst.getLabels().get(3), cmst.getLabels().get(4));

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
