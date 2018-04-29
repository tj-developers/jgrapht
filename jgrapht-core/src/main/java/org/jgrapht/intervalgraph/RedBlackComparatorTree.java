package org.jgrapht.intervalgraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of a Red-Black-Tree
 *
 * @param <V> the value
 *
 * @author Daniel Mock (danielmock)
 * @author Christoph Grüne (christophgruene)
 * @since Apr 18, 2018
 */
public class RedBlackComparatorTree<V> implements Serializable {

    private static final long serialVersionUID = 1199228564356373435L;

    protected RBNode<V> root;

    public final Comparator<V> comparator;
    public final Comparator<RBNode<V>> nodeComparator;

    public RedBlackComparatorTree(Comparator<V> comparator) {
        this.comparator = comparator;
        this.nodeComparator = (o1, o2) -> comparator.compare(o1.getVal(), o2.getVal());
    }

    public RBNode<V> getRoot() {
        return root;
    }

    private boolean isRed(RBNode<V> node){
        if (node == null) {
            return false;
        }
        return node.isRed();
    }

    /**
     * Returns whether a key is contained in the tree
     *
     * @return true if tree contains key, false otherwise
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    
    public boolean contains(V val) {
        if (val == null) {
            throw new IllegalArgumentException("Val is null");
        }

        return searchNode(val) != null;
    }

    /**
     * Insertes the given (key, value) pair into the tree. If the tree contains already a symbol with the given key
     * it overwrites the old value with the new.
     *
     * @param val the value
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    
    public void insert(V val) {
        if (val == null) {
            throw new IllegalArgumentException("Value is null");
        }

        root = insert(root, val);
        root.setBlack();
    }

    protected RBNode<V> insert(RBNode<V> current, V val) {
        if (current == null){
            return new RBNode<>(val, true, 1);
        }

        if (val.equals(current.getVal())) {
            current.setVal(val);
        } else if (comparator.compare(val, current.getVal()) < 0) {
            current.setLeftChild(insert(current.getLeftChild(), val));
        } else {
            current.setRightChild(insert(current.getRightChild(), val));
        }

        // Fixup

        if (isRed(current.getRightChild()) && !isRed(current.getLeftChild())) {
            current = rotateLeft(current);
        }
        if (isRed(current.getLeftChild()) && isRed(current.getLeftChild().getLeftChild())) {
            current = rotateRight(current);
        }
        if (isRed(current.getLeftChild()) && isRed(current.getRightChild())) {
            changeColor(current);
        }
        current.setSize(size(current.getLeftChild()) + size(current.getRightChild()) + 1);

        return current;
    }

    /**
     * Removes the specified key and its associated value from this tree
     *
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    
    public void delete(V val) {
        if (val == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (!contains(val)) {
            return;
        }

        if (!isRed(root.getLeftChild()) && !isRed(root.getRightChild())) {
            root.setRed();
        }

        root = delete(root, val);
        if (!isEmpty()) {
            root.setBlack();
        }
    }

    private boolean isEmpty() {
        return root == null;
    }

    protected RBNode<V> delete(RBNode<V> current, V val) {
        if (comparator.compare(val, current.getVal()) < 0) {
            if (!isRed(current.getLeftChild()) && !isRed(current.getLeftChild().getLeftChild())) {
                current = moveRedLeft(current);
            }
            current.setLeftChild(delete(current.getLeftChild(), val));
        } else {
            if (isRed(current.getLeftChild())) {
                current = rotateRight(current);
            }
            if (comparator.compare(val, current.getVal()) == 0 && current.getRightChild() == null) {
                return null;
            }
            if (!isRed(current.getRightChild()) && !isRed(current.getRightChild().getLeftChild())) {
                current = moveRedRight(current);
            }
            if (comparator.compare(val, current.getVal()) == 0) {
                RBNode<V> node = min(current.getRightChild());
                current.setVal(node.getVal());
                current.setRightChild(deleteMin(current.getRightChild()));
            }
            else current.setRightChild(delete(current.getRightChild(), val));
        }

        return balance(current);
    }

    protected RBNode<V> balance(RBNode<V> node) {
        if (isRed(node.getRightChild())) {
            node = rotateLeft(node);
        }
        if (isRed(node.getLeftChild()) && isRed(node.getLeftChild().getLeftChild())) {
            node = rotateRight(node);
        }
        if (isRed(node.getLeftChild()) && isRed(node.getRightChild())) {
            changeColor(node);
        }

        node.setSize(size(node.getLeftChild()) + size(node.getRightChild() ) + 1);
        return node;
    }

    /**
     * Removes the smallest key and associated value from the tree.
     *
     * @throws NoSuchElementException if the tree is empty
     */
    
    public void deleteMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("empty tree");
        }

        if (!isRed(root.getLeftChild()) && !isRed(root.getRightChild())) {
            root.setRed();
        }

        root = deleteMin(root);
        if (!isEmpty()) {
            root.setBlack();
        }
    }

    private RBNode<V> deleteMin(RBNode<V> node) {
        if (node.getLeftChild() == null) {
            return null;
        }

        if (!isRed(node.getLeftChild()) && !isRed(node.getLeftChild().getLeftChild())) {
            root = moveRedLeft(node);
        }

        node.setLeftChild(deleteMin(node.getLeftChild()));
        return balance(node);
    }

    /**
     * Removes the largest key and associated value from the tree.
     *
     * @throws NoSuchElementException if the tree is empty
     */
    
    public void deleteMax() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        if (!isRed(root.getRightChild()) && !isRed(root.getRightChild())) {
            root.setRed();
        }

        root = deleteMax(root);
        if (!isEmpty()) {
            root.setBlack();
        }
    }

    private RBNode<V> deleteMax(RBNode<V> node) {
        if (isRed(node.getLeftChild())) {
            node = rotateRight(node);
        }
        if (node.getRightChild() == null) {
            return null;
        }
        if (!isRed(node.getRightChild()) && !isRed(node.getRightChild().getLeftChild())) {
            node = moveRedRight(node);
        }

        node.setRightChild(deleteMax(node.getRightChild()));

        return balance(node);
    }

    /**
     * Returns the height of the BST.
     *
     * @return the height of the BST (a tree with 1 node has height 0)
     */
    
    public int height() {
        return height(root);
    }

    private int height(RBNode<V> node) {
        if (node == null) {
            return -1;
        }

        return 1 + Math.max(height(node.getLeftChild()), height(node.getRightChild()));
    }

    /**
     * Returns the smallest key in the tree.
     *
     * @return the smallest key in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    
    public V min() {
        if (isEmpty()) {
            throw new NoSuchElementException("empty tree");
        }

        return min(root).getVal();
    }

    private RBNode<V> min(RBNode<V> node) {
        if (node.getLeftChild() == null) {
            return node;
        }
        return min(node.getLeftChild());
    }

    /**
     * Returns the largest key in the tree.
     *
     * @return the largest key in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    
    public V max() {
        if (isEmpty()) {
            throw new NoSuchElementException("empty tree");
        }

        return max(root.getRightChild()).getVal();
    }

    private RBNode<V> max(RBNode<V> node) {
        if (node.getRightChild() == null) {
            return node;
        }

        return max(node.getRightChild());
    }

    /**
     * Returns the largest key in the tree less than or equal to <code>key</code>.
     *
     * @return the largest key in the tree less than or equal to <code>key</code>
     * @throws NoSuchElementException   if there is no such key
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    
    public V floor(V val) {
        return null;
    }

    /**
     * Returns the smallest key in the tree greater than or equal to <code>key</code>.
     *
     * @return the smallest key in the tree greater than or equal to <code>key</code>
     * @throws NoSuchElementException   if there is no such key
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    
    public V ceiling(V val) {
        return null;
    }

    /**
     * Return the key in the tree whose rank is <code>k</code>.
     * This is the (k+1)st smallest key in the tree.
     *
     * @param k the position
     * @return the key in the tree of rank <code>k</code>
     * @throws IllegalArgumentException if <code>k</code> not in {0, ..., n-1}
     */
    
    public V select(int k) {
        return null;
    }

    /**
     * Return the number of keys in the tree strictly less than <code>key</code>.
     *
     * @param val the value
     * @return the number of keys in the tree strictly less than <code>key</code>
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    
    public int rank(V val) {
        return 0;
    }

    /**
     * Returns all keys in the symbol table as an <code>Iterable</code>.
     * To iterate over all of the keys in the symbol table named <code>st</code>,
     * use the foreach notation: <code>for (Key key : st.keys())</code>.
     *
     * @return all keys in the symbol table as an <code>Iterable</code>
     */
    
    public Iterable<V> keys() {
        return null;
    }

    /**
     * Returns all keys in the symbol table in the given range,
     * as an <code>Iterable</code>.
     *
     * @param min minimum endpoint
     * @param max maximum endpoint
     * @return all keys in the sybol table between <code>min</code>
     * (inclusive) and <code>max</code> (inclusive) as an <code>Iterable</code>
     * @throws IllegalArgumentException if either <code>min</code> or <code>max</code>
     *                                  is <code>null</code>
     */
    
    public Iterable<V> keys(V min, V max) {
        return null;
    }

    /**
     * Returns the number of keys in the symbol table in the given range.
     *
     * @param min minimum endpoint
     * @param max maximum endpoint
     * @return the number of keys in the sybol table between <code>min</code>
     * (inclusive) and <code>max</code> (inclusive)
     * @throws IllegalArgumentException if either <code>min</code> or <code>max</code>
     *                                  is <code>null</code>
     */
    
    public int size(V min, V max) {
        return 0;
    }

    public int size(RBNode<V> node) {
        if (node == null) {
            return 0;
        }

        return getSize(node);
    }

    /*******************************************************************************************************************
     * HELPER METHODS                                                                                                  *
     ******************************************************************************************************************/

    protected RBNode<V> rotateLeft(RBNode<V> node) {
        RBNode<V> rightChild = node.getRightChild();
        node.setRightChild(rightChild.getLeftChild());
        rightChild.setLeftChild(node);
        rightChild.setRed(isRed(rightChild.getLeftChild()));
        rightChild.getLeftChild().setRed(true);
        rightChild.setSize(getSize(node));
        node.setSize(getSize(node.getLeftChild()) + getSize(node.getRightChild()) + 1);
        return rightChild;
    }

    private int getSize(RBNode<V> node) {
        return node != null ? node.getSize() : 0;
    }

    protected RBNode<V> rotateRight(RBNode<V> node) {
        RBNode<V> leftChild = node.getLeftChild();
        node.setLeftChild(leftChild.getRightChild());
        leftChild.setRightChild(node);
        leftChild.setRed(isRed(leftChild.getRightChild()));
        leftChild.getRightChild().setRed(true);
        leftChild.setSize(getSize(node));
        node.setSize(getSize(node.getLeftChild()) + getSize(node.getRightChild()) + 1);
        return leftChild;
    }

    private void changeColor(RBNode<V> node) {
        node.setRed(!isRed(node));
        node.getRightChild().setRed(!isRed(node.getRightChild()));
        node.getLeftChild().setRed(!isRed(node.getLeftChild()));
    }

    private RBNode<V> searchNode(V val) {
        RBNode<V> current = root;
        while (current != null) {
            if (current.getVal().equals(val)) {
                return current;
            } else if (comparator.compare(current.getVal(), val) < 0) {
                current = current.getRightChild();
            } else {
                current = current.getLeftChild();
            }
        }

        return null;
    }

    private RBNode<V> moveRedRight(RBNode<V> node) {
        changeColor(node);
        if (isRed(node.getLeftChild().getLeftChild())) {
            node = rotateRight(node);
            changeColor(node);
        }

        return node;
    }

    private RBNode<V> moveRedLeft(RBNode<V> node) {
        changeColor(node);
        if (isRed(node.getRightChild().getLeftChild())) {
            node.setRightChild(rotateRight(node.getRightChild()));
            node = rotateLeft(node);
            changeColor(node);
        }

        return node;
    }

    // returns the nodes inorder
    private List<RBNode<V>> inorder() {
        if (root == null) {
            return new ArrayList<>();
        }

        List<RBNode<V>> result = new ArrayList<>(getSize(root));
        inorder(root, result);
        return result;
    }

    private void inorder(RBNode<V> current, List<RBNode<V>> result) {
        if (current == null) {
            return;
        }

        inorder(current.getLeftChild(), result);
        result.add(current);
        inorder(current.getRightChild(), result);
    }

    public List<V> inorderValues(){
        List<RBNode<V>> inorder = inorder();
        List<V> result = new ArrayList<>(inorder.size());
        for (RBNode<V> node: inorder) {
            result.add(node.getVal());
        }
        return result;
    }

    private List<RBNode<V>> preorder() {
        if (root == null) {
            return new ArrayList<>();
        }

        List<RBNode<V>> result = new ArrayList<>(getSize(root));
        preorder(root, result);
        return result;
    }

    private void preorder(RBNode<V> current, List<RBNode<V>> result) {
        if (current == null) {
            return;
        }

        result.add(current);
        inorder(current.getLeftChild(), result);
        inorder(current.getRightChild(), result);
    }

    // returns the minimum node in the subtree of input node
    private RBNode<V> getMin(RBNode<V> node) {
        while (node.getLeftChild() != null) {
            node = node.getLeftChild();
        }
        return node;
    }
}
