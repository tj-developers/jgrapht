package org.jgrapht.alg.decomposition;

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.decomposition.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.interval.Interval;
import org.jgrapht.util.SupplierUtil;
import org.junit.*;

public class IntervalNicePathDecompositionTest
{

    
    /**
     * Interval Graph representation of the graph to test:
     * _ ___________ ___________
     *    ____________ ___________
     *     _____________ ___________
     *     ...
     */
    @Test
    public void testIntervalgraphDecompositionForRegularGraphs()
    {
        //graph
        Graph<Integer,DefaultEdge> g = new DefaultUndirectedGraph<>(DefaultEdge.class);
        g.addVertex(-1);
        for(int i = 0; i<10; i++)
        	g.addVertex(i);
        
        for(int i = 0; i<10; i++) {
        	for(int j = i+1; j<10; j++) {
        		if((i%2)==0  && (j%2)==0)
        			g.addEdge(i, j);
        		
        		if((i%2)==1 && (j%2)==0 && i<j)
        			g.addEdge(i, j);
        		
        		if((i%2)==1 && (j%2)==1)
        			g.addEdge(i, j);
        	}
        }
        
        //compute decomposition
        IntervalNicePathDecomposition<Integer,Integer> decomp = 
            IntervalNicePathDecomposition.<Integer,DefaultEdge>create(g);
        assertNotNull("graph was detected as not an interval graph", decomp);
        
        //test for nice decomposition
        assertTrue(NiceTreeDecompositionTestUtil.isNiceDecomposition(decomp.getDecomposition(), decomp.getMap(), decomp.getRoot()));
        assertTrue(NiceTreeDecompositionTestUtil.isDecomposition(g, decomp.getDecomposition(), decomp.getMap()));
    }

    /**
     * Test for the create method of lists of intervals
     * Representation:
     * 
     * __ __ __
     *        __
     *         __
     *           ...
     *             __
     *             ___
     *             ____
     *             ...
     */
    @Test
    public void testIntervalgraphDecompositionForIntervalLists()
    {
        List<Interval<Integer>> list = new ArrayList<Interval<Integer>>();
        //unconnected
        list.add(new Interval<Integer>(-4,-3));
        list.add(new Interval<Integer>(-2,-1));
        //just path
        for(int i = 0; i<10; i++)
        {
            list.add(new Interval<Integer>(i,i+1));
        }
        //and to spice it up, a clique
        for(int i = 0; i<10; i++)
        {
            list.add(new Interval<Integer>(10,10+i));
        }
        IntervalNicePathDecomposition<Integer,Integer> decompalg = 
            IntervalNicePathDecomposition.<Integer,Integer>create(list,SupplierUtil.createIntegerSupplier());
        Graph<Integer,DefaultEdge> decomp = decompalg.getDecomposition();
        Map<Integer,Set<Integer>> map = decompalg.getMap();
        Integer root = decompalg.getRoot();
        assertTrue(NiceTreeDecompositionTestUtil.isNiceDecomposition(decomp,map,root));
        
    }

}

