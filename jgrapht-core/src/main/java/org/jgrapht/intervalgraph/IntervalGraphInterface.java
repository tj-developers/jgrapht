package org.jgrapht.intervalgraph;

import org.jgrapht.Graph;
import org.jgrapht.intervalgraph.interval.Interval;

import java.util.Collection;

/**
 * An interface for interval graph containers
 * @param <T> The underlying type for the intervals
 */
public interface IntervalGraphInterface<T extends Comparable<T>> {

    /**
     * Returns a collection of intersecting intervals
     * @param queryPoint The query point
     * @return A collection of intersecting intervals
     */
    Collection<Interval<T>> intersections(T queryPoint);

    /**
     * Returns a collection of intersecting intervals
     * @param queryInterval The query interval
     * @return A collection of intersecting intervals
     */
    Collection<Interval<T>> intersections(Interval<T> queryInterval);

    /**
     * Returns a graph of the stored intervals
     * @return A graph of the stored intervals
     */
    Graph asGraph();
}
