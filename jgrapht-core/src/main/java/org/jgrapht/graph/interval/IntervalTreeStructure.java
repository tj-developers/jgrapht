/*
 * (C) Copyright 2018-2018, by Christoph Grüne and Contributors.
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of IntervalTreeStructure
 * This class realises the interval tree structure, which has a interval tree as maintaining object.
 *
 * @param <T> the type of the interval
 *
 * @author Christoph Grüne
 * @since Apr 26, 2018
 */
public class IntervalTreeStructure<T extends Comparable<T>> implements IntervalIndex<T>, Serializable {

    private static final long serialVersionUID = 2834567756342332325L;

    private IntervalTree<Interval<T>, IntervalTreeNodeValue<Interval<T>, T>, T> tree;

    /**
     * Initializes a new instance of the class
     */
    public IntervalTreeStructure() {
        this.tree = new RedBlackIntervalTree<>(getComparator());
    }

    /**
     * Initializes a new instance of the class.
     * Runs in linear time if list is sorted, otherwise naive insertion is performed
     *
     * @param intervals The set of contained intervals
     */
    public IntervalTreeStructure(ArrayList<Interval<T>> intervals) {
        ArrayList<Interval<T>> keys = new ArrayList<>(intervals.size());
        ArrayList<IntervalTreeNodeValue<Interval<T>, T>> values = new ArrayList<>(intervals.size());

        for(int i = 0; i < intervals.size(); ++i) {
            keys.add(i, intervals.get(i));
            values.add(i, new IntervalTreeNodeValue<>(intervals.get(i)));
        }

        this.tree = new RedBlackIntervalTree<>(keys, values, getComparator());
    }

    /**
     * Returns all intervals that overlap with the given <code>interval</code>
     *
     * @param interval the interval
     * @return all intervals that overlap with the given <code>interval</code>
     */
    public List<Interval<T>> findOverlappingIntervals(Interval<T> interval) {
        return tree.overlapsWith(interval);
    }

    /**
     * Returns all intervals that overlap with the given <code>point</code>
     * @param point the point
     * @return all intervals that overlap with the given <code>point</code>
     */
    public List<Interval<T>> findOverlappingIntervals(T point) {
        return tree.overlapsWith(point);
    }

    /**
     * Adds an interval to the interval tree. It returns true iff the key has been added successfully.
     *
     * @param interval the interval
     *
     * @return true, if the key was not contained in the tree; false, otherwise
     */
    public boolean add(Interval<T> interval) {
        if(interval == null) {
            throw new NullPointerException();
        }
        if(!tree.contains(interval)) {
            tree.insert(interval, new IntervalTreeNodeValue<>(interval));
            return true;
        }
        return false;
    }

    /**
     * Removes an interval from the tree. It returns true iff the key has been removed successfully.
     *
     * @param interval the interval
     *
     * @return true, if the key was contained in the tree; false, otherwise
     */
    public boolean remove(Interval<T> interval) {
        if(interval == null) {
            throw new NullPointerException();
        }
        return tree.delete(interval);
    }

    /**
     * Returns the comparator used to compare the keys in the interval tree
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
