/*
 * (C) Copyright 2003-2018, by Daniel Mock and Contributors.
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
package org.jgrapht.util.interval;

/**
 * An implementation of integer intervals.
 * @author Daniel Mock
 */
public class IntegerInterval extends Interval<Integer> {

    /**
     * Construct an integer interval
     *
     * @param start interval start
     * @param end   interval end
     * @throws IllegalArgumentException if interval start or end is null, or if interval start is greater than interval end
     */
    public IntegerInterval(int start, int end) {
        super(start, end);
    }

    /**
     * Get the duration of the interval
     *
     * @return the duration of the interval
     */
    public int length() {
        return end - start;
    }

    /**
     * Returns a string representation for the interval
     *
     * @return A string representation for the interval
     */
    @Override
    public String toString() {
        return "IntegerInterval[" + start + ", " + end + "]";
    }
}
