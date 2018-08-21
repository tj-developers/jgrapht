package org.jgrapht.alg.interval.mpq;

import java.util.HashSet;

/**
 * A node of a modified PQ-tree
 *
 * @param <V> the element type of the tree node
 * @author Jiong Fu (magnificent_tony)
 * @author Ira Justus Fesefeldt (PhoenixIra)
 */
public abstract class MPQTreeNode<V> {

    /**
     * The parent of the current node
     */
    protected MPQTreeNode<V> parent = null;

    /**
     * The graph vertices to be removed from the current tree node, representing the set A
     */
    private final HashSet<V> setA = new HashSet<>();

    /**
     * The graph vertices associated with the current tree node, representing the set B
     */
    final HashSet<V> setB = new HashSet<>();

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
        this.setB.addAll(vertexSet);
    }

    /**
     * Get the parent tree node
     *
     * @return the parent tree node
     */
    public MPQTreeNode<V> getParent() {
        return parent;
    }

    /**
     * Set the parent tree node
     *
     * @param parent the parent tree node
     */
    public void setParent(MPQTreeNode<V> parent) {
        this.parent = parent;
    }

    /**
     * Get the setA associated with this tree node
     *
     * @return the setA associated with this tree node
     */
    public HashSet<V> getSetA() {
        return setA;
    }

    /**
     * Add a graph vertex to the setA of this tree node
     *
     * @param vertex the graph vertex to be added to the setA of this tree node
     */
    public void addToSetA(V vertex) {
        this.setA.add(vertex);
    }

    /**
     * Get the setB associated with this tree node
     *
     * @return the setB associated with this tree node
     */
    public HashSet<V> getSetB() {
        return setB;
    }

    /**
     * Add a graph vertex to the setB of this tree node
     *
     * @param vertex the graph vertex to be added to the setB of this tree node
     */
    public void addToSetB(V vertex) {
        this.setB.add(vertex);
    }

    /**
     * Move elements from setA to setB by adding elements to setB and clearing elements from setA
     */
    public void moveSetAToSetB() {
        this.setB.addAll(setA);
        this.setA.clear();
    }

}
