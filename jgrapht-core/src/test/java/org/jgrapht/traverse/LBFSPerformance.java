package org.jgrapht.traverse;

import org.jgrapht.Graph;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.util.SupplierUtil;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LBFSPerformance {

    @Test
    public void perfomance() {
        int n = 3000;
        GnpRandomGraphGenerator<Integer, DefaultEdge> generator = new GnpRandomGraphGenerator<>(n, 0.5);
        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
        generator.generateGraph(graph);

        List<Integer> list = new ArrayList<>(graph.vertexSet().size());

        Instant start = Instant.now();
        new LBFSIteratorOld<>(graph).forEachRemaining(list::add);
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("LBFS-Old" + graph.edgeSet().size() + ": " + timeElapsed);

        list.clear();
         start = Instant.now();
        new LexBreadthFirstIterator<>(graph).forEachRemaining(list::add);
         finish = Instant.now();
         timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("LBFS-New" + graph.edgeSet().size() + ": " + timeElapsed);

    }
}