package org.jgrapht.decomposition;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.cycle.*;

public class ChordalityNiceDecomposition<V,E>
    extends
    NiceDecompositionBuilder<V>
{
    //the chordal graph
    Graph<V,E> graph;
    
    //the perfect eliminiation order of graph
    List<V> perfectOrder;
    
    //another representation of the perfect eliminiation order of graph
    Map<V, Integer> vertexInOrder;
    
    /**
     * Factory method for the nice decomposition builder of chordal graphs. Returns null, if the graph is not chordal.
     * 
     * 
     * @param graph
     * @return a nice decomposition builder for the graph if the graph was chordal, else null
     */
    public static <V,E> ChordalityNiceDecomposition<V, E> create(Graph<V,E> graph)
    {
        ChordalityInspector<V, E> inspec = new ChordalityInspector<V,E>(graph);
        if(!inspec.isChordal()) 
            return null;
        else
            return new ChordalityNiceDecomposition<>(graph, inspec.getSearchOrder());
        
    }
    
    /**
     * Creates a nice decomposition builder for chordal graphs. 
     * @param graph the chordal graph
     * @param perfectOrder the perfect elimination order of graph
     */
    private ChordalityNiceDecomposition(Graph<V,E> graph, List<V> perfectOrder)
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
    private void computeNiceDecomposition() {

        // init
        Map<V, Integer> introduceMap = new HashMap<V, Integer>(graph.vertexSet().size());
        V vertex = null;
        Integer decompVertex = getRoot();

        // iterate from last to first
        for (int i = perfectOrder.size() - 1; i >= 0; i--) {
            vertex = perfectOrder.get(i);
            Set<V> successors = getSuccessors(vertex);
            // calculate nearest successors according to order
            V lastVertex = null;
            for (V successor : successors) {
                if (lastVertex == null)
                    lastVertex = successor;
                if (vertexInOrder.get(successor) < vertexInOrder.get(lastVertex))
                    lastVertex = successor;
            }

            // create a join node for the nearest successor
            Integer oldDecompVertex = -1;
            if (lastVertex != null)
                oldDecompVertex = introduceMap.get(lastVertex);

            // not a leaf node, thus create join node
            if (Graphs.vertexHasSuccessors(getDecomposition(), decompVertex)) {
                // found some intersection!
                if (lastVertex != null)
                    decompVertex = addJoin(oldDecompVertex).getFirst();

                // only root is possible (i.e. it is unconnected)
                if (lastVertex == null)
                    decompVertex =
                        addJoin(getRoot()).getFirst();
            }

            // calculate nodes of nearest successor, which needs to be forgotten.
            Set<V> clique = new HashSet<V>(successors);
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
     * Returns the successors of {@code vertex} in the order defined by {@code map}. More precisely,
     * returns those of {@code vertex}, whose mapped index in {@code map} is greater then the index of {@code vertex}.
     *
     * @param vertexInOrder defines the mapping of vertices in {@code graph} to their indices in order.
     * @param vertex        the vertex whose successors in order are to be returned.
     * @return the successors of {@code vertex} in order defines by {@code map}.
     */
    private Set<V> getSuccessors(V vertex) {
        Set<V> successors = new HashSet<>();
        Integer vertexPosition = vertexInOrder.get(vertex);
        Set<E> edges = graph.edgesOf(vertex);
        for (E edge : edges) {
            V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
            Integer destPosition = vertexInOrder.get(oppositeVertex);
            if (destPosition > vertexPosition) {
                successors.add(oppositeVertex);
            }
        }
        return successors;
    }
}
