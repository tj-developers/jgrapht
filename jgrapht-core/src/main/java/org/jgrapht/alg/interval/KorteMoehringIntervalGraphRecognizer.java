package org.jgrapht.alg.interval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.interval.*;

/**
 * TODO: better Javadoc
 * @author Ira Justus Fesefeldt (PhoenixIra)
 * @author Timofey Chudakov
 *
 * @param <V> the vertex type of the graph
 * @param <E> the edge type of the graph
 */
public class KorteMoehringIntervalGraphRecognizer<V, E> implements IntervalGraphRecognizerInterface<V>
{

    // The recognized graph
    private Graph<V, E> graph;
    
    ChordalityInspector<V, E> chorInspec;

    private MPQNode treeRoot;
    
    private HashMap<V,Set<MPQNodeSetElement>> vertexToListPositionMap;
    
    private boolean isIntervalGraph;
    private boolean isChordal;

    /**
     * Constructor for the algorithm
     * @param graph the graph which should be recognized
     */
    public KorteMoehringIntervalGraphRecognizer(Graph<V, E> graph)
    {
        this.graph = graph;
        chorInspec = new ChordalityInspector<>(graph);
        treeRoot = new PNode(null,null);
    }

    /**
     * TODO: better Javadoc
     * 
     * the Korte-Moehring Algorithm, which tests the graphs with an MPQ tree for an interval representation.
     * If the algorithm returns true, we can computed an interval representation of the MPQ Tree
     * If the algorithm returns false, we can computed an counter example of the MPQ Tree
     */
    private void testIntervalGraph()
    {

        //check for chordality
        isChordal = chorInspec.isChordal();
        if(!isChordal) 
        {
            isIntervalGraph = false;
            return;
        }
        
        // init all relevant objects
        Map<V, Integer> vertexOrder = getVertexInOrder(chorInspec.getPerfectEliminationOrder());
        // iterate over the perfect elimination order
        for (V u : chorInspec.getPerfectEliminationOrder()) {
            // calculate Adj(u) - the predecessors of u
            Set<V> predecessors = getPredecessors(vertexOrder, u);

            // special case for predecessors is empty
            if (predecessors.isEmpty()) {
                addEmptyPredecessors(u);
                continue;
            }

            // labeling phase: 
            // 1 if one but not all vertices in a PQNode is a predecessor
            // 2/inf if all vertices in a PQNode is a predecessor
            Map<MPQNode,Integer> positiveLabels = labelTree(predecessors);
            
            // test phase:
            // check for path of positive labels
            if(!testPath(positiveLabels.keySet()) 
            //check if outer sections of Q nodes N contain predecessors intersection V(N)
                | !testOuterSectionsOfQNodes(positiveLabels.keySet(), predecessors))
            {
                //then this is not an interval graph
                isIntervalGraph = false;
                return;
            }
            
            // update phase:
            // generate the path
            List<MPQNode> path = getPath(positiveLabels.keySet());
            
            //get lowest positive node in path
            MPQNode Nsmall = getNSmall(path, positiveLabels);
            
            //get highest non-inf node in path
            MPQNode Nbig = getNBig(path, positiveLabels);
            
            //update MPQ Tree
            if(Nsmall.equals(Nbig))
                addVertexToLeaf(u,path);
            else
                changedPathToTemplates(u,path,Nsmall,Nbig);

        }
    }
    
    
    
    
    
    /**
     * Returns the predecessors of {@code vertex} in the order defined by {@code map}. More
     * precisely, returns those of {@code vertex}, whose mapped index in {@code map} is less then
     * the index of {@code vertex}.
     *
     * @param vertexInOrder defines the mapping of vertices in {@code graph} to their indices in
     *        order.
     * @param vertex the vertex whose predecessors in order are to be returned.
     * @return the predecessors of {@code vertex} in order defines by {@code map}.
     */
    private Set<V> getPredecessors(Map<V, Integer> vertexInOrder, V vertex)
    {
        Set<V> predecessors = new HashSet<>();
        Integer vertexPosition = vertexInOrder.get(vertex);
        Set<E> edges = graph.edgesOf(vertex);
        for (E edge : edges) {
            V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
            Integer destPosition = vertexInOrder.get(oppositeVertex);
            if (destPosition < vertexPosition) {
                predecessors.add(oppositeVertex);
            }
        }
        return predecessors;
    }

    /**
     * Returns a map containing vertices from the {@code vertexOrder} mapped to their indices in
     * {@code vertexOrder}.
     *
     * @param vertexOrder a list with vertices.
     * @return a mapping of vertices from {@code vertexOrder} to their indices in
     *         {@code vertexOrder}.
     */
    private Map<V, Integer> getVertexInOrder(List<V> vertexOrder)
    {
        Map<V, Integer> vertexInOrder = new HashMap<>(vertexOrder.size());
        int i = 0;
        for (V vertex : vertexOrder) {
            vertexInOrder.put(vertex, i++);
        }
        return vertexInOrder;
    }
    
    
    /**
     * Changed the MPQ Tree if u has no predecessors.
     * Adds a new leaf node with the bag of this vertex to the root.
     * 
     * @param u the vertex to be added to the MPQ Tree
     */
    private void addEmptyPredecessors(V u)
    {
        MPQNodeSetElement bag = new MPQNodeSetElement(u);
        MPQNode leaf = new PNode(null,bag);
        treeRoot.add(leaf);
        leaf.parent = treeRoot;
            
    }
    
    /**
     * TODO: Better Javadoc
     * Label every positive vertex in the MPQ Tree
     * 
     * @param predecessors the predecessors which are used to label the vertices in the tree
     * @return the labeling of all positive labeled vertices
     */
    private Map<MPQNode,Integer> labelTree(Set<V> predecessors)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * TODO: Better Javadoc
     * tests if positiveLabels form a path
     * 
     * @param positiveLabels the vertices which should form a path
     * @return true iff it forms a path
     */
    private boolean testPath(Set<MPQNode> positiveLabels)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /**
     * TODO: Better Javadoc
     * tests if an outer section of every Q nodes N in positive labels contains predecessors intersection V(N)
     * 
     * @param positiveLabels the positive vertices
     * @param predecessors the predecessors of u
     * @return true iff it fulfills the condition
     */
    private boolean testOuterSectionsOfQNodes(Set<MPQNode> positiveLabels, Set<V> predecessors)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /**
     * TODO: better Javadoc
     * computes a path from the root to a leaf, containing all positive vertices
     * 
     * @param positiveLabels the vertices which forms a path
     * @return the path from root to a leaf
     */
    private List<MPQNode> getPath(Set<MPQNode> positiveLabels)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * TODO: better Javadoc
     * computes the smallest vertex N of the Tree which has a positive label
     * 
     * @param path the path from root to leaf
     * @param positiveLabels the map from nodes to positive labels
     * @return smalles vertex N with positive label
     */
    private MPQNode getNSmall(List<MPQNode> path, Map<MPQNode, Integer> positiveLabels)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * TODO: better Javadoc
     * computes the highest vertex N of the tree which is non-empty and non-inf
     * 
     * @param path the path from root to leaf
     * @param positiveLabels the map from nodes to positive labels
     * @return highest non-empty, non-inf vertex N
     */
    private MPQNode getNBig(List<MPQNode> path, Map<MPQNode, Integer> positiveLabels)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * TODO: better Javadoc
     * Adds the vertex u to the leaf of the path
     * 
     * @param u the vertex to be added
     * @param path the path of the leaf
     */
    private void addVertexToLeaf(V u, List<MPQNode> path)
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * TODO: better Javadoc
     * Checks the path for specifig patterns and changes every node accordingly
     * 
     * @param u the vertex to add to the tree
     * @param path the path of vertices to be changed
     * @param nSmall the smalles positive node in path
     * @param nBig the highest non-empty, non-inf node in path
     */
    private void changedPathToTemplates(V u, List<MPQNode> path, MPQNode nSmall, MPQNode nBig)
    {
        // TODO Auto-generated method stub
        
    }

    
    
    
    
    @Override
    public boolean isIntervalGraph()
    {
        return isIntervalGraph;
    }
    
    @Override
    public List<Interval<Integer>> getIntervalsSortedByStartingPoint()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<V, IntervalVertexPair<V, Integer>> getVertexToIntervalMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Map<Interval<Integer>, V> getIntervalToVertexMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * TODO: better javadoc
     * 
     * the hole in the graph as an counter example for chordality
     * @return a hole if the graph is not chordal, or null if the graph is chordal.
     */
    public GraphPath<V,E> getHole()
    {
        return chorInspec.getHole();
    }
    
    /**
     * TODO: better javadoc
     * 
     * the Umbrella sub graph in the graph iff the graph is chordal but not an interval graph
     * @return an umbrella if the graph is not an intervalgraph, or null if the graph is an intervalgraph.
     */
    public Graph<V,E> getUmbrellaSubGraph()
    {
        // TODO implement
        return null;
    }
    
    
    
    
    
    
    
    
    
    
    private abstract class MPQNode
    {
        MPQNode left;
        MPQNode right;
        MPQNode parent;
        
        MPQNodeSetElement bag;
        
        MPQNode(MPQNodeSetElement bag) {
            this.bag = bag;
        }
        
        abstract void add(MPQNode newChild);
    }
    
    private class PNode extends MPQNode
    {
        MPQNode children;
        
        PNode(MPQNode child, MPQNodeSetElement bag) {
            super(bag);
            this.children = child;
        }
        
        void add(MPQNode child) {
            child.parent = this;
            if(this.children == null)
            {
                child.left = child;
                child.right = child;
                this.children = child;
            }else {
                child.left = this.children;
                child.right = this.children.left;
                this.children.left.right = child;
                this.children.left = child;
            }
        }
    }
    
    private class QNode extends MPQNode
    {
        MPQNode leftestSection;
        MPQNode rightestSection;
        
        QNode(MPQNode section, MPQNodeSetElement bag) {
            super(bag);
            this.leftestSection = section;
            this.rightestSection = section;
        }
        
        void add(MPQNode child) {
            //TODO
        }
        
    }
    
    private class QSectionNode extends MPQNode
    {
        MPQNode child;
        
        QSectionNode(MPQNode child, MPQNodeSetElement bag) {
            super(bag);
            
        }
        
        void add(MPQNode child) {
            throw new UnsupportedOperationException();
        }
    }
    
    private class MPQNodeSetElement
    {
        V vertex;
        MPQNodeSetElement left;
        MPQNodeSetElement right;
        MPQNode owner;
        
        MPQNodeSetElement(V vertex) {
            this.vertex = vertex;
        }
    }
}
