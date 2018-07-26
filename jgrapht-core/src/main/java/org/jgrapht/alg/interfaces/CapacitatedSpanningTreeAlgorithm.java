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
package org.jgrapht.alg.interfaces;

import org.jgrapht.alg.util.Pair;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * An algorithm which computes a capacitated (minimum) spanning tree of a given connected graph with a designated root
 * vertex. A <a href="https://en.wikipedia.org/wiki/Capacitated_minimum_spanning_tree">Capacitated Minimum
 * Spanning Tree</a> (CMST) is a rooted minimal cost spanning tree that satisfies the capacity constrained on all trees
 * that are connected to the designated root. These trees build up a partition on the vertex set of the graph.
 * The problem is NP-hard.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public interface CapacitatedSpanningTreeAlgorithm<V, E> {
    /**
     * Computes a capacitated spanning tree.
     *
     * @return a capacitated spanning tree
     */
    CapacitatedSpanningTree<V, E> getCapacitatedSpanningTree();

    /**
     * A spanning tree.
     *
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    interface CapacitatedSpanningTree<V, E> extends Iterable<E>, SpanningTreeAlgorithm.SpanningTree<E> {

        /**
         * Returns the root vertex of the capacitated spanning tree.
         *
         * @return the root vertex of capacitated spanning tree.
         */
        V getRoot();

        /**
         * Returns the edge capacity of the capacitated spanning tree.
         *
         * @return the edge capacity of the capacitated spanning tree.
         */
        double getCapacity();

        /**
         * Returns the vertex demand map of the capacitated spanning tree.
         *
         * @return the vertex demand map of the capacitated spanning tree.
         */
        Map<V, Double> getDemands();

        /**
         * Return the set of labels of the underlying partition of the capacitated spanning tree.
         * The labels are a key to the vertex sets of the partition.
         *
         * @return the label set of the capacitated spanning tree.
         */
        Map<V, Integer> getLabels();

        /**
         * Return the label-to-partition map of the underlying partition of capacitated spanning tree.
         *
         * @return map from labels to the subsets of the partition of the capacitated spanning tree.
         */
        Map<Integer, Pair<Set<V>, Double>> getPartition();
    }

    /**
     * Default implementation of the spanning tree interface.
     *
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    class CapacitatedSpanningTreeImpl<V, E> implements CapacitatedSpanningTree<V, E>, Serializable {

        private static final long serialVersionUID = 7088989899889893333L;

        private final V root;
        private final double capacity;
        private final Map<V, Double> demands;
        private final Map<V, Integer> labels;
        private final Map<Integer, Pair<Set<V>, Double>> partition;
        private final double weight;
        private final Set<E> edges;

        /**
         * Construct a new spanning tree.
         *
         * @param edges the edges
         * @param weight the weight
         */
        public CapacitatedSpanningTreeImpl(V root, double capacity, Map<V, Double> demands, Map<V, Integer> labels, Map<Integer, Pair<Set<V>, Double>> partition, Set<E> edges, double weight) {
            this.root = root;
            this.capacity = capacity;
            this.demands = demands;
            this.labels = labels;
            this.partition = partition;
            this.edges = edges;
            this.weight = weight;
        }

        @Override
        public V getRoot() {
            return root;
        }

        @Override
        public double getCapacity() {
            return capacity;
        }

        @Override
        public Map<V, Double> getDemands() {
            return demands;
        }

        @Override
        public Map<V, Integer> getLabels() {
            return labels;
        }

        @Override
        public Map<Integer, Pair<Set<V>, Double>> getPartition() {
            return partition;
        }

        @Override
        public double getWeight() {
            return weight;
        }

        @Override
        public Set<E> getEdges() {
            return edges;
        }

        @Override
        public String toString() {
            return "Spanning-Tree [weight=" + weight + ", edges=" + edges + "]";
        }
    }
}
