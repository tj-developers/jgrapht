package org.jgrapht.alg.treedecompostion;

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.treedecomposition.*;
import org.jgrapht.event.*;
import org.jgrapht.graph.*;
import org.jgrapht.intervalgraph.*;
import org.jgrapht.intervalgraph.interval.*;
import org.junit.*;

public class IntervalgraphDecompositionTest
{

    @Test
    public void testIntervalgraphDecompositionForRegularGraphs()
    {
        Graph<Integer,DefaultEdge> g = new DefaultUndirectedGraph<>(DefaultEdge.class);
        //TODO: change g
        //IntervalgraphDecomposition<Integer,IntervalVertex<Integer,Integer>> decompositionAlg = new IntervalgraphDecomposition<>(g);
        //Graph<Set<Integer>,DefaultEdge> decomp = decompositionAlg.getTreeDecomposition();
        //TODO: test here
        //assertNotEquals(decomp,decomp);
    }

    @Test
    public void testIntervalgraphDecompositionForIntervalGraphs()
    {
        IntervalGraphInterface<Integer> ig = new CenteredIntervalTree<>();
        //TODO: change ig
        //IntervalgraphDecomposition<Integer,DefaultEdge,Integer> decompositionAlg = new IntervalgraphDecomposition<>(ig);
    }

    @Test
    public void testIntervalgraphDecompositionForIntervalLists()
    {
        List<IntervalVertex<Integer,Integer>> list = new ArrayList<IntervalVertex<Integer,Integer>>();
        for(int i = 0; i<1; i++)
        {
        }
    }

    @Test
    public void testGetTreeDecomposition()
    {
        fail("Not yet implemented");
    }

}
