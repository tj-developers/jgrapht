package org.jgrapht.graph.interval;

import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

public class IntervalGraphMappingListener<V extends IntervalVertexInterface<VertexType, T>, E, VertexType, T extends Comparable<T>> implements GraphListener<V, E> {

    private IntervalGraphMapping<V, E, VertexType, T> intervalGraphMapping;

    IntervalGraphMappingListener(IntervalGraphMapping<V, E, VertexType, T> intervalGraphMapping) {
        this.intervalGraphMapping = intervalGraphMapping;
    }

    @Override
    public void edgeWeightUpdated(GraphEdgeChangeEvent e) {
        // nothing to do, this operation is allowed
    }

    @Override
    public void edgeAdded(GraphEdgeChangeEvent<V, E> e) {
        // we have to invalidate the mapping as there was added an illegal edge
        intervalGraphMapping.setMappingInvalid();
    }

    @Override
    public void edgeRemoved(GraphEdgeChangeEvent<V, E> e) {
        // we have to invalidate the mapping as there was removed an edge illegally
        intervalGraphMapping.setMappingInvalid();
    }

    @Override
    public void vertexAdded(GraphVertexChangeEvent<V> e) {
        // we have to add the vertex to the mapping as well as all corresponding edges
        intervalGraphMapping.addVertex(e.getVertex());
    }

    @Override
    public void vertexRemoved(GraphVertexChangeEvent<V> e) {
        // we have to remove the vertex from the mapping as well as all corresponding edges
        intervalGraphMapping.removeVertex(e.getVertex());
    }
}
