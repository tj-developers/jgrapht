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
import org.jgrapht.alg.cycle.AhujaOrlinSharmaCyclicExchangeLocalAugmentation;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;

/**
 * Implementation of an algorithm for the capacitated minimum spanning tree problem using a cyclic exchange neighborhood,
 * based on
 * Ravindra K. Ahuja, James B. Orlin, Dushyant Sharma,
 * A composite very large-scale neighborhood structure for the capacitated minimum spanning tree problem,
 * Operations Research Letters, Volume 31, Issue 3, 2003, Pages 185-194, ISSN 0167-6377,
 * https://doi.org/10.1016/S0167-6377(02)00236-5. (http://www.sciencedirect.com/science/article/pii/S0167637702002365)
 *
 * The <a href="https://en.wikipedia.org/wiki/Capacitated_minimum_spanning_tree">Capacitated Minimum Spanning Tree</a>
 * (CMST) problem is a rooted minimal cost spanning tree that statisfies the capacity
 * constrained on all trees that are connected by the designated root. The problem is NP-hard.
 * The hard part of the problem is the implicit partition defined by the subtrees.
 * If one can find the correct partition, the MSTs can be calculated in polynomial time.
 *
 * This algorithm is a very large scale neighborhood search algorithm using a cyclic exchange neighborhood
 * until a local minimum is found. It makes frequently use of a MST algorithm and the algorithm for subset
 * disjoint cycles by Ahuja et al.
 * That is, the algorithm may run in exponential time.
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 *
 * @author Christoph Grüne
 * @since July 11, 2018
 */
public class AhujaOrlinSharmaCapacitatedMinimumSpanningTree<V, E> extends AbstractCapacitatedMinimumSpanningTree<V, E> {

    /**
     * the maximal length of the cycle in the neighborhood
     */
    private final int lengthBound;

    /**
     * the number of the most profitable operations considered in the GRASP procedure for the initial solution.
     */
    private final int numberOfOperationsParameter;

    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> weights, int lengthBound, int numberOfOperationsParameter) {
        super(graph, root, capacity, weights);
        this.lengthBound = lengthBound;
        this.numberOfOperationsParameter = numberOfOperationsParameter;
    }

    @Override
    public SpanningTree<E> getSpanningTree() {

        /*
         * this map manages pointers to Map<V, Integer> labels such that the local cycle augmentation algorithm can use
         * it, whereby this map only supports read access to the implemented methods.
         */
        Map<Pair<V, Integer>, Integer> cycleAugmentationLabels = new Map<Pair<V, Integer>, Integer>() {
            @Override
            public int size() {
                return solutionRepresentation.sizeOfLabel();
            }

            @Override
            public boolean isEmpty() {
                return solutionRepresentation.isLabelEmpty();
            }

            @Override
            public boolean containsKey(Object key) {
                return solutionRepresentation.containsLabelKey(key);
            }

            @Override
            public boolean containsValue(Object value) {
                return solutionRepresentation.containsLabelValue(value);
            }

            @Override
            public Integer get(Object key) {
                return solutionRepresentation.getLabel(key);
            }

            @Override
            public Integer put(Pair<V, Integer> key, Integer value) {
                throw new RuntimeException("This method is not implemented.");
            }

            @Override
            public Integer remove(Object key) {
                throw new RuntimeException("This method is not implemented.");
            }

            @Override
            public void putAll(Map<? extends Pair<V, Integer>, ? extends Integer> m) {
                throw new RuntimeException("This method is not implemented.");
            }

            @Override
            public void clear() {
                throw new RuntimeException("This method is not implemented.");
            }

            @Override
            public Set<Pair<V, Integer>> keySet() {
                throw new RuntimeException("This method is not implemented.");
            }

            @Override
            public Collection<Integer> values() {
                throw new RuntimeException("This method is not implemented.");
            }

            @Override
            public Set<Entry<Pair<V, Integer>, Integer>> entrySet() {
                throw new RuntimeException("This method is not implemented.");
            }
        };

        // calculates initial solution on which we base the local search
        solutionRepresentation = getInitialSolution();

        //calculates all spanning trees of the current partition
        Map<Integer, SpanningTree<E>> partitionSpanningTrees;
        // calculates the subtrees of all vertices
        Map<V, Pair<Set<V>, Double>> subtrees;

        double currentCost;

        // do local improvement steps
        do {

            partitionSpanningTrees = calculateSpanningTrees();
            subtrees = calculateSubtrees(partitionSpanningTrees);

            Graph<Pair<V, Integer>, DefaultWeightedEdge> improvementGraph = buildImprovementGraph(subtrees, partitionSpanningTrees);

            AhujaOrlinSharmaCyclicExchangeLocalAugmentation<Pair<V, Integer>, DefaultWeightedEdge> ahujaOrlinSharmaCyclicExchangeLocalAugmentation
                    = new AhujaOrlinSharmaCyclicExchangeLocalAugmentation<>(improvementGraph, lengthBound, cycleAugmentationLabels);

            AhujaOrlinSharmaCyclicExchangeLocalAugmentation.LabeledPath<Pair<V, Integer>> cycle = ahujaOrlinSharmaCyclicExchangeLocalAugmentation.getLocalAugmentationCycle();
            currentCost = cycle.getCost();

            executeNeighborhoodOperation(subtrees, cycle);

        } while(currentCost < 0);

        return solutionRepresentation.calculateResultingSpanningTree();
    }

    private SolutionRepresentation getInitialSolution() {
        return new EsauWilliamsGRASPCapacitatedMinimumSpanningTree<>(graph, root, capacity, weights, numberOfOperationsParameter).getSolution();
    }

    private Graph<Pair<V, Integer>, DefaultWeightedEdge> buildImprovementGraph(Map<V, Pair<Set<V>, Double>> subtrees, Map<Integer, SpanningTree<E>> partitionSpanningTrees) {
        Graph<Pair<V, Integer>, DefaultWeightedEdge> improvementGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        for(V v : graph.vertexSet()) {
            improvementGraph.addVertex(new Pair<>(v, 0));
            improvementGraph.addVertex(new Pair<>(v, 1));
        }

        for(V v1 : graph.vertexSet()) {

            Pair<V, Integer> improvementGraphVertexOfV = new Pair<>(v1, 0);
            Pair<V, Integer> improvementGraphVertexOfVTree = new Pair<>(v1, 1);

            for(Integer label : solutionRepresentation.getLabels()) {

                solutionRepresentation.getPartitionSet(label).add(root);

                if(!label.equals(solutionRepresentation.getLabelOfVertex(v1))) {

                    solutionRepresentation.getPartitionSet(label).add(v1);

                    for(V v2 : solutionRepresentation.getPartitionSet(label)) {

                        /*
                         * edge for v1 vertex replacing v2 vertex
                         */
                        if(solutionRepresentation.getPartitionWeight(solutionRepresentation.getLabelOfVertex(v2)) + weights.get(v1) - weights.get(v2) < capacity) {
                            DefaultWeightedEdge edge = improvementGraph.addEdge(improvementGraphVertexOfV, new Pair<>(v2, 0));
                            solutionRepresentation.getPartitionSet(label).remove(v2);
                            double newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(label))).getSpanningTree().getWeight();
                            solutionRepresentation.getPartitionSet(label).add(v2);

                            double oldWeight = partitionSpanningTrees.get(label).getWeight();

                            improvementGraph.setEdgeWeight(edge, newWeight - oldWeight);
                        }

                        /*
                         * edge for v1 vertex replacing v2 subtree
                         */
                        if(solutionRepresentation.getPartitionWeight(solutionRepresentation.getLabelOfVertex(v2)) + weights.get(v1) - subtrees.get(v2).getSecond() < capacity) {
                            DefaultWeightedEdge edge = improvementGraph.addEdge(improvementGraphVertexOfVTree, new Pair<>(v2, 0));
                            solutionRepresentation.getPartitionSet(label).removeAll(subtrees.get(v2).getFirst());
                            double newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(label))).getSpanningTree().getWeight();
                            solutionRepresentation.getPartitionSet(label).addAll(subtrees.get(v2).getFirst());

                            double oldWeight = partitionSpanningTrees.get(label).getWeight();

                            improvementGraph.setEdgeWeight(edge, newWeight - oldWeight);
                        }
                    }

                    solutionRepresentation.getPartitionSet(label).remove(v1);

                    solutionRepresentation.getPartitionSet(label).addAll(subtrees.get(v1).getFirst());

                    for(V v2 : solutionRepresentation.getPartitionSet(label)) {
                        /*
                         * edge for v1 subtree replacing v2 vertex
                         */
                        if(solutionRepresentation.getPartitionWeight(solutionRepresentation.getLabelOfVertex(v2)) + subtrees.get(v1).getSecond() - weights.get(v2) < capacity) {
                            DefaultWeightedEdge edge = improvementGraph.addEdge(improvementGraphVertexOfV, new Pair<>(v2, 1));
                            solutionRepresentation.getPartitionSet(label).remove(v2);
                            double newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(label))).getSpanningTree().getWeight();
                            solutionRepresentation.getPartitionSet(label).add(v2);

                            double oldWeight = partitionSpanningTrees.get(label).getWeight();

                            improvementGraph.setEdgeWeight(edge, newWeight - oldWeight);
                        }

                        /*
                         * edge for v1 subtree replacing v2 subtree
                         */
                        if(solutionRepresentation.getPartitionWeight(solutionRepresentation.getLabelOfVertex(v2)) + subtrees.get(v1).getSecond() - subtrees.get(v2).getSecond() < capacity) {
                            DefaultWeightedEdge edge = improvementGraph.addEdge(improvementGraphVertexOfVTree, new Pair<>(v2, 1));
                            solutionRepresentation.getPartitionSet(label).removeAll(subtrees.get(v2).getFirst());
                            double newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(label))).getSpanningTree().getWeight();
                            solutionRepresentation.getPartitionSet(label).addAll(subtrees.get(v2).getFirst());

                            double oldWeight = partitionSpanningTrees.get(label).getWeight();

                            improvementGraph.setEdgeWeight(edge, newWeight - oldWeight);
                        }
                    }

                    solutionRepresentation.getPartitionSet(label).removeAll(subtrees.get(v1).getFirst());

                    solutionRepresentation.getPartitionSet(label).remove(root);
                }
            }
        }

        return improvementGraph;
    }

    private void executeNeighborhoodOperation(Map<V, Pair<Set<V>, Double>> subtrees,
                                              AhujaOrlinSharmaCyclicExchangeLocalAugmentation.LabeledPath<Pair<V, Integer>> cycle) {
        Iterator<Pair<V, Integer>> it = cycle.getVertices().iterator();
        if(it.hasNext()) {
            V cur = it.next().getFirst();
            if(it.hasNext()) {
                while (it.hasNext()) {
                    V next = it.next().getFirst();

                    if (cur.equals(0)) {
                        solutionRepresentation.moveVertex(cur, solutionRepresentation.getLabelOfVertex(cur), solutionRepresentation.getLabelOfVertex(next));
                    } else {
                        // get the whole subtree that has to be moved
                        Set<V> subtreeToMove = subtrees.get(cur).getFirst();
                        solutionRepresentation.moveVertices(subtreeToMove, solutionRepresentation.getLabelOfVertex(cur), solutionRepresentation.getLabelOfVertex(next));
                    }
                    cur = next;
                }
            }
        }
    }

    private Map<Integer, SpanningTree<E>> calculateSpanningTrees() {
        Map<Integer, SpanningTree<E>> partitionSpanningTrees = new HashMap<>();
        for(Integer label : solutionRepresentation.getLabels()) {
            Set<V> set = solutionRepresentation.getPartitionSet(label);
            solutionRepresentation.getPartitionSet(label).add(root);
            partitionSpanningTrees.put(label, new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, set)).getSpanningTree());
            solutionRepresentation.getPartitionSet(label).remove(root);
        }
        return partitionSpanningTrees;
    }

    private Map<V, Pair<Set<V>, Double>> calculateSubtrees(Map<Integer, SpanningTree<E>> partitionSpanningTree) {
        Map<V, Pair<Set<V>, Double>> subtrees = new HashMap<>();
        for(V v : graph.vertexSet()) {
            Pair<Set<V>, Double> currentSubtree = subtree(v, partitionSpanningTree);
            subtrees.put(v, currentSubtree);
        }
        return subtrees;
    }

    private Pair<Set<V>, Double> subtree(V v, Map<Integer, SpanningTree<E>> partitionSpanningTree) {
        Set<V> partVertices = solutionRepresentation.getPartitionSet(solutionRepresentation.getLabelOfVertex(v));
        SpanningTree<E> partSpanningTree = partitionSpanningTree.get(solutionRepresentation.getLabelOfVertex(v));
        Graph<V, E> spanningTree = new AsSubgraph<>(graph, partVertices, partSpanningTree.getEdges());

        Set<V> subtree = new HashSet<>();
        double subtreeWeight = 0;

        Iterator<V> depthFirstIterator = new DepthFirstIterator<>(spanningTree, v);
        Set<V> currentPath = new HashSet<>();
        double currentWeight = 0;

        boolean storeCurrentPath = true;
        while(depthFirstIterator.hasNext()) {
            V next = depthFirstIterator.next();
            if(spanningTree.containsEdge(next, v)) {
                storeCurrentPath = true;

                subtree.addAll(currentPath);
                subtreeWeight += currentWeight;

                currentPath = new HashSet<>();
                currentWeight = 0;
            }
            if(next == root) {
                storeCurrentPath = false;

                currentPath = new HashSet<>();
                currentWeight = 0;
            }
            if(storeCurrentPath) {
                currentPath.add(next);
                currentWeight += weights.get(next);
            }
        }
        return Pair.of(subtree, subtreeWeight);
    }
}
