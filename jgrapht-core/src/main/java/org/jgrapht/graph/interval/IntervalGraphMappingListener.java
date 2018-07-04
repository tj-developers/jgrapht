package org.jgrapht.graph.interval;

import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

/**
 * Implementation of an interval graph mapping listener. IntervalGraphMapping needs this class to provide a live mapping.
 *
 * @param <V> the vertex type with a corresponding interval
 * @param <E> the edge type
 * @param <VertexType> the underlying vertex type
 * @param <T> The underlying type for intervals
 *
 * @author Christoph Grüne (christophgruene)
 * @since May 30, 2018
 */
public class IntervalGraphMappingListener<V extends IntervalVertexInterface<VertexType, T>, E, VertexType, T extends Comparable<T>>
        implements GraphListener<V, E> {

    private IntervalGraphMapping<V, E, VertexType, T> intervalGraphMapping;

    IntervalGraphMappingListener(IntervalGraphMapping<V, E, VertexType, T> intervalGraphMapping) {
        this.intervalGraphMapping = intervalGraphMapping;
    }

    @Override
    public void edgeWeightUpdated(GraphEdgeChangeEvent e) {
        // nothing to do
    }

    @Override
    public void edgeAdded(GraphEdgeChangeEvent<V, E> e) {
        // invalidate the mapping as there was added an illegal edge
        intervalGraphMapping.setMappingInvalid();
    }

    @Override
    public void edgeRemoved(GraphEdgeChangeEvent<V, E> e) {
        // invalidate the mapping as there was removed an edge illegally
        intervalGraphMapping.setMappingInvalid();
    }

    @Override
    public void vertexAdded(GraphVertexChangeEvent<V> e) {
        // add the vertex to the mapping as well as all corresponding edges
        intervalGraphMapping.addVertex(e.getVertex());
    }

    @Override
    public void vertexRemoved(GraphVertexChangeEvent<V> e) {
        // remove the vertex from the mapping as well as all corresponding edges
        intervalGraphMapping.removeVertex(e.getVertex());
    }
}
