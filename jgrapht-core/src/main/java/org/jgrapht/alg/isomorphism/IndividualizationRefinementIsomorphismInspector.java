package org.jgrapht.alg.isomorphism;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.color.ColorRefinementAlgorithm;
import org.jgrapht.alg.color.IndividualizationRefinementAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    /**
     * Check if an isomorphism exists.
     *
     * @return Optional.of(true) if there is an isomorphism, Optional.of(false) if there is no isomorphism,
     * Optional.empty if ut cannot be decided whether there is an isomorphism or not.
     */
    @Override
    public Optional<Boolean> isomorphismExists() {
        if(isomorphismTestExecuted) {
            return Optional.of(isIsomorphic);
        }

        if(graph1.vertexSet().size() != graph2.vertexSet().size()) {
            return Optional.of(false);
        }

        IndividualizationRefinementAlgorithm<V, E> individualizationRefinementAlgorithm1
                = new IndividualizationRefinementAlgorithm<>(graph1);
        IndividualizationRefinementAlgorithm<V, E> individualizationRefinementAlgorithm2
                = new IndividualizationRefinementAlgorithm<>(graph2);

        // execute color refinement for graph1
        VertexColoringAlgorithm.Coloring<V> coloring1 = individualizationRefinementAlgorithm1.getColoring();
        // execute color refinement for graph2
        VertexColoringAlgorithm.Coloring<V> coloring2 = individualizationRefinementAlgorithm2.getColoring();

        isIsomorphic = coarseColoringAreEqual(coloring1, coloring2);
        return Optional.of(isIsomorphic);
    }
    /**
     * checks whether two coarse colorings are equal. Furthermore, it sets <code>isColoringDiscrete</code> to true iff the colorings are discrete.
     *
     * @param coloring1 the first coarse coloring
     * @param coloring2 the second coarse coloring
     * @return if the given coarse colorings are equal
     */
    private boolean coarseColoringAreEqual(VertexColoringAlgorithm.Coloring<V> coloring1, VertexColoringAlgorithm.Coloring<V> coloring2) {
        if(coloring1.getNumberColors() != coloring2.getNumberColors()) {
            return false;
        }

        List<Set<V>> colorClasses1 = coloring1.getColorClasses();
        List<Set<V>> colorClasses2 = coloring2.getColorClasses();

        if(colorClasses1.size() != colorClasses2.size()) {
            return false;
        }

        sortColorClasses(colorClasses1, coloring1);
        sortColorClasses(colorClasses2, coloring2);

        Iterator<Set<V>> it1 = colorClasses1.iterator();
        Iterator<Set<V>> it2 = colorClasses2.iterator();

        // check the color classes
        while(it1.hasNext() && it2.hasNext()) {
            Set<V> cur1 = it1.next();
            Set<V> cur2 = it2.next();

            if(cur1.size() != cur2.size()) { // check if the size for the current color class are the same for both graphs.
                return false;
            }
            if(cur1.iterator().hasNext()) { // safety check whether the color class is not empty.
                if(!coloring1.getColors().get(cur1.iterator().next()).equals(coloring2.getColors().get(cur2.iterator().next()))) { // check if the color are not the same (works as colors are integers).
                    return false; // colors are not the same -> graphs are not isomorphic.
                }
            }
        }

        if(!it1.hasNext() && !it2.hasNext()) { // no more color classes for both colorings, that is, the graphs have the same coloring.
            if(coloring1.getColorClasses().size() == graph1.vertexSet().size() && coloring2.getColorClasses().size() == graph2.vertexSet().size()) { // check if the colorings are discrete, that is, the color mapping is injective.
                calculateGraphMapping(coloring1, coloring2);
                return true;
            }
            return false;
        } else { // just a safety check. The program should not go into that branch as we checked that the size of the sets of all color classes is the same. Nevertheless, the graphs are not isomorphic if this case occurs.
            return false;
        }
    }
}
