package org.jgrapht.alg.interval.mpq;

import java.util.HashSet;

/**
 * A node of a modified PQ-tree
 */
public abstract class MPQTreeNode<V> {

    /**
     * The parent of the current node
     */
    protected MPQTreeNode<V> parent = null;

    /**
     * The graph vertex list representing the set A
     */
    protected final HashSet<V> setA = new HashSet<>();

    /**
     * The graph vertices associated with the current tree node, representing the set B
     */
    protected HashSet<V> setB = null;

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
        this.setB = new HashSet<>();
        this.setB.addAll(vertexSet);
    }

    public abstract boolean hasAtMostOneSon();

    public MPQTreeNode<V> getParent() {
        return parent;
    }

    public void setParent(MPQTreeNode<V> parent) {
        this.parent = parent;
    }

    public HashSet<V> getSetA() {
        return setA;
    }

    public void addToSetA(V vertex) {
        this.setA.add(vertex);
    }

    public void clearSetA() {
        this.setA.clear();
    }

    public HashSet<V> getSetB() {
        return setB;
    }

    public void setSetB(HashSet<V> setB) {
        this.setB = setB;
    }

    public void addToSetB(V vertex) {
        this.setB.add(vertex);
    }

    public void addSetAToSetB() {
        this.setB.addAll(setA);
    }

}
