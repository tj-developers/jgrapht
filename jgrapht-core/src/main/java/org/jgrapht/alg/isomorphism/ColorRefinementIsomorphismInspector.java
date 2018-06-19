/*
 * (C) Copyright 2018-2018, by Christoph Grüne and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
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
 * Implementation of the color refinement algorithm isomorphism test using its feature of detecting
 * <a href="http://mathworld.wolfram.com/GraphIsomorphism.html">isomorphism between two graphs</a>
 * as described in
 * C. Berkholz, P. Bonsma, and M. Grohe.  Tight lower and upper bounds for the complexity of canonical
 * colour refinement. Theory of Computing Systems,doi:10.1007/s00224-016-9686-0, 2016 (color refinement)
 * The complexity of this algorithm is O(|V| + |E| log |V|).
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 *
 * @author Christoph Grüne
 */
public class ColorRefinementIsomorphismInspector<V, E> extends RefinementAbstractIsomorphismInspector<V, E> implements Serializable {

    private static final long serialVersionUID = -4798546523147487442L;

    private IsomorphicGraphMapping isomorphicGraphMapping = null;

    private boolean isColoringDiscrete;
    private boolean isIsomorphic;

    private boolean isomorphismTestExecuted = false;

    public ColorRefinementIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {
        super(graph1, graph2);
        isColoringDiscrete = false;
    }

    /**
     * returns the mapping if the calculated coarse coloring returned by color refinement is discrete.
     * Otherwise it returns null.
     *
     * @return the mapping of the graph isomorphism if the coloring is discrete, otherwise null.
     */
    @Override
    public Iterator<GraphMapping<V, E>> getMappings() throws IllegalStateException {
        //TODO we have to calculate the mapping if the coloring is discrete
        return null;
    }

    /**
     * returns whether the graphs are isomorphic or not.
     *
     * @return false, iff the graphs are not isomorphic; true, iff we cannot decide whether they are isomorphic.
     * If the method says true, call isColoringDiscrete(). If this method says true, the graphs are isomorphic,
     * otherwise we cannot say whether they are or not.
     */
    @Override
    public boolean isomorphismExists() {
        // TODO: maybe rename to couldBeIsomorphic
        if(isomorphismTestExecuted) {
            return isIsomorphic;
        }

        if(graph1.vertexSet().size() != graph2.vertexSet().size()) {
            return false;
        }

        ColorRefinementAlgorithm<V, E> colorRefinementAlgorithm1 = new ColorRefinementAlgorithm<>(graph1);
        ColorRefinementAlgorithm<V, E> colorRefinementAlgorithm2 = new ColorRefinementAlgorithm<>(graph2);

        Coloring<V> coloring1 = colorRefinementAlgorithm1.getColoring(); // execute color refinement for graph1
        Coloring<V> coloring2 = colorRefinementAlgorithm2.getColoring(); // execute color refinement for graph2

        isIsomorphic = coarseColoringAreEqual(coloring1, coloring2);
        return isIsomorphic;
    }

    /**
     * returns whether the coarse colorings of the two given graphs are discrete if they are the same.
     *
     * @return if the both colorings are discrete if they are the same on both graphs.
     */
    public boolean isColoringDiscrete() {
        if(!isomorphismTestExecuted) {
            isomorphismExists();
        }
        return isColoringDiscrete;
    }

    /**
     * checks whether two coarse colorings are equal. Furthermore, it sets <code>isColoringDiscrete</code> to true iff the colorings are discrete.
     *
     * @param coloring1 the first coarse coloring
     * @param coloring2 the second coarse coloring
     * @return if the given coarse colorings are equal
     */
    private boolean coarseColoringAreEqual(Coloring<V> coloring1, Coloring<V> coloring2) {
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
            if(coloring1.getNumberColors() == graph1.vertexSet().size() && coloring2.getNumberColors() == graph2.vertexSet().size()) { // check if the colorings are discrete, that is, the color mapping is injective.
                isColoringDiscrete = true;
            }
            return true;
        } else { // just a safety check. The program should not go into that branch as we checked that the size of the sets of all color classes is the same. Nevertheless, the graphs are not isomorphic if this case occurs.
            return false;
        }
    }

    /**
     * sorts a list of color classes by the size and the color (integer representation of the color) and
     *
     * @param colorClasses the list of the color classes
     * @param coloring the coloring
     */
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
