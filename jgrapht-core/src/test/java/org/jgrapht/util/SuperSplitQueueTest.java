package org.jgrapht.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

public class SuperSplitQueueTest {
    @Test
    public void contains() {
        SubSplitQueue q = SubSplitQueue.subSplitQueueFactory(10);
        for (int i = 0; i < 10; i++) {
            assertTrue(q.contains(i));
        }

        assertFalse(q.contains(10));
    }

    @Test
    public void split() {
        SubSplitQueue q = SubSplitQueue.subSplitQueueFactory(10);
        assertTrue(q.contains(2));

        SubSplitQueue split = q.split(new int[] {2,5,9});

        assertTrue(split.contains(2));
        assertTrue(split.contains(5));
        assertTrue(split.contains(9));
        assertFalse(split.contains(1));

        assertFalse(q.contains(2));
        assertFalse(q.contains(5));
        assertFalse(q.contains(9));

        SubSplitQueue splitsplit = split.split(new int[] {1,2});

        assertTrue(splitsplit.contains(2));
        assertFalse(splitsplit.contains(1));
    }

    @Test
    public void asArray() {
        SubSplitQueue q = SubSplitQueue.subSplitQueueFactory(10);
        System.out.println(Arrays.toString(q.asArray()));
    }

    @Test
    public void split2() {
        SubSplitQueue q = SubSplitQueue.subSplitQueueFactory(10);
        assertEquals(10, q.getSize());
        assertArrayEquals(q.asArray(), new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});

        SubSplitQueue split = q.split(new int[] {2,5,9});
        assertArrayEquals(q.asArray(), new int[] {0, 1, 3, 4, 6, 7, 8});
        assertArrayEquals(split.asArray(), new int[] {2,5,9});

        SubSplitQueue splitsplit = split.split(new int[] {2});
        assertEquals(7, q.getSize());
        assertEquals(2, split.getSize());
        assertEquals(1, splitsplit.getSize());


        for (int i : split) {
            System.out.println(i);
        }

        System.out.println(Arrays.toString(q.parent.previous));
        System.out.println(q.parent.firstOfQ);

        assertArrayEquals(q.asArray(), new int[] {0, 1, 3, 4, 6, 7, 8});
        assertArrayEquals(split.asArray(), new int[] {5, 9});
        assertArrayEquals(splitsplit.asArray(), new int[] {2});
    }

    @Test
    public void iterator() {
        SubSplitQueue q = SubSplitQueue.subSplitQueueFactory(10);
        Iterator<Integer> it = q.iterator();
        for (int i = 0; i < 10; i++) {
            int element =  it.next();

            System.out.println(element);
            assertEquals(i, element);
        }

        SubSplitQueue split = q.split(new int[] {2,5,9});
        for (int i: split) {
            System.out.println(i);
        }
        for (int i: q) {
            System.out.println(i);
        }

        System.out.println("...");
    }

    @Test
    public void name() {
        SubSplitQueue q = SubSplitQueue.subSplitQueueFactory(10);
        SubSplitQueue split = q.split(new int[] {2,5,9});
        //SubSplitQueue empty = split.split(new int[] {1});
        //assertTrue(empty.isEmpty());

        SubSplitQueue splitSplit = split.split(new int[] {2,5,9});

        for (int i: splitSplit) {
            System.out.println(i);
        }

        System.out.println("...");

        for (int i: split) {
            System.out.println(i);
        }

    }
}