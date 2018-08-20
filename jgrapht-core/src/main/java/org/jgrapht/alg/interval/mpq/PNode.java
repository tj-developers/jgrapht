package org.jgrapht.alg.interval.mpq;

import org.jgrapht.alg.interval.CircularListNode;

import java.util.HashSet;

/**
 * A P-node of a modified PQ-tree
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

    @Override
    public boolean hasAtMostOneSon() {
        return currentChild == null || currentChild == currentChild.next();
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
     * Remove the current child from the current P-node
     */
    public CircularListNode<MPQTreeNode<V>> removeCurrentChild() {
        CircularListNode<MPQTreeNode<V>> result = null;
        if (currentChild != null) {
            result = currentChild;

            if (currentChild.next() != null) {
                // if there are more elements in the child list, set the current child pointer to the next child
                currentChild = currentChild.next();
            } else {
                // otherwise, set the current child pointer to null
                currentChild = null;
            }
        }

        return result.remove();
    }

    public MPQTreeNode<V> getCurrentChild() {
        return currentChild.element();
    }

    public void setCurrentChild(CircularListNode<MPQTreeNode<V>> currentChild) {
        this.currentChild = currentChild;
    }

}
