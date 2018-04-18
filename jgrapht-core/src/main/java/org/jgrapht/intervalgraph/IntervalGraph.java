package org.jgrapht.intervalgraph;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.IntrusiveEdgesSpecifics;
import org.jgrapht.graph.specifics.IntervalSpecifics;
import org.jgrapht.graph.specifics.Specifics;
import org.jgrapht.intervalgraph.interval.Interval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Implementation of an Interval Graph
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 18, 2018
 */
public class IntervalGraph<V, E> extends AbstractBaseGraph<V, E> {

    private static final long serialVersionUID = 7835287075273098344L;

    private static final String INTERVAL_GRAPH_ADD_EDGE = "Intervals of nodes define edges in interval graphs and cannot be modified manually";

    /**
     * Construct a new graph. The graph can either be directed or undirected, depending on the
     * specified edge factory.
     *
     * @param ef                 the edge factory of the new graph.
     * @param weighted           whether the graph is weighted, i.e. the edges support a weight attribute
     * @throws NullPointerException if the specified edge factory is <code>
     *                              null</code>.
     */
    protected IntervalGraph(EdgeFactory<V, E> ef, boolean weighted) {
        super(ef, false, false, false, weighted);
    }

    /**
     * {@inheritDoc}
     *
     * @param sourceVertex
     * @param targetVertex
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }

    /**
     * {@inheritDoc}
     *
     * @param sourceVertex
     * @param targetVertex
     * @param e
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }

    /**
     * {@inheritDoc}
     *
     * @param sourceVertex
     * @param targetVertex
     */
    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }

    /**
     * {@inheritDoc}
     *
     * @param e
     */
    @Override
    public boolean removeEdge(E e) {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }

    /**
     * {@inheritDoc}
     *
     * @param v
     */
    @Override
    public boolean removeVertex(V v) {
        if (containsVertex(v)) {
            super.removeVertex(v); // remove the vertex itself
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphType getType() {
        //TODO is that correct? -> Probably, we have to add a new mwtho to the DefaultGraphType class.
        return super.getType();
    }

    /**
     * Create the specifics for this graph. Subclasses can override this method in order to adjust
     * the specifics and thus the space-time tradeoffs of the graph implementation.
     *
     * @param directed if true the specifics should adjust the behavior to a directed graph
     *                 otherwise undirected
     * @return the specifics used by this graph
     */
    @Override
    protected Specifics<V, E> createSpecifics(boolean directed) {
        return new IntervalSpecifics<>(this);
    }

    /**
     * @param edges
     * @see Graph#removeAllEdges(Collection)
     */
    @Override
    public boolean removeAllEdges(Collection<? extends E> edges) {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }

    /**
     * @param sourceVertex
     * @param targetVertex
     * @see Graph#removeAllEdges(Object, Object)
     */
    @Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex) {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }

    /**
     * Removes all the edges in this graph that are also contained in the specified edge array.
     * After this call returns, this graph will contain no edges in common with the specified edges.
     * This method will invoke the {@link Graph#removeEdge(Object)} method.
     *
     * @param edges edges to be removed from this graph.
     * @return <tt>true</tt> if this graph changed as a result of the call.
     * @see Graph#removeEdge(Object)
     * @see Graph#containsEdge(Object)
     */
    @Override
    protected boolean removeAllEdges(E[] edges) {
        throw new IllegalArgumentException(INTERVAL_GRAPH_ADD_EDGE);
    }
}
