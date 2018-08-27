/*
 * (C) Copyright 2018-2018, by Daniel Mock and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.graph.interval;

import java.util.Comparator;
import java.util.Objects;

/**
 * Model of an interval in the interval graph. The intervals have closed bounds.
 *
 * @param <T> the type of the interval
 * @author Daniel Mock
 */
public class Interval<T extends Comparable<T>> implements Comparable<Interval<T>> {

    protected T start;
    protected T end;

    /**
     * Constructs an interval
     *
     * @param start interval start
     * @param end   interval end
     * @throws IllegalArgumentException if interval start or end is null, or if interval start is greater than interval end
     */
    public Interval(T start, T end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Interval start or end cannot be null.");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Interval start must be smaller than or equal to interval end.");
        }

        this.start = start;
        this.end = end;
    }

    /**
     * Get the start point of the interval
     *
     * @return the start point of the interval
     */
    public T getStart() {
        return start;
    }

    /**
     * Get the end point of the interval
     *
     * @return the end point of the interval
     */
    public T getEnd() {
        return end;
    }

    /**
     * Checks whether the other interval intersects this interval
     *
     * @param other The other interval
     * @return true if the other interval intersects this, false otherwise
     */
    public boolean isIntersecting(Interval<T> other) {
        return this.contains(other.getStart()) || this.contains(other.getEnd()) || other.contains(this.getStart());
    }

    /**
     * Check if current interval contains the given point
     *
     * @param point the point to be tested
     * @return true if current interval contains the given point, false otherwise
     */
    public boolean contains(T point) {
        if (point == null) {
            throw new IllegalArgumentException("Point to be tested cannot be null.");
        }

        boolean result = point.compareTo(start) >= 0 && point.compareTo(end) <= 0;
        assert result == (compareToPoint(point) == 0);
        return result;
    }

    /**
     * Compare current interval with the given point
     *
     * @param point the point to be tested
     * @return 0 if current interval contains the given point, comparison result with the interval start otherwise
     */
    private int compareToPoint(T point) {
        if (point == null) {
            throw new IllegalArgumentException("Point to be tested cannot be null.");
        }

        int relativeStart = start.compareTo(point);
        int relativeEnd = end.compareTo(point);

        if (relativeStart <= 0 && relativeEnd >= 0) {
            return 0;
        } else {
            return relativeStart;
        }
    }

    /**
     * Compares this interval to the other interval
     *
     * @param o The other interval
     * @return A value &lt; 0 if the this interval is completely left of the other interval, 0 if the intervals intersect,
     * otherwise a value &gt; 0
     */
    @Override
    public int compareTo(Interval<T> o) {
        int left = end.compareTo(o.getStart()); // < 0 if this ends before other starts
        int right = start.compareTo(o.getEnd()); // > 0 if this starts before other ends

        if (left >= 0 && right <= 0) {
            return 0;
        } else if (left < 0) {
            return left;
        } else {
            return right;
        }
    }

    @Override
    public String toString() {
        return "Interval[" + start + ", " + end + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interval<?> interval = (Interval<?>) o;
        return Objects.equals(start, interval.start) &&
                Objects.equals(end, interval.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
    /**
     * getter for a Comparator, which only compares the starting points
     * 
     * @param <T> the value of the interval
     * @return a starting point comparator
     */
    public static <T extends Comparable<T>> Comparator<Interval<T>> getStartingComparator()
    {
        return new Comparator<Interval<T>>() 
        {
            @Override
            public int compare(Interval<T> o1, Interval<T> o2)
            {
                return o1.getStart().compareTo(o2.getStart());
            }
        };
    }

    /**
     * getter for a Comparator, which only compares the ending points
     * 
     * @param <T> the value of the interval
     * @return a ending point comparator
     */
    public static <T extends Comparable<T>> Comparator<Interval<T>> getEndingComparator()
    {
        return new Comparator<Interval<T>>()
        {

            @Override
            public int compare(Interval<T> o1, Interval<T> o2)
            {
                return o1.getEnd().compareTo(o2.getEnd());
            }

        };
}
}
