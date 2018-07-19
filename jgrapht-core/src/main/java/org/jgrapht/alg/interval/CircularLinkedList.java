package org.jgrapht.alg.interval;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * The doubly linked circular list
 *
 * @param <E> the type of the element stored in the list
 * @author Jiong Fu
 */
public class CircularLinkedList<E> extends LinkedList<E> {

    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListItr(0);
    }

    /**
     * This iterator iterates the linked list element by delegating the method call to the LinkedList class
     */
    private class ListItr implements ListIterator<E> {

        private int fromIndex = 0;
        private ListIterator<E> listIterator = null;

        ListItr(int fromIndex) {
            this.fromIndex = fromIndex;
            initInternalListItr(fromIndex);
        }

        /**
         * There is always a next element in the circular list
         *
         * @return true always
         */
        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public E next() {
            // if there exists no next element, instantiate a new list iterator and iterate forward again from the beginning
            if (!listIterator.hasNext()) {
                initInternalListItr(fromIndex);
            }
            return listIterator.next();
        }

        /**
         * There is always a previous element in the circular list
         *
         * @return true always
         */
        @Override
        public boolean hasPrevious() {
            return true;
        }

        @Override
        public E previous() {
            // if there exists no previous element, instantiate a new list iterator and iterate backward again from the end
            if (!listIterator.hasPrevious()) {
                initInternalListItr(size());
            }
            return listIterator.previous();
        }

        @Override
        public int nextIndex() {
            return listIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return listIterator.previousIndex();
        }

        @Override
        public void remove() {
            listIterator.remove();
        }

        @Override
        public void set(E o) {
            listIterator.set(o);
        }

        @Override
        public void add(E o) {
            listIterator.add(o);
        }

        /**
         * Initialize the internal list iterator by getting the list iterator from the LinkedList class
         *
         * @param index index of the first element to be returned from the list-iterator
         */
        private void initInternalListItr(int index) {
            listIterator = CircularLinkedList.super.listIterator(index);
        }
    }

}
