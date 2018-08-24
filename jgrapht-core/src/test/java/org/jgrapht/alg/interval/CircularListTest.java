package org.jgrapht.alg.interval;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for CircularListNode
 *
 * @author Jiong Fu (magnificent_tony)
 */
public class CircularListTest {

    @Test
    public void testInitNode() {
        CircularListNode<Integer> node = new CircularListNode<>(1);

        assertEquals(1, node.element().intValue());
        assertEquals(node.prev(), node);
        assertEquals(node.next(), node);
    }

    @Test
    public void testAddPreviousNode() {
        CircularListNode<Integer> node = new CircularListNode<>(1);
        CircularListNode<Integer> prev1 = node.addPrev(2);
        CircularListNode<Integer> prev2 = prev1.addPrev(3);

        // test properties of the current node
        assertEquals(1, node.element().intValue());
        assertEquals(node.prev(), prev1);
        assertEquals(node.next(), prev2);

        // test properties of the first previous node
        assertEquals(2, prev1.element().intValue());
        assertEquals(prev1.prev(), prev2);
        assertEquals(prev1.next(), node);

        // test properties of the second previous node
        assertEquals(3, prev2.element().intValue());
        assertEquals(prev2.prev(), node);
        assertEquals(prev2.next(), prev1);

        // test properties after removing
        prev1.remove();
        assertEquals(node.prev(), prev2);
        assertEquals(prev2.next(), node);
    }

    @Test
    public void testAddNextNode() {
        CircularListNode<Integer> node = new CircularListNode<>(1);
        CircularListNode<Integer> next1 = node.addNext(2);
        CircularListNode<Integer> next2 = next1.addNext(3);

        // test properties of the current node
        assertEquals(1, node.element().intValue());
        assertEquals(node.prev(), next2);
        assertEquals(node.next(), next1);

        // test properties of the first next node
        assertEquals(2, next1.element().intValue());
        assertEquals(next1.prev(), node);
        assertEquals(next1.next(), next2);

        // test properties of the second next node
        assertEquals(3, next2.element().intValue());
        assertEquals(next2.prev(), next1);
        assertEquals(next2.next(), node);

        // test properties after removing
        next1.remove();
        assertEquals(node.next(), next2);
        assertEquals(next2.prev(), node);
    }

}
