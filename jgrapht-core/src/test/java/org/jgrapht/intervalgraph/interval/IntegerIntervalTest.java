package org.jgrapht.intervalgraph.interval;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerIntervalTest {
    IntegerInterval i00 = new IntegerInterval(0,0);
    IntegerInterval i01 = new IntegerInterval(0,1);
    IntegerInterval i56 = new IntegerInterval(5,6);

    @Test
    public void isIntersecting() {
    }

    @Test
    public void contains() {
        assertEquals(i00.contains(0), true);
        assertEquals(i01.contains(0), true);
        assertEquals(i00.contains(1), false);
    }

    @Test
    public void relativeDistance() {
    }

    @Test
    public void compareTo() {
        assertEquals(i00.compareTo(i01), 0);
        assertEquals(i01.compareTo(i00), 0);
        assertEquals(i56.compareTo(i01), 1);
        assertEquals(i00.compareTo(i56), -1);
    }

    @Test
    public void isValid() {
        assertEquals(i00.isValid(), true);
        assertEquals(i01.isValid(), true);
    }
}