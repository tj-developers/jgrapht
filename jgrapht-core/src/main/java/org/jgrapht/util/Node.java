/*
 * (C) Copyright 2003-2018, by Christoph Grüne, Daniel Mock and Contributors.
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
package org.jgrapht.util;

import java.io.Serializable;

/**
 * Implementation of Node
 * This class implements the node for a BST.
 *
 * @param <K> the key
 * @param <V> the value
 *
 * @author Christoph Grüne (christophgruene)
 * @author Daniel Mock (danielmock)
 * @since Apr 26, 2018
 */
public class Node<K, V> implements Serializable {

    private static final long serialVersionUID = 5674337686253743843L;

    /**
     * the key of the node
     */
    private K key;
    /**
     * the value of the node
     */
    private V val;
    /**
     * node's corresponding left and right children
     */
    private Node<K, V> leftChild, rightChild;
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
     * @param key the key of the node
     * @param val the value of the node
     * @param red the color of the node
     * @param size the size of the sub tree rooted at the node
     */
    public Node(K key, V val, boolean red, int size) {
        this.key = key;
        this.val = val;
        this.red = red;
        this.size = size;
    }

    /**
     * sets a new left child of the node
     *
     * @param leftChild the node that should be new left child
     */
    protected void setLeftChild(Node<K, V> leftChild) {
        this.leftChild = leftChild;
    }

    /**
     * sets a new right child of the node
     *
     * @param rightChild the node that should be new right child
     */
    protected void setRightChild(Node<K, V> rightChild) {
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
     * Getter for <code>key</code>
     *
     * @return the key of the node
     */
    protected K getKey() {
        return key;
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
     * @param val the new value of the node
     */
    public void setVal(V val) {
        this.val = val;
    }

    /**
     * Getter for <code>leftChild</code>
     *
     * @return the left child of the node
     */
    protected Node<K, V> getLeftChild() {
        return leftChild;
    }

    /**
     * Getter for <code>rightChild</code>
     *
     * @return the right child of the node
     */
    protected Node<K, V> getRightChild() {
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

    /**
     * sets a new key element for this node
     *
     * @param key the new key of the node
     */
    public void setKey(K key) {
        this.key = key;
    }
}
