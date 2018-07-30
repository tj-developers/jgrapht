/*
 * (C) Copyright 2018-2018, by Christoph Grüne and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.alg.spanning;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.AhujaOrlinSharmaCyclicExchangeLocalAugmentation;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;

/**
 * Implementation of an algorithm for the capacitated minimum spanning tree problem using a cyclic exchange neighborhood,
 * based on
 * Ravindra K. Ahuja, James B. Orlin, Dushyant Sharma,
 * A composite very large-scale neighborhood structure for the capacitated minimum spanning tree problem,
 * Operations Research Letters, Volume 31, Issue 3, 2003, Pages 185-194, ISSN 0167-6377,
 * https://doi.org/10.1016/S0167-6377(02)00236-5. (http://www.sciencedirect.com/science/article/pii/S0167637702002365)
 *
 * A <a href="https://en.wikipedia.org/wiki/Capacitated_minimum_spanning_tree">Capacitated Minimum Spanning Tree</a>
 * (CMST) is a rooted minimal cost spanning tree that satisfies the capacity
 * constrained on all trees that are connected to the designated root. The problem is NP-hard.
 * The hard part of the problem is the implicit partition defined by the subtrees.
 * If one can find the correct partition, the MSTs can be calculated in polynomial time.
 *
 * This algorithm is a very large scale neighborhood search algorithm using a cyclic exchange neighborhood
 * until a local minimum is found. It makes frequently use of a MST algorithm and the algorithm for subset
 * disjoint cycles by Ahuja et al.
 * That is, the algorithm may run in exponential time.
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 *
 * @author Christoph Grüne
 * @since July 11, 2018
 */
public class AhujaOrlinSharmaCapacitatedMinimumSpanningTree<V, E> extends AbstractCapacitatedMinimumSpanningTree<V, E> {

    /**
     * the maximal length of the cycle in the neighborhood
     */
    private final int lengthBound;

    /**
     * the number of the most profitable operations considered in the GRASP procedure for the initial solution.
     */
    private final int numberOfOperationsParameter;

    /**
     * the initial solution
     */
    private CapacitatedSpanningTree<V, E> initialSolution;

    /**
     * contains whether the local search uses the vertex operation
     */
    private boolean useVertexOperation;

    /**
     * conatains whether the local search uses the subtree operation
     */
    private boolean useSubtreeOperation;


    /**
     * Constructs a new instance of this algorithm.
     *
     * @param graph the base graph
     * @param root the designated root of the CMST
     * @param capacity the edge capacity constraint
     * @param demands the demands of the vertices
     * @param lengthBound the length bound of the cycle detection algorithm
     * @param numberOfOperationsParameter the number of operations that are considered in the randomized Esau-Williams algorithm {@see EsauWilliamsCapacitatedMinimumSpanningTree}
     */
    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> demands, int lengthBound, int numberOfOperationsParameter) {
        this(graph, root, capacity, demands, lengthBound, numberOfOperationsParameter, true, true);
    }

    /**
     * Constructs a new instance of this algorithm with the proposed initial solution.
     *
     * @param initialSolution the initial solution
     * @param graph the base graph
     * @param root the designated root of the CMST
     * @param capacity the edge capacity constraint
     * @param demands the demands of the vertices
     * @param lengthBound the length bound of the cycle detection algorithm
     */
    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(CapacitatedSpanningTree<V, E> initialSolution, Graph<V, E> graph, V root, double capacity, Map<V, Double> demands, int lengthBound) {
        this(graph, root, capacity, demands, lengthBound, 0, true, true);
        this.initialSolution = initialSolution;
    }

    /**
     * Constructs a new instance of this algorithm.
     *
     * @param graph the base graph
     * @param root the designated root of the CMST
     * @param capacity the edge capacity constraint
     * @param demands the demands of the vertices
     * @param lengthBound the length bound of the cycle detection algorithm
     * @param numberOfOperationsParameter the number of operations that are considered in the randomized Esau-Williams algorithm {@see EsauWilliamsCapacitatedMinimumSpanningTree}
     * @param useVertexOperation contains whether the local search uses the vertex operation
     * @param useSubtreeOperation contains whether the local search uses the subtree operation
     */
    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(
            Graph<V, E> graph,
            V root,
            double capacity,
            Map<V, Double> demands,
            int lengthBound,
            int numberOfOperationsParameter,
            boolean useVertexOperation,
            boolean useSubtreeOperation
    ) {
        super(graph, root, capacity, demands);
        this.lengthBound = lengthBound;
        this.numberOfOperationsParameter = numberOfOperationsParameter;
        if(!useSubtreeOperation && !useVertexOperation) {
            throw new IllegalArgumentException("At least one of the options has to be enabled, otherwise it is not possible to excute the local search: useVertexOperation and useSubtreeOperation.");
        }
        this.useVertexOperation = useVertexOperation;
        this.useSubtreeOperation = useSubtreeOperation;
    }

    /**
     * Constructs a new instance of this algorithm with the proposed initial solution.
     *
     * @param initialSolution the initial solution
     * @param graph the base graph
     * @param root the designated root of the CMST
     * @param capacity the edge capacity constraint
     * @param demands the demands of the vertices
     * @param lengthBound the length bound of the cycle detection algorithm
     * @param useVertexOperation contains whether the local search uses the vertex operation
     * @param useSubtreeOperation contains whether the local search uses the subtree operation
     */
    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(
            CapacitatedSpanningTree<V, E> initialSolution,
            Graph<V, E> graph, V root,
            double capacity,
            Map<V, Double> demands,
            int lengthBound,
            boolean useVertexOperation,
            boolean useSubtreeOperation
    ) {
        this(graph, root, capacity, demands, lengthBound, 0, useVertexOperation, useSubtreeOperation);
        this.initialSolution = initialSolution;
    }

    @Override
    public CapacitatedSpanningTree<V, E> getCapacitatedSpanningTree() {

        // calculates initial solution on which we base the local search
        solutionRepresentation = getInitialSolution();

        // map that contains all spanning trees of the current partition
        Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTrees = new HashMap<>();
        // map that contains the subtrees of all vertices
        Map<V, Pair<Set<V>, Double>> subtrees = new HashMap<>();
        // set that contains all part of the partition that were affected by an exchange operation
        Set<Integer> affectedParts = solutionRepresentation.getLabels();
        // the improvement graph
        ImprovementGraph improvementGraph = new ImprovementGraph();

        double currentCost;

        // do local improvement steps
        do {

            partitionSpanningTrees = calculateSpanningTrees(partitionSpanningTrees, affectedParts);
            subtrees = calculateSubtreesOfVertices(subtrees, partitionSpanningTrees, affectedParts);

            improvementGraph.updateImprovementGraph(subtrees, partitionSpanningTrees, affectedParts);

            AhujaOrlinSharmaCyclicExchangeLocalAugmentation<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> ahujaOrlinSharmaCyclicExchangeLocalAugmentation
                    = new AhujaOrlinSharmaCyclicExchangeLocalAugmentation<>(improvementGraph.improvementGraph, lengthBound, improvementGraph.cycleAugmentationLabels);

            GraphWalk<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> cycle = ahujaOrlinSharmaCyclicExchangeLocalAugmentation.getLocalAugmentationCycle();
            currentCost = cycle.getWeight();

            affectedParts = executeNeighborhoodOperation(improvementGraph.improvementGraphVertexMapping, improvementGraph.pathExchangeVertexMapping, subtrees, cycle);

        } while(currentCost < 0);

        return solutionRepresentation.calculateResultingSpanningTree();
    }

    /**
     * Calculates an initial solution depending on whether an initial solution was transferred while construction of the algorithm.
     * If no initial solution was proposed, the algorithm of Esau-Williams is used.
     *
     * @return an initial solution
     */
    private SolutionRepresentation getInitialSolution() {
        if(initialSolution != null) {
            return new SolutionRepresentation(initialSolution.getLabels(), initialSolution.getPartition());
        }
        return new EsauWilliamsCapacitatedMinimumSpanningTree<>(graph, root, capacity, demands, numberOfOperationsParameter).getSolution();
    }

    /**
     * Executes the move operations induced by the calculated cycle in the improvement graph. It returns the set of labels of the subsets that were affected by the move operations.
     *
     * @param improvementGraphVertexMapping the mapping from the index of the improvement graph vertex to the correspondent vertex in the base graph
     * @param pathExchangeVertexMapping the mapping from the improvement graph pseudo vertices to their subset that they represent
     * @param subtrees the map containing the subtree for every vertex
     * @param cycle the calculated cycle in the improvement graph
     *
     * @return the set of affected labels of subsets that were affected by the move operations
     */
    private Set<Integer> executeNeighborhoodOperation(
            Map<Integer, V> improvementGraphVertexMapping,
            Map<Pair<Integer, ImprovementGraphVertexType>, Integer> pathExchangeVertexMapping,
            Map<V, Pair<Set<V>, Double>> subtrees,
            GraphWalk<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> cycle
    ) {
        Set<Integer> affectedLabels = new HashSet<>();

        Iterator<Pair<Integer, ImprovementGraphVertexType>> it = cycle.getVertexList().iterator();
        if(it.hasNext()) {
            Pair<Integer, ImprovementGraphVertexType> cur = it.next();
            Integer firstLabel;
            switch(cur.getSecond()) {
                case SINGLE: firstLabel = solutionRepresentation.getLabel(improvementGraphVertexMapping.get(cur.getFirst())); break;
                case SUBTREE: firstLabel = solutionRepresentation.getLabel(improvementGraphVertexMapping.get(cur.getFirst())); break;
                default: firstLabel = -1;
            }
                while (it.hasNext()) {
                    Pair<Integer, ImprovementGraphVertexType> next = it.next();

                    switch(cur.getSecond()) {
                        /*
                         * A vertex is moved form the part of cur to the part of next. Therefore, both parts are affected.
                         * We only consider the label of cur to be affected for now, the label of next will be add to the affected set in the next iteration.
                         */
                        case SINGLE: {
                            V curVertex = improvementGraphVertexMapping.get(cur.getFirst());
                            Integer curLabel = solutionRepresentation.getLabel(curVertex);
                            Integer nextLabel;
                            if(it.hasNext()) {
                                switch(next.getSecond()) {
                                    case SINGLE: nextLabel = solutionRepresentation.getLabel(improvementGraphVertexMapping.get(next.getFirst())); break;
                                    case SUBTREE: nextLabel = solutionRepresentation.getLabel(improvementGraphVertexMapping.get(next.getFirst())); break;
                                    case PSEUDO: nextLabel = pathExchangeVertexMapping.get(next); break;
                                    default: throw new IllegalStateException("This is a bug. There are invalid types of vertices in the cycle.");
                                }
                            } else {
                                nextLabel = firstLabel;
                            }
                            affectedLabels.add(curLabel);

                            solutionRepresentation.moveVertex(curVertex, curLabel, nextLabel);
                            break;
                        }
                        /*
                         * A subtree is moved from the part of cur to the part of next. Therefore, the part of cur is affected.
                         */
                        case SUBTREE: {
                            V curVertex = improvementGraphVertexMapping.get(cur.getFirst());
                            Integer curLabel = solutionRepresentation.getLabel(curVertex);
                            Integer nextLabel;
                            if(it.hasNext()) {
                                switch(next.getSecond()) {
                                    case SINGLE: nextLabel = solutionRepresentation.getLabel(improvementGraphVertexMapping.get(next.getFirst())); break;
                                    case SUBTREE: nextLabel = solutionRepresentation.getLabel(improvementGraphVertexMapping.get(next.getFirst())); break;
                                    case PSEUDO: nextLabel = pathExchangeVertexMapping.get(next); break;
                                    default: throw new IllegalStateException("This is a bug. There are invalid types of vertices in the cycle.");
                                }
                            } else {
                                nextLabel = firstLabel;
                            }

                            affectedLabels.add(curLabel);

                            // get the whole subtree that has to be moved
                            Set<V> subtreeToMove = subtrees.get(curVertex).getFirst();
                            solutionRepresentation.moveVertices(subtreeToMove, curLabel, nextLabel);
                            break;
                        }
                        /*
                         * cur is the end of a path exchange. Thus, the part of cur is affected because vertices were inserted.
                         */
                        case PSEUDO: {
                            Integer curLabel = pathExchangeVertexMapping.get(cur);
                            affectedLabels.add(curLabel);
                            break;
                        }
                        /*
                         * This is the beginning of a path exchange. We have nothing to do.
                         */
                        case ORIGIN: {
                            break;
                        }
                        default: throw new IllegalStateException("This is a bug. There are invalid types of vertices in the cycle.");
                    }

                    cur = next;
                }

        }

        Set<Integer> moreAffectedLabels = new HashSet<>();
        Iterator<Integer> affectedLabelIterator = affectedLabels.iterator();
        while(affectedLabelIterator.hasNext()) {
            int label = affectedLabelIterator.next();
            Set<V> vertexSubset = solutionRepresentation.getPartitionSet(label);
            if(vertexSubset.isEmpty()) {
                affectedLabelIterator.remove();
            } else {
                moreAffectedLabels.addAll(partitionSubtreesOfSubset(vertexSubset, label));
            }
        }
        affectedLabels.addAll(moreAffectedLabels);

        solutionRepresentation.cleanUp();


        return affectedLabels;
    }

    /**
     * Refines the partition by adding new subsets if the designated root has more than one subtree in the subset <code>label</code> of the partition.
     *
     * @param label the label of the subset of the partition that has to be refined
     */
    private Set<Integer> partitionSubtreesOfSubset(Set<V> vertexSubset, int label) {

        List<Set<V>> subtreesOfSubset = new LinkedList<>();

        if(vertexSubset.isEmpty()) {
            return new HashSet<>();
        }

        vertexSubset.add(root);
        SpanningTreeAlgorithm.SpanningTree<E> spanningTree = new PrimMinimumSpanningTree<>(new AsSubgraph<>(graph, vertexSubset, graph.edgeSet())).getSpanningTree();

        Graph<V, E> spanningTreeGraph = new AsSubgraph<>(graph, vertexSubset, spanningTree.getEdges());

        int degreeOfRoot = spanningTreeGraph.degreeOf(root);
        if(degreeOfRoot == 1) {
            vertexSubset.remove(root);
            return new HashSet<>();
        }

        Set<Integer> affectedLabels = new HashSet<>();

        DepthFirstIterator<V, E> depthFirstIterator = new DepthFirstIterator<>(spanningTreeGraph, root);
        if(depthFirstIterator.hasNext()) {
            depthFirstIterator.next();
        }

        int numberOfRootEdgesExplored = 0;
        Set<V> currentSubtree = new HashSet<>();

        while(depthFirstIterator.hasNext()) {
            V next = depthFirstIterator.next();

            // exploring new subtree
            if(spanningTreeGraph.containsEdge(root, next)) {
                if(!currentSubtree.isEmpty()) {
                    subtreesOfSubset.add(currentSubtree);
                    currentSubtree = new HashSet<>();
                }
                // we do not have to move more vertices
                if(numberOfRootEdgesExplored + 1 == degreeOfRoot) {
                    break;
                }
            }
            currentSubtree.add(next);
        }

        // move the subtrees to new subsets in the partition
        for(Set<V> subtree : subtreesOfSubset) {
            int nextLabel = solutionRepresentation.getNextFreeLabel();
            solutionRepresentation.moveVertices(subtree, label, nextLabel);
            affectedLabels.add(nextLabel);
        }

        vertexSubset.remove(root);
        return affectedLabels;
    }

    /**
     * Updates the map containing the MSTs for every subset of the partition.
     *
     * @param partitionSpanningTrees the map containing the MST for every subset of the partition
     * @param affectedLabels the labels of the subsets of the partition that were changed due to the multi-exchange
     *
     * @return the updated map containing the MST for every subset of the partition
     */
    private Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> calculateSpanningTrees(Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTrees, Set<Integer> affectedLabels) {
        for(Integer label : affectedLabels) {
            Set<V> set = solutionRepresentation.getPartitionSet(label);
            solutionRepresentation.getPartitionSet(label).add(root);
            partitionSpanningTrees.put(label, new KruskalMinimumSpanningTree<>(new AsSubgraph<>(graph, set)).getSpanningTree());
            solutionRepresentation.getPartitionSet(label).remove(root);
        }
        return partitionSpanningTrees;
    }

    /**
     * Updates the map containing the subtrees of all vertices in the graph with respect to the MST in the partition and returns them in map.
     *
     * @param subtrees the subtree map to update
     * @param partitionSpanningTree the map containing the MST for every subset of the partition
     * @param affectedLabels the labels of the subsets of the partition that were changed due to the multi-exchange
     *
     * @return the updated map of vertices to their subtrees
     */
    private Map<V, Pair<Set<V>, Double>> calculateSubtreesOfVertices(Map<V, Pair<Set<V>, Double>> subtrees, Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTree, Set<Integer> affectedLabels) {
        for(Integer label : affectedLabels) {
            for (V v : solutionRepresentation.getPartitionSet(label)) {
                Pair<Set<V>, Double> currentSubtree = subtree(v, partitionSpanningTree);
                subtrees.put(v, currentSubtree);
            }
        }
        return subtrees;
    }

    /**
     * Calculates the subtree of <code>v</code> with respect to the MST given in <code>partitionSpanningTree</code>.
     *
     * @param v the vertex to calculate the subtree for
     * @param partitionSpanningTree the map from labels to spanning trees of the partition.
     *
     * @return the subtree of <code>v</code> with respect to the MST given in <code>partitionSpanningTree</code>.
     */
    private Pair<Set<V>, Double> subtree(V v, Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTree) {
        Set<V> partVertices = solutionRepresentation.getPartitionSet(solutionRepresentation.getLabel(v));
        SpanningTreeAlgorithm.SpanningTree<E> partSpanningTree = partitionSpanningTree.get(solutionRepresentation.getLabel(v));
        Graph<V, E> spanningTree = new AsSubgraph<>(graph, partVertices, partSpanningTree.getEdges());

        Set<V> subtree = new HashSet<>();
        double subtreeWeight = 0;

        Iterator<V> depthFirstIterator = new DepthFirstIterator<>(spanningTree, v);
        Set<V> currentPath = new HashSet<>();
        double currentWeight = 0;

        boolean storeCurrentPath = true;
        while(depthFirstIterator.hasNext()) {
            V next = depthFirstIterator.next();
            if(spanningTree.containsEdge(next, v)) {
                storeCurrentPath = true;

                subtree.addAll(currentPath);
                subtreeWeight += currentWeight;

                currentPath = new HashSet<>();
                currentWeight = 0;
            }
            if(next.equals(root)) {
                storeCurrentPath = false;

                currentPath = new HashSet<>();
                currentWeight = 0;
            }
            if(storeCurrentPath) {
                currentPath.add(next);
                currentWeight += demands.get(next);
            }
        }
        return Pair.of(subtree, subtreeWeight);
    }

    /**
     * This enums contains the vertex types of the improvement graph.
     */
    private enum ImprovementGraphVertexType {
        SINGLE, SUBTREE, PSEUDO, ORIGIN
    }

    // TODO BIG TEXT TO INSERT HERE
    private class ImprovementGraph {

        /**
         * the improvement graph itself
         */
        Graph<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> improvementGraph;

        /**
         * mapping form all improvement graph vertices to their labels corresponding to the base graph for the CMST problem
         */
        Map<Pair<Integer, ImprovementGraphVertexType>, Integer> cycleAugmentationLabels;

        /**
         * mapping from the vertex index in the improvement graph to the vertex in the base graph
         */
        Map<Integer, V> improvementGraphVertexMapping;
        /**
         * mapping from the base graph vertex to the vertex index in the improvement graph
         */
        Map<V, Integer> initialVertexMapping;
        /**
         * mapping from the label of the subsets to the corresponding vertex mapping
         */
        Map<Integer, Pair<Integer, ImprovementGraphVertexType>> pseudoVertexMapping;
        /**
         * mapping from the pseudo vertices to the label of the subset they are representing
         */
        Map<Pair<Integer, ImprovementGraphVertexType>, Integer> pathExchangeVertexMapping;
        /**
         * the origin vertex
         */
        Pair<Integer, ImprovementGraphVertexType> origin;
        /**
         *
         */
        final Integer originVertexLabel = -1;

        /**
         * Constructs an new improvement graph object for this CMST algorithm instance.
         */
        public ImprovementGraph() {
            this.improvementGraphVertexMapping = new HashMap<>();
            this.initialVertexMapping = new HashMap<>();
            this.pseudoVertexMapping = new HashMap<>();
            this.pathExchangeVertexMapping = new HashMap<>();
            /*
             * We initialize this map such that it can be used in the subset-disjoint cycle detection algorithm.
             * This map redirects the getters to the corresponding maps in this improvement graph such that it realises the correct functionality.
             */
            this.cycleAugmentationLabels = new Map<Pair<Integer, ImprovementGraphVertexType>, Integer>() {
                @Override
                public int size() {
                    return improvementGraphVertexMapping.size() + pseudoVertexMapping.size();
                }

                @Override
                public boolean isEmpty() {
                    return improvementGraphVertexMapping.isEmpty() || pseudoVertexMapping.isEmpty();
                }

                @Override
                public boolean containsKey(Object key) {
                    if(key instanceof Pair) {
                        return improvementGraphVertexMapping.containsKey(((Pair) key).getFirst())
                                || pseudoVertexMapping.containsKey(((Pair) key).getFirst());
                    }
                    return false;
                }

                @Override
                public boolean containsValue(Object value) {
                    return improvementGraphVertexMapping.containsValue(value) || pseudoVertexMapping.containsValue(value);
                }

                @Override
                public Integer get(Object key) {
                    if(key instanceof Pair && improvementGraphVertexMapping.containsKey(((Pair) key).getFirst())) {
                        return solutionRepresentation.getLabel(improvementGraphVertexMapping.get(((Pair) key).getFirst()));
                    }
                    return pathExchangeVertexMapping.get(key);
                }

                @Override
                public Integer put(Pair<Integer, ImprovementGraphVertexType> key, Integer value) {
                    throw new IllegalStateException();
                }

                @Override
                public Integer remove(Object key) {
                    throw new IllegalStateException();
                }

                @Override
                public void putAll(Map<? extends Pair<Integer, ImprovementGraphVertexType>, ? extends Integer> m) {
                    throw new IllegalStateException();
                }

                @Override
                public void clear() {
                    throw new IllegalStateException();
                }

                @Override
                public Set<Pair<Integer, ImprovementGraphVertexType>> keySet() {
                    Set<Pair<Integer, ImprovementGraphVertexType>> keySet = new HashSet<>();
                    for(Integer i : improvementGraphVertexMapping.keySet()) {
                        keySet.add(Pair.of(i, ImprovementGraphVertexType.SINGLE));
                        keySet.add(Pair.of(i, ImprovementGraphVertexType.SUBTREE));
                    }
                    keySet.addAll(pathExchangeVertexMapping.keySet());
                    keySet.add(origin);
                    return keySet;
                }

                @Override
                public Collection<Integer> values() {
                    return solutionRepresentation.getLabels();
                }

                @Override
                public Set<Entry<Pair<Integer, ImprovementGraphVertexType>, Integer>> entrySet() {

                    Set<Entry<Pair<Integer, ImprovementGraphVertexType>, Integer>> entrySet = new HashSet<>();
                    for(Integer i : improvementGraphVertexMapping.keySet()) {
                        Integer label = solutionRepresentation.getLabel(improvementGraphVertexMapping.get(i));
                        entrySet.add(new AbstractMap.SimpleEntry<>(Pair.of(i, ImprovementGraphVertexType.SINGLE), label));
                        entrySet.add(new AbstractMap.SimpleEntry<>(Pair.of(i, ImprovementGraphVertexType.SUBTREE), label));
                    }
                    for(Pair<Integer, ImprovementGraphVertexType> pseudoVertex : pathExchangeVertexMapping.keySet()) {
                        entrySet.add(new AbstractMap.SimpleEntry<>(pseudoVertex, pathExchangeVertexMapping.get(pseudoVertex)));
                    }
                    entrySet.add(new AbstractMap.SimpleEntry<>(origin, originVertexLabel));
                    return entrySet;
                }
            };

            this.improvementGraph = createImprovementGraph();
        }

        /**
         * Initializes the improvement graph, i.e. adds single, subtree and pseudo vertices as well as the origin vertex. Furthermore, it initializes all mappings.
         *
         * @return the improvement graph itself.
         */
        public Graph<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> createImprovementGraph() {
            Graph<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> improvementGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

            int counter = 0;

            for(V v : graph.vertexSet()) {

                if(v.equals(root)) {
                    continue;
                }

                Pair<Integer, ImprovementGraphVertexType> singleVertex = new Pair<>(counter, ImprovementGraphVertexType.SINGLE);
                improvementGraph.addVertex(singleVertex);

                Pair<Integer, ImprovementGraphVertexType> subtreeVertex = new Pair<>(counter, ImprovementGraphVertexType.SUBTREE);
                improvementGraph.addVertex(subtreeVertex);

                // we have to add these only once
                improvementGraphVertexMapping.put(counter, v);
                initialVertexMapping.put(v, counter);

                counter++;
            }

            Pair<Integer, ImprovementGraphVertexType> origin = new Pair<>(counter, ImprovementGraphVertexType.ORIGIN);
            improvementGraph.addVertex(origin);
            this.origin = origin;
            pathExchangeVertexMapping.put(origin, originVertexLabel);
            counter++;

            for(Integer label : solutionRepresentation.getLabels()) {
                Pair<Integer, ImprovementGraphVertexType> pseudoVertex = new Pair<>(counter, ImprovementGraphVertexType.PSEUDO);
                pseudoVertexMapping.put(label, pseudoVertex);
                pathExchangeVertexMapping.put(pseudoVertex, label);
                improvementGraph.addVertex(pseudoVertex);

                counter++;
            }

            /*
             * connection of pseudo nodes and origin node
             */
            for(Pair<Integer, ImprovementGraphVertexType> v : pseudoVertexMapping.values()) {
                improvementGraph.setEdgeWeight(improvementGraph.addEdge(v, origin), 0);
            }

            return improvementGraph;
        }

        /**
         * Updates the improvement graph. It updates the vertices and edges in the parts specified in <code>labelsToUpdate</code>.
         *
         * @param subtrees the mapping from vertices to their subtree
         * @param partitionSpanningTrees the mapping from labels of subsets to their spanning tree
         * @param labelsToUpdate the labels of all subsets that has to be updated (because of the multi-exchange operation)
         */
        public void updateImprovementGraph(Map<V, Pair<Set<V>, Double>> subtrees, Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTrees, Set<Integer> labelsToUpdate) {

            double newCapacity, newWeight, oldWeight;
            SpanningTreeAlgorithm.SpanningTree<E> spanningTree;

            for(V v1 : graph.vertexSet()) {

                if(v1.equals(root)) {
                    continue;
                }

                Pair<Integer, ImprovementGraphVertexType> vertexOfV1Single = Pair.of(initialVertexMapping.get(v1), ImprovementGraphVertexType.SINGLE);
                Pair<Integer, ImprovementGraphVertexType> vertexOfV1Subtree = Pair.of(initialVertexMapping.get(v1), ImprovementGraphVertexType.SUBTREE);

                /*
                 * update connections to origin node
                 */
                if(labelsToUpdate.contains(solutionRepresentation.getLabel(v1))) {
                    oldWeight = partitionSpanningTrees.get(solutionRepresentation.getLabel(v1)).getWeight();
                    // edge for v1 vertex remove operation
                    Set<V> partitionSetOfV1 = solutionRepresentation.getPartitionSet(solutionRepresentation.getLabel(v1));
                    partitionSetOfV1.add(root);
                    partitionSetOfV1.remove(v1);
                    spanningTree = new PrimMinimumSpanningTree<>(new AsSubgraph<>(graph, partitionSetOfV1)).getSpanningTree();
                    if(spanningTree.getEdges().size() == partitionSetOfV1.size() - 1) {
                        newWeight = spanningTree.getWeight();
                    } else {
                        newWeight = Double.NaN;
                    }
                    updateImprovementGraphEdge(origin, vertexOfV1Single,0,newWeight - oldWeight);
                    partitionSetOfV1.add(v1);

                    /*
                     * edge for v1 subtree remove operation
                     * If the subtree of v1 contains only the vertex itself, it is the same operation as removing v1 as vertex. Thus, do not add edges.
                     */
                    if(subtrees.get(v1).getFirst().size() > 1) {
                        partitionSetOfV1.removeAll(subtrees.get(v1).getFirst());
                        spanningTree = new PrimMinimumSpanningTree<>(new AsSubgraph<>(graph, partitionSetOfV1)).getSpanningTree();
                        if(spanningTree.getEdges().size() == partitionSetOfV1.size() - 1) {
                            newWeight = spanningTree.getWeight();
                        } else {
                            newWeight = Double.NaN;
                        }
                        updateImprovementGraphEdge(origin, vertexOfV1Subtree,0,newWeight - oldWeight);
                        partitionSetOfV1.addAll(subtrees.get(v1).getFirst());
                    }
                    partitionSetOfV1.remove(root);
                }

                /*
                 * update the connections to regular nodes and pseudo nodes
                 */
                for(Integer label : solutionRepresentation.getLabels()) {

                    /*
                     * only update if there is a change induced by a changed part. This potentially saves a lot of time.
                     */
                    if (label.equals(solutionRepresentation.getLabel(v1)) || (!labelsToUpdate.contains(solutionRepresentation.getLabel(v1)) && !labelsToUpdate.contains(label))) {
                        continue;
                    }

                    Pair<Integer, ImprovementGraphVertexType> pseudoVertex = pseudoVertexMapping.get(label);

                    Set<V> modifiableSet = new HashSet<>(solutionRepresentation.getPartitionSet(label));

                    oldWeight = partitionSpanningTrees.get(label).getWeight();

                    // add root and v1 to the set for MST calculations
                    modifiableSet.add(root);
                    modifiableSet.add(v1);

                    /*
                     * Adding of edges for v1 vertex replacing an object in v2.
                     * We need to considers this only if vertex operations should be used.
                     */
                    if (useVertexOperation) {
                        for (V v2 : solutionRepresentation.getPartitionSet(label)) {

                            if (v2.equals(root)) {
                                throw new IllegalStateException("FUCK");
                            }

                            /*
                             * edge for v1 vertex replacing v2 vertex
                             */
                            modifiableSet.remove(v2);
                            spanningTree = new PrimMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiableSet, graph.edgeSet())).getSpanningTree();
                            if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                                newCapacity = calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, solutionRepresentation.getPartitionWeight(label) + demands.get(v1) - demands.get(v2));
                                newWeight = spanningTree.getWeight();
                            } else {
                                newCapacity = Double.NaN;
                                newWeight = Double.NaN;
                            }
                            updateImprovementGraphEdge(vertexOfV1Single, Pair.of(initialVertexMapping.get(v2), ImprovementGraphVertexType.SINGLE), newCapacity, newWeight - oldWeight);
                            modifiableSet.add(v2);
                            // end edge for v1 vertex replacing v2 vertex

                            /*
                             * edge for v1 vertex replacing v2 subtree
                             * If the subtree of v2 contains only the vertex itself and both operations are used, it is the same operation as moving v2 as vertex. Thus, do not add edges.
                             */
                            if (useSubtreeOperation) {
                                if (subtrees.get(v2).getFirst().size() > 1 || !useVertexOperation) {
                                    modifiableSet.removeAll(subtrees.get(v2).getFirst());
                                    spanningTree = new PrimMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiableSet, graph.edgeSet())).getSpanningTree();
                                    if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                                        newCapacity = calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, solutionRepresentation.getPartitionWeight(label) + demands.get(v1) - subtrees.get(v2).getSecond());
                                        newWeight = spanningTree.getWeight();
                                    } else {
                                        newCapacity = Double.NaN;
                                        newWeight = Double.NaN;
                                    }
                                    updateImprovementGraphEdge(vertexOfV1Single, Pair.of(initialVertexMapping.get(v2), ImprovementGraphVertexType.SUBTREE), newCapacity, newWeight - oldWeight);
                                    modifiableSet.addAll(subtrees.get(v2).getFirst());
                                }
                            }
                            // end edge for v1 vertex replacing v2 subtree
                        }

                        /*
                         * edge for v1 vertex replacing no object
                         */
                        spanningTree = new PrimMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiableSet, graph.edgeSet())).getSpanningTree();
                        if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                            newCapacity = calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, solutionRepresentation.getPartitionWeight(label) + demands.get(v1));
                            newWeight = spanningTree.getWeight();
                        } else {
                            newCapacity = Double.NaN;
                            newWeight = Double.NaN;
                        }
                        updateImprovementGraphEdge(vertexOfV1Single, pseudoVertex, newCapacity, newWeight - oldWeight);
                        // end edge for v1 vertex replacing no object

                        // remove v1 from the set
                        modifiableSet.remove(v1);
                    }

                    /*
                     * Adding of edges for v1 subtree replacing an object in v2.
                     * We need to considers this only if subtree operations should be used.
                     *
                     * If the subtree of v1 contains only the vertex itself and both operations are used, it is the same operation as moving v1 as vertex. Thus, do not add edges.
                     */
                    if (useSubtreeOperation && (subtrees.get(v1).getFirst().size() > 1 || !useVertexOperation)) {

                        // add the subtree of v1 to the set for MST calculations
                        modifiableSet.addAll(subtrees.get(v1).getFirst());

                        for (V v2 : solutionRepresentation.getPartitionSet(label)) {

                            if(v2.equals(root)) {
                                throw new IllegalStateException("FUCK");
                            }

                            /*
                             * edge for v1 subtree replacing v2 vertex
                             */
                            if(useVertexOperation) {
                                modifiableSet.remove(v2);
                                spanningTree = new PrimMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiableSet, graph.edgeSet())).getSpanningTree();
                                if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                                    newCapacity = calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, solutionRepresentation.getPartitionWeight(label) + subtrees.get(v1).getSecond() - demands.get(v2));
                                    newWeight = spanningTree.getWeight();
                                } else {
                                    newCapacity = Double.NaN;
                                    newWeight = Double.NaN;
                                }
                                updateImprovementGraphEdge(vertexOfV1Subtree, Pair.of(initialVertexMapping.get(v2), ImprovementGraphVertexType.SINGLE), newCapacity, newWeight - oldWeight);
                                modifiableSet.add(v2);
                            }
                            // end edge for v1 subtree replacing v2 vertex

                            /*
                             * edge for v1 subtree replacing v2 subtree
                             */
                            modifiableSet.removeAll(subtrees.get(v2).getFirst());
                            spanningTree = new PrimMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiableSet, graph.edgeSet())).getSpanningTree();
                            if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                                newCapacity = calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, solutionRepresentation.getPartitionWeight(solutionRepresentation.getLabel(v2)) + subtrees.get(v1).getSecond() - subtrees.get(v2).getSecond());
                                newWeight = spanningTree.getWeight();
                            } else {
                                newCapacity = Double.NaN;
                                newWeight = Double.NaN;
                            }
                            updateImprovementGraphEdge(vertexOfV1Subtree, Pair.of(initialVertexMapping.get(v2), ImprovementGraphVertexType.SUBTREE), newCapacity, newWeight - oldWeight);
                            modifiableSet.addAll(subtrees.get(v2).getFirst());
                            // end edge for v1 subtree replacing v2 subtree
                        }

                        /*
                         * edge for v1 subtree replacing no object
                         */
                        spanningTree = new PrimMinimumSpanningTree<>(new AsSubgraph<>(graph, modifiableSet, graph.edgeSet())).getSpanningTree();
                        if(spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                            newCapacity = calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, solutionRepresentation.getPartitionWeight(label) + subtrees.get(v1).getSecond());
                            newWeight = spanningTree.getWeight();
                        } else {
                            newCapacity = Double.NaN;
                            newWeight = Double.NaN;
                        }
                        updateImprovementGraphEdge(vertexOfV1Subtree, pseudoVertex, newCapacity, newWeight - oldWeight);
                        // end edge for v1 subtree replacing no object

                        // remove the subtree of v1 and the root from the set
                        // modifiableSet.removeAll(subtrees.get(v1).getFirst());
                        // modifiableSet.remove(root);
                    }
                }
            }
        }

        /**
         * Adds an edge between <code>v1</code> and <code>v2</code> to the improvement graph if <code>newCapacity</code> does not exceed the capacity constraint.
         * The weight of the edge is <code>newCost</code>.
         *
         * @param v1 start vertex (the vertex or subtree induced by <code>v1</code> that will be moved to the subset of <code>v2</code>)
         * @param v2 end vertex (the vertex or subtree induced by <code>v2</code> that will be removed from the subset of <code>v2</code>)
         * @param newCapacity the used capacity by adding the vertex or subtree induced by <code>v1</code> to the subset of <code>v2</code> and deleting the vertex or subtree induced by <code>v2</code>
         * @param newCost the cost of the edge (the cost induced by the operation induced by <code>v1</code> and <code>v2</code>)
         */
        public void updateImprovementGraphEdge(Pair<Integer, ImprovementGraphVertexType> v1, Pair<Integer, ImprovementGraphVertexType> v2, double newCapacity, double newCost) {
            if (!Double.isNaN(newCapacity) && newCapacity <= capacity && !Double.isNaN(newCost)) {
                DefaultWeightedEdge edge;
                edge = improvementGraph.getEdge(v1, v2);
                if(edge == null) {
                    edge = improvementGraph.addEdge(v1, v2);
                }
                improvementGraph.setEdgeWeight(edge, newCost);
            } else {
                improvementGraph.removeEdge(v1, v2);
            }
        }

        /**
         * Calculates the maximum demand over all new subtrees induced by the minimum spanning tree <code>spanningTree</code>.
         * A spanning tree induces more than one subset in the partition if the root vertex of the base graph connects more than one subtree of the spanning tree.
         *
         * @param vertexSubset the vertex subset <code>spanning Tree is defined on</code>
         * @param spanningTree the spanning tree
         * @param totalDemand the total demand of the whole spanning tree
         *
         * @return the maximum demand over all new subtrees induced by the minimum spanning tree <code>spanningTree</code>
         */
        public double calculateMaximumDemandOfSubtrees(Set<V> vertexSubset, SpanningTreeAlgorithm.SpanningTree<E> spanningTree, double totalDemand) {

            Graph<V, E> spanningTreeGraph = new AsSubgraph<>(graph, vertexSubset, spanningTree.getEdges());

            /*
             * The subtree does not evolve to more than 1 partition subsets, thus, we can return the total demand.
             */
            int degreeOfRoot = spanningTreeGraph.degreeOf(root);
            if(degreeOfRoot == 1) {
                return totalDemand;
            }

            double maximumDemand = 0;

            DepthFirstIterator<V, E> depthFirstIterator = new DepthFirstIterator<>(spanningTreeGraph, root);
            if(depthFirstIterator.hasNext()) {
                depthFirstIterator.next();
            }

            int numberOfRootEdgesExplored = 0;

            double exploredVerticesDemand = 0;
            double currentDemand = 0;

            while(depthFirstIterator.hasNext()) {
                V next = depthFirstIterator.next();

                // exploring new subtree
                if(spanningTreeGraph.containsEdge(root, next)) {

                    exploredVerticesDemand += currentDemand;

                    // we can stop the exploration, all subtrees but one are explored
                    if(numberOfRootEdgesExplored + 1 == degreeOfRoot) {
                        return totalDemand - exploredVerticesDemand;
                    }

                    if(maximumDemand < currentDemand) {
                        maximumDemand = currentDemand;
                    }

                    // we can stop the exploration
                    if(maximumDemand >= 0.5 * totalDemand || exploredVerticesDemand + maximumDemand >= totalDemand) {
                        return maximumDemand;
                    }

                    numberOfRootEdgesExplored++;

                    currentDemand = 0;
                }

                currentDemand += demands.get(next);
            }

            return maximumDemand;
        }
    }
}
