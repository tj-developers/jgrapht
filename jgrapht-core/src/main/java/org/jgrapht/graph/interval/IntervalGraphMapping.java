package org.jgrapht.graph.interval;

import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.interval.IntervalGraphRecognizer;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.*;

import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;

/**
 * Implementation of an interval graph mapping. An interval graph is an intersection graph of intervals on a line. Because of
 * that instances do not allow the adding or removing of edges. The edges are implicitly defined by the intervals.
 *
 * @param <V> the vertex type with a corresponding interval
 * @param <E> the edge type
 * @param <VertexType> the underlying vertex type
 * @param <T> The underlying type for intervals
 *
 * @author Christoph Grüne (christophgruene)
 * @since May 30, 2018
 */
public class IntervalGraphMapping<V extends IntervalVertexInterface<VertexType, T>, E, VertexType, T extends Comparable<T>> implements Serializable {

    private static final long serialVersionUID = 1112673663745683444L;

    private static final String INTERVAL_GRAPH_ADD_EDGE = "Intervals of nodes define edges in interval graphs and cannot be modified manually";

    private Graph<V, E> graph;

    /**
     * <code>intervalStructure</code> maintains all intervals in order to get intersecting intervals efficiently.
     */
    private IntervalStructureInterface<T> intervalStructure;
    /**
     * <code>intervalMap</code> maintains the assignment of every interval to vertex
     */
    private Map<Interval<T>, V> intervalMap;

    /**
     * stores state whether this mapping is valid
     */
    private boolean mappingValid;

    /**
     * stores state whether this mapping is live (that is, is a ListenableGraph used)
     */
    private boolean mappingLive;

    /**
     * Basic Constructor for a non-live mapping to an interval graph. All methods are passed through to the underlying interval graph.
     *
     * @param graph the graph to be mapped
     */
    private IntervalGraphMapping(Graph<V, E> graph) {
        this.graph = graph;
        this.intervalStructure = new IntervalTreeStructure<>();
        this.intervalMap = new HashMap<>();
        // as we know that a this is a correctly initialized mapping, we can set this mapping to valid
        this.mappingValid = true;
        // as we know that no ListenableGraph is used, we set the mapping non-live
        this.mappingLive = false;
    }

    /**
     * Basic constructor for a live mapping to an interval graph. All methods are passed through to the underlying interval graph.
     * Every vertex and edge update will be considered.
     *
     * @param graph the graph to be mapped
     */
    private IntervalGraphMapping(ListenableGraph<V, E> graph) {
        this((Graph<V, E>) graph);
        // add the basic IntervalGraphListener to the ListenableGraph such that we have a live mapping
        graph.addGraphListener(new IntervalGraphMappingListener<>(this));
        // as we know that a ListenableGraph is used, we set the mapping live
        this.mappingLive = true;
    }

    /**
     * Constructs a new live interval graph mapping with a new graph. The graph can either be directed or undirected, depending on the
     * specified edge supplier.
     *
     * @param vertexSupplier the vertex supplier of the new graph
     * @param edgeSupplier the edge supplier of the new graph.
     * @param weighted whether the graph is weighted, i.e. the edges support a weight attribute
     */
    public IntervalGraphMapping(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        this(new DefaultListenableGraph<>(new DefaultUndirectedGraph<>(vertexSupplier, edgeSupplier, weighted)));
    }

    /**
     * Construct a new live interval graph mapping with a new graph with given <code>vertices</code>. The graph can either be directed or undirected, depending on the
     * specified edge supplier.
     *
     * @param vertices initial vertices
     * @param vertexSupplier the vertex supplier of the new graph.
     * @param edgeSupplier the edge supplier of the new graph.
     * @param weighted whether the graph is weighted, i.e. the edges support a weight attribute
     *
     * @throws NullPointerException if the specified edge factory is <code>
     * null</code>.
     */
    public IntervalGraphMapping(ArrayList<V> vertices, Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        this(vertexSupplier, edgeSupplier, weighted);

        boolean isSorted = true;
        V current = vertices.get(0);
        for (int i = 1; i < vertices.size(); i++) {
            V next = vertices.get(i);
            if (current.getInterval().compareTo(next.getInterval()) > 0) {
                isSorted = false;
                break;
            }
            current = next;
        }

        if(isSorted) {
            ArrayList<LinkedList<V>> edges = new ArrayList<>(vertices.size());

            // calculate all edges
            for(int i = 0; i < vertices.size(); ++i) {
                edges.add(i, new LinkedList<>());
                for(int j = 0; j < i; ++j) {
                    if(vertices.get(j).getInterval().getEnd().compareTo(vertices.get(i).getInterval().getStart()) >= 0) {
                        edges.get(j).add(vertices.get(i));
                    }
                }
            }

            // add all vertices to the graph
            for(int i = 0; i < vertices.size(); ++i) {
                addVertexToDataStructures(vertices.get(i));
            }

            // add all calculated edges to the graph
            for(int i = 0; i < vertices.size(); ++i) {
                addIntervalEdges(vertices.get(i), edges.get(i));
            }

        } else { // if the list is not sorted, add vertices as usual. Thereby, the edges are calculated
            for (V vertex : vertices) {
                this.addVertex(vertex);
            }
        }
    }

    /**
     * This method can only be used if the graph is an interval graph! Therefore, it is private. We use it for asIntervalGraphMapping()
     *
     * Construct a new live interval graph mapping with a new graph with given <code>vertices</code>. The graph can either be directed or undirected, depending on the
     * specified edge factory.
     *
     * @param vertices initial vertices
     * @param edges initial edges
     * @param vertexSupplier the vertex supplier of the new graph.
     * @param edgeSupplier the edge supplier of the new graph.
     * @param weighted whether the graph is weighted, i.e. the edges support a weight attribute
     *
     * @throws NullPointerException if the specified edge factory is <code>
     * null</code>.
     */
    private IntervalGraphMapping(ArrayList<V> vertices, Map<Pair<V, V>, E> edges, Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        this(new DefaultListenableGraph<>(new DefaultUndirectedGraph<>(vertexSupplier, edgeSupplier, weighted)));

        // add sorted vertices
        for(V v : vertices) {
            addVertexToDataStructures(v);
        }

        // add all edges
        for(Pair<V, V> v : edges.keySet()) {
            graph.addEdge(v.getFirst(), v.getSecond(), edges.get(v));
        }
    }

    /**
     * Getter for the underlying graph.
     *
     * @return the underlying graph
     */
    public Graph<V, E> getGraph() {
        return graph;
    }

    /**
     * returns interval graph representation if one exists, otherwise null
     *
     * @param graph the graph to check
     * @param <E> the edge type
     * @param <VertexType> the internal vertex type
     * @return interval graph representation if one exists, otherwise null
     */
    public static <E, VertexType> IntervalGraphMapping<IntervalVertexInterface<VertexType, Integer>, E, VertexType, Integer> asIntervalGraphMapping(Graph<VertexType, E> graph) {

        // initialize IntervalGraphRecognizer
        IntervalGraphRecognizer<VertexType, E> intervalGraphRecognizer = new IntervalGraphRecognizer<>(graph);

        // execute IntervalGraphRecognizer
        if(!intervalGraphRecognizer.isIntervalGraph()) {
            return null;
        }

        // get necessary data structures from IntervalGraphRecognizer
        ArrayList<Interval<Integer>> sortedIntervalList = intervalGraphRecognizer.getIntervalsSortedByStartingPoint();
        Map<Interval<Integer>, VertexType> intervalVertexMap = intervalGraphRecognizer.getIntervalToVertexMap();
        Map<VertexType, IntervalVertexInterface<VertexType, Integer>> vertexIntervalMap = intervalGraphRecognizer.getVertexToIntervalMap();

        //initialize ArrayList for vertices for quick iteration
        ArrayList<IntervalVertexInterface<VertexType, Integer>> vertices = new ArrayList<>(sortedIntervalList.size());

        // add vertices to the vertex list
        for(int i = 0; i < sortedIntervalList.size(); ++i) {
            vertices.add(i, vertexIntervalMap.get(intervalVertexMap.get(sortedIntervalList.get(i))));
        }

        // initialze a map from vertex pairs to all edges
        Map<Pair<IntervalVertexInterface<VertexType, Integer>, IntervalVertexInterface<VertexType, Integer>>, E> edges = new LinkedHashMap<>();

        // add the edges to the map
        for(IntervalVertexInterface<VertexType, Integer> vertex : vertices) {
            for (E edge : graph.outgoingEdgesOf(vertex.getVertex())) {
                if (!vertex.getVertex().equals(graph.getEdgeTarget(edge))) {
                    edges.put(Pair.of(vertex, vertexIntervalMap.get(graph.getEdgeTarget(edge))), edge);
                }
            }
        }

        // return new IntervalGraphMapping with an invalid vertex supplier as adding of general vertices is not allowed because we need for every vertex the corresponding interval.
        return new IntervalGraphMapping<>(vertices, edges, null, () -> graph.getEdgeSupplier().get(), graph.getType().isWeighted());
    }

    /**
     * returns whether the mapping is valid
     *
     * @return true, if mapping is valid; false, otherwise
     */
    public boolean isMappingValid() {
        return mappingValid;
    }

    /**
     * returns whether the mapping is valid
     *
     * @return true, if mapping is valid; false, otherwise
     */
    public boolean isMappingLive() {
        return mappingLive;
    }

    /**
     * sets the mapping to invalid
     */
    void setMappingInvalid() {
        this.mappingValid = false;
    }

    /**
     * adds a vertex to all data structures (graph, intervalStructure, intervalMap) that are used in this mapping.
     *
     * @param v the vertex to add
     *
     * @return <tt>true</tt> if this graph did not already contain the specified edge.
     *
     * @throws NullPointerException if any of the specified vertices is <code>null</code>.
     */
    public boolean addVertex(V v) {
        if(v == null) {
            throw new NullPointerException();
        } else if(!graph.containsVertex(v)) {
            graph.addVertex(v);


            intervalStructure.add(v.getInterval());
            intervalMap.put(v.getInterval(), v);
            List<Interval<T>> intervalList = intervalStructure.overlapsWith(v.getInterval());
            List<V> vertexList = new LinkedList<>();

            for (Interval<T> interval : intervalList) {
                vertexList.add(intervalMap.get(interval));
            }

            addIntervalEdges(v, vertexList);
            return true;
        }
        return false;
    }

    /**
     * removes a vertex from all data structures (graph, intervalStructure, intervalMap) that are used in this mapping.
     *
     * @param v the vertex to remove
     *
     * @return <code>true</code> if the graph contained the specified vertex; <code>false</code> otherwise.
     */
    public boolean removeVertex(V v) {
        if(graph.containsVertex(v)) {
            boolean mappingCurrentlyValid = true;
            if(!mappingValid) {
                mappingCurrentlyValid = false;
            }

            graph.removeVertex(v);

            intervalStructure.remove(v.getInterval());
            intervalMap.remove(v.getInterval());

            if(mappingCurrentlyValid) {
                mappingValid = true;
            }

            return true;
        }
        return false;
    }

    /**
     * Adds edges between <code>sourceVertex</code> and every vertex from <code>vertices</code>.
     * @param sourceVertex source vertex of all edges
     * @param targetVertices target vertices of edges
     */
    private void addIntervalEdges(V sourceVertex, Collection<V> targetVertices) {
        boolean mappingCurrentlyValid = true;
        if(!mappingValid) {
            mappingCurrentlyValid = false;
        }
        for(V targetVertex: targetVertices) {
            if(!sourceVertex.equals(targetVertex)) {
                graph.addEdge(sourceVertex, targetVertex);
            }
        }
        if(mappingCurrentlyValid) {
            mappingValid = true;
        }
    }

    /**
     * adds given vertex to all data structures without adding the interval edges to the graph
     *
     * @param v the vertex
     */
    private void addVertexToDataStructures(V v) {
        intervalStructure.add(v.getInterval());
        intervalMap.put(v.getInterval(), v);
        graph.addVertex(v);
    }
}
