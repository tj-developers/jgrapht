package org.jgrapht.alg.interval;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.ListIterator;

import static junit.framework.TestCase.assertEquals;

public class CircularLinkedListTest {

    private List<Integer> list = null;

    @Before
    public void prepareData() {
        list = new CircularLinkedList<>();
        list.add(1);
        list.add(3);
        list.add(5);
    }

    @Test
    public void testIterateNext() {
        ListIterator<Integer> listIterator = list.listIterator();
        assertEquals(1, listIterator.next().intValue());
        assertEquals(3, listIterator.next().intValue());
        assertEquals(5, listIterator.next().intValue());
        assertEquals(1, listIterator.next().intValue());
        assertEquals(3, listIterator.next().intValue());
    }

    @Test
    public void testIteratePrevious() {
        ListIterator<Integer> listIterator = list.listIterator();
        assertEquals(5, listIterator.previous().intValue());
        assertEquals(3, listIterator.previous().intValue());
        assertEquals(1, listIterator.previous().intValue());
        assertEquals(5, listIterator.previous().intValue());
        assertEquals(3, listIterator.previous().intValue());
    }

    @Test
    public void testAdd() {
        ListIterator<Integer> listIterator = list.listIterator();
        assertEquals(1, listIterator.next().intValue());
        listIterator.add(7);
        assertEquals(7, listIterator.previous().intValue());
        listIterator.add(9);
        assertEquals(9, listIterator.previous().intValue());
    }

    @Test
    public void testRemove() {
        ListIterator<Integer> listIterator = list.listIterator();
        assertEquals(1, listIterator.next().intValue());
        listIterator.remove();
        assertEquals(3, listIterator.next().intValue());
        listIterator.remove();
        assertEquals(5, listIterator.next().intValue());
    }

}
