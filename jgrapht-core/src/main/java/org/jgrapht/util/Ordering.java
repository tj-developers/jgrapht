package org.jgrapht.util;

import java.lang.reflect.Array;
import java.util.*;

public class Ordering<E> implements Comparator<E>, Iterable<E> {
    List<E> list;
    HashMap<E, Integer> positionOf;

    public Ordering(List<E> list) {
        this.list = new ArrayList<>(list);
        positionOf = new HashMap<>(list.size());

        int position = 0;
        for (E element: this.list) {
            positionOf.put(element, position++);
        }
    }

    public Ordering(Collection<E> collection) {
        this(new ArrayList<>(collection));
    }

    public Ordering(HashMap<E, Integer> positionOf) {
        this.positionOf = new HashMap<>(positionOf);
        list = new ArrayList<>(positionOf.size());
        for (int i = 0; i < positionOf.size(); i++) {
            list.add(null);
        }

        for (E element: positionOf.keySet()) {
            list.set(positionOf.get(element), element);
        }
    }

    public int getPositionOf(E element) {
        Integer result = positionOf.get(element);
        if (result == null) {
            throw new IllegalArgumentException();
        }
        return result;
    }

    public List<E> getOrderedList() {
        return new ArrayList<>(list);
    }

    public E getElementAt(int position) {
        return list.get(position);
    }

    public int size() {
        return list.size();
    }

    public boolean contains(E element) {
        return positionOf.containsKey(element);
    }

    @Override
    public int compare(E o1, E o2) {
        Integer position1 = positionOf.get(o1);
        Integer position2 = positionOf.get(o2);

        if (position1 == null || position2 == null) {
            throw new IllegalArgumentException();
        }

        return position1 - position2;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Iterator<E> it = list.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public E next() {
                return it.next();
            }
        };
    }
}
