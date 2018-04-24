package org.jgrapht.intervalgraph.interval;

public interface IntervalVertexInterface<V, T extends Comparable<T>> {

    V getVertex();

    Interval<T> getInterval();

}
