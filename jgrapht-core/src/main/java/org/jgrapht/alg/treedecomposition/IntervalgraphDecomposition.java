package org.jgrapht.alg.treedecomposition;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.intervalgraph.*;
import org.jgrapht.intervalgraph.interval.*;

public class IntervalgraphDecomposition<T extends Comparable<T>>
{
    private Graph<Set<Interval<T>>, DefaultEdge> treeDecomposition = null;
    private Set<Interval<T>> currentVertex, root = null;
    private List<Interval<T>> startSort, endSort;
    
    public <V,E> IntervalgraphDecomposition(Graph<V,E> graph) 
    {
        throw new UnsupportedOperationException("Not yet implemented");
        //TODO
    }
    
    public IntervalgraphDecomposition(IntervalGraphInterface<T>  graph)
    {
        throw new UnsupportedOperationException("Not yet implemented");
        //TODO
    }
    
    public IntervalgraphDecomposition(List<Interval<T>> intervals)
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
        for(Interval<T> iv: startSort) {
            while(endSort.get(endIndex).getEnd().compareTo(
                    iv.getStart()) < 0) 
            {
                addForget(endSort.get(endIndex));
                endIndex++;
            }
            addIntroduce(iv);
        }
        while(endIndex < endSort.size()-1) {
            addForget(endSort.get(endIndex++));
        }
    }
    
    private void initTreeDecomposition() 
    {
        treeDecomposition = new DefaultDirectedGraph<Set<Interval<T>>,DefaultEdge>(DefaultEdge.class);
        root = new TreeSet<Interval<T>>();
        treeDecomposition.addVertex(root);
        currentVertex = root;
    }
    
    private void addIntroduce(Interval<T> vertex) 
    {
        Set<Interval<T>> nextVertex = new TreeSet<Interval<T>>(currentVertex);
        nextVertex.add(vertex);
        treeDecomposition.addVertex(nextVertex);
        treeDecomposition.addEdge(currentVertex, nextVertex);
        currentVertex = nextVertex;
    }
    
    private void addForget(Interval<T> vertex)
    {
        Set<Interval<T>> nextVertex = new TreeSet<Interval<T>>(currentVertex);
        nextVertex.remove(vertex);
        treeDecomposition.addVertex(nextVertex);
        treeDecomposition.addEdge(currentVertex, nextVertex);
        currentVertex = nextVertex;
    }

    
    public Graph<Set<Interval<T>>,DefaultEdge> getTreeDecomposition()
    {
        if(treeDecomposition == null) computeTreeDecomposition();
        return treeDecomposition;
    }
    
    public Set<Interval<T>> getRoot()
    {
        if(root == null) computeTreeDecomposition();
        return root;
    }
    
    
    
    private class IntervalVertexComparator implements Comparator<Interval<T>> {

        private boolean start = true;;
        
        public IntervalVertexComparator(boolean startKey) {
            start = startKey;
        }
        
        @Override
        public int compare(Interval<T> o1, Interval<T> o2)
        {
            if(start)
                return o1.getStart().compareTo(o2.getStart());
            else
                return o1.getEnd().compareTo(o2.getEnd());
        }
    }
}
