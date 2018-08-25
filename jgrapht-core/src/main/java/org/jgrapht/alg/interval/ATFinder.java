package org.jgrapht.alg.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.interval.Interval;
import org.jgrapht.graph.interval.IntervalVertexPair;

public class ATFinder<V,E>
{

    private List<Interval<Integer>> intervalsSortedByStartingPoint, intervalsSortedByEndingPoint;
    private Map<Interval<Integer>, V> intervalToVertexMap;
    private Map<V, IntervalVertexPair<V, Integer>> vertexToIntervalMap;
    private Graph<V,E> subgraph;
    private V asteroidalTriple1, asteroidalTriple2, asteroidalTriple3;
    private V conflictVertex;

    /**
     *
     * @param subgraph null
     * @param intervalsSortedByStartingPoint null
     * @param intervalsSortedByEndingPoint null
     * @param intervalToVertexMap null
     * @param vertexToIntervalMap null
     * @param conflictVertex null
     */
    public ATFinder(Graph<V,E> subgraph, 
            List<Interval<Integer>> intervalsSortedByStartingPoint, 
            List<Interval<Integer>> intervalsSortedByEndingPoint,
            Map<Interval<Integer>, V> intervalToVertexMap,
            Map<V, IntervalVertexPair<V, Integer>> vertexToIntervalMap,
            V conflictVertex) {
        this.subgraph = subgraph;
        this.intervalsSortedByEndingPoint = intervalsSortedByEndingPoint;
        this.intervalsSortedByStartingPoint = intervalsSortedByStartingPoint;
        this.intervalToVertexMap = intervalToVertexMap;
        this.vertexToIntervalMap = vertexToIntervalMap;
        this.conflictVertex = conflictVertex;
    }

    /**
     *
     * @return
     */
    public List<V> getAsteroidalTriple(){
        computeAT();
        List<V> triple = new ArrayList<>(3);
        triple.add(asteroidalTriple1);
        triple.add(asteroidalTriple2);
        triple.add(asteroidalTriple3);
        return Collections.unmodifiableList(triple);
    }

    /**
     * Computes the asteroidal triple from the conflicting, simplicial vertex of the graph up to conflict vertex
     * according to the perfect elimination order and the interval model up the this vertex given by
     * intervalsSortedByStartingPoint, intervalsSortedByEndingPoint, vertexToIntervalMap, intervalToVertexMap
     */
    private void computeAT() {
        Interval<Integer> vertexInterval = intervalOfNeighbor(conflictVertex);
        List<Interval<Integer>> componentWoNeighbors = computeComponentWoNeighbors(conflictVertex, vertexInterval);
        Set<Interval<Integer>> leftOfVertex = new HashSet<>(componentWoNeighbors.size());
        Set<Interval<Integer>> rightOfVertex = new HashSet<>(componentWoNeighbors.size());
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
        for(int i = intervalsSortedByStartingPoint.size()-1; i >= 0; i--) {
            Interval<Integer> currentInterval = intervalsSortedByStartingPoint.get(i);
            if(intervalToL.containsKey(currentInterval)) {
                Pair<Interval<Integer>,Interval<Integer>> lPair = new Pair<>(currentInterval,
                    new Interval<>(intervalToL.get(currentInterval),currentInterval.getStart()));
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
     * @param vertex null
     * @return null
     */
    private Interval<Integer> intervalOfNeighbor(V vertex){
        List<V> neighbors = Graphs.neighborListOf(subgraph, vertex);
        int minEndPoint = Integer.MAX_VALUE;
        int maxStartPoint = Integer.MIN_VALUE;
        for(V neighbor: neighbors) {
            
            
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
     * @param vertex null
     * @param vertexInterval null
     * @return null
     */
    private List<Interval<Integer>> computeComponentWoNeighbors(V vertex, Interval<Integer> vertexInterval) {
        List<Interval<Integer>> componentWoNeighbors = new ArrayList<>(subgraph.vertexSet().size());
        Set<Interval<Integer>> currentVertices = new HashSet<>();
        int j = 0;
        int i = 0;
        boolean foundComponent = false;
        //iterate over all points from left to right
        while(i< intervalsSortedByStartingPoint.size()) {
            Interval<Integer> currentStartingInterval = intervalsSortedByStartingPoint.get(i);
            
            //mark interval
            currentVertices.add(currentStartingInterval);
            
            //add only neighbors to current component
            if(!isNeighborOf(vertex, currentStartingInterval)) {
                componentWoNeighbors.add(currentStartingInterval);
            }
            
            //we found an interval which crosses vertexInterval
            if(vertexInterval.getStart() <= intervalsSortedByStartingPoint.get(i).getEnd()) {
                foundComponent = true;
            }
            
            
            while(intervalsSortedByEndingPoint.get(j).getEnd() < intervalsSortedByStartingPoint.get(i).getStart()) {                
                //unmark interval
                currentVertices.remove(intervalsSortedByEndingPoint.get(j));
                
                //next ending interval
                j++;
            }
            
            //component is to end
            if(currentVertices.isEmpty()) {
                //corrent component?
                if(foundComponent) {
                    break;
                }else {
                    componentWoNeighbors = new ArrayList<>();
                }
            }
            
           //next starting interval
           i++; 

        }
        //is last component the correct component?
        if(foundComponent) {
            return componentWoNeighbors;
        }else {
            return new ArrayList<>();
        }
    }
    
    /**
     * computes the R value for intervalsForR based on intervalModelComponent and the class interval model and graph.
     * @param intervalsForR
     * @param intervalModelComponent
     * @return
     */
    private Map<Interval<Integer>, Integer> computeR(
        Set<Interval<Integer>> intervalsForR, List<Interval<Integer>> intervalModelComponent)
    {
        List<Interval<Integer>> intervalsForRSortedByEndpoint =
            new ArrayList<>(intervalsForR.size());
        Set<Interval<Integer>> componentSet = new HashSet<>(intervalModelComponent);

        // sort the intervals correctly
        for (int i=0; i<intervalsSortedByEndingPoint.size();i++) {
            Interval<Integer> interval = intervalsSortedByEndingPoint.get(i);
            if (componentSet.contains(interval) && intervalsForR.contains(interval)) {
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
                Graphs.neighborListOf(subgraph, intervalToVertexMap.get(currentInterval));
            for (V neighbor : neighbors) {
                
                
                Interval<Integer> neighborInterval =
                    vertexToIntervalMap.get(neighbor).getInterval();

                int rValue = minRValue;
                // neighbor not in the component without conflict neighbors
                if (!componentSet.contains(neighborInterval)) {
                    // so it must be an neighbor of the conflict vertex
                    rValue = currentInterval.getEnd();

                }
                // neighbor has an arc
                else if (neighborInterval.getEnd() > currentInterval.getEnd()) {
                    //if it is R, take the value
                    if(intervalsForR.contains(neighborInterval)) {
                        rValue = intervalToR.get(neighborInterval);
                    }
                    //else it has a neighbor of the ciritcal neighbors
                    else {
                        rValue = neighborInterval.getEnd();
                    }
                }
                if (minRValue > rValue) {
                    minRValue = rValue;
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
        Set<Interval<Integer>> intervalsForL, List<Interval<Integer>> intervalModelComponent)
    {
        List<Interval<Integer>> intervalsForLSortedByStartpoint =
            new ArrayList<>(intervalsForL.size());
        Set<Interval<Integer>> componentSet = new HashSet<>(intervalModelComponent);

        // sort the intervals correctly
        for (int i=0; i<intervalsSortedByStartingPoint.size();i++) {
            Interval<Integer> interval = intervalsSortedByStartingPoint.get(i);
            if (componentSet.contains(interval) && intervalsForL.contains(interval)) {
                intervalsForLSortedByStartpoint.add(interval);
            }
        }
        // compute inductively all L
        Map<Interval<Integer>, Integer> intervalToL = new HashMap<>(intervalsForL.size());
        for (int i = 0; i < intervalsForLSortedByStartpoint.size(); i++) {
            Interval<Integer> currentInterval = intervalsForLSortedByStartpoint.get(i);
            
            // set to inf at the start
            int minLValue = Integer.MIN_VALUE;

            // iterate over neighbors
            List<V> neighbors =
                Graphs.neighborListOf(subgraph, intervalToVertexMap.get(currentInterval));
            for (V neighbor : neighbors) {
                
                
                Interval<Integer> neighborInterval =
                    vertexToIntervalMap.get(neighbor).getInterval();

                int lValue = minLValue;
                // neighbor not in the component without conflict neighbors
                if (!componentSet.contains(neighborInterval)) {
                    // so it must be an neighbor of the conflict vertex
                    lValue = currentInterval.getStart();

                }
                // neighbor has an arc
                else if (neighborInterval.getStart() < currentInterval.getStart()) {
                    //if it is L, take the value
                    if(intervalsForL.contains(neighborInterval)) {
                        lValue = intervalToL.get(neighborInterval);
                    }
                    //else it has a neighbor of the ciritcal neighbors
                    else {
                        lValue = neighborInterval.getStart();
                    }
                }
                if (minLValue < lValue) {
                    minLValue = lValue;
                }
            }
            intervalToL.put(currentInterval, minLValue);
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
                yi--;
            }else {
                return Pair.of(x.get(xi).getFirst(), y.get(yi).getFirst());
            }
        }
        return null;
    }
    
    private boolean isNeighborOf(V vertex, Interval<Integer> interval) {
        return subgraph.containsEdge(vertex, intervalToVertexMap.get(interval));
    }
}
