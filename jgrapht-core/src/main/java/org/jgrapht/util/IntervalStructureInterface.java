package org.jgrapht.util;

import org.jgrapht.util.interval.Interval;

import java.util.List;

/**
 * Interface of IntervalStructure
 * This interface is used for an implementation of an efficient data structure to maintain intervals.
 *
 * @param <T> the type of the interval
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
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
     */
    void remove(Interval<T> interval);
}
