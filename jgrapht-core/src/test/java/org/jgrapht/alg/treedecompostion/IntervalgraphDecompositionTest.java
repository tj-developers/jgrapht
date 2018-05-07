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

    private <V,E> void isNiceTreeDecomposition(Graph<Set<V>,E> graph, Set<V> root){
        if(root.size() != 0) assertFalse(root+" is no valid root", true);
        Queue<Set<V>> queue = new LinkedList<Set<V>>();
        queue.add(root);
        while(!queue.isEmpty())
        {
            Set<V> current = queue.poll();
            List<Set<V>> successor = Graphs.successorListOf(graph, current);
            System.out.println("DEBUG: current:"+current+", neighbor:"+successor);
            if(successor.size() == 0 && current.size() <= 1) continue; //leaf node
            if(successor.size() == 1) //forget or introduce
            {
                Set<V> next = successor.get(0);
                queue.add(next);
                Set<V> union = new HashSet<V>(current);
                union.addAll(next);
                if(union.size() == next.size() || union.size() == current.size())
                {
                    if(current.size() == next.size()+1) continue; //introduce
                    else if(current.size()+1 == next.size()) continue; //forget
                }
            }
            if(successor.size() == 2) //join
            {
                Set<V> first = successor.get(0);
                Set<V> second = successor.get(1);
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
            assertFalse("Vertex Set "+current+" is no valid nice tree node in tree "+graph, true); //no valid node!
        }
        assertTrue(true);
    }
    
    @Test
    public void testIntervalgraphDecompositionForRegularGraphs()
    {
        Graph<Integer,DefaultEdge> g = new DefaultUndirectedGraph<>(DefaultEdge.class);
        //TODO: change g
        //IntervalgraphDecomposition<Integer,IntervalVertex<Integer,Integer>> decompositionAlg = new IntervalgraphDecomposition<>(g);
        //Graph<Set<Integer>,DefaultEdge> decomp = decompositionAlg.getTreeDecomposition();
        //TODO: test here
        assertTrue(false);
    }

    @Test
    public void testIntervalgraphDecompositionForIntervalGraphs()
    {
        IntervalGraphInterface<Integer> ig = new CenteredIntervalTree<>();
        //TODO: change ig
        //IntervalgraphDecomposition<Integer,DefaultEdge,Integer> decompositionAlg = new IntervalgraphDecomposition<>(ig);
        assertTrue(false);
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
        isNiceTreeDecomposition(decomp,root);
    }

    @Test
    public void testGetTreeDecomposition()
    {
        fail("Not yet implemented");
    }

}
