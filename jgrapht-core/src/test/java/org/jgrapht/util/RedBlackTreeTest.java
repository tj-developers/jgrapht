/*
 * (C) Copyright 2003-2018, by Jiong Fu, Daniel Mock and Contributors.
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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Jiong Fu
 * @author Daniel Mock
 */
public class RedBlackTreeTest {

    private RedBlackTree<Integer, Integer> redBlackTree = new RedBlackTree<>();

    @Before
    public void setUp() {
        redBlackTree.insert(13, 13);
        redBlackTree.insert(8, 8);
        redBlackTree.insert(17, 17);
        redBlackTree.insert(1, 1);
        redBlackTree.insert(11, 11);
        redBlackTree.insert(15, 15);
        redBlackTree.insert(25, 25);
        redBlackTree.insert(6, 6);
        redBlackTree.insert(22, 22);
        redBlackTree.insert(27, 27);
    }

    @Test
    public void testGet() {
        assertEquals(8, redBlackTree.get(8).intValue());
        assertEquals(6, redBlackTree.get(6).intValue());
        assertNull(redBlackTree.get(30));
        assertNull(redBlackTree.get(35));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNegative() {
        redBlackTree.get(null);
    }

    @Test
    public void testContains(){
        assertTrue(redBlackTree.contains(1));
        assertTrue(redBlackTree.contains(11));
        assertFalse(redBlackTree.contains(30));
        assertFalse(redBlackTree.contains(35));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContainsNegative() {
        redBlackTree.contains(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertNegative() {
        redBlackTree.insert(null, null);
    }

    @Test
    public void testDelete() {
        assertTrue(redBlackTree.contains(15));
        redBlackTree.delete(15);
        redBlackTree.delete(15);
        assertFalse(redBlackTree.contains(15));

        redBlackTree.delete(30);
        redBlackTree.delete(35);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteNegative() {
        redBlackTree.delete(null);
    }

    // Test important properties of red-black tree

    /**
     * The root is black
     */
    @Test
    public void testBlackRoot() {
        assertFalse(redBlackTree.getRoot().isRed());
    }

    /**
     * If a node is red, then both its children are black
     */
    @Test
    public void testBlackChildren() {
        testBlackChildren(redBlackTree.getRoot());
    }

    private <K, V> void testBlackChildren(Node<K, V> currentNode) {
        Node<K, V> leftChild = currentNode.getLeftChild();
        if (leftChild != null) {
            if (currentNode.isRed()) {
                assertFalse(leftChild.isRed());
            }
            testBlackChildren(leftChild);
        }

        Node<K, V> rightChild = currentNode.getRightChild();
        if (rightChild != null) {
            if (currentNode.isRed()) {
                assertFalse(rightChild.isRed());
            }
            testBlackChildren(rightChild);
        }
    }

    @Test
    public void testListConstructor() {
        ArrayList<Integer> keyList = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            keyList.add(i, i);
        }
        RedBlackTree<Integer, Integer> tree = new RedBlackTree<>(keyList, keyList);
        List<Integer> result = tree.inorderValues();
        for (int i = 0; i < 20; i++) {
            assertEquals(i, (int) result.get(i));
        }
    }


    /**
     * Every path from a given node to any of its descendant leaves contains the same number of black nodes
     */
    @Test
    public void testBlackNodeNumber() {
        assertEquals(countLeftChildren(redBlackTree.getRoot(), 0), countRightChildren(redBlackTree.getRoot(), 0));
    }

    private <K, V> int countLeftChildren(Node<K, V> node, int currentBlackNumber) {
        currentBlackNumber = node.isRed() ? currentBlackNumber : currentBlackNumber + 1;
        return node.getLeftChild() == null ? currentBlackNumber : countLeftChildren(node.getLeftChild(), currentBlackNumber);
    }

    private <K, V> int countRightChildren(Node<K, V> node, int currentBlackNumber) {
        currentBlackNumber = node.isRed() ? currentBlackNumber : currentBlackNumber + 1;
        return node.getRightChild() == null ? currentBlackNumber : countRightChildren(node.getRightChild(), currentBlackNumber);
    }

}
