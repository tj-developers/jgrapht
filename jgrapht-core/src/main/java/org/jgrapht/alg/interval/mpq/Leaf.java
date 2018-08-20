package org.jgrapht.alg.interval.mpq;

import java.util.HashSet;

/**
 * A leaf node of a modified PQ-tree
 */
public final class Leaf<V> extends MPQTreeNode<V> {

    /**
     * Initiating a leaf node associating with a graph vertex
     *
     * @param vertex the graph vertex to be associated with
     */
    public Leaf(V vertex) {
        HashSet<V> vertexSet = new HashSet<>();
        vertexSet.add(vertex);
        this.setB = vertexSet;
    }

    /**
     * Initiating a leaf node associating with a graph vertex set
     *
     * @param vertexSet the graph vertex set to be associated with
     */
    public Leaf(HashSet<V> vertexSet) {
        super(vertexSet);
    }

    @Override
    public boolean hasAtMostOneSon() {
        return true;
    }

}
