package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.IntegerInterval;
import org.jgrapht.intervalgraph.interval.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class CenteredIntervalTreeStructureTest {

    IntegerInterval i00 = new IntegerInterval(0,0);
    IntegerInterval i01 = new IntegerInterval(0,1);
    IntegerInterval i56 = new IntegerInterval(5,6);

    List<Interval<Integer>> list;


    @Test
    public void intersections() {
        CenteredIntervalTree<Integer> tree = new CenteredIntervalTree<>(list);
        assertEquals(0, tree.intersections(2).size());
        assertEquals(2, tree.intersections(0).size());
        assertEquals(0, new CenteredIntervalTree<Integer>(new LinkedList<>()).intersections(0).size());
    }

    @Before
    public void setUp() {
        list = new LinkedList<>();
    }
}
