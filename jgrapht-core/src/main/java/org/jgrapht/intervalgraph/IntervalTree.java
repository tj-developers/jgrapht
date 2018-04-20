package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class IntervalTree<T extends Comparable<T>> implements IntervalTreeInterface<T>, Serializable {
    private static final long serialVersionUID = 2834567756342332325L;

    private RedBlackIntervalTree<T, Interval<T>> tree = new RedBlackIntervalTree<>();

    /**
     * Returns all intervals that overlap with the given <code>interval</code>
     *
     * @param interval the interval
     * @return all intervals that overlap with the given <code>interval</code>
     */
    @Override
    public List<Interval<T>> overlapsWith(Interval<T> interval) {
        List<Interval<T>> result = new LinkedList<>();

        overlapsWith(tree.getRoot(), interval, result);

        return result;
    }

    private void overlapsWith(Node<T, Interval<T>> node, Interval<T> interval, List<Interval<T>> result) {
        if (node == null) {
            return;
        }

        // query starts strictly after any interval in the subtree
        if (interval.getStart().compareTo(node.getHi()) > 0) {
            return;
        }

        // node and query overlap
        if (node.getVal().isIntersecting(interval)) {
            result.add(node.getVal());
        }

        // if the node starts before the query ends, check right children
        if (node.getVal().getStart().compareTo(interval.getEnd()) <= 0) {
           overlapsWith(node.getRightChild(), interval, result);
        }

        overlapsWith(node.getLeftChild(), interval, result);
    }

    @Override
    public List<Interval<T>> overlapsWithPoint(T point) {
        List<Interval<T>> result = new LinkedList<>();

        overlapsWithPoint(tree.getRoot(), point, result);
        return result;
    }

    private void overlapsWithPoint(Node<T, Interval<T>> node, T point, List<Interval<T>> result) {
        if (node == null) {
            return;
        }

        // point is bigger than the endpoint of any interval in the subtree
        if (point.compareTo(node.getHi()) > 0) {
            return;
        }

        // check left subtrees
        overlapsWithPoint(node.getLeftChild(), point, result);

        // add node interval if it contains the query point
        if (node.getVal().contains(point)) {
            result.add(node.getVal());
        }

        // check right subtree if their start values are smaller (equal) than query point
        if (point.compareTo(node.getVal().getStart()) >= 0) {
            overlapsWithPoint(node.getRightChild(), point, result);
        }
    }


    /**
     * adds an interval to the interval tree
     *
     * @param interval the interval
     */
    @Override
    public void add(Interval<T> interval) {
        tree.insert(interval.getStart(), interval);
    }

    /**
     * removes an interval from the tree
     *
     * @param interval the interval
     * @return
     */
    @Override
    public void remove(Interval<T> interval) {
        tree.delete(interval.getStart());
    }
}
