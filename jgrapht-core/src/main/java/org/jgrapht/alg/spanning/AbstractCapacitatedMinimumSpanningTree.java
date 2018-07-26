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
import org.jgrapht.alg.interfaces.CapacitatedSpanningTreeAlgorithm;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsSubgraph;

import java.util.*;

public abstract class AbstractCapacitatedMinimumSpanningTree<V, E> implements CapacitatedSpanningTreeAlgorithm<V, E> {

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

        protected CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<V, E> calculateResultingSpanningTree() {
            Set<E> spanningTreeEdges = new HashSet<>();
            double weight = 0;

            for(Pair<Set<V>, Double> part : solutionRepresentation.partition.values()) {
                // get spanning tree on the part inclusive the root vertex
                Set<V> set = part.getFirst();
                set.add(root);
                SpanningTreeAlgorithm.SpanningTree<E> subtree = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, set)).getSpanningTree();
                set.remove(root);

                // add the partial solution to the overall solution
                spanningTreeEdges.addAll(subtree.getEdges());
                weight += subtree.getWeight();
            }

            return new CapacitatedSpanningTreeImpl<>(root, capacity, demands, labels, partition, spanningTreeEdges, weight);
        }

        protected void moveVertex(V vertex, Integer fromLabel, Integer toLabel) {
            labels.put(vertex, toLabel);

            Set<V> oldPart = partition.get(fromLabel).getFirst();
            oldPart.remove(vertex);
            partition.put(fromLabel, Pair.of(oldPart, partition.get(fromLabel).getSecond() - demands.get(vertex)));

            Set<V> newPart = partition.get(toLabel).getFirst();
            newPart.add(vertex);
            partition.put(toLabel, Pair.of(newPart, partition.get(toLabel).getSecond() + demands.get(vertex)));

            // remove merged part from partition if empty
            if(partition.get(fromLabel).getFirst().isEmpty()) {
                partition.remove(fromLabel);
            }
        }

        protected void moveVertices(Set<V> vertices, Integer fromLabel, Integer toLabel) {
            // update labels and calculate weight change
            double weightOfVertices = 0;
            for(V v : vertices) {
                weightOfVertices += demands.get(v);
                labels.put(v, toLabel);
            }

            // update partition
            Set<V> newPart = partition.get(toLabel).getFirst();
            newPart.addAll(vertices);
            partition.put(toLabel, Pair.of(newPart, partition.get(toLabel).getSecond() + weightOfVertices));

            Set<V> oldPart = partition.get(fromLabel).getFirst();
            oldPart.removeAll(vertices);
            partition.put(fromLabel, Pair.of(oldPart, partition.get(fromLabel).getSecond() - weightOfVertices));

            // remove merged part from partition if empty
            if(partition.get(fromLabel).getFirst().isEmpty()) {
                partition.remove(fromLabel);
            }
        }

        protected Integer getLabel(V vertex) {
            return labels.get(vertex);
        }

        protected Set<Integer> getLabels() {
            return partition.keySet();
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
     * the demand function over all vertices.
     */
    protected final Map<V, Double> demands;

    /**
     * representation of the solution
     */
    protected SolutionRepresentation solutionRepresentation;

    protected AbstractCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> demands) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        if (!graph.getType().isUndirected()) {
            throw new IllegalArgumentException("Graph must be undirected");
        }
        this.root = Objects.requireNonNull(root, "Root cannot be null");
        this.capacity = capacity;
        this.demands = Objects.requireNonNull(demands, "Demands cannot be null");

        this.solutionRepresentation = new SolutionRepresentation();
    }

    @Override
    public abstract CapacitatedSpanningTree<V, E> getCapacitatedSpanningTree();
}
