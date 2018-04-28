package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.IntegerInterval;
import org.jgrapht.intervalgraph.interval.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class CenteredIntervalTreeTest {

    List<Interval<Integer>> list;

    @Test
    public void testCompareToPoint() {
        IntegerInterval interval = new IntegerInterval(0, 1);
        assertEquals(-1, interval.compareToPoint(-1));
        assertEquals(0, interval.compareToPoint(0));
        assertEquals(0, interval.compareToPoint(1));
        assertEquals(1, interval.compareToPoint(2));
    }

    @Test
    public void testEmptyTree() {
        CenteredIntervalTree<Integer> tree = new CenteredIntervalTree<>(list);
        assertEquals(0, tree.intersections(-2).size());
        assertEquals(0, tree.intersections(0).size());
        assertEquals(0, tree.intersections((2)).size());
    }

    @Test
    public void testSingleInterval() {
        list.add(new IntegerInterval(0, 2));
        CenteredIntervalTree<Integer> tree = new CenteredIntervalTree<>(list);
        assertEquals(0, tree.intersections(-1).size());
        assertEquals(1, tree.intersections(0).size());
        assertEquals(1, tree.intersections(1).size());
        assertEquals(1, tree.intersections(2).size());
        assertEquals(0, tree.intersections(3).size());
    }

    @Test
    public void testPath() {
        list.add(new IntegerInterval(0, 1));
        list.add(new IntegerInterval(1, 2));
        list.add(new IntegerInterval(2, 3));
        list.add(new IntegerInterval(3, 4));
        list.add(new IntegerInterval(4, 5));
        list.add(new IntegerInterval(5, 6));
        CenteredIntervalTree<Integer> tree = new CenteredIntervalTree<>(list);
        assertEquals(0, tree.intersections(-1).size());
        assertEquals(1, tree.intersections(0).size());
        assertEquals(2, tree.intersections(1).size());
        assertEquals(2, tree.intersections(2).size());
        assertEquals(2, tree.intersections(3).size());
        assertEquals(2, tree.intersections(4).size());
        assertEquals(2, tree.intersections(5).size());
        assertEquals(1, tree.intersections(6).size());
        assertEquals(0, tree.intersections(7).size());
    }

    @Before
    public void setUp() {
        list = new LinkedList<>();
    }
}
