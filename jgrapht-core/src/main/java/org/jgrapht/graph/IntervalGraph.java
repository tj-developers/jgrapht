package org.jgrapht.graph;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.alg.intervalgraph.IntervalGraphRecognizer;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.specifics.IntervalSpecifics;
import org.jgrapht.util.interval.Interval;
import org.jgrapht.util.TypeUtil;

import java.io.Serializable;
import java.util.*;

/**
 * Implementation of an Interval Graph
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 * @param <VertexType> the internal vertex type
 * @param <T> The underlying type for intervals
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 18, 2018
 */
public class IntervalGraph<V extends IntervalVertexInterface<VertexType, T>, E, VertexType, T extends Comparable<T>> extends AbstractGraph<V, E> implements Graph<V, E>, Cloneable, Serializable {

    private static final long serialVersionUID = 7835287075273098344L;

    private static final String INTERVAL_GRAPH_ADD_EDGE = "Intervals of nodes define edges in interval graphs and cannot be modified manually";
    private static final String GRAPH_SPECIFICS_MUST_NOT_BE_NULL = "Graph specifics must not be null";

    private EdgeFactory<V, E> edgeFactory;
    private transient Set<V> unmodifiableVertexSet = null;

    private IntervalSpecifics<V, E, VertexType, T> specifics;
    private IntrusiveEdgesSpecifics<V, E> intrusiveEdgesSpecifics;

    private boolean directed;
    private boolean weighted;
    private boolean allowingMultipleEdges;
    private boolean allowingLoops;

    /**
     * Construct a new graph. The graph can either be directed or undirected, depending on the
     * specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     * @param weighted whether the graph is weighted, i.e. the edges support a weight attribute
     *
     * @throws NullPointerException if the specified edge factory is <code>
     * null</code>.
     */
    protected IntervalGraph(
            EdgeFactory<V, E> ef, boolean weighted)
    {
        Objects.requireNonNull(ef);

        this.edgeFactory = ef;
        this.allowingLoops = false;
        this.allowingMultipleEdges = false;
        this.directed = false;
        this.specifics =
                Objects.requireNonNull(createSpecifics(), GRAPH_SPECIFICS_MUST_NOT_BE_NULL);
        this.weighted = weighted;
        this.intrusiveEdgesSpecifics = Objects.requireNonNull(
                createIntrusiveEdgesSpecifics(weighted), GRAPH_SPECIFICS_MUST_NOT_BE_NULL);
    }

    /**
     * Construct a new graph with given <code>vertices</code>. The graph can either be directed or undirected, depending on the
     * specified edge factory.
     *
     * @param vertices initial vertices
     * @param ef the edge factory of the new graph.
     * @param weighted whether the graph is weighted, i.e. the edges support a weight attribute
     *
     * @throws NullPointerException if the specified edge factory is <code>
     * null</code>.
     */
    protected IntervalGraph(
            ArrayList<V> vertices, EdgeFactory<V, E> ef, boolean weighted)
    {

        Objects.requireNonNull(ef);

        boolean isSorted = true;
        V current = vertices.get(0);
        for (int i = 1; i < vertices.size(); i++) {
            V next = vertices.get(i);
            if (current.getInterval().compareTo(next.getInterval()) > 0) {
                isSorted = false;
                break;
            }
            current = next;
        }

        this.edgeFactory = ef;
        this.allowingLoops = false;
        this.allowingMultipleEdges = false;
        this.directed = false;

        this.weighted = weighted;
        this.intrusiveEdgesSpecifics = Objects.requireNonNull(
                createIntrusiveEdgesSpecifics(weighted), GRAPH_SPECIFICS_MUST_NOT_BE_NULL);

        if(isSorted) {
            this.specifics = Objects.requireNonNull(createSpecifics(vertices), GRAPH_SPECIFICS_MUST_NOT_BE_NULL);

            ArrayList<LinkedList<V>> edges = new ArrayList<>(vertices.size());

            for(int i = 0; i < vertices.size(); ++i) {
                edges.add(i, new LinkedList<>());
                for(int j = 0; j < i; ++j) {
                    if(vertices.get(j).getInterval().getEnd().compareTo(vertices.get(i).getInterval().getStart()) >= 0) {
                        edges.get(j).add(vertices.get(i));
                    }
                }
            }

            for(int i = 0; i < vertices.size(); ++i) {
                addIntervalEdges(vertices.get(i), edges.get(i));
            }

        } else {
            this.specifics = Objects.requireNonNull(createSpecifics(), GRAPH_SPECIFICS_MUST_NOT_BE_NULL);

            for (V vertex : vertices) {
                this.addVertex(vertex);
            }
        }
    }

    /**
     * Construct a new graph with given <code>vertices</code>. The graph can either be directed or undirected, depending on the
     * specified edge factory.
     *
     * @param vertices initial vertices
     * @param edges initial edges
     * @param ef the edge factory of the new graph.
     * @param directed if true the graph will be directed, otherwise undirected
     * @param allowMultipleEdges whether to allow multiple (parallel) edges or not.
     * @param allowLoops whether to allow edges that are self-loops or not.
     * @param weighted whether the graph is weighted, i.e. the edges support a weight attribute
     *
     * @throws NullPointerException if the specified edge factory is <code>
     * null</code>.
     */
    private IntervalGraph(ArrayList<V> vertices, Map<Pair<V, V>, E> edges, EdgeFactory<V, E> ef,
                          boolean directed, boolean allowMultipleEdges, boolean allowLoops, boolean weighted) {

        Objects.requireNonNull(ef);

        this.edgeFactory = ef;
        this.allowingLoops = allowLoops;
        this.allowingMultipleEdges = allowMultipleEdges;
        this.directed = directed;
        this.specifics =
                Objects.requireNonNull(createSpecifics(vertices), GRAPH_SPECIFICS_MUST_NOT_BE_NULL);
        this.weighted = weighted;
        this.intrusiveEdgesSpecifics = Objects.requireNonNull(
                createIntrusiveEdgesSpecifics(weighted), GRAPH_SPECIFICS_MUST_NOT_BE_NULL);

        for(Pair<V, V> v : edges.keySet()) {
            addExistingIntervalEdges(v.getFirst(), v.getSecond(), edges.get(v));
        }
    }

    private boolean addExistingIntervalEdges(V sourceVertex, V targetVertex, E edge) {

        assertVertexExist(sourceVertex);
        assertVertexExist(targetVertex);

        if(intrusiveEdgesSpecifics.add(edge, sourceVertex, targetVertex)) {
            specifics.addEdgeToTouchingVertices(edge);
        }

        return true;
    }

    /**
     * returns interval graph representation if one exists, otherwise null
     *
     * @param graph the graph to check
     * @param <E> the edge type
     * @param <VertexType> the internal vertex type
     * @return interval graph representation if one exists, otherwise null
     */
    public static <E, VertexType>
            IntervalGraph<IntervalVertexInterface<VertexType, Integer>, E, VertexType, Integer> asIntervalGraph(Graph<VertexType, E> graph) {

        IntervalGraphRecognizer<VertexType, E> intervalGraphRecognizer = new IntervalGraphRecognizer<>(graph);

        if(!intervalGraphRecognizer.isIntervalGraph()) {
            return null;
        }

        ArrayList<Interval<Integer>> sortedIntervalList = intervalGraphRecognizer.getIntervalsSortedByStartingPoint();
        Map<Interval<Integer>, VertexType> intervalVertexMap = intervalGraphRecognizer.getIntervalToVertexMap();
        Map<VertexType, IntervalVertexInterface<VertexType, Integer>> vertexIntervalMap = intervalGraphRecognizer.getVertexToIntervalMap();

        ArrayList<IntervalVertexInterface<VertexType, Integer>> vertices = new ArrayList<>(sortedIntervalList.size());

        for(int i = 0; i < sortedIntervalList.size(); ++i) {
            vertices.add(i, vertexIntervalMap.get(intervalVertexMap.get(sortedIntervalList.get(i))));
        }

        Map<Pair<IntervalVertexInterface<VertexType, Integer>, IntervalVertexInterface<VertexType, Integer>>, E> edges = new LinkedHashMap<>();

        for(IntervalVertexInterface<VertexType, Integer> vertex : vertices) {
            for (E edge : graph.outgoingEdgesOf(vertex.getVertex())) {
                edges.put(Pair.of(vertex, vertexIntervalMap.get(graph.getEdgeTarget(edge))), edge);
            }
        }


        return new IntervalGraph<>(vertices, edges, (sourceVertex, targetVertex) -> graph.getEdgeFactory().createEdge(sourceVertex.getVertex(), targetVertex.getVertex()),
                graph.getType().isDirected(), graph.getType().isAllowingMultipleEdges(),
                graph.getType().isAllowingSelfLoops(), graph.getType().isWeighted());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        return specifics.getAllEdges(sourceVertex, targetVertex);
    }

    /**
     * Returns <code>true</code> if and only if self-loops are allowed in this graph. A self loop is
     * an edge that its source and target vertices are the same.
     *
     * @return <code>true</code> if and only if graph loops are allowed.
     */
    public boolean isAllowingLoops()
    {
        return allowingLoops;
    }

    /**
     * Returns <code>true</code> if and only if multiple (parallel) edges are allowed in this graph. The
     * meaning of multiple edges is that there can be many edges going from vertex v1 to vertex v2.
     *
     * @return <code>true</code> if and only if multiple (parallel) edges are allowed.
     */
    public boolean isAllowingMultipleEdges()
    {
        return allowingMultipleEdges;
    }

    /**
     * Returns <code>true</code> if and only if the graph supports edge weights.
     *
     * @return <code>true</code> if the graph supports edge weights, <code>false</code> otherwise.
     */
    public boolean isWeighted()
    {
        return weighted;
    }

    /**
     * Returns <code>true</code> if the graph is directed, false if undirected.
     *
     * @return <code>true</code> if the graph is directed, false if undirected.
     */
    public boolean isDirected()
    {
        return directed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        return specifics.getEdge(sourceVertex, targetVertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgeFactory<V, E> getEdgeFactory()
    {
        return edgeFactory;
    }

    /**
     * This method an IllegalArgumentException in every case because you can not add edges to an interval graph manually.
     *
     * {@inheritDoc}
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }

    /**
     * This method an IllegalArgumentException in every case because you can not add edges to an interval graph manually.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addVertex(V v)
    {
        if (v == null) {
            throw new NullPointerException();
        } else if (containsVertex(v)) {
            return false;
        } else {
            specifics.addVertex(v);
            // add all edges between the new vertex and vertices with intersecting intervals
            addIntervalEdges(v, specifics.getOverlappingIntervalVertices(v));
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeSource(E e)
    {
        return intrusiveEdgesSpecifics.getEdgeSource(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeTarget(E e)
    {
        return intrusiveEdgesSpecifics.getEdgeTarget(e);
    }

    /**
     * Returns a shallow copy of this graph instance. Neither edges nor vertices are cloned.
     *
     * @return a shallow copy of this graph.
     *
     * @throws RuntimeException in case the clone is not supported
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        try {
            IntervalGraph<V, E, VertexType, T> newGraph = TypeUtil.uncheckedCast(super.clone());

            newGraph.edgeFactory = this.edgeFactory;
            newGraph.unmodifiableVertexSet = null;

            // NOTE: it's important for this to happen in an object
            // method so that the new inner class instance gets associated with
            // the right outer class instance
            newGraph.specifics = newGraph.createSpecifics();
            newGraph.intrusiveEdgesSpecifics =
                    newGraph.createIntrusiveEdgesSpecifics(this.weighted);

            Graphs.addGraph(newGraph, this);

            return newGraph;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsEdge(E e)
    {
        return intrusiveEdgesSpecifics.containsEdge(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsVertex(V v)
    {
        return specifics.getVertexSet().contains(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int degreeOf(V vertex)
    {
        assertVertexExist(vertex);
        return specifics.degreeOf(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgeSet()
    {
        return intrusiveEdgesSpecifics.getEdgeSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgesOf(V vertex)
    {
        assertVertexExist(vertex);
        return specifics.edgesOf(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int inDegreeOf(V vertex)
    {
        assertVertexExist(vertex);
        return specifics.inDegreeOf(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        assertVertexExist(vertex);
        return specifics.incomingEdgesOf(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int outDegreeOf(V vertex)
    {
        assertVertexExist(vertex);
        return specifics.outDegreeOf(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        assertVertexExist(vertex);
        return specifics.outgoingEdgesOf(vertex);
    }

    /**
     * This method an IllegalArgumentException in every case because you can not remove edges from an interval graph manually.
     *
     * {@inheritDoc}
     */
    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }

    /**
     * This method an IllegalArgumentException in every case because you can not remove edges from an interval graph manually.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(E e)
    {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeVertex(V v)
    {
        if (containsVertex(v)) {
            removeAllEdgesFromVertex(v);//remove all edges from the given vertex to delete it safely

            specifics.removeVertex(v);// remove the vertex itself

            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<V> vertexSet()
    {
        if (unmodifiableVertexSet == null) {
            unmodifiableVertexSet = Collections.unmodifiableSet(specifics.getVertexSet());
        }

        return unmodifiableVertexSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEdgeWeight(E e)
    {
        if (e == null) {
            throw new NullPointerException();
        }
        return intrusiveEdgesSpecifics.getEdgeWeight(e);
    }

    /**
     * Set an edge weight.
     *
     * @param e the edge
     * @param weight the weight
     * @throws UnsupportedOperationException if the graph is not weighted
     */
    @Override
    public void setEdgeWeight(E e, double weight)
    {
        if (e == null) {
            throw new NullPointerException();
        }
        intrusiveEdgesSpecifics.setEdgeWeight(e, weight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphType getType()
    {
        if (directed) {
            return new DefaultGraphType.Builder()
                    .directed().weighted(weighted).allowMultipleEdges(allowingMultipleEdges)
                    .allowSelfLoops(allowingLoops).build();
        } else {
            return new DefaultGraphType.Builder()
                    .undirected().weighted(weighted).allowMultipleEdges(allowingMultipleEdges)
                    .allowSelfLoops(allowingLoops).build();
        }
    }

    /**
     * Create the specifics for this graph. Subclasses can override this method in order to adjust
     * the specifics and thus the space-time tradeoffs of the graph implementation.
     *
     * @return the specifics used by this graph
     */
    private IntervalSpecifics<V, E, VertexType, T> createSpecifics() {
        return new IntervalSpecifics<>(this);
    }

    /**
     * Create the specifics for this graph. Subclasses can override this method in order to adjust
     * the specifics and thus the space-time tradeoffs of the graph implementation.
     *
     * @param vertices The set of vertices
     * @return the specifics used by this graph
     */
    private IntervalSpecifics<V, E, VertexType, T> createSpecifics(ArrayList<V> vertices) {
        return new IntervalSpecifics<>(this, vertices);
    }

    /**
     * Create the specifics for the edges set of the graph.
     *
     * @param weighted if true the specifics should support weighted edges
     * @return the specifics used for the edge set of this graph
     */
    private IntrusiveEdgesSpecifics<V, E> createIntrusiveEdgesSpecifics(boolean weighted) {
        if (weighted) {
            return new WeightedIntrusiveEdgesSpecifics<>();
        } else {
            return new UniformIntrusiveEdgesSpecifics<>();
        }
    }

    /**
     * Adds edges between <code>sourceVertex</code> and every vertex from <code>vertices</code>.
     *
     * @param sourceVertex source vertex of all edges
     * @param targetVertices target vertices of edges
     */
    private boolean addIntervalEdges(V sourceVertex, Collection<V> targetVertices) {

        assertVertexExist(sourceVertex);

        for(V targetVertex: targetVertices) {
            assertVertexExist(targetVertex);

            if(!sourceVertex.equals(targetVertex)) {
                E e = edgeFactory.createEdge(sourceVertex, targetVertex);

                if (intrusiveEdgesSpecifics.add(e, sourceVertex, targetVertex)) {
                    specifics.addEdgeToTouchingVertices(e);
                }
            }
        }
        return true;
    }

    /**
     * @see Graph#removeAllEdges(Collection)
     */
    private boolean removeAllEdgesFromVertex(V vertex) {
        Set<E> touchingEdgesList = edgesOf(vertex);

        // cannot iterate over list - will cause
        // ConcurrentModificationException
        ArrayList<E> edges = new ArrayList<>(touchingEdgesList);

        boolean modified = false;

        for (E e : edges) {

            if (containsEdge(e)) {
                specifics.removeEdgeFromTouchingVertices(e);
                intrusiveEdgesSpecifics.remove(e);
                modified = true;
            }
        }

        return modified;
    }
}
