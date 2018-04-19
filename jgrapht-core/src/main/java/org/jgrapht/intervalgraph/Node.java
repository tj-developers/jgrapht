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

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    public void setRed(boolean red) {
        this.red = red;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isRed() {
        return red;
    }

    public K getKey() {
        return key;
    }

    public V getVal() {
        return val;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public int getSize() {
        return size;
    }
}
