package org.jgrapht.util.interval;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerIntervalTest {

    private IntegerInterval i00 = new IntegerInterval(0,0);
    private IntegerInterval i01 = new IntegerInterval(0,1);
    private IntegerInterval i56 = new IntegerInterval(5,6);

    @Test
    public void testIntersecting() {
        assertTrue(i00.isIntersecting(i01));
        assertTrue(i01.isIntersecting(i01));
        assertFalse(i56.isIntersecting(i01));
    }

    @Test
    public void testContains() {
        assertTrue(i00.contains(0));
        assertTrue(i01.contains(0));
        assertFalse(i00.contains(1));
    }

    @Test
    public void testCompareTo() {
        assertEquals(i00.compareTo(i01), 0);
        assertEquals(i01.compareTo(i00), 0);
        assertEquals(i56.compareTo(i01), 1);
        assertEquals(i00.compareTo(i56), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInterval1() {
        new Interval(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInterval2() {
        new IntegerInterval(6, 5);
    }
}
