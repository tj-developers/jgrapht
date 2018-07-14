package org.jgrapht.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SubSplitQueueTest {

    @Test
    public void name() {
        SubSplitQueue q = SubSplitQueue.subSplitQueueFactory(10, new int[] {9,8,7,6,5,4,3,2,1,0});
        SubSplitQueue q2 = q.split(new int[]{7,6,5});
        System.out.println(Arrays.toString(q.asArray()));
        System.out.println(Arrays.toString(q2.asArray()));

        assertEquals(9, q.peek());
        assertEquals(7, q2.peek());
    }

    @Test
    public void test7() {
        SubSplitQueue q = SubSplitQueue.subSplitQueueFactory(10, new int[] {9,8,7,6,5,4,3,2,1,0});
        SubSplitQueue q2 = q.split(new int[] {7,6,5});
        SubSplitQueue q3 = q2.split(new int[] {9,7,2,1});
        SubSplitQueue q0 = q.split(new int[] {9,7,2,1});

        assertArrayEquals(new int[] {8,4,3,0}, q.asArray());
        assertArrayEquals(new int[] {6,5}, q2.asArray());
        assertArrayEquals(new int[] {7}, q3.asArray());
        assertArrayEquals(new int[] {9,2,1}, q0.asArray());
    }
}