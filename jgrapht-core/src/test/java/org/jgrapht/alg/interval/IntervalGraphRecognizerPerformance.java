package org.jgrapht.alg.interval;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

import static org.junit.Assert.*;

public class IntervalGraphRecognizerPerformance {
    @Test
    public void trivialTest() {
        for (int n = 1; n < 10000; n *= 2) {
            GnpRandomGraphGenerator<Integer, DefaultEdge> generator = new GnpRandomGraphGenerator<>(n, 0.5);
            Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
            generator.generateGraph(graph);

            Instant start = Instant.now();
            new IntervalGraphRecognizer<>(graph).isIntervalGraph();
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("" + graph.edgeSet().size() + ": " + timeElapsed);
        }
    }

    @Test
    public void test() {
        int n = 2000;
        GnpRandomGraphGenerator<Integer, DefaultEdge> generator = new GnpRandomGraphGenerator<>(n, 0.5);
        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
        generator.generateGraph(graph);

        Instant start = Instant.now();
        new IntervalGraphRecognizer<>(graph).isIntervalGraph();
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("" + graph.edgeSet().size() + ": " + timeElapsed);
    }

    @Test
    public void circular() {
        for (int n = 2; n < 100000; n *= 2) {
            GraphBuilder<Integer, DefaultEdge, ? extends SimpleGraph<Integer, DefaultEdge>> builder
                    = SimpleGraph.createBuilder(DefaultEdge.class);

            for(int i = 0; i < n; i++) {
                builder.addEdge(i, (i + 1) % n);
            }

            Graph<Integer, DefaultEdge> graph = builder.build();

            Instant start = Instant.now();
            new IntervalGraphRecognizer<>(graph).isIntervalGraph();
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("" + graph.edgeSet().size() + ": " + timeElapsed);
        }
    }

    @Test
    public void complete() {
        for (int n = 2; n < 2000; n *= 2) {
            CompleteGraphGenerator<Integer,DefaultEdge> cgg =
                    new CompleteGraphGenerator<Integer,DefaultEdge>(n);
            Graph<Integer,DefaultEdge> graph =
                    new SimpleGraph<>(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
            cgg.generateGraph(graph);


            Instant start = Instant.now();
            new IntervalGraphRecognizer<>(graph).isIntervalGraph();
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("" + graph.edgeSet().size() + ": " + timeElapsed);
        }
    }

    @Test
    public void irgendwasHolland() {
        for (int n = 6; n < 200000; n *= 2) {
            GraphBuilder<Integer, DefaultEdge, ? extends SimpleGraph<Integer, DefaultEdge>> builder
                    = SimpleGraph.createBuilder(DefaultEdge.class);

            //asteroidal triple: 1,3,5
            builder.addEdge(0, 1);
            builder.addEdge(1, 2);
            builder.addEdge(2, 3);
            builder.addEdge(3, 4);
            builder.addEdge(4, 5);
            builder.addEdge(5, n);

            for (int i=5; i<n; i++) {
                builder.addEdge(i, i+1);
                builder.addEdge(i, 2);
                builder.addEdge(i, 4);
            }
            builder.addEdge(n, 2);
            builder.addEdge(n, 4);

            Graph<Integer, DefaultEdge> graph = builder.build();

            Instant start = Instant.now();
            new IntervalGraphRecognizer<>(graph).isIntervalGraph();
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("" + graph.edgeSet().size() + ": " + timeElapsed);
        }
    }

    @Test
    public void testdijkstra() {
        for (int n = 10; n < 10000; n *= 2) {
            GnpRandomGraphGenerator<Integer, DefaultEdge> generator = new GnpRandomGraphGenerator<>(n, 0.5);
            Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
            generator.generateGraph(graph);

            Instant start = Instant.now();
            new DijkstraShortestPath<>(graph).getPath(1, n/2).getEdgeList();
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("" + graph.edgeSet().size() + ": " + timeElapsed);
        }
    }
}