package org.jgrapht.alg.intervalgraph;

import org.jgrapht.*;
import org.jgrapht.traverse.*;

import java.util.*;

public class LexBreadthFirstSearch<V, E>
{
    
    /**
     * Performs a lexicographical BFS starting at {@code startingVertex}.
     *
     * @param graph the graph we want to perform LBFS on
     * @param startingVertex the starting vertex of the LBFS
     * @return an array of vertices representing the order in which the vertices were found
     */
    public List<V> lexBreadthFirstSearch(Graph<V, E> graph, V startingVertex)
    {
       ArrayList<V> result = new ArrayList<>(graph.vertexSet().size());
       LexBreadthFirstIterator<V,E> lbfIterator = new LexBreadthFirstIterator<>(graph);
       
       for(int i = 0; i < graph.vertexSet().size(); i++) {
           result.add(lbfIterator.next());
       }
       
       return result;
    }
    
    /**
     * Performs LBFS+ starting at {@code startingVertex} using the previous ordering {@code prevOrdering}.
     *
     * @param graph the graph we want to perform LBFS on
     * @param startingVertex the starting vertex of the LBFS
     * @param priority the priority of vertices for tiebreaking
     * @return an array of vertices representing the order in which the vertices were found
     */
    
    public List<V> lexBreadthFirstSearch(Graph<V, E> graph, V startingVertex, V[] priority)
    {
       ArrayList<V> result = new ArrayList<>(graph.vertexSet().size());
       LexBreadthFirstIterator<V,E> lbfIterator = new LexBreadthFirstIterator<>(graph, priority);
       
       for(int i = 0; i < graph.vertexSet().size(); i++) {
           result.add(lbfIterator.next());
       }
       
       return result;
    }
}
