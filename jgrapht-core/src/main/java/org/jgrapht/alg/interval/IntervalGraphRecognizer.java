/*
 * (C) Copyright 2003-2018, by Oliver Feith, Dennis Fischer and Contributors.
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
package org.jgrapht.alg.interval;

import static org.jgrapht.alg.interval.LexBreadthFirstSearch.*;

import java.lang.reflect.Array;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.interval.IntervalVertex;
import org.jgrapht.graph.interval.IntervalVertexInterface;
import org.jgrapht.graph.interval.Interval;

/**
 * A recognizer for interval graphs.
 *
 * An interval graph is a intersection graph of a set of intervals on the line, i.e. they contain a
 * vertex for each interval and two vertices are connected if the corresponding intervals have a
 * nonempty intersection.
 *
 * The recognizer uses the algorithm described in <a href=
 * "https://webdocs.cs.ualberta.ca/~stewart/Pubs/IntervalSIAM.pdf">https://webdocs.cs.ualberta.ca/~stewart/Pubs/IntervalSIAM.pdf</a>
 * (<i>The LBFS Structure and Recognition of Interval Graphs. SIAM J. Discrete Math.. 23. 1905-1953.
 * 10.1137/S0895480100373455.</i>) by Derek Corneil, Stephan Olariu and Lorna Stewart based on
 * multiple lexicographical breadth-first search (LBFS) sweeps.
 *
 * For this recognizer to work correctly the graph must not be modified during iteration.
 *
 *
 * @param <V> the graph vertex type.
 * @param <E> the graph edge type.
 * @author Oliver Feith
 * @author Dennis Fischer
 * @since April 2018
 */
public final class IntervalGraphRecognizer<V, E>
{

    /**
     * Stores whether or not the graph is an interval graph.
     */
    private Boolean isIntervalGraph;

    private Graph<V, E> graph;

    /**
     * Stores the computed interval graph representation (or <tt>null</tt> if no such representation
     * exists) of the graph.
     */
    private ArrayList<Interval<Integer>> intervalsSortedByStartingPoint;
    private Map<Interval<Integer>, V> intervalToVertexMap;
    private Map<V, IntervalVertexInterface<V, Integer>> vertexToIntervalMap;

    /**
     * Creates (and runs) a new interval graph recognizer for the given graph.
     * 
     * @param graph the graph to be tested.
     */
    public IntervalGraphRecognizer(Graph<V, E> graph)
    {
        this.graph = graph;
    }

    /**
     * check if the graph is an interval graph
     */
    @SuppressWarnings({ "unchecked" })
    private void computeIsIntervalGraph()
    {

        // An empty graph is an interval graph.
        if (graph.vertexSet().isEmpty()) {
            // Create (empty) interval representation
            this.intervalsSortedByStartingPoint = new ArrayList<>();
            this.intervalToVertexMap = new HashMap<>(0);
            this.vertexToIntervalMap = new HashMap<>(0);
            this.isIntervalGraph = true;

            return;
        }

        // Step 1 - LBFS from an arbitrary vertex
        // Input - random vertex r
        // Output - the result of current sweep alpha, further last vertex a visited by current
        // sweep
        HashMap<V, Integer> sweepAlpha =
            lexBreadthFirstSearch(graph, randomElementOf(graph.vertexSet()));

        // Step 2 - LBFS+ from the last vertex of the previous sweep
        // Input - the result of previous sweep alpha, vertex a
        // Output - the result of current sweep beta, further last vertex b visited by current sweep
        HashMap<V, Integer> sweepBeta = lexBreadthFirstSearchPlus(graph, sweepAlpha);

        // Step 3 - LBFS+ from the last vertex of the previous sweep
        // Input - the result of previous sweep beta, vertex b
        // Output - the result of current sweep gamma, further last vertex c visited by current
        // sweep
        HashMap<V, Integer> sweepGamma = lexBreadthFirstSearchPlus(graph, sweepBeta);

        // Step 4 - LBFS+ from the last vertex of the previous sweep
        // Input - the result of previous sweep gamma, vertex c
        // Output - the result of current sweep delta, further last vertex d visited by current
        // sweep
        HashMap<V, Integer> sweepDelta = lexBreadthFirstSearchPlus(graph, sweepGamma);

        // Step 5 - LBFS+ from the last vertex of the previous sweep
        // Input - the result of previous sweep delta, vertex d
        // Output - the result of current sweep epsilon, further last vertex e visited by current
        // sweep
        HashMap<V, Integer> sweepEpsilon = lexBreadthFirstSearchPlus(graph, sweepDelta);

        // Step 6 - LBFS* with the resulting sweeps
        // Input - the result of sweep gamma and sweep epsilon
        // Output - the result of current sweep zeta
        HashMap<V, Integer> sweepZeta = lexBreadthFirstSearchStar(graph, sweepDelta, sweepEpsilon);

        // if sweepZeta is umbrella-free, then the graph is interval.
        // otherwise, the graph is not interval

        if (isIOrdering(sweepZeta, graph)) {

            HashMap<V, Integer> neighborIndex = new HashMap<>();
            for (V vertex : graph.vertexSet()) {
                int maxNeighbor = 0;

                List<V> neighbors = Graphs.neighborListOf(graph, vertex);
                neighbors.add(vertex);

                for (V neighbor : neighbors) {
                    maxNeighbor = Math.max(maxNeighbor, sweepZeta.get(neighbor));
                }

                neighborIndex.put(vertex, maxNeighbor);
            }

            Interval<Integer>[] intervals =
                (Interval<Integer>[]) Array.newInstance(Interval.class, graph.vertexSet().size());
            this.intervalsSortedByStartingPoint = new ArrayList<>(graph.vertexSet().size());

            // Initialize the vertex map. Because we know the number of vertices we can make sure
            // the hashmap does not
            // need to rehash by setting the capacity to the number of vertices divided by the
            // default load factor
            // of 0.75.
            this.intervalToVertexMap =
                new HashMap<>((int) Math.ceil(graph.vertexSet().size() / 0.75));
            this.vertexToIntervalMap =
                new HashMap<>((int) Math.ceil(graph.vertexSet().size() / 0.75));

            // Compute intervals and store them associated by their starting point ...
            for (V vertex : graph.vertexSet()) {
                Interval<Integer> vertexInterval =
                    new Interval<>(sweepZeta.get(vertex), neighborIndex.get(vertex));

                intervals[sweepZeta.get(vertex)] = vertexInterval;

                this.intervalToVertexMap.put(vertexInterval, vertex);
                this.vertexToIntervalMap.put(vertex, IntervalVertex.of(vertex, vertexInterval));
            }

            // ... and produce a list sorted by the starting points for an efficient construction of
            // the graph
            this.intervalsSortedByStartingPoint.addAll(Arrays.asList(intervals));

            this.isIntervalGraph = true;
        } else {
            this.isIntervalGraph = false;
        }
    }

    /**
     * Calculates if the given sweep is an I-Ordering (according to the Graph graph) in linear time.
     *
     * @param <V> The vertex type.
     * @param <E> The edge type.
     *
     * @param sweep the order we want to check if its an I-Order
     * @param graph the graph we want to check if its an I-Order
     * @return true, if sweep is an I-Order according to graph
     */
    @SuppressWarnings({ "unchecked" })
    private static <V, E> boolean isIOrdering(HashMap<V, Integer> sweep, Graph<V, E> graph)
    {
        // Compute inverse sweep map to quickly find vertices at given indices
        V[] inverseSweep = (V[]) new Object[graph.vertexSet().size()];

        for (V vertex : graph.vertexSet()) {
            int index = sweep.get(vertex);
            inverseSweep[index] = vertex;
        }
        // Compute maximal neighbors w.r.t. sweep ordering for every vertex
        HashMap<V, V> maxNeighbors = new HashMap<>(graph.vertexSet().size());

        for (V vertex : graph.vertexSet()) {
            List<V> neighbors = Graphs.neighborListOf(graph, vertex);
            V maxNeighbor = vertex;

            for (V neighbor : neighbors) {
                if (sweep.get(neighbor) > sweep.get(maxNeighbor)) {
                    maxNeighbor = neighbor;
                }
            }

            maxNeighbors.put(vertex, maxNeighbor);
        }

        // Check if every vertex is connected to all vertices between itself and its maximal
        // neighbor
        for (V vertex : graph.vertexSet()) {
            int index = sweep.get(vertex);
            int maxIndex = sweep.get(maxNeighbors.get(vertex));

            for (int i = index; i < maxIndex; i++) {
                if (vertex != inverseSweep[i] && !graph.containsEdge(vertex, inverseSweep[i])) {
                    // Found missing edge
                    return false;
                }
            }
        }

        // No missing edge found
        return true;
    }

    /**
     * return a random element of the given set
     *
     * @param set The input set.
     * @param <V> the generic type representing vertices
     * @return A random element of the set
     */
    private static <V> V randomElementOf(Set<V> set) {
        if (set == null) {
            throw new IllegalArgumentException("Set parameter cannot be null.");
        }

        int index = new Random().nextInt(set.size());
        Iterator<V> iterator = set.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    /**
     * Makes sure the algorithm has been run and all fields are populated with their proper value.
     */
    private void ensureComputationComplete() {
        if (this.isIntervalGraph == null){
            computeIsIntervalGraph();
        }
    }

    /**
     * Returns whether or not the graph is an interval graph.
     *
     * @return <tt>true</tt> if the graph is an interval graph, otherwise false.
     */
    public boolean isIntervalGraph()
    {
        ensureComputationComplete();
        return isIntervalGraph;
    }

    /**
     * Returns the list of all intervals sorted by starting point, or null, if the graph was not an
     * interval graph.
     *
     * @return The list of all intervals sorted by starting point, or null, if the graph was not an
     *         interval graph.
     */
    public ArrayList<Interval<Integer>> getIntervalsSortedByStartingPoint()
    {
        ensureComputationComplete();
        return this.intervalsSortedByStartingPoint;
    }

    /**
     * Returns a mapping of the constructed intervals to the vertices of the original graph, or
     * null, if the graph was not an interval graph.
     *
     * @return A mapping of the constructed intervals to the vertices of the original graph, or
     *         null, if the graph was not an interval graph.
     */
    public Map<Interval<Integer>, V> getIntervalToVertexMap()
    {
        ensureComputationComplete();
        return this.intervalToVertexMap;
    }

    /**
     * Returns a mapping of the vertices of the original graph to the constructed intervals, or
     * null, if the graph was not an interval graph.
     *
     * @return A mapping of the vertices of the original graph to the constructed intervals, or
     *         null, if the graph was not an interval graph.
     */
    public Map<V, IntervalVertexInterface<V, Integer>> getVertexToIntervalMap()
    {
        ensureComputationComplete();
        return this.vertexToIntervalMap;
    }
}
