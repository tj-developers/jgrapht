package org.jgrapht.alg.intervalgraph;

import org.jgrapht.Graph;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.jgrapht.alg.intervalgraph.LexBreadthFirstSearch.*;

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
    List<V> sweepAlpha = lexBreadthFirstSearch(graph, randomElementOf(graph.vertexSet()));
    V vertexA = lastElementOf(sweepAlpha);

    // Step 2 - LBFS+ from the last vertex of the previous sweep
    // Input - the result of previous sweep alpha, vertex a
    // Output - the result of current sweep beta, further last vertex b visited by current sweep
    List<V> sweepBeta = lexBreadthFirstSearchPlus(graph, vertexA, arrayOf(sweepAlpha));
    V vertexB = lastElementOf(sweepBeta);

    // Step 3 - LBFS+ from the last vertex of the previous sweep
    // Input - the result of previous sweep beta, vertex b
    // Output - the result of current sweep gamma, further last vertex c visited by current sweep
    List<V> sweepGamma = lexBreadthFirstSearchPlus(graph, vertexB, arrayOf(sweepBeta));
    V vertexC = lastElementOf(sweepGamma);

    // Step 4 - LBFS+ from the last vertex of the previous sweep
    // Input - the result of previous sweep gamma, vertex c
    // Output - the result of current sweep delta, further last vertex d visited by current sweep
    List<V> sweepDelta = lexBreadthFirstSearchPlus(graph, vertexC, arrayOf(sweepGamma));
    V vertexD = lastElementOf(sweepDelta);

    // Additionally, calculate the index and the corresponding A set for each vertex

    // Step 5 - LBFS+ from the last vertex of the previous sweep
    // Input - the result of previous sweep delta, vertex d
    // Output - the result of current sweep epsilon, further last vertex e visited by current sweep
    List<V> sweepEpsilon = lexBreadthFirstSearchPlus(graph, vertexD, arrayOf(sweepDelta));
    V vertexE = lastElementOf(sweepEpsilon);

    // Additionally, calculate the index and the corresponding B set for each vertex

    // Step 6 - LBFS* with the resulting sweeps
    // Input - the result of sweep gamma and sweep epsilon
    // Output - the result of current sweep zeta
    List<V> sweepZeta = null; // TODO: replace by the invocation of lexBreadthFirstSearchStar

    // if sweepZeta is umbrella-free, then the graph is interval.
    // otherwise, the graph is not interval
    return isIOrdering(arrayOf(sweepZeta), graph);
  }

  /**
   * Calculates if the given sweep is an I-Ordering
   * (according to the Graph graph)
   *
   * @param sweep the order we want to check if its an I-Order
   * @param graph the graph we want to check if its an I-Order
   * @return true, if sweep is an I-Order according to graph
   */
  private static <V> boolean isIOrdering(V[] sweep, Graph graph) {
    for (int i = 0; i < sweep.length - 2; i++) {
      for (int j = i + 1; j < sweep.length - 1; j++) {
        for (int k = j + 1; k < sweep.length; k++) {
          boolean edgeIJ = graph.containsEdge(sweep[i], sweep[j]);
          boolean edgeIK = graph.containsEdge(sweep[i], sweep[k]);
          if (!edgeIJ && edgeIK) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * return the last element of the given list
   *
   * @param list
   * @param <V>  the generic type representing vertices
   * @return
   */
  private static <V> V lastElementOf(List<V> list) {
    if (list == null) {
      throw new IllegalArgumentException("List parameter cannot be null.");
    }

    return list.get(list.size() - 1);
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

  /**
   * convert the given list into array in weak typing way
   *
   * @param list
   * @param <V>  the generic type representing vertices
   * @return
   */
  private static <V> V[] arrayOf(List<V> list) {
    if (list == null) {
      throw new IllegalArgumentException("List parameter cannot be null.");
    }

    V[] resultArray = (V[]) new Object[list.size()];
    list.toArray(resultArray);
    return resultArray;
  }

}