package org.jgrapht.alg.interval;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.graph.interval.Interval;
import org.jgrapht.graph.interval.IntervalVertexPair;
import org.junit.Before;
import org.junit.Test;

public class ATFinderTest
{
    

    List<Interval<Integer>> intervalsSortedByStartingPoint = new ArrayList<>();
    List<Interval<Integer>> intervalsSortedByEndingPoint = new ArrayList<>();
    Map<Interval<Integer>, Integer> intervalToVertexMap = new HashMap<>();
    Map<Integer, IntervalVertexPair<Integer, Integer>> vertexToIntervalMap = new HashMap<>();
    
    @Before
    public void initAll() {
        intervalsSortedByStartingPoint = new ArrayList<>();
        intervalsSortedByEndingPoint = new ArrayList<>();
        intervalToVertexMap = new HashMap<>();
        vertexToIntervalMap = new HashMap<>();
    }
    
    private void addInterval(Integer vertex, Interval<Integer> interval) {
        
        boolean found = false;
        for(int i = 0; i < intervalsSortedByStartingPoint.size(); i++) {
            if(interval.getStart() < intervalsSortedByStartingPoint.get(i).getStart()) {
                intervalsSortedByStartingPoint.add(i, interval);
                found = true;
                break;
            }                
        }
        if(!found) {
            intervalsSortedByStartingPoint.add(interval);
        }
        
        found = false;
        for(int i = 0; i < intervalsSortedByEndingPoint.size(); i++) {
            if(interval.getEnd() < intervalsSortedByEndingPoint.get(i).getEnd()) {
                intervalsSortedByEndingPoint.add(i, interval);
                found = true;
                break;
            }                
        }
        if(!found) {
            intervalsSortedByEndingPoint.add(interval);
        }
        intervalToVertexMap.put(interval, vertex);
        vertexToIntervalMap.put(vertex, IntervalVertexPair.of(vertex, interval));
    }


    @Test
    public void test()
    {
        GraphBuilder<Integer, DefaultEdge, ? extends SimpleGraph<Integer, DefaultEdge>> builder 
            = SimpleGraph.createBuilder(DefaultEdge.class);
        
        
        
        builder.addEdge(0, 1);
        builder.addEdge(0, 2);
        builder.addEdge(0, 3);

        builder.addEdge(1, 4);
        builder.addEdge(2, 5);
        builder.addEdge(3, 6);
        
        
        addInterval(5, new Interval<Integer>(0,2));
        addInterval(2, new Interval<Integer>(1,4));
        addInterval(0, new Interval<Integer>(3,8));
        addInterval(3, new Interval<Integer>(5,6));
        addInterval(1, new Interval<Integer>(7,10));
        addInterval(4, new Interval<Integer>(9,11));

        ATFinder<Integer,DefaultEdge> atfinder = new ATFinder<>(builder.build(),
                                                    intervalsSortedByStartingPoint,
                                                    intervalsSortedByEndingPoint,
                                                    intervalToVertexMap,
                                                    vertexToIntervalMap,
                                                    6);
        
        List<Integer> asteroidalTriple = atfinder.getAsteroidalTriple();
        assertTrue(asteroidalTriple.contains(6));
        assertTrue(asteroidalTriple.contains(5));
        assertTrue(asteroidalTriple.contains(4));
        assertEquals(3,asteroidalTriple.size());
        
    }

}
