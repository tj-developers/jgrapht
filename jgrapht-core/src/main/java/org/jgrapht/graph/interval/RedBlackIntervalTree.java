/*
 * (C) Copyright 2018-2018, by Christoph Grüne, Daniel Mock and Contributors.
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
package org.jgrapht.graph.interval;

import org.jgrapht.util.RedBlackTree;
import org.jgrapht.util.RedBlackTreeNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of RedBlackIntervalTree
 * This class implements the augmented interval tree. Essentially, this class updates the highValue
 * after any operation on this tree. The highValue equals to the highest endpoint in the subtree
 *
 * @param <T> the type of the interval
 * @param <K> the type of the key
 * @param <NodeValue> the type of the node value
 *
 * @author Daniel Mock (danielmock)
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
class RedBlackIntervalTree
        <K, NodeValue extends IntervalTreeNodeValue<Interval<T>, T>, T extends Comparable<T>>
        extends RedBlackTree<K, NodeValue>
        implements IntervalTree<K, NodeValue, T>, Serializable {

    private static final long serialVersionUID = 4353687394654923429L;

    /**
     * Constructs an empty interval tree.
     *
     * @param keyComparator the comparator for the keys
     */
    RedBlackIntervalTree(Comparator<K> keyComparator) {
        super(keyComparator);
    }

    /**
     * Constructs an interval tree with the given keys and values.
     * keys.get(i) is assigned to values.get(i)
     * Runs in linear time if list is sorted, otherwise naive insertion is performed
     *
     * @param keys the keys for the future nodes
     * @param values the values of the future nodes corresponding to the given keys (i-th key belongs to i-th value)
     * @param keyComparator the comparator for the keys
     */
    RedBlackIntervalTree(ArrayList<K> keys, ArrayList<NodeValue> values, Comparator<K> keyComparator) {
        super(keys, values, keyComparator);
    }

    /**
     * Returns all intervals of all vertices that intersect with the given <code>interval</code>
     *
     * @param interval the interval
     * @return all intersecting intervals of vertices in the graph
     */
    public List<Interval<T>> overlapsWith(Interval<T> interval) {
        List<Interval<T>> result = new LinkedList<>();
        overlapsWith(this.getRoot(), interval, result);
        return result;
    }

    /**
     * Returns all intervals of all vertices that include the given <code>point</code>
     *
     * @param point the point
     * @return all including intervals of vertices in the graph
     */
    public List<Interval<T>> overlapsWith(T point) {
        List<Interval<T>> result = new LinkedList<>();
        overlapsWithPoint(this.getRoot(), point, result);
        return result;
    }

    @Override
    protected RedBlackTreeNode<K, NodeValue> rotateRight(RedBlackTreeNode<K, NodeValue> node) {
        // Perform rotation as usual
        RedBlackTreeNode<K, NodeValue> result = super.rotateRight(node);

        // update hi vals
        result.getVal().setHighValue(node.getVal().getHighValue());
        updateHi(node);

        return result;
    }

    @Override
    protected RedBlackTreeNode<K, NodeValue> rotateLeft(RedBlackTreeNode<K, NodeValue> node) {
        // Perform rotation as usual
        RedBlackTreeNode<K, NodeValue> result = super.rotateLeft(node);

        // update hi vals
        result.getVal().setHighValue(node.getVal().getHighValue());
        updateHi(node);

        return result;
    }

    @Override
    protected RedBlackTreeNode<K, NodeValue> delete(RedBlackTreeNode<K, NodeValue> current, K key) {
        RedBlackTreeNode<K, NodeValue> result = super.delete(current, key);
        updateHi(result);
        return result;
    }

    @Override
    protected RedBlackTreeNode<K, NodeValue> insert(RedBlackTreeNode<K, NodeValue> current, K key, NodeValue val) {
        RedBlackTreeNode<K, NodeValue> result = super.insert(current, key, val);
        updateHi(result);
        return result;
    }

    @Override
    protected RedBlackTreeNode<K, NodeValue> balance(RedBlackTreeNode<K, NodeValue> node) {
        RedBlackTreeNode<K, NodeValue> result = super.balance(node);
        updateHi(result);
        return result;
    }

    // sets the hi attribute of the given node to the max of the subtree or itself
    private void updateHi(RedBlackTreeNode<K, NodeValue> node) {
        if (node == null) {
            return;
        }

        T result = node.getVal().getInterval().getEnd();
        if (node.getRightChild() != null) {
            result = max(result, node.getRightChild().getVal().getHighValue());
        }

        if (node.getLeftChild() != null) {
            result = max(result, node.getLeftChild().getVal().getHighValue());
        }

        node.getVal().setHighValue(result);
    }

    public T max(T t1, T t2) {
        if (t1 == null || t2 == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }
        return t1.compareTo(t2) > 0 ? t1 : t2;
    }

    private void overlapsWith(RedBlackTreeNode<K, NodeValue> node, Interval<T> interval, List<Interval<T>> result) {
        if (node == null) {
            return;
        }

        // query starts strictly after any interval in the subtree
        if (interval.getStart().compareTo(node.getVal().getHighValue()) > 0) {
            return;
        }

        // node and query overlap
        if (node.getVal().getInterval().isIntersecting(interval)) {
            result.add(node.getVal().getInterval());
        }

        // if the node starts before the query ends, check right children
        if (node.getVal().getInterval().getStart().compareTo(interval.getEnd()) <= 0) {
            overlapsWith(node.getRightChild(), interval, result);
        }

        overlapsWith(node.getLeftChild(), interval, result);
    }

    private void overlapsWithPoint(RedBlackTreeNode<K, NodeValue> node, T point, List<Interval<T>> result) {
        if (node == null) {
            return;
        }

        // point is bigger than the endpoint of any interval in the subtree
        if (point.compareTo(node.getVal().getHighValue()) > 0) {
            return;
        }

        // check left subtrees
        overlapsWithPoint(node.getLeftChild(), point, result);

        // add node interval if it contains the query point
        if (node.getVal().getInterval().contains(point)) {
            result.add(node.getVal().getInterval());
        }

        // check right subtree if their start values are smaller (equal) than query point
        if (point.compareTo(node.getVal().getInterval().getStart()) >= 0) {
            overlapsWithPoint(node.getRightChild(), point, result);
        }
    }
}
