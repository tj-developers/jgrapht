package org.jgrapht.graph.interval;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.SupplierUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class IntervalGraphMappingTest {

    private IntervalGraphMapping<IntervalVertexInterface<Integer, Integer>, DefaultEdge, Integer, Integer> intervalGraphMapping;

    @Test
    public void addVertex() {
        Interval<Integer> interval1 = new Interval<>(3, 7);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);

        ArrayList<IntervalVertexInterface<Integer, Integer>> vertices = new ArrayList<>();
        vertices.add(vertex1);

        intervalGraphMapping = new IntervalGraphMapping<>(
                vertices, null, SupplierUtil.createDefaultEdgeSupplier(), false
        );

        Interval<Integer> interval2 = new Interval<>(4, 309);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);

        intervalGraphMapping.addVertex(vertex2);
    }

    @Test
    public void removeVertex() {
        Interval<Integer> interval1 = new Interval<>(3, 7);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);

        Interval<Integer> interval2 = new Interval<>(2, 49);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);

        ArrayList<IntervalVertexInterface<Integer, Integer>> vertices = new ArrayList<>();
        vertices.add(vertex1);
        vertices.add(vertex2);

        intervalGraphMapping = new IntervalGraphMapping<>(
                vertices, null, SupplierUtil.createDefaultEdgeSupplier(), false
        );

        intervalGraphMapping.removeVertex(vertex1);
        assertFalse(intervalGraphMapping.getGraph().containsVertex(vertex1));
    }
}