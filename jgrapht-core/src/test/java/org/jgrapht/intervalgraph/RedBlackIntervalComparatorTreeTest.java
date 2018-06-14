package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.IntegerInterval;
import org.jgrapht.intervalgraph.interval.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class RedBlackIntervalComparatorTreeTest {

    List<IntegerInterval> sorted = new LinkedList<>();
    RedBlackIntervalComparatorTree<Integer, IntervalTreeNodeValue<Interval<Integer>, Integer>> tree = new RedBlackIntervalComparatorTree<>();

    @Before
    public void setUp() {
        for (int i = 0; i < 20; i++) {
            IntegerInterval interval = new IntegerInterval(i, i+3);
            tree.insert(new IntervalTreeNodeValue<>(interval));
            sorted.add(interval);
        }
    }

    @Test
    public void testInorder() {
        List<IntervalTreeNodeValue<Interval<Integer>, Integer>> result = tree.inorderValues();
        for (int i1 = 0, resultSize = result.size(); i1 < resultSize; i1++) {
            Interval<Integer> i = result.get(i1).getInterval();
            assertEquals("fault at " + i1,i, sorted.get(i1));
        }


        assertTrue(tree.contains(new IntervalTreeNodeValue<>(new IntegerInterval(5,8))));
        tree.delete(new IntervalTreeNodeValue<>(new IntegerInterval(5,8)));
        assertFalse(tree.contains(new IntervalTreeNodeValue<>(new IntegerInterval(5,8))));
        assertEquals(Integer.valueOf(19 + 3), tree.getRoot().getVal().getHighValue());
    }
}