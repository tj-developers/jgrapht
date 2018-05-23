package org.jgrapht.alg.color;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

public class DecompositionTreeColour<V,E> implements VertexColoringAlgorithm<V> {


	private Graph<List<V>, E> graph;

	/**
	 * Construct a new coloring algorithm.
	 * 
	 * @param graph the input graph
	 */
	public DecompositionTreeColour(Graph<List<V>, E> graph)
	{
		this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
	}
	protected Iterable<List<V>> getVertexOrdering()
	{
		return (Iterable<List<V>>) graph.vertexSet();
	}

	@Override
	public Coloring<V> getColoring() {

		
		Map<V, Integer> asssignedColors = new HashMap<>();
		Set<Integer> used = new HashSet<>();
		Set<V> free = new HashSet<>();



		for(List<V> outerVertex:getVertexOrdering() ) {
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
			used.clear();

		}
		int maxColourAssigned = Collections.max(asssignedColors.values());
		
		
		return  new ColoringImpl<>(asssignedColors, maxColourAssigned + 1);
	}
}









