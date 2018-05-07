package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

class RedBlackIntervalComparatorTree<T extends Comparable<T>, NodeValue extends IntervalTreeNodeValue<Interval<T>, T>> extends RedBlackComparatorTree<NodeValue> {
    public RedBlackIntervalComparatorTree(Comparator<Interval<T>> comparator) {
        super((o1, o2) -> comparator.compare(o1.getInterval(), o2.getInterval()));
    }


    /**
     * Constructs a new instance. The comparator compares the start value of both intervals.
     * In a tie, it compares the end values.
     */
    public RedBlackIntervalComparatorTree() {
        this((o1, o2) -> {
            int startCompare = o1.getStart().compareTo(o2.getStart());
            if (startCompare != 0) {
                return startCompare;
            } else {
                return o1.getEnd().compareTo(o2.getEnd());
            }
        });
    }

    private static final long serialVersionUID = 4353687394654923429L;

    
    public List<Interval<T>> overlapsWith(Interval<T> interval) {
        List<Interval<T>> result = new LinkedList<>();

        overlapsWith(this.getRoot(), interval, result);

        return result;
    }

    
    public List<Interval<T>> overlapsWith(T point) {
        List<Interval<T>> result = new LinkedList<>();

        overlapsWithPoint(this.getRoot(), point, result);
        return result;
    }

    
    protected RBNode<NodeValue> rotateRight(RBNode<NodeValue> node) {
        // Perform rotation as usual
        RBNode<NodeValue> result = super.rotateRight(node);

        // update hi vals
        result.getVal().setHighValue(node.getVal().getHighValue());
        updateHi(node);

        return result;
    }

    
    protected RBNode<NodeValue> rotateLeft(RBNode<NodeValue> node) {
        // Perform rotation as usual
        RBNode<NodeValue> result = super.rotateLeft(node);

        // update hi vals
        result.getVal().setHighValue(node.getVal().getHighValue());
        updateHi(node);

        return result;
    }

    @Override
    protected RBNode<NodeValue> delete(RBNode<NodeValue> current, NodeValue val) {
        RBNode<NodeValue> result = super.delete(current, val);
        updateHi(result);
        return result;
    }

    
    protected RBNode<NodeValue> insert(RBNode<NodeValue> current, NodeValue val) {
        RBNode<NodeValue> result = super.insert(current, val);
        updateHi(result);
        return result;
    }

    
    protected RBNode<NodeValue> balance(RBNode<NodeValue> node) {
        RBNode<NodeValue> result = super.balance(node);
        updateHi(result);
        return result;
    }

    // sets the hi attribute of the given node to the max of the subtree or itself
    private void updateHi(RBNode<NodeValue> node) {
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

    private void overlapsWith(RBNode<NodeValue> node, Interval<T> interval, List<Interval<T>> result) {
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

    private void overlapsWithPoint(RBNode<NodeValue> node, T point, List<Interval<T>> result) {
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
