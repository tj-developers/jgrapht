package org.jgrapht.alg.color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.Coloring;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;
/**
 * Coloring tests
 * 
 * @author Suchanda Bhattacharyya (dia007)
 */
public class DecompositionTreeColourTest {

    protected VertexColoringAlgorithm<Integer> getAlgorithm(Graph<Integer, DefaultEdge> graph, Map <Integer, Set<Integer>> decompositionMap)
    {
        return new DecompositionTreeColour<>(graph, decompositionMap);
    }
    //need to add more graphs

    final protected List<Object> createGraph1()
    {

        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class) ;
        Map <Integer, Set<Integer>> decompositionMap = new HashMap<>();
        List<Object> returnArrayList= new ArrayList<>();

        Graphs.addEdgeWithVertices(graph, 0, 1);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 2, 3);
        Graphs.addEdgeWithVertices(graph, 3, 4);
        Graphs.addEdgeWithVertices(graph, 4, 5);

        int v1 = 11,v2=22,v3=33,v4=44;
        Set set0 = new HashSet<>();
        Set set1 = new HashSet<>();
        Set set2 = new HashSet<>();
        Set set3 = new HashSet<>();
        Set set4 = new HashSet<>();
        Set set5 = new HashSet<>();
        set0.add(Collections.emptySet()); set1.add(v1); set2.add(v1); set2.add(v2); set2.add(v3);set3.add(v2); set3.add(v3);  set4.add(v2); set5.add(v2); set5.add(v4);

        decompositionMap.put(0,set0); decompositionMap.put(1,set1 ); decompositionMap.put(2,set2 );decompositionMap.put(3,set3 );decompositionMap.put(4,set4 );decompositionMap.put(5,set5 );
        returnArrayList.add(graph); returnArrayList.add(decompositionMap);
        
        return returnArrayList;
    }



    @Test
    public void testGreedy1()
    
    {
        List<Object> returnArrayList= createGraph1();
        Graph<Integer, DefaultEdge> newGraph = (Graph<Integer, DefaultEdge>) returnArrayList.get(0);
        Map <Integer, Set<Integer>> newDecompositionMap = (Map<Integer, Set<Integer>>) returnArrayList.get(1);

        Coloring<Integer> coloring = new DecompositionTreeColour<>(newGraph,newDecompositionMap).getColoring();
        assertEquals(3, coloring.getNumberColors());
        Map<Integer, Integer> colors = coloring.getColors();
        

        //iterate over all edges/vertices and check if the test can be done better
        assertNotEquals(colors.get(11).intValue(), colors.get(22).intValue(), colors.get(33).intValue());
    }
}