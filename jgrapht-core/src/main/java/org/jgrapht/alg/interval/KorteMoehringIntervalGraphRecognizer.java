package org.jgrapht.alg.interval;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.graph.interval.Interval;
import org.jgrapht.graph.interval.IntervalVertexPair;

import java.util.*;

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
    private MPQTreeNode treeRoot = null;

    /**
     * The mapping from a vertex in the graph to a set of nodes in the MPQ tree, in order to reach the associated node quickly
     */
    private HashMap<V, Set<MPQTreeNode>> vertexNodeMap;

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

            // get the predecessors of the vertex u
            Set<V> predecessors = getPrecedingNeighbors(vertexIndexMap, u);

            // special case for predecessors is empty
            if (predecessors.isEmpty()) {
                addEmptyPredecessors(u);
                continue;
            }

            // labeling
            Queue<MPQTreeNode> treeNodeQueue = new LinkedList<>();

            // phase A
            for (V vertex : predecessors) {
                MPQTreeNode associatedNode = getAssociatedTreeNode(vertex);
                if (associatedNode == null) {
                    throw new IllegalStateException("The vertex is not found in the MPQ tree.");
                }

                if (associatedNode.getClass() == QSectionNode.class) {
                    QSectionNode qSectionNode = (QSectionNode) associatedNode;
                    if (!qSectionNode.isLeftmostSection() && !qSectionNode.isRightmostSection()) {
                        return false;
                    }
                }

                removeVertexFromSetB(vertex, associatedNode);
                addVertexToSetA(vertex, associatedNode);

                // put the node or the outer section on a queue
                treeNodeQueue.add(associatedNode);
            }

            // phase B
            List<MPQTreeNode> markedTreeNodes = new LinkedList<>();

            while (!treeNodeQueue.isEmpty()) {

                // delete the tree node from the front of the queue
                MPQTreeNode currentTreeNode = treeNodeQueue.remove();

                // if the tree node is unmarked, mark it and add its father at the rear of the queue
                if (!markedTreeNodes.contains(currentTreeNode)) {
                    markedTreeNodes.add(currentTreeNode);
                    if (currentTreeNode.parent != null) {
                        treeNodeQueue.add(currentTreeNode.parent);
                    }
                }
            }

            // updating
            // check if every marked tree node has at most one son
            if (!isPath(markedTreeNodes)) {
                // if not, the marked tree nodes do not actually form a path
                return false;
            }

            //get lowest positive node in path
//            MPQTreeNode smallestNode = getSmallN(path, positiveLabels);

            //get highest non-inf node in path
//            MPQTreeNode biggestNode = getBigN(path, positiveLabels);

            //update MPQ Tree
//            if(smallestNode.equals(biggestNode))
//                addVertexToLeaf(u,path);
//            else
//                changedPathToTemplates(u,path,smallestNode,biggestNode);

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

        HashSet<V> vertexSet = new HashSet<>();
        vertexSet.add(vertex);
        MPQTreeNode leaf = new Leaf(vertexSet);

        if (treeRoot == null) {
            // if the tree root is null, make the leaf the tree root
            treeRoot = leaf;
        } else {
            leaf.parent = treeRoot;
        }

        // create associated node set for the vertex
        Set<MPQTreeNode> nodeSet = new HashSet<>();
        nodeSet.add(leaf);

        // put the vertex - associated node set to the map
        vertexNodeMap.put(vertex, nodeSet);
    }

    /**
     * Get the node in the MPQ tree containing the given vertex in the bag
     *
     * @param vertex the vertex in the graph to be tested
     * @return the associated node in the MPQ tree
     */
    private MPQTreeNode getAssociatedTreeNode(V vertex) {
        Set<MPQTreeNode> associatedPositions = vertexNodeMap.get(vertex);

        // if the associated position set is empty, then the associated tree node is not found
        if (associatedPositions.isEmpty()) {
            return null;
        }

        // if the associated position set contains only one element, then the associated tree node is unique
        if (associatedPositions.size() == 1) {
            return associatedPositions.iterator().next();
        }

        // if the associated position set contains more than one element, then the associated tree node should be a Q-node
        MPQTreeNode associatedQNode = null;
        for (MPQTreeNode associatedPosition : associatedPositions) {
            if (associatedPosition.getClass() != QSectionNode.class) {
                throw new RuntimeException("Associated position must be a Q section node in this case.");
            }
            if (associatedQNode != null && associatedPosition.parent != associatedQNode) {
                throw new RuntimeException("Associated parent is node unique.");
            }
            associatedQNode = associatedPosition.parent;
        }

        return associatedQNode;
    }

    /**
     * Remove the vertex from the set B of the associated node
     *
     * @param vertex   the vertex to be removed
     * @param treeNode the associated node of the vertex
     */
    private void removeVertexFromSetB(V vertex, MPQTreeNode treeNode) {
        if (treeNode.setB == null) {
            throw new IllegalStateException("The element set in the associated node is null.");
        }
        treeNode.setB.remove(vertex);
    }

    /**
     * Add the vertex into the set A of the associated node
     *
     * @param vertex   the vertex to be added
     * @param treeNode the associated node of the vertex
     */
    private void addVertexToSetA(V vertex, MPQTreeNode treeNode) {
        treeNode.setA = new HashSet<>();
        treeNode.setA.add(vertex);
    }

    /**
     * Test if the input tree node list forms a path
     *
     * @param treeNodes the input tree node list to be tested
     * @return true if the input tree node list forms a path, false otherwise
     */
    private boolean isPath(List<MPQTreeNode> treeNodes) {
        for (MPQTreeNode treeNode : treeNodes) {
            if (!treeNode.hasAtMostOneSon()) {
                return false;
            }
        }
        return true;
    }

    /**
     * TODO: better Javadoc
     * computes the smallest vertex N of the Tree which has a positive label
     *
     * @param path           the path from root to leaf
     * @param positiveLabels the map from nodes to positive labels
     * @return smalles vertex N with positive label
     */
    private MPQTreeNode getSmallN(List<MPQTreeNode> path, Map<MPQTreeNode, Integer> positiveLabels) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * TODO: better Javadoc
     * computes the highest vertex N of the tree which is non-empty and non-inf
     *
     * @param path           the path from root to leaf
     * @param positiveLabels the map from nodes to positive labels
     * @return highest non-empty, non-inf vertex N
     */
    private MPQTreeNode getBigN(List<MPQTreeNode> path, Map<MPQTreeNode, Integer> positiveLabels) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * TODO: better Javadoc
     * Adds the vertex u to the leaf of the path
     *
     * @param u    the vertex to be added
     * @param path the path of the leaf
     */
    private void addVertexToLeaf(V u, List<MPQTreeNode> path) {
        // TODO Auto-generated method stub

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

    // Modified PQ-tree data structure

    /**
     * A node of a modified PQ-tree
     */
    private abstract class MPQTreeNode {

        /**
         * The parent of the current node
         */
        MPQTreeNode parent = null;

        /**
         * The graph vertices associated with the current tree node, representing the set B
         */
        HashSet<V> setB = null;

        /**
         * The graph vertex list representing the set A
         */
        HashSet<V> setA = null;

        /**
         * Instantiate a tree node associating with no graph vertex
         */
        MPQTreeNode() {
        }

        /**
         * Instantiate a tree node associated with a graph vertex set
         *
         * @param vertexSet the current node in the associated graph vertex set
         */
        MPQTreeNode(HashSet<V> vertexSet) {
            this.setB = vertexSet;
        }

        abstract boolean hasAtMostOneSon();

    }

    /**
     * A P-node of a modified PQ-tree
     */
    private class PNode extends MPQTreeNode {

        /**
         * The children of a P-node are stored with a doubly linked circular list
         * <p>
         * P-node has a pointer of the current child as the entrance to this list
         */
        CircularListNode currentChild = null;

        /**
         * Instantiate a P node associating with a graph vertex set
         *
         * @param vertexSet the current node in the associated graph vertex set
         */
        PNode(HashSet<V> vertexSet) {
            super(vertexSet);
        }

        @Override
        boolean hasAtMostOneSon() {
            return currentChild == null || currentChild == currentChild.next();
        }

        /**
         * add child for the current P-node
         *
         * @param child the child node to be added
         */
        void addChild(MPQTreeNode child) {
            // TODO: add child according to the template operations
        }

    }

    /**
     * A Q-node of a modified PQ-tree
     */
    private class QNode extends MPQTreeNode {

        /**
         * The children of a Q-node are stored with a doubly linked list
         * <p>
         * Q-node has two pointers of the outermost sections as the entrances to this list
         */
        QSectionNode leftmostSection = null;
        QSectionNode rightmostSection = null;

        /**
         * Instantiate a Q node associating with a set of graph vertices
         */
        QNode(QSectionNode section) {
            super(null); // elements of Q-node are currently stored in the corresponding section nodes, make this null here

            // TODO: check nullability of the input section and raise NullPointerException accordingly, no more nullability check after this point
            this.leftmostSection = section;
            this.rightmostSection = section;
        }

        @Override
        boolean hasAtMostOneSon() {
            // TODO: check the correctness of this comparison
            return leftmostSection == rightmostSection;
        }
    }

    /**
     * A section node of a Q-node
     */
    private class QSectionNode extends MPQTreeNode {

        /**
         * The child of the current Q section node
         * <p>
         * Each section has a pointer to its son
         */
        MPQTreeNode child = null;

        /**
         * The sections have a pointer to their neighbor sections
         * <p>
         * For the left most section, the left sibling is null
         * For the right most section, the right sibling is null
         */
        QSectionNode leftSibling = null;
        QSectionNode rightSibling = null;

        /**
         * Initiating a section node of a Q-node associating with a graph vertex set
         *
         * @param vertexSet the current node in the associated graph vertex set
         */
        QSectionNode(HashSet<V> vertexSet) {
            super(vertexSet);
        }

        @Override
        boolean hasAtMostOneSon() {
            return true;
        }

        /**
         * Test if the current Q section node is the leftmost section in the associated Q node by checking if the left sibling is null
         *
         * @return true if the current Q section node is the leftmost section, false otherwise
         */
        private boolean isLeftmostSection() {
            return this.leftSibling == null;
        }

        /**
         * Test if the current Q section node is the rightmost section in the associated Q node by checking if the right sibling is null
         *
         * @return true if the current Q section node is the rightmost section, false otherwise
         */
        private boolean isRightmostSection() {
            return this.rightSibling == null;
        }

    }

    /**
     * A leaf node of a modified PQ-tree
     */
    private class Leaf extends MPQTreeNode {

        /**
         * Initiating a leaf node associating with a graph vertex set
         *
         * @param vertexSet the current node in the associated graph vertex set
         */
        Leaf(HashSet<V> vertexSet) {
            super(vertexSet);
        }

        @Override
        boolean hasAtMostOneSon() {
            return true;
        }

    }

    /**
     * the label of a node N or a section S of a Q-node
     */
    private enum Label {

        ALL(2), SOME(1), NONE(0);

        private int value;

        Label(int value) {
            this.value = value;
        }
    }

}
