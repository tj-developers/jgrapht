package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

import java.util.LinkedList;
import java.util.List;

// Essentially, this class updates the hi value after any operation on this tree.
// The hi value equals to the highest endpoint in the subtree
public class RedBlackIntervalTree<T extends Comparable<T>, NodeValue extends IntervalTreeNodeValue<Interval<T>, T>> extends RedBlackTree<T, NodeValue> implements IntervalTreeInterface<T, NodeValue> {

    @Override
    public List<Interval<T>> overlapsWith(Interval<T> interval) {
        List<Interval<T>> result = new LinkedList<>();

        overlapsWith(this.getRoot(), interval, result);

        return result;
    }

    @Override
    public List<Interval<T>> overlapsWithPoint(T point) {
        List<Interval<T>> result = new LinkedList<>();

        overlapsWithPoint(this.getRoot(), point, result);
        return result;
    }

    @Override
    protected Node<T, NodeValue> rotateRight(Node<T, NodeValue> node) {
        // Perform rotation as usual
        Node<T, NodeValue> result = super.rotateRight(node);

        // update hi vals
        result.getVal().setHighValue(node.getVal().getHighValue());
        updateHi(node);

        return result;
    }

    @Override
    protected Node<T, NodeValue> rotateLeft(Node<T, NodeValue> node) {
        // Perform rotation as usual
        Node<T, NodeValue> result = super.rotateLeft(node);

        // update hi vals
        result.getVal().setHighValue(node.getVal().getHighValue());
        updateHi(node);

        return result;
    }

    @Override
    protected Node<T, NodeValue> delete(Node<T, NodeValue> current, T key) {
        Node<T, NodeValue> result = super.delete(current, key);
        updateHi(result);
        return result;
    }

    @Override
    protected Node<T, NodeValue> insert(Node<T, NodeValue> current, T key, NodeValue val) {
        Node<T, NodeValue> result = super.insert(current, key, val);
        updateHi(result);
        return result;
    }

    @Override
    protected Node<T, NodeValue> balance(Node<T, NodeValue> node) {
        Node<T, NodeValue> result = super.balance(node);
        updateHi(result);
        return result;
    }

    // sets the hi attribute of the given node to the max of the subtree or itself
    private void updateHi(Node<T, NodeValue> node) {
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


    // returns the max of two values
    public T max(T t1, T t2) {
        if (t1.compareTo(t2) > 0) {
            return t1;
        } else {
            return t2;
        }
    }

    private void overlapsWith(Node<T, NodeValue> node, Interval<T> interval, List<Interval<T>> result) {
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

    private void overlapsWithPoint(Node<T, NodeValue> node, T point, List<Interval<T>> result) {
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
