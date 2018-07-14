/*
 * (C) Copyright 2003-2018, by Christoph Grüne and Contributors.
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
package org.jgrapht.alg.spanning;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsSubgraph;

import java.util.*;

public abstract class AbstractCapacitatedMinimumSpanningTree<V, E> implements SpanningTreeAlgorithm<E> {

    protected class SolutionRepresentation {

        /**
         * labeling of the improvement graph vertices. There are two vertices in the improvement graph for every vertex
         * in the input graph: the vertex indicating the vertex itself and the vertex indicating the subtree.
         */
        private Map<V, Integer> labels;

        /**
         * the implicit partition defined by the subtrees
         */
        private Map<Integer, Pair<Set<V>, Double>> partition;

        protected SolutionRepresentation() {
            this(new HashMap<>(), new HashMap<>());
        }

        protected SolutionRepresentation(Map<V, Integer> labels, Map<Integer, Pair<Set<V>, Double>> partition) {
            this.labels = labels;
            this.partition = partition;
        }

        protected SpanningTree<E> calculateResultingSpanningTree() {
            Set<E> spanningTreeEdges = new HashSet<>();
            double weight = 0;

            for(Pair<Set<V>, Double> part : solutionRepresentation.partition.values()) {
                // get spanning tree on the part inclusive the root vertex
                Set<V> set = part.getFirst();
                set.add(root);
                SpanningTree<E> subtree = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, set)).getSpanningTree();
                set.remove(root);

                // add the partial solution to the overall solution
                spanningTreeEdges.addAll(subtree.getEdges());
                weight += subtree.getWeight();
            }

            return new SpanningTreeImpl<>(spanningTreeEdges, weight);
        }

        protected void moveVertex(V vertex, Integer fromLabel, Integer toLabel) {
            labels.put(vertex, toLabel);
            partition.get(fromLabel).getFirst().remove(vertex);
            Set<V> part = partition.get(toLabel).getFirst();
            part.add(vertex);
            partition.put(toLabel, Pair.of(part, partition.get(toLabel).getSecond() + weights.get(vertex)));

            // remove merged part from partition if empty
            if(partition.get(fromLabel).getFirst().isEmpty()) {
                partition.remove(fromLabel);
            }
        }

        protected void moveVertices(Set<V> vertices, Integer fromLabel, Integer toLabel) {
            // update labels and partition
            for(V v : vertices) {
                moveVertex(v, fromLabel, toLabel);
            }
        }

        protected Integer getLabelOfVertex(V vertex) {
            return labels.get(vertex);
        }

        protected Set<Integer> getLabels() {
            return partition.keySet();
        }

        protected int sizeOfLabel() {
            return 2 * labels.size();
        }

        protected boolean isLabelEmpty() {
            return labels.isEmpty();
        }

        protected boolean containsLabelKey(Object key) {
            if(key instanceof Pair) {
                return labels.containsKey(((Pair) key).getFirst());
            }
            return false;
        }

        protected boolean containsLabelValue(Object value) {
            return labels.containsValue(value);
        }

        protected Integer getLabel(Object key) {
            if(key instanceof Pair) {
                return labels.get(((Pair) key).getFirst());
            }
            return null;
        }

        protected Set<V> getPartitionSet(Integer label) {
            return partition.get(label).getFirst();
        }

        protected Double getPartitionWeight(Integer label) {
            return partition.get(label).getSecond();
        }
    }

    /**
     * the input graph.
     */
    protected final Graph<V, E> graph;

    /**
     * the designated root of the CMST.
     */
    protected final V root;

    /**
     * the maximal capacity for each subtree.
     */
    protected final double capacity;

    /**
     * the weight function over all vertices.
     */
    protected final Map<V, Double> weights;

    /**
     * representation of the solution
     */
    protected SolutionRepresentation solutionRepresentation;

    protected AbstractCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> weights) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.root = Objects.requireNonNull(root, "Root cannot be null");
        this.capacity = capacity;
        this.weights = Objects.requireNonNull(weights, "Weight cannot be null");

        this.solutionRepresentation = new SolutionRepresentation();
    }

    @Override
    public abstract SpanningTree<E> getSpanningTree();
}
