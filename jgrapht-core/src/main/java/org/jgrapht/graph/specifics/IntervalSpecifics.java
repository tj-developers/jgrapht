package org.jgrapht.graph.specifics;

import org.jgrapht.graph.EdgeSetFactory;
import org.jgrapht.graph.IntervalGraph;
import org.jgrapht.graph.IntervalVertexInterface;
import org.jgrapht.util.ArrayUnenforcedSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of IntervalSpecifics.
 * This class implements necessary methods for IntervalGraph.
 *
 * @param <V> the interval vertex type
 * @param <E> the edge type
 * @param <VertexType> the internal vertex type
 * @param <T> The underlying type for intervals
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
public class IntervalSpecifics<V extends IntervalVertexInterface<VertexType, T>, E, VertexType, T extends Comparable<T>> implements Specifics<V, E>, Serializable {

    private static final long serialVersionUID = 1112673663745687843L;

    private IntervalGraph<V, E, VertexType, T> intervalGraph;
    private IntervalGraphVertexContainerInterface<V, E> intervalGraphVertexContainerInterface;
    private EdgeSetFactory<V, E> edgeSetFactory;

    /**
     * Constructs new interval specifics.
     *
     * @param intervalGraph the graph for which these specifics are for
     */
    public IntervalSpecifics(IntervalGraph<V, E, VertexType, T> intervalGraph) {
        this.intervalGraph = intervalGraph;
        this.intervalGraphVertexContainerInterface = new IntervalGraphVertexContainer<>();
        this.edgeSetFactory = new ArrayUnenforcedSetEdgeSetFactory<>();
    }

    /**
     * Constructs new interval specifics.
     *
     * @param intervalGraph the graph for which these specifics are for
     * @param vertices The vertices of the graph
     */
    public IntervalSpecifics(IntervalGraph<V, E, VertexType, T> intervalGraph, ArrayList<V> vertices) {

        this.intervalGraph = intervalGraph;
        this.edgeSetFactory = new ArrayUnenforcedSetEdgeSetFactory<>();

        ArrayList<UndirectedEdgeContainer<V, E>> undirectedEdgeContainers = new ArrayList<>(vertices.size());
        for(int i = 0; i < vertices.size(); ++i) {
            undirectedEdgeContainers.add(i, new UndirectedEdgeContainer<>(edgeSetFactory, vertices.get(i)));
        }

        this.intervalGraphVertexContainerInterface = new IntervalGraphVertexContainer<>(vertices, undirectedEdgeContainers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addVertex(V vertex)
    {
        getEdgeContainer(vertex);
    }

    /**
     * Gets the overlapping vertices for a given vertex
     *
     * @param vertex The input vertex
     *
     * @return A list of vertices that overlap the input vertex
     */
    public List<V> getOverlappingIntervalVertices(V vertex) {
        return intervalGraphVertexContainerInterface.getOverlappingIntervalVertices(vertex);
    }

    /**
     * Removes a vertex
     *
     * @param v The vertex that should be removed
     */
    public void removeVertex(V v) {
        intervalGraphVertexContainerInterface.remove(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<V> getVertexSet()
    {
        return intervalGraphVertexContainerInterface.getVertexSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        Set<E> edges = null;

        if (intervalGraph.containsVertex(sourceVertex)
                && intervalGraph.containsVertex(targetVertex))
        {
            edges = new ArrayUnenforcedSet<>();

            for (E e : getEdgeContainer(sourceVertex).vertexEdges) {
                boolean equal = isEqualsStraightOrInverted(sourceVertex, targetVertex, e);

                if (equal) {
                    edges.add(e);
                }
            }
        }

        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        if (intervalGraph.containsVertex(sourceVertex)
                && intervalGraph.containsVertex(targetVertex))
        {

            for (E e : getEdgeContainer(sourceVertex).vertexEdges) {
                boolean equal = isEqualsStraightOrInverted(sourceVertex, targetVertex, e);

                if (equal) {
                    return e;
                }
            }
        }

        return null;
    }

    private boolean isEqualsStraightOrInverted(Object sourceVertex, Object targetVertex, E e)
    {
        boolean equalStraight = sourceVertex.equals(intervalGraph.getEdgeSource(e))
                && targetVertex.equals(intervalGraph.getEdgeTarget(e));

        boolean equalInverted = sourceVertex.equals(intervalGraph.getEdgeTarget(e))
                && targetVertex.equals(intervalGraph.getEdgeSource(e));
        return equalStraight || equalInverted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEdgeToTouchingVertices(E e)
    {
        V source = intervalGraph.getEdgeSource(e);
        V target = intervalGraph.getEdgeTarget(e);

        getEdgeContainer(source).addEdge(e);

        if (!source.equals(target)) {
            getEdgeContainer(target).addEdge(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int degreeOf(V vertex)
    {
        if (intervalGraph.isAllowingLoops()) { // then we must count, and add loops twice
            int degree = 0;
            Set<E> edges = getEdgeContainer(vertex).vertexEdges;

            for (E e : edges) {
                if (intervalGraph.getEdgeSource(e).equals(intervalGraph.getEdgeTarget(e))) {
                    degree += 2;
                } else {
                    degree += 1;
                }
            }

            return degree;
        } else {
            return getEdgeContainer(vertex).edgeCount();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgesOf(V vertex)
    {
        return getEdgeContainer(vertex).getUnmodifiableVertexEdges();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int inDegreeOf(V vertex)
    {
        return degreeOf(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        return getEdgeContainer(vertex).getUnmodifiableVertexEdges();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int outDegreeOf(V vertex)
    {
        return degreeOf(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        return getEdgeContainer(vertex).getUnmodifiableVertexEdges();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEdgeFromTouchingVertices(E e)
    {
        V source = intervalGraph.getEdgeSource(e);
        V target = intervalGraph.getEdgeTarget(e);

        getEdgeContainer(source).removeEdge(e);

        if (!source.equals(target)) {
            getEdgeContainer(target).removeEdge(e);
        }
    }

    /**
     * Get the edge container for a specified vertex.
     *
     * @param vertex a vertex in this graph
     *
     * @return an edge container
     */
    private UndirectedEdgeContainer<V, E> getEdgeContainer(V vertex)
    {
        UndirectedEdgeContainer<V, E> ec = intervalGraphVertexContainerInterface.get(vertex);

        if (ec == null) {
            ec = new UndirectedEdgeContainer<>(edgeSetFactory, vertex);
            intervalGraphVertexContainerInterface.put(vertex, ec);
        }

        return ec;
    }

}
