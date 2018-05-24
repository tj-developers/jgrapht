/*
 * (C) Copyright 2003-2018, by Christoph Grüne and Contributors.
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

import java.util.NoSuchElementException;

/**
 * Interface for Binary Search Trees
 *
 * @param <K> the key
 * @param <V> the value
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 18, 2018
 */
public interface BinarySearchTree<K, V> {

    /*
     * Search Operations
     */

    /**
     * Returns the value associated with the given key
     *
     * @param key the key
     * @return the value associated with the given key. If the key is not in the tree, null is returned.
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    V get(K key);

    /**
     * Returns whether a key is contained in the tree
     *
     * @param key the key
     * @return true if tree contains key, false otherwise
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    boolean contains(K key);


    /*
     * Insertion Operations
     */

    /**
     * Inserts the given (key, value) pair into the tree. If the tree contains already a symbol with the given key
     * it overwrites the old value with the new.
     *
     * @param key the key
     * @param val the value
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    void insert(K key, V val);


    /*
     * Deletion Operations
     */

    /**
     * Removes the specified key and its associated value from this tree
     *
     * @param  key the key
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    void delete(K key);

    /**
     * Removes the smallest key and associated value from the tree.
     * @throws NoSuchElementException if the tree is empty
     */
    void deleteMin();

    /**
     * Removes the largest key and associated value from the tree.
     * @throws NoSuchElementException if the tree is empty
     */
    void deleteMax();


    /*
     * Utility Operations
     */

    /**
     * Returns the height of the BST.
     * @return the height of the BST (a tree with 1 node has height 0)
     */
    int height();


    /*
     * Special Search Operations
     */

    /**
     * Returns the smallest key in the tree.
     * @return the smallest key in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    K min();
    /**
     * Returns the largest key in the tree.
     * @return the largest key in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    K max();

    /**
     * Returns the largest key in the tree less than or equal to <code>key</code>.
     *
     * @param key the key
     * @return the largest key in the tree less than or equal to <code>key</code>
     * @throws NoSuchElementException if there is no such key
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    K floor(K key);

    /**
     * Returns the smallest key in the tree greater than or equal to <code>key</code>.
     *
     * @param key the key
     * @return the smallest key in the tree greater than or equal to <code>key</code>
     * @throws NoSuchElementException if there is no such key
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    K ceiling(K key);

    /**
     * Return the key in the tree whose rank is <code>k</code>.
     * This is the (k+1)st smallest key in the tree.
     *
     * @param  k the position
     * @return the key in the tree of rank <code>k</code>
     * @throws IllegalArgumentException if <code>k</code> not in {0, ..., n-1}
     */
    K select(int k);

    /**
     * Return the number of keys in the tree strictly less than <code>key</code>.
     *
     * @param key the key
     * @return the number of keys in the tree strictly less than <code>key</code>
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>
     */
    int rank(K key);


    /*
     * Range Search Operations
     */

    /**
     * Returns all keys in the symbol table as an <code>Iterable</code>.
     * To iterate over all of the keys in the symbol table named <code>st</code>,
     * use the foreach notation: <code>for (IntervalTreeNodeKey key : st.keys())</code>.
     *
     * @return all keys in the symbol table as an <code>Iterable</code>
     */
    Iterable<K> keys();

    /**
     * Returns all keys in the symbol table in the given range,
     * as an <code>Iterable</code>.
     *
     * @param  min minimum endpoint
     * @param  max maximum endpoint
     * @return all keys in the sybol table between <code>min</code>
     *    (inclusive) and <code>max</code> (inclusive) as an <code>Iterable</code>
     * @throws IllegalArgumentException if either <code>min</code> or <code>max</code>
     *    is <code>null</code>
     */
    Iterable<K> keys(K min, K max);

    /**
     * Returns the number of keys in the symbol table in the given range.
     *
     * @param  min minimum endpoint
     * @param  max maximum endpoint
     * @return the number of keys in the sybol table between <code>min</code>
     *    (inclusive) and <code>max</code> (inclusive)
     * @throws IllegalArgumentException if either <code>min</code> or <code>max</code>
     *    is <code>null</code>
     */
    int size(K min, K max);
}
