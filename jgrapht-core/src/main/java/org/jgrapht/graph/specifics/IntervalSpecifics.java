package org.jgrapht.graph.specifics;

import org.jgrapht.graph.EdgeSetFactory;
import org.jgrapht.intervalgraph.IntervalGraph;
import org.jgrapht.intervalgraph.IntervalGraphVertexContainerInterface;
import org.jgrapht.intervalgraph.IntervalGraphVertexContainer;
import org.jgrapht.intervalgraph.interval.IntervalVertex;
import org.jgrapht.util.ArrayUnenforcedSet;

import java.io.Serializable;
import java.util.Set;

public class IntervalSpecifics<V extends IntervalVertex, E> implements Specifics<V, E>, Serializable {

    private static final long serialVersionUID = 1112673663745687843L;

    protected IntervalGraph<V, E> intervalGraph;
    protected IntervalGraphVertexContainerInterface<V, E> intervalGraphVertexContainerInterface; //TODO anpassen
    protected EdgeSetFactory<V, E> edgeSetFactory;

    /**
     * Construct a new interval specifics.
     *
     * @param intervalGraph the graph for which these specifics are for
     */
    public IntervalSpecifics(IntervalGraph<V, E> intervalGraph) {
        this.intervalGraph = intervalGraph;
        this.intervalGraphVertexContainerInterface = new IntervalGraphVertexContainer<>();
        this.edgeSetFactory = new ArrayUnenforcedSetEdgeSetFactory<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addVertex(V v)
    {
        getEdgeContainer(v);
        intervalGraph.addIntervalEdges(v, intervalGraphVertexContainerInterface.getOverlappingIntervalVertices(v));
    }

    /**
     *
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
    protected UndirectedEdgeContainer<V, E> getEdgeContainer(V vertex)
    {
        UndirectedEdgeContainer<V, E> ec = intervalGraphVertexContainerInterface.get(vertex);

        if (ec == null) {
            ec = new UndirectedEdgeContainer<>(edgeSetFactory, vertex);
            intervalGraphVertexContainerInterface.put(vertex, ec);
        }

        return ec;
    }

}
