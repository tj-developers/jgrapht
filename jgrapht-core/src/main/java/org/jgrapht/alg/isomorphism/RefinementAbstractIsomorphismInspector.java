package org.jgrapht.alg.isomorphism;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.GraphType;

import java.util.Iterator;

public abstract class RefinementAbstractIsomorphismInspector<V, E> implements IsomorphismInspector<V, E> {

    protected Graph<V, E> graph1, graph2;

    /**
     * Construct a new base implementation of the Refinement isomorphism inspector.
     *
     * @param graph1 the first graph
     * @param graph2 the second graph
     */
    public RefinementAbstractIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {

        GraphType type1 = graph1.getType();
        GraphType type2 = graph2.getType();
        if (type1.isAllowingMultipleEdges() || type2.isAllowingMultipleEdges()) {
            throw new IllegalArgumentException("graphs with multiple (parallel) edges are not supported");
        }

        if (type1.isMixed() || type2.isMixed()) {
            throw new IllegalArgumentException("mixed graphs not supported");
        }

        if (type1.isUndirected() && type2.isDirected() || type1.isDirected() && type2.isUndirected()) {
            throw new IllegalArgumentException("can not match directed with " + "undirected graphs");
        }

        this.graph1 = graph1;
        this.graph2 = graph2;
    }

    @Override
    public abstract Iterator<GraphMapping<V, E>> getMappings();

    @Override
    public boolean isomorphismExists() {
        Iterator<GraphMapping<V, E>> iter = getMappings();
        return iter.hasNext();
    }
}
