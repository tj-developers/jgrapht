/*
 * (C) Copyright 2018-2018, by Christoph Grüne, Daniel Mock and Contributors.
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

import java.io.Serializable;
import java.util.*;

/**
 * Implementation of a Red-Black-Tree
 *
 * @param <K> the key
 * @param <V> the value
 *
 * @author Daniel Mock (danielmock)
 * @author Christoph Grüne (christophgruene)
 * @since Apr 18, 2018
 */
public class RedBlackTree<K, V> implements BinarySearchTree<K, V>, Serializable {

    private static final long serialVersionUID = 1199228564356373435L;

    protected RedBlackTreeNode<K, V> root;

    protected Comparator<K> keyComparator;

    /**
     * Get the root of the red-black tree.
     *
     * @return the root of the red-black tree
     */
    public RedBlackTreeNode<K, V> getRoot() {
        return root;
    }

    /**
     * Constructs an empty tree.
     *
     * @param keyComparator the comparator for the keys
     */
    protected RedBlackTree(Comparator<K> keyComparator) {
        this.keyComparator = keyComparator;
    }

    /**
     * Constructs a tree with the given keys and values.
     * keys.get(i) is assigned to values.get(i)
     * Runs in linear time if list is sorted, otherwise naive insertion is performed.
     *
     * @param keys the keys for the future nodes
     * @param values the values of the future nodes corresponding to the given keys (i-th key belongs to i-th value)
     * @param keyComparator the comparator for the keys
     */
    protected RedBlackTree(ArrayList<K> keys, ArrayList<V> values, Comparator<K> keyComparator) {
        this.keyComparator = keyComparator;

        int length = keys.size();

        // exceptions
        if (length != values.size()) {
            throw new RuntimeException("Key and value list have to have same length");
        }
        if (keys.size() == 0) {
            return;
        }

        // check if list is sorted to use efficient insertion
        boolean isSorted = true;
        K current = keys.get(0);
        for (int i = 1; i < length; i++) {
            K next = keys.get(i);
            if (compareKey(current, next) > 0) {
                isSorted = false;
                break;
            }
            current = next;
        }

        // use optimized insert if input is sorted, otherwise trivial insertion
        if (isSorted) {
            root = sortedListToBST(keys, values, 0, length - 1);
        } else {
            for (int i = 0; i < length; i++) {
                this.insert(keys.get(i), values.get(i));
            }
        }
    }

    /**
     * Inserts a sorted list into the tree.
     *
     * @param keys the keys to insert
     * @param values the values to insert at the corresponding position to the keys
     * @param start the start index of the list to insert
     * @param end the end index of the list ot insert
     *
     * @return root node of sorted BST
     */
    private RedBlackTreeNode<K, V> sortedListToBST(ArrayList<K> keys, ArrayList<V> values, int start, int end) {
        if (start > end) {
            return null;
        }

        int mid = start + (end - start) / 2;
        RedBlackTreeNode<K, V> node = new RedBlackTreeNode<>(keys.get(mid), values.get(mid), false, 0); // colors and size have to be updated
        RedBlackTreeNode<K, V> left = sortedListToBST(keys, values, start, mid - 1);
        RedBlackTreeNode<K, V> right = sortedListToBST(keys, values, mid + 1, end);
        node.setLeftChild(left);
        node.setRightChild(right);

        // color all nodes black and only the leaves red
        if (left == null && right == null) {
            node.setRed();
            node.setSize(0);
        } else {
            // update sizes
            node.setSize(Math.max((left != null) ? left.getSize() : 0, right != null ? right.getSize() : 0) + 1);
        }

        return node;
    }


    /**
     * @inheritDoc
     */
    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }

        RedBlackTreeNode<K, V> searchResult = searchNode(key);
        return searchResult != null ? searchResult.getVal() : null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean contains(K key) {
        return get(key) != null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void insert(K key, V val) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }
        if (val == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }

        root = insert(root, key, val);
        root.setBlack();
    }

    /**
     * Inserts the node with value <code>val</code> and key <code>key</code> in the subtree with root current.
     * If <code>key</code> is present, then <code>val</code> overwrites the old value.
     *
     * @param current the root of the current subtree
     * @param key the key to insert
     * @param val the value to insert with key <code>key</code>
     * @return <code>current</code> as root node of the subtree
     */
    protected RedBlackTreeNode<K, V> insert(RedBlackTreeNode<K, V> current, K key, V val) {
        if (current == null){
            return new RedBlackTreeNode<>(key, val, true, 1);
        }

        if (key.equals(current.getKey())) {
            current.setVal(val);
        } else if (compareKey(key, current.getKey()) < 0) {
            current.setLeftChild(insert(current.getLeftChild(), key, val));
        } else {
            current.setRightChild(insert(current.getRightChild(), key, val));
        }

        // Fixup

        if (isRed(current.getRightChild()) && !isRed(current.getLeftChild())) {
            current = rotateLeft(current);
        }
        if (isRed(current.getLeftChild()) && isRed(current.getLeftChild().getLeftChild())) {
            current = rotateRight(current);
        }
        if (isRed(current.getLeftChild()) && isRed(current.getRightChild())) {
            changeColor(current);
        }
        current.setSize(size(current.getLeftChild()) + size(current.getRightChild()) + 1);

        return current;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean delete(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }
        if (!contains(key)) {
            return false;
        }

        if (!isRed(root.getLeftChild()) && !isRed(root.getRightChild())) {
            root.setRed();
        }

        root = delete(root, key);
        if (!isEmpty()) {
            root.setBlack();
        }
        return true;
    }

    private boolean isEmpty() {
        return root == null;
    }

    private boolean isRed(RedBlackTreeNode<K, V> node){
        return node != null && node.isRed();
    }

    /**
     * Deletes the node with key <code>key</code> in the subtree with root <code>current</code>,
     * balances the subtree of the deleted node and returns the new subtree.
     *
     * @param current the node to delete
     * @param key the key of the node to delete
     * @return the root node of the new subtree
     */
    protected RedBlackTreeNode<K, V> delete(RedBlackTreeNode<K, V> current, K key) {
        if (compareKey(key, current.getKey()) < 0) {
            if (!isRed(current.getLeftChild()) && !isRed(current.getLeftChild().getLeftChild())) {
                current = moveRedLeft(current);
            }
            current.setLeftChild(delete(current.getLeftChild(), key));
        } else {
            if (isRed(current.getLeftChild())) {
                current = rotateRight(current);
            }
            if (compareKey(key, current.getKey()) == 0 && current.getRightChild() == null) {
                return null;
            }
            if (!isRed(current.getRightChild()) && !isRed(current.getRightChild().getLeftChild())) {
                current = moveRedRight(current);
            }
            if (compareKey(key, current.getKey()) == 0) {
                RedBlackTreeNode<K, V> node = min(current.getRightChild());
                current.setKey(node.getKey());
                current.setVal(node.getVal());
                current.setRightChild(deleteMin(current.getRightChild()));
            } else {
                current.setRightChild(delete(current.getRightChild(), key));
            }
        }

        return balance(current);
    }

    /**
     * Balances the subtree with root <code>node</code>.
     *
     * @param node the root node of the subtree to balance
     * @return <code>root</code> as root of the subtree
     */
    protected RedBlackTreeNode<K, V> balance(RedBlackTreeNode<K, V> node) {
        if (isRed(node.getRightChild())) {
            node = rotateLeft(node);
        }
        if (isRed(node.getLeftChild()) && isRed(node.getLeftChild().getLeftChild())) {
            node = rotateRight(node);
        }
        if (isRed(node.getLeftChild()) && isRed(node.getRightChild())) {
            changeColor(node);
        }

        node.setSize(size(node.getLeftChild()) + size(node.getRightChild() ) + 1);
        return node;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void deleteMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("empty tree");
        }

        if (!isRed(root.getLeftChild()) && !isRed(root.getRightChild())) {
            root.setRed();
        }

        root = deleteMin(root);
        if (!isEmpty()) {
            root.setBlack();
        }
    }

    private RedBlackTreeNode<K, V> deleteMin(RedBlackTreeNode<K, V> node) {
        if (node.getLeftChild() == null) {
            return null;
        }

        if (!isRed(node.getLeftChild()) && !isRed(node.getLeftChild().getLeftChild())) {
            root = moveRedLeft(node);
        }

        node.setLeftChild(deleteMin(node.getLeftChild()));
        return balance(node);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void deleteMax() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        if (!isRed(root.getRightChild()) && !isRed(root.getRightChild())) {
            root.setRed();
        }

        root = deleteMax(root);
        if (!isEmpty()) {
            root.setBlack();
        }
    }

    private RedBlackTreeNode<K, V> deleteMax(RedBlackTreeNode<K, V> node) {
        if (isRed(node.getLeftChild())) {
            node = rotateRight(node);
        }
        if (node.getRightChild() == null) {
            return null;
        }
        if (!isRed(node.getRightChild()) && !isRed(node.getRightChild().getLeftChild())) {
            node = moveRedRight(node);
        }

        node.setRightChild(deleteMax(node.getRightChild()));
        return balance(node);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int height() {
        return height(root);
    }

    private int height(RedBlackTreeNode<K, V> node) {
        return node == null ? -1 : 1 + Math.max(height(node.getLeftChild()), height(node.getRightChild()));
    }

    /**
     * @inheritDoc
     */
    @Override
    public K min() {
        if (isEmpty()) {
            throw new NoSuchElementException("empty tree");
        }
        return min(root).getKey();
    }

    private RedBlackTreeNode<K, V> min(RedBlackTreeNode<K, V> node) {
        if (node.getLeftChild() == null) {
            return node;
        }
        return min(node.getLeftChild());
    }

    /**
     * @inheritDoc
     */
    @Override
    public K max() {
        if (isEmpty()) {
            throw new NoSuchElementException("empty tree");
        }
        return max(root.getRightChild()).getKey();
    }

    private RedBlackTreeNode<K,V> max(RedBlackTreeNode<K, V> node) {
        if (node.getRightChild() == null) {
            return node;
        }
        return max(node.getRightChild());
    }

    /**
     * @inheritDoc
     */
    @Override
    public K floor(K key) {
        return floorNode(key).getKey();
    }

    /**
     * The Search tree node associated to the given key.
     *
     * @param key the key of the tree node
     * @return the path (as stack) to the node containing the key, if the key is not contained, the path leads to the node that would be the parent of the searched key
     */
    Deque<RedBlackTreeNode<K, V>> searchNodeWithStack(K key) {
        Deque<RedBlackTreeNode<K, V>> stack = new ArrayDeque<>();
        RedBlackTreeNode<K, V> current = root;

        while (current != null) {
            stack.push(current);
            if (current.getKey().equals(key)) {
                break;
            } else if (compareKey(current.getKey(), key) < 0) {
                current = current.getRightChild();
            } else {
                current = current.getLeftChild();
            }
        }

        return stack;
    }

    private RedBlackTreeNode<K, V> floorNode(K key) {
        Deque<RedBlackTreeNode<K, V>> stack = searchNodeWithStack(key);

        // check whether the node with the key is found
        if (stack.peek().getKey().equals(key)) {
            return stack.peek();
        }

        // key is not in the tree, search for next smaller key

        // get last not null node
        RedBlackTreeNode<K, V> node = stack.pop();

        if (keyComparator.compare(node.getKey(), key) < 0) {
            return node;
        } else if (keyComparator.compare(node.getKey(), key) > 0) {
            RedBlackTreeNode<K, V> parent;
            while (node != root) {
                parent = stack.pop();
                if (node == parent.getLeftChild()) {
                    continue;
                }
                return parent;
            }
        }

        // root does not have a parent, key is not contained in tree
        throw new NoSuchElementException();
    }

    /**
     * @inheritDoc
     */
    @Override
    public K ceiling(K key) {
        return ceilingNode(key).getKey();
    }

    private RedBlackTreeNode<K, V> ceilingNode(K key) {
        Deque<RedBlackTreeNode<K, V>> stack = searchNodeWithStack(key);

        // check whether the node with the key is found
        if (stack.peek().getKey().equals(key)) {
            return stack.peek();
        }

        // key is not in the tree, search for next smaller key

        // get last not null node
        RedBlackTreeNode<K, V> node = stack.pop();

        if (keyComparator.compare(node.getKey(), key) < 0) {
            RedBlackTreeNode<K, V> parent;
            while (node != root) {
                parent = stack.pop();
                if (node == parent.getLeftChild()) {
                    continue;
                }
                return parent;
            }
        } else if (keyComparator.compare(node.getKey(), key) > 0) {
            return node;
        }

        // root does not have a parent, key is not contained in tree
        throw new NoSuchElementException();
    }

    /**
     * @inheritDoc
     */
    @Override
    public K select(int position) {
        RedBlackTreeNode<K, V> node = root;
        while (node != null) {
            int size = getSize(node.getLeftChild());
            if (size == position) {
                return node.getKey();
            } else if (size > position) {
                node = node.getLeftChild();
            } else {
                position -= size + 1;
                node = node.getRightChild();
            }
        }

        throw new IllegalArgumentException();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int orderingPosition(K key) {
        int position = 0;
        Deque<RedBlackTreeNode<K, V>> stack = searchNodeWithStack(key);

        if (stack.peek().getKey() != key) {
            throw new IllegalArgumentException();
        }

        while (!stack.isEmpty()) {
            RedBlackTreeNode<K, V> current = stack.pollLast();
            RedBlackTreeNode<K, V> next = stack.peekLast();
            if (current.getKey().equals(key)) {
                position += getSize(current.getLeftChild());
                break;
            }
            if (next == current.getRightChild()) {
                position += getSize(current.getLeftChild()) + 1;
            }
        }

        return position;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Iterable<K> keys() {
        return () -> keys(root);
    }

    private Iterator<K> keys(RedBlackTreeNode<K, V> start) {
        return new Iterator<K>() {
            Deque<RedBlackTreeNode<K, V>> stack;

            @Override
            public boolean hasNext() {
                if (stack == null) {
                    stack = new ArrayDeque<>(2 * (int) Math.ceil(Math.log(root.getSize() + 1)));
                    goToSuccessor(start);
                }
                return !stack.isEmpty();
            }

            @Override
            public K next() {
                RedBlackTreeNode<K, V> node = stack.pop();
                goToSuccessor(node.getRightChild());
                return node.getKey();
            }

            private void goToSuccessor(RedBlackTreeNode<K, V> node) {
                while (node != null) {
                    stack.push(node);
                    node = node.getLeftChild();
                }
            }
        };
    }

    /**
     * @inheritDoc
     */
    @Override
    public Iterable<K> keys(K min, K max) {
        return () -> new Iterator<K>() {
            Deque<RedBlackTreeNode<K, V>> stackLeft;
            Deque<RedBlackTreeNode<K, V>> stackRight;

            @Override
            public boolean hasNext() {
                if (stackLeft == null) {
                    RedBlackTreeNode<K, V> topNode = root;
                    stackLeft = searchNodeWithStack(min);
                    stackRight = searchNodeWithStack(max);

                    while (stackRight.peekLast() == stackLeft.peekLast()) {
                        topNode = stackLeft.peekLast();
                        stackLeft.pollLast();
                        stackRight.pollLast();
                        if (stackLeft.isEmpty() || stackRight.isEmpty()) {
                            break;
                        }
                    }

                    stackRight.addLast(topNode);
                    stackLeft.removeIf(node -> compareKey(node.getKey(), min) < 0);
                    stackRight.removeIf(node -> compareKey(node.getKey(), max) > 0);
                }
                return !stackLeft.isEmpty() || !stackRight.isEmpty();
            }

            @Override
            public K next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                RedBlackTreeNode<K, V> node;
                if (!stackLeft.isEmpty()) {
                    node = stackLeft.pop();
                    goToSuccessor(node.getRightChild());
                } else {
                    node = stackRight.pollLast();
                    if (!stackRight.isEmpty()) {
                        goToSuccessor(stackRight.peekLast().getLeftChild());
                    }
                }

                return node.getKey();
            }

            private void goToSuccessor(RedBlackTreeNode<K, V> node) {
                while (node != null) {
                    stackLeft.push(node);
                    node = node.getLeftChild();
                }
            }
        };
    }

    /**
     * @inheritDoc
     */
    @Override
    public int size(K min, K max) {
        return orderingPosition(ceiling(max)) - orderingPosition(floor(min)) + 1;
    }

    /**
     * @inheritDoc
     */
    public int size(RedBlackTreeNode<K,V> node) {
        if (node == null) {
            return 0;
        }

        return getSize(node);
    }

    /**
     * Rotate the subtree with root <code>node</code> left.
     *
     * @param node the root of the subtree to rotate left
     * @return the new root of the subtree, i.e the right child of <code>node</code>
     */
    protected RedBlackTreeNode<K, V> rotateLeft(RedBlackTreeNode<K, V> node) {
        RedBlackTreeNode<K, V> rightChild = node.getRightChild();
        node.setRightChild(rightChild.getLeftChild());
        rightChild.setLeftChild(node);
        rightChild.setRed(isRed(rightChild.getLeftChild()));
        rightChild.getLeftChild().setRed(true);
        rightChild.setSize(getSize(node));
        node.setSize(getSize(node.getLeftChild()) + getSize(node.getRightChild()) + 1);
        return rightChild;
    }

    private int getSize(RedBlackTreeNode<K, V> node) {
        return node != null ? node.getSize() : 0;
    }

    /**
     * Rotate the subtree with root <code>node</code> right.
     *
     * @param node the root of the subtree to rotate right
     * @return the new root of the subtree, i.e the left child of <code>node</code>
     */
    protected RedBlackTreeNode<K, V> rotateRight(RedBlackTreeNode<K, V> node) {
        RedBlackTreeNode<K, V> leftChild = node.getLeftChild();
        node.setLeftChild(leftChild.getRightChild());
        leftChild.setRightChild(node);
        leftChild.setRed(isRed(leftChild.getRightChild()));
        leftChild.getRightChild().setRed(true);
        leftChild.setSize(getSize(node));
        node.setSize(getSize(node.getLeftChild()) + getSize(node.getRightChild()) + 1);
        return leftChild;
    }

    private void changeColor(RedBlackTreeNode<K, V> node) {
        node.setRed(!isRed(node));
        node.getRightChild().setRed(!isRed(node.getRightChild()));
        node.getLeftChild().setRed(!isRed(node.getLeftChild()));
    }

    /**
     * Search tree node associated to the given key.
     *
     * @param key the key of the tree node
     * @return the tree node associated to the given key, null if the tree node doesn't exist
     */
    private RedBlackTreeNode<K, V> searchNode(K key) {
        RedBlackTreeNode<K, V> current = root;

        while (current != null) {
            if (current.getKey().equals(key)) {
                return current;
            } else if (compareKey(current.getKey(), key) < 0) {
                current = current.getRightChild();
            } else {
                current = current.getLeftChild();
            }
        }

        return null;
    }

    private RedBlackTreeNode<K, V> moveRedRight(RedBlackTreeNode<K, V> node) {
        changeColor(node);
        if (isRed(node.getLeftChild().getLeftChild())) {
            node = rotateRight(node);
            changeColor(node);
        }

        return node;
    }

    private RedBlackTreeNode<K,V> moveRedLeft(RedBlackTreeNode<K,V> node) {
        changeColor(node);
        if (isRed(node.getRightChild().getLeftChild())) {
            node.setRightChild(rotateRight(node.getRightChild()));
            node = rotateLeft(node);
            changeColor(node);
        }

        return node;
    }

    // returns the nodes inorder
    private List<RedBlackTreeNode<K, V>> inorder() {
        if (root == null) {
            return new ArrayList<>();
        }

        List<RedBlackTreeNode<K, V>> result = new ArrayList<>(getSize(root));
        inorder(root, result);
        return result;
    }

    private void inorder(RedBlackTreeNode<K, V> current, List<RedBlackTreeNode<K, V>> result) {
        if (current == null) {
            return;
        }

        inorder(current.getLeftChild(), result);
        result.add(current);
        inorder(current.getRightChild(), result);
    }

    /**
     * Returns the values as an inorder list representation.
     *
     * @return the values of the tree in an inorder list representation
     */
    public List<V> inorderValues(){
        List<RedBlackTreeNode<K, V>> inorder = inorder();
        List<V> result = new ArrayList<>(inorder.size());
        for (RedBlackTreeNode<K, V> node: inorder) {
            result.add(node.getVal());
        }
        return result;
    }

    private int compareKey(K key1, K key2) {
        return keyComparator.compare(key1, key2);
    }
}
