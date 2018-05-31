package org.jgrapht.decomposition;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.*;

public class ChordalityNiceDecomposition<V>
    extends
    NiceDecompositionBuilder<V>
{
    
    Graph<V,Object> graph;
    
    public ChordalityNiceDecomposition(Graph<V,Object> graph)
    {
        super();
        this.graph = graph;
    }


    /**
     * Returns the successors of {@code vertex} in the order defined by {@code map}. More precisely,
     * returns those of {@code vertex}, whose mapped index in {@code map} is greater then the index of {@code vertex}.
     *
     * @param vertexInOrder defines the mapping of vertices in {@code graph} to their indices in order.
     * @param vertex        the vertex whose successors in order are to be returned.
     * @return the successors of {@code vertex} in order defines by {@code map}.
     */
    private Set<V> getSuccessors(Map<V, Integer> vertexInOrder, V vertex) {
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
