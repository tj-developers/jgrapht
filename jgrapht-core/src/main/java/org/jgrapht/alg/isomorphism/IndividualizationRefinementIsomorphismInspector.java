package org.jgrapht.alg.isomorphism;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;

import java.util.Iterator;

/**
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class IndividualizationRefinementIsomorphismInspector<V, E> extends RefinementAbstractIsomorphismInspector<V, E> {

    public IndividualizationRefinementIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {
        super(graph1, graph2);
    }

    /**
     *
     * @return
     */
    @Override
    public Iterator<GraphMapping<V, E>> getMappings() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isomorphismExists() {

        return false;
    }
}
