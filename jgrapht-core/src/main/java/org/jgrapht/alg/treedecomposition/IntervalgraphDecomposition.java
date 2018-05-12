package org.jgrapht.alg.treedecomposition;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.intervalgraph.*;
import org.jgrapht.intervalgraph.interval.*;

public class IntervalgraphDecomposition<T extends Comparable<T>>
{
    private Graph<Integer, DefaultEdge> decomposition = null;
    private Map<Integer, Set<Interval<T>>> decompositionMap = null;
    private Integer currentVertex = null;
    private Set<Integer> roots = null;
    private Set<Interval<T>> currentIntervalSet = null;
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
        startSort.sort(Interval.<T>getStartingComparator());
        endSort.sort(Interval.<T>getEndingComparator());
    }
    
    private void computeTreeDecomposition() 
    {
        if(decomposition != null)
            return;
        
        initTreeDecomposition();
        
        int endIndex=0;
        Interval<T> last = null;
        for(Interval<T> current : startSort)
        {
            while(endSort.get(endIndex).getEnd().compareTo(
                    current.getStart()) < 0) 
            {
                addForget(endSort.get(endIndex));
                endIndex++;
            }
            //  root or leaf node AND last one had no successor (i.e. end of last is before start of current)
            if(currentIntervalSet.size() != 0 || last == null)
            	addIntroduce(current);
            else if(last.getEnd().compareTo(
            		current.getStart()) < 0)
            {
            	addNewRoot();
            	addIntroduce(current);
            }
            last = current;
        }
        while(endIndex < endSort.size()-1) {
            addForget(endSort.get(endIndex++));
        }
    }
    
    private void initTreeDecomposition() 
    {
        decomposition = new DefaultDirectedGraph<Integer,DefaultEdge>(DefaultEdge.class);
        decompositionMap = new HashMap<Integer,Set<Interval<T>>>();
        roots = new HashSet<Integer>();
        currentVertex = 0;
        roots.add(currentVertex);
        currentIntervalSet = new HashSet<Interval<T>>();
        decompositionMap.put(currentVertex,currentIntervalSet);
        decomposition.addVertex(currentVertex);
    }
    
    private void addNewRoot()
    {
    	Set<Interval<T>> nextVertex = new HashSet<Interval<T>>();
    	currentIntervalSet = nextVertex;
    	decomposition.addVertex(currentVertex+1);
    	roots.add(currentVertex+1);
    	decompositionMap.put(currentVertex+1, nextVertex);
    	currentVertex++;
    }
    
    private void addIntroduce(Interval<T> vertex) 
    {
        Set<Interval<T>> nextVertex = new HashSet<Interval<T>>(currentIntervalSet);
        nextVertex.add(vertex);
        currentIntervalSet = nextVertex;
        decomposition.addVertex(currentVertex+1);
        decomposition.addEdge(currentVertex, currentVertex+1);
        decompositionMap.put(currentVertex+1, nextVertex);
        currentVertex++;
    }
    
    private void addForget(Interval<T> vertex)
    {
        Set<Interval<T>> nextVertex = new HashSet<Interval<T>>(currentIntervalSet);
        nextVertex.remove(vertex);
        currentIntervalSet = nextVertex;
        decomposition.addVertex(currentVertex+1);
        decomposition.addEdge(currentVertex, currentVertex+1);
        decompositionMap.put(currentVertex+1, nextVertex);
        currentVertex++;
    }

    
    public Graph<Integer,DefaultEdge> getTreeDecomposition()
    {
        if(decomposition == null) computeTreeDecomposition();
        return decomposition;
    }
    
    public Map<Integer,Set<Interval<T>>> getMap(){
    	if(decompositionMap == null) computeTreeDecomposition();
    	return decompositionMap;
    }
    
    public Set<Integer> getRoot()
    {
        if(roots == null) computeTreeDecomposition();
        return roots;
    }
}
