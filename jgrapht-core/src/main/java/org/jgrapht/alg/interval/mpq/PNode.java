package org.jgrapht.alg.interval.mpq;

import org.jgrapht.alg.interval.CircularListNode;

import java.util.HashSet;

/**
 * A P-node of a modified PQ-tree
 *
 * @param <V> the element type of the P-node
 * @author Jiong Fu (magnificent_tony)
 * @author Ira Justus Fesefeldt (PhoenixIra)
 */
public final class PNode<V> extends MPQTreeNode<V> {

    /**
     * The children of a P-node are stored with a doubly linked circular list
     * <p>
     * P-node has a pointer of the current child as the entrance to this list
     */
    private CircularListNode<MPQTreeNode<V>> currentChild = null;

    /**
     * Instantiate a P node associating with a graph vertex set
     *
     * @param vertexSet the current node in the associated graph vertex set
     */
    public PNode(HashSet<V> vertexSet) {
        super(vertexSet);
    }

    /**
     * Add a child to the current P-node
     *
     * @param child the child node to be added
     */
    public void addChild(MPQTreeNode<V> child) {
        if (currentChild == null) {
            currentChild = new CircularListNode<>(child);
        } else {
            currentChild.addPrev(child);
            currentChild = currentChild.prev();
        }
    }

    /**
     * Get the containing element of the current child
     *
     * @return the containing element of the current child
     */
    public MPQTreeNode<V> getCurrentElement() {
        return currentChild.element();
    }

}
