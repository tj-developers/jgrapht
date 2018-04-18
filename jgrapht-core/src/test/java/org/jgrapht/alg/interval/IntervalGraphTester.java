/*
 * (C) Copyright 2017-2018, by TODO and Contributors.
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
package org.jgrapht.alg.interval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.cycle.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;
import org.junit.*;

/**
 * Test that correct interval graphs are detected and presentation is correct as well
 * 
 * @author Ira Justus Fesefeldt
 */
public class IntervalGraphTester
{
    
    @Test
    public void completeGraphTest()
    {
        for(int i=0; i<10; i++) {
            CompleteGraphGenerator<Integer,DefaultEdge> cgg = 
                new CompleteGraphGenerator<Integer,DefaultEdge>(i);
            Graph<Integer,DefaultEdge> cg =
                new SimpleGraph<>(DefaultEdge.class);
            cgg.generateGraph(cg, new IntegerVertexFactory(), null);
            
            //Every complete Graph is an interval graph
            assertTrue(GraphTests.isIntervalGraph(cgg));
        }
            
    }
    
    @Test
    public void circleGraphTest()
    {
        for(int i=4; i<10; i++) {
            Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
            Graphs.addEdgeWithVertices(graph, 0, 1);
            Graphs.addEdgeWithVertices(graph, 1, 2);
            Graphs.addEdgeWithVertices(graph, 2, 3);
            Graphs.addEdgeWithVertices(graph, 3, 4);
            Graphs.addEdgeWithVertices(graph, 4, 5);
            Graphs.addEdgeWithVertices(graph, 5, 0);

            //Every circle graph bigger than 4 is not an interval graph
            assertFalse(GraphTests.isIntervalGraph(graph));
        }
    }
    
    @Test
    public void complexGraphTest() {
        /*
         * 0: [0,2], 1: [1,4], 2: [3,8], 3: [3,6], 4: [3,8], 5: [5,8], 6: [9,10]
         *  An arbitrary example for an interval graph
         * 
         */
        
        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        Graphs.addEdgeWithVertices(graph, 0, 1);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 1, 3);
        Graphs.addEdgeWithVertices(graph, 1, 4);
        Graphs.addEdgeWithVertices(graph, 2, 3);
        Graphs.addEdgeWithVertices(graph, 2, 4);
        Graphs.addEdgeWithVertices(graph, 2, 5);
        Graphs.addEdgeWithVertices(graph, 3, 4);
        Graphs.addEdgeWithVertices(graph, 3, 5);
        Graphs.addEdgeWithVertices(graph, 4, 5);
        graph.addVertex(6);
        
        
        assertTrue(GraphTests.isIntervalGraph(graph));
        
        //Adding (5,6) and (6,0) creates an circle
        graph.addEdge(5, 6);
        graph.addEdge(6, 0);
        assertFalse(GraphTests.isIntervalGraph(graph));
    }

}
