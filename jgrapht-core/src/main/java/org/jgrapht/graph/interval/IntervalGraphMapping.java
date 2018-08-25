/*
 * (C) Copyright 2018-2018, by Christoph Grüne and Contributors.
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
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.interval.IntervalGraphRecognizer;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;

/**
 * Implementation of an interval graph mapping. An interval graph G = (V, E) is an undirected graph formed by a family
 * of intervals I = \{I_v\}_{v \in V} such that (u, v) \in E iff I_u \cap I_v \neq \emptyset.
 * That is, the edges are implicitly defined by the intervals, such that instances do not allow the adding or removing
 * of edges.
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Interval_graph">https://en.wikipedia.org/wiki/Interval_graph</a>.
 *
 * @param <V>          the vertex type with a corresponding interval
 * @param <E>          the edge type
 * @param <VertexType> the underlying vertex type
 * @param <T>          The underlying type for intervals
 *
 * @author Christoph Grüne
 * @since May 30, 2018
 */
public class IntervalGraphMapping<V extends IntervalVertexPair<VertexType, T>, E, VertexType, T extends Comparable<T>>
        implements Serializable {

    private static final long serialVersionUID = 1112673663745683444L;

    /**
     * the underlying graph
     */
    private Graph<V, E> graph;

    /**
     * <code>intervalStructure</code> maintains all intervals in order to get intersecting intervals efficiently.
     */
    private IntervalIndex<T> intervalStructure;
    /**
     * <code>intervalVertexMap</code> maintains the assignment of every interval to vertex
     */
    private Map<Interval<T>, V> intervalVertexMap;
    /**
     * <code>intervalVertexMap</code> maintains the assignment of every vertex to interval
     */
    private Map<VertexType, Interval<T>> vertexIntervalMap;

    /**
     * stores state whether this mapping is valid
     */
    private boolean mappingValid;

    /**
     * stores state whether this mapping is live (that is, is a ListenableGraph used)
     */
    private boolean mappingLive;

    /**
     * Constructor for a non-live mapping to an interval graph. All methods are passed through to the underlying
     * interval graph.
     *
     * @param graph the graph to be mapped
     */
    private IntervalGraphMapping(Graph<V, E> graph) {
        this.graph = graph;
        this.intervalStructure = new IntervalTreeStructure<>();
        this.intervalVertexMap = new HashMap<>();
        this.vertexIntervalMap = new HashMap<>();
        // as we know that a this is a correctly initialized mapping, we can set this mapping to valid
        this.mappingValid = true;
        // as we know that no ListenableGraph is used, we set the mapping non-live
        this.mappingLive = false;
    }

    /**
     * Constructor for a live mapping to an interval graph. All methods are passed through to the underlying
     * interval graph. Every vertex and edge update changes the underlying graph and this mapping.
     *
     * @param graph the graph to be mapped
     */
    private IntervalGraphMapping(ListenableGraph<V, E> graph) {
        this((Graph<V, E>) graph);
        // add the basic IntervalGraphListener to the ListenableGraph such that we have a live mapping
        graph.addGraphListener(new IntervalGraphMappingListener<>(this));
        // as we know that a ListenableGraph is used, we set the mapping live
        this.mappingLive = true;
    }

    /**
     * Constructs a new live interval graph mapping with a new undirected graph.
     *
     * @param vertexSupplier the vertex supplier of the new graph
     * @param edgeSupplier   the edge supplier of the new graph.
     * @param weighted       whether the graph is weighted, i.e. the edges support a weight attribute
     */
    public IntervalGraphMapping(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        this(new DefaultListenableGraph<>(new DefaultUndirectedGraph<>(vertexSupplier, edgeSupplier, weighted)));
    }

    /**
     * Construct a new live interval graph mapping with a new undirected graph with given <code>vertices</code>.
     * Runs in linear time if <code>vertices</code> is sorted by the starting point and the end point of the interval,
     * otherwise naive insertion is performed.
     *
     * @param vertices       initial vertices
     * @param vertexSupplier the vertex supplier of the new graph.
     * @param edgeSupplier   the edge supplier of the new graph.
     * @param weighted       whether the graph is weighted, i.e. the edges support a weight attribute
     */
    public IntervalGraphMapping(List<V> vertices, Supplier<V> vertexSupplier,
                                Supplier<E> edgeSupplier, boolean weighted) {
        this(vertexSupplier, edgeSupplier, weighted);

        ArrayList<Interval<T>> vertexIntervals = new ArrayList<>(vertices.size());

        // add all vertices to the graph
        for (V v : vertices) {
            vertexIntervals.add(v.getInterval());
            intervalVertexMap.put(v.getInterval(), v);
            vertexIntervalMap.put(v.getVertex(), v.getInterval());
            graph.addVertex(v);
        }

        // build up the interval tree in linear time if vertexIntervals is sorted
        this.intervalStructure = new IntervalTreeStructure<>(vertexIntervals);

        for (V v : vertices) {
            addIntervalEdges(v, intervalStructure.findOverlappingIntervals(v.getInterval()));
        }
    }

    /**
     * This method can only be used if the graph is an interval graph! Therefore, it is private. We use it for
     * asIntervalGraphMapping()
     * <p>
     * Construct a new live interval graph mapping with a new graph with given <code>vertices</code>.
     * The graph can either be directed or undirected, depending on the specified edge factory.
     * <p>
     * Runs in linear time if <code>vertices</code> is sorted by the starting point and the end point of the interval,
     * otherwise naive insertion is performed.
     *
     * @param vertices       initial vertices
     * @param edges          initial edges
     * @param vertexSupplier the vertex supplier of the new graph.
     * @param edgeSupplier   the edge supplier of the new graph.
     * @param weighted       whether the graph is weighted, i.e. the edges support a weight attribute
     */
    private IntervalGraphMapping(List<V> vertices, Map<Pair<V, V>, E> edges, Supplier<V> vertexSupplier,
                                 Supplier<E> edgeSupplier, boolean weighted) {
        this(new DefaultListenableGraph<>(new DefaultUndirectedGraph<>(vertexSupplier, edgeSupplier, weighted)));

        ArrayList<Interval<T>> vertexIntervals = new ArrayList<>(vertices.size());

        // add sorted vertices
        for (V v : vertices) {
            vertexIntervals.add(v.getInterval());
            intervalVertexMap.put(v.getInterval(), v);
            vertexIntervalMap.put(v.getVertex(), v.getInterval());
            graph.addVertex(v);
        }

        this.intervalStructure = new IntervalTreeStructure<>(vertexIntervals);

        // add all edges
        for (Pair<V, V> v : edges.keySet()) {
            graph.addEdge(v.getFirst(), v.getSecond(), edges.get(v));
        }
    }

    /**
     * Getter for the underlying graph.
     *
     * @return the underlying graph
     */
    public Graph<V, E> getGraph() {
        return graph;
    }

    /**
     * Returns interval graph representation if one exists, otherwise null
     *
     * @param graph        the graph to check
     * @param <E>          the edge type
     * @param <VertexType> the internal vertex type
     * @return interval graph representation if one exists, otherwise null
     */
    public static <E, VertexType> IntervalGraphMapping<IntervalVertexPair<VertexType, Integer>, E, VertexType, Integer>
    asIntervalGraphMapping(Graph<VertexType, E> graph) {

        Objects.requireNonNull(graph);

        // initialize IntervalGraphRecognizer
        IntervalGraphRecognizer<VertexType, E> intervalGraphRecognizer = new IntervalGraphRecognizer<>(graph);

        // execute IntervalGraphRecognizer
        if (!intervalGraphRecognizer.isIntervalGraph()) {
            return null;
        }

        // get necessary data structures from IntervalGraphRecognizer
        ArrayList<Interval<Integer>> sortedIntervalList = intervalGraphRecognizer.getIntervalsSortedByStartingPoint();
        Map<Interval<Integer>, VertexType> intervalVertexMap = intervalGraphRecognizer.getIntervalToVertexMap();
        Map<VertexType, IntervalVertexPair<VertexType, Integer>> vertexIntervalMap =
                intervalGraphRecognizer.getVertexToIntervalMap();

        //initialize ArrayList for vertices for quick iteration
        ArrayList<IntervalVertexPair<VertexType, Integer>> vertices = new ArrayList<>(sortedIntervalList.size());

        // add vertices to the vertex list
        for (int i = 0; i < sortedIntervalList.size(); ++i) {
            vertices.add(i, vertexIntervalMap.get(intervalVertexMap.get(sortedIntervalList.get(i))));
        }

        // initialize a map from vertex pairs to all edges
        Map<Pair<IntervalVertexPair<VertexType, Integer>, IntervalVertexPair<VertexType, Integer>>, E> edges =
                new HashMap<>();

        // add the edges to the map
        for (IntervalVertexPair<VertexType, Integer> vertex : vertices) {
            for (E edge : graph.outgoingEdgesOf(vertex.getVertex())) {
                if (!vertex.getVertex().equals(graph.getEdgeTarget(edge))) {
                    edges.put(Pair.of(vertex, vertexIntervalMap.get(graph.getEdgeTarget(edge))), edge);
                }
            }
        }

        // return new IntervalGraphMapping with an invalid vertex supplier as adding of general vertices is not allowed
        // because we need for every vertex the corresponding interval.
        return new IntervalGraphMapping<>(vertices, edges, null,
                () -> graph.getEdgeSupplier().get(), graph.getType().isWeighted());
    }

    /**
     * Returns the interval vertex pair corresponding to <code>interval</code>.
     *
     * @param interval the interval corresponding to the interval vertex pair
     * @return the interval vertex pair corresponding to <code>interval</code>
     */
    public V getIntervalVertexPair(Interval<T> interval) {
        return intervalVertexMap.get(interval);
    }

    /**
     * Returns the vertex corresponding to <code>interval</code>.
     *
     * @param interval the interval corresponding to the vertex
     * @return the vertex corresponding to <code>interval</code>
     */
    public VertexType getVertex(Interval<T> interval) {
        return intervalVertexMap.get(interval).getVertex();
    }

    /**
     * Returns the interval corresponding to <code>vertexType</code>.
     *
     * @param vertexType the vertex corresponding to the interval
     * @return the interval corresponding to <code>vertexType</code>
     */
    public Interval<T> getInterval(VertexType vertexType) {
        return vertexIntervalMap.get(vertexType);
    }

    /**
     * Returns whether the mapping is valid.
     *
     * @return true, if mapping is valid; false, otherwise
     */
    public boolean isMappingValid() {
        return mappingValid;
    }

    /**
     * Returns whether the mapping is valid.
     *
     * @return true, if mapping is valid; false, otherwise
     */
    public boolean isMappingLive() {
        return mappingLive;
    }

    /**
     * Sets the mapping to invalid.
     */
    void setMappingInvalid() {
        this.mappingValid = false;
    }

    /**
     * Adds a vertex to all data structures (graph, intervalStructure, intervalVertexMap) that are used in this mapping.
     *
     * @param v the vertex to add
     * @return <code>true</code> if this graph did not already contain the specified vertex.
     * @throws NullPointerException if the specified vertex is <code>null</code>
     */
    public boolean addVertex(V v) {
        Objects.requireNonNull(v);

        graph.addVertex(v);
        if (intervalStructure.add(v.getInterval())) {
            intervalVertexMap.put(v.getInterval(), v);

            addIntervalEdges(v, intervalStructure.findOverlappingIntervals(v.getInterval()));
            return true;
        }
        return false;
    }

    /**
     * Removes a vertex from all data structures (graph, intervalStructure, intervalVertexMap) that are used in this mapping.
     *
     * @param v the vertex to remove
     * @return <code>true</code> if the graph contained the specified vertex; <code>false</code> otherwise.
     */
    public boolean removeVertex(V v) {
        Objects.requireNonNull(v);

        boolean mappingCurrentlyValid = true;
        if (!mappingValid) {
            mappingCurrentlyValid = false;
        }
        graph.removeVertex(v);
        if (mappingCurrentlyValid) {
            mappingValid = true;
        }
        if (intervalStructure.remove(v.getInterval())) {
            intervalVertexMap.remove(v.getInterval());

            return true;
        }
        return false;
    }

    /**
     * Adds edges between <code>sourceVertex</code> and every vertex from <code>vertices</code>.
     * The mapping remains valid if it was valid before.
     * That is, use this method only for adding correctly defined interval edges.
     *
     * @param sourceVertex    source vertex of all edges
     * @param targetIntervals target intervals of edges
     */
    private void addIntervalEdges(V sourceVertex, Collection<Interval<T>> targetIntervals) {
        boolean mappingCurrentlyValid = true;
        if (!mappingValid) {
            mappingCurrentlyValid = false;
        }
        for (Interval<T> targetInterval : targetIntervals) {
            V targetVertex = intervalVertexMap.get(targetInterval);
            if (!sourceVertex.equals(targetVertex)) {
                graph.addEdge(sourceVertex, targetVertex);
            }
        }
        if (mappingCurrentlyValid) {
            mappingValid = true;
        }
    }
}
