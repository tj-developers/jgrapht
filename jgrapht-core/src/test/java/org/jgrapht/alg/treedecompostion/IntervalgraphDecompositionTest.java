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

    public <V,E> boolean isNiceTreeDecomposition(Graph<Set<V>,E> graph, Set<V> root){
        if(root.size() != 0) return false;
        Queue<Set<V>> queue = new LinkedList<Set<V>>();
        queue.add(root);
        while(!queue.isEmpty())
        {
            Set<V> current = queue.poll();
            List<Set<V>> neighbors = Graphs.neighborListOf(graph, current);
            if(neighbors.size() == 0 && current.size() == 0) continue; //leaf node
            if(neighbors.size() == 1) //forget or introduce
            {
                Set<V> next = neighbors.get(0);
                queue.add(next);
                Set<V> union = new HashSet<V>(current);
                union.addAll(next);
                if(union.size() == next.size() || union.size() == current.size())
                {
                    if(current.size() == next.size()+1) continue; //introduce
                    else if(current.size()+1 == next.size()) continue; //forget
                }
            }
            if(neighbors.size() == 2) //join
            {
                Set<V> first = neighbors.get(0);
                Set<V> second = neighbors.get(1);
                queue.add(first);
                queue.add(second);
                Set<V> union = new HashSet<V>(current);
                union.addAll(first);
                union.addAll(second);
                if(union.size() == current.size() 
                && union.size() == first.size() 
                && union.size() == second.size()) 
                    continue; //join node!
            }
            return false; //no valid node!
        }
        return true;
    }
    
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
        List<Interval<Integer>> list = new ArrayList<Interval<Integer>>();
        //just path
        for(int i = 0; i<10; i++)
        {
            list.add(new Interval<Integer>(i,i+1));
        }
        //and to spice it up, a clique
        for(int i = 0; i<5; i++)
        {
            list.add(new Interval<Integer>(10,10+i));
        }
        IntervalgraphDecomposition<Integer> decompalg = new IntervalgraphDecomposition<>(list);
        Graph<Set<Interval<Integer>>,DefaultEdge> decomp = decompalg.getTreeDecomposition();
        Set<Interval<Integer>> root = decompalg.getRoot();
        assertTrue(isNiceTreeDecomposition(decomp,root));
    }

    @Test
    public void testGetTreeDecomposition()
    {
        fail("Not yet implemented");
    }

}
