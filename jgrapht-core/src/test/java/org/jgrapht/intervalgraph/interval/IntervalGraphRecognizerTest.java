package org.jgrapht.intervalgraph.interval;

import org.jgrapht.Graph;
import org.jgrapht.alg.intervalgraph.IntervalGraphRecognizer;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.intervalgraph.IntervalGraph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntervalGraphRecognizerTest {

    @Test
    public void testEmptyGraph() {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        assertTrue(IntervalGraphRecognizer.isIntervalGraph(g));
    }

    @Test
    public void testForbiddenSubgraphBiclawWithAT() {
        GraphBuilder<Integer, DefaultEdge, ? extends SimpleGraph<Integer, DefaultEdge>> builder = new SimpleGraph<>((sourceVertex, targetVertex) -> new DefaultEdge()).createBuilder(DefaultEdge.class);

        builder.addEdge(0, 1);
        builder.addEdge(0, 2);
        builder.addEdge(0, 3);

        builder.addEdge(1, 4);
        builder.addEdge(2, 5);
        builder.addEdge(3, 6);

        assertFalse(IntervalGraphRecognizer.isIntervalGraph(builder.build()));
    }

    @Test
    public void testForbiddenSubgraphLekkerkerkerBoland() {


        GraphBuilder<Integer, DefaultEdge, ? extends SimpleGraph<Integer, DefaultEdge>> builder = new SimpleGraph<>((sourceVertex, targetVertex) -> new DefaultEdge()).createBuilder(DefaultEdge.class);

        builder.addEdge(0, 1);
        builder.addEdge(0, 2);
        builder.addEdge(0, 3);
        builder.addEdge(0, 4);
        builder.addEdge(0, 5);

        builder.addEdge(1, 2);
        builder.addEdge(2, 3);
        builder.addEdge(3, 4);
        builder.addEdge(4, 5);

        builder.addEdge(3, 6);

        assertFalse(IntervalGraphRecognizer.isIntervalGraph(builder.build()));
    }

    public void testForbiddenSubgraphLekkerkerkerBolandFamily(int n) {
        GraphBuilder<Integer, DefaultEdge, ? extends SimpleGraph<Integer, DefaultEdge>> builder = new SimpleGraph<>((sourceVertex, targetVertex) -> new DefaultEdge()).createBuilder(DefaultEdge.class);

        builder.addEdge(0, 1);

        for(int i = 3; i < n; i++) {
            builder.addEdge(1, i);
            builder.addEdge(i - 1, i);
        }

        builder.addEdge(n - 1, n);

        assertFalse(IntervalGraphRecognizer.isIntervalGraph(builder.build()));
    }

    @Test
    public void testForbiddenSubgraphLekkerkerkerBolandFamily() {
        for(int n = 7; n < 20; n++) {
            testForbiddenSubgraphLekkerkerkerBolandFamily(n);
        }
    }

    public boolean isCnAnIntervalGraph(int n) {
        GraphBuilder<Integer, DefaultEdge, ? extends SimpleGraph<Integer, DefaultEdge>> builder = new SimpleGraph<>((sourceVertex, targetVertex) -> new DefaultEdge()).createBuilder(DefaultEdge.class);

        for(int i = 0; i < n; i++) {
            builder.addEdge(i, (i + 1) % n);
        }

        return IntervalGraphRecognizer.isIntervalGraph(builder.build());
    }

    @Test
    public void testForbiddenSubgraphCn() {
        for(int n = 2; n < 20; n++) {
            assertEquals("Testing C_" + n, n < 4, isCnAnIntervalGraph(n));
        }
    }
}
