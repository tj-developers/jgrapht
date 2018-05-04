package org.jgrapht.intervalgraph;

import java.io.Serializable;

/**
 * Implementation of red-black tree node
 *
 * @param <V> the value
 * @author Christoph Grüne (christophgruene)
 * @author Daniel Mock (danielmock)
 * @since Apr 26, 2018
 */
public class RBNode<V> implements Serializable {

    private static final long serialVersionUID = 5674337686253743843L;

    /**
     * the value of the node
     */
    private V val;
    /**
     * node's corresponding left and right children
     */
    private RBNode<V> leftChild, rightChild;
    /**
     * the color of the node (important for usage in red black tree)(not necessary for all implementations)
     */
    private boolean red;
    /**
     * the size of the sub tree rooted at this node (not necessary for all implementations)
     */
    private int size;

    /**
     * constructs a node object
     *
     * @param val the value of the node
     * @param red the color of the node
     * @param size the size of the sub tree rooted at the node
     */
    public RBNode(V val, boolean red, int size) {
        this.val = val;
        this.red = red;
        this.size = size;
    }

    /**
     * sets a new left child of the node
     *
     * @param leftChild the node that should be new left child
     */
    protected void setLeftChild(RBNode<V> leftChild) {
        this.leftChild = leftChild;
    }

    /**
     * sets a new right child of the node
     *
     * @param rightChild the node that should be new right child
     */
    protected void setRightChild(RBNode<V> rightChild) {
        this.rightChild = rightChild;
    }

    /**
     * sets the color for the node
     *
     * @param red red: true, black: false
     */
    protected void setRed(boolean red) {
        this.red = red;
    }

    /**
     * sets the color of the node to red
     */
    protected void setRed() {
        this.red = true;
    }

    /**
     * sets the color of the node to black
     */
    protected void setBlack() {
        this.red = false;
    }

    /**
     * sets a new size for the sub tree rooted at this node
     *
     * @param size the size of the sub tree rooted at this node
     */
    protected void setSize(int size) {
        this.size = size;
    }

    /**
     * returns whether node is red or black
     *
     * @return red:true, black:false
     */
    protected boolean isRed() {
        return red;
    }

    /**
     * Getter for <code>val</code>
     *
     * @return the value of the node
     */
    protected V getVal() {
        return val;
    }

    /**
     * Setter for <code>val</code>
     *
     * @param val
     */
    public void setVal(V val) {
        this.val = val;
    }

    /**
     * Getter for <code>leftChild</code>
     *
     * @return the left child of the node
     */
    protected RBNode<V> getLeftChild() {
        return leftChild;
    }

    /**
     * Getter for <code>rightChild</code>
     *
     * @return the right child of the node
     */
    protected RBNode<V> getRightChild() {
        return rightChild;
    }

    /**
     * Getter for <code>size</code>
     *
     * @return the size of the sub tree rooted at the node, if maintained
     */
    protected int getSize() {
        return size;
    }
}
