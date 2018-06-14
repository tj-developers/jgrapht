package org.jgrapht.alg.isomorphism;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;

import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class IndividualizationRefinementIsomorphismInspector<V, E> extends RefinementAbstractIsomorphismInspector<V, E> implements Serializable {

    private static final long serialVersionUID = -4757432456465400134L;

    public IndividualizationRefinementIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {
        super(graph1, graph2);
    }

    /**
     *
     * @return
     */
    @Override
    public Iterator<GraphMapping<V, E>> getMappings() {
        // TODO
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isomorphismExists() {
        // TODO
        return false;
    }
}
