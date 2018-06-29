package org.jgrapht.alg.isomorphism;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Optional;

/**
 *
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 *
 * @author Christoph Grüne
 */
public class IndividualizationRefinementIsomorphismInspector<V, E> extends RefinementAbstractIsomorphismInspector<V, E> implements Serializable {

    private static final long serialVersionUID = -8774735383117487442L;

    public IndividualizationRefinementIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {
        super(graph1, graph2);
    }

    @Override
    public Iterator<GraphMapping<V, E>> getMappings() {
        return null;
    }

    @Override
    public Optional<Boolean> isomorphismExists() {
        return Optional.empty();
    }
}
