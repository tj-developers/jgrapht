package org.jgrapht.alg.interval;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for KorteMoehringIntervalGraphRecognizer
 *
 * @author Jiong Fu (magnificent_tony)
 */
public class KorteMoehringIntervalGraphRecognizerTest {

    @Test
    public void testEmptyGraphInstance() {
        Graph<Integer, DefaultEdge> emptyGraph = new SimpleGraph<>(DefaultEdge.class);
        KorteMoehringIntervalGraphRecognizer<Integer, DefaultEdge> recognizer = new KorteMoehringIntervalGraphRecognizer<>(emptyGraph);
        assertTrue(recognizer.isIntervalGraph());
    }

    @Test
    public void testPositiveGraphInstance() {
        GraphBuilder<Character, DefaultEdge, ? extends SimpleGraph<Character, DefaultEdge>> builder
                = SimpleGraph.createBuilder(DefaultEdge.class);
        builder.addEdge('A', 'B');
        builder.addEdge('A', 'D');
        builder.addEdge('B', 'C');
        builder.addEdge('B', 'D');
        builder.addEdge('C', 'D');
        KorteMoehringIntervalGraphRecognizer<Character, DefaultEdge> recognizer = new KorteMoehringIntervalGraphRecognizer<>(builder.build());
        System.err.println(recognizer.isIntervalGraph());
    }

    @Test
    public void testNegativeGraphInstance() {
        GraphBuilder<Character, DefaultEdge, ? extends SimpleGraph<Character, DefaultEdge>> builder
                = SimpleGraph.createBuilder(DefaultEdge.class);
        builder.addEdge('A', 'B');
        builder.addEdge('A', 'E');
        builder.addEdge('B', 'C');
        builder.addEdge('B', 'D');
        builder.addEdge('B', 'E');
        builder.addEdge('C', 'D');
        builder.addEdge('D', 'E');
        builder.addEdge('D', 'F');
        builder.addEdge('E', 'F');
        KorteMoehringIntervalGraphRecognizer<Character, DefaultEdge> recognizer = new KorteMoehringIntervalGraphRecognizer<>(builder.build());
        System.err.println(recognizer.isIntervalGraph());
    }

}
