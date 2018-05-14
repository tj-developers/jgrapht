package org.jgrapht.alg.treedecomposition;

import java.util.*;
import java.util.Map.*;

import org.jgrapht.*;
import org.jgrapht.alg.intervalgraph.IntervalGraphRecognizer;
import org.jgrapht.graph.*;
import org.jgrapht.intervalgraph.*;
import org.jgrapht.intervalgraph.interval.*;

/**
 * Class for calculating the nice Tree Decomposition for interval graphs
 *
 * @param <T> the value of the intervals of the interval graph
 *
 * @author Ira Justus Fesefeldt (PhoenixIra)
 * @since Mai 14, 2018
 */
public class IntervalgraphDecomposition<T extends Comparable<T>,V>
{
	//resulting forest of the decomposition
    private Graph<Integer, DefaultEdge> decomposition = null;
    
    //map from decomposition nodes to the interval sets
    private Map<Integer, Set<V>> decompositionIntervalMap = null;
    
    //the roots of the forest
    private Set<Integer> roots = null;
    
    //input to the algorithm, list of sorted intervals
    private List<Interval<T>> startSort, endSort;
    private Map<Interval<T>,V> intervalToVertexMap; 
    private Map<V,Interval<T>> vertexToIntervalMap;
    
    //helper attributes
    private Integer currentVertex = null;
    private Set<V> currentSet = null;
    
    /**
     * Private constructor for the factory methods, which changes the inputs
     * 
     * @param sortedByStartPoint the intervals sorted after the starting points
     * @param sortedByEndPoint the intervals sorted after the ending points
     * @param intervalToVertexMap maps intervals to the vertices of the graph (may be the same)
     */
    private IntervalgraphDecomposition(List<Interval<T>> sortedByStartPoint, 
                                       List<Interval<T>> sortedByEndPoint,
                                       Map<Interval<T>,V> intervalToVertexMap,
                                       Map<V,Interval<T>> vertexToIntervalMap)
    {
    	this.startSort = sortedByStartPoint;
    	this.endSort = sortedByEndPoint;
    	this.intervalToVertexMap = intervalToVertexMap;
    	this.vertexToIntervalMap = vertexToIntervalMap;
    }
    
    /**
     * Factory method for creating a nice tree decomposition for interval graphs.
     * This factory method uses general graphs and the IntervalGraphRecognizer to generate a list of intervals 
     * sorted by starting and ending points. 
     * The complexity of this method depends on the sorting algorithm in IntervalGraphRecognizer (probably quasi linear in the number of intervals)
     * 
     * @param graph the graph which should transformed to an interval graph and then into a corresponding nice tree decomposition
     * @return the algorithm for the computation of the nice tree decomposition, returns null if graph was no interval graph
     * @see IntervalGraphRecognizer
     */
    public static <V,E> IntervalgraphDecomposition<Integer,V> create(Graph<V,E> graph){
    	IntervalGraphRecognizer<V, E> recog = new IntervalGraphRecognizer<>(graph);
    	HashMap<V,Interval<Integer>> vertexToIntegerMap = new HashMap<>(recog.getVertexToIntervalMap().size());
    	for(Entry<V,IntervalVertexInterface<V, Integer>> entry : recog.getVertexToIntervalMap().entrySet())
    	    vertexToIntegerMap.put(entry.getKey(), entry.getValue().getInterval());
    	if(recog.isIntervalGraph())
    		return new IntervalgraphDecomposition<Integer,V>
    	                                                    (recog.getIntervalsSortedByStartingPoint(),
    		                                                 recog.getIntervalsSortedByStartingPoint(),
    		                                                 recog.getIntervalToVertexMap(),
    		                                                 vertexToIntegerMap);
    	else
    	    return null;
    }
    
    /**
     * Factory method for creating a nice tree decomposition for interval graphs.
     * This factory method extracts the intervals from the interval graph and uses them as an input for the computation.
     * The complexity of this method depends on the sorting algorithm of ArrayList (probably O(|V| log(|V|))
     * 
     * @param intervalGraph the input for which a nice tree decomposition should be computed
     * @return the algorithm for the computation of the nice tree decomposition
     * @see ArrayList#sort(Comparator)
     */
    public static <V extends IntervalVertexInterface<VertexType, T>, E, VertexType, T extends Comparable<T>> 
    												IntervalgraphDecomposition<T,V> create(IntervalGraph<V,E,VertexType,T>  intervalGraph)
    {
        Set<V> vertexSet = intervalGraph.vertexSet();
        List<Interval<T>> intervals = new ArrayList<Interval<T>>(vertexSet.size());
        Map<Interval<T>,V> intervalToVertexMap = new HashMap<>(vertexSet.size());
        Map<V,Interval<T>> vertexToIntervalMap = new HashMap<>(vertexSet.size());
        for(V iv : vertexSet) {
        	intervals.add(iv.getInterval());
        	intervalToVertexMap.put(iv.getInterval(), iv);
        	vertexToIntervalMap.put(iv, iv.getInterval());
        }
        ArrayList<Interval<T>> startSort = new ArrayList<>(intervals);
        ArrayList<Interval<T>> endSort = new ArrayList<>(intervals);
        startSort.sort(Interval.<T>getStartingComparator());
        endSort.sort(Interval.<T>getEndingComparator());
        return new IntervalgraphDecomposition<T,V>(startSort, endSort,intervalToVertexMap, vertexToIntervalMap);
    }
    
    /**
     * Factory method for creating a nice tree decomposition for interval graphs.
     * This factory method needs to lists of intervals, the first sorted after starting points, the second after ending points
     * The complexity of this method is linear in the number of intervals
     * 
     * @param sortedByStartPoint a list of all intervals sorted by the starting point
     * @param sortedByEndPoint a list of all intervals sorted by the ending point
     * @return the algorithm for the computation of the nice tree decomposition
     */
    public static <T extends Comparable<T>> IntervalgraphDecomposition<T,Interval<T>> create(List<Interval<T>> sortedByStartPoint, 
    																			List<Interval<T>> sortedByEndPoint)
    {
        HashMap<Interval<T>,Interval<T>> identity = new HashMap<>(sortedByStartPoint.size());
        for(Interval<T> interval : sortedByStartPoint)
            identity.put(interval, interval);
    	return new IntervalgraphDecomposition<T,Interval<T>>(new ArrayList<Interval<T>>(sortedByStartPoint),
    										  	   new ArrayList<Interval<T>>(sortedByEndPoint),
    										  	   identity, identity);
    }
    
    /**
     * Factory method for creating a nice tree decomposition for interval graphs.
     * This factory method needs to lists of intervals, which then is sorted by ArrayList.sort().
     * The complexity of this method depends on the sorting Algorithm of ArrayList (probably O(|List| log(|List|))
     * 
     * @param intervals the (unsorted) list of all intervals
     * @return the algorithm for the computation of the nice tree decomposition
     * @see ArrayList#sort(Comparator)
     */
    public static <T extends Comparable<T>> IntervalgraphDecomposition<T,Interval<T>> create(List<Interval<T>> intervals)
    {
        ArrayList<Interval<T>> startSort = new ArrayList<>(intervals);
        ArrayList<Interval<T>> endSort = new ArrayList<>(intervals);
        startSort.sort(Interval.<T>getStartingComparator());
        endSort.sort(Interval.<T>getEndingComparator());
        HashMap<Interval<T>,Interval<T>> identity = new HashMap<>(startSort.size());
        for(Interval<T> interval : startSort)
            identity.put(interval, interval);
    	return new IntervalgraphDecomposition<T,Interval<T>>(startSort, endSort,identity,identity);
    }
    
    /**
     * Main method for computing the nice tree decomposition
     */
    private void computeNiceDecomposition() 
    {
    	//already computed
        if(decomposition != null)
            return;
        
        //create all objects and set new root
        initDecomposition();
        
        int endIndex=0;
        Interval<T> last = null;
        
        //as long as intervals remain
        for(Interval<T> current : startSort)
        {
        	//first forget until you need to introduce new nodes
            while(endSort.get(endIndex).getEnd().compareTo(
                    current.getStart()) < 0) 
            {
                addForget(endSort.get(endIndex));
                endIndex++;
            }
            //  root or leaf node AND last one had no successor (i.e. end of last is before start of current)
            if(currentSet.size() != 0 || last == null)
            {
            	//no root node, so introduce
            	addIntroduce(current);
            }
            else if(last.getEnd().compareTo(
            		current.getStart()) < 0)
            {
            	//root node!
            	addNewRoot();
            	addIntroduce(current);
            }
            //save last node for root detection
            last = current;
        }
        //add the last forget nodes
        while(endIndex < endSort.size()-1) {
            addForget(endSort.get(endIndex++));
        }
    }
    
    /**
     * Method for initializing the decomposition
     */
    private void initDecomposition() 
    {
    	//creating objects
        decomposition = new DefaultDirectedGraph<Integer,DefaultEdge>(DefaultEdge.class);
        decompositionIntervalMap = new HashMap<Integer,Set<V>>();
        roots = new HashSet<Integer>();
        
        //create root
        currentVertex = 0;
        roots.add(currentVertex);
        currentSet = new HashSet<>();
        decompositionIntervalMap.put(currentVertex,currentSet);
        decomposition.addVertex(currentVertex);
    }
    
    /**
     * Method for adding a new root
     */
    private void addNewRoot()
    {
    	Set<V> nextVertex = new HashSet<>();
    	currentSet = nextVertex;
    	decomposition.addVertex(currentVertex+1);
    	roots.add(currentVertex+1);
    	decompositionIntervalMap.put(currentVertex+1, nextVertex);
    	
    	//new integer for next vertex
    	currentVertex++;
    }
    
    /**
     * Method for adding introducing nodes
     * @param vertex the vertex, which is introduced
     */
    private void addIntroduce(Interval<T> interval) 
    {
        Set<V> nextVertex = new HashSet<>(currentSet);
        nextVertex.add(intervalToVertexMap.get(interval));
        currentSet = nextVertex;
        decomposition.addVertex(currentVertex+1);
        decomposition.addEdge(currentVertex, currentVertex+1);
        decompositionIntervalMap.put(currentVertex+1, nextVertex);
        
        //new integer for next vertex
        currentVertex++;
    }
    
    /**
     * method for adding forget nodes
     * @param vertex the vertex, which is introduced
     */
    private void addForget(Interval<T> interval)
    {
        Set<V> nextVertex = new HashSet<>(currentSet);
        nextVertex.remove(intervalToVertexMap.get(interval));
        currentSet = nextVertex;
        decomposition.addVertex(currentVertex+1);
        decomposition.addEdge(currentVertex, currentVertex+1);
        decompositionIntervalMap.put(currentVertex+1, nextVertex);
        
        //new integer for next vertex
        currentVertex++;
    }

    /**
     * getter for the decomposition as an directed graph
     * 
     * @return the computed decomposition
     */
    public Graph<Integer,DefaultEdge> getDecomposition()
    {
        if(decomposition == null) computeNiceDecomposition();
        return decomposition;
    }
    
    /**
     *  getter for the map from integer nodes of the decomposition to the intervals of the interval graph
     * 
     * @return a nodes to interval map
     */
    public Map<Integer,Set<V>> getMap(){
    	if(decompositionIntervalMap == null) computeNiceDecomposition();
    	return decompositionIntervalMap;
    }
    
    /**
     * getter for all roots of the decomposition
     * 
     * @return a set of roots
     */
    public Set<Integer> getRoot()
    {
        if(roots == null) computeNiceDecomposition();
        return roots;
    }
    
    /**
     * getter for interval to vertex Map
     * 
     * @return a map that maps intervals to vertices
     */
    public Map<Interval<T>,V> getIntervalToVertexMap(){
        return intervalToVertexMap;
    }
    
    /**
     * getter for vertex to interval map
     * 
     * @return a map that maps vertices to intervals
     */
    public Map<V,Interval<T>> getVertexToIntervalMap(){
        return vertexToIntervalMap;
    }
}
