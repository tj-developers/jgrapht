package org.jgrapht.alg.treedecompostion;

import static org.junit.Assert.*;

import java.util.*;
import java.util.Map.Entry;

import org.jgrapht.*;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.treedecomposition.*;
import org.jgrapht.event.*;
import org.jgrapht.graph.*;
import org.jgrapht.intervalgraph.*;
import org.jgrapht.intervalgraph.interval.*;
import org.junit.*;

public class IntervalgraphDecompositionTest
{

    private <V,E,W> void isNiceDecomposition(Graph<V,E> decomposition, Map<V,Set<W>> map, V root){
        if(map.get(root).size() != 0) assertFalse(root+" is no valid root", true);
        Queue<V> queue = new LinkedList<V>();
        queue.add(root);
        while(!queue.isEmpty())
        {
            V current = queue.poll();
            List<V> successor = Graphs.successorListOf(decomposition, current);
            if(successor.size() == 0 && map.get(current).size() <= 1) continue; //leaf node
            if(successor.size() == 1) //forget or introduce
            {
                V next = successor.get(0);
                queue.add(next);
                Set<W> union = new HashSet<W>(map.get(current));
                union.addAll(map.get(next));
                if(union.size() == map.get(next).size() || union.size() == map.get(current).size())
                {
                    if(map.get(current).size() == map.get(next).size()-1) continue; //introduce
                    else if(map.get(current).size()-1 == map.get(next).size()) continue; //forget
                }
            }
            if(successor.size() == 2) //join
            {
                V first = successor.get(0);
                V second = successor.get(1);
                queue.add(first);
                queue.add(second);
                Set<W> union = new HashSet<W>(map.get(current));
                union.addAll(map.get(first));
                union.addAll(map.get(second));
                if(union.size() == map.get(current).size() 
                && union.size() == map.get(first).size() 
                && union.size() == map.get(second).size()) 
                    continue; //join node!
            }
            assertFalse("Vertex Set "+current+" is not a valid node for a nice decomposition\n in tree "+decomposition+"\n with map "+map, true); //no valid node!
        }
        assertTrue(true);
    }
    
    private <V,E,W,F> void isDecomposition(Graph<W,F> oldGraph, Graph<V,E> decomposition, Map<V,Set<W>> map){
    	Set<F> edgeSet = oldGraph.edgeSet();
    	Set<V> vertexSet = decomposition.vertexSet();
    	//Every edge is represented
    	for(F e : edgeSet)
    	{
    		boolean hasVertex = false;
    		for(V v : vertexSet)
    		{
    			if(map.get(v).contains(oldGraph.getEdgeSource(e)) && map.get(v).contains(oldGraph.getEdgeTarget(e))) {
    				hasVertex = true;
    				continue; 
    			}
    		}
    		if(!hasVertex) assertFalse("Edge "+e+" is not found\n in graph "+decomposition+"\n with map "+map, true);
    	}
    	//every vertex has non-empty connected set of vertex sets
    	Set<W> oldVertexSet = oldGraph.vertexSet();
    	for(W w : oldVertexSet)
    	{
    		Set<V> keySet = new HashSet<V>();
    		for(Entry<V,Set<W>> entry : map.entrySet())
    		{
    			if(entry.getValue().contains(w))
    				keySet.add(entry.getKey());
    		}
    		//not empty
    		assertFalse("Vertex "+w+" is not represented\n in decomposition "+decomposition+"\n and map "+map, keySet.isEmpty());
    		//connected
    		Graph<V,E> subgraph = new AsSubgraph<V,E>(decomposition,keySet);
    		assertTrue("Vertex "+w+" is not connected\n in decomposition "+decomposition+"\n and map "+map,GraphTests.isConnected(subgraph));
    	}
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
        //unconnected
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
        IntervalgraphDecomposition<Integer> decompalg = new IntervalgraphDecomposition<>(list);
        Graph<Integer,DefaultEdge> decomp = decompalg.getTreeDecomposition();
        Map<Integer,Set<Interval<Integer>>> map = decompalg.getMap();
        Set<Integer> roots = decompalg.getRoot();
        for(Integer root : roots)
        	isNiceDecomposition(decomp,map,root);
        
    }

}
