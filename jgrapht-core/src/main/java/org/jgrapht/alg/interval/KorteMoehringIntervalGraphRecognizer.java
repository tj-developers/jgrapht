package org.jgrapht.alg.interval;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.alg.interval.mpq.Leaf;
import org.jgrapht.alg.interval.mpq.MPQTreeNode;
import org.jgrapht.alg.interval.mpq.QSectionNode;
import org.jgrapht.graph.interval.Interval;
import org.jgrapht.graph.interval.IntervalVertexPair;

import java.util.*;

import static org.jgrapht.alg.interval.MPQTreeUpdater.addToVertexNodeMap;
import static org.jgrapht.alg.interval.MPQTreeUpdater.addVertexToNode;

/**
 * TODO: better Javadoc
 *
 * @param <V> the vertex type of the graph
 * @param <E> the edge type of the graph
 * @author Jiong Fu (magnificent_tony)
 * @author Ira Justus Fesefeldt (PhoenixIra)
 * @author Timofey Chudakov
 */
public class KorteMoehringIntervalGraphRecognizer<V, E> implements IntervalGraphRecognizerInterface<V> {

    /**
     * The graph to be recognized
     */
    private Graph<V, E> graph = null;

    /**
     * The chordal graph inspector
     */
    private ChordalityInspector<V, E> chordalInspector = null;

    /**
     * The root of the MPQ tree
     */
    private MPQTreeNode<V> treeRoot = null;

    /**
     * The mapping from a vertex in the graph to a set of nodes in the MPQ tree, in order to reach the associated node quickly
     */
    private final HashMap<V, Set<MPQTreeNode<V>>> vertexNodeMap = new HashMap<>();

    /**
     * The boolean flag indicating if the input graph is an interval graph
     */
    private boolean isIntervalGraph;

    /**
     * Constructor for the algorithm
     *
     * @param graph the graph to be recognized
     */
    public KorteMoehringIntervalGraphRecognizer(Graph<V, E> graph) {
        this.graph = graph;
        this.chordalInspector = new ChordalityInspector<>(graph);
        this.isIntervalGraph = testIntervalGraph();
    }

    /**
     * The concrete implementation of Korte-Moehring Algorithm, which tests if the graph is an interval graph with the help of MPQ tree.
     * <p>
     * If the graph is proved to be an interval graph, the interval graph representation is calculated.
     * If the graph is proved not to be an interval graph, the maximal clique is calculated as counter example.
     */
    private boolean testIntervalGraph() {

        // if the graph is not chordal, then it is not interval
        if (!chordalInspector.isChordal()) {
            return false;
        }

        // get the perfect elimination order for the vertices in the graph
        List<V> perfectEliminationOrder = chordalInspector.getPerfectEliminationOrder();
        Map<V, Integer> vertexIndexMap = getVertexIndexMap(perfectEliminationOrder);

        // iterate over the perfect elimination order
        for (V u : perfectEliminationOrder) {

            // get preceding neighbors of the vertex u
            Set<V> precedingNeighbors = getPrecedingNeighbors(vertexIndexMap, u);

            // special case for preceding neighbors is empty
            if (precedingNeighbors.isEmpty()) {
                addEmptyPredecessors(u);
                continue;
            }

            // labeling
            Queue<MPQTreeNode<V>> treeNodeQueue = new LinkedList<>();

            // phase A
            for (V vertex : precedingNeighbors) {
                System.err.println("Current " + u + " Neighbor " + vertex);

                // get associated nodes from the map
                Set<MPQTreeNode<V>> associatedNodes = vertexNodeMap.get(vertex);
                if (associatedNodes == null) {
                    throw new IllegalStateException("Vertex entry cannot be found in the map.");
                }

                if (associatedNodes.size() > 1) {
                    int outermostSectionCount = 0;
                    for (MPQTreeNode<V> associatedNode : associatedNodes) {
                        if (associatedNode.getClass() != QSectionNode.class) {
                            throw new IllegalStateException("The vertex is associated with more than one tree nodes, but not all of them are QSection nodes.");
                        }
                        QSectionNode qSectionNode = (QSectionNode) associatedNode;
                        if (qSectionNode.isLeftmostSection() || qSectionNode.isRightmostSection()) {
                            outermostSectionCount++;
                        }
                    }
                    if (outermostSectionCount == 0) {
                        System.err.println("The vertex is associated with more than one QSection nodes, but none of them are outermost QSection node.");
                        // the input grapgh is chordal graph, but not interval graph
                        return false;
                    }
                }

                for (MPQTreeNode<V> associatedNode : associatedNodes) {
                    moveVertex(vertex, associatedNode);
                    treeNodeQueue.add(associatedNode);
                }
            }

            // phase B
            LinkedList<MPQTreeNode<V>> markedTreeNodes = new LinkedList<>();

            while (!treeNodeQueue.isEmpty()) {

                // delete the tree node from the front of the queue
                MPQTreeNode<V> currentTreeNode = treeNodeQueue.remove();

                // if the tree node is unmarked, mark it and add its father at the rear of the queue
                if (!markedTreeNodes.contains(currentTreeNode)) {
                    markedTreeNodes.add(currentTreeNode);
                    if (currentTreeNode.getParent() != null) {
                        treeNodeQueue.add(currentTreeNode.getParent());
                    }
                }
            }

            // updating
            // check if every marked tree node has at most one son
            if (!isPath(markedTreeNodes)) {
                // if not, the marked tree nodes do not actually form a path
                return false;
            }

            // calculate N_, namely the lowermost node with label 1 or infinite
            MPQTreeNode<V> lowermostNode = markedTreeNodes.getLast();
            // calculate N+, namely the uppermost node with label 0 or 1
            MPQTreeNode<V> uppermostNode = markedTreeNodes.getFirst();

            MPQTreeNode<V> currentNode = lowermostNode;
            if (lowermostNode == uppermostNode) {
                addVertexToNode(u, currentNode, lowermostNode, uppermostNode, vertexNodeMap);
            } else {
                while (true) {
                    addVertexToNode(u, currentNode, lowermostNode, uppermostNode, vertexNodeMap);

                    // if the uppermost node has been updated, stop iterating
                    if (currentNode == uppermostNode) {
                        break;
                    }

                    // otherwise, continue iterating the parent
                    currentNode = currentNode.getParent();
                }
            }
        }

        // after inserting all vertices into the MPQ tree without being returned halfway, the input graph is an interval graph
        return true;
    }

    /**
     * Returns the vertices on the one hand adjacent to the given vertex,
     * on the other hand with indices smaller than the index of the given vertex
     *
     * @param vertexIndexMap the mapping of the vertices in the graph to their indices in an ordering
     * @param vertex         the vertex in the graph to be tested
     * @return the preceding neighbors of the {@code vertex} in the order defined by {@code vertexIndexMap}.
     */
    private Set<V> getPrecedingNeighbors(Map<V, Integer> vertexIndexMap, V vertex) {
        Set<V> precedingNeighbors = new HashSet<>();
        Integer vertexIndex = vertexIndexMap.get(vertex);

        for (E edge : graph.edgesOf(vertex)) {
            V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
            Integer oppositeIndex = vertexIndexMap.get(oppositeVertex);
            if (oppositeIndex < vertexIndex) {
                precedingNeighbors.add(oppositeVertex);
            }
        }

        return precedingNeighbors;
    }

    /**
     * Returns a map containing vertices from the {@code vertexOrder} mapped to their indices in
     * {@code vertexOrder}.
     *
     * @param vertexOrder the list of vertices
     * @return a mapping of vertices from {@code vertexOrder} to their indices in {@code vertexOrder}.
     */
    private Map<V, Integer> getVertexIndexMap(List<V> vertexOrder) {
        Map<V, Integer> vertexIndexMap = new HashMap<>(vertexOrder.size());
        for (int i = 0; i < vertexOrder.size(); i++) {
            vertexIndexMap.put(vertexOrder.get(i), i);
        }
        return vertexIndexMap;
    }

    /**
     * Adds a new leaf node with the bag of this vertex
     *
     * @param vertex the vertex to be added onto the MPQ Tree
     */
    private void addEmptyPredecessors(V vertex) {
        MPQTreeNode<V> leaf = new Leaf<>(vertex);
        addToVertexNodeMap(vertex, leaf, vertexNodeMap);

        if (treeRoot == null) {
            // if the tree root is null, make the leaf the tree root
            treeRoot = leaf;
        } else {
            // if the tree root is not null, make the leaf a new branch from the tree root
            leaf.setParent(treeRoot);
        }
    }

    /**
     * Remove the vertex from the set B and add the vertex into the set A of the associated node
     *
     * @param vertex   the vertex to be moved
     * @param treeNode the associated node of the vertex
     */
    private void moveVertex(V vertex, MPQTreeNode<V> treeNode) {
        if (treeNode.getSetB() == null) {
            throw new IllegalStateException("The element set in the associated node is null.");
        }
        treeNode.getSetB().remove(vertex);
        vertexNodeMap.get(vertex).remove(treeNode); // the vertex is removed from setB, meaning the vertex is not associated with the tree node any more
        treeNode.addToSetA(vertex);
    }

    /**
     * Test if the input tree node list forms a path
     *
     * @param treeNodes the input tree node list to be tested
     * @return true if the input tree node list forms a path, false otherwise
     */
    private boolean isPath(List<MPQTreeNode<V>> treeNodes) {
        // TODO: come back to this later
//        for (MPQTreeNode treeNode : treeNodes) {
//            if (!treeNode.hasAtMostOneSon()) {
//                return false;
//            }
//        }
        return true;
    }

    /**
     * TODO: better Javadoc
     * Checks the path for specifig patterns and changes every node accordingly
     *
     * @param u      the vertex to add to the tree
     * @param path   the path of vertices to be changed
     * @param nSmall the smalles positive node in path
     * @param nBig   the highest non-empty, non-inf node in path
     */
    private void changedPathToTemplates(V u, List<MPQTreeNode> path, MPQTreeNode nSmall, MPQTreeNode nBig) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isIntervalGraph() {
        return isIntervalGraph;
    }

    @Override
    public List<Interval<Integer>> getIntervalsSortedByStartingPoint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<V, IntervalVertexPair<V, Integer>> getVertexToIntervalMap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Interval<Integer>, V> getIntervalToVertexMap() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * TODO: better javadoc
     * <p>
     * the hole in the graph as an counter example for chordality
     *
     * @return a hole if the graph is not chordal, or null if the graph is chordal.
     */
    public GraphPath<V, E> getHole() {
        return chordalInspector.getHole();
    }

    /**
     * TODO: better javadoc
     * <p>
     * the Umbrella sub graph in the graph iff the graph is chordal but not an interval graph
     *
     * @return an umbrella if the graph is not an intervalgraph, or null if the graph is an intervalgraph.
     */
    public Graph<V, E> getUmbrellaSubGraph() {
        // TODO implement
        return null;
    }

}
