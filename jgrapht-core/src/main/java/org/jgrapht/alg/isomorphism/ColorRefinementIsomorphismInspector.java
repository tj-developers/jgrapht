package org.jgrapht.alg.isomorphism;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.color.ColorRefinementAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.Coloring;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class ColorRefinementIsomorphismInspector<V, E> extends RefinementAbstractIsomorphismInspector<V, E> implements Serializable {

    private static final long serialVersionUID = -4798546523147487442L;

    public ColorRefinementIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {
        super(graph1, graph2);
    }

    /**
     *
     *
     * @return
     * @throws IllegalStateException
     */
    @Override
    public Iterator<GraphMapping<V, E>> getMappings() throws IllegalStateException {
        throw new IllegalStateException("ColorRefinement does not calculate a mapping. It only can decide whether the graphs are not isomorphic.");
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isomorphismExists() {
        ColorRefinementAlgorithm<V, E> colorRefinementAlgorithm1 = new ColorRefinementAlgorithm<>(graph1);
        ColorRefinementAlgorithm<V, E> colorRefinementAlgorithm2 = new ColorRefinementAlgorithm<>(graph2);

        Coloring<V> coloring1 = colorRefinementAlgorithm1.getColoring();
        Coloring<V> coloring2 = colorRefinementAlgorithm2.getColoring();

        return coarseColoringAreEqual(coloring1, coloring2);
    }

    private boolean coarseColoringAreEqual(Coloring<V> coloring1, Coloring<V> coloring2) {
        // TODO
        return false;
    }
}
