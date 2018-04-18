package org.jgrapht.intervalgraph;

// BST helper node data type
private class Node {
    private Key key;           // key
    private Value val;         // associated data
    private Node left, right;  // links to left and right subtrees
    private boolean color;     // color of parent link
    private int size;          // subtree count

    public Node(Key key, Value val, boolean color, int size) {
        this.key = key;
        this.val = val;
        this.color = color;
        this.size = size;
    }
}
