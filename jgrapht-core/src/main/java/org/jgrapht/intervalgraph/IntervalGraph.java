package org.jgrapht.intervalgraph;

import java.util.Collection;

import org.jgrapht.Graph;
import org.jgrapht.intervalgraph.interval.Interval;

public interface IntervalGraph<T extends Comparable<T>> {
    Collection<Interval<T>> intersections(T queryInterval);

    Collection<Interval<T>> intersections(Interval<T> queryInterval);

    Graph asGraph();
}
