package org.jgrapht.intervalgraph;

import java.io.Serializable;

// BST helper node data type
public class Node<K, V> implements Serializable {

    private static final long serialVersionUID = 5674337686253743843L;

    private K key;
    private V val;
    private Node leftChild, rightChild;
    private boolean red;
    private int size;

    public Node(K key, V val, boolean red, int size) {
        this.key = key;
        this.val = val;
        this.red = red;
        this.size = size;
    }

    protected void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    protected void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    protected void setRed(boolean red) {
        this.red = red;
    }

    protected void setRed() {
        this.red = true;
    }

    protected void setBlack() {
        this.red = false;
    }

    protected void setSize(int size) {
        this.size = size;
    }

    protected boolean isRed() {
        return red;
    }

    protected K getKey() {
        return key;
    }

    protected V getVal() {
        return val;
    }

    public void setVal(V val) {
        this.val = val;
    }

    protected Node getLeftChild() {
        return leftChild;
    }

    protected Node getRightChild() {
        return rightChild;
    }

    protected int getSize() {
        return size;
    }

    public void setKey(K key) {
        this.key = key;
    }
}
