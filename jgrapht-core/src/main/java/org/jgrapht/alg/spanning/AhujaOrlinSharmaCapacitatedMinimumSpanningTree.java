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
    private Graph<V, E> graph;

    /**
     * the designated root of the CMST.
     */
    private V root;

    /**
     * the maximal capacity for each subtree.
     */
    private double capacity;

    /**
     * the maximal length of the cycle in the neighborhood
     */
    private int lengthBound;

    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, int lengthBound) {
        this.graph = graph;
        this.root = root;
    }

    @Override
    public SpanningTree<E> getSpanningTree() {

        /*
         * labeling of the improvement graph vertices. There are two vertices in the improvement graph for every vertex
         * in the input graph: the vertex indicating the vertex itself and the vertex indicating the subtree.
         */
        Map<Pair<V, Integer>, Integer> labels = new HashMap<>();
        // the implicit partition defined by the subtrees
        Map<Integer, Set<V>> partition = new HashMap<>();

        // calculates initial solution on which we base the local search
        getInitialPartition(labels, partition);

        double currentCost;

        // do local improvement steps
        do {

            Graph<Pair<V, Integer>, DefaultWeightedEdge> improvementGraph = buildImprovementGraph(labels, partition);

            AhujaOrlinSharmaCyclicExchangeLocalAugmentation<Pair<V, Integer>, DefaultWeightedEdge> ahujaOrlinSharmaCyclicExchangeLocalAugmentation
                    = new AhujaOrlinSharmaCyclicExchangeLocalAugmentation<>(improvementGraph, lengthBound, labels);

            AhujaOrlinSharmaCyclicExchangeLocalAugmentation.LabeledPath<Pair<V, Integer>> cycle = ahujaOrlinSharmaCyclicExchangeLocalAugmentation.getLocalAugmentationCycle();
            currentCost = cycle.getCost();

            executeNeighborhoodOperation(labels, partition, cycle);

        } while(currentCost < 0);

        return calculateResultingSpanningTree(partition);
    }

    private void getInitialPartition(Map<Pair<V, Integer>, Integer> labels, Map<Integer, Set<V>> partition) {
        for(V v : graph.vertexSet()) {
            // TODO
        }
    }

    private Graph<Pair<V, Integer>, DefaultWeightedEdge> buildImprovementGraph(Map<Pair<V, Integer>, Integer> labels, Map<Integer, Set<V>> partition) {
        Graph<Pair<V, Integer>, DefaultWeightedEdge> improvementGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        for(V v : graph.vertexSet()) {
            improvementGraph.addVertex(new Pair<>(v, 0));
            improvementGraph.addVertex(new Pair<>(v, 1));
        }

        Map<Integer, Set<V>> modifiablePartition = new HashMap<>();
        for(Integer i : modifiablePartition.keySet()) {
            modifiablePartition.put(i, new HashSet<>(partition.get(i)));
        }

        for(V v1 : graph.vertexSet()) {

            Pair<V, Integer> improvementGraphVertexOfV = new Pair<>(v1, 0);
            Pair<V, Integer> improvementGraphVertexOfVTree = new Pair<>(v1, 1);

            for(Integer i : partition.keySet()) {

                if(!i.equals(labels.get(improvementGraphVertexOfV))) {

                    // TODO kanten, die nicht zu einer zulässigen lösung beitragen, müssen noch entfernt werden!!!

                    modifiablePartition.get(i).add(v1);

                    for(V v2 : partition.get(i)) {

                        /*
                         * edge for v1 vertex replacing v2 vertex
                         */
                        improvementGraph.addEdge(improvementGraphVertexOfV, new Pair<>(v2, 0));
                        modifiablePartition.get(i).remove(v2);
                        new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiablePartition.get(i))).getSpanningTree().getWeight();
                        modifiablePartition.get(i).add(v2);

                        /*
                         * edge for v1 vertex replacing v2 subtree
                         */
                        improvementGraph.addEdge(improvementGraphVertexOfVTree, new Pair<>(v2, 0));
                        modifiablePartition.get(i).removeAll(subtree(v2));
                        new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiablePartition.get(i))).getSpanningTree().getWeight();
                        modifiablePartition.get(i).addAll(subtree(v2));
                    }

                    modifiablePartition.get(i).remove(v1);

                    modifiablePartition.get(i).addAll(subtree(v1));

                    for(V v2 : partition.get(i)) {
                        /*
                         * edge for v1 subtree replacing v2 vertex
                         */
                        improvementGraph.addEdge(improvementGraphVertexOfV, new Pair<>(v2, 1));
                        modifiablePartition.get(i).remove(v2);
                        new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiablePartition.get(i))).getSpanningTree().getWeight();
                        modifiablePartition.get(i).add(v2);

                        /*
                         * edge for v1 subtree replacing v2 subtree
                         */
                        improvementGraph.addEdge(improvementGraphVertexOfVTree, new Pair<>(v2, 1));
                        modifiablePartition.get(i).removeAll(subtree(v2));
                        new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiablePartition.get(i))).getSpanningTree().getWeight();
                        modifiablePartition.get(i).addAll(subtree(v2));
                    }

                    modifiablePartition.get(i).removeAll(subtree(v1));

                }
            }
        }

        return improvementGraph;
    }

    private void executeNeighborhoodOperation(Map<Pair<V, Integer>, Integer> labels, Map<Integer, Set<V>> partition, AhujaOrlinSharmaCyclicExchangeLocalAugmentation.LabeledPath<Pair<V, Integer>> cycle) {
        Iterator<Pair<V, Integer>> it = cycle.getVertices().iterator();
        if(it.hasNext()) {
            Pair<V, Integer> cur = it.next();
            if(it.hasNext()) {
                while (it.hasNext()) {
                    Pair<V, Integer> next = it.next();

                    if (cur.getSecond().equals(0)) {
                        labels.put(cur, labels.get(next));
                        partition.get(labels.get(cur)).remove(cur.getFirst());
                        partition.get(labels.get(next)).add(cur.getFirst());
                    } else {
                        // TODO
                    }

                    cur = next;
                }
            }
        }
    }

    private Set<V> subtree(V v) {
        // TODO
        return new HashSet<>();
    }

    private SpanningTree<E> calculateResultingSpanningTree(Map<Integer, Set<V>> partition) {
        Set<E> spanningTreeEdges = new HashSet<>();
        double weight = 0;

        for(Set<V> set : partition.values()) {
            SpanningTree<E> subtree = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, set)).getSpanningTree();
            E minimalEdgeToRoot = null;
            for(E e : graph.outgoingEdgesOf(root)) {
                V opposite = Graphs.getOppositeVertex(graph, e, root);
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
