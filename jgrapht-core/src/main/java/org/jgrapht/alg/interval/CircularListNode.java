package org.jgrapht.alg.interval;

/**
 * A node in a doubly linked circular list
 */
public class CircularListNode<E> {

    /**
     * The element of type E associated with this node
     */
    private E element;

    /**
     * The previous neighboring node
     */
    private CircularListNode<E> prev = null;

    /**
     * The next neighboring node
     */
    private CircularListNode<E> next = null;

    /**
     * Instantiate a new node for the list
     * <p>
     * Previous and next neighboring nodes are pointed to the current node by default
     *
     * @param element the element associated with this node
     */
    public CircularListNode(E element) {
        this.element = element;
        this.prev = this;
        this.next = this;
    }

    /**
     * Add a previous node for the current node
     *
     * @param element the element associated with the node to be added
     * @return the new previous node added
     */
    public CircularListNode<E> addPrev(E element) {
        CircularListNode<E> node = new CircularListNode<>(element);

        node.prev = prev;
        node.next = this;
        prev.next = node;
        this.prev = node;

        return node;
    }

    /**
     * Add a next node for the current node
     *
     * @param element the element associated with the node to be added
     * @return the new next node added
     */
    public CircularListNode<E> addNext(E element) {
        CircularListNode<E> node = new CircularListNode<>(element);

        node.prev = this;
        node.next = next;
        next.prev = node;
        this.next = node;

        return node;
    }

    /**
     * Get the previous neighboring node
     *
     * @return the previous neighboring node of the current node
     */
    public CircularListNode<E> prev() {
        return prev;
    }

    /**
     * Get the element associated with the current node
     *
     * @return the element associated with the current node
     */
    public E element() {
        return element;
    }

    /**
     * Get the next neighboring node
     *
     * @return the next neighboring node of the current node
     */
    public CircularListNode<E> next() {
        return next;
    }

    /**
     * Remove the current node from the list
     *
     * @return the current node removed from the list
     */
    public CircularListNode<E> remove() {
        prev.next = next;
        next.prev = prev;

        return this;
    }

}