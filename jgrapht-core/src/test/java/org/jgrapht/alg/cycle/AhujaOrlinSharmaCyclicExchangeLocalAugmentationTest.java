package org.jgrapht.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link AhujaOrlinSharmaCyclicExchangeLocalAugmentation}.
 *
 * @author Christoph Grüne
 */
public class AhujaOrlinSharmaCyclicExchangeLocalAugmentationTest {

    @Test
    public void testImprovementGraph1() {
        LinkedList<Integer> cycle = new LinkedList<>();
        cycle.add(0);
        cycle.add(4);
        cycle.add(0);


        Map<Integer, Integer> labels = new HashMap<>();

        for(int i = 0; i < 3; ++i) {
            labels.put(i, 1);
            labels.put(i + 3, 2);
            labels.put(i + 6, 3);
        }

        Graph<Integer, DefaultEdge> graph = generateImprovementGraphForPartitionProblem(labels, 1);

        graph.setEdgeWeight(graph.getEdge(0, 4), -5);
        graph.setEdgeWeight(graph.getEdge(4, 8), -5);

        int lengthBound = 2;

        LabeledPath<Integer> calculatedCycle = new AhujaOrlinSharmaCyclicExchangeLocalAugmentation<>(graph, lengthBound, labels).getLocalAugmentationCycle();

        assertEquals(cycle, calculatedCycle.getVertices());
        assertEquals(-4, calculatedCycle.getCost(), 0.0000001);

    }

    @Test
    public void testImprovementGraph2() {
        LinkedList<Integer> cycle = new LinkedList<>();
        for(int i = 0; i <= 20; i += 5) {
            cycle.add(i);
        }
        cycle.add(0);


        Map<Integer, Integer> labels = new HashMap<>();

        for(int i = 0; i < 5; ++i) {
            labels.put(i, 1);
            labels.put(i + 5, 2);
            labels.put(i + 10, 3);
            labels.put(i +  15, 4);
            labels.put(i + 20, 5);
        }

        Graph<Integer, DefaultEdge> graph = generateImprovementGraphForPartitionProblem(labels, 4.9);

        graph.setEdgeWeight(graph.getEdge(0, 5), -1);
        graph.setEdgeWeight(graph.getEdge(5, 10), -1);
        graph.setEdgeWeight(graph.getEdge(10, 15), -1);
        graph.setEdgeWeight(graph.getEdge(15, 20), -1);
        graph.setEdgeWeight(graph.getEdge(20, 0), -1);

        int lengthBound = 5;

        LabeledPath<Integer> calculatedCycle = new AhujaOrlinSharmaCyclicExchangeLocalAugmentation<>(graph, lengthBound, labels).getLocalAugmentationCycle();

        assertEquals(cycle, calculatedCycle.getVertices());
        assertEquals(-5, calculatedCycle.getCost(), 0.0000001);

    }

    @Test
    public void testImprovementGraph3() {
        LinkedList<Integer> cycle = new LinkedList<>();
        for(int i = 0; i <= 20; i += 5) {
            cycle.add(i);
        }
        cycle.add(0);


        Map<Integer, Integer> labels = new HashMap<>();

        for(int i = 0; i < 5; ++i) {
            labels.put(i, 1);
            labels.put(i + 5, 2);
            labels.put(i + 10, 3);
            labels.put(i +  15, 4);
            labels.put(i + 20, 5);
        }

        Graph<Integer, DefaultEdge> graph = generateImprovementGraphForPartitionProblem(labels, 3.9);

        graph.setEdgeWeight(graph.getEdge(0, 5), -1);
        graph.setEdgeWeight(graph.getEdge(5, 10), -1);
        graph.setEdgeWeight(graph.getEdge(10, 15), -1);
        graph.setEdgeWeight(graph.getEdge(15, 20), -1);

        int lengthBound = 5;

        LabeledPath<Integer> calculatedCycle = new AhujaOrlinSharmaCyclicExchangeLocalAugmentation<>(graph, lengthBound, labels).getLocalAugmentationCycle();

        assertEquals(cycle, calculatedCycle.getVertices());
        assertEquals(3.9 - 4, calculatedCycle.getCost(), 0.0000001);

    }

    private Graph<Integer, DefaultEdge> generateImprovementGraphForPartitionProblem(Map<Integer, Integer> labels, double initialWeight) {
        Graph<Integer, DefaultEdge> graph = new DefaultDirectedGraph<>(new ClassBasedEdgeFactory<>(DefaultEdge.class), true);

        for(Integer v1 : labels.keySet()) {
            graph.addVertex(v1);
        }

        for(Integer v1 : labels.keySet()) {
            for(Integer v2 : labels.keySet()) {
                if(!labels.get(v1).equals(labels.get(v2))) {
                    graph.addEdge(v1, v2);
                    graph.setEdgeWeight(graph.getEdge(v1, v2), initialWeight);
                    graph.addEdge(v2, v1);
                    graph.setEdgeWeight(graph.getEdge(v1, v2), initialWeight);
                }
            }
        }

        return graph;
    }
}
