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
import org.jgrapht.alg.cycle.AhujaOrlinSharmaCyclicExchangeLocalAugmentation;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
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
public class AhujaOrlinSharmaCapacitatedMinimumSpanningTree<V, E> implements SpanningTreeAlgorithm<E> {

    /**
     * the input graph.
     */
    private final Graph<V, E> graph;

    /**
     * the designated root of the CMST.
     */
    private final V root;

    /**
     * the maximal capacity for each subtree.
     */
    private final double capacity;

    /**
     * the weight function over all vertices.
     */
    private final Map<V, Double> weights;

    /**
     * the maximal length of the cycle in the neighborhood
     */
    private final int lengthBound;

    /**
     * the number of the most profitable operations considered in the GRASP procedure for the initial solution.
     */
    private final int numberOfOperationsParameter;

    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> weights, int lengthBound, int numberOfOperationsParameter) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.root = Objects.requireNonNull(root, "Root cannot be null");
        this.capacity = capacity;
        this.weights = Objects.requireNonNull(weights, "Weight cannot be null");
        this.lengthBound = lengthBound;
        this.numberOfOperationsParameter = numberOfOperationsParameter;
    }

    @Override
    public SpanningTree<E> getSpanningTree() {

        /*
         * labeling of the improvement graph vertices. There are two vertices in the improvement graph for every vertex
         * in the input graph: the vertex indicating the vertex itself and the vertex indicating the subtree.
         */
        Map<V, Integer> labels = new HashMap<>();
        /*
         * this map manages pointers to Map<V, Integer> labels such that the local cycle augmentation algorithm can use
         * it, whereby this map only supports read access to the implemented methods.
         */
        Map<Pair<V, Integer>, Integer> cycleAugmentationLabels = new Map<Pair<V, Integer>, Integer>() {
            @Override
            public int size() {
                return 2*labels.size();
            }

            @Override
            public boolean isEmpty() {
                return labels.isEmpty();
            }

            @Override
            public boolean containsKey(Object key) {
                if(key instanceof Pair) {
                    return labels.containsKey(((Pair) key).getFirst());
                }
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return labels.containsValue(value);
            }

            @Override
            public Integer get(Object key) {
                if(key instanceof Pair) {
                    return labels.get(((Pair) key).getFirst());
                }
                return null;
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
        // the implicit partition defined by the subtrees
        Map<Integer, Pair<Set<V>, Double>> partition = new HashMap<>();

        Map<V, Pair<Set<V>, Double>> subtrees = calculateSubtrees(labels, partition);

        // calculates initial solution on which we base the local search
        getInitialPartition(labels, partition);

        double currentCost;

        // do local improvement steps
        do {

            Graph<Pair<V, Integer>, DefaultWeightedEdge> improvementGraph = buildImprovementGraph(labels, partition, subtrees);

            AhujaOrlinSharmaCyclicExchangeLocalAugmentation<Pair<V, Integer>, DefaultWeightedEdge> ahujaOrlinSharmaCyclicExchangeLocalAugmentation
                    = new AhujaOrlinSharmaCyclicExchangeLocalAugmentation<>(improvementGraph, lengthBound, cycleAugmentationLabels);

            AhujaOrlinSharmaCyclicExchangeLocalAugmentation.LabeledPath<Pair<V, Integer>> cycle = ahujaOrlinSharmaCyclicExchangeLocalAugmentation.getLocalAugmentationCycle();
            currentCost = cycle.getCost();

            executeNeighborhoodOperation(labels, partition, subtrees, cycle);

        } while(currentCost < 0);

        return calculateResultingSpanningTree(partition);
    }

    private void getInitialPartition(Map<V, Integer> labels, Map<Integer, Pair<Set<V>, Double>> partition) {
        Pair<Map<V, Integer>, Map<Integer, Pair<Set<V>, Double>>> initialPartition
                = new EsauWilliamsGRASPCapacitatedMinimumSpanningTree<>(graph, root, capacity, weights, numberOfOperationsParameter).getPartition();

        labels = initialPartition.getFirst();
        partition = initialPartition.getSecond();
    }

    private Graph<Pair<V, Integer>, DefaultWeightedEdge> buildImprovementGraph(Map<V, Integer> labels, Map<Integer, Pair<Set<V>, Double>> partition, Map<V, Pair<Set<V>, Double>> subtrees) {
        Graph<Pair<V, Integer>, DefaultWeightedEdge> improvementGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        for(V v : graph.vertexSet()) {
            improvementGraph.addVertex(new Pair<>(v, 0));
            improvementGraph.addVertex(new Pair<>(v, 1));
        }

        Map<Integer, Set<V>> modifiablePartition = new HashMap<>();
        for(Integer i : modifiablePartition.keySet()) {
            modifiablePartition.put(i, new HashSet<>(partition.get(i).getFirst()));
        }

        for(V v1 : graph.vertexSet()) {

            Pair<V, Integer> improvementGraphVertexOfV = new Pair<>(v1, 0);
            Pair<V, Integer> improvementGraphVertexOfVTree = new Pair<>(v1, 1);

            for(Integer i : partition.keySet()) {

                if(!i.equals(labels.get(v1))) {

                    // TODO kanten, die nicht zu einer zulässigen lösung beitragen, müssen noch entfernt werden!!!
                    // TODO kanten brauchen gewichte

                    modifiablePartition.get(i).add(v1);

                    for(V v2 : partition.get(i).getFirst()) {

                        /*
                         * edge for v1 vertex replacing v2 vertex
                         */
                        if(partition.get(labels.get(v2)).getSecond() + weights.get(v1) - weights.get(v2) < capacity) {
                            DefaultWeightedEdge edge = improvementGraph.addEdge(improvementGraphVertexOfV, new Pair<>(v2, 0));
                            modifiablePartition.get(i).remove(v2);
                            new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiablePartition.get(i))).getSpanningTree().getWeight();
                            modifiablePartition.get(i).add(v2);
                        }

                        /*
                         * edge for v1 vertex replacing v2 subtree
                         */
                        if(partition.get(labels.get(v2)).getSecond() + weights.get(v1) - subtrees.get(v2).getSecond() < capacity) {
                            DefaultWeightedEdge edge = improvementGraph.addEdge(improvementGraphVertexOfVTree, new Pair<>(v2, 0));
                            modifiablePartition.get(i).removeAll(subtrees.get(v2).getFirst());
                            new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiablePartition.get(i))).getSpanningTree().getWeight();
                            modifiablePartition.get(i).addAll(subtrees.get(v2).getFirst());
                        }
                    }

                    modifiablePartition.get(i).remove(v1);

                    modifiablePartition.get(i).addAll(subtrees.get(v1).getFirst());

                    for(V v2 : partition.get(i).getFirst()) {
                        /*
                         * edge for v1 subtree replacing v2 vertex
                         */
                        if(partition.get(labels.get(v2)).getSecond() + subtrees.get(v1).getSecond() - weights.get(v2) < capacity) {
                            DefaultWeightedEdge edge = improvementGraph.addEdge(improvementGraphVertexOfV, new Pair<>(v2, 1));
                            modifiablePartition.get(i).remove(v2);
                            new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiablePartition.get(i))).getSpanningTree().getWeight();
                            modifiablePartition.get(i).add(v2);
                        }

                        /*
                         * edge for v1 subtree replacing v2 subtree
                         */
                        if(partition.get(labels.get(v2)).getSecond() + subtrees.get(v1).getSecond() - subtrees.get(v2).getSecond() < capacity) {
                            DefaultWeightedEdge edge = improvementGraph.addEdge(improvementGraphVertexOfVTree, new Pair<>(v2, 1));
                            modifiablePartition.get(i).removeAll(subtrees.get(v2).getFirst());
                            new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiablePartition.get(i))).getSpanningTree().getWeight();
                            modifiablePartition.get(i).addAll(subtrees.get(v2).getFirst());
                        }
                    }

                    modifiablePartition.get(i).removeAll(subtrees.get(v1).getFirst());

                }
            }
        }

        return improvementGraph;
    }

    private void executeNeighborhoodOperation(Map<V, Integer> labels,
                                              Map<Integer, Pair<Set<V>, Double>> partition,
                                              Map<V, Pair<Set<V>, Double>> subtrees,
                                              AhujaOrlinSharmaCyclicExchangeLocalAugmentation.LabeledPath<Pair<V, Integer>> cycle) {
        Iterator<Pair<V, Integer>> it = cycle.getVertices().iterator();
        if(it.hasNext()) {
            V cur = it.next().getFirst();
            if(it.hasNext()) {
                while (it.hasNext()) {
                    V next = it.next().getFirst();

                    if (cur.equals(0)) {
                        labels.put(cur, labels.get(next));
                        partition.get(labels.get(cur)).getFirst().remove(cur);
                        partition.get(labels.get(next)).getFirst().add(cur);
                    } else {
                        //TODO update weight of tree

                        // get the whole subtree that has to be moved
                        Pair<Set<V>, Double> subtreeToMove = subtrees.get(cur);
                        // update tree by merging both trees
                        partition.get(labels.get(next)).getFirst().addAll(subtreeToMove.getFirst());
                        Pair<Set<V>, Double> newSubtree = Pair.of(partition.get(labels.get(next)).getFirst(), partition.get(labels.get(next)).getSecond() + subtreeToMove.getSecond());
                        partition.put(labels.get(next), newSubtree);
                        // update labels
                        for(V v : subtreeToMove.getFirst()) {
                            labels.put(v, labels.get(next));
                        }
                        // remove merged part from partition
                        if(partition.get(labels.get(cur)).getFirst().isEmpty()) {
                            partition.remove(labels.get(cur));
                        }
                    }

                    cur = next;
                }
            }
        }
    }

    private Map<V, Pair<Set<V>, Double>> calculateSubtrees(Map<V, Integer> labels, Map<Integer, Pair<Set<V>, Double>> partition) {
        Map<Integer, SpanningTree<E>> partitionSpanningTree = new HashMap<>();
        for(Map.Entry<Integer, Pair<Set<V>, Double>> entry : partition.entrySet()) {
            partitionSpanningTree.put(entry.getKey(), new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, entry.getValue().getFirst())).getSpanningTree());
        }

        Map<V, Pair<Set<V>, Double>> subtrees = new HashMap<>();
        for(V v : graph.vertexSet()) {
            Pair<Set<V>, Double> currentSubtree = subtree(v, labels, partition, partitionSpanningTree);
            subtrees.put(v, currentSubtree);
        }
        return subtrees;
    }

    private Pair<Set<V>, Double> subtree(V v, Map<V, Integer> labels, Map<Integer, Pair<Set<V>, Double>> partition, Map<Integer, SpanningTree<E>> partitionSpanningTree) {
        Set<V> partVertices = partition.get(labels.get(v)).getFirst();
        SpanningTree<E> partSpanningTree = partitionSpanningTree.get(labels.get(v));
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

    private SpanningTree<E> calculateResultingSpanningTree(Map<Integer, Pair<Set<V>, Double>> partition) {
        Set<E> spanningTreeEdges = new HashSet<>();
        double weight = 0;

        for(Pair<Set<V>, Double> part : partition.values()) {
            Set<V> set = part.getFirst();
            SpanningTree<E> subtree = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, set)).getSpanningTree();
            E minimalEdgeToRoot = null;
            for(E e : graph.outgoingEdgesOf(root)) {
                if(minimalEdgeToRoot == null || graph.getEdgeWeight(minimalEdgeToRoot) < graph.getEdgeWeight(e)) {
                    minimalEdgeToRoot = e;
                }
            }
            if(minimalEdgeToRoot == null) {
                throw new IllegalStateException("There is no edge from the root to a subtree. This is impossible. This is a bug.");
            }

            spanningTreeEdges.addAll(subtree.getEdges());
            weight += subtree.getWeight();
            spanningTreeEdges.add(minimalEdgeToRoot);
            weight += graph.getEdgeWeight(minimalEdgeToRoot);
        }

        return new SpanningTreeImpl<>(spanningTreeEdges, weight);
    }
}
