package org.jgrapht.intervalgraph;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RedBlackComparatorTreeTest {

    private RedBlackComparatorTree<Integer> redBlackComparatorTree = new RedBlackComparatorTree<>(Integer::compareTo);

    @Before
    public void setUp() {
        for (int i = 0; i < 100; i++) {
            redBlackComparatorTree.insert(i);
        }
    }

    @Test
    public void testOrdered() {
        for (int i = 0; i < 100; i++) {
            assertEquals(i, (int) redBlackComparatorTree.inorderValues().get(i));
        }
    }

    @Test
    public void testContains() {
        assertTrue(redBlackComparatorTree.contains(0));
        assertTrue(redBlackComparatorTree.contains(4));
    }

    @Test
    public void testContainsNegative() {
        redBlackComparatorTree.delete(5);
        redBlackComparatorTree.delete(73);
        redBlackComparatorTree.delete(200);

        assertFalse(redBlackComparatorTree.contains(5));
        assertFalse(redBlackComparatorTree.contains(73));
        assertFalse(redBlackComparatorTree.contains(200));
    }

    @Test
    public void testDelete() {
        redBlackComparatorTree.delete(25);
        redBlackComparatorTree.delete(25);
    }

    // Test important properties of red-black tree

    /**
     * The root is black
     */
    @Test
    public void testBlackRoot() {
        assertFalse(redBlackComparatorTree.getRoot().isRed());
    }

    /**
     * If a node is red, then both its children are black
     */
    @Test
    public void testBlackChildren() {
        testBlackChildren(redBlackComparatorTree.getRoot());
    }

    private <V> void testBlackChildren(RBNode<V> currentNode) {
        RBNode<V> leftChild = currentNode.getLeftChild();
        if (leftChild != null) {
            if (currentNode.isRed()) {
                assertFalse(leftChild.isRed());
            }
            testBlackChildren(leftChild);
        }

        RBNode<V> rightChild = currentNode.getRightChild();
        if (rightChild != null) {
            if (currentNode.isRed()) {
                assertFalse(rightChild.isRed());
            }
            testBlackChildren(rightChild);
        }
    }

    /**
     * Every path from a given node to any of its descendant leaves contains the same number of black nodes
     */
    @Test
    public void testBlackNodeNumber() {
        assertEquals(countLeftChildren(redBlackComparatorTree.getRoot(), 0), countRightChildren(redBlackComparatorTree.getRoot(), 0));
    }

    private <V> int countLeftChildren(RBNode<V> node, int currentBlackNumber) {
        currentBlackNumber = node.isRed() ? currentBlackNumber : currentBlackNumber + 1;
        return node.getLeftChild() == null ? currentBlackNumber : countLeftChildren(node.getLeftChild(), currentBlackNumber);
    }

    private <V> int countRightChildren(RBNode<V> node, int currentBlackNumber) {
        currentBlackNumber = node.isRed() ? currentBlackNumber : currentBlackNumber + 1;
        return node.getRightChild() == null ? currentBlackNumber : countRightChildren(node.getRightChild(), currentBlackNumber);
    }
}