package org.jgrapht.alg.color;

import java.util.Collections;
import org.jgrapht.Graph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

/**
 * Colouring of decomposition trees (currently done for interval graphs). This
 * algorithm iterates over lists of vertices and assigns the smallest colour to
 * each of the vertices such that the no vertex in the same list has the same
 * colour.
 * 
 * @author Suchanda Bhattacharyya (dia007)
 *
 * @param <V>
 *            The type of graph vertex
 * @param <E>
 *            The type of graph edge
 */

public class DecompositionTreeColour<V, E> implements VertexColoringAlgorithm<V> {

    /**
     * The input graph
     */
    private Graph<Integer, E> graph;
    /**
     * The map of the vertices in the input graph to it's interval sets
     */
    private Map<Integer, Set<V>> decompositionMap;

    /**
     * 
     * @param graph
     * @param decompositionMap
     */
    public DecompositionTreeColour(Graph<Integer, E> graph, Map<Integer, Set<V>> decompositionMap) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.decompositionMap = Objects.requireNonNull(decompositionMap, "there must be some decomposition present");
    }

    /**
     * Getting the ordering for the vertices
     * 
     * @return the ordering of the vertices
     */

    protected Iterable<Integer> getVertexOrdering() {
        return (Iterable<Integer>) graph.vertexSet();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jgrapht.alg.interfaces.VertexColoringAlgorithm#getColoring()
     */
    @Override
    public Coloring<V> getColoring() {

        Map<V, Integer> asssignedColors = new HashMap<>();
        Set<Integer> used = new HashSet<>();
        Set<V> free = new HashSet<>();

        // self loops not allowed, repetitions of inner vertices not allowed

        for (Integer vertex : getVertexOrdering()) {

            // find the intervals corresponding to the vertex
            // need to sort the vertices
            List<V> intervalSet = decompositionMap.get(vertex).stream().sorted().collect(Collectors.toList());

            for (V innerVertex : intervalSet) {
                // first need to iterate over each innerVertex in the outerVertex to check that
                // if there is any vertex with an already assigned colour
                if (asssignedColors.containsKey(innerVertex)) {
                    used.add(asssignedColors.get(innerVertex));

                } else {
                    // these are the vertices without any assigned colours
                    free.add(innerVertex);

                }
            }

            // here we assign colours to the free vertices

            for (V freeVertex : free) {
                int colourCandidate = 0;
                while (used.contains(colourCandidate)) {
                    colourCandidate++;
                }

                asssignedColors.put(freeVertex, colourCandidate);

                used.add(colourCandidate);

            }
            free.clear();
            used.clear();

        }

        int maxColourAssigned = Collections.max(asssignedColors.values());

        return new ColoringImpl<>(asssignedColors, maxColourAssigned + 1);
    }
}
