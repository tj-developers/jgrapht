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
import org.jgrapht.util.TypeUtil;

import java.util.*;

/**
 * Implementation of an algorithm for the local augmentation problem for the cyclic exchange neighborhood,
 * i.e. it finds subset-disjoint negative cycles in a graph, based on
 * Ravindra K. Ahuja, James B. Orlin, Dushyant Sharma,
 * A composite very large-scale neighborhood structure for the capacitated minimum spanning tree problem,
 * Operations Research Letters, Volume 31, Issue 3, 2003, Pages 185-194, ISSN 0167-6377,
 * https://doi.org/10.1016/S0167-6377(02)00236-5. (http://www.sciencedirect.com/science/article/pii/S0167637702002365)
 *
 * This algorithm may enumerate all paths up to the length given by the parameter <code>lengthBound</code>,
 * i.e the algorithm runs in exponential time.
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 *
 * @author Christoph Grüne
 * @since June 7, 2018
 */
public class AhujaOrlinSharmaCyclicExchangeLocalAugmentation<V, E> {

    /**
     * Implementation of a labeled path.
     * It is used in AhujaOrlinSharmaCyclicExchangeLocalAugmentation to efficiently maintain the paths in the calculation.
     *
     * @param <V> the vertex type
     *
     * @author Christoph Grüne
     * @since June 7, 2018
     */
    public static class LabeledPath<V> implements Cloneable {

        private LinkedList<V> vertices;
        private Map<V, Integer> labels;

        private double cost;

        /**
         * constructs a LabeledPath with the given inputs
         *
         * @param vertices the vertices of the path in order of the path
         * @param cost the cost of the edges connecting the vertices
         * @param labels the mapping of the vertices to labels (subsets)
         */
        public LabeledPath(LinkedList<V> vertices, double cost, Map<V, Integer> labels) {
            this.vertices = vertices;
            this.cost = cost;
            this.labels = labels;
        }

        /**
         * constructs an empty path with cost Double.MAX_VALUE
         */
        public LabeledPath() {
            this(new LinkedList<>(), Double.MAX_VALUE, new LinkedHashMap<>());
        }

        /**
         * adds a vertex to the path
         *
         * @param v the vertex
         * @param edgeCost the cost of the edge connecting the last vertex of the path and the new vertex
         * @param label the label of the new vertex
         */
        public void addVertex(V v, double edgeCost, int label) {
            this.vertices.add(v);
            this.cost += edgeCost;
            this.labels.put(v, label);
        }

        /**
         * returns whether this instance dominates <code>path2</code>.
         * A labeled path path1 dominated another path path2 iff
         * cost of path1 are lower than cost of path2,
         * both paths have the same start and end vertex,
         * and the set of all labels of path1 are a subset of the labels of path 2.
         *
         * @param path2 the second labeled path
         * @return whether this instance dominates <code>path2</code>
         */
        public boolean dominates(LabeledPath<V> path2) {

            if(this.getCost() >= path2.getCost()) {
                return false;
            }
            if(!this.getTail().equals(path2.getTail())) {
                return false;
            }
            if(!this.getHead().equals(path2.getHead())) {
                return false;
            }
            if(!path2.getLabels().containsAll(this.getLabels())) {
                return false;
            }

            return true;
        }

        /**
         * return the start vertex of the path
         *
         * @return the start vertex of the path
         */
        public V getHead() {
            return vertices.getFirst();
        }

        /**
         * return the end vertex of the path
         *
         * @return the end vertex of the path
         */
        public V getTail() {
            return vertices.getLast();
        }

        /**
         * returns whether the path is empty, i.e. has no vertices
         *
         * @return whether the path is empty
         */
        public boolean isEmpty() {
            return vertices.isEmpty();
        }

        /**
         * returns an ordered list of the vertices of the path
         *
         * @return an ordered list of the vertices of the path
         */
        public LinkedList<V> getVertices() {
            return vertices;
        }

        /**
         * returns the labels of all vertices of the graph
         *
         * @return the labels of all vertices of the graph
         */
        public Collection<Integer> getLabels() {
            return labels.values();
        }

        /**
         * returns the cost of the path
         *
         * @return the cost of the path
         */
        public double getCost() {
            return cost;
        }

        /**
         * Returns a shallow copy of this labeled path instance. Vertices are not cloned.
         *
         * @return a shallow copy of this path.
         *
         * @throws RuntimeException in case the clone is not supported
         *
         * @see java.lang.Object#clone()
         */
        public LabeledPath<V> clone() {
            try {
                LabeledPath<V> newLabeledPath = TypeUtil.uncheckedCast(super.clone());
                newLabeledPath.vertices = new LinkedList<>();
                newLabeledPath.vertices.addAll(this.vertices);
                newLabeledPath.cost = this.cost;

                return newLabeledPath;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }

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
     * calculates a valid subset-disjoint negative cycle.
     * If there is no such a cycle, it returns an empty LabeledPath instance with cost Double.MAX_VALUE.
     *
     * @return a valid subset-disjoint negative cycle encoded as LabeledPath
     */
    public LabeledPath<V> getLocalAugmentationCycle() {

        int k = 1;
        LabeledPath<V> cycleResult = new LabeledPath<>();

        Set<LabeledPath<V>> pathsLengthK = new LinkedHashSet<>();
        Set<LabeledPath<V>> pathsLengthKplus1 = new LinkedHashSet<>();

        // initialize pathsLengthK for k = 1
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
                pathsLengthK.add(path);
            }
        }

        while(k < lengthBound && cycleResult.getCost() >= 0) {
            while(!pathsLengthK.isEmpty()) {

                // go through all valid paths of length k
                for(Iterator<LabeledPath<V>> it = pathsLengthK.iterator(); it.hasNext();) {
                    LabeledPath<V> path = it.next();
                    it.remove();

                    V head = path.getHead();
                    V tail = path.getTail();

                    if(graph.containsEdge(tail, head) && path.getCost() + graph.getEdgeWeight(graph.getEdge(tail, head)) < cycleResult.getCost()) { // the path builds a valid cycle
                        cycleResult = path.clone();
                        cycleResult.addVertex(head, graph.getEdgeWeight(graph.getEdge(tail, head)), labels.get(head));

                        /*
                         * only return the cycle if it is negative (in the first iteration it can be non-negative)
                         */
                        if(cycleResult.getCost() < 0) {
                            return cycleResult;
                        }
                    }

                    for(E e : graph.outgoingEdgesOf(tail)) {
                        V currentVertex = graph.getEdgeTarget(e);
                        if(!path.getLabels().contains(labels.get(currentVertex)) && path.getCost() + graph.getEdgeWeight(e) < 0) { // extend the path if the extension is still negative a correctly labeled
                            LabeledPath<V> newPath = path.clone();
                            newPath.addVertex(currentVertex, graph.getEdgeWeight(e), labels.get(currentVertex));
                            pathsLengthKplus1.add(newPath);

                            /*
                             * check if paths are dominated, i.e. if the path is definitely worse than other paths
                             * and does not have to be considered in the future
                             */
                            testDomination(path, pathsLengthKplus1);
                        }
                    }
                }
            }
            // update k and the corresponding sets
            k += 1;
            pathsLengthK = pathsLengthKplus1;
            pathsLengthKplus1 = new LinkedHashSet<>();
        }

        return new LabeledPath<>();
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
            LabeledPath<V> pathInSetKplus1 = it.next();

            if(path == pathInSetKplus1) {
                continue;
            }

            if(pathInSetKplus1.dominates(path)) {
                /*
                 * we have to delete path after the for loop otherwise we get an ConcurrentModificationException
                 */
                removePath = true;
                /*
                 * we can break because domination of paths is transitive, i.e. pathInSetKplus1 already removed the dominated paths
                 */
                break;
            }
            if(path.dominates(pathInSetKplus1)) {
                PathsLengthKplus1.remove(pathInSetKplus1);
            }
        }

        if(removePath) {
            PathsLengthKplus1.remove(path);
        }
    }
}
