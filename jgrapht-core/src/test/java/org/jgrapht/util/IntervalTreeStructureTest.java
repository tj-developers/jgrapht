/*
 * (C) Copyright 2003-2018, by Daniel Mock and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.util;

import org.jgrapht.util.interval.IntegerInterval;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Daniel Mock
 */
public class IntervalTreeStructureTest {

    private List<IntegerInterval> sorted = new LinkedList<>();
    private IntervalTreeStructure<Integer> tree = new IntervalTreeStructure<>();

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

    @Test
    public void testIntervalOverlap() {
        assertEquals(4, tree.overlapsWith(new IntegerInterval(0,3)).size());
        assertEquals(1 , tree.overlapsWith(new IntegerInterval(0,0)).size());
        assertEquals(0, tree.overlapsWith(new IntegerInterval(-3, -1)).size());
        assertEquals(20, tree.overlapsWith(new IntegerInterval(-5, 20)).size());
    }
}
