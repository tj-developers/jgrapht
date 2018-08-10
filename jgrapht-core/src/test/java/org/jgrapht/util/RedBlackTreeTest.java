/*
 * (C) Copyright 2018-2018, by Jiong Fu, Daniel Mock and Contributors.
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

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Jiong Fu
 * @author Daniel Mock
 */
public class RedBlackTreeTest {

    private int TEST_SIZE = 10;
    private RedBlackTree<Integer, Integer> redBlackTree = new RedBlackTree<>(Integer::compare);
    private int[] keys = new int[]{13, 8, 17, 1, 11, 15, 25, 6, 22, 27};
    private int[] sortedKeys;

    @Before
    public void setUp() {
        sortedKeys = Arrays.copyOf(keys, TEST_SIZE);
        Arrays.sort(sortedKeys);

        for (int i: keys) {
            redBlackTree.insert(i, i);
        }

//        redBlackTree.insert(13, 13);
//        redBlackTree.insert(8, 8);
//        redBlackTree.insert(17, 17);
//        redBlackTree.insert(1, 1);
//        redBlackTree.insert(11, 11);
//        redBlackTree.insert(15, 15);
//        redBlackTree.insert(25, 25);
//        redBlackTree.insert(6, 6);
//        redBlackTree.insert(22, 22);
//        redBlackTree.insert(27, 27);
    }

    @Test
    public void orderingPosition() {
        int[] array = new int[TEST_SIZE];
        int[] expected = new int[TEST_SIZE];

        for (int i = 0; i < TEST_SIZE; i++) {
            array[i] = redBlackTree.orderingPosition(sortedKeys[i]);
            expected[i] = i;
        }
        assertArrayEquals(expected, array);
    }

    @Test
    public void orderingPosition1() {
        assertEquals(9,redBlackTree.orderingPosition(sortedKeys[9]));
    }


    @Test
    public void size() {
        for (int min = 0; min < TEST_SIZE; min++) {
            for (int max = min; max < TEST_SIZE; max++) {
                assertEquals("HELP" + redBlackTree.orderingPosition(sortedKeys[min]) + " " + redBlackTree.orderingPosition(sortedKeys[max]),
                        max - min + 1, redBlackTree.size(sortedKeys[min], sortedKeys[max]));
            }
        }
    }

    @Test
    public void searchNodeWithStack() {
        int[] array = new int[TEST_SIZE];

        for (int i = 0; i < TEST_SIZE; i++) {
            array[i] = redBlackTree.searchNodeWithStack(sortedKeys[i]).peekFirst().getKey();
        }
        assertArrayEquals(sortedKeys, array);
    }

    @Test
    public void ceil() {
        assertEquals(1, (int) redBlackTree.ceiling(1));
        assertEquals(1, (int) redBlackTree.ceiling(0));
        assertEquals(11, (int) redBlackTree.ceiling(9));
    }

    @Test
    public void select() {
        for (int i = 0; i < TEST_SIZE; i++) {
            assertEquals(sortedKeys[i], (int) redBlackTree.select(i));
        }
    }

    @Test
    public void iterator() {
        List<Integer> list = new ArrayList<>(TEST_SIZE);
        redBlackTree.keys().forEach(list::add);
        assertArrayEquals(sortedKeys, list.stream().mapToInt(Integer::intValue).toArray());

        list.clear();
        redBlackTree.keys(8,22).forEach(list::add);
        System.out.println(Arrays.toString(Arrays.copyOfRange(sortedKeys, 2, 8)));
        assertArrayEquals(Arrays.copyOfRange(sortedKeys, 2, 8), list.stream().mapToInt(Integer::intValue).toArray());
        for (int min = 0; min < TEST_SIZE; min++) {
            for (int max = min; max < TEST_SIZE; max++) {
                list.clear();
                redBlackTree.keys(sortedKeys[min], sortedKeys[max]).forEach(list::add);
                int[] a = Arrays.copyOfRange(sortedKeys, min, max+1);
                int[] b = list.stream().mapToInt(Integer::intValue).toArray();
                System.out.println(Arrays.toString(a));
                System.out.println(Arrays.toString(b));

                assertArrayEquals("" + sortedKeys[min] + "" + sortedKeys[max], a, b);
            }
        }
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

    private <K, V> void testBlackChildren(RedBlackTreeNode<K, V> currentNode) {
        RedBlackTreeNode<K, V> leftChild = currentNode.getLeftChild();
        if (leftChild != null) {
            if (currentNode.isRed()) {
                assertFalse(leftChild.isRed());
            }
            testBlackChildren(leftChild);
        }

        RedBlackTreeNode<K, V> rightChild = currentNode.getRightChild();
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
        RedBlackTree<Integer, Integer> tree = new RedBlackTree<>(keyList, keyList, Integer::compare);
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

    private <K, V> int countLeftChildren(RedBlackTreeNode<K, V> node, int currentBlackNumber) {
        currentBlackNumber = node.isRed() ? currentBlackNumber : currentBlackNumber + 1;
        return node.getLeftChild() == null ? currentBlackNumber : countLeftChildren(node.getLeftChild(), currentBlackNumber);
    }

    private <K, V> int countRightChildren(RedBlackTreeNode<K, V> node, int currentBlackNumber) {
        currentBlackNumber = node.isRed() ? currentBlackNumber : currentBlackNumber + 1;
        return node.getRightChild() == null ? currentBlackNumber : countRightChildren(node.getRightChild(), currentBlackNumber);
    }
}
