package org.jgrapht.intervalgraph;

import org.jgrapht.intervalgraph.interval.IntegerInterval;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class RedBlackIntervalTreeTest {
    List<IntegerInterval> list = new LinkedList<>();
    List<IntegerInterval> sorted = new LinkedList<>();
    RedBlackIntervalTree<Integer, IntegerInterval> tree = new RedBlackIntervalTree<>();

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < 20; i++) {
            IntegerInterval interval = new IntegerInterval(i, i+3);
            tree.insert(i, interval);
            sorted.add(interval);
        }
    }

    @Test
    public void testInorder() {
        List<IntegerInterval> result = tree.inorderValues();
        for (int i1 = 0, resultSize = result.size(); i1 < resultSize; i1++) {
            IntegerInterval i = result.get(i1);
            assertEquals("fault at " + i1,i, sorted.get(i1));
        }


        tree.delete(5);
        assertFalse(tree.contains(5));
        assertEquals(Integer.valueOf(19 + 3), tree.getRoot().getHi());
    }
}