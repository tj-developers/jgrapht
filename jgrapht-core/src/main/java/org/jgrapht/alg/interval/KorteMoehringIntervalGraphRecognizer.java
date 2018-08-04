package org.jgrapht.alg.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.print.attribute.IntegerSyntax;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.alg.util.Pair;
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
    
    private ArrayList<Interval<Integer>> intervalsSortedByStartingPoint,
                                         intervalsSortedByEndingPoint;
    private Map<Interval<Integer>, V> intervalToVertexMap;
    private Map<V, IntervalVertexPair<V, Integer>> vertexToIntervalMap;
    
    private V asteroidalTriple1, asteroidalTriple2, asteroidalTriple3;

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
    
    private void computeIntervals() {

    }

    /**
     * Computes the asteroidal triple from the conflicting, simplicial vertex of the graph up to conflict vertex 
     * according to the perfect elimination order and the interval model up the this vertex given by
     * intervalsSortedByStartingPoint, intervalsSortedByEndingPoint, vertexToIntervalMap, intervalToVertexMap
     * 
     * 
     * @param conflictVertex
     */
    private void computeAT(V conflictVertex) {
        Interval<Integer> vertexInterval = intervalOfNeighbor(conflictVertex);
        List<Interval<Integer>> componentWoNeighbors = computeComponentWoNeighbors(conflictVertex, vertexInterval);
        List<Interval<Integer>> leftOfVertex = new ArrayList<>(componentWoNeighbors.size());
        List<Interval<Integer>> rightOfVertex = new ArrayList<>(componentWoNeighbors.size());
        for(Interval<Integer> interval: componentWoNeighbors) {
            if(interval.getEnd() < vertexInterval.getStart()) {
                leftOfVertex.add(interval);
            }
            if(vertexInterval.getEnd() < interval.getStart()) {
                rightOfVertex.add(interval);
            }
        }
        Map<Interval<Integer>,Integer> intervalToR = computeR(leftOfVertex,componentWoNeighbors);
        Map<Interval<Integer>,Integer> intervalToL = computeL(rightOfVertex,componentWoNeighbors);
        
        //generate sorted List with R values (ascending by ending point)
        List<Pair<Interval<Integer>,Interval<Integer>>> leftOfVertexWithRList = new ArrayList<>(leftOfVertex.size());
        for(int i = 0; i < intervalsSortedByEndingPoint.size(); i++) {
            Interval<Integer> currentInterval = intervalsSortedByEndingPoint.get(i);
            if(intervalToR.containsKey(currentInterval)) {
                Pair<Interval<Integer>,Interval<Integer>> rPair = new Pair<>(currentInterval,
                    new Interval<>(currentInterval.getEnd(),intervalToR.get(currentInterval)));
                leftOfVertexWithRList.add(rPair);
            }
        }
        
        //generate sorted List with L values (descending by starting point)
        List<Pair<Interval<Integer>,Interval<Integer>>> rightOfVertexWithLList = new ArrayList<>(rightOfVertex.size());
        for(int i = intervalsSortedByStartingPoint.size(); i >= 0; i--) {
            Interval<Integer> currentInterval = intervalsSortedByStartingPoint.get(i);
            if(intervalToL.containsKey(currentInterval)) {
                Pair<Interval<Integer>,Interval<Integer>> lPair = new Pair<>(currentInterval,
                    new Interval<>(currentInterval.getStart(),intervalToL.get(currentInterval)));
                rightOfVertexWithLList.add(lPair);
            }
        }

        //we found the asteroidal triple!
        Pair<Interval<Integer>,Interval<Integer>> asteroidalPair = searchPreceedingElements(leftOfVertexWithRList, rightOfVertexWithLList);
        
        asteroidalTriple1 = conflictVertex;
        asteroidalTriple2 = intervalToVertexMap.get(asteroidalPair.getFirst());
        asteroidalTriple3 = intervalToVertexMap.get(asteroidalPair.getSecond());
        
    }
    
    /**
     * Computes the interval the vertex would have got, if it would be added to the current interval model.
     * @param vertex
     * @return
     */
    private Interval<Integer> intervalOfNeighbor(V vertex){
        List<V> neighbors = Graphs.neighborListOf(graph, vertex);
        int minEndPoint = Integer.MAX_VALUE;
        int maxStartPoint = Integer.MIN_VALUE;
        for(V neighbor: neighbors) {
            
            //not relevant
            if(!vertexToIntervalMap.containsKey(neighbor)) 
                continue;
            
            
            Interval<Integer> current = vertexToIntervalMap.get(neighbor).getInterval();
            if(current.getStart() > maxStartPoint ) {
                maxStartPoint = current.getStart();
            }
            if(current.getEnd() < minEndPoint ) {
                minEndPoint = current.getEnd();
            }
        }
        return new Interval<Integer>(maxStartPoint, minEndPoint);
    }
    
    /**
     * Computed the component spanning vertexInterval without the neighbor of vertex
     * @param vertex
     * @param vertexInterval
     * @return
     */
    private List<Interval<Integer>> computeComponentWoNeighbors(V vertex, Interval<Integer> vertexInterval) {
        List<Interval<Integer>> componentWoNeighbors = new ArrayList<>(graph.vertexSet().size());
        Set<Interval<Integer>> currentVertices = new HashSet<>();
        int j = 0;
        boolean foundComponent = false;
        for(int i = 0; i<intervalsSortedByStartingPoint.size(); i++) {
            while(intervalsSortedByEndingPoint.get(j).getEnd() < intervalsSortedByStartingPoint.get(i).getStart()) {
                //the component spans vertexInterval (since we stop as soon as we now find the end of the component)
                if(intervalsSortedByEndingPoint.get(j).getEnd() >= vertexInterval.getStart()) {
                    foundComponent = true;
                }
                
                //do not consider the neighborhood - it still is a component since the component has an AT with vertex
                if(!graph.containsEdge(vertex, intervalToVertexMap.get(intervalsSortedByEndingPoint.get(j)))) {
                    componentWoNeighbors.add(intervalsSortedByEndingPoint.get(j));
                }
                //unmark interval
                currentVertices.remove(intervalsSortedByEndingPoint.get(j));
                j++;
                
            }
            
            //correct component
            if(currentVertices.isEmpty() 
                && foundComponent 
                && intervalsSortedByStartingPoint.get(i).getStart() > vertexInterval.getEnd()) {
                
                return componentWoNeighbors;
            }
            
            //wrong component
            if(currentVertices.isEmpty() 
                && !componentWoNeighbors.isEmpty() 
                && !foundComponent) {
                
                componentWoNeighbors = new ArrayList<>();
            }
            
            currentVertices.add(intervalsSortedByStartingPoint.get(i));

        }
        return new ArrayList<>();
    }
    
    /**
     * computes the R value for intervalsForR based on intervalModelComponent and the class interval model and graph.
     * @param intervalsForR
     * @param intervalModelComponent
     * @return
     */
    private Map<Interval<Integer>, Integer> computeR(
        List<Interval<Integer>> intervalsForR, List<Interval<Integer>> intervalModelComponent)
    {
        List<Interval<Integer>> intervalsForRSortedByEndpoint =
            new ArrayList<>(intervalsForR.size());
        Set<Interval<Integer>> componentSet = new HashSet<>(intervalModelComponent);

        // sort the intervals correctly
        for (int i=0; i<intervalsSortedByEndingPoint.size();i++) {
            Interval<Integer> interval = intervalsSortedByEndingPoint.get(i);
            if (componentSet.contains(interval)) {
                intervalsForRSortedByEndpoint.add(interval);
            }
        }

        // compute inductively all R
        Map<Interval<Integer>, Integer> intervalToR = new HashMap<>(intervalsForR.size());
        for (int i = intervalsForRSortedByEndpoint.size() - 1; i >= 0; i--) {
            Interval<Integer> currentInterval = intervalsForRSortedByEndpoint.get(i);

            // set to inf at the start
            int minRValue = Integer.MAX_VALUE;

            // iterate over neighbors
            List<V> neighbors =
                Graphs.neighborListOf(graph, intervalToVertexMap.get(currentInterval));
            for (V neighbor : neighbors) {
                Interval<Integer> neighborInterval =
                    vertexToIntervalMap.get(neighbor).getInterval();

                // neighbor not in the component
                if (!componentSet.contains(neighborInterval)) {
                    // so it must be an neighbor of the conflict vertex
                    int rValue = currentInterval.getEnd();
                    if (minRValue > rValue) {
                        minRValue = rValue;
                    }
                }
                // neighbor has an arc
                else if (neighborInterval.getEnd() > currentInterval.getEnd()) {
                    int rValueNeighbor = intervalToR.get(neighborInterval);
                    if (minRValue > rValueNeighbor) {
                        minRValue = rValueNeighbor;
                    }
                }
            }
            intervalToR.put(currentInterval, minRValue);
        }

        return intervalToR;
    }
    
    
    /**
     * computes the L value for intervalsForR based on intervalModelComponent and the class interval model and graph.
     * @param intervalsForL
     * @param intervalModelComponent
     * @return
     */
    private Map<Interval<Integer>, Integer> computeL(
        List<Interval<Integer>> intervalsForL, List<Interval<Integer>> intervalModelComponent)
    {
        List<Interval<Integer>> intervalsForRSortedReverseByStarting =
            new ArrayList<>(intervalsForL.size());
        Set<Interval<Integer>> componentSet = new HashSet<>(intervalModelComponent);

        // sort the intervals correctly
        for (int i=intervalsSortedByStartingPoint.size()-1; i>=0; i--) {
            Interval<Integer> interval = intervalsSortedByStartingPoint.get(i);
            if (componentSet.contains(interval)) {
                intervalsForRSortedReverseByStarting.add(interval);
            }
        }

        // compute inductively all L
        Map<Interval<Integer>, Integer> intervalToL = new HashMap<>(intervalsForL.size());
        for (int i = 0; i < intervalsForRSortedReverseByStarting.size(); i++) {
            Interval<Integer> currentInterval = intervalsForRSortedReverseByStarting.get(i);

            // set to inf at the start
            int maxLValue = Integer.MIN_VALUE;

            // iterate over neighbors
            List<V> neighbors =
                Graphs.neighborListOf(graph, intervalToVertexMap.get(currentInterval));
            for (V neighbor : neighbors) {
                Interval<Integer> neighborInterval =
                    vertexToIntervalMap.get(neighbor).getInterval();

                // neighbor not in the component
                if (!componentSet.contains(neighborInterval)) {
                    // so it must be an neighbor of the conflict vertex
                    int lValue = currentInterval.getStart();
                    if (maxLValue > lValue) {
                        maxLValue = lValue;
                    }
                }
                // neighbor has an arc
                else if (neighborInterval.getStart() < currentInterval.getStart()) {
                    int lValueNeighbor = intervalToL.get(neighborInterval);
                    if (maxLValue > lValueNeighbor) {
                        maxLValue = lValueNeighbor;
                    }
                }
            }
            intervalToL.put(currentInterval, maxLValue);
        }

        return intervalToL;
    }
    
    /**
     * Search for preceedings Elements
     * @param x
     * @param y
     * @return
     */
    private Pair<Interval<Integer>,Interval<Integer>> searchPreceedingElements(
        List<Pair<Interval<Integer>,Interval<Integer>>> x, List<Pair<Interval<Integer>,Interval<Integer>>> y){
        if(x.isEmpty() || y.isEmpty())
            return null;
        int xi=0;
        int yi=y.size()-1;
        while(xi < x.size() && yi >= 0) {
            if(x.get(xi).getSecond().getStart() >= y.get(yi).getSecond().getStart()) {
                xi++;
            }else if(x.get(xi).getSecond().getEnd() >= y.get(yi).getSecond().getEnd()) {
                yi++;
            }else {
                return Pair.of(x.get(xi).getFirst(), y.get(yi).getFirst());
            }
        }
        return null;
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
