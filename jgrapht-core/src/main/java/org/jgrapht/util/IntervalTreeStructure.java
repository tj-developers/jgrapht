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
package org.jgrapht.util;

import org.jgrapht.util.interval.Interval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of IntervalTreeStructure
 * This class realises the interval tree structure, which has a interval tree as maintaining object.
 *
 * @param <T> the type of the interval
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
public class IntervalTreeStructure<T extends Comparable<T>> implements IntervalStructureInterface<T>, Serializable {

    private static final long serialVersionUID = 2834567756342332325L;

    private IntervalTreeInterface<T, IntervalTreeNodeKey<Interval<T>>, IntervalTreeNodeValue<Interval<T>, T>> tree;

    /**
     * Initializes a new instance of the class
     */
    public IntervalTreeStructure() {
        this.tree = new RedBlackIntervalTree<>();
    }

    /**
     * Initializes a new instance of the class
     * @param intervals The set of contained intervals
     */
    public IntervalTreeStructure(ArrayList<Interval<T>> intervals) {
        ArrayList<IntervalTreeNodeKey<Interval<T>>> keys = new ArrayList<>(intervals.size());
        ArrayList<IntervalTreeNodeValue<Interval<T>, T>> values = new ArrayList<>(intervals.size());

        for(int i = 0; i < intervals.size(); ++i) {
            keys.add(i, new IntervalTreeNodeKey<>(intervals.get(i), getComparator()));
            values.add(i, new IntervalTreeNodeValue<>(intervals.get(i)));
        }

        this.tree = new RedBlackIntervalTree<>(keys, values);
    }

    /**
     * Returns all intervals that overlap with the given <code>interval</code>
     *
     * @param interval the interval
     * @return all intervals that overlap with the given <code>interval</code>
     */
    @Override
    public List<Interval<T>> overlapsWith(Interval<T> interval) {
        return tree.overlapsWith(interval);
    }

    @Override
    public List<Interval<T>> overlapsWithPoint(T point) {
        return tree.overlapsWith(point);
    }

    /**
     * adds an interval to the interval tree
     *
     * @param interval the interval
     */
    @Override
    public void add(Interval<T> interval) {
        tree.insert(new IntervalTreeNodeKey<>(interval, getComparator()), new IntervalTreeNodeValue<>(interval));
    }

    /**
     * removes an interval from the tree
     *
     * @param interval the interval
     */
    @Override
    public void remove(Interval<T> interval) {
        tree.delete(new IntervalTreeNodeKey<>(interval, getComparator()));
    }

    /**
     * returns the comparator used to compare the keys in the interval tree
     *
     */
    private Comparator<Interval<T>> getComparator() {
        return (o1, o2) -> {
            int startCompare = o1.getStart().compareTo(o2.getStart());
            if (startCompare != 0) {
                return startCompare;
            } else {
                return o1.getEnd().compareTo(o2.getEnd());
            }
        };
    }
}
