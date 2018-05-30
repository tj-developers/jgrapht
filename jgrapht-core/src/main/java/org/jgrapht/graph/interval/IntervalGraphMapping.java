package org.jgrapht.graph.interval;

import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.interval.IntervalGraphRecognizer;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.*;
import org.jgrapht.graph.specifics.UndirectedEdgeContainer;
import org.jgrapht.util.TypeUtil;

import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;

import static org.jgrapht.graph.interval.IntervalVertex.*;

/**
 * Implementation of an interval graph mapping . An interval graph is an intersection graph of intervals on a line. Because of
 * that instances do not allow the adding or removing of edges. The edges are implicitly defined by the intervals.
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 * @param <T> The underlying type for intervals
 *
 * @author Christoph Grüne (christophgruene)
 * @since May 30, 2018
 */
public class IntervalGraphMapping<V extends IntervalVertexInterface<VertexType, T>, E, VertexType, T extends Comparable<T>> implements Serializable {

    private static final long serialVersionUID = 1112673663745683444L;

    private static final String INTERVAL_GRAPH_ADD_EDGE = "Intervals of nodes define edges in interval graphs and cannot be modified manually";

    private Graph<V, E> graph;

    private GraphListener<V, E> graphListener;

    /**
     * <code>intervalStructure</code> maintains all intervals in order to get intersecting intervals efficiently.
     */
    private IntervalStructureInterface<T> intervalStructure;
    /**
     * <code>intervalMap</code> maintains the assignment of every interval to vertex
     */
    private Map<Interval<T>, V> intervalMap;

    private boolean isInvalid;

    public IntervalGraphMapping(ListenableGraph<V, E> graph) {
        this.graph = graph;
        this.intervalStructure = new IntervalTreeStructure<>();
        this.intervalMap = new HashMap<>();
        this.graphListener = new IntervalGraphMappingListener<>(this);
    }

    /**
     * Constructs a new interval graph mapping with a new graph. The graph can either be directed or undirected, depending on the
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
     * Construct a new interval graph mapping with a new graph with given <code>vertices</code>. The graph can either be directed or undirected, depending on the
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

            // add all calculated edges and all vertices to the graph
            for(int i = 0; i < vertices.size(); ++i) {
                graph.addVertex(vertices.get(i));
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
     * Construct a new interval graph mapping with a new graph with given <code>vertices</code>. The graph can either be directed or undirected, depending on the
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
            graph.addVertex(v);
        }

        // add all edges
        for(Pair<V, V> v : edges.keySet()) {
            graph.addEdge(v.getFirst(), v.getSecond(), edges.get(v));
        }
    }

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

        IntervalGraphRecognizer<VertexType, E> intervalGraphRecognizer = new IntervalGraphRecognizer<>(graph);

        if(graph.getType().isDirected() || graph.getType().isAllowingMultipleEdges() || graph.getType().isAllowingSelfLoops()) {
            return null;
        }
        if(!intervalGraphRecognizer.isIntervalGraph()) {
            return null;
        }

        ArrayList<Interval<Integer>> sortedIntervalList = intervalGraphRecognizer.getIntervalsSortedByStartingPoint();
        Map<Interval<Integer>, VertexType> intervalVertexMap = intervalGraphRecognizer.getIntervalToVertexMap();
        Map<VertexType, IntervalVertexInterface<VertexType, Integer>> vertexIntervalMap = intervalGraphRecognizer.getVertexToIntervalMap();

        ArrayList<IntervalVertexInterface<VertexType, Integer>> vertices = new ArrayList<>(sortedIntervalList.size());

        for(int i = 0; i < sortedIntervalList.size(); ++i) {
            vertices.add(i, vertexIntervalMap.get(intervalVertexMap.get(sortedIntervalList.get(i))));
        }

        Map<Pair<IntervalVertexInterface<VertexType, Integer>, IntervalVertexInterface<VertexType, Integer>>, E> edges = new LinkedHashMap<>();

        for(IntervalVertexInterface<VertexType, Integer> vertex : vertices) {
            for (E edge : graph.outgoingEdgesOf(vertex.getVertex())) {
                if (!vertex.getVertex().equals(graph.getEdgeTarget(edge))) {
                    edges.put(Pair.of(vertex, vertexIntervalMap.get(graph.getEdgeTarget(edge))), edge);
                }
            }
        }

        // return new IntervalGraphMapping with an invalid vertex supplier as adding of general vertices is not allowed because we need for every vertex the corresponding interval.
        return new IntervalGraphMapping<>(vertices, edges, () -> null, () -> graph.getEdgeSupplier().get(), graph.getType().isWeighted());
    }

    public boolean isMappingInvalid() {
        return isInvalid;
    }

    void  setMappingInvalid() {
        this.isInvalid = true;
    }


    /**
     * Returns a shallow copy of this graph instance. Neither edges nor vertices are cloned.
     *
     * @return a shallow copy of this graph.
     *
     * @throws RuntimeException in case the clone is not supported
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {

        //TODO
        /*
        try {
            IntervalGraphMapping<V, E, VertexType, T> newGraphMapping = TypeUtil.uncheckedCast(super.clone());

            newGraph.edgeSupplier = this.edgeSupplier;
            newGraph.unmodifiableVertexSet = null;

            // NOTE: it's important for this to happen in an object
            // method so that the new inner class instance gets associated with
            // the right outer class instance
            newGraph.specifics = newGraph.createSpecifics();
            newGraph.intrusiveEdgesSpecifics =
                    newGraph.createIntrusiveEdgesSpecifics(this.weighted);

            Graphs.addGraph(newGraph, this);

            return newGraph;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }*/
        return null;
    }

    void addVertex(V v) {
        graph.addVertex(v);

        intervalStructure.add(v.getInterval());
        List<Interval<T>> intervalList = intervalStructure.overlapsWith(v.getInterval());
        List<V> vertexList = new LinkedList<>();

        for(Interval<T> interval: intervalList) {
            vertexList.add(intervalMap.get(interval));
        }

        addIntervalEdges(v, vertexList);
    }

    void removeVertex(V v) {
        graph.removeVertex(v);

        intervalStructure.remove(v.getInterval());
        intervalMap.remove(v.getInterval());
    }

    /**
     * Adds edges between <code>sourceVertex</code> and every vertex from <code>vertices</code>.
     * @param sourceVertex source vertex of all edges
     * @param targetVertices target vertices of edges
     */
    private void addIntervalEdges(V sourceVertex, Collection<V> targetVertices) {
        for(V targetVertex: targetVertices) {
            if(!sourceVertex.equals(targetVertex)) {
                E e = graph.getEdgeSupplier().get();
                graph.addEdge(sourceVertex, targetVertex, e);
            }
        }
    }
}
