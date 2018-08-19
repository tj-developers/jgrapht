package org.jgrapht.alg.interval;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class KorteMoehringIntervalGraphRecognizerTest {

    @Test
    public void testEmptyGraphInstance() {
        Graph<Integer, DefaultEdge> emptyGraph = new SimpleGraph<>(DefaultEdge.class);
        KorteMoehringIntervalGraphRecognizer<Integer, DefaultEdge> recognizer = new KorteMoehringIntervalGraphRecognizer<>(emptyGraph);
        assertTrue(recognizer.isIntervalGraph());
    }

    @Test
    public void testPositiveGraphInstance() {

    }

    @Test
    public void testNegativeGraphInstance() {

    }

}
