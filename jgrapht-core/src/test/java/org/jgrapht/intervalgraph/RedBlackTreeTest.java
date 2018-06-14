package org.jgrapht.intervalgraph;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test important properties of red-black tree
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
