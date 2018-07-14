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
import org.jgrapht.alg.util.Pair;

import java.util.*;

/**
 * Implementation of a randomized version of the Esau-Williams heuristic, a greedy randomized adaptive search heuristic
 * (GRASP) for the capacitated minimum spanning tree (CMST) problem. It calculates a suboptimal CMST.
 * The original version can be found in
 * L. R. Esau and K. C. Williams. 1966. On teleprocessing system design: part II a method for approximating the optimal network.
 * IBM Syst. J. 5, 3 (September 1966), 142-147. DOI=http://dx.doi.org/10.1147/sj.53.0142
 * This implementation runs in polynomial time O(n^3).
 *
 * This implementation uses a GRASP version described in
 * Ahuja, Ravindra K., Orlin, James B., and Sharma, Dushyant, (1998).
 * New neighborhood search structures for the capacitated minimum spanning tree problem, No WP 4040-98.
 * Working papers, Massachusetts Institute of Technology (MIT), Sloan School of Management.
 *
 * This version runs in polynomial time dependent on the number of considered operations per iteration
 * <code>numberOfOperationsParameter</code> (denoted by p), such that it is in O(n^3 + p*n).
 *
 * The <a href="https://en.wikipedia.org/wiki/Capacitated_minimum_spanning_tree">Capacitated Minimum Spanning Tree</a>
 * (CMST) problem is a rooted minimal cost spanning tree that statisfies the capacity
 * constrained on all trees that are connected by the designated root. The problem is NP-hard.
 * The hard part of the problem is the implicit partition defined by the subtrees.
 * If one can find the correct partition, the MSTs can be calculated in polynomial time.
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 *
 * @author Christoph Grüne
 * @since July 12, 2018
 */
public class EsauWilliamsGRASPCapacitatedMinimumSpanningTree<V, E> extends AbstractCapacitatedMinimumSpanningTree<V, E> {

    /**
     * the number of the most profitable operations for every iteration considered in the GRASP procedure.
     */
    private final int numberOfOperationsParameter;

    /**
     * Constructs an Esau-Williams GRASP algorithm instance.
     *
     * @param graph the graph
     * @param root the root of the CMST
     * @param capacity the capacity constraint of the CMST
     * @param weights the weights of the vertices
     * @param numberOfOperationsParameter the parameter how many best vertices are considered in the GRASP procedure
     */
    public EsauWilliamsGRASPCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> weights, int numberOfOperationsParameter) {
        super(graph, root, capacity, weights);
        this.numberOfOperationsParameter = numberOfOperationsParameter;
    }

    /**
     * {@inheritDoc}
     *
     * Returns a capacitated spanning tree computed by the Esau-Williams algorithm.
     */
    @Override
    public SpanningTree<E> getSpanningTree() {
        return getSolution().calculateResultingSpanningTree();
    }

    /**
     * Calculates a partition representation of the capacitated spanning tree. With that, it is possible to calculate a
     * capacitated miniumum spanning tree in polynomial time.
     *
     * @return a representation of the partition of the capacitated spanning tree.
     */
    protected SolutionRepresentation getSolution() {
        /*
         * labeling of the improvement graph vertices. There are two vertices in the improvement graph for every vertex
         * in the input graph: the vertex indicating the vertex itself and the vertex indicating the subtree.
         */
        Map<V, Integer> labels = new HashMap<>();
        // the implicit partition defined by the subtrees
        Map<Integer, Pair<Set<V>, Double>> partition = new HashMap<>();

        /*
         * initialize labels and partitions by assigning every vertex to a new part and create solution representation
         */
        int counter = 0;
        for(V v : graph.vertexSet()) {
            labels.put(v, counter);
            Set<V> currentPart = new HashSet<>();
            currentPart.add(v);
            partition.put(counter, Pair.of(currentPart, weights.get(v)));
            counter++;
        }
        solutionRepresentation = new SolutionRepresentation(labels, partition);

        Map<V, Double> savings = new HashMap<>();
        Map<V, V> closestVertex = new HashMap<>();
        Map<V, Set<Integer>> restriction = new HashMap<>();

        Set<V> vertices = new HashSet<>(graph.vertexSet());
        vertices.remove(root);

        while(true) {
            for(Iterator<V> it = vertices.iterator(); it.hasNext();) {

                V v = it.next();

                V closestVertexToV = calculateClosestVertex(v, restriction.get(v));

                if(closestVertexToV == null) {
                    // there is not valid closest vertex to connect with, i.e. v will not be connected to any vertex
                    it.remove();
                    savings.remove(v);
                    continue;
                }

                // store closest vertex to v1
                closestVertex.put(v, closestVertexToV);
                // store the maximum trade off and the corresponding vertex
                savings.put(v, graph.getEdgeWeight(graph.getEdge(v, root)) - graph.getEdgeWeight(graph.getEdge(v, closestVertexToV)));
            }

            // calculate list of best operations
            ArrayList<V> bestVertices = getListOfBestOptions(savings);

            if(!bestVertices.isEmpty()) {
                V vertexToMove = bestVertices.get((int) (Math.random() * bestVertices.size()));
                solutionRepresentation.moveVertex(vertexToMove, solutionRepresentation.getLabelOfVertex(vertexToMove), solutionRepresentation.getLabelOfVertex(closestVertex.get(vertexToMove)));

                // delete last connected vertices from vertices
                vertices.remove(vertexToMove);
                vertices.remove(closestVertex.get(vertexToMove));

                // delete last connected vertices from savings to save time
                savings.remove(vertexToMove);
                savings.remove(closestVertex.get(vertexToMove));

                // delete positive savings from vertices and savings, they will never be included
                for(V v : savings.keySet()) {
                    if(savings.get(v) >= 0) {
                        vertices.remove(v);
                        savings.remove(v);
                    }
                }
            } else {
                break;
            }
        }

        return new SolutionRepresentation(labels, partition);
    }

    /**
     * Returns the list of the best options as stored in <code>savings</code>.
     *
     * @param savings the savings calculated in the algorithm (see getSolution())
     *
     * @return the list of the <code>numberOfOperationsParameter</code> best options
     */
    private ArrayList<V> getListOfBestOptions(Map<V, Double> savings) {
        ArrayList<V> bestVertices = new ArrayList<>();

        for (Map.Entry<V, Double> entry : savings.entrySet()) {
            /*
             * insert current tradeOffFunction entry at the position such that the list is order by the tradeOff
             * and the size of the list is at most numberOfOperationsParameter
             */
            int position = 0;
            for (V v : bestVertices) {
                if (savings.get(v) > entry.getValue()) {
                    break;
                }
                position++;
            }
            if (bestVertices.size() == numberOfOperationsParameter) {
                bestVertices.remove(bestVertices.size() - 1);
            }
            bestVertices.add(position, entry.getKey());

        }

        return bestVertices;
    }

    /**
     * Calculates the closest vertex to <code>vertex</code> such that the connection of <code>vertex</code> to the
     * subtree of the closest vertex does not violate the cpacity constraint and the savings are negative.
     *
     * @param vertex the vertex to find a valid closest vertex for
     * @param restriction the set of labels of sets of the partition, in which the capacity constraint is violated.
     *
     * @return the closest valid vertex.
     */
    private V calculateClosestVertex(V vertex, Set<Integer> restriction) {
        V closestVertexToV1 = null;
        double minDistance = 0;

        // calculate closest vertex to v1
        for(Integer label : solutionRepresentation.getLabels()) {
            if(!restriction.contains(label)) {
                Set<V> part = solutionRepresentation.getPartitionSet(label);
                if(!part.contains(vertex)) {
                    for (V v2 : part) {
                        if (graph.containsEdge(vertex, v2)) {
                            if(solutionRepresentation.getPartitionWeight(solutionRepresentation.getLabelOfVertex(vertex))
                                + weights.get(v2) > capacity) {
                                double currentEdgeWeight = graph.getEdgeWeight(graph.getEdge(vertex, v2));
                                if (currentEdgeWeight < minDistance) {
                                    closestVertexToV1 = v2;
                                    minDistance = currentEdgeWeight;
                                }
                            } else {
                                restriction.add(solutionRepresentation.getLabelOfVertex(v2));
                            }
                        }
                    }
                }
            }
        }

        return closestVertexToV1;
    }
}
