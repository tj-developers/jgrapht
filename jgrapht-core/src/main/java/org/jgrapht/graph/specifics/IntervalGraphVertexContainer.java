package org.jgrapht.graph.specifics;

import org.jgrapht.util.interval.Interval;
import org.jgrapht.graph.IntervalVertexInterface;
import org.jgrapht.util.IntervalStructureInterface;
import org.jgrapht.util.IntervalTreeStructure;

import java.io.Serializable;
import java.util.*;

/**
 * Implementation of IntervalGraphVertexContainer
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 * @param <VertexType> the internal vertex type
 * @param <T> the type of the interval
 *
 * @author Christoph Grüne (christophgruene)
 * @since Apr 26, 2018
 */
public class IntervalGraphVertexContainer<V extends IntervalVertexInterface<VertexType, T>, E, VertexType, T extends Comparable<T>> implements IntervalGraphVertexContainerInterface<V, E>, Serializable {

    private static final long serialVersionUID = 7768940080894764546L;

    /**
     * <code>intervalStructure</code> maintains all intervals in order to get intersecting intervals efficiently.
     */
    private IntervalStructureInterface<T> intervalStructure;
    /**
     * <code>vertexMap</code> maintains an EdgeContainer for every Vertex
     */
    private Map<V, UndirectedEdgeContainer<V, E>> vertexMap;
    /**
     * <code>intervalMap</code> maintains the assignment of every interval to vertex
     */
    private Map<Interval<T>, V> intervalMap;

    /**
     * Constructs a vertex container for an interval graph with all necessary objects.
     */
    IntervalGraphVertexContainer() {
        this.intervalStructure = new IntervalTreeStructure<>();
        this.vertexMap = new LinkedHashMap<>();
        this.intervalMap = new LinkedHashMap<>();
    }

    /**
     * Constructs a vertex container for an interval graph with all necessary objects.
     *
     * @param vertices The set of vertices
     * @param  undirectedEdgeContainers The set of undirected edge containers
     */
    IntervalGraphVertexContainer(ArrayList<V> vertices, ArrayList<UndirectedEdgeContainer<V, E>> undirectedEdgeContainers) {
        this.vertexMap = new LinkedHashMap<>();
        this.intervalMap = new LinkedHashMap<>();

        ArrayList<Interval<T>> intervals = new ArrayList<>(vertices.size());

        for(int i = 0; i < vertices.size(); ++i) {
            intervals.add(i, vertices.get(i).getInterval());
            intervalMap.put(vertices.get(i).getInterval(), vertices.get(i));
            vertexMap.put(vertices.get(i), undirectedEdgeContainers.get(i));
        }

        this.intervalStructure = new IntervalTreeStructure<>(intervals);
    }

    /**
     * Returns the whole vertex set of the graph.
     *
     * @return all vertices of the graph in a set
     */
    @Override
    public Set<V> getVertexSet() {
        return vertexMap.keySet();
    }

    /**
     * returns a list of all vertices with overlapping interval w.r.t <code>v</code>
     *
     * @param vertex the vertex with interval
     */
    @Override
    public List<V> getOverlappingIntervalVertices(V vertex) {
        List<Interval<T>> intervalList = intervalStructure.overlapsWith(vertex.getInterval());
        List<V> vertexList = new LinkedList<>();

        for(Interval<T> interval: intervalList) {
            vertexList.add(intervalMap.get(interval));
        }

        return vertexList;
    }

    /**
     * Returns the edge container to the vertex
     *
     * @param vertex the vertex
     * @return the edge container to the vertex
     */
    @Override
    public UndirectedEdgeContainer<V, E> get(V vertex) {
        return vertexMap.get(vertex);
    }

    /**
     * puts the given edge container to the data structure
     *
     * @param vertex the vertex
     * @param ec     the edge container
     */
    @Override
    public UndirectedEdgeContainer<V, E> put(V vertex, UndirectedEdgeContainer<V, E> ec) {
        intervalStructure.add(vertex.getInterval());
        intervalMap.put(vertex.getInterval(), vertex);
        return vertexMap.put(vertex, ec);
    }

    /**
     * Removes a vertex from the data structure if it is present.
     *
     * @param vertex the vertex to be removed
     * @return true if this data structure contained the specified element
     */
    @Override
    public UndirectedEdgeContainer<V, E> remove(V vertex) {
        intervalStructure.remove(vertex.getInterval());
        intervalMap.remove(vertex.getInterval());
        return vertexMap.remove(vertex);
    }
}
