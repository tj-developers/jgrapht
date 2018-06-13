package org.jgrapht.alg.decomposition;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.graph.*;

/**
 * A builder for nice tree decompositions. 
 * A tree decomposition of a graph G is a tree T and a map b:V(T) &rarr; Set&lt;V(G)&gt;, 
 * which satisfies the properties: 
 * <ul> <li>for every edge e in E(G), there is a node t in V(T) with e is a subset of b(v)</li>
 * <li>for all vertices v in V(G) the set {t &isin; V(T) | v &isin; b(t)} is non-empty and connected in T</li></ul>
 * 
 * A nice tree decomposition is a special tree decomposition, which satisfies the properties: 
 * <ul> <li>for root r &isin; V(T) and leaf l &isin; V(T): b(r)=b(t)=&empty;</li>
 * <li>every non-leaf node t &isin; V(T) is of one of the following three types: 
 * <ul><li>introduce node: t has exactly one child d and b(t) = b(d) &cup; w for some w &isin; V(G)</li>
 * <li>forget node: t has exactly one child d and b(t) &cup; w = b(d) for some w &isin; V(G)\b(t)</li>
 * <li>join node: t has exactly two child d_1, d_2 and b(t)=b(d_1)=b(d_2)</li></ul></ul>
 * 
 * @author Ira Justus Fesefeldt (PhoenixIra)
 *
 * @param <V> the vertices of G
 */
abstract public class NiceDecompositionBuilder<V>
{

    // resulting decomposition
    private Graph<Integer, DefaultEdge> decomposition;

    // map from decomposition nodes to the interval sets
    private Map<Integer, Set<V>> decompositionMap;

    // the root of the tree
    private Integer root;

    // next integer for vertex generation
    private Integer nextInteger;

    /**
     * constructor for all methods used in the abstract method
     */
    protected NiceDecompositionBuilder()
    {
        // creating objects
        decomposition = new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
        decompositionMap = new HashMap<Integer, Set<V>>();

        // create root
        root = 0;
        nextInteger = 1;
        decompositionMap.put(root, new HashSet<V>());
        decomposition.addVertex(root);
    }

    /**
     * getter for the next free Integer
     * 
     * @return unused integer
     */
    private Integer getNextInteger()
    {
        return nextInteger++;
    }

    /**
     * Method for adding a new join node
     * 
     * @param toJoin which nodes should get a join node
     * @return the new children of the join node, first element has no children, second element has
     *         the children of toJoin
     */
    protected Pair<Integer, Integer> addJoin(Integer toJoin)
    {
        Set<V> vertexSet = null;

        // new
        Integer vertexChildLeft = getNextInteger();
        decomposition.addVertex(vertexChildLeft);
        vertexSet = new HashSet<V>(decompositionMap.get(toJoin));
        decompositionMap.put(vertexChildLeft, vertexSet);

        // new current root
        Integer vertexChildRight = getNextInteger();
        decomposition.addVertex(vertexChildRight);
        vertexSet = new HashSet<V>(decompositionMap.get(toJoin));
        decompositionMap.put(vertexChildRight, vertexSet);

        // redirect all edges to new parent (should be just one!)
        for (Integer successor : Graphs.successorListOf(decomposition, toJoin)) {
            decomposition.removeEdge(toJoin, successor);
            decomposition.addEdge(vertexChildRight, successor);
        }
        // make children of parent vertex
        decomposition.addEdge(toJoin, vertexChildLeft);
        decomposition.addEdge(toJoin, vertexChildRight);

        return new Pair<Integer, Integer>(vertexChildLeft, vertexChildRight);
    }

    /**
     * Method for adding introducing nodes. It is only usable if the vertex currentVertex is a leaf.
     * It then adds the new introducing node as the child of currentVertex
     * 
     * @param introducingElement the element, which is introduced
     * @param currentVertex the vertex this element is introduced to
     * @return the next vertex
     */
    protected Integer addIntroduce(V introducingElement, Integer currentVertex)
    {
        Set<V> nextVertexSet = new HashSet<>(decompositionMap.get(currentVertex));
        nextVertexSet.add(introducingElement);
        Integer nextVertex = getNextInteger();
        decomposition.addVertex(nextVertex);
        decomposition.addEdge(currentVertex, nextVertex);
        decompositionMap.put(nextVertex, nextVertexSet);

        return nextVertex;
    }

    /**
     * method for adding forget nodes. It is only usable if the vertex currentVertex is a leaf.
     * It then adds the new forget node as the child of currentVertex
     * 
     * @param forgettingElement the element, which is forgotten
     * @param currentVertex the vertex this element is forgotten
     * @return the next vertex
     */
    protected Integer addForget(V forgettingElement, Integer currentVertex)
    {
        Set<V> nextVertexSet = new HashSet<>(decompositionMap.get(currentVertex));
        nextVertexSet.remove(forgettingElement);
        Integer nextVertex = getNextInteger();
        decomposition.addVertex(nextVertex);
        decomposition.addEdge(currentVertex, nextVertex);
        decompositionMap.put(nextVertex, nextVertexSet);

        return nextVertex;
    }

    /**
     * Adds to all current leaves in the decomposition forget/introduce nodes until only empty sets are leafes
     */
    protected void leafClosure()
    {
        Set<Integer> vertices = new HashSet<Integer>(decomposition.vertexSet());
        // make leave nodes
        for (Integer leaf : vertices) {
            // leaf is not a leaf
            if (Graphs.vertexHasSuccessors(decomposition, leaf))
                continue;

            // otherwise add forget until empty set
            Set<V> vertexSet = decompositionMap.get(leaf);
            Integer current = leaf;
            for (V forget : vertexSet) {
                current = addForget(forget, current);
            }
        }
    }

    /**
     * Getter for the decomposition as an unmodifiable, directed graph
     * 
     * @return the computed decomposition
     */
    public Graph<Integer, DefaultEdge> getDecomposition()
    {
        return new AsUnmodifiableGraph<>(decomposition);
    }

    /**
     * Getter for an unmodifiable map from integer nodes of the decomposition to the intervals of the interval
     * graph
     * 
     * @return a nodes to interval map
     */
    public Map<Integer, Set<V>> getMap()
    {
        return Collections.unmodifiableMap(decompositionMap);
    }

    /**
     * Getter for the root of the decomposition
     * 
     * @return a set of roots
     */
    public Integer getRoot()
    {
        return root;
    }

    @Override
    public String toString()
    {
        return getDecomposition() + "\n " + getMap();
    }
}
