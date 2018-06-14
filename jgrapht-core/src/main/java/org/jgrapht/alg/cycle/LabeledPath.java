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

import org.jgrapht.util.TypeUtil;

import java.util.*;

/**
 * Implementation of a labeled path.
 * It is used in AhujaOrlinSharmaCyclicExchangeLocalAugmentation to efficiently maitain the paths in the calculation.
 *
 * @param <V> the vertex type
 *
 * @author Christoph Grüne
 * @since June 7, 2018
 */
public class LabeledPath<V> implements Cloneable {

    private LinkedList<V> vertices;
    private Map<V, Integer> labels;

    private double cost;

    /**
     * constructs a LabeledPath with the given inputs
     *
     * @param vertices the vertices of the path in order of the path
     * @param cost the cost of the edges connecting the vertices
     * @param labels the mapping of the vertives to labels (subsets)
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
