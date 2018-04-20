package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class IntervalTreeStructure<T extends Comparable<T>> implements IntervalStructureInterface<T>, Serializable {
    private static final long serialVersionUID = 2834567756342332325L;

    private IntervalTreeInterface<T, Interval<T>> tree = new RedBlackIntervalTree<>();

    /**
     * Returns all intervals that overlap with the given <code>interval</code>
     *
     * @param interval the interval
     * @return all intervals that overlap with the given <code>interval</code>
     */
    @Override
    public List<Interval<T>> overlapsWith(Interval<T> interval) {
        return tree.overlapsWith(interval);
    }

    @Override
    public List<Interval<T>> overlapsWithPoint(T point) {
        return tree.overlapsWithPoint(point);
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
