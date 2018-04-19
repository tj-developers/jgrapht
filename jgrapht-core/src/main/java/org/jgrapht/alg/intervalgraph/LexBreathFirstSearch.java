package org.jgrapht.alg.intervalgraph;

import org.jgrapht.*;

import java.util.*;

public class LexBreathFirstSearch<V, E>
{
    
    /**
     * Performs a lexicographical BFS starting at {@code startingVertex}.
     *
     * @param graph the graph we want to perform LBFS on
     * @param startingVertex the starting vertex of the LBFS
     * @return an array of vertices representing the order in which the vertices were found
     */
    public V[] lexBreathFirstSearch(Graph<V, E> graph, V startingVertex)
    {
        int n = graph.vertexSet().size();
        ArrayList<V> ordering = new ArrayList<V>();
        
        String maxLabel = Integer.toString(n);
        
        HashMap<String, ArrayList<V>> labelToVertexMap = new HashMap<String, ArrayList<V>>();
        HashMap<V, String> vertexToLabelMap = new HashMap<V, String>();
       
        // initialize labels to be empty ...
        ArrayList<V> initialVertexList = new ArrayList<V>(graph.vertexSet());
        initialVertexList.remove(startingVertex);
        
        labelToVertexMap.put("", initialVertexList);
        
        for(V vertex : initialVertexList) {
            vertexToLabelMap.put(vertex, "");
        }
        
        // ... except for the starting vertex
        ArrayList<V> startingVertexList = new ArrayList<V>();
        startingVertexList.add(startingVertex);
        
        labelToVertexMap.put(maxLabel, startingVertexList);
        vertexToLabelMap.put(startingVertex, maxLabel);
        
        for(int i = n; i >= 1; i--) {
            // pick unvisited vertex v with largest label
            ArrayList<V> candidates = labelToVertexMap.get(maxLabel);
            V currentVertex = candidates.get(0);
            
            // set index of v to be n + 1 - i
            ordering.add(currentVertex);
            
            // for every unvisited neighbor w of v, append i to label of w
            for(V neighbor : Graphs.neighborListOf(graph, currentVertex)) {
                String oldLabel = vertexToLabelMap.get(neighbor);
                String newLabel = oldLabel + Integer.toString(i);
                
                // remove neighbor from previous list
                ArrayList<V> oldList = labelToVertexMap.get(oldLabel);
                oldList.remove(neighbor);
                
                // if there are no vertices left in this list, delete the entry
                if(oldList.isEmpty()) {
                    labelToVertexMap.remove(oldLabel);
                } else {
                    // else we replace the list with its updated version 
                    labelToVertexMap.replace(oldLabel, oldList);
                }
                
                // now add neighbor to the list of the new label
                if(labelToVertexMap.containsKey(newLabel)) {
                    ArrayList<V> newList = labelToVertexMap.get(newLabel);
                    newList.add(neighbor);
                    labelToVertexMap.replace(newLabel, newList);
                } else {
                    ArrayList<V> newList = new ArrayList<V>();
                    newList.add(neighbor);
                    labelToVertexMap.put(newLabel, newList);
                }
                
                // associate neighbor with its new label
                vertexToLabelMap.replace(neighbor, newLabel);
            }
            
            // mark current vertex as visited, i.e. remove its entries from the maps
            vertexToLabelMap.remove(currentVertex);
            candidates.remove(currentVertex);
            labelToVertexMap.replace(maxLabel, candidates);
        }
    
        // return ordering -- thanks Java for not being able to create an array from a list with generic types for some reason
        V[] orderingArray = null;
        orderingArray = ordering.toArray(orderingArray);
        
        return orderingArray;
    }
    
    /**
     * Performs LBFS+ starting at {@code startingVertex} using the previous ordering {@code prevOrdering}.
     *
     * @param graph the graph we want to perform LBFS on
     * @param startingVertex the starting vertex of the LBFS
     * @param prevOrdering the previous LBFS ordering given as an array
     * @return an array of vertices representing the order in which the vertices were found
     */
    public V[] lexBreathFirstSearch(Graph<V, E> graph, V startingVertex, V[] prevOrdering)
    {
        LBFSComparator comp = new LBFSComparator(prevOrdering);
        
        int n = graph.vertexSet().size();
        ArrayList<V> ordering = new ArrayList<V>();
        
        String maxLabel = Integer.toString(n);
        
        HashMap<String, ArrayList<V>> labelToVertexMap = new HashMap<String, ArrayList<V>>();
        HashMap<V, String> vertexToLabelMap = new HashMap<V, String>();
       
        // initialize labels to be empty ...
        ArrayList<V> initialVertexList = new ArrayList<V>(graph.vertexSet());
        initialVertexList.remove(startingVertex);
        
        labelToVertexMap.put("", initialVertexList);
        
        for(V vertex : initialVertexList) {
            vertexToLabelMap.put(vertex, "");
        }
        
        // ... except for the starting vertex
        ArrayList<V> startingVertexList = new ArrayList<V>();
        startingVertexList.add(startingVertex);
        
        labelToVertexMap.put(maxLabel, startingVertexList);
        vertexToLabelMap.put(startingVertex, maxLabel);
        
        for(int i = n; i >= 1; i--) {
            // pick unvisited vertex v with largest label
            ArrayList<V> candidates = labelToVertexMap.get(maxLabel);
            
            // LBFS+: pick the vertex with the lex. largest label which appears last in the previous ordering
            candidates.sort(comp.reversed());
            V currentVertex = candidates.get(0);
            
            // set index of v to be n + 1 - i
            ordering.add(currentVertex);
            
            // for every unvisited neighbor w of v, append i to label of w
            for(V neighbor : Graphs.neighborListOf(graph, currentVertex)) {
                String oldLabel = vertexToLabelMap.get(neighbor);
                String newLabel = oldLabel + Integer.toString(i);
                
                // remove neighbor from previous list
                ArrayList<V> oldList = labelToVertexMap.get(oldLabel);
                oldList.remove(neighbor);
                
                // if there are no vertices left in this list, delete the entry
                if(oldList.isEmpty()) {
                    labelToVertexMap.remove(oldLabel);
                } else {
                    // else we replace the list with its updated version 
                    labelToVertexMap.replace(oldLabel, oldList);
                }
                
                // now add neighbor to the list of the new label
                if(labelToVertexMap.containsKey(newLabel)) {
                    ArrayList<V> newList = labelToVertexMap.get(newLabel);
                    newList.add(neighbor);
                    labelToVertexMap.replace(newLabel, newList);
                } else {
                    ArrayList<V> newList = new ArrayList<V>();
                    newList.add(neighbor);
                    labelToVertexMap.put(newLabel, newList);
                }
                
                // associate neighbor with its new label
                vertexToLabelMap.replace(neighbor, newLabel);
            }
            
            // mark current vertex as visited, i.e. remove its entries from the maps
            vertexToLabelMap.remove(currentVertex);
            candidates.remove(currentVertex);
            labelToVertexMap.replace(maxLabel, candidates);
        }
    
        // return ordering -- thanks Java for not being able to create an array from a list with generic types for some reason
        V[] orderingArray = null;
        orderingArray = ordering.toArray(orderingArray);
        
        return orderingArray;
    }
    
    class LBFSComparator implements Comparator<V>
    {
        private final V[] prevOrdering;
        
        public LBFSComparator(V[] prevOrdering) {
            this.prevOrdering = prevOrdering;
        }
        
        // apparantly Java has no indexOf method...
        private int indexOf(V vertex) throws NoSuchElementException{
            for(int index = 0; index < prevOrdering.length; index++) {
                if(prevOrdering[index].equals(vertex)) {
                    return index;
                }
            }
            throw new NoSuchElementException("Previous ordering is on a different vertex set.");
        }
        
        @Override
        public int compare(V vertex1, V vertex2)
        {
            int index1 = indexOf(vertex1);
            int index2 = indexOf(vertex2);
            
            return (index1 - index2);
        }
    }
}
