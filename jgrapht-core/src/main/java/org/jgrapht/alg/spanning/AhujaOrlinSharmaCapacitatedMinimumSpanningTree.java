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
import org.jgrapht.graph.GraphWalk;
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

    private enum ImprovementGraphVertexType {
        SINGLE, SUBTREE, PSEUDO, ORIGIN
    }

    private class ImprovementGraph {

        Graph<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> improvementGraph;

        Map<Pair<Integer, ImprovementGraphVertexType>, Integer> cycleAugmentationLabels;

        Map<Integer, V> improvementGraphVertexMapping;
        Map<V, Integer> initialVertexMapping;
        Map<Integer, Pair<Integer, ImprovementGraphVertexType>> pseudoVertexMapping;
        Map<Pair<Integer, ImprovementGraphVertexType>, Integer> pathExchangeVertexMapping;
        Pair<Integer, ImprovementGraphVertexType> origin;

        private ImprovementGraph() {
            this.improvementGraphVertexMapping = new HashMap<>();
            this.initialVertexMapping = new HashMap<>();
            this.pseudoVertexMapping = new HashMap<>();
            this.cycleAugmentationLabels = new Map<Pair<Integer, ImprovementGraphVertexType>, Integer>() {
                @Override
                public int size() {
                    return improvementGraphVertexMapping.size() + pseudoVertexMapping.size();
                }

                @Override
                public boolean isEmpty() {
                    return improvementGraphVertexMapping.isEmpty() || pseudoVertexMapping.isEmpty();
                }

                @Override
                public boolean containsKey(Object key) {
                    return improvementGraphVertexMapping.containsKey(key) || pseudoVertexMapping.containsKey(key);
                }

                @Override
                public boolean containsValue(Object value) {
                    return improvementGraphVertexMapping.containsValue(value) || pseudoVertexMapping.containsValue(value);
                }

                @Override
                public Integer get(Object key) {
                    if(key instanceof Pair && improvementGraphVertexMapping.containsKey(key)) {
                        return solutionRepresentation.getLabel(improvementGraphVertexMapping.get(key));
                    }
                    return pathExchangeVertexMapping.get(key);
                }

                @Override
                public Integer put(Pair<Integer, ImprovementGraphVertexType> key, Integer value) {
                    return null;
                }

                @Override
                public Integer remove(Object key) {
                    return null;
                }

                @Override
                public void putAll(Map<? extends Pair<Integer, ImprovementGraphVertexType>, ? extends Integer> m) {

                }

                @Override
                public void clear() {

                }

                @Override
                public Set<Pair<Integer, ImprovementGraphVertexType>> keySet() {
                    return null;
                }

                @Override
                public Collection<Integer> values() {
                    return null;
                }

                @Override
                public Set<Entry<Pair<Integer, ImprovementGraphVertexType>, Integer>> entrySet() {
                    return null;
                }
            };

            this.improvementGraph = createImprovementGraph();
        }

        private Graph<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> createImprovementGraph() {
            Graph<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> improvementGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

            int counter = 0;

            for(V v : graph.vertexSet()) {
                Pair<Integer, ImprovementGraphVertexType> singleVertex = new Pair<>(counter, ImprovementGraphVertexType.SINGLE);
                improvementGraphVertexMapping.put(counter, v);
                improvementGraph.addVertex(singleVertex);

                Pair<Integer, ImprovementGraphVertexType> subtreeVertex = new Pair<>(counter, ImprovementGraphVertexType.SUBTREE);
                improvementGraphVertexMapping.put(counter, v);
                improvementGraph.addVertex(subtreeVertex);

                counter++;
            }

            Pair<Integer, ImprovementGraphVertexType> origin = new Pair<>(counter, ImprovementGraphVertexType.ORIGIN);
            improvementGraph.addVertex(origin);
            this.origin = origin;
            pathExchangeVertexMapping.put(origin, Integer.MIN_VALUE);
            counter++;

            for(Integer label : solutionRepresentation.getLabels()) {
                Pair<Integer, ImprovementGraphVertexType> pseudoVertex = new Pair<>(counter, ImprovementGraphVertexType.PSEUDO);
                pseudoVertexMapping.put(label, pseudoVertex);
                pathExchangeVertexMapping.put(pseudoVertex, label);
                improvementGraph.addVertex(pseudoVertex);

                counter++;
            }

            /*
             * connection of pseudo nodes and origin node
             */
            for(Pair<Integer, ImprovementGraphVertexType> v : pseudoVertexMapping.values()) {
                improvementGraph.setEdgeWeight(improvementGraph.addEdge(v, origin), 0);
            }

            return improvementGraph;
        }

        private void updateImprovementGraph(Map<V, Pair<Set<V>, Double>> subtrees, Map<Integer, SpanningTree<E>> partitionSpanningTrees, Set<Integer> labelsToUpdate) {

            double newCapacity, newWeight, oldWeight;

            for(V v1 : graph.vertexSet()) {

                Pair<Integer, ImprovementGraphVertexType> vertexOfV1Single = Pair.of(initialVertexMapping.get(v1), ImprovementGraphVertexType.SINGLE);
                Pair<Integer, ImprovementGraphVertexType> vertexOfV1Subtree = Pair.of(initialVertexMapping.get(v1), ImprovementGraphVertexType.SUBTREE);

                /*
                 * update connections to origin node
                 */
                oldWeight = partitionSpanningTrees.get(solutionRepresentation.getLabel(v1)).getWeight();
                // edge for v1 vertex removed
                solutionRepresentation.getPartitionSet(solutionRepresentation.getLabel(v1)).remove(v1);
                newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(solutionRepresentation.getLabel(v1)))).getSpanningTree().getWeight();
                addImprovementGraphEdge(
                        origin,
                        vertexOfV1Single,
                        0,
                        newWeight - oldWeight
                );
                solutionRepresentation.getPartitionSet(solutionRepresentation.getLabel(v1)).add(v1);

                // edge for v1 subtree removed
                solutionRepresentation.getPartitionSet(solutionRepresentation.getLabel(v1)).removeAll(subtrees.get(v1).getFirst());
                newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(solutionRepresentation.getLabel(v1)))).getSpanningTree().getWeight();
                addImprovementGraphEdge(
                        origin,
                        vertexOfV1Subtree,
                        0,
                        newWeight - oldWeight
                );
                solutionRepresentation.getPartitionSet(solutionRepresentation.getLabel(v1)).addAll(subtrees.get(v1).getFirst());

                /*
                 * update the connections to regular nodes
                 */
                for(Integer label : solutionRepresentation.getLabels()) {

                    oldWeight = partitionSpanningTrees.get(label).getWeight();

                    /*
                     * only update if there is a change induced by a changed part. This potentially saves a lot of time.
                     */
                    if (!label.equals(solutionRepresentation.getLabel(v1)) && labelsToUpdate.contains(solutionRepresentation.getLabel(v1)) && labelsToUpdate.contains(label)) {
                        /*
                         * edge for v1 vertex replacing v2 vertex
                         */
                        solutionRepresentation.getPartitionSet(label).add(root);
                        solutionRepresentation.getPartitionSet(label).add(v1);

                        for (V v2 : solutionRepresentation.getPartitionSet(label)) {
                            /*
                             * edge for v1 vertex replacing v2 vertex
                             */
                            newCapacity = solutionRepresentation.getPartitionWeight(label) + weights.get(v1) - weights.get(v2);
                            solutionRepresentation.getPartitionSet(label).remove(v2);
                            newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(label))).getSpanningTree().getWeight();
                            solutionRepresentation.getPartitionSet(label).add(v2);
                            addImprovementGraphEdge(
                                    vertexOfV1Single,
                                    Pair.of(initialVertexMapping.get(v2), ImprovementGraphVertexType.SINGLE),
                                    newCapacity,
                                    newWeight - oldWeight
                            );

                            /*
                             * edge for v1 vertex replacing v2 subtree
                             */
                            newCapacity = solutionRepresentation.getPartitionWeight(label) + weights.get(v1) - subtrees.get(v2).getSecond();
                            solutionRepresentation.getPartitionSet(label).removeAll(subtrees.get(v2).getFirst());
                            newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(label))).getSpanningTree().getWeight();
                            solutionRepresentation.getPartitionSet(label).addAll(subtrees.get(v2).getFirst());
                            addImprovementGraphEdge(
                                    vertexOfV1Single,
                                    Pair.of(initialVertexMapping.get(v2), ImprovementGraphVertexType.SUBTREE),
                                    newCapacity,
                                    newWeight - oldWeight
                            );
                        }

                        /*
                         * edge for v1 vertex replacing no vertex or subtree
                         */
                        Pair<Integer, ImprovementGraphVertexType> pseudoVertex = pseudoVertexMapping.get(label);
                        newCapacity = solutionRepresentation.getPartitionWeight(label) + weights.get(v1);
                        newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(label))).getSpanningTree().getWeight();
                        addImprovementGraphEdge(vertexOfV1Single, pseudoVertex, newCapacity, newWeight - oldWeight);

                        solutionRepresentation.getPartitionSet(label).remove(v1);

                        solutionRepresentation.getPartitionSet(label).addAll(subtrees.get(v1).getFirst());
                        for (V v2 : solutionRepresentation.getPartitionSet(label)) {
                            /*
                             * edge for v1 subtree replacing v2 vertex
                             */
                            newCapacity = solutionRepresentation.getPartitionWeight(label) + subtrees.get(v1).getSecond() - weights.get(v2);
                            solutionRepresentation.getPartitionSet(label).remove(v2);
                            newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(label))).getSpanningTree().getWeight();
                            solutionRepresentation.getPartitionSet(label).add(v2);
                            addImprovementGraphEdge(
                                    vertexOfV1Subtree,
                                    Pair.of(initialVertexMapping.get(v2), ImprovementGraphVertexType.SINGLE),
                                    newCapacity,
                                    newWeight - oldWeight
                            );

                            /*
                             * edge for v1 subtree replacing v2 subtree
                             */
                            newCapacity = solutionRepresentation.getPartitionWeight(solutionRepresentation.getLabel(v2)) + subtrees.get(v1).getSecond() - subtrees.get(v2).getSecond();
                            solutionRepresentation.getPartitionSet(label).removeAll(subtrees.get(v2).getFirst());
                            newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(label))).getSpanningTree().getWeight();
                            solutionRepresentation.getPartitionSet(label).addAll(subtrees.get(v2).getFirst());
                            addImprovementGraphEdge(
                                    vertexOfV1Subtree,
                                    Pair.of(initialVertexMapping.get(v2), ImprovementGraphVertexType.SUBTREE),
                                    newCapacity,
                                    newWeight - oldWeight
                            );
                        }

                        /*
                         * edge for v1 subtree replacing no vertex or subtree
                         */
                        newCapacity = solutionRepresentation.getPartitionWeight(label) + subtrees.get(v1).getSecond();
                        newWeight = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, solutionRepresentation.getPartitionSet(label))).getSpanningTree().getWeight();
                        addImprovementGraphEdge(vertexOfV1Subtree, pseudoVertex, newCapacity, newWeight - oldWeight);

                        solutionRepresentation.getPartitionSet(label).removeAll(subtrees.get(v1).getFirst());

                        solutionRepresentation.getPartitionSet(label).remove(root);
                    }
                }
            }
        }

        private void addImprovementGraphEdge(Pair<Integer, ImprovementGraphVertexType> v1, Pair<Integer, ImprovementGraphVertexType> v2, double newCapacity, double newCost) {
            if (newCapacity < capacity) {
                DefaultWeightedEdge edge;
                if(improvementGraph.containsEdge(v1, v2)) {
                    edge = improvementGraph.getEdge(v1, v2);
                } else {
                    edge = improvementGraph.addEdge(v1, v2);
                }
                improvementGraph.setEdgeWeight(edge, newCost);
            } else {
                improvementGraph.removeEdge(v1, v2);
            }
        }
    }

    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> weights, int lengthBound, int numberOfOperationsParameter) {
        super(graph, root, capacity, weights);
        this.lengthBound = lengthBound;
        this.numberOfOperationsParameter = numberOfOperationsParameter;
    }

    @Override
    public SpanningTree<E> getSpanningTree() {

        // calculates initial solution on which we base the local search
        solutionRepresentation = getInitialSolution();

        // map that contains all spanning trees of the current partition
        Map<Integer, SpanningTree<E>> partitionSpanningTrees = new HashMap<>();
        // map that contains the subtrees of all vertices
        Map<V, Pair<Set<V>, Double>> subtrees = new HashMap<>();
        // set that contains all part of the partition that were affected by an exchange operation
        Set<Integer> affectedParts = solutionRepresentation.getLabels();


        double currentCost;

        // do local improvement steps
        do {

            partitionSpanningTrees = calculateSpanningTrees(partitionSpanningTrees, affectedParts);
            subtrees = calculateSubtrees(subtrees, partitionSpanningTrees, affectedParts);

            ImprovementGraph improvementGraph = new ImprovementGraph();

            AhujaOrlinSharmaCyclicExchangeLocalAugmentation<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> ahujaOrlinSharmaCyclicExchangeLocalAugmentation
                    = new AhujaOrlinSharmaCyclicExchangeLocalAugmentation<>(improvementGraph.improvementGraph, lengthBound, improvementGraph.cycleAugmentationLabels);

            GraphWalk<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> cycle = ahujaOrlinSharmaCyclicExchangeLocalAugmentation.getLocalAugmentationCycle();
            currentCost = cycle.getWeight();

            affectedParts = executeNeighborhoodOperation(improvementGraph.improvementGraphVertexMapping, improvementGraph.pathExchangeVertexMapping, subtrees, cycle);

        } while(currentCost < 0);

        return solutionRepresentation.calculateResultingSpanningTree();
    }

    private SolutionRepresentation getInitialSolution() {
        return new EsauWilliamsCapacitatedMinimumSpanningTree<>(graph, root, capacity, weights, numberOfOperationsParameter).getSolution();
    }

    private Set<Integer> executeNeighborhoodOperation(
            Map<Integer, V> improvementGraphVertexMapping,
            Map<Pair<Integer, ImprovementGraphVertexType>, Integer> pathExchangeVertexMapping,
            Map<V, Pair<Set<V>, Double>> subtrees,
            GraphWalk<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> cycle
    ) {
        Set<Integer> affectedLabels = new HashSet<>();

        Iterator<Pair<Integer, ImprovementGraphVertexType>> it = cycle.getVertexList().iterator();
        if(it.hasNext()) {
            Pair<Integer, ImprovementGraphVertexType> cur = it.next();
            if(it.hasNext()) {
                while (it.hasNext()) {
                    Pair<Integer, ImprovementGraphVertexType> next = it.next();

                    switch(cur.getSecond()) {
                        /*
                         * A vertex is moved form the part of cur to the part of next. Therefore, both parts are affected.
                         * We only consider the label of cur to be affected for now, the label of next will be add to the affected set in the next iteration.
                         */
                        case SINGLE: {
                            V curVertex = improvementGraphVertexMapping.get(cur.getFirst());
                            Integer curLabel = solutionRepresentation.getLabel(curVertex);

                            affectedLabels.add(curLabel);

                            solutionRepresentation.moveVertex(
                                    curVertex,
                                    curLabel,
                                    solutionRepresentation.getLabel(improvementGraphVertexMapping.get(next.getFirst()))
                            );
                            break;
                        }
                        /*
                         * A subtree is moved from the part of cur to the part of next. Therefore, the part of cur is affected.
                         */
                        case SUBTREE: {
                            V curVertex = improvementGraphVertexMapping.get(cur.getFirst());
                            Integer curLabel = solutionRepresentation.getLabel(curVertex);

                            affectedLabels.add(curLabel);

                            // get the whole subtree that has to be moved
                            Set<V> subtreeToMove = subtrees.get(curVertex).getFirst();
                            solutionRepresentation.moveVertices(
                                    subtreeToMove,
                                    curLabel,
                                    solutionRepresentation.getLabel(improvementGraphVertexMapping.get(next.getFirst()))
                            );
                            break;
                        }
                        /*
                         * cur is the end of a path exchange. Thus, the part of cur is affected because vertices were inserted.
                         */
                        case PSEUDO: {
                            Integer curLabel = pathExchangeVertexMapping.get(cur);
                            affectedLabels.add(curLabel);
                            break;
                        }
                        /*
                         * This is the beginning of a path exchange. We have nothing to do.
                         */
                        case ORIGIN: {

                            break;
                        }
                        default: throw new IllegalStateException("This is a bug. There are invalid types of vertices in the cycle.");
                    }

                    cur = next;
                }
            }
        }

        return affectedLabels;
    }

    private Map<Integer, SpanningTree<E>> calculateSpanningTrees(Map<Integer, SpanningTree<E>> partitionSpanningTrees, Set<Integer> affectedLabels) {
        for(Integer label : affectedLabels) {
            Set<V> set = solutionRepresentation.getPartitionSet(label);
            solutionRepresentation.getPartitionSet(label).add(root);
            partitionSpanningTrees.put(label, new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, set)).getSpanningTree());
            solutionRepresentation.getPartitionSet(label).remove(root);
        }
        return partitionSpanningTrees;
    }

    private Map<V, Pair<Set<V>, Double>> calculateSubtrees(Map<V, Pair<Set<V>, Double>> subtrees, Map<Integer, SpanningTree<E>> partitionSpanningTree, Set<Integer> affectedLabels) {
        for(Integer label : affectedLabels) {
            for (V v : solutionRepresentation.getPartitionSet(label)) {
                Pair<Set<V>, Double> currentSubtree = subtree(v, partitionSpanningTree);
                subtrees.put(v, currentSubtree);
            }
        }
        return subtrees;
    }

    private Pair<Set<V>, Double> subtree(V v, Map<Integer, SpanningTree<E>> partitionSpanningTree) {
        Set<V> partVertices = solutionRepresentation.getPartitionSet(solutionRepresentation.getLabel(v));
        SpanningTree<E> partSpanningTree = partitionSpanningTree.get(solutionRepresentation.getLabel(v));
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
