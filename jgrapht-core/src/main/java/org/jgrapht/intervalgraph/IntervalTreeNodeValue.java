package org.jgrapht.intervalgraph;

import org.jgrapht.alg.util.Pair;
import org.jgrapht.intervalgraph.interval.Interval;

import java.io.Serializable;
import java.util.Objects;

public class IntervalTreeNodeValue<I extends Interval<T>, T extends Comparable<T>> implements Serializable {

    private static final long serialVersionUID = 1111005364785643338L;

    private Interval<T> interval;

    private T highValue;

    /**
     * Create a new pair
     *
     * @param interval the first element
     * @param highValue the second element
     */
    public IntervalTreeNodeValue(I interval, T highValue) {
        this.interval = interval;
        this.highValue = highValue;
    }

    /**
     * Get the first element of the pair
     *
     * @return the first element of the pair
     */
    public Interval<T> getInterval()
    {
        return interval;
    }

    /**
     * Get the second element of the pair
     *
     * @return the second element of the pair
     */
    public T getHighValue()
    {
        return highValue;
    }

    public void setHighValue(T highValue) {
        this.highValue = highValue;
    }


    public <E> boolean hasInterval(E e)
    {
        if (e == null) {
            return interval == null;
        } else {
            return e.equals(interval);
        }
    }

    public <E> boolean hasHighValue(E e)
    {
        if (e == null) {
            return highValue == null;
        } else {
            return e.equals(highValue);
        }
    }

    @Override
    public String toString()
    {
        return "(" + interval + "," + highValue + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        else if (!(o instanceof IntervalTreeNodeValue))
            return false;

        @SuppressWarnings("unchecked") IntervalTreeNodeValue<I, T> other = (IntervalTreeNodeValue<I, T>) o;
        return Objects.equals(interval, other.interval) && Objects.equals(highValue, other.highValue);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(interval, highValue);
    }
}
