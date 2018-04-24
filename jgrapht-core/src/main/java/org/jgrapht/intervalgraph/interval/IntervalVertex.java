package org.jgrapht.intervalgraph.interval;

import org.jgrapht.alg.util.Pair;

import java.util.Objects;

public class IntervalVertex<V, T extends Comparable<T>> implements IntervalVertexInterface {
    private V vertex;
    private Interval<T> interval;

    public IntervalVertex(V vertex, Interval<T> interval) {
        this.vertex = vertex;
        this.interval = interval;
    }

    @Override
    public Interval<T> getInterval() {
        return interval;
    }

    @Override
    public V getVertex() {
        return vertex;
    }

    @Override
    public String toString()
    {
        return "(" + vertex + "," + interval + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        else if (!(o instanceof IntervalVertex))
            return false;

        @SuppressWarnings("unchecked") IntervalVertex<V, T> other = (IntervalVertex<V, T>) o;
        return Objects.equals(vertex, other.vertex) && Objects.equals(interval, other.interval);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(vertex, interval);
    }

    public static <V, T extends Comparable<T>> IntervalVertex<V, T> of(V vertex, Interval<T> interval) {
        return new IntervalVertex<>(vertex, interval);
    }
}
