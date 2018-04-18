package org.jgrapht.intervalgraph;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.IntrusiveEdgesSpecifics;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.specifics.Specifics;

import java.util.Collection;
import java.util.LinkedHashSet;
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
public class IntervalGraph<V, E> extends SimpleWeightedGraph<V, E> {

    private static final String INTERVAL_GRAPH_ADD_EDGE = "Intervals of nodes define edges in interval graphs and cannot be modified manually";

    /**
     * Creates a new simple weighted graph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public IntervalGraph(EdgeFactory<V, E> ef) {
        super(ef);
    }

    /**
     * Creates a new simple weighted graph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public IntervalGraph(Class<? extends E> edgeClass) {
        super(edgeClass);
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
     * @param v
     */
    @Override
    public boolean addVertex(V v) {
        return super.addVertex(v);
    }

    /**
     * {@inheritDoc}
     *
     * @param sourceVertex
     * @param targetVertex
     */
    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        return super.removeEdge(sourceVertex, targetVertex);
    }

    /**
     * {@inheritDoc}
     *
     * @param e
     */
    @Override
    public boolean removeEdge(E e) {
        return super.removeEdge(e);
    }

    /**
     * {@inheritDoc}
     *
     * @param v
     */
    @Override
    public boolean removeVertex(V v) {
        return super.removeVertex(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphType getType() {
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
        return super.createSpecifics(directed);
    }

    /**
     * Create the specifics for the edges set of the graph.
     *
     * @param weighted if true the specifics should support weighted edges
     * @return the specifics used for the edge set of this graph
     */
    @Override
    protected IntrusiveEdgesSpecifics<V, E> createIntrusiveEdgesSpecifics(boolean weighted) {
        return super.createIntrusiveEdgesSpecifics(weighted);
    }

    /**
     * @param edges
     * @see Graph#removeAllEdges(Collection)
     */
    @Override
    public boolean removeAllEdges(Collection<? extends E> edges) {
        return super.removeAllEdges(edges);
    }

    /**
     * @param sourceVertex
     * @param targetVertex
     * @see Graph#removeAllEdges(Object, Object)
     */
    @Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex) {
        return super.removeAllEdges(sourceVertex, targetVertex);
    }

    /**
     * @param vertices
     * @see Graph#removeAllVertices(Collection)
     */
    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        return super.removeAllVertices(vertices);
    }

    /**
     * Ensures that the specified vertex exists in this graph, or else throws exception.
     *
     * @param v vertex
     * @return <code>true</code> if this assertion holds.
     * @throws NullPointerException     if specified vertex is <code>null</code>.
     * @throws IllegalArgumentException if specified vertex does not exist in this graph.
     */
    @Override
    protected boolean assertVertexExist(V v) {
        return super.assertVertexExist(v);
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
        return super.removeAllEdges(edges);
    }

    /**
     * Helper for subclass implementations of toString( ).
     *
     * @param vertexSet the vertex set V to be printed
     * @param edgeSet   the edge set E to be printed
     * @param directed  true to use parens for each edge (representing directed); false to use curly
     *                  braces (representing undirected)
     * @return a string representation of (V,E)
     */
    @Override
    protected String toStringFromSets(Collection<? extends V> vertexSet, Collection<? extends E> edgeSet, boolean directed) {
        return super.toStringFromSets(vertexSet, edgeSet, directed);
    }
}
