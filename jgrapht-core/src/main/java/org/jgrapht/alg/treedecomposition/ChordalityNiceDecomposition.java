package org.jgrapht.alg.treedecomposition;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.cycle.*;

/**
 * A builder for a nice decomposition for chordal graphs. See {@link NiceDecompositionBuilder} for
 * an explanation of nice decomposition.
 * 
 * @author Ira Justus Fesefeldt (PhoenixIra)
 *
 * @param <V> the vertex type of the graph
 * @param <E> the edge type of the graph
 */
public class ChordalityNiceDecomposition<V, E>
    extends
    NiceDecompositionBuilder<V>
{
    // the chordal graph
    Graph<V, E> graph;

    // the perfect eliminiation order of graph
    List<V> perfectOrder;

    // another representation of the perfect eliminiation order of graph
    Map<V, Integer> vertexInOrder;

    /**     
     * Factory method for the nice decomposition builder of chordal graphs. Returns null, if the
     * graph is not chordal.
     * 
     * @param <V> the vertex type of graph
     * @param <E> the edge type of graph
     * @param graph the chordal graph for which a decomposition should be created
     * @return a nice decomposition builder for the graph if the graph was chordal, else null
     */
    public static <V, E> ChordalityNiceDecomposition<V, E> create(Graph<V, E> graph)
    {
        ChordalityInspector<V, E> inspec = new ChordalityInspector<V, E>(graph);
        if (!inspec.isChordal())
            return null;
        else
            return new ChordalityNiceDecomposition<>(graph, inspec.getSearchOrder());

    }

    /**
     * Creates a nice decomposition builder for chordal graphs.
     * 
     * @param graph the chordal graph
     * @param perfectOrder the perfect elimination order of graph
     */
    private ChordalityNiceDecomposition(Graph<V, E> graph, List<V> perfectOrder)
    {
        super();
        this.graph = graph;
        this.perfectOrder = perfectOrder;
        vertexInOrder = getVertexInOrder();
        computeNiceDecomposition();
    }

    /**
     * Computes the nice decomposition of the graph.
     * 
     * @return nice decomposition builder if it is chordal, null otherwise.
     */
    private void computeNiceDecomposition()
    {

        // init
        Map<V, Integer> introduceMap = new HashMap<V, Integer>(graph.vertexSet().size());
        Integer decompVertex = getRoot();

        // iterate from last to first
        for (V vertex : perfectOrder) {
            Set<V> predecessors = getPredecessors(vertexInOrder, vertex);
            // calculate nearest successors according to order
            V lastVertex = null;
            for (V predecessor : predecessors) {
                if (lastVertex == null)
                    lastVertex = predecessor;
                if (vertexInOrder.get(predecessor) > vertexInOrder.get(lastVertex))
                    lastVertex = predecessor;
            }

            // create a join node for the nearest successor
            if (lastVertex != null)
                decompVertex = introduceMap.get(lastVertex);

            // not a leaf node, thus create join node
            if (Graphs.vertexHasSuccessors(getDecomposition(), decompVertex)) {
                // found some intersection!
                if (lastVertex != null)
                    decompVertex = addJoin(decompVertex).getFirst();
                // only root is possible
                // (should never happen, since if lastVertex == null then decompVertex is a leaf)
                else
                    decompVertex = addJoin(getRoot()).getFirst();
            }

            // calculate nodes of nearest successor, which needs to be forgotten.
            Set<V> clique = new HashSet<V>(predecessors);
            clique.add(vertex);
            Set<V> toForget = new HashSet<V>(getMap().get(decompVertex));
            toForget.removeAll(clique);

            // first remove unnecessary nodes
            for (V forget : toForget) {
                decompVertex = addForget(forget, decompVertex);
            }
            // now add new node!
            decompVertex = addIntroduce(vertex, decompVertex);
            introduceMap.put(vertex, decompVertex);

        }
        leafClosure();

    }

    /**
     * Returns a map containing vertices from the {@code vertexOrder} mapped to their indices in
     * {@code vertexOrder}.
     *
     * @param vertexOrder a list with vertices.
     * @return a mapping of vertices from {@code vertexOrder} to their indices in
     *         {@code vertexOrder}.
     */
    private Map<V, Integer> getVertexInOrder()
    {
        Map<V, Integer> vertexInOrder = new HashMap<>(perfectOrder.size());
        int i = 0;
        for (V vertex : perfectOrder) {
            vertexInOrder.put(vertex, i++);
        }
        return vertexInOrder;
    }

    /**
     * Returns the predecessors of {@code vertex} in the order defined by {@code map}. More
     * precisely, returns those of {@code vertex}, whose mapped index in {@code map} is less then
     * the index of {@code vertex}.
     *
     * @param map defines the mapping of vertices in {@code graph} to their indices in order.
     * @param vertex the vertex whose predecessors in order are to be returned.
     * @return the predecessors of {@code vertex} in order defines by {@code map}.
     */
    private Set<V> getPredecessors(Map<V, Integer> map, V vertex)
    {
        Set<V> predecessors = new HashSet<>();
        Integer vertexPosition = map.get(vertex);
        Set<E> edges = graph.edgesOf(vertex);
        for (E edge : edges) {
            V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
            Integer destPosition = map.get(oppositeVertex);
            if (destPosition < vertexPosition) {
                predecessors.add(oppositeVertex);
            }
        }
        return predecessors;
    }
}
