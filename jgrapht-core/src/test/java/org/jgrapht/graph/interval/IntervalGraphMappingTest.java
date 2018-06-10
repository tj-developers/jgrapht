/*
 * (C) Copyright 2003-2018, by Abdallah Atouani and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.graph.interval;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class IntervalGraphMappingTest {

    private IntervalGraphMapping<IntervalVertexInterface<Integer, Integer>, DefaultEdge, Integer, Integer> intervalGraphMapping;

    @Test
    public void testAddVertex() {
        intervalGraphMapping = new IntervalGraphMapping<>(
                null, SupplierUtil.createDefaultEdgeSupplier(), false
        );

        Interval<Integer> interval1 = new Interval<>(3, 7);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraphMapping.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(4, 309);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraphMapping.addVertex(vertex2);

        assertTrue(intervalGraphMapping.getGraph().vertexSet().contains(vertex2));
        assertTrue(intervalGraphMapping.getGraph().vertexSet().contains(vertex1));
        assertEquals(2, intervalGraphMapping.getGraph().vertexSet().size());

    }

    @Test
    public void testRemoveVertex() {
        Interval<Integer> interval1 = new Interval<>(3, 7);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);

        Interval<Integer> interval2 = new Interval<>(5, 49);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);

        ArrayList<IntervalVertexInterface<Integer, Integer>> vertices = new ArrayList<>();
        vertices.add(vertex1);
        vertices.add(vertex2);

        intervalGraphMapping = new IntervalGraphMapping<>(
                vertices, null, SupplierUtil.createDefaultEdgeSupplier(), false
        );

        intervalGraphMapping.removeVertex(vertex2);
        assertFalse(intervalGraphMapping.getGraph().vertexSet().contains(vertex2));
        assertTrue(intervalGraphMapping.getGraph().vertexSet().contains(vertex1));
        assertEquals(1, intervalGraphMapping.getGraph().vertexSet().size());

        intervalGraphMapping.removeVertex(vertex1);
        assertFalse(intervalGraphMapping.getGraph().vertexSet().contains(vertex1));
        assertFalse(intervalGraphMapping.getGraph().vertexSet().contains(vertex2));
        assertEquals(0, intervalGraphMapping.getGraph().vertexSet().size());
    }

    @Test
    public void testAsIntervalGraphMappingForUndirectedGraph() {
        Graph<Integer, DefaultEdge> undirectedGraph = new DefaultUndirectedGraph<>(DefaultEdge.class);

        undirectedGraph.addVertex(1);
        undirectedGraph.addVertex(2);
        undirectedGraph.addEdge(1, 2);

        IntervalGraphMapping intervalGraphMapping = IntervalGraphMapping.asIntervalGraphMapping(undirectedGraph);
        assertEquals(2, intervalGraphMapping.getGraph().vertexSet().size());
        assertEquals(1, intervalGraphMapping.getGraph().edgeSet().size());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testAsIntervalGraphMappingForDirectedGraph() {
        Graph<Integer, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        directedGraph.addVertex(1);
        directedGraph.addVertex(2);
        directedGraph.addEdge(1, 2);

        intervalGraphMapping = IntervalGraphMapping.asIntervalGraphMapping(directedGraph);
        assertNull(intervalGraphMapping);
    }

    @Test
    public void testIsMappingValid() {
        intervalGraphMapping = new IntervalGraphMapping<>(
                null, SupplierUtil.createDefaultEdgeSupplier(), false
        );

        assertTrue(intervalGraphMapping.isMappingValid());

        Interval<Integer> interval1 = new Interval<>(3, 7);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraphMapping.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(4, 309);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraphMapping.addVertex(vertex2);

        assertTrue(intervalGraphMapping.isMappingValid());

        intervalGraphMapping.removeVertex(vertex1);
        assertTrue(intervalGraphMapping.isMappingValid());

        intervalGraphMapping.removeVertex(vertex2);
        assertTrue(intervalGraphMapping.isMappingValid());
    }

    @Test
    public void testIsMappingValidAfterRemovingEdges() {
        intervalGraphMapping = new IntervalGraphMapping<>(
                null, SupplierUtil.createDefaultEdgeSupplier(), false
        );

        Interval<Integer> interval1 = new Interval<>(-3, 43);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraphMapping.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(39, 44);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraphMapping.addVertex(vertex2);

        assertTrue(intervalGraphMapping.isMappingValid());

        DefaultEdge  edge1 = intervalGraphMapping.getGraph().getEdge(vertex1, vertex2);
        intervalGraphMapping.getGraph().removeEdge(edge1);

        assertFalse(intervalGraphMapping.isMappingValid());
    }

    @Test
    public void testIsMapingValidAfterAddingEdges() {
        intervalGraphMapping = new IntervalGraphMapping<>(
                null, SupplierUtil.createDefaultEdgeSupplier(), false
        );

        Interval<Integer> interval1 = new Interval<>(-3, 43);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraphMapping.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(87, 89);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraphMapping.addVertex(vertex2);

        assertTrue(intervalGraphMapping.isMappingValid());

        intervalGraphMapping.getGraph().addEdge(vertex1, vertex2);
        assertFalse(intervalGraphMapping.isMappingValid());
    }

    @Test
    public void testIsMappingValidAfterRemovingOrAddingVertices() {
        intervalGraphMapping = new IntervalGraphMapping<>(
                null, SupplierUtil.createDefaultEdgeSupplier(), false
        );

        Interval<Integer> interval1 = new Interval<>(-984, -45);
        IntervalVertex<Integer, Integer> vertex1 = IntervalVertex.of(1, interval1);
        intervalGraphMapping.addVertex(vertex1);

        Interval<Integer> interval2 = new Interval<>(-9, 23);
        IntervalVertex<Integer, Integer> vertex2 = IntervalVertex.of(2, interval2);
        intervalGraphMapping.addVertex(vertex2);

        assertTrue(intervalGraphMapping.isMappingValid());

        intervalGraphMapping.getGraph().removeVertex(vertex1);
        assertTrue(intervalGraphMapping.isMappingValid());

        intervalGraphMapping.getGraph().removeVertex(vertex2);
        assertTrue(intervalGraphMapping.isMappingValid());

        intervalGraphMapping.addVertex(vertex1);

        Interval<Integer> interval3 = new Interval<>(-90, 23);
        IntervalVertex<Integer, Integer> vertex3 = IntervalVertex.of(2, interval3);
        intervalGraphMapping.addVertex(vertex3);

        assertTrue(intervalGraphMapping.isMappingValid());
    }
}
