package org.jgrapht.alg.treedecomposition;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.intervalgraph.*;
import org.jgrapht.intervalgraph.interval.*;

public class IntervalgraphDecomposition<V,E,T extends Comparable<T>>
    implements TreeDecomposition<V,E,T>
{
    private Graph<Set<V>,DefaultEdge> treeDecomposition = null;
    private Set<V> currentVertex = null;
    private List<IntervalVertex<V,T>> startSort, endSort;
    
    public IntervalgraphDecomposition(Graph<V,E> graph) 
    {
        //TODO
    }
    
    public IntervalgraphDecomposition(IntervalGraph<IntervalVertexInterface<V,T>,E>  graph)
    {
        //TODO
    }
    
    public IntervalgraphDecomposition(List<IntervalVertex<V,T>> intervals)
    {
        startSort = new ArrayList<>(intervals);
        endSort = new ArrayList<>(intervals);
        startSort.sort(new IntervalVertexComparator(true));
        endSort.sort(new IntervalVertexComparator(false));
    }
    
    private void computeTreeDecomposition() 
    {
        if(treeDecomposition != null)
            return;
        
        initTreeDecomposition();
        
        int endIndex=0;
        for(IntervalVertex<V,T> iv: startSort) {
            while(endSort.get(endIndex).getInterval().getEnd().compareTo(
                    iv.getInterval().getStart()) < 0) 
            {
                addForget(endSort.get(endIndex).getVertex());
                endIndex++;
            }
            addIntroduce(iv.getVertex());
        }
    }
    
    private void initTreeDecomposition() 
    {
        treeDecomposition = new DefaultUndirectedGraph<Set<V>,DefaultEdge>(DefaultEdge.class);
        Set<V> root = new HashSet<V>(0);
        treeDecomposition.addVertex(root);
        currentVertex = root;
    }
    
    private void addIntroduce(V vertex) 
    {
        Set<V> nextVertex = new HashSet<V>(currentVertex);
        nextVertex.add(vertex);
        treeDecomposition.addVertex(nextVertex);
        treeDecomposition.addEdge(currentVertex, nextVertex);
        currentVertex = nextVertex;
    }
    
    private void addForget(V vertex)
    {
        Set<V> nextVertex = new HashSet<V>(currentVertex);
        nextVertex.remove(vertex);
        treeDecomposition.addVertex(nextVertex);
        treeDecomposition.addEdge(currentVertex, nextVertex);
        currentVertex = nextVertex;
    }

    @Override
    public Graph<Set<V>,DefaultEdge> getTreeDecomposition()
    {
        computeTreeDecomposition();
        return treeDecomposition;
    }
    
    
    
    private class IntervalVertexComparator implements Comparator<IntervalVertex<V,T>> {

        private boolean start = true;;
        
        public IntervalVertexComparator(boolean startKey) {
            start = startKey;
        }
        
        @Override
        public int compare(IntervalVertex<V, T> o1, IntervalVertex<V, T> o2)
        {
            if(start)
                return o1.getInterval().getStart().compareTo(o2.getInterval().getStart());
            else
                return o1.getInterval().getEnd().compareTo(o2.getInterval().getEnd());
        }

    }
}
