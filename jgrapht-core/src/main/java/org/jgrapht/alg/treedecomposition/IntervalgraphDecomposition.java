package org.jgrapht.alg.treedecomposition;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.intervalgraph.*;
import org.jgrapht.intervalgraph.interval.*;

public class IntervalgraphDecomposition<V,T extends Comparable<T>>
{
    private Graph<Set<IntervalVertex<V,T>>, DefaultEdge> treeDecomposition = null;
    private Set<IntervalVertex<V,T>> currentVertex = null;
    private List<IntervalVertex<V,T>> startSort, endSort;
    
    public IntervalgraphDecomposition(Graph<V,Object> graph) 
    {
        throw new UnsupportedOperationException("Not yet implemented");
        //TODO
    }
    
    public IntervalgraphDecomposition(IntervalGraphInterface<T>  graph)
    {
        throw new UnsupportedOperationException("Not yet implemented");
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
                addForget(endSort.get(endIndex));
                endIndex++;
            }
            addIntroduce(iv);
        }
    }
    
    private void initTreeDecomposition() 
    {
        treeDecomposition = new DefaultUndirectedGraph<Set<IntervalVertex<V,T>>,DefaultEdge>(DefaultEdge.class);
        Set<IntervalVertex<V,T>> root = new HashSet<IntervalVertex<V,T>>(0);
        treeDecomposition.addVertex(root);
        currentVertex = root;
    }
    
    private void addIntroduce(IntervalVertex<V,T> vertex) 
    {
        Set<IntervalVertex<V,T>> nextVertex = new HashSet<IntervalVertex<V,T>>(currentVertex);
        nextVertex.add(vertex);
        treeDecomposition.addVertex(nextVertex);
        treeDecomposition.addEdge(currentVertex, nextVertex);
        currentVertex = nextVertex;
    }
    
    private void addForget(IntervalVertex<V,T> vertex)
    {
        Set<IntervalVertex<V,T>> nextVertex = new HashSet<IntervalVertex<V,T>>(currentVertex);
        nextVertex.remove(vertex);
        treeDecomposition.addVertex(nextVertex);
        treeDecomposition.addEdge(currentVertex, nextVertex);
        currentVertex = nextVertex;
    }

    
    public Graph<Set<IntervalVertex<V,T>>,DefaultEdge> getTreeDecomposition()
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
