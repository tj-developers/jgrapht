package org.jgrapht.alg.intervalgraph;

import static org.jgrapht.alg.intervalgraph.LexBreadthFirstSearch.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.intervalgraph.*;
import org.jgrapht.intervalgraph.interval.*;

public final class IntervalGraphRecognizer {

  /**
   * check if the given graph is an interval graph
   *
   * @param graph the graph to be checked
   * @param <V>   the generic type representing vertices
   * @param <E>   the generic type representing edges
   * @return
   */
  public static <V, E> boolean isIntervalGraph(Graph<V, E> graph) {
    // Step 1 - LBFS from an arbitrary vertex
    // Input - random vertex r
    // Output - the result of current sweep alpha, further last vertex a visited by current sweep
    HashMap<V, Integer> sweepAlpha = lexBreadthFirstSearch(graph, randomElementOf(graph.vertexSet()));
    V vertexA = lastElementOf(sweepAlpha);

    // Step 2 - LBFS+ from the last vertex of the previous sweep
    // Input - the result of previous sweep alpha, vertex a
    // Output - the result of current sweep beta, further last vertex b visited by current sweep
    HashMap<V, Integer> sweepBeta = lexBreadthFirstSearchPlus(graph, vertexA, sweepAlpha);
    V vertexB = lastElementOf(sweepBeta);

    // Step 3 - LBFS+ from the last vertex of the previous sweep
    // Input - the result of previous sweep beta, vertex b
    // Output - the result of current sweep gamma, further last vertex c visited by current sweep
    HashMap<V, Integer> sweepGamma = lexBreadthFirstSearchPlus(graph, vertexB, sweepBeta);
    V vertexC = lastElementOf(sweepGamma);

    // Step 4 - LBFS+ from the last vertex of the previous sweep
    // Input - the result of previous sweep gamma, vertex c
    // Output - the result of current sweep delta, further last vertex d visited by current sweep
    HashMap<V, Integer> sweepDelta = lexBreadthFirstSearchPlus(graph, vertexC, sweepGamma);
    V vertexD = lastElementOf(sweepDelta);

    // Additionally, calculate the index and the corresponding A set for each vertex

    // Step 5 - LBFS+ from the last vertex of the previous sweep
    // Input - the result of previous sweep delta, vertex d
    // Output - the result of current sweep epsilon, further last vertex e visited by current sweep
    HashMap<V, Integer> sweepEpsilon = lexBreadthFirstSearchPlus(graph, vertexD, sweepDelta);
    // V vertexE = lastElementOf(sweepEpsilon); TODO: not used?

    // Additionally, calculate the index and the corresponding B set for each vertex

    // Step 6 - LBFS* with the resulting sweeps
    // Input - the result of sweep gamma and sweep epsilon
    // Output - the result of current sweep zeta
    HashMap<V, Integer> sweepZeta = lexBreadthFirstSearchStar(graph, vertexD, sweepDelta, sweepEpsilon); 

    // if sweepZeta is umbrella-free, then the graph is interval.
    // otherwise, the graph is not interval
    //return isIOrdering(sweepZeta, graph);
    
    // Compute interval representation -- TODO: complete after merge
    HashMap<V, Integer> neighborIndex = new HashMap<>();
    for(V vertex : graph.vertexSet()) {
        int maxNeighbor = 0;
        
        List<V> neighbors = Graphs.neighborListOf(graph, vertex);
        neighbors.add(vertex);
        
        for(V neighbor : neighbors) {
            maxNeighbor = Math.max(maxNeighbor, sweepZeta.get(neighbor));
        }
        
        neighborIndex.put(vertex, maxNeighbor);
    }
    
    ArrayList<Interval<Integer>> intervals = new ArrayList<>(graph.vertexSet().size());
    
    for(V vertex : graph.vertexSet()) {
        Interval<Integer> vertexInterval = new Interval<>(sweepZeta.get(vertex), neighborIndex.get(vertex));
        intervals.add(vertexInterval);
    }
    
    return isIOrdering(sweepZeta, graph);
  }

  /**
   * Calculates if the given sweep is an I-Ordering
   * (according to the Graph graph)
 * @param <E>
   *
   * @param sweep the order we want to check if its an I-Order
   * @param graph the graph we want to check if its an I-Order
   * @return true, if sweep is an I-Order according to graph
   */
  private static <V, E> boolean isIOrdering(HashMap<V, Integer> sweep, Graph<V, E> graph) {
      HashMap<V, Integer> last = new HashMap<>();
      HashMap<Integer, V> inverseSweep = new HashMap<>();
      
      for(V vertex : graph.vertexSet()) {
          int index = sweep.get(vertex);
          inverseSweep.put(index, vertex);
      }
      
      for(int i = 0; i < graph.vertexSet().size(); i++) {
          V vertex = inverseSweep.get(i);
          
          for(V neighbor : Graphs.neighborListOf(graph, vertex)) {
              if(last.get(neighbor) != null && last.get(neighbor) != i - 1) {
                  return false;
              } else {
                 last.replace(neighbor, i);
              }
          }
      }
      
      return true;
  }

  /**
   * return the last element of the given map
   *
   * @param map
   * @param <V>  the generic type representing vertices
   * @return
   */
  private static <V> V lastElementOf(HashMap<V, Integer> map) {
      return Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
  }

  /**
   * return a random element of the given set
   *
   * @param set
   * @param <V> the generic type representing vertices
   * @return
   */
  private static <V> V randomElementOf(Set<V> set) {
    if (set == null) {
      throw new IllegalArgumentException("List parameter cannot be null.");
    }

    int index = new Random().nextInt(set.size());
    Iterator<V> iterator = set.iterator();
    for (int i = 0; i < index; i++) {
      iterator.next();
    }
    return iterator.next();
  }
}
