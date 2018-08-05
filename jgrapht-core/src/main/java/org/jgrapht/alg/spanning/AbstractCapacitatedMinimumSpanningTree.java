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
package org.jgrapht.alg.spanning;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.CapacitatedSpanningTreeAlgorithm;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.util.TypeUtil;

import java.util.*;

/**
 * This is an abstract class for capacitated minimum spanning tree algorithms.
 * This class manages the basic instance information and a solution representation {see SolutionRepresentation} for a capacitated spanning tree.
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 *
 * @author Christoph Grüne
 * @since July 18, 2018
 */
public abstract class AbstractCapacitatedMinimumSpanningTree<V, E> implements CapacitatedSpanningTreeAlgorithm<V, E> {

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
    protected SolutionRepresentation bestSolution;

    /**
     * Construct a new abstract capacitated minimum spanning tree algorithm.
     *
     * @param graph the base graph to calculate the capacitated spanning tree for
     * @param root the root of the capacitated spanning tree
     * @param capacity the edge capacity constraint
     * @param demands the demands of the vertices
     */
    protected AbstractCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> demands) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        if (!graph.getType().isUndirected()) {
            throw new IllegalArgumentException("Graph must be undirected");
        }
        if(!new ConnectivityInspector<>(graph).isConnected()) {
            throw new IllegalArgumentException("Graph must be connected. Otherwise, there is no capacitated minimum spanning tree.");
        }
        this.root = Objects.requireNonNull(root, "Root cannot be null");
        this.capacity = capacity;
        this.demands = Objects.requireNonNull(demands, "Demands cannot be null");
        for(double demand : demands.values()) {
            if(demand > capacity) {
                throw new IllegalArgumentException("Demands must not be greater than the capacity. Otherwise, there is no capacitated minimum spanning tree.");
            }
        }

        this.bestSolution = new SolutionRepresentation();
    }

    @Override
    public abstract CapacitatedSpanningTree<V, E> getCapacitatedSpanningTree();

    /**
     * This class represents a solution instance by managing the labels and the partition mapping.
     * With the help of this class, a capacitated spanning tree based on the label and partition mapping can be calculated.
     */
    protected class SolutionRepresentation implements Cloneable {

        /**
         * labeling of the improvement graph vertices. There are two vertices in the improvement graph for every vertex
         * in the input graph: the vertex indicating the vertex itself and the vertex indicating the subtree.
         */
        private Map<V, Integer> labels;

        /**
         * the implicit partition defined by the subtrees
         */
        private Map<Integer, Pair<Set<V>, Double>> partition;

        /**
         * the next free label
         */
        private int nextFreeLabel;

        /**
         * Constructs a new solution representation for the CMST problem.
         */
        public SolutionRepresentation() {
            this(new HashMap<>(), new HashMap<>());
        }

        /**
         * Constructs a new solution representation for the CMST problem based on <code>labels</code> and <code>partition</code>.
         * All labels have to be positive.
         *
         * @param labels the labels of the subsets in the partition
         * @param partition the partition map
         */
        public SolutionRepresentation(Map<V, Integer> labels, Map<Integer, Pair<Set<V>, Double>> partition) {
            for(Integer i : labels.values()) {
                if(i < 0) {
                    throw new IllegalArgumentException("Labels are not non-negative");
                }
            }
            for(Integer i : partition.keySet()) {
                if(i < 0) {
                    throw new IllegalArgumentException("Labels are not non-negative");
                }
            }
            this.labels = labels;
            this.partition = partition;
            getNextFreeLabel();
        }

        /**
         * Calculates the resulting spanning tree based on this solution representation.
         *
         * @return the resulting spanning tree based on this solution representation
         */
        public CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<V, E> calculateResultingSpanningTree() {
            Set<E> spanningTreeEdges = new HashSet<>();
            double weight = 0;

            for(Pair<Set<V>, Double> part : bestSolution.partition.values()) {
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

        /**
         * Moves <code>vertex</code> from the subset represented by <code>fromLabel</code> to the subset represented by <code>toLabel</code>.
         *
         * @param vertex the vertex to move
         * @param fromLabel the subset to move the vertex from
         * @param toLabel the subset to move the vertex to
         */
        public void moveVertex(V vertex, Integer fromLabel, Integer toLabel) {
            labels.put(vertex, toLabel);

            Set<V> oldPart = partition.get(fromLabel).getFirst();
            oldPart.remove(vertex);
            partition.put(fromLabel, Pair.of(oldPart, partition.get(fromLabel).getSecond() - demands.get(vertex)));

            if(!partition.keySet().contains(toLabel)) {
                partition.put(toLabel, Pair.of(new HashSet<>(), 0.0));
            }
            Set<V> newPart = partition.get(toLabel).getFirst();
            newPart.add(vertex);
            partition.put(toLabel, Pair.of(newPart, partition.get(toLabel).getSecond() + demands.get(vertex)));
        }

        /**
         * Moves all vertices in <code>vertices</code> from the subset represented by <code>fromLabel</code> to the subset represented by <code>toLabel</code>.
         *
         * @param vertices the vertices to move
         * @param fromLabel the subset to move the vertices from
         * @param toLabel the subset to move the vertices to
         */
        public void moveVertices(Set<V> vertices, Integer fromLabel, Integer toLabel) {
            // update labels and calculate weight change
            double weightOfVertices = 0;
            for(V v : vertices) {
                weightOfVertices += demands.get(v);
                labels.put(v, toLabel);
            }

            // update partition
            if(!partition.keySet().contains(toLabel)) {
                partition.put(toLabel, Pair.of(new HashSet<>(), 0.0));
            }
            Set<V> newPart = partition.get(toLabel).getFirst();
            newPart.addAll(vertices);
            partition.put(toLabel, Pair.of(newPart, partition.get(toLabel).getSecond() + weightOfVertices));

            Set<V> oldPart = partition.get(fromLabel).getFirst();
            oldPart.removeAll(vertices);
            partition.put(fromLabel, Pair.of(oldPart, partition.get(fromLabel).getSecond() - weightOfVertices));
        }

        /**
         * Cleans up the solution representation by removing all empty sets from the partition.
         */
        public void cleanUp() {
            partition.entrySet().removeIf(entry -> entry.getValue().getFirst().isEmpty());
        }

        /**
         * Returns the next free label in the label map respectively partition
         *
         * @return the next free label in the label map respectively partition
         */
        public int getNextFreeLabel() {
            int freeLabel = nextFreeLabel;
            nextFreeLabel++;
            while(partition.keySet().contains(nextFreeLabel)) {
                nextFreeLabel++;
            }
            return freeLabel;
        }

        /**
         * Returns the label of the subset that contains <code>vertex</code>.
         *
         * @param vertex the vertex to return the label from
         *
         * @return the label of <code>vertex</code>
         */
        public int getLabel(V vertex) {
            return labels.get(vertex);
        }

        /**
         * Returns all labels of all subsets.
         *
         * @return the labels of all subsets
         */
        public Set<Integer> getLabels() {
            return partition.keySet();
        }

        /**
         * Returns the set of vertices that are in the subset with label <code>label</code>.
         *
         * @param label the label of the subset to return the vertices from
         *
         * @return the set of vertices that are in the subset with label <code>label</code>
         */
        public Set<V> getPartitionSet(Integer label) {
            return partition.get(label).getFirst();
        }

        /**
         * Returns the sum of the weights of all vertices that are in the subset with label <code>label</code>.
         *
         * @param label the label of the subset to return the weight from
         *
         * @return the sum of the weights of all vertices that are in the subset with label <code>label</code>
         */
        public double getPartitionWeight(Integer label) {
            return partition.get(label).getSecond();
        }

        /**
         * Returns a shallow copy of this solution representation instance. Vertices are not cloned.
         *
         * @return a shallow copy of this solution representation.
         *
         * @throws RuntimeException in case the clone is not supported
         *
         * @see java.lang.Object#clone()
         */
        public SolutionRepresentation clone() {
            try {
                SolutionRepresentation solutionRepresentation = TypeUtil.uncheckedCast(super.clone());
                solutionRepresentation.labels = new HashMap<>(labels);
                solutionRepresentation.partition = new HashMap<>();
                for(Map.Entry<Integer, Pair<Set<V>, Double>> entry : this.partition.entrySet()) {
                    solutionRepresentation.partition.put(entry.getKey(), Pair.of(new HashSet<>(entry.getValue().getFirst()), entry.getValue().getSecond()));
                }
                solutionRepresentation.nextFreeLabel = this.nextFreeLabel;

                return solutionRepresentation;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }
}
