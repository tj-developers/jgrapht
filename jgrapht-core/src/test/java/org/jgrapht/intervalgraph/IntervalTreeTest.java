package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.IntegerInterval;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class IntervalTreeTest {
    List<IntegerInterval> sorted = new LinkedList<>();
    IntervalTree<Integer> tree = new IntervalTree<>();

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < 20; i++) {
            IntegerInterval interval = new IntegerInterval(i, i+3);
            tree.add(interval);
            sorted.add(interval);
        }
    }

    @Test
    public void test1() {
        for (int i = 3; i < 20; i++) {
            assertEquals("loop " + i, 4, tree.overlapsWithPoint(i).size());
        }
    }


}