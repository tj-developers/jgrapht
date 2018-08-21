/*
 * (C) Copyright 2018-2018, by Timofey Chudakov and Contributors.
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
package org.jgrapht.traverse;

import org.jgrapht.*;
import org.jgrapht.util.*;

import java.util.*;

/**
 * A lexicographical breadth-first iterator for an undirected graph.
 * <p>
 * Every vertex has an implicit label (they aren't used explicitly in order to reduce time and
 * memory complexity). When some vertex is returned by this iterator, its index is the number of
 * vertices in this graph minus number of already returned vertices. For a given vertex v its label
 * is a concatenation of indices of already returned vertices, that were also its neighbours, with
 * some separator between them. For example, 7#4#3 is a valid vertex label.
 * <p>
 * Iterator chooses vertex with lexicographically largest label and returns it. It breaks ties
 * arbitrarily. For more information on lexicographical BFS see the following article: Corneil D.G.
 * (2004) <a href="https://pdfs.semanticscholar.org/d4b5/a492f781f23a30773841ec79c46d2ec2eb9c.pdf">
 * <i>Lexicographic Breadth First Search – A Survey</i></a>. In: Hromkovic J., Nagl M., Westfechtel
 * B. (eds) Graph-Theoretic Concepts in Computer Science. WG 2004. Lecture Notes in Computer
 * Science, vol 3353. Springer, Berlin, Heidelberg; and the following
 * paper:<a href="http://www.cse.iitd.ac.in/~naveen/courses/CSL851/uwaterloo.pdf"><i>CS 762:
 * Graph-theoretic algorithms. Lecture notes of a graduate course. University of Waterloo</i></a>.
 * <p>
 * For this iterator to work correctly the graph must not be modified during iteration. Currently
 * there are no means to ensure that, nor to fail-fast. The results of such modifications are
 * undefined.
 * <p>
 * Note: only vertex events are fired by this iterator.
 *
 * @param <V> the graph vertex type.
 * @param <E> the graph edge type.
 * @author Timofey Chudakov
 * @author Oliver Feith
 * @author Dennis Fischer
 * @author Daniel Mock
 * @since March 2018
 */
public class LbfsStar<V, E>
        extends
        AbstractGraphIterator<V, E>
{

    /**
     * Reference to the {@code BucketList} that contains unvisited vertices.
     */
    private BucketList bucketList;

    /**
     * Contains current vertex of the {@code graph}.
     */
    private V current;

    /**
     * Lookup tables for the sorted neighborhoods according to the imposed tiebreaking orders.
     */
    private Map<V, List<V>> sortedNeighbors;
    private Map<V, List<V>> sortedNeighborsB = null;

    private Ordering<V> priorityA;
    private Ordering<V> priorityB;


    private HashMap<V, Set<V>> aSets = null;
    private HashMap<V, Set<V>> bSets = null;

    /**
     * LBFS(+,*) static parameters
     */
    private Ordering<V> neighborIndexA = null;
    private Ordering<V> neighborIndexB = null;


    /**
     * Creates new lexicographical breadth-first iterator with a static priority list for
     * {@code graph}.
     *
     * This is a variant for the LBFS* algorithm used for interval graph detection, the terminology
     * used follows the original paper
     * (<a href= "https://webdocs.cs.ualberta.ca/~stewart/Pubs/IntervalSIAM.pdf">
     * https://webdocs.cs.ualberta.ca/~stewart/Pubs/IntervalSIAM.pdf</a>, <i>The LBFS Structure and
     * Recognition of Interval Graphs. SIAM J. Discrete Math.. 23. 1905-1953.
     * 10.1137/S0895480100373455.</i>) by Derek Corneil, Stephan Olariu and Lorna Stewart.
     *
     * @param graph the graph to be iterated.
     * @param priorityA A mapping containing the A priorities of the vertices. The priority mapping
     *        must be a permutation of $\{0, ..., n - 1\}$ where $n$ is the number of vertices of
     *        the graph.
     * @param priorityB A mapping containing the B priorities of the vertices. The priority mapping
     *        must be a permutation of $\{0, ..., n - 1\}$ where $n$ is the number of vertices of
     *        the graph.
     * @param neighborIndexA The A neighboring list
     * @param neighborIndexB The B neighboring list
     * @param aSets The A sets
     * @param bSets The B sets
     */
    public LbfsStar(
            Graph<V, E> graph, Ordering<V> priorityA, Ordering<V> priorityB,
            Ordering<V> neighborIndexA, Ordering<V> neighborIndexB,
            HashMap<V, Set<V>> aSets, HashMap<V, Set<V>> bSets)
    {
        super(graph);
        GraphTests.requireUndirected(graph);

        // check that orderings and vertex set are compatible
        boolean k = priorityA.size() == priorityB.size();
        k &= priorityA.size() == graph.vertexSet().size();
        for (V vertex: graph.vertexSet()) {
            k &= priorityA.contains(vertex);
            k &= priorityB.contains(vertex);
            if (!k) {
                throw new IllegalArgumentException();
            }
        }
        LinkedHashSet<V> verticesA = new LinkedHashSet<>(graph.vertexSet().size());
        LinkedHashSet<V> verticesB = new LinkedHashSet<>(graph.vertexSet().size());
        priorityA.forEach(verticesA::add);
        priorityB.forEach(verticesB::add);
        bucketList = new BucketList(verticesA, verticesB);

        this.priorityA = priorityA;
        this.priorityB = priorityB;
        this.neighborIndexA = neighborIndexA;
        this.neighborIndexB = neighborIndexB;
        this.aSets = aSets;
        this.bSets = bSets;

        // Precompute sorted neighborhoods
        this.sortedNeighbors = computeSortedNeighborhoods(priorityA);
        this.sortedNeighborsB = computeSortedNeighborhoods(priorityB);
    }

    public LbfsStar(Graph<V, E> graph, Ordering<V> priority) {
        super(graph);
        GraphTests.requireUndirected(graph);

        // check that orderings and vertex set are compatible
        boolean k = priorityA.size() == graph.vertexSet().size();
        for (V vertex: graph.vertexSet()) {
            k &= priorityA.contains(vertex);
            if (!k) {
                throw new IllegalArgumentException();
            }
        }

        LinkedHashSet<V> verticesA = new LinkedHashSet<>(graph.vertexSet().size());
        bucketList = new BucketList(verticesA, null);

        this.priorityA = priority;
        priorityA.forEach(verticesA::add);

        this.sortedNeighbors = computeSortedNeighborhoods(priorityA);
    }



    /**
     * Helper function to compute the neighborhoods sorted by the vertex priority for efficiency.
     * Must be called before using the iterator. Uses linear, i.e. O(|V| + |E|) time.
     *
     * @param inversePriority A mapping from the priorities to the vertex set. It is assumed that
     *        the priorities are a permutation of $\{0, ..., n - 1\}$.
     * @return A map from the vertices to lists containing their neighborhoods (sorted by the vertex
     *         priorities)
     */
    private HashMap<V, List<V>> computeSortedNeighborhoods(Ordering<V> inversePriority)
    {
        HashMap<V, List<V>> neighborhoodMap = new HashMap<>();

        for (int priority = graph.vertexSet().size() - 1; priority >= 0; priority--) {
            // get vertex with priority
            V vertex = inversePriority.getElementAt(priority);

            // if needed, initialize the neighbor list of this vertex
            if (!neighborhoodMap.containsKey(vertex)) {
                neighborhoodMap.put(vertex, new LinkedList<>());
            }

            // add vertex to the neighbor lists of its neighbors
            for (V neighbor : Graphs.neighborSetOf(graph, vertex)) {
                if (neighborhoodMap.containsKey(neighbor)) {
                    neighborhoodMap.get(neighbor).add(vertex);
                } else {
                    List<V> neighborList = new LinkedList<>();
                    neighborList.add(vertex);

                    neighborhoodMap.put(neighbor, neighborList);
                }
            }
        }

        return neighborhoodMap;
    }

    /**
     * Checks whether there exist unvisited vertices.
     *
     * @return true if there exist unvisited vertices.
     */
    @Override
    public boolean hasNext()
    {
        if (current != null) {
            return true;
        }
        current = advance();
        if (current != null && nListeners != 0) {
            fireVertexTraversed(createVertexTraversalEvent(current));
        }
        return current != null;
    }

    /**
     * Returns the next vertex in the ordering.
     *
     * @return the next vertex in the ordering.
     */
    @Override
    public V next()
    {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        V result = current;
        current = null;
        if (nListeners != 0) {
            fireVertexFinished(createVertexTraversalEvent(result));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always returns true since this iterator doesn't care about connected components.
     */
    @Override
    public boolean isCrossComponentTraversal()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Trying to disable the cross components nature of this iterator will result into throwing a
     * {@link IllegalArgumentException}.
     */
    @Override
    public void setCrossComponentTraversal(boolean crossComponentTraversal)
    {
        if (!crossComponentTraversal) {
            throw new IllegalArgumentException("Iterator is always cross-component");
        }
    }

    /**
     * Retrieves vertex from the {@code bucketList} and returns it.
     *
     * @return the vertex retrieved from the {@code bucketList}.
     */
    private V advance()
    {
        V vertex = bucketList.poll();
        if (vertex != null) {
            bucketList.updateBuckets(vertex);
        }
        return vertex;
    }

    /**
     * Data structure for performing lexicographical breadth-first search. Allows to add and
     * retrieve vertices from buckets, update their buckets after a new vertex has been added to the
     * LexBFS order. Labels aren't used explicitly, which results in time and space optimization.
     *
     * @author Timofey Chudakov
     * @since March 2018
     */
    class BucketList
    {
        /**
         * Bucket with the vertices that have lexicographically largest label.
         */
        private Bucket head;
        /**
         * Map for mapping vertices to buckets they are currently in. Is used for finding the bucket
         * of the vertex in constant time.
         */
        private Map<V, Bucket> bucketMap;

        /**
         * Creates a {@code BucketList} with a single bucket and all specified {@code vertices} in
         * it.
         * The Sets have to contain the same elements.
         *
         * @param verticesA The vertices sorted by the first ordering
         * @param verticesB The vertices sorted by the second ordering
         */
        BucketList(LinkedHashSet<V> verticesA, LinkedHashSet<V> verticesB)
        {
            head = new Bucket(verticesA, verticesB);

            bucketMap = new HashMap<>(verticesA.size());
            for (V vertex : verticesA) {
                bucketMap.put(vertex, head);
            }
        }


        /**
         * Checks whether there exists a bucket with the specified {@code vertex}.
         *
         * @param vertex The vertex whose presence in some {@code Bucket} in this {@code BucketList}
         *        is checked.
         * @return <tt>true</tt> if there exists a bucket with {@code vertex} in it, otherwise
         *         <tt>false</tt>.
         */
        boolean containsBucketWith(V vertex)
        {
            return bucketMap.containsKey(vertex);
        }

        /**
         * Retrieves element from the head bucket by invoking {@link Bucket#poll()} or null if this
         * {@code BucketList} is empty.
         * <p>
         * Removes the head bucket if it becomes empty after the operation.
         *
         * @return Vertex returned by {@link Bucket#poll()} invoked on head bucket or null if this
         *         {@code BucketList} is empty.
         */
        V poll()
        {
            if (bucketMap.size() <= 0) {
                return null;
            }

            // Poll the vertex according to the tiebreaking rules
            V res = head.poll(neighborIndexA != null);

            bucketMap.remove(res);
            if (head.isEmpty()) {
                head = head.next;
                if (head != null) {
                    head.prev = null;
                }
            }
            return res;
        }

        /**
         * Split every bucket $B$ into buckets $B \cap N(vertex)$ and $B \setminus N(vertex)$. For
         * every such {@code Bucket} B only one new {@code Bucket} B' is created. If some bucket B
         * becomes empty after this operation, it is removed from the data structure.
         *
         * @param vertex The vertex whose neighborhood is used for splitting.
         */
        void updateBuckets(V vertex)
        {
            HashSet<Bucket> bucketsToUpdate = new HashSet<>();
            Map<Bucket, List<V>> intersectionLists = new HashMap<>();
            Map<Bucket, List<V>> intersectionListsB = null;

            List<V> sortedNeighborsOfVertex = sortedNeighbors.get(vertex);
            List<V> sortedNeighborsOfVertexB;

            for (V neighbor : sortedNeighborsOfVertex) {
                // If this vertex was already traversed (and was thus deleted from the bucketMap),
                // do not consider it.
                if (!bucketMap.containsKey(neighbor)) {
                    continue;
                }

                Bucket b = bucketMap.get(neighbor);

                // If necessary, memorize that we need to update B and initialize a sorted list
                // which holds the intersection of B and N(vertex)
                if (!bucketsToUpdate.contains(b)) {
                    bucketsToUpdate.add(bucketMap.get(neighbor));
                    intersectionLists.put(b, new LinkedList<>());
                }

                // Add neighbor to said intersection list
                intersectionLists.get(b).add(neighbor);
            }

            if (sortedNeighborsB != null) {
                sortedNeighborsOfVertexB = sortedNeighborsB.get(vertex);
                intersectionListsB = new HashMap<>();

                for (V neighbor : sortedNeighborsOfVertexB) {
                    // If this vertex was already traversed (and was thus deleted from the
                    // bucketMap), do not consider it.
                    if (!bucketMap.containsKey(neighbor)) {
                        continue;
                    }

                    Bucket b = bucketMap.get(neighbor);

                    if (!intersectionListsB.containsKey(b)) {
                        intersectionListsB.put(b, new LinkedList<>());
                    }

                    // Add neighbor to intersection list B
                    intersectionListsB.get(b).add(neighbor);
                }
            }

            for (Bucket bucket : bucketsToUpdate) {
                Bucket newBucket;

                // Split the bucket...
                if (intersectionListsB == null) {
                    // We do not use a B-priority (i.e. we are doing LBFS/LBFS+), so we only split
                    // one queue -- the second one is nulled
                    newBucket = bucket.split(intersectionLists.get(bucket), null);
                } else {
                    // We are doing LBFS* and thus have to update both queues accordingly
                    newBucket =
                            bucket.split(intersectionLists.get(bucket), intersectionListsB.get(bucket));
                }

                // ... and insert the resulting new bucket before the old one
                newBucket.insertBefore(bucket);

                if (head == bucket) {
                    head = newBucket;
                }

                // Update the bucketMap with the changes made
                for (V updatedVertex : newBucket.vertices) {
                    bucketMap.put(updatedVertex, newBucket);
                }

                // If necessary, remove the bucket
                if (bucket.isEmpty()) {
                    bucket.removeSelf();
                }
            }
        }

        /**
         * Plays the role of the container of vertices. All vertices stored in a bucket have
         * identical label. Labels aren't used explicitly.
         * <p>
         * Encapsulates operations of addition and removal of vertices from the bucket and removal
         * of a bucket from the data structure.
         */
        private class Bucket
        {
            /**
             * Reference of the bucket with lexicographically smaller label.
             */
            private Bucket next;
            /**
             * Reference of the bucket with lexicographically larger label.
             */
            private Bucket prev;
            /**
             * Set of vertices currently stored in this bucket.
             */
            private LinkedHashSet<V> vertices;
            /**
             * Set of vertices currently stored in this bucket (sorted by other order).
             */
            private LinkedHashSet<V> verticesB;

            /**
             * Creates a new bucket with all {@code vertices} stored in it.
             *
             * @param vertices A SplitQueue (sorted to priority A) holding the vertices to store in
             *        this bucket.
             * @param verticesB A SplitQueue (sorted to priority B) holding the vertices to store in
             *        this bucket.
             */
            Bucket(LinkedHashSet<V> vertices, LinkedHashSet<V> verticesB)
            {
                this.vertices = vertices;
                this.verticesB = verticesB;
            }

            /**
             * Splits this bucket $B$ along the passed sorted list $S$ of vertices into $B \cap S$
             * and $B \setminus S$.
             *
             * @param splitVertices The vertices to be removed from this bucket, sorted according to
             *        the (A)-priority used.
             * @param splitVerticesB The vertices to be removed from this bucket, sorted according to
             *        the (B)-priority used.
             * @return A new bucket containing the vertices that were removed from this bucket.
             */
            Bucket split(List<V> splitVertices, List<V> splitVerticesB)
            {
                LinkedHashSet<V> newVertices = split(vertices, splitVertices);
                LinkedHashSet<V> newVerticesB = split(verticesB, splitVerticesB);
                return new Bucket(newVertices, newVerticesB);

            }

            /**
             * Splits the LinkedHashSet along the given vertices.
             * The returned LinkedHashSet is ordered by the ordering of the vertices.
             * @param vertices
             * @param splitVertices
             * @return
             */
            private LinkedHashSet<V> split(LinkedHashSet<V> vertices, List<V> splitVertices)
            {
                if (splitVertices == null) {
                    return null;
                }

                LinkedHashSet<V> newVertices = new LinkedHashSet<>();
                for (V vertex: splitVertices) {
                    if (vertices.remove(vertex)) {
                        newVertices.add(vertex);
                    }
                }
                return newVertices;
            }

            /**
             * Removes this bucket from the data structure.
             */
            void removeSelf()
            {
                if (next != null) {
                    next.prev = prev;
                }
                if (prev != null) {
                    prev.next = next;
                }
            }

            /**
             * Inserts this bucket in the data structure before the {@code bucket}.
             *
             * @param bucket The bucket that will be the in front of this bucket.
             */
            void insertBefore(Bucket bucket)
            {
                this.next = bucket;
                if (bucket != null) {
                    this.prev = bucket.prev;
                    if (bucket.prev != null) {
                        bucket.prev.next = this;
                    }
                    bucket.prev = this;
                } else {
                    this.prev = null;
                }
            }

            /**
             * Retrieves one vertex from this bucket (without special priorities).
             *
             * @param useStarTiebreaking Set to <tt>false</tt> if normal tiebreaking (according to
             *        static ordering) should be used and set to <tt>true</tt> if LBFS* tiebreaking
             *        should be used.
             * @return The vertex that was removed from this bucket, null if the bucket was empty.
             */
            V poll(boolean useStarTiebreaking)
            {
                if (vertices.isEmpty()) {
                    return null;
                }

                // When LBFS or LBFS+ is used
                if (!useStarTiebreaking) {
                    Iterator<V> it = vertices.iterator();
                    V alpha = it.next();
                    it.remove();

                    return alpha;
                }
                // For LBFS*

                // The first element of vertices and verticesB are peeked
                Iterator<V> it = vertices.iterator();
                Iterator<V> itB = verticesB.iterator();

                V alpha = it.next();
                V beta = itB.next();

                V result;

                // Choose the vertex according to Section 7
                if (neighborIndexA.getPositionOf(alpha) > priorityA.getPositionOf(alpha)) {
                    result = beta; // return Beta
                } else if (neighborIndexB.getPositionOf(beta) > priorityB.getPositionOf(beta)) {
                    result = alpha; // return Alpha
                } else if (bSets.get(beta).isEmpty() || !aSets.get(alpha).isEmpty()) {
                    result = beta; // return Beta
                } else if (Objects.equals(
                        neighborIndexA.getPositionOf(bSets.get(beta).iterator().next()),
                        priorityA.getPositionOf(alpha)))
                {
                    result = beta; // return Beta
                } else {
                    result = alpha; // return Alpha
                }
                // remove the chosen vertex
                vertices.remove(result);
                verticesB.remove(result);
                return result;
            }

            /**
             * Checks whether this bucket is empty.
             *
             * @return <tt>true</tt> if this bucket doesn't contain any elements, otherwise false.
             */
            boolean isEmpty()
            {
                return vertices.size() == 0;
            }
        }
    }
}
