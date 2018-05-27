package org.jgrapht.alg.treedecomposition;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

/**
 * Colouring of decomposition trees (currently done for interval graphs). This algorithm iterates over lists of vertices and assigns
 * the smallest colour to each of the vertices such that the no vertex in the same list has the same colour.
 * 
 * @author Suchanda Bhattacharyya
 *
 * @param <V> The type of graph vertex
 * @param <E> The type of graph edge
 */

public class DecompositionTreeColour<V,E> implements VertexColoringAlgorithm<V> {

	/**
	 * The input graph
	 */
	private Graph<List<V>, E> graph;

	
	/**
	 * @param graph
	 */
	
	public DecompositionTreeColour(Graph<List<V>, E> graph)
	{
		this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
	}
	
	
	/**
	 * Getting the ordering for the vertices
	 * @return the ordering of the vertices
	 */
	protected Iterable<List<V>> getVertexOrdering()
	{
		return (Iterable<List<V>>) graph.vertexSet();
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.alg.interfaces.VertexColoringAlgorithm#getColoring()
	 */
	@Override
	public Coloring<V> getColoring() {


		Map<V, Integer> asssignedColors = new HashMap<>();
		Set<Integer> used = new HashSet<>();
		Set<V> free = new HashSet<>();



		for(List<V> outerVertex:getVertexOrdering() ) {
			//need to sort the inner vertex here or do something so that sorting is not needed
			for(V innerVertex : outerVertex ) { 
				//first need to iterate over each innerVertex in the outerVertex to check that if there is any vertex with an already assigned colour
				if(asssignedColors.containsKey(innerVertex)) {
					used.add(asssignedColors.get(innerVertex));



				}
				else {
					//these are the vertices without any assigned colours
					free.add(innerVertex);

				}
			}

			//here we assign colours to the free vertices

			for(V  freeVertex: free) { 
				int colourCandidate = 0;
				while (used.contains(colourCandidate)) {
					colourCandidate++;
				}

				asssignedColors.put(freeVertex,colourCandidate);


				used.add(colourCandidate);


			}
			free.clear();
			used.clear();

		}
		int maxColourAssigned = Collections.max(asssignedColors.values());

		return  new ColoringImpl<>(asssignedColors, maxColourAssigned + 1);
	}
}


