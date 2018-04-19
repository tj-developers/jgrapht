package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

import java.io.Serializable;
import java.util.List;

public class IntervalTree<T extends Comparable<T>> implements IntervalTreeInterface<T>, Serializable {
    private static final long serialVersionUID = 2834567756342332325L;

    /**
     * Returns all intervals that overlap with the given <code>interval</code>
     *
     * @param interval the interval
     * @return all intervals that overlap with the given <code>interval</code>
     */
    @Override
    public List<Interval<T>> overlapsWith(Interval<T> interval) {
        return null;
    }

    /**
     * adds an interval to the interval tree
     *
     * @param interval the interval
     * @return
     */
    @Override
    public boolean add(Interval<T> interval) {
        return false;
    }

    /**
     * removes an interval from the tree
     *
     * @param interval the interval
     * @return
     */
    @Override
    public boolean remove(Interval<T> interval) {
        return false;
    }
}