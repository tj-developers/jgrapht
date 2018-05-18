package org.jgrapht.alg.color;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.intervalgraph.interval.Interval;

import java.util.*;

public class ColorRefinementAlgorithm<V, E> implements VertexColoringAlgorithm<V> {

    /**
     * The input graph
     */
    protected final Graph<V, E> graph;

    /**
     * Construct a new coloring algorithm.
     *
     * @param graph the input graph
     */
    public ColorRefinementAlgorithm(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
    }
    
    @Override
    public Coloring<V> getColoring() {

        Map<V, Integer> vertexToColorMap = new HashMap<>(); // future coloring
        Map<Integer, Set<V>> colorToVertexMap = new HashMap<>(); // mapping from color to all vertices with that color
        Integer lastColor = 1; // last color used
        Integer numberOfColorsUsed = 1; // number of colors used

        Queue<Integer> colorQueue = new LinkedList<>(); // queue for all colors that will be used for refinement
        colorQueue.add(1); // init queue with first color

        HashSet<V> color1VertexMap = new HashSet<>(); // helper set: vertex set for first color
        Integer currentColor; // helper variable
        Map<V, Integer> numberOfNeighborsForColor = new HashMap<>(); // helper map for loop
        List<Set<V>> orderedPartition; // helper list for partition in loop

        for(V vertex : graph.vertexSet()) {
            vertexToColorMap.put(vertex, 1); // initial coloring
            color1VertexMap.add(vertex); // map is initialized for first color
        }
        colorToVertexMap.put(1, color1VertexMap); // initialize mapping form color to vertices

        while(!colorQueue.isEmpty()) {
            currentColor = colorQueue.poll();

            for(V vertex : graph.vertexSet()) {
                numberOfNeighborsForColor.put(vertex, getNumberOfNeighborsOfColor(vertex, currentColor)); // number of neighbors of vertex color currentColor
            }
            orderedPartition = getOrderedPartition(vertexToColorMap, numberOfNeighborsForColor);

            if(orderedPartition.size() > numberOfColorsUsed) {
                for(Integer color = lastColor - numberOfColorsUsed + 1; color <= lastColor; ++color) {
                    Integer i1 = 0;
                    Integer i2 = 0;
                    getColorClassesOfColor(i1, i2, orderedPartition); // color class of c will be split into classes B_{i1}, ..., B_{i2}

                    Integer iStar = maximumPartitionSize(i1, i2, orderedPartition); // find the largest class

                    addColorsToColorQueue(colorQueue, lastColor, i1, i2, iStar); // add all colors except the one with the largest class to queue colorQueue
                }

                for(Integer b = lastColor + 1; b <= lastColor + orderedPartition.size(); ++b) {
                    saveNewColoring();
                }
            }
        }

        return new ColoringImpl<>(vertexToColorMap, numberOfColorsUsed);
    }

    private Integer getNumberOfNeighborsOfColor(V vertex, Integer color) {
        return 0;
    }

    private List<Set<V>> getOrderedPartition(Map<V, Integer> coloring, Map<V, Integer> numberOfNeighborsForColor) {
        List<Set<V>> partition = new ArrayList<>();

        return partition;
    }

    private void getColorClassesOfColor(Integer i1, Integer i2, List<Set<V>> orderedPartition) {
        i1 = 1;
        i2 = 1;
    }

    private Integer maximumPartitionSize(Integer i1, Integer i2, List<Set<V>> orderedPartition) {
        return 0;
    }

    private void addColorsToColorQueue(Queue<Integer> colorQueue, Integer lastColor, Integer i1, Integer i2, Integer iStar) {
        for(Integer i = i1; i <= i2; ++i) {
            if(!i.equals(iStar)) {
                colorQueue.add(lastColor + i);
            }
        }
    }

    private void saveNewColoring() {

    }
}
