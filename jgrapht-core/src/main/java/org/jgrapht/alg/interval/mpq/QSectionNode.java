package org.jgrapht.alg.interval.mpq;

import java.util.HashSet;

/**
 * A section node of a Q-node
 *
 * @param <V> the element type of the section node
 * @author Jiong Fu (magnificent_tony)
 * @author Ira Justus Fesefeldt (PhoenixIra)
 */
public final class QSectionNode<V> extends MPQTreeNode<V> {

    /**
     * The child of the current Q section node
     * <p>
     * Each section has a pointer to its son
     */
    private MPQTreeNode<V> child = null;

    /**
     * The sections have a pointer to their neighbor sections
     * <p>
     * For the left most section, the left sibling is null
     * For the right most section, the right sibling is null
     */
    private QSectionNode<V> leftSibling = null;
    private QSectionNode<V> rightSibling = null;

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
        this.setB.add(vertex);
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

    /**
     * Get the child of this Q-section node
     *
     * @return the child of this Q-section node
     */
    public MPQTreeNode<V> getChild() {
        return child;
    }

    /**
     * Set the child for this Q-section node
     *
     * @param child the child to be set for this Q-section node
     */
    public void setChild(MPQTreeNode<V> child) {
        this.child = child;
    }

    /**
     * Get the left sibling of this Q-section node
     *
     * @return the left sibling of this Q-section node
     */
    public QSectionNode<V> getLeftSibling() {
        return leftSibling;
    }

    /**
     * Set the left sibling for this Q-section node
     *
     * @param leftSibling the left sibling to be set for this Q-section node
     */
    public void setLeftSibling(QSectionNode<V> leftSibling) {
        this.leftSibling = leftSibling;
    }

    /**
     * Get the right sibling of this Q-section node
     *
     * @return the right sibling of this Q-section node
     */
    public QSectionNode<V> getRightSibling() {
        return rightSibling;
    }

    /**
     * Set the right sibling for this Q-section node
     *
     * @param rightSibling the right sibling to be set for this Q-section node
     */
    public void setRightSibling(QSectionNode<V> rightSibling) {
        this.rightSibling = rightSibling;
    }

}
