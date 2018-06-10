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
package org.jgrapht.alg.cycle;

import org.jgrapht.Graph;

import java.util.*;

/**
 * Implementation of an algorithm for the local augmentation problem for the cyclic exchange neighborhood,
 * i.e. it finds subset-disjoint negative cycles, based on the
 * <a href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.15.6758&rep=rep1&type=pdf">paper</a>
 * by Ahuja et al.
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 *
 * @author Christoph Grüne
 * @since June 7, 2018
 */
public class AhujaOrlinSharmaCyclicExchangeLocalAugmentation<V, E> {

    private Graph<V, E> graph;
    private Map<V, Integer> labels;
    private int lengthBound;

    /**
     * Constructs an algorithm with given inputs
     *
     * @param graph the (improvement) graph on which to calculate the local augmentation
     * @param lengthBound the upper bound for the length of cycles to detect
     * @param labels the labels of the vertices encoding the subsets of vertices
     */
    public AhujaOrlinSharmaCyclicExchangeLocalAugmentation(Graph<V, E> graph, int lengthBound, Map<V, Integer> labels) {
        this.graph = graph;
        this.lengthBound = lengthBound;
        this.labels = labels;
    }

    /**
     * calculates a valid subset-disjoint negative cycle
     *
     * @return a valid subset-disjoint negative cycle encoded as LabeledPath
     */
    public LabeledPath<V> getLocalAugmentationCycle() {

        int k = 1;
        LabeledPath<V> C = new LabeledPath<>();

        Set<LabeledPath<V>> PathsLengthK = new LinkedHashSet<>();
        Set<LabeledPath<V>> PathsLengthKplus1 = new LinkedHashSet<>();

        // initialize PathsLengthK for k = 1
        for(E e : graph.edgeSet()) {
            if(graph.getEdgeWeight(e) < 0) {
                // initialize all paths of cost < 0
                LinkedList<V> pathVertices = new LinkedList<>();
                Map<V, Integer> pathLabels = new LinkedHashMap<>();
                V sourceVertex = graph.getEdgeSource(e);
                V targetVertex = graph.getEdgeTarget(e);
                pathVertices.add(sourceVertex);
                pathVertices.add(targetVertex);
                pathLabels.put(sourceVertex, labels.get(sourceVertex));
                pathLabels.put(targetVertex, labels.get(targetVertex));
                LabeledPath<V> path = new LabeledPath<>(pathVertices, graph.getEdgeWeight(e), pathLabels);

                // add path to set of paths of length 1
                PathsLengthK.add(path);
            }
        }

        while(k < lengthBound && C.getCost() >= 0) {
            while(!PathsLengthK.isEmpty()) {
                // go through all valid paths of length k
                for(Iterator<LabeledPath<V>> it = PathsLengthK.iterator(); it.hasNext();) {
                    LabeledPath<V> path = it.next();
                    it.remove();

                    V head = path.getHead();
                    V tail = path.getTail();

                    if(graph.containsEdge(tail, head) && path.getCost() + graph.getEdgeWeight(graph.getEdge(tail, head))
                            < C.getCost()) { // the path builds a valid cycle
                        C = path.clone();
                        C.addVertex(head, graph.getEdgeWeight(graph.getEdge(tail, head)), labels.get(head));
                        // only return the cycle if it is negative (in the first iteration it can be non-negative)
                        if(C.getCost() < 0) {
                            return C;
                        }
                    }

                    for(E e : graph.outgoingEdgesOf(tail)) {
                        V currentVertex = graph.getEdgeTarget(e);

                        // extend the path if the extension is still negative a correctly labeled
                        if(!path.getLabels().contains(labels.get(currentVertex))
                                && path.getCost() + graph.getEdgeWeight(e) < 0) {
                            LabeledPath<V> newPath = path.clone();
                            newPath.addVertex(currentVertex, graph.getEdgeWeight(e), labels.get(currentVertex));
                            PathsLengthKplus1.add(newPath);

                            // check if paths are dominated, i.e. if the path is definitely worse than other paths and
                            // does not have to be considered in the further calculation
                            testDomination(path,  PathsLengthKplus1);
                        }
                    }
                }
            }
            // update k and the corresponding sets
            k += 1;
            PathsLengthK = PathsLengthKplus1;
            PathsLengthKplus1 = new LinkedHashSet<>();
        }
        return null;
    }

    /**
     * removes all paths that are dominated from all calculated paths of length k + 1.
     * This is important out of efficiency reasons, otherwise many unnecessary paths may
     * be considered in further calculations.
     *
     *
     * @param path the currently calculated path
     * @param PathsLengthKplus1 all before calculated paths of length k + 1
     */
    private void testDomination(LabeledPath<V> path, Set<LabeledPath<V>> PathsLengthKplus1) {
        boolean removePath = false;

        for(Iterator<LabeledPath<V>> it = PathsLengthKplus1.iterator(); it.hasNext();) {
            LabeledPath<V> path1 = it.next();

            if(path == path1) {
                continue;
            }

            if(path1.dominates(path)) {
                // we have to delete path after the for loop otherwise we get an ConcurrentModificationException
                removePath = true;

                // we can break because domination of paths is transitive,
                // i.e. path1 already removed the dominated paths
                break;
            }
            if(path.dominates(path1)) {
                PathsLengthKplus1.remove(path1);
            }
        }

        if(removePath) {
            PathsLengthKplus1.remove(path);
        }
    }
}
