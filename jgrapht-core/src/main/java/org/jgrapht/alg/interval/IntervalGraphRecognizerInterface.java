package org.jgrapht.alg.interval;

import org.jgrapht.graph.interval.Interval;
import org.jgrapht.graph.interval.IntervalVertexPair;

import java.util.List;
import java.util.Map;

/**
 * An Interface for IntervalGraph Recognizing algorithms
 * @author Ira Justus Fesefeldt (PhoenixIra)
 *
 * @param <V> vertex type of the graph
 */
public interface IntervalGraphRecognizerInterface<V>
{
    /**
     * Returns whether or not the graph is an interval graph.
     *
     * @return <tt>true</tt> if the graph is an interval graph, otherwise false.
     */
    boolean isIntervalGraph();

    /**
     * Returns the list of all intervals sorted by starting point, or null, if the graph was not an
     * interval graph.
     *
     * @return The list of all intervals sorted by starting point, or null, if the graph was not an
     *         interval graph.
     */
    List<Interval<Integer>> getIntervalsSortedByStartingPoint();
    
    /**
     * Returns a mapping of the constructed intervals to the vertices of the original graph, or null, if the graph was not an interval graph.
     *
     * @return A mapping of the constructed intervals to the vertices of the original graph, or null, if the graph was not an interval graph.
     */
    Map<Interval<Integer>, V> getIntervalToVertexMap();

    /**
     * Returns a mapping of the vertices of the original graph to the constructed intervals, or null, if the graph was not an interval graph.
     *
     * @return A mapping of the vertices of the original graph to the constructed intervals, or null, if the graph was not an interval graph.
     */
    Map<V, IntervalVertexPair<V, Integer>> getVertexToIntervalMap();
}
