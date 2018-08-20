package org.jgrapht.alg.interval.mpq;

import java.util.HashSet;

/**
 * A section node of a Q-node
 */
public final class QSectionNode<V> extends MPQTreeNode<V> {

    /**
     * The child of the current Q section node
     * <p>
     * Each section has a pointer to its son
     */
    private MPQTreeNode child = null;

    /**
     * The sections have a pointer to their neighbor sections
     * <p>
     * For the left most section, the left sibling is null
     * For the right most section, the right sibling is null
     */
    private QSectionNode leftSibling = null;
    private QSectionNode rightSibling = null;

    /**
     * Initiating a section node of a Q-node associating with a graph vertex set
     *
     * @param vertexSet the current node in the associated graph vertex set
     */
    public QSectionNode(HashSet<V> vertexSet) {
        super(vertexSet);
    }

    /**
     * Initiating a section node of a Q-node associating with a graph vertex
     *
     * @param vertex the graph vertex to be associated with
     */
    public QSectionNode(V vertex) {
        HashSet<V> vertexSet = new HashSet<>();
        vertexSet.add(vertex);
        this.setB = vertexSet;
    }

    @Override
    public boolean hasAtMostOneSon() {
        return true;
    }

    /**
     * Test if the current Q section node is the leftmost section in the associated Q node by checking if the left sibling is null
     *
     * @return true if the current Q section node is the leftmost section, false otherwise
     */
    public boolean isLeftmostSection() {
        return this.leftSibling == null;
    }

    /**
     * Test if the current Q section node is the rightmost section in the associated Q node by checking if the right sibling is null
     *
     * @return true if the current Q section node is the rightmost section, false otherwise
     */
    public boolean isRightmostSection() {
        return this.rightSibling == null;
    }

    public MPQTreeNode<V> getChild() {
        return child;
    }

    public void setChild(MPQTreeNode<V> child) {
        this.child = child;
    }

    public QSectionNode<V> getLeftSibling() {
        return leftSibling;
    }

    public void setLeftSibling(QSectionNode<V> leftSibling) {
        this.leftSibling = leftSibling;
    }

    public QSectionNode<V> getRightSibling() {
        return rightSibling;
    }

    public void setRightSibling(QSectionNode<V> rightSibling) {
        this.rightSibling = rightSibling;
    }

}
