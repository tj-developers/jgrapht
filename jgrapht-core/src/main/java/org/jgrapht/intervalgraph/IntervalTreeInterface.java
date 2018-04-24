package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

import java.util.List;

public interface IntervalTreeInterface<T extends Comparable<T>, NodeValue extends IntervalTreeNodeValue<Interval<T>, T>> extends BinarySearchTree<T, NodeValue> {

    List<Interval<T>> overlapsWith(Interval<T> interval);

    List<Interval<T>> overlapsWithPoint(T point);

}
