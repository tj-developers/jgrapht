package org.jgrapht.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A collection of SplitQueues. Each are disjoint. Elements cannot be added or removed (except with
 * poll).
 *
 */
class SuperSplitQueue
{
    /**
     * containedIn[i] == k IFF i is contained in queue k init with 0
     */
    final private int[] containedIn;

    /**
     * previous[i] = j IFF i and j are in the same queue and j is the direct predecessor of i or -1
     * if it is not contained in any queue or i is the first element in a queue
     */
    final private int[] previous;

    /**
     * next[i] = j IFF IFF i and j are in the same queue and j is the direct successor of i or -1 if
     * i is not contained in the queue or i is the last element in a queue
     */
    final private int[] next;

    final private ArrayList<Integer> firstOfQ;

    final private ArrayList<Integer> lastOfQ;

    final private ArrayList<Integer> sizeOfQ;

    final private int universeSize;

    private int amountQueues;

    final private ArrayList<SubSplitQueue> queueByIndex;

    static SubSplitQueue instantiate(int universeSize)
    {
        return (new SuperSplitQueue(universeSize, true)).queueByIndex.get(0);
    }

    static SubSplitQueue instantiate(int universeSize, int[] sortedElements)
    {
        return new SuperSplitQueue(universeSize, sortedElements).queueByIndex.get(0);
    }

    /**
     * Returns a new empty SuperSplitQueue of size universeSize with one SubSplitQueue
     * 
     * @param universeSize
     */
    private SuperSplitQueue(int universeSize)
    {
        this.universeSize = universeSize;

        containedIn = new int[universeSize];
        previous = new int[universeSize];
        next = new int[universeSize];

        queueByIndex = new ArrayList<>();

        firstOfQ = new ArrayList<>();
        lastOfQ = new ArrayList<>();
        sizeOfQ = new ArrayList<>();

        // initialize arrays with -1
        for (int i = 0; i < universeSize; i++) {
            previous[i] = -1;
            next[i] = -1;
            containedIn[i] = -1;
        }

        addNewSubSplitQueue();
    }

    /**
     * Returns a full supersplitqueue
     * 
     * @param universeSize
     * @param isFull
     */
    private SuperSplitQueue(int universeSize, boolean isFull)
    {
        this(universeSize);

        if (!isFull) {
            return;
        }

        for (int i = 0; i < universeSize; i++) {
            addLast(i, 0);
        }
    }

    private SuperSplitQueue(int universeSize, int[] sortedElements)
    {
        this(universeSize);
        for (int i : sortedElements) {
            addLast(i, 0);
        }
    }

    private SubSplitQueue addNewSubSplitQueue()
    {
        SubSplitQueue result = new SubSplitQueue(amountQueues, this);

        firstOfQ.add(-1);
        lastOfQ.add(-1);
        sizeOfQ.add(0);
        queueByIndex.add(result);

        amountQueues++;

        return result;
    }

    int peek(int queueIndex)
    {
        if (isEmpty(queueIndex)) {
            throw new NoSuchElementException();
        }
        return firstOfQ.get(queueIndex);
    }

    int poll(int queueIndex)
    {
        int result = peek(queueIndex);
        remove(result, queueIndex);
        return result;
    }

    boolean isEmpty(int queueIndex)
    {
        return firstOfQ.get(queueIndex) == -1;
    }

    /**
     * Removes the given element and keeps the data structure consistent Runs in constant time
     * 
     * @param element the element to be removed
     */
    void remove(int element, int queueIndex)
    {
        inputChecker(element, queueIndex);
        if (containedIn[element] != queueIndex) {
            throw new NoSuchElementException("Element " + element + " not in specified queue");
        }
        // for h <-> element <-> j, where h = previous[element] and j = next[element]
        // we want h <-> j

        // set h -> j
        if (previous[element] != -1) { // nothing to update if it is the first element
            next[previous[element]] = next[element];
        } else if (firstOfQ.get(queueIndex) == element) {
            firstOfQ.set(queueIndex, next[element]);
        } else {
            throw new RuntimeException();
        }

        // set h <- j
        if (next[element] != -1) { // nothing to update if it is the last element
            previous[next[element]] = previous[element];
        } else if (lastOfQ.get(queueIndex) == element) {
            lastOfQ.set(queueIndex, previous[element]);
        }

        // reset element's entries
        containedIn[element] = -1;
        previous[element] = -1;
        next[element] = -1;

        sizeOfQ.set(queueIndex, sizeOfQ.get(queueIndex) - 1);
    }

    /**
     * Splits this queue in two: Elements in splitters are removed and transferred to a new queue,
     * the others stay. Run time:
     * 
     * @param splitters HAVE TO BE SORTED
     * @return the index of the new queue
     */
    SubSplitQueue split(int[] splitters, int queue)
    {
        SubSplitQueue newQueue = addNewSubSplitQueue();

        for (int i : splitters) {
            if (containedIn[i] == queue) {
                moveElementTo(i, queue, newQueue.getOwnIndex());
            }
        }

        return newQueue;
    }

    private void addLast(int element, int queueIndex)
    {
        inputChecker(element, queueIndex);
        if (containedIn[element] != -1) {
            throw new IllegalArgumentException();
        }

        int last = lastOfQ.get(queueIndex);
        int first = firstOfQ.get(queueIndex);

        containedIn[element] = queueIndex;
        // adapt "pointers, last <-> i
        previous[element] = last;
        if (last != -1) {
            next[last] = element;
        }
        last = element;

        if (first == -1) {
            first = element;
        }

        lastOfQ.set(queueIndex, last);
        firstOfQ.set(queueIndex, first);
        sizeOfQ.set(queueIndex, sizeOfQ.get(queueIndex) + 1);
    }

    private void moveElementTo(int element, int oldQueue, int newQueue)
    {
        remove(element, oldQueue);
        addLast(element, newQueue);
    }

    boolean contains(int element, int queueIndex)
    {
        if (element < 0 || element >= universeSize) {
            return false;
        }
        return containedIn[element] == queueIndex;
    }

    private void inputChecker(int element, int queueIndex)
    {
        if (element < 0 || element >= universeSize) {
            throw new IllegalArgumentException("Element not suitable for this queue in general");
        }
        if (queueIndex < 0 || queueIndex >= amountQueues) {
            throw new IllegalArgumentException("Queue does not exist");
        }
    }

    Iterator<Integer> iterator(int ownIndex)
    {
        return new Iterator<Integer>()
        {
            private int currentIndex = firstOfQ.get(ownIndex);
            private int previous = -1;

            @Override
            public boolean hasNext()
            {
                return currentIndex != -1;
            }

            @Override
            public Integer next()
            {
                if (currentIndex == -1) {
                    throw new NoSuchElementException();
                }

                previous = currentIndex;
                currentIndex = next[currentIndex];
                return previous;
            }

            @Override
            public void remove()
            {
                SuperSplitQueue.this.remove(previous, ownIndex);
            }
        };
    }

    int[] asArray(int queueIndex)
    {
        int currentIndex = firstOfQ.get(queueIndex);
        int[] result = new int[getSize(queueIndex)];
        for (int i = 0; i < result.length; i++) {
            result[i] = currentIndex;
            currentIndex = next[currentIndex];
        }

        return result;
    }

    int getSize(int queueIndex)
    {
        return sizeOfQ.get(queueIndex);
    }
}
