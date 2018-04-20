package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

public class RedBlackIntervalTree<T extends Comparable<T>, I extends Interval<T>> extends RedBlackTree<T, I>{
    @Override
    protected Node<T, I> rotateRight(Node<T, I> node) {
        // Perform rotation as usual
        Node<T, I> result = super.rotateRight(node);

        // update hi vals
        result.setHi(node.getHi());
        updateHi(node);

        return result;
    }

    @Override
    protected Node<T, I> rotateLeft(Node<T, I> node) {
        // Perform rotation as usual
        Node<T, I> result = super.rotateLeft(node);

        // update hi vals
        result.setHi(node.getHi());
        updateHi(node);

        return result;
    }

    @Override
    protected Node<T, I> delete(Node<T, I> current, T key) {
        Node<T, I> result = super.delete(current, key);
        updateHi(result);
        return result;
    }

    @Override
    protected Node<T, I> insert(Node<T, I> current, T key, I val) {
        Node<T, I> result = super.insert(current, key, val);
        updateHi(result);
        return result;
    }

    @Override
    protected Node<T, I> balance(Node<T, I> node) {
        Node<T, I> result = super.balance(node);
        updateHi(result);
        return result;
    }

    // sets the hi attribute of the given node to the max of the subtree or itself
    private void updateHi(Node<T, I> node) {
        if (node == null) {
            return;
        }

        T result = node.getVal().getEnd();
        if (node.getRightChild() != null) {
            result = max(result, node.getRightChild().getHi());
        }

        if (node.getLeftChild() != null) {
            result = max(result, node.getLeftChild().getHi());
        }

        node.setHi(result);
    }


    // returns the max of two values
    public T max(T t1, T t2) {
        if (t1.compareTo(t2) > 0) {
            return t1;
        } else {
            return t2;
        }
    }
}
