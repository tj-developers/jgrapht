package org.jgrapht.alg.interval.mpq;

import java.util.HashSet;

/**
 * A leaf node of a modified PQ-tree
 *
 * @param <V> the element type of the leaf node
 * @author Jiong Fu (magnificent_tony)
 * @author Ira Justus Fesefeldt (PhoenixIra)
 */
public final class Leaf<V> extends MPQTreeNode<V> {

    /**
     * Initiating a leaf node associating with a graph vertex
     *
     * @param vertex the graph vertex to be associated with
     */
    public Leaf(V vertex) {
        this.setB.add(vertex);
    }

    /**
     * Initiating a leaf node associating with a graph vertex set
     *
     * @param vertexSet the graph vertex set to be associated with
     */
    public Leaf(HashSet<V> vertexSet) {
        super(vertexSet);
    }

}
