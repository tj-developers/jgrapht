package org.jgrapht.intervalgraph;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RedBlackComparatorTreeTest {

    RedBlackComparatorTree<Integer> tree = new RedBlackComparatorTree<>(Integer::compareTo);

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < 100; i++) {
            tree.insert(i);
        }

        tree.delete(5);
        tree.delete(73);
        tree.delete(200);
    }

    @Test
    public void isOrdered() {
        for (int i = 0; i < 5; i++) {
            assertEquals(i, (int) tree.inorderValues().get(i));
        }
        for (int i = 6; i < 73; i++) {
            assertEquals(i, (int) tree.inorderValues().get(i - 1));
        }
        for (int i = 74; i < 100; i++) {
            assertEquals(i, (int) tree.inorderValues().get(i - 2));
        }
    }

    @Test
    public void testContains() {
        assertTrue(tree.contains(0));
        assertTrue(tree.contains(4));

        assertFalse(tree.contains(5));
        assertFalse(tree.contains(73));
        assertFalse(tree.contains(200));
    }
}
