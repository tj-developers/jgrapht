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
     * a simple cyclic exchange
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
        graph.setEdgeWeight(graph.addEdge(1, 4), 2);
        graph.setEdgeWeight(graph.addEdge(1, 5), 1);
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
                            || e == graph.getEdge(3, 6)
            );
        }
    }

    /**
     * in this example, the initial solution should not be changed
     */
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

    /**
     * a double cyclic exchange
     */
    @Test
    public void testInstance3() {
        Graph<Integer, DefaultWeightedEdge> graph = new DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        for(int i = 0; i < 6; ++i) {
            graph.addVertex(i);
        }
        graph.setEdgeWeight(graph.addEdge(0, 1), 1);
        graph.setEdgeWeight(graph.addEdge(0, 2), 1);
        graph.setEdgeWeight(graph.addEdge(0, 3), 1);
        graph.setEdgeWeight(graph.addEdge(0, 4), 1);
        graph.setEdgeWeight(graph.addEdge(1, 5), 1);
        graph.setEdgeWeight(graph.addEdge(2, 5), 3);
        graph.setEdgeWeight(graph.addEdge(3, 5), 2);
        graph.setEdgeWeight(graph.addEdge(4, 5), 0);

        Map<Integer, Double> weights = new HashMap<>();
        weights.put(1, 1.0);
        weights.put(2, 1.0);
        weights.put(3, 1.0);
        weights.put(4, 2.0);
        weights.put(5, 1.0);

        Map<Integer, Integer> labels = new HashMap<>();
        labels.put(1, 0);
        labels.put(2, 1);
        labels.put(3, 2);
        labels.put(4, 3);
        labels.put(5, 1);

        Map<Integer, Pair<Set<Integer>, Double>> partition = new HashMap<>();
        partition.put(0, Pair.of(new HashSet<>(Collections.singletonList(1)), 1.0));
        partition.put(1, Pair.of(new HashSet<>(Arrays.asList(2, 5)), 2.0));
        partition.put(2, Pair.of(new HashSet<>(Collections.singletonList(3)), 1.0));
        partition.put(3, Pair.of(new HashSet<>(Collections.singletonList(4)), 2.0));

        Set<DefaultWeightedEdge> edges = new HashSet<>();
        edges.add(graph.getEdge(0, 1));
        edges.add(graph.getEdge(0, 2));
        edges.add(graph.getEdge(0, 3));
        edges.add(graph.getEdge(0, 4));
        edges.add(graph.getEdge(2, 5));

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> initialSolution =
                new CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTreeImpl<>(0, 2.0, weights, labels, partition, edges, 7);

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> cmst
                = new AhujaOrlinSharmaCapacitatedMinimumSpanningTree<>(initialSolution, graph, 0, 2.0, weights, 2).getCapacitatedSpanningTree();

        assertNotNull(cmst);
        assertEquals(5.0, cmst.getWeight(), 0.0000001);
        assertEquals(0, cmst.getRoot(), 0);
        assertEquals(2.0, cmst.getCapacity(), 0.00000001);
        assertEquals(weights, cmst.getDemands());

        assertEquals(Pair.of(new HashSet<>(Arrays.asList(1, 5)), 2.0), cmst.getPartition().get(cmst.getLabels().get(1)));
        assertEquals(Pair.of(new HashSet<>(Collections.singletonList(2)), 1.0), cmst.getPartition().get(cmst.getLabels().get(2)));
        assertEquals(Pair.of(new HashSet<>(Collections.singletonList(3)), 1.0), cmst.getPartition().get(cmst.getLabels().get(3)));
        assertEquals(Pair.of(new HashSet<>(Collections.singletonList(4)), 2.0), cmst.getPartition().get(cmst.getLabels().get(4)));

        assertEquals(cmst.getLabels().get(1), cmst.getLabels().get(5), 0);
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(2));
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(3));
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(4));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(3));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(4));
        assertNotEquals(cmst.getLabels().get(3), cmst.getLabels().get(4));

        for(DefaultWeightedEdge e : cmst.getEdges()) {
            assertTrue(
                    e == graph.getEdge(0, 1)
                            || e == graph.getEdge(0, 2)
                            || e == graph.getEdge(0, 3)
                            || e == graph.getEdge(0, 4)
                            || e == graph.getEdge(1, 5)
            );
        }
    }

    /**
     * a simple path exchange
     */
    @Test
    public void testInstance4() {
        Graph<Integer, DefaultWeightedEdge> graph = new DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        for(int i = 0; i < 8; ++i) {
            graph.addVertex(i);
        }
        graph.setEdgeWeight(graph.addEdge(0, 1), 1);
        graph.setEdgeWeight(graph.addEdge(0, 2), 1);
        graph.setEdgeWeight(graph.addEdge(0, 3), 1);
        graph.setEdgeWeight(graph.addEdge(1, 4), 1);
        graph.setEdgeWeight(graph.addEdge(2, 5), 1);
        graph.setEdgeWeight(graph.addEdge(3, 6), 1);
        graph.setEdgeWeight(graph.addEdge(4, 7), 1);
        graph.setEdgeWeight(graph.addEdge(5, 7), 3);
        graph.setEdgeWeight(graph.addEdge(6, 7), 2);

        Map<Integer, Double> weights = new HashMap<>();
        for(int i = 1; i < 8; ++i) {
            weights.put(i, 1.0);
        }

        Map<Integer, Integer> labels = new HashMap<>();
        labels.put(1, 0);
        labels.put(2, 1);
        labels.put(3, 2);
        labels.put(4, 0);
        labels.put(5, 1);
        labels.put(6, 2);
        labels.put(7, 1);

        Map<Integer, Pair<Set<Integer>, Double>> partition = new HashMap<>();
        partition.put(0, Pair.of(new HashSet<>(Arrays.asList(1, 4)), 2.0));
        partition.put(1, Pair.of(new HashSet<>(Arrays.asList(2, 5, 7)), 3.0));
        partition.put(2, Pair.of(new HashSet<>(Arrays.asList(3, 6)), 2.0));

        Set<DefaultWeightedEdge> edges = new HashSet<>();
        edges.add(graph.getEdge(0, 1));
        edges.add(graph.getEdge(0, 2));
        edges.add(graph.getEdge(0, 3));
        edges.add(graph.getEdge(1, 4));
        edges.add(graph.getEdge(2, 5));
        edges.add(graph.getEdge(3, 6));
        edges.add(graph.getEdge(5, 7));

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> initialSolution =
                new CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTreeImpl<>(0, 3.0, weights, labels, partition, edges, 7);

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> cmst
                = new AhujaOrlinSharmaCapacitatedMinimumSpanningTree<>(initialSolution, graph, 0, 3.0, weights, 3).getCapacitatedSpanningTree();

        assertNotNull(cmst);
        assertEquals(7.0, cmst.getWeight(), 0.0000001);
        assertEquals(0, cmst.getRoot(), 0);
        assertEquals(3.0, cmst.getCapacity(), 0.00000001);
        assertEquals(weights, cmst.getDemands());

        assertEquals(Pair.of(new HashSet<>(Arrays.asList(1, 4, 7)), 3.0), cmst.getPartition().get(cmst.getLabels().get(1)));
        assertEquals(Pair.of(new HashSet<>(Arrays.asList(2, 5)), 2.0), cmst.getPartition().get(cmst.getLabels().get(2)));
        assertEquals(Pair.of(new HashSet<>(Arrays.asList(3, 6)), 2.0), cmst.getPartition().get(cmst.getLabels().get(3)));

        assertEquals(cmst.getLabels().get(1), cmst.getLabels().get(4), 0);
        assertEquals(cmst.getLabels().get(1), cmst.getLabels().get(7), 0);
        assertEquals(cmst.getLabels().get(2), cmst.getLabels().get(5), 0);
        assertEquals(cmst.getLabels().get(3), cmst.getLabels().get(6), 0);
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(2));
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(3));
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(5));
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(6));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(3));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(4));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(6));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(7));
        assertNotEquals(cmst.getLabels().get(3), cmst.getLabels().get(4));
        assertNotEquals(cmst.getLabels().get(3), cmst.getLabels().get(7));
        assertNotEquals(cmst.getLabels().get(3), cmst.getLabels().get(5));

        for(DefaultWeightedEdge e : cmst.getEdges()) {
            assertTrue(
                    e == graph.getEdge(0, 1)
                            || e == graph.getEdge(0, 2)
                            || e == graph.getEdge(0, 3)
                            || e == graph.getEdge(1, 4)
                            || e == graph.getEdge(2, 5)
                            || e == graph.getEdge(3, 6)
                            || e == graph.getEdge(4, 7)
            );
        }
    }

    /**
     * a simple subtree exchange
     */
    @Test
    public void testInstance5() {
        Graph<Integer, DefaultWeightedEdge> graph = new DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        for(int i = 0; i < 9; ++i) {
            graph.addVertex(i);
        }
        graph.setEdgeWeight(graph.addEdge(0, 1), 1);
        graph.setEdgeWeight(graph.addEdge(0, 2), 1);
        graph.setEdgeWeight(graph.addEdge(0, 3), 1);
        graph.setEdgeWeight(graph.addEdge(1, 4), 1);
        graph.setEdgeWeight(graph.addEdge(2, 5), 1);
        graph.setEdgeWeight(graph.addEdge(3, 6), 1);
        graph.setEdgeWeight(graph.addEdge(4, 7), 1);
        graph.setEdgeWeight(graph.addEdge(5, 7), 3);
        graph.setEdgeWeight(graph.addEdge(6, 7), 2);
        graph.setEdgeWeight(graph.addEdge(7, 8), 1);

        Map<Integer, Double> weights = new HashMap<>();
        for(int i = 1; i < 9; ++i) {
            weights.put(i, 1.0);
        }

        Map<Integer, Integer> labels = new HashMap<>();
        labels.put(1, 0);
        labels.put(2, 1);
        labels.put(3, 2);
        labels.put(4, 0);
        labels.put(5, 1);
        labels.put(6, 2);
        labels.put(7, 1);
        labels.put(8, 1);

        Map<Integer, Pair<Set<Integer>, Double>> partition = new HashMap<>();
        partition.put(0, Pair.of(new HashSet<>(Arrays.asList(1, 4)), 2.0));
        partition.put(1, Pair.of(new HashSet<>(Arrays.asList(2, 5, 7, 8)), 4.0));
        partition.put(2, Pair.of(new HashSet<>(Arrays.asList(3, 6)), 2.0));

        Set<DefaultWeightedEdge> edges = new HashSet<>();
        edges.add(graph.getEdge(0, 1));
        edges.add(graph.getEdge(0, 2));
        edges.add(graph.getEdge(0, 3));
        edges.add(graph.getEdge(1, 4));
        edges.add(graph.getEdge(2, 5));
        edges.add(graph.getEdge(3, 6));
        edges.add(graph.getEdge(5, 7));
        edges.add(graph.getEdge(7, 8));

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> initialSolution =
                new CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTreeImpl<>(0, 4.0, weights, labels, partition, edges, 8);

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> cmst
                = new AhujaOrlinSharmaCapacitatedMinimumSpanningTree<>(initialSolution, graph, 0, 4.0, weights, 3).getCapacitatedSpanningTree();

        assertNotNull(cmst);
        assertEquals(8.0, cmst.getWeight(), 0.0000001);
        assertEquals(0, cmst.getRoot(), 0);
        assertEquals(4.0, cmst.getCapacity(), 0.00000001);
        assertEquals(weights, cmst.getDemands());

        assertEquals(Pair.of(new HashSet<>(Arrays.asList(1, 4, 7, 8)), 4.0), cmst.getPartition().get(cmst.getLabels().get(1)));
        assertEquals(Pair.of(new HashSet<>(Arrays.asList(2, 5)), 2.0), cmst.getPartition().get(cmst.getLabels().get(2)));
        assertEquals(Pair.of(new HashSet<>(Arrays.asList(3, 6)), 2.0), cmst.getPartition().get(cmst.getLabels().get(3)));

        assertEquals(cmst.getLabels().get(1), cmst.getLabels().get(4), 0);
        assertEquals(cmst.getLabels().get(1), cmst.getLabels().get(7), 0);
        assertEquals(cmst.getLabels().get(1), cmst.getLabels().get(8), 0);
        assertEquals(cmst.getLabels().get(2), cmst.getLabels().get(5), 0);
        assertEquals(cmst.getLabels().get(3), cmst.getLabels().get(6), 0);
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(2));
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(3));
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(5));
        assertNotEquals(cmst.getLabels().get(1), cmst.getLabels().get(6));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(3));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(4));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(6));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(7));
        assertNotEquals(cmst.getLabels().get(2), cmst.getLabels().get(8));
        assertNotEquals(cmst.getLabels().get(3), cmst.getLabels().get(4));
        assertNotEquals(cmst.getLabels().get(3), cmst.getLabels().get(7));
        assertNotEquals(cmst.getLabels().get(3), cmst.getLabels().get(5));
        assertNotEquals(cmst.getLabels().get(3), cmst.getLabels().get(8));

        for(DefaultWeightedEdge e : cmst.getEdges()) {
            assertTrue(
                    e == graph.getEdge(0, 1)
                            || e == graph.getEdge(0, 2)
                            || e == graph.getEdge(0, 3)
                            || e == graph.getEdge(1, 4)
                            || e == graph.getEdge(2, 5)
                            || e == graph.getEdge(3, 6)
                            || e == graph.getEdge(4, 7)
                            || e == graph.getEdge(7, 8)
            );
        }
    }

    /**
     * a more complicate example
     */
    @Test
    public void testInstance6() {
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

        Map<Integer, Integer> labels = new HashMap<>();
        labels.put(1, 0);
        labels.put(2, 1);
        labels.put(3, 2);
        labels.put(4, 3);
        labels.put(5, 4);

        Map<Integer, Pair<Set<Integer>, Double>> partition = new HashMap<>();
        partition.put(0, Pair.of(new HashSet<>(Collections.singletonList(1)), 2.0));
        partition.put(1, Pair.of(new HashSet<>(Collections.singletonList(2)), 1.0));
        partition.put(2, Pair.of(new HashSet<>(Collections.singletonList(3)), 2.0));
        partition.put(3, Pair.of(new HashSet<>(Collections.singletonList(4)), 3.0));
        partition.put(4, Pair.of(new HashSet<>(Collections.singletonList(5)), 2.0));

        Set<DefaultWeightedEdge> edges = new HashSet<>();
        edges.add(graph.getEdge(0, 1));
        edges.add(graph.getEdge(0, 2));
        edges.add(graph.getEdge(0, 3));
        edges.add(graph.getEdge(0, 4));
        edges.add(graph.getEdge(0, 5));

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> initialSolution =
                new CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTreeImpl<>(0, 4.0, weights, labels, partition, edges, 8);

        CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<Integer, DefaultWeightedEdge> cmst =
                new AhujaOrlinSharmaCapacitatedMinimumSpanningTree<>(initialSolution, graph, 0, 4, weights, 7).getCapacitatedSpanningTree();

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
