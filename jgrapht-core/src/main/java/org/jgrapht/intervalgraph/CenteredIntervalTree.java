package org.jgrapht.intervalgraph;

import org.jgrapht.Graph;
import org.jgrapht.intervalgraph.interval.Interval;
import org.omg.SendingContext.RunTime;

import java.util.*;

public class CenteredIntervalTree<T extends Comparable<T>> implements IntervalGraph<T>{
    private T centerPoint;
    private CenteredIntervalTree<T> leftTree; // contains all the intervals *completely* to the left of the center point
    private CenteredIntervalTree<T> rightTree; // contains all the intervals *completely* to the right of the center point
    private List<Interval<T>> intersectionsByStart = new LinkedList<>(); // intervals intersecting center point, sorted by start point
    private List<Interval<T>> intersectionsByEnd = new LinkedList<>(); // ..., sorted by end point

    public CenteredIntervalTree(T centerPoint, List<Interval<T>> intervals) {
        this.centerPoint = centerPoint;
        compute(intervals);
    }

    public CenteredIntervalTree(List<Interval<T>> intervals) {
        this.centerPoint = intervals.get(0).getEnd(); // TODO replace this with something, its arbitrary
        compute(intervals);
    }

    private void compute( List<Interval<T>> intervals) {
        List<Interval<T>> leftIntervals = new LinkedList<>(); // containing the intervals completely to the left of the center point
        List<Interval<T>> rightIntervals = new LinkedList<>(); // ... to the right
        List<Interval<T>> intersectingIntervals = new LinkedList<>(); // intervals intersecting the center point

        for (Interval<T> interval: intervals) {
            if (interval.contains(centerPoint)) {
                intersectingIntervals.add(interval);
            }

            if (interval.relativeDistance(centerPoint) < 0) { // is completely left to center point
                leftIntervals.add(interval);
            } else if (interval.relativeDistance(centerPoint) > 0) { // completely right
                rightIntervals.add(interval);
            }
        }


        // sorting the intervals according to start/end point
        intersectionsByStart = new LinkedList<>(intersectingIntervals);
        intersectionsByStart.sort(Comparator.comparing(Interval::getStart));

        intersectionsByEnd = intersectingIntervals;
        intersectionsByEnd.sort(Comparator.comparing(Interval::getStart));


        // add children to the left and right
        if (!leftIntervals.isEmpty()){
            leftTree = new CenteredIntervalTree<>(leftIntervals);
        }

        if (!rightIntervals.isEmpty()){
            rightTree = new CenteredIntervalTree<>(rightIntervals);
        }
    }

    @Override
    public Collection<Interval<T>> intersections(T point) {
        Set<Interval<T>> result = new HashSet<>(); // TODO Be aware, that I dont know if using sets guarantees linear run time
        intersections(point, result);
        return result;
    }

    @Override
    public Collection<Interval<T>> intersections(Interval<T> queryInterval) {
        throw new RuntimeException();
    }

    private void intersections(T point, Set<Interval<T>> result) {
        if (point.equals(centerPoint)){
            result.addAll(intersectionsByEnd); // or intersectionsByStart
        } else if (point.compareTo(centerPoint) < 0) {
            // add all intervals with start point <= center point
            for (Interval<T> interval: intersectionsByStart) {
                if (interval.getStart().compareTo(point) > 0) {
                    break;
                }
                result.add(interval);
            }

            // recursion to child in tree
            if (leftTree != null) {
                leftTree.intersections(point, result);
            }
        } else { // point > centerPoint
            // add all intervals with end point >= center point
            for (Interval<T> interval: intersectionsByEnd) {
                if (interval.getEnd().compareTo(point) < 0) {
                    continue;
                }
                result.add(interval);
            }

            // recursion to child in tree
            if (rightTree != null) {
                rightTree.intersections(point, result);
            }
        }
    }

    @Override
    public Graph asGraph() {
        throw new RuntimeException();
    }
}
