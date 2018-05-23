package org.jgrapht.alg.isomorphism;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.color.ColorRefinementAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.Coloring;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        if(coloring1.getNumberColors() != coloring2.getNumberColors()) {
            return false;
        }

        List<Set<V>> colorClasses1 = coloring1.getColorClasses();
        List<Set<V>> colorClasses2 = coloring2.getColorClasses();

        sortColorClasses(colorClasses1, coloring1);
        sortColorClasses(colorClasses2, coloring2);

        Iterator<Set<V>> it1 = colorClasses1.iterator();
        Iterator<Set<V>> it2 = colorClasses2.iterator();

        while(true) {
            if(!it1.hasNext()) {
                return !it2.hasNext();
            } else if(!it2.hasNext()) {
                return false;
            }

            Set<V> cur1 = it1.next();
            Set<V> cur2 = it2.next();

            if(cur1.size() != cur2.size()) {
                return false;
            }
            if(cur1.iterator().hasNext()) {
                if(!coloring1.getColors().get(cur1.iterator().next()).equals(coloring2.getColors().get(cur2.iterator().next()))) {
                    return false;
                }
            }
        }
    }

    private void sortColorClasses(List<Set<V>> colorClasses, Coloring<V> coloring) {
        colorClasses.sort((o1, o2) -> {
            if(o1.size() == o2.size()) {
                Iterator it1 = o1.iterator();
                Iterator it2 = o2.iterator();
                if(!it1.hasNext() || !it2.hasNext()) {
                    return ((Integer) o1.size()).compareTo(o2.size());
                }
                return coloring.getColors().get(it1.next()).compareTo(coloring.getColors().get(it2.next()));
            }
            return ((Integer) o1.size()).compareTo(o2.size());
        });
    }
}
