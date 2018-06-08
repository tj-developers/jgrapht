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
     * Initializes a new instance of the class.
     * Runs in linear time if list is sorted, otherwise naive insertion is performed
     *
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
     * adds an interval to the interval tree. It returns true iff the key has been added successfully.
     *
     * @param interval the interval
     *
     * @return true, if the key was not contained in the tree; false, otherwise
     */
    @Override
    public boolean add(Interval<T> interval) {
        if(interval == null) {
            throw new NullPointerException();
        }
        if(!tree.contains(new IntervalTreeNodeKey<>(interval, getComparator()))) {
            tree.insert(new IntervalTreeNodeKey<>(interval, getComparator()), new IntervalTreeNodeValue<>(interval));
            return true;
        }
        return false;
    }

    /**
     * removes an interval from the tree. It returns true iff the key has been removed successfully.
     *
     * @param interval the interval
     *
     * @return true, if the key was contained in the tree; false, otherwise
     */
    @Override
    public boolean remove(Interval<T> interval) {
        if(interval == null) {
            throw new NullPointerException();
        }
        return tree.delete(new IntervalTreeNodeKey<>(interval, getComparator()));
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
