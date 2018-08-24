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

import org.jgrapht.*;
import org.jgrapht.alg.color.ColorRefinementAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.Coloring;

import java.util.*;

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
public class ColorRefinementIsomorphismInspector<V, E> extends RefinementAbstractIsomorphismInspector<V, E> {

    /**
     * contains whether the two graphs produce a discrete coloring.
     * Then, we can decide whether the graphs are isomorphic.
     */
    private boolean isColoringDiscrete;
    /**
     * contains whether the two graphs are forests. Forests can be identified to be isomorphic or not.
     */
    private boolean isForest;

    /**
     * Constructor for a isomorphism inspector based on color refinement. It checks whether <code>graph1</code> and
     * <code>graph2</code> are isomorphic.
     *
     * @param graph1 the first graph
     * @param graph2 the second graph
     */
    public ColorRefinementIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {
        super(graph1, graph2);

        this.isColoringDiscrete = false;
        this.isForest = false;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IsomorphismUndecidableException if the isomorphism test was not executed and the inspector cannot decide whether the graphs are isomorphic
     */
    @Override
    public Iterator<GraphMapping<V, E>> getMappings() {
        if(!isomorphismTestExecuted) {
            isomorphismExists();
        }
        ArrayList<GraphMapping<V, E>> iteratorList = new ArrayList<>(1);
        if(isIsomorphic != null && isIsomorphic) {
            iteratorList.add(isomorphicGraphMapping);
        }
        return iteratorList.iterator();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IsomorphismUndecidableException if the inspector cannot decide whether the graphs are isomorphic
     */
    @Override
    public boolean isomorphismExists() {
        if(isomorphismTestExecuted) {
            if(isIsomorphic != null) {
                return isIsomorphic;
            } else {
                throw new IsomorphismUndecidableException();
            }
        }

        if(graph1.vertexSet().size() != graph2.vertexSet().size()) {
            return false;
        }

        ColorRefinementAlgorithm<V, E> colorRefinementAlgorithm1 = new ColorRefinementAlgorithm<>(graph1);
        ColorRefinementAlgorithm<V, E> colorRefinementAlgorithm2 = new ColorRefinementAlgorithm<>(graph2);

        // execute color refinement for graph1
        Coloring<V> coloring1 = colorRefinementAlgorithm1.getColoring();
        // execute color refinement for graph2
        Coloring<V> coloring2 = colorRefinementAlgorithm2.getColoring();

        isomorphismTestExecuted = true;

        isIsomorphic = coarseColoringAreEqual(coloring1, coloring2);
        return isIsomorphic;
    }

    /**
     * Returns whether the coarse colorings of the two given graphs are discrete if the colorings are the same.
     * Otherwise, we do not computed if the coloring is discrete, hence, this method is undefined.
     *
     * @return if the both colorings are discrete if they are the same on both graphs.
     *
     * @throws IsomorphismUndecidableException if the isomorphism test was not executed and the inspector cannot decide whether the graphs are isomorphic
     */
    public boolean isColoringDiscrete() {
        if(!isomorphismTestExecuted) {
            isomorphismExists();
        }
        return isColoringDiscrete;
    }

    /**
     * Returns whether the two given graphs are forests if an isomorphism exists and the coloring is not discrete,
     * because if the coloring is discrete, it does not have to be calculated if the graphs are forests.
     * Otherwise, we do not compute if the graphs are forests, hence, this method is undefined.
     *
     * @return if the both colorings are discrete if they are the same on both graphs.
     *
     * @throws IsomorphismUndecidableException if the isomorphism test was not executed and the inspector cannot decide whether the graphs are isomorphic
     */
    public boolean isForest() {
        if(!isomorphismTestExecuted) {
            isomorphismExists();
        }
        return isForest;
    }

    /**
     * Checks whether two coarse colorings are equal.
     * Furthermore, it sets <code>isColoringDiscrete</code> to true iff the colorings are discrete and equal.
     * Furthermore, it sets <code>isColoringDiscrete</code> to true iff the graphs are forests and isomorphic.
     *
     * @param coloring1 the first coarse coloring
     * @param coloring2 the second coarse coloring
     * @return if the given coarse colorings are equal
     */
    private boolean coarseColoringAreEqual(Coloring<V> coloring1, Coloring<V> coloring2) throws IsomorphismUndecidableException {
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

            // check if the size for the current color class are the same for both graphs.
            if(cur1.size() != cur2.size()) {
                return false;
            }
            // safety check whether the color class is not empty.
            if(cur1.iterator().hasNext()) {
                // check if the color are not the same (works as colors are integers).
                if(!coloring1.getColors().get(cur1.iterator().next()).equals(coloring2.getColors().get(cur2.iterator().next()))) {
                    // colors are not the same -> graphs are not isomorphic.
                    return false;
                }
            }
        }

        // no more color classes for both colorings, that is, the graphs have the same coloring.
        if(!it1.hasNext() && !it2.hasNext()) {

            /*
             * Check if the colorings are discrete, that is, the color mapping is injective.
             * Check if the graphs are forests.
             * In both cases color refinement can decide if there is an isomorphism.
             */
            if(coloring1.getColorClasses().size() == graph1.vertexSet().size() && coloring2.getColorClasses().size() == graph2.vertexSet().size()) {
                isColoringDiscrete = true;
                calculateGraphMapping(coloring1, coloring2);
                return true;
            } else if(GraphTests.isForest(graph1) && GraphTests.isForest(graph2)) {
                isForest = true;
                calculateGraphMapping(coloring1, coloring2);
                return true;
            }
            isIsomorphic = null;
            throw new IsomorphismUndecidableException("Color refinement cannot decide whether the two graphs are isomorphic or not.");
        } else {
            return false;
        }
    }
}
