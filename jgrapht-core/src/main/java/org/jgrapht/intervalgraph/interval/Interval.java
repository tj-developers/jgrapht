package org.jgrapht.intervalgraph.interval;

import java.util.Objects;

/**
 * The base class for an interval
 * @param <T> The underlying type
 */
public class Interval<T extends Comparable<T>> implements Comparable<Interval<T>> {
    private T start;
    private T end;

    /**
     * Creates a new class of the interval class
     * @param start The start point
     * @param end The end point
     */
    public Interval(T start, T end)  {
        this.start = start;
        this.end = end;

        if (start == null || end == null || !isValid())
            throw new IllegalArgumentException();
    }

    /**
     * Gets the start point
     * @return The start point
     */
    public T getStart() {
        return start;
    }

    /**
     * Gets the end point
     * @return The end point
     */
    public T getEnd() {
        return end;
    }

    /**
     * Checks whether or not the other interval intersects this interval
     * @param other The other interval
     * @return true, if the other interval intersects this, otherwise false
     */
    public boolean isIntersecting(Interval<T> other) {
        return this.contains(other.getStart()) || this.contains(other.getEnd()) || other.contains(this.getStart());
    }

    /**
     * Checks whether or not the point is contained in this interval
     * @param point The point
     * @return true, if the point is contained in this, otherwise false
     */
    public boolean contains(T point) {
        if (point == null) {
            throw new IllegalArgumentException();
        }

        boolean result = point.compareTo(getStart()) >= 0 && point.compareTo(getEnd()) <= 0;
        assert result == (compareToPoint(point) == 0);
        return result;
    }

    /**
     * Compares the point to this interval
     * @param point The point
     * @return A value < 0 if the interval is left of the point, 0 if the point is contained in the interval,
     * otherwise a value > 0
     */
    public int compareToPoint(T point) {
        if (point == null) {
            throw new IllegalArgumentException();
        }

        int relativeStart = getStart().compareTo(point);
        int relativeEnd = getEnd().compareTo(point);

        if (relativeStart <= 0 && relativeEnd >= 0) {
            return 0;
        } else  {
            return relativeStart;
        }
    }

    /**
     * Compares this interval to the other interval
     * @param other The other interval
     * @return A value < 0 if the this interval is completely left of the other interval, 0 if the intervals intersect,
     * otherwise a value > 0
     */
    @Override
    public int compareTo(Interval<T> other) {
        int isLeft = getEnd().compareTo(other.getStart()); // < 0 if this ends before other starts
        int isRight = getStart().compareTo(other.getEnd()); // > 0 if this starts before other ends

        if (isLeft >= 0 && isRight <= 0) {
            return 0;
        } else if (isLeft < 0) {
            return isLeft;
        } else {
            return isRight;
        }
    }

    /**
     * Checks whether or not the current interval is valid
     * @return true, if the end point is at least as big as the start point, otherwise false
     */
    public boolean isValid() {
        return getStart().compareTo(getEnd()) <= 0;
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
}
