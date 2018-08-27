package org.jgrapht.alg.decomposition;

import java.util.*;
import java.util.function.Supplier;

import org.jgrapht.*;
import org.jgrapht.alg.interval.IntervalGraphRecognizer;
import org.jgrapht.graph.interval.Interval;
import org.jgrapht.graph.interval.IntervalVertexPair;
import org.jgrapht.util.SupplierUtil;

/**
 * Class for calculating the nice Tree Decomposition for interval graphs
 *
 * @param <T> the value type of the intervals of the interval graph
 * @param <V> the type of the nodes of the input graph
 *
 * @author Ira Justus Fesefeldt (PhoenixIra)
 * @since Mai 14, 2018
 */
public class IntervalNicePathDecomposition<T extends Comparable<T>, V> extends NiceTreeDecompositionBase<V>
{
    // input to the algorithm, list of sorted intervals
    private List<Interval<T>> startSort, endSort;
    private Map<Interval<T>, V> intervalToVertexMap;
    private Map<V, IntervalVertexPair<V, T>> vertexToIntervalMap;

    // helper attributes
    private Integer currentNode = null;

    /**
     * Private constructor for the factory methods, which changes the inputs
     * 
     * @param sortedByStartPoint the intervals sorted after the starting points
     * @param sortedByEndPoint the intervals sorted after the ending points
     * @param intervalToVertexMap maps intervals to the vertices of the graph (may be the same)
     */
    private IntervalNicePathDecomposition(
        List<Interval<T>> sortedByStartPoint, List<Interval<T>> sortedByEndPoint,
        Map<Interval<T>, V> intervalToVertexMap, Map<V, IntervalVertexPair<V, T>> vertexToInterval)
    {
        super();
        
        this.startSort = sortedByStartPoint;
        this.endSort = sortedByEndPoint;
        this.intervalToVertexMap = intervalToVertexMap;
        this.vertexToIntervalMap = vertexToInterval;
    }

    /**
     * Factory method for creating a nice tree decomposition for interval graphs. This factory
     * method uses general graphs and the IntervalGraphRecognizer to generate a list of intervals
     * sorted by starting and ending points. The complexity of this method is linear (O(|V|+|E|)).
     * 
     * @param graph the graph which should transformed to an interval graph and then into a
     *        corresponding nice tree decomposition
     * @param <V> the vertex type of the graph
     * @param <E> the edge type of the graph
     * @return the algorithm for the computation of the nice tree decomposition
     * @throws IllegalArgumentException if the graph is not an interval graph
     * @see IntervalGraphRecognizer
     */
    public static <V, E> IntervalNicePathDecomposition<Integer, V> create(Graph<V, E> graph)
    {
        IntervalGraphRecognizer<V, E> recog = new IntervalGraphRecognizer<>(graph);
        
        if (recog.isIntervalGraph()) {
            return new IntervalNicePathDecomposition<Integer, V>(
                recog.getIntervalsSortedByStartingPoint(),
                recog.getIntervalsSortedByEndingPoint(), 
                recog.getIntervalToVertexMap(),
                recog.getVertexToIntervalMap());
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Factory method for creating a nice tree decomposition for interval graphs. This factory
     * method needs to lists of intervals, the first sorted after starting points, the second after
     * ending points. Every interval has to be unique.
     * The complexity of this method is linear (O(|V|+|E|))
     * 
     * @param sortedByStartPoint a list of all intervals sorted by the starting point
     * @param sortedByEndPoint a list of all intervals sorted by the ending point
     * @param supplier a supplier which yields the new generated vertices of the interval graph
     * @param <T> the value of the intervals
     * @param <V> the values of the vertices of the interval graph
     * @return the algorithm for the computation of the nice tree decomposition
     */
    public static <T extends Comparable<T>, V> IntervalNicePathDecomposition<T, V> create(
        List<Interval<T>> sortedByStartPoint, List<Interval<T>> sortedByEndPoint, Supplier<V> supplier)
    {
        HashMap<Interval<T>,V> intervalToVertex = new HashMap<>(sortedByStartPoint.size());
        HashMap<V, IntervalVertexPair<V, T>> vertexToInterval = new HashMap<>(sortedByStartPoint.size());
        for (Interval<T> interval : sortedByStartPoint) {
            V newVertex = supplier.get();
            intervalToVertex.put(interval,newVertex);
            vertexToInterval.put(newVertex, IntervalVertexPair.of(newVertex, interval));
        }
        return new IntervalNicePathDecomposition<T, V>(
            new ArrayList<Interval<T>>(sortedByStartPoint),
            new ArrayList<Interval<T>>(sortedByEndPoint), 
            intervalToVertex, 
            vertexToInterval);
    }

    /**
     * Factory method for creating a nice tree decomposition for interval graphs. This factory
     * method needs to lists of intervals, which then is sorted by ArrayList.sort(). 
     * Every interval has to be unique. The complexity of this method depends on the sorting Algorithm of 
     * ArrayList (O(|List| log(|List|))
     * 
     * @param intervals the (unsorted) list of all intervals
     * @param supplier a supplier which yields the new generated vertices of the interval graph
     * @param <T> the values of the intervals
     * @param <V> the values of the vertices of the interval graph
     * @return the algorithm for the computation of the nice tree decomposition
     * @see ArrayList#sort(Comparator)
     */
    public static <T extends Comparable<T>, V> IntervalNicePathDecomposition<T, V> create(
        List<Interval<T>> intervals, Supplier<V> supplier)
    {
        ArrayList<Interval<T>> startSort = new ArrayList<>(intervals);
        ArrayList<Interval<T>> endSort = new ArrayList<>(intervals);
        startSort.sort(Interval.<T> getStartingComparator());
        endSort.sort(Interval.<T> getEndingComparator());
        return create(startSort, endSort, supplier);
    }

    /**
     * Main method for computing the nice tree decomposition
     */
    protected void computeLazyNiceTreeDecomposition()
    {

        Iterator<Interval<T>> iteratorStart = startSort.iterator();
        
        if(!iteratorStart.hasNext()) {
            return;
        }
        
        // create all objects and set new root
        currentNode = addRootNode(intervalToVertexMap.get(iteratorStart.next()));

        int endIndex = 0;

        // as long as intervals remain
        while (iteratorStart.hasNext()) {
            Interval<T> currentStart = iteratorStart.next();
            // first introduce until you need to forget new nodes
            while (endSort.get(endIndex).getEnd().compareTo(currentStart.getStart()) < 0) {
                V introduceElement = intervalToVertexMap.get(endSort.get(endIndex));
                currentNode = addIntroduceNode(introduceElement, currentNode);
                endIndex++;
            }            
            V forgetElement = intervalToVertexMap.get(currentStart);
            currentNode = addForgetNode(forgetElement, currentNode);
        }
        // add the last introduce nodes
        while (endIndex < endSort.size()-1) {
            V introduceElement = intervalToVertexMap.get(endSort.get(endIndex++));
            currentNode = addIntroduceNode(introduceElement, currentNode);
        }
    }

    /**
     * getter for interval to vertex Map
     * 
     * @return a map that maps intervals to vertices
     */
    public Map<Interval<T>, V> getIntervalToVertexMap()
    {
        return intervalToVertexMap;
    }

    /**
     * getter for vertex to interval map
     * 
     * @return a map that maps vertices to intervals
     */
    public Map<V, IntervalVertexPair<V, T>> getVertexToIntervalMap()
    {
        return vertexToIntervalMap;
    }
}

