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
 * @since March 2018
 */
public class LexBreadthFirstIterator<V, E>
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
     * Lookup tables for a static vertex numbering used to store the vertices.
     */
    private Map<Integer, V> vertexNumbering = new HashMap<Integer, V>();
    private Map<V, Integer> inverseVertexNumbering = new HashMap<V, Integer>();

    /**
     * Lookup tables for the sorted neighborhoods according to the imposed tiebreaking orders.
     */
    private Map<V, List<V>> sortedNeighbors = null;
    private Map<V, List<V>> sortedNeighborsB = null;

    /**
     * Creates new lexicographical breadth-first iterator for {@code graph}.
     *
     * @param graph the graph to be iterated.
     */
    public LexBreadthFirstIterator(Graph<V, E> graph)
    {
        super(graph);
        GraphTests.requireUndirected(graph);
        labelVertices(graph.vertexSet());
        bucketList = new BucketList(graph.vertexSet());

        // Precompute sorted neighborhoods
        this.sortedNeighbors = computeSortedNeighborhoods(graph, vertexNumbering);
    }

    /**
     * Creates new lexicographical breadth-first iterator with a static priority list for
     * {@code graph}.
     *
     * @param graph The graph to be iterated.
     * @param priority A mapping containing the priorities of the vertices. The priority mapping
     *        must be a permutation of $\{0, ..., n - 1\}$ where $n$ is the number of vertices of
     *        the graph.
     */
    public LexBreadthFirstIterator(Graph<V, E> graph, HashMap<V, Integer> priority)
    {
        super(graph);
        GraphTests.requireUndirected(graph);

        Set<V> copyOfSet = new HashSet<>(graph.vertexSet());
        labelVertices(graph.vertexSet());
        bucketList = new BucketList(copyOfSet, priority);

        // Invert priorities
        HashMap<Integer, V> inversePriority = new HashMap<Integer, V>();

        for (V vertex : graph.vertexSet()) {
            inversePriority.put(priority.get(vertex), vertex);
        }

        // Precompute sorted neighborhoods
        this.sortedNeighbors = computeSortedNeighborhoods(graph, inversePriority);
    }

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
    public LexBreadthFirstIterator(
        Graph<V, E> graph, HashMap<V, Integer> priorityA, HashMap<V, Integer> priorityB,
        HashMap<V, Integer> neighborIndexA, HashMap<V, Integer> neighborIndexB,
        HashMap<V, Set<V>> aSets, HashMap<V, Set<V>> bSets)
    {
        super(graph);
        GraphTests.requireUndirected(graph);

        Set<V> copyOfSet = new HashSet<>(graph.vertexSet());
        labelVertices(graph.vertexSet());
        bucketList = new BucketList(
            copyOfSet, priorityA, priorityB, neighborIndexA, neighborIndexB, aSets, bSets);

        // Invert priorities
        HashMap<Integer, V> inversePriorityA = new HashMap<Integer, V>();
        HashMap<Integer, V> inversePriorityB = new HashMap<Integer, V>();

        for (V vertex : graph.vertexSet()) {
            inversePriorityA.put(priorityA.get(vertex), vertex);
            inversePriorityB.put(priorityB.get(vertex), vertex);
        }

        // Precompute sorted neighborhoods
        this.sortedNeighbors = computeSortedNeighborhoods(graph, inversePriorityA);
        this.sortedNeighborsB = computeSortedNeighborhoods(graph, inversePriorityB);
    }

    /**
     * Helper function to compute the neighborhoods sorted by the vertex priority for efficiency.
     * Must be called before using the iterator. Uses linear, i.e. O(|V| + |E|) time.
     * 
     * @param graph The graph we are to iterate over
     * @param inversePriority A mapping from the priorities to the vertex set. It is assumed that
     *        the priorities are a permutation of $\{0, ..., n - 1\}$.
     * @return A map from the vertices to lists containing their neighborhoods (sorted by the vertex
     *         priorities)
     */
    private HashMap<V, List<V>> computeSortedNeighborhoods(
        Graph<V, E> graph, Map<Integer, V> inversePriority)
    {
        HashMap<V, List<V>> neighborhoodMap = new HashMap<V, List<V>>();

        for (int priority = graph.vertexSet().size() - 1; priority >= 0; priority--) {
            // get vertex with priority
            V vertex = inversePriority.get(priority);

            // if needed, initialize the neighbor list of this vertex
            if (!neighborhoodMap.containsKey(vertex)) {
                neighborhoodMap.put(vertex, new LinkedList<V>());
            }

            // add vertex to the neighbor lists of its neighbors
            for (V neighbor : Graphs.neighborSetOf(graph, vertex)) {
                if (neighborhoodMap.containsKey(neighbor)) {
                    neighborhoodMap.get(neighbor).add(vertex);
                } else {
                    List<V> neighborList = new LinkedList<V>();
                    neighborList.add(vertex);

                    neighborhoodMap.put(neighbor, neighborList);
                }
            }
        }

        return neighborhoodMap;
    }

    /**
     * Helper method to compute some fixed numbering of the vertices. Takes linear, i.e. O(|V|)
     * time.
     * 
     * @param vertices The vertex set of the graph we are to iterate over.
     */
    private void labelVertices(Set<V> vertices)
    {
        int label = 0;
        for (V vertex : vertices) {
            vertexNumbering.put(label, vertex);
            inverseVertexNumbering.put(vertex, label);
            label++;
        }
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
         * LBFS(+,*) static parameters
         */
        private HashMap<V, Integer> neighborIndexA = null;
        private HashMap<V, Integer> neighborIndexB = null;

        private HashMap<V, Set<V>> aSets = null;
        private HashMap<V, Set<V>> bSets = null;

        private HashMap<V, Integer> priorityA = null;
        private HashMap<V, Integer> priorityB = null;

        /**
         * Creates a {@code BucketList} with a single bucket and all specified {@code vertices} in
         * it.
         *
         * @param vertices The vertices of the graph, that should be stored in the {@code head}
         *        bucket.
         */
        BucketList(Collection<V> vertices)
        {
            head = new Bucket(vertices, null); // We do not use explicit tiebraking, thus the
                                               // priority map is nulled

            bucketMap = new HashMap<>(vertices.size());
            for (V vertex : vertices) {
                bucketMap.put(vertex, head);
            }
        }

        /**
         * Creates a {@code BucketList} with a single bucket and all specified {@code vertices} in
         * it.
         *
         * @param vertices The vertices of the graph, that should be stored in the {@code head}
         *        bucket.
         * @param priority A mapping which defines a priority for tiebreaking. The priorities must
         *        be normalized to be $\{0, ..., n - 1\}$ where $n$ is the number of vertices.
         */
        BucketList(Collection<V> vertices, HashMap<V, Integer> priority)
        {
            this.priorityA = priority;
            head = new Bucket(vertices, priority);

            bucketMap = new HashMap<>(vertices.size());
            for (V vertex : vertices) {
                bucketMap.put(vertex, head);
            }
        }

        /**
         * The constructor used for the LBFS* variant (see {@link #LexBreadthFirstIterator(Graph<V,
         * E>, HashMap<V, Integer>, HashMap<V, Integer>, HashMap<V, Integer>, HashMap<V, Integer>,
         * HashMap<V, Set<V>>, HashMap<V, Set<V>>) the paper linked above}). Creates a
         * {@code BucketList} with a single bucket and all specified {@code vertices} in it and sets
         * the corresponding parameters.
         * 
         * @param vertices The vertices of the graph, that should be stored in the {@code head}
         *        bucket.
         * @param priorityA A mapping containing the A priorities of the vertices. The priority
         *        mapping must be a permutation of $\{0, ..., n - 1\}$ where $n$ is the number of
         *        vertices of the graph.
         * @param priorityB A mapping containing the B priorities of the vertices. The priority
         *        mapping must be a permutation of $\{0, ..., n - 1\}$ where $n$ is the number of
         *        vertices of the graph.
         * @param neighborIndexA The A neighboring list.
         * @param neighborIndexB The B neighboring list.
         * @param aSets The A sets.
         * @param bSets The B sets.
         */
        BucketList(
            Collection<V> vertices, HashMap<V, Integer> priorityA, HashMap<V, Integer> priorityB,
            HashMap<V, Integer> neighborIndexA, HashMap<V, Integer> neighborIndexB,
            HashMap<V, Set<V>> aSets, HashMap<V, Set<V>> bSets)
        {
            this.neighborIndexA = neighborIndexA;
            this.neighborIndexB = neighborIndexB;
            this.aSets = aSets;
            this.bSets = bSets;
            this.priorityA = priorityA;
            this.priorityB = priorityB;

            head = new Bucket(vertices, priorityA, priorityB);

            bucketMap = new HashMap<>(vertices.size());
            for (V vertex : vertices) {
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
            if (bucketMap.size() > 0) {
                V res;

                // Poll the vertex according to the tiebreaking rules
                if (neighborIndexA == null) {
                    res = head.poll(false);
                } else {
                    res = head.poll(true);
                }

                bucketMap.remove(res);
                if (head.isEmpty()) {
                    head = head.next;
                    if (head != null) {
                        head.prev = null;
                    }
                }
                return res;
            } else {
                return null;
            }
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
            Map<Bucket, List<V>> intersectionLists = new HashMap<Bucket, List<V>>();
            Map<Bucket, List<V>> intersectionListsB = null;

            List<V> sortedNeighborsOfVertex = sortedNeighbors.get(vertex);
            List<V> sortedNeighborsOfVertexB = null;

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
                    intersectionLists.put(b, new LinkedList<V>());
                }

                // Add neighbor to said intersection list
                intersectionLists.get(b).add(neighbor);
            }

            if (sortedNeighborsB != null) {
                sortedNeighborsOfVertexB = sortedNeighborsB.get(vertex);
                intersectionListsB = new HashMap<Bucket, List<V>>();

                for (V neighbor : sortedNeighborsOfVertexB) {
                    // If this vertex was already traversed (and was thus deleted from the
                    // bucketMap), do not consider it.
                    if (!bucketMap.containsKey(neighbor)) {
                        continue;
                    }

                    Bucket b = bucketMap.get(neighbor);

                    if (!intersectionListsB.containsKey(b)) {
                        intersectionListsB.put(b, new LinkedList<V>());
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
                for (Integer i : newBucket.vertices) {
                    V updatedVertex = vertexNumbering.get(i);
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
            private SubSplitQueue vertices;
            /**
             * Set of vertices currently stored in this bucket (sorted by other order).
             */
            private SubSplitQueue verticesB = null;

            /**
             * Creates a new bucket with all {@code vertices} stored in it.
             * 
             * @param vertices A SplitQueue (sorted to priority A) holding the vertices to store in
             *        this bucket.
             * @param verticesB A SplitQueue (sorted to priority B) holding the vertices to store in
             *        this bucket.
             */
            Bucket(SubSplitQueue vertices, SubSplitQueue verticesB)
            {
                this.vertices = vertices;
                this.verticesB = verticesB;
            }

            // LBFS+ variant

            /**
             * Creates a new bucket with all {@code vertices} stored in it.
             *
             * @param vertices Vertices to store in this bucket.
             * @param priority The priorities of the vertices for tiebreaking (this is needed for
             *        LBFS+). If this parameter is nulled, some static ordering is used.
             */
            Bucket(Collection<V> vertices, HashMap<V, Integer> priority)
                throws IllegalArgumentException
            {
                int[] vertexIndexArray = new int[vertices.size()];

                if (priority == null) {
                    // No priority is specified, so we use the static ordering that we computed
                    for (V vertex : vertices) {
                        int vertexIndex = inverseVertexNumbering.get(vertex);
                        vertexIndexArray[vertexIndex] = vertexIndex;
                    }
                } else {
                    // Compute the indices of the vertices and order them according to the priority
                    // (descending)
                    for (V vertex : vertices) {
                        int vertexPriority = priority.get(vertex);

                        if (vertexPriority < 0 || vertexPriority > vertices.size() - 1) {
                            throw new IllegalArgumentException(
                                "The priorities must be a permutation of {0, ..., n - 1}");
                        }

                        vertexIndexArray[priority.get(vertex)] = inverseVertexNumbering.get(vertex);
                    }
                }

                this.vertices =
                    SubSplitQueue.subSplitQueueFactory(graph.vertexSet().size(), vertexIndexArray);
            }

            // LBFS* variant

            /**
             * Creates a new bucket with all {@code vertices} stored in it. If no (or only one for
             * LBFS+) priority is needed, please use {@link Bucket(Collection<V>, HashMap<V,
             * Integer>)} instead.
             * 
             * @param vertices Vertices to store in this bucket.
             * @param priorityA The A-priorities of the vertices for tiebreaking (this is needed for
             *        LBFS*).
             * @param priorityB The B-priorities of the vertices for tiebreaking (this is needed for
             *        LBFS*).
             */
            Bucket(
                Collection<V> vertices, HashMap<V, Integer> priorityA,
                HashMap<V, Integer> priorityB)
                throws IllegalArgumentException
            {
                int[] vertexIndexArrayA = new int[vertices.size()];
                int[] vertexIndexArrayB = new int[vertices.size()];

                // Compute the indices of the vertices and order them according to the priorities
                // (descending)
                for (V vertex : vertices) {
                    int vertexPriorityA = priorityA.get(vertex);
                    int vertexPriorityB = priorityB.get(vertex);

                    if (vertexPriorityA < 0 || vertexPriorityA > vertices.size() - 1) {
                        throw new IllegalArgumentException(
                            "The A-priorities must be a permutation of {0, ..., n - 1}");
                    } else if (vertexPriorityB < 0 || vertexPriorityB > vertices.size() - 1) {
                        throw new IllegalArgumentException(
                            "The B-priorities must be a permutation of {0, ..., n - 1}");
                    }

                    vertexIndexArrayA[priorityA.get(vertex)] = inverseVertexNumbering.get(vertex);
                    vertexIndexArrayB[priorityB.get(vertex)] = inverseVertexNumbering.get(vertex);
                }

                this.vertices =
                    SubSplitQueue.subSplitQueueFactory(graph.vertexSet().size(), vertexIndexArrayA);
                this.verticesB =
                    SubSplitQueue.subSplitQueueFactory(graph.vertexSet().size(), vertexIndexArrayB);
            }

            /**
             * Splits this bucket $B$ along the passed sorted list $S$ of vertices into $B \cap S$
             * and $B \setminus S$.
             * 
             * @param splitVertices The vertices to be removed from this bucket, sorted according to
             *        the (A)-priority used.
             * @param splitVertices The vertices to be removed from this bucket, sorted according to
             *        the (B)-priority used.
             * @return A new bucket containing the vertices that were removed from this bucket.
             */
            Bucket split(List<V> splitVertices, List<V> splitVerticesB)
            {
                int[] vertexArray = new int[splitVertices.size()];

                // Build sorted arrays from the lists
                int i = 0;
                for (V vertex : splitVertices) {
                    vertexArray[i] = inverseVertexNumbering.get(vertex);
                }

                SubSplitQueue newVertices = vertices.split(vertexArray);

                if (splitVerticesB != null) {
                    int[] vertexArrayB = new int[splitVerticesB.size()];

                    for (V vertex : splitVerticesB) {
                        vertexArrayB[i] = inverseVertexNumbering.get(vertex);
                    }

                    SubSplitQueue newVerticesB = verticesB.split(vertexArrayB);
                    return new Bucket(newVertices, newVerticesB);
                }

                return new Bucket(newVertices, null);
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
                } else {
                    if (!useStarTiebreaking) {
                        return vertexNumbering.get(vertices.poll());
                    } else {
                        int idxAlpha = vertices.peek();
                        int idxBeta = verticesB.peek();

                        V alpha = vertexNumbering.get(idxAlpha);
                        V beta = vertexNumbering.get(idxBeta);

                        if (neighborIndexA.get(alpha) > priorityA.get(alpha)) {
                            vertices.remove(idxBeta);
                            return vertexNumbering.get(verticesB.poll()); // return Beta
                        } else if (neighborIndexB.get(beta) > priorityB.get(beta)) {
                            verticesB.remove(idxAlpha);
                            return vertexNumbering.get(vertices.poll()); // return Alpha
                        } else if (bSets.get(beta).isEmpty() || !aSets.get(alpha).isEmpty()) {
                            vertices.remove(idxBeta);
                            return vertexNumbering.get(verticesB.poll()); // return Beta
                        } else if (Objects.equals(
                            neighborIndexA.get(bSets.get(beta).iterator().next()),
                            priorityA.get(alpha)))
                        {
                            vertices.remove(idxBeta);
                            return vertexNumbering.get(verticesB.poll()); // return Beta
                        } else {
                            verticesB.remove(idxAlpha);
                            return vertexNumbering.get(vertices.poll()); // return Alpha
                        }
                    }
                }
            }

            /**
             * Checks whether this bucket is empty.
             *
             * @return <tt>true</tt> if this bucket doesn't contain any elements, otherwise false.
             */
            boolean isEmpty()
            {
                return vertices.getSize() == 0;
            }
        }
    }
}
