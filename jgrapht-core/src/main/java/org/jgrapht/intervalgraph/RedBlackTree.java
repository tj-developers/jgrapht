package org.jgrapht.intervalgraph;

import java.util.NoSuchElementException;

/**
 * Implementation of a Red-Black-Tree
 *
 * @param <K> the key
 * @param <V> the value
 *
 * @author Daniel Mock (danielmock)
 * @author Christoph Grüne (christophgruene)
 * @since Apr 18, 2018
 */
public class RedBlackTree<K, V> implements BinarySearchTree<K, V> {

    private Node root;

    /**
     * Returns the value associated with the given key
     *
     * @param key the key
     * @return the value associated with the given key. If the key is not in the tree, null is returned.
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    @Override
    public V get(K key) {
        return null;
    }

    /**
     * Returns whether a key is contained in the tree
     *
     * @param key the key
     * @return true if tree contains key, false otherwise
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    @Override
    public boolean contains(K key) {
        return false;
    }

    /**
     * Insertes the given (key, value) pair into the tree. If the tree contains already a symbol with the given key
     * it overwrites the old value with the new.
     *
     * @param key the key
     * @param val the value
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    @Override
    public void insert(K key, V val) {

    }

    /**
     * Removes the specified key and its associated value from this tree
     *
     * @param key the key
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    @Override
    public void delete(K key) {

    }

    /**
     * Removes the smallest key and associated value from the tree.
     *
     * @throws NoSuchElementException if the tree is empty
     */
    @Override
    public void deleteMin() {

    }

    /**
     * Removes the largest key and associated value from the tree.
     *
     * @throws NoSuchElementException if the tree is empty
     */
    @Override
    public void deleteMax() {

    }

    /**
     * Returns the height of the BST.
     *
     * @return the height of the BST (a tree with 1 node has height 0)
     */
    @Override
    public int height() {
        return 0;
    }

    /**
     * Returns the smallest key in the tree.
     *
     * @return the smallest key in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    @Override
    public K min() {
        return null;
    }

    /**
     * Returns the largest key in the tree.
     *
     * @return the largest key in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    @Override
    public K max() {
        return null;
    }

    /**
     * Returns the largest key in the tree less than or equal to <code>key</code>.
     *
     * @param key the key
     * @return the largest key in the tree less than or equal to <code>key</code>
     * @throws NoSuchElementException   if there is no such key
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    @Override
    public K floor(K key) {
        return null;
    }

    /**
     * Returns the smallest key in the tree greater than or equal to <code>key</code>.
     *
     * @param key the key
     * @return the smallest key in the tree greater than or equal to <code>key</code>
     * @throws NoSuchElementException   if there is no such key
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    @Override
    public K ceiling(K key) {
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
    @Override
    public K select(int k) {
        return null;
    }

    /**
     * Return the number of keys in the tree strictly less than <code>key</code>.
     *
     * @param key the key
     * @return the number of keys in the tree strictly less than <code>key</code>
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    @Override
    public int rank(K key) {
        return 0;
    }

    /**
     * Returns all keys in the symbol table as an <code>Iterable</code>.
     * To iterate over all of the keys in the symbol table named <code>st</code>,
     * use the foreach notation: <code>for (Key key : st.keys())</code>.
     *
     * @return all keys in the symbol table as an <code>Iterable</code>
     */
    @Override
    public Iterable<K> keys() {
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
    @Override
    public Iterable<K> keys(K min, K max) {
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
    @Override
    public int size(K min, K max) {
        return 0;
    }

    /*******************************************************************************************************************
     * HELPER METHODS                                                                                                  *
     ******************************************************************************************************************/

    private Node rotateLeft(Node node) {
        //TODO implementation: Christoph
        return null;
    }

    private Node rotateRight(Node node) {
        //TODO implementation: Christoph
        return null;
    }

    private void changeColor(Node node) {
        //TODO implementation: Christoph
    }
}
