package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

import java.io.Serializable;
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

    private IntervalTreeInterface<T, IntervalTreeNodeValue<Interval<T>, T>> tree = new RedBlackIntervalTree<>();

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
        tree.insert(interval.getStart(), new IntervalTreeNodeValue<>(interval));
    }

    /**
     * removes an interval from the tree
     *
     * @param interval the interval
     * @return
     */
    @Override
    public void remove(Interval<T> interval) {
        tree.delete(interval.getStart());
    }
}
