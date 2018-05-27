package org.jgrapht.alg.treedecomposition;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	protected VertexColoringAlgorithm<Integer> getAlgorithm(Graph<List<Integer>, DefaultEdge> graph)
	{
		return new DecompositionTreeColour<>(graph);
	}
	//need to add more graphs

	final protected Graph<List<Integer>, DefaultEdge> createGraph1()
	{
		Graph<List<Integer>, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);


		List<Integer> outerVertex1 = new ArrayList<>();
		List<Integer> outerVertex2 = new ArrayList<>();
		List<Integer> outerVertex3 = new ArrayList<>();
		List<Integer> outerVertex4 = new ArrayList<>();
		List<Integer> outerVertex5 = new ArrayList<>();

		outerVertex1.add(1); 
		outerVertex2.add(1); outerVertex2.add(2);outerVertex2.add(3);
		outerVertex3.add(2);outerVertex3.add(3);
		outerVertex4.add(2);
		outerVertex5.add(2);outerVertex5.add(4);

		Graphs.addEdgeWithVertices(graph, outerVertex1, outerVertex2);
		Graphs.addEdgeWithVertices(graph, outerVertex2, outerVertex3);
		Graphs.addEdgeWithVertices(graph, outerVertex3, outerVertex4);
		Graphs.addEdgeWithVertices(graph, outerVertex4, outerVertex5);
		return graph;
	}

	final protected Graph<List<Integer>, DefaultEdge> createGraph2()
	{
		Graph<List<Integer>, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);


		List<Integer> outerVertex1 = new ArrayList<>();
		List<Integer> outerVertex2 = new ArrayList<>();
		List<Integer> outerVertex3 = new ArrayList<>();
		List<Integer> outerVertex4 = new ArrayList<>();
		List<Integer> outerVertex5 = new ArrayList<>();

		outerVertex1.add(2); outerVertex1.add(1);
		outerVertex2.add(3); outerVertex2.add(4);outerVertex2.add(2);
		outerVertex3.add(4);
		outerVertex4.add(5);outerVertex4.add(4); 

		outerVertex5.add(4);outerVertex5.add(6);
		Graphs.addEdgeWithVertices(graph, outerVertex1, outerVertex2);
		Graphs.addEdgeWithVertices(graph, outerVertex2, outerVertex3);
		Graphs.addEdgeWithVertices(graph, outerVertex3, outerVertex4);
		Graphs.addEdgeWithVertices(graph, outerVertex3, outerVertex5);

		return graph;
	}


	@Test
	public void testGreedy1()
	{
		Graph<List<Integer>, DefaultEdge> newGraph = createGraph1();

		Coloring<Integer> coloring = new DecompositionTreeColour<>(newGraph).getColoring();
		assertEquals(3, coloring.getNumberColors());
		Map<Integer, Integer> colors = coloring.getColors();
		assertEquals(0, colors.get(1).intValue());
		assertEquals(1, colors.get(2).intValue());
		assertEquals(2, colors.get(3).intValue());
		assertEquals(0, colors.get(4).intValue());
	}
	@Test
	public void testGreedy2()
	{
		Graph<List<Integer>, DefaultEdge> newGraph = createGraph2();

		Coloring<Integer> coloring = new DecompositionTreeColour<>(newGraph).getColoring();
		assertEquals(3, coloring.getNumberColors());
		Map<Integer, Integer> colors = coloring.getColors();
		assertEquals(0, colors.get(1).intValue());
		assertEquals(1, colors.get(2).intValue());
		assertEquals(0, colors.get(3).intValue());
		assertEquals(2, colors.get(4).intValue());
		assertEquals(0, colors.get(5).intValue());
		assertEquals(0, colors.get(6).intValue());
	}
}
