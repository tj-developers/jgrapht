package org.jgrapht.util;

import org.jgrapht.util.interval.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RedBlackIntervalTreeTest {

    List<Interval<Integer>> sorted = new LinkedList<>();
    RedBlackIntervalTree<IntervalTreeNodeKey<Interval<Integer>>, IntervalTreeNodeValue<Interval<Integer>, Integer>, Integer> tree = new RedBlackIntervalTree<>();

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < 20; i++) {
            Interval<Integer> interval = new Interval<>(i, i + 3);
            tree.insert(new IntervalTreeNodeKey<>(interval, getComparator()), new IntervalTreeNodeValue<>(interval));
            sorted.add(interval);
        }
    }

    @Test
    public void testInorder() {
        List<IntervalTreeNodeValue<Interval<Integer>, Integer>> result = tree.inorderValues();
        for (int i1 = 0, resultSize = result.size(); i1 < resultSize; i1++) {
            Interval<Integer> i = result.get(i1).getInterval();
            assertEquals("fault at " + i1, i, sorted.get(i1));
        }



        tree.delete(new IntervalTreeNodeKey<>(new Interval<>(5, 8), getComparator()));
        assertFalse(tree.contains(new IntervalTreeNodeKey<>(new Interval<>(5, 8), getComparator())));
        assertEquals(Integer.valueOf(19 + 3), tree.getRoot().getVal().getHighValue());
    }

    private Comparator<Interval<Integer>> getComparator() {
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

