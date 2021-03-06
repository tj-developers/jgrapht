/*
 * (C) Copyright 2003-2018, by Christoph Grüne and Contributors.
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

import java.io.Serializable;
import java.util.Objects;

/**
 * Implementation of IntervalTreeNodeValue
 * This is a container class to store the necessary data for the (augmented) interval tree in the nodes of the tree.
 *
 * @param <I> the type of the interval
 * @param <T> The underlying type for the intervals
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
public class IntervalTreeNodeValue<I extends Interval<T>, T extends Comparable<T>> implements Serializable {

    private static final long serialVersionUID = 1111005364785643338L;

    /**
     * The interval
     */
    private Interval<T> interval;

    /**
     * the greatest end point of all intervals in the subtree rooted at that node.
     */
    private T highValue;

    /**
     * Create a new pair
     *
     * @param interval the first element
     */
    IntervalTreeNodeValue(I interval) {
        this.interval = interval;
        this.highValue = interval.getEnd();
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
    T getHighValue()
    {
        return highValue;
    }

    /**
     * Sets the high value
     * @param highValue The high value
     */
    void setHighValue(T highValue) {
        this.highValue = highValue;
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
        return interval.equals(other.interval);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(interval, highValue);
    }
}
