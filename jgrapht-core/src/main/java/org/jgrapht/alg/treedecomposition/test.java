package org.jgrapht.alg.treedecomposition;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class test<V, E> {

	
	public static void main(String[] args) {
		
		

        Graph<List<Integer>, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class) ;
        List<Integer> outerVertex1 = new ArrayList<>();
        List<Integer> outerVertex2 = new ArrayList<>();
        List<Integer> outerVertex3 = new ArrayList<>();
        List<Integer> outerVertex4 = new ArrayList<>();
        List<Integer> outerVertex5 = new ArrayList<>();
        List<Integer> outerVertex6 = new ArrayList<>();

        outerVertex1.add(1); 
        outerVertex2.add(1); outerVertex2.add(2);outerVertex2.add(3);
        outerVertex3.add(2);outerVertex3.add(3);
        outerVertex4.add(2);
        outerVertex5.add(2);outerVertex5.add(4);
        outerVertex6.add(null);
        
        Graphs.addEdgeWithVertices(graph, outerVertex1, outerVertex2);
        Graphs.addEdgeWithVertices(graph, outerVertex2, outerVertex3);
        Graphs.addEdgeWithVertices(graph, outerVertex3, outerVertex4);
        Graphs.addEdgeWithVertices(graph, outerVertex4, outerVertex5);
        Graphs.addEdgeWithVertices(graph, outerVertex5, outerVertex6);

        
        /*List<Integer> outerVertex1 = new ArrayList<>();
        List<Integer> outerVertex2 = new ArrayList<>();
        List<Integer> outerVertex3 = new ArrayList<>();
        List<Integer> outerVertex4 = new ArrayList<>();
        List<Integer> outerVertex5 = new ArrayList<>();

        
        outerVertex1.add(null); 
        outerVertex2.add(1); 
        outerVertex3.add(null); 
        outerVertex4.add(2); 
        outerVertex5.add(null);
        Graphs.addEdgeWithVertices(graph, outerVertex1, outerVertex2);
        Graphs.addEdgeWithVertices(graph, outerVertex2, outerVertex3);
        Graphs.addEdgeWithVertices(graph, outerVertex3, outerVertex4);
        Graphs.addEdgeWithVertices(graph, outerVertex4, outerVertex5);
        */
        
        
        DecompositionTreeColour testClass = new DecompositionTreeColour(graph);
   //  testClass.getVertexOrdering(); //this works ok
       testClass.getColoring();
       
	}

}
