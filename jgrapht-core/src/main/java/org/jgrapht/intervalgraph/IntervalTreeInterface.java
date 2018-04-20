package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.Interval;

import java.util.List;

public interface IntervalTreeInterface<T extends Comparable<T>, I extends Interval<T>> extends BinarySearchTree<T, I> {

    List<I> overlapsWith(I interval);

    List<I> overlapsWithPoint(T point);

}
