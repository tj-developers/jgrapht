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

    public boolean isRed() {
        return red;
    }
}
