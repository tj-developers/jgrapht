package org.jgrapht.alg.spanning;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsSubgraph;

import java.util.*;

/**
 * Implementation of a randomized version of the Esau-Williams heuristic, a greedy randomized adaptive search heuristic
 * (GRASP) for the capacitated minimum spanning tree (CMST) problem. It calculates a suboptimal CMST.
 * The original version can be found in
 * L. R. Esau and K. C. Williams. 1966. On teleprocessing system design: part II a method for approximating the optimal network.
 * IBM Syst. J. 5, 3 (September 1966), 142-147. DOI=http://dx.doi.org/10.1147/sj.53.0142
 * This implementation runs in polynomial time O(n^3).
 *
 * This implementation uses a GRASP version described in
 * Ahuja, Ravindra K., Orlin, James B., and Sharma, Dushyant, (1998).
 * New neighborhood search structures for the capacitated minimum spanning tree problem, No WP 4040-98.
 * Working papers, Massachusetts Institute of Technology (MIT), Sloan School of Management.
 * This version runs in pseudo polynomial time as it is dependent on the number of operations
 * <code>numberOfOperationsParameter (denoted by p)</code>, such that it is in O(n^3 + p*n^2).
 *
 * The <a href="https://en.wikipedia.org/wiki/Capacitated_minimum_spanning_tree">Capacitated Minimum Spanning Tree</a>
 * (CMST) problem is a rooted minimal cost spanning tree that statisfies the capacity
 * constrained on all trees that are connected by the designated root. The problem is NP-hard.
 * The hard part of the problem is the implicit partition defined by the subtrees.
 * If one can find the correct partition, the MSTs can be calculated in polynomial time.
 */
public class EsauWilliamsGRASPCapacitatedMinimumSpanningTree<V, E> implements SpanningTreeAlgorithm<E> {
    /**
     * the input graph.
     */
    private final Graph<V, E> graph;

    /**
     * the designated root of the CMST.
     */
    private final V root;

    /**
     * the maximal capacity for each subtree.
     */
    private final double capacity;

    /**
     * the weight function over all vertices.
     */
    private final Map<V, Double> weights;

    /**
     * the number of the most profitable operations considered in the GRASP procedure.
     */
    private final int numberOfOperationsParameter;

    public EsauWilliamsGRASPCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> weights, int numberOfOperationsParameter) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.root = Objects.requireNonNull(root, "Root cannot be null");
        this.capacity = capacity;
        this.weights = Objects.requireNonNull(weights, "Weight cannot be null");
        this.numberOfOperationsParameter = numberOfOperationsParameter;
    }

    @Override
    public SpanningTree<E> getSpanningTree() {

        Pair<Map<V, Integer>, Map<Integer, Pair<Set<V>, Double>>> partition = getPartition();

        return calculateResultingSpanningTree(partition.getSecond());
    }

    Pair<Map<V, Integer>, Map<Integer, Pair<Set<V>, Double>>> getPartition() {
        /*
         * labeling of the improvement graph vertices. There are two vertices in the improvement graph for every vertex
         * in the input graph: the vertex indicating the vertex itself and the vertex indicating the subtree.
         */
        Map<V, Integer> labels = new HashMap<>();
        // the implicit partition defined by the subtrees
        Map<Integer, Pair<Set<V>, Double>> partition = new HashMap<>();

        int counter = 0;
        for(V v : graph.vertexSet()) {
            labels.put(v, counter);
            Set<V> currentPart = new HashSet<>();
            currentPart.add(v);
            partition.put(counter, Pair.of(currentPart, weights.get(v)));
            counter++;
        }

        while(true) {
            Map<V, Double> tradeOffFunction = new HashMap<>();
            Map<V, V> closestVertex = new HashMap<>();


            for(V v1 : graph.vertexSet()) {

                V closestVertexToV1 = null;
                double minDistance = Double.MAX_VALUE;

                // calculate closest vertex to v1
                for(Pair<Set<V>, Double> part : partition.values()) {
                    for (V v2 : part.getFirst()) {
                        if (!part.getFirst().contains(v1) && root != v2 && graph.containsEdge(v1, v2)) {
                            double currentEdgeWeight = graph.getEdgeWeight(graph.getEdge(v1, v2));
                            if (closestVertexToV1 == null || currentEdgeWeight < minDistance) {
                                closestVertexToV1 = v2;
                                minDistance = currentEdgeWeight;
                            }
                        }
                    }
                }

                // store closest vertex to v1
                closestVertex.put(v1, closestVertexToV1);
                // store the maximum trade off and the corresponding vertex
                tradeOffFunction.put(v1, graph.getEdgeWeight(graph.getEdge(v1, root)) - minDistance);
            }

            // manage list of best operations
            LinkedList<V> tradeOffMaximumVertexList = new LinkedList<>();
            for(Map.Entry<V, Double> entry : tradeOffFunction.entrySet()) {
                // check if capacity constraint is not violated
                if(partition.get(labels.get(entry.getKey())).getSecond() + partition.get(labels.get(closestVertex.get(entry.getKey()))).getSecond() <= capacity) {
                    /*
                     * insert current tradeOffFunction entry at the position such that the list is order by the tradeOff
                     * and the size of the list is at most numberOfOperationsParameter
                     */
                    int position = 0;
                    for(V v : tradeOffMaximumVertexList) {
                        if(tradeOffFunction.get(v) > entry.getValue()) {
                            break;
                        }
                        position++;
                    }
                    if(tradeOffMaximumVertexList.size() == numberOfOperationsParameter) {
                        tradeOffMaximumVertexList.removeLast();
                    }
                    tradeOffMaximumVertexList.add(position, entry.getKey());
                }
            }

            if(!tradeOffMaximumVertexList.isEmpty()) {
                // get improvement
                V tradeOffMaximumVertex = tradeOffMaximumVertexList.get((int) (Math.random() * tradeOffMaximumVertexList.size()));

                // get the whole subtree that has to be moved
                Pair<Set<V>, Double> subtreeToMove = partition.get(labels.get(closestVertex.get(tradeOffMaximumVertex)));
                // update tree by merging both trees
                partition.get(labels.get(tradeOffMaximumVertex)).getFirst().addAll(subtreeToMove.getFirst());
                Pair<Set<V>, Double> newSubtree = Pair.of(partition.get(labels.get(tradeOffMaximumVertex)).getFirst(), partition.get(labels.get(tradeOffMaximumVertex)).getSecond() + subtreeToMove.getSecond());
                partition.put(labels.get(tradeOffMaximumVertex), newSubtree);
                // update labels
                for(V v : subtreeToMove.getFirst()) {
                    labels.put(v, labels.get(tradeOffMaximumVertex));
                }
                // remove merged part from partition
                partition.remove(labels.get(closestVertex.get(tradeOffMaximumVertex)));
            } else {
                break;
            }
        }

        return Pair.of(labels, partition);
    }

    private SpanningTree<E> calculateResultingSpanningTree(Map<Integer, Pair<Set<V>, Double>> partition) {
        Set<E> spanningTreeEdges = new HashSet<>();
        double weight = 0;

        for(Pair<Set<V>, Double> part : partition.values()) {
            Set<V> set = part.getFirst();
            SpanningTree<E> subtree = new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, set)).getSpanningTree();
            E minimalEdgeToRoot = null;
            for(E e : graph.outgoingEdgesOf(root)) {
                V opposite = Graphs.getOppositeVertex(graph, e, root);
                if(minimalEdgeToRoot == null || graph.getEdgeWeight(minimalEdgeToRoot) < graph.getEdgeWeight(e)) {
                    minimalEdgeToRoot = e;
                }
            }
            if(minimalEdgeToRoot == null) {
                throw new IllegalStateException("There is no edge from the root to a subtree. This is impossible. This is a bug.");
            }

            spanningTreeEdges.addAll(subtree.getEdges());
            weight += subtree.getWeight();
            spanningTreeEdges.add(minimalEdgeToRoot);
            weight += graph.getEdgeWeight(minimalEdgeToRoot);
        }

        return new SpanningTreeImpl<>(spanningTreeEdges, weight);
    }
}
