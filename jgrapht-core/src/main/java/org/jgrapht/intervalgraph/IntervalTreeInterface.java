package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

import java.util.List;

public interface IntervalTreeInterface<T extends Comparable<T>> {

    /**
     * Returns all intervals that overlap with the given <code>interval</code>
     *
     * @param interval the interval
     * @return all intervals that overlap with the given <code>interval</code>
     */
    List<Interval<T>> overlapsWith(Interval<T> interval);

    /**
     * adds an interval to the interval tree
     *
     * @param interval the interval
     * @return
     */
    boolean add(Interval<T> interval);

    /**
     * removes an interval from the tree
     *
     * @param interval the interval
     * @return
     */
    boolean remove(Interval<T> interval);
}
