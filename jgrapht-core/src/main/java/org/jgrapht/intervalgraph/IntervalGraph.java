package org.jgrapht.intervalgraph;

import org.jgrapht.Graph;
import org.jgrapht.intervalgraph.interval.Interval;

import java.util.Collection;

public interface IntervalGraph<T extends Comparable<T>> {
    Collection<Interval<T>> intersections(T queryInterval);

    Collection<Interval<T>> intersections(Interval<T> queryInterval);

    Graph asGraph();
}
