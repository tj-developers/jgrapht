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

import org.jgrapht.*;
import org.jgrapht.traverse.*;

import java.util.*;

/** A class that is used to perform the BFS algorithms used to detect interval graphs.
 * @author Oliver Feith
 * @author Dennis Fischer
 */
final class LexBreadthFirstSearch
{

    /**
     * Performs a lexicographical BFS starting at {@code startingVertex}.
     *
     * @param <V> The vertex type
     * @param <E> The edge type
     * @param graph the graph we want to perform LBFS on
     * @param startingVertex the starting vertex of the LBFS
     * @return an array of vertices representing the order in which the vertices were found
     */
    static <V, E> HashMap<V, Integer> lexBreadthFirstSearch(Graph<V, E> graph, V startingVertex)
    {
        HashMap<V, Integer> result = new HashMap<>(graph.vertexSet().size());
        LexBreadthFirstIterator<V,E> lbfIterator = new LexBreadthFirstIterator<>(graph, startingVertex);

        for(int i = 0; i < graph.vertexSet().size(); i++) {
            result.put(lbfIterator.next(), i);
        }

        return result;
    }

    /**
     * Performs LBFS+ starting at {@code startingVertex} using the previous ordering {@code prevOrdering}.
     *
     * @param graph the graph we want to perform LBFS on
     * @param priority the priority of vertices for tiebreaking
     * @param <V> The vertex type
     * @param <E> The edge type
     * @return an array of vertices representing the order in which the vertices were found
     */

    static <V, E> HashMap<V, Integer> lexBreadthFirstSearchPlus(Graph<V, E> graph, HashMap<V, Integer> priority)
    {
        HashMap<V, Integer> result = new HashMap<>(graph.vertexSet().size());
        LexBreadthFirstIterator<V, E> lbfIterator = new LexBreadthFirstIterator<>(graph, priority);

        for(int i = 0; i < graph.vertexSet().size(); i++) {
            result.put(lbfIterator.next(), i);
        }

        return result;
    }

    /**
     * Performs LBFS* starting at {@code startingVertex} using two previous orderings {@code prevOrdering1} and {@code prevOrdering2}.
     *
     * @param graph the graph we want to perform LBFS on
     * @param priorityA the first priority of vertices for tiebreaking
     * @param priorityB the second priority of vertices for tiebreaking
     * @param <V> The vertex type
     * @param <E> The edge type
     * @return an array of vertices representing the order in which the vertices were found
     */

    static <V, E> HashMap<V, Integer> lexBreadthFirstSearchStar(Graph<V, E> graph, HashMap<V, Integer> priorityA, HashMap<V, Integer> priorityB)
    {
        HashMap<V, Integer> neighborIndexA = new HashMap<>();
        HashMap<V, Integer> neighborIndexB = new HashMap<>();

        HashMap<V, Set<V>> ASets = new HashMap<>();
        HashMap<V, Set<V>> BSets = new HashMap<>();

        for(V vertex : graph.vertexSet()) {
            // Compute indexA, indexB
            int maxNeighborA = 0;
            int maxNeighborB = 0;

            List<V> neighbors = Graphs.neighborListOf(graph, vertex);
            neighbors.add(vertex);

            for(V neighbor : neighbors) {
                maxNeighborA = Math.max(maxNeighborA, priorityA.get(neighbor));
                maxNeighborB = Math.max(maxNeighborB, priorityB.get(neighbor));
            }

            neighborIndexA.put(vertex, maxNeighborA);
            neighborIndexB.put(vertex, maxNeighborB);
        }

        for(V vertex : graph.vertexSet()) {
            HashSet<V> Av = new HashSet<>();
            HashSet<V> Bv = new HashSet<>();

            for(V neighbor : Graphs.neighborListOf(graph, vertex)) {
                if(priorityA.get(neighbor) < priorityA.get(vertex) && neighborIndexA.get(neighbor) > priorityA.get(vertex)) {
                    Av.add(neighbor);
                }

                if(priorityB.get(neighbor) < priorityB.get(vertex) && neighborIndexB.get(neighbor) > priorityB.get(vertex)) {
                    Bv.add(neighbor);
                }
            }

            ASets.put(vertex, Av);
            BSets.put(vertex, Bv);
        }

        HashMap<V, Integer> result = new HashMap<>(graph.vertexSet().size());
        LexBreadthFirstIterator<V, E> lbfIterator = new LexBreadthFirstIterator<>(graph, priorityA, priorityB, neighborIndexA, neighborIndexB, ASets, BSets);

        for(int i = 0; i < graph.vertexSet().size(); i++) {
            result.put(lbfIterator.next(), i);
        }
        return result;
    }
}
