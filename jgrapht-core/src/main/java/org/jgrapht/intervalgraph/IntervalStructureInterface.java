package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

import java.util.List;

public interface IntervalStructureInterface<T extends Comparable<T>> {

    /**
     * Returns all intervals that overlap with the given <code>interval</code>
     *
     * @param interval the interval
     * @return all intervals that overlap with the given <code>interval</code>
     */
    List<Interval<T>> overlapsWith(Interval<T> interval);


    /**
     * Returns all intervals that overlap with the given <code>point</code>
     * @param point the point
     * @return all intervals that overlap with the given <code>point</code>
     */
    List<Interval<T>> overlapsWithPoint(T point);

    /**
     * adds an interval to the interval tree
     *
     * @param interval the interval
     */
    void add(Interval<T> interval);

    /**
     * removes an interval from the tree
     *
     * @param interval the interval
     * @return
     */
    void remove(Interval<T> interval);
}
