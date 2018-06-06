package org.jgrapht.alg.color;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.io.Serializable;
import java.util.*;

public class ColorRefinementAlgorithm<V, E> implements VertexColoringAlgorithm<V>, Serializable {

    private static final long serialVersionUID = -987646758624545630L;

    /**
     * The input graph
     */
    protected final Graph<V, E> graph;

    private Coloring<V> alpha;



    /**
     * Construct a new coloring algorithm.
     *
     * @param graph the input graph
     * @param alpha the coloring on the graph to be refined
     */
    public ColorRefinementAlgorithm(Graph<V, E> graph, Coloring<V> alpha) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.alpha = Objects.requireNonNull(alpha, "alpha cannot be null");
        if(!isAlphaConsistent(alpha, graph)) {
            throw new IllegalArgumentException("alpha is not a valid surjective l-coloring for the given graph.");
        }
    }

    /**
     * Construct a new coloring algorithm.
     *
     * @param graph the input graph
     */
    public ColorRefinementAlgorithm(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.alpha = getDefaultAlpha(graph.vertexSet());
    }

    /**
     * calculates a canonical surjective k-colring of the given graph such that the classes of the coloring form the coarsest stable partition that refines alpha.
     *
     * @return the calculated coloring
     */
    @Override
    public Coloring<V> getColoring() {
        Integer n = graph.vertexSet().size(); // the size of the graph
        Integer k; // number of colors used

        // The following arrays have length of n+1 instead of n, since c ranges from 1 to n and not from 0 to n-1
        // TODO: Adjust c and the array ranges

        // mapping from all colors to their classes
        ArrayList<List<V>> C = new ArrayList<>(n + 1);
        // mapping from color to their classes, whereby every vertex in the classes has cdeg(v) >= 1
        ArrayList<List<V>> A = new ArrayList<>(n + 1);

        int[] maxcdeg = new int[n + 1]; // mapping from color to its maximum color degree
        int[] mincdeg = new int[n + 1]; // mapping from color to its minimum color degree
        Map<V, Integer> cdeg = new HashMap<>(); // mapping from vertex to the color degree (number of neighbors with different colors) of the vertex
        Map<V, Integer> color = new HashMap<>(); // stores the coloring (that is returned in the end)

        for(int c = 1; c <= n; ++c) {
            C.add(c, new LinkedList<>()); // init color classes
            A.add(c, new LinkedList<>()); // init color classes with cdeg(v) >= 1
            // the maximum color degree is already initialised with 0
        }
        for(V v : graph.vertexSet()) {
            C.get(alpha.getColors().get(v)).add(v); // init the color classes corresponding to the given coloring alpha
            cdeg.put(v, 0); // init the color degree for every vertex with 0
            color.put(v, alpha.getColors().get(v)); // assign a color to every vertex (for initialization)
        }
        k = alpha.getNumberColors(); // number of colors used
        Deque<Integer> S_refine = getSortedStack(alpha); // get an ascendingly sorted stack of all colors that are predefined by alpha
        //S_refine.sort(Comparator.comparingInt(o -> o)); // TODO sort stack ascendingly (not necessary with this implementation because getSortedStack() already returns a sorted stack) -> maybe it will be necessary
        // TODO Argue if linked list should be replaced by ArrayList
        LinkedList<Integer> Colors_adj = new LinkedList<>(); // list of all colors that have at least one vertex with cdeg >= 1

        while(!S_refine.isEmpty()) {
            Integer r = S_refine.pop(); // analyze the next color

            //calculate number of neighbours of v of color r, and calculate maximum and minimum degree of all colors
            calculateColorDegree(r, color, C, A, Colors_adj, maxcdeg, mincdeg, cdeg);

            //calculate new partition of the colors and update the color classes correspondingly
            calculateColorPartition(color, C, A, S_refine, k, Colors_adj, maxcdeg, mincdeg, cdeg);

            //reset attributes for new iteration such that the invariants are still correct
            for(Integer c : Colors_adj) {
                for(V v : A.get(c)) {
                    cdeg.put(v, 0);
                }
                maxcdeg[c] = 0;
                A.add(c, new LinkedList<>());
                Colors_adj.remove(c);
            }

        }

        return new ColoringImpl<>(color, color.size());
    }

    /**
     * Helper method for getColoring().
     * calculates the color degree for every vertex and the maximum and minimum color degree for every color.
     *  @param r refining color (current color in the iteration)
     * @param color the color mapping
     * @param C the mapping from all colors to their classes
     * @param A the mapping from all colors to their classes with cdeg(v) >= 1
     * @param Colors_adj the list of all colors that have at least one vertex with cdeg >= 1
     * @param maxcdeg the mapping from color to its maximum color degree
     * @param mincdeg the mapping from color to its minimum color degree
     * @param cdeg the mapping from vertex to the color degree (number of neighbors with different colors) of the vertex
     */
    private void calculateColorDegree(Integer r, Map<V, Integer> color, ArrayList<List<V>> C, ArrayList<List<V>> A, LinkedList<Integer> Colors_adj, int[] maxcdeg, int[] mincdeg, Map<V, Integer> cdeg) {
        for(V v : C.get(r)) {
            Set<E> N_minus = graph.incomingEdgesOf(v); // get all incident edges to get all adjacent vertices
            for(E e : N_minus) { // go through all incident edges
                V w = graph.getEdgeTarget(e); // get all adjacent vertex
                cdeg.put(w, cdeg.get(w) + 1); // increase color degree of the current vertex
                if(cdeg.get(w) == 1) { // add vertex to A if color degree of exactly 1 is reached
                    A.get(color.get(w)).add(w);
                }
                if(!Colors_adj.contains(color.get(w))) { // add vertex to Color_adj only if it is not already contained in Color_adj
                    Colors_adj.add(color.get(w));
                }
                if(cdeg.get(w) > maxcdeg[color.get(w)]) { // update maxcdeg for color(w) if maximum color degree has increased
                    maxcdeg[color.get(w)] = cdeg.get(w);
                }
            }
        }

        for(Integer c : Colors_adj) { // go through all colors, which have at least one vertex with cdeg >= 1, to update mincdeg
            if(C.get(c).size() != A.get(c).size()) { // if there is a vertex with cdeg(v) = 0 < 1, set minimum color degree to 0
                mincdeg[c] = 0;
            } else {
                mincdeg[c] = maxcdeg[c]; // set mincdeg(c) to maxcdeg before iterating over all vertices
                for(V v : A.get(c)) { // update mincdeg by iterating over all vertices
                    if (cdeg.get(v) < mincdeg[c]) { // if there is a vertex v with lower color degree, update
                        mincdeg[c] = cdeg.get(v);
                    }
                } // now mincdeg is correctly computed for color c
            }
        }
    }

    /**
     * Helper method for getColoring().
     * Partition the colors that do not have the same minimum and maximum color degree. That is, these colors are not completely refined.
     *  @param color the color mapping
     * @param C the mapping from all colors to their classes
     * @param A the mapping from all colors to their classes with cdeg(v) >= 1
     * @param S_refine the stack containing all colors that have to be refined
     * @param k the number of colors
     * @param Colors_adj the list of all colors that have at least one vertex with cdeg >= 1
     * @param maxcdeg the mapping from color to its maximum color degree
     * @param mincdeg the mapping from color to its minimum color degree
     * @param cdeg the mapping from vertex to the color degree (number of neighbors with different colors) of the vertex
     */
    private void calculateColorPartition(Map<V, Integer> color, ArrayList<List<V>> C, ArrayList<List<V>> A, Deque<Integer> S_refine, Integer k, LinkedList<Integer> Colors_adj, int[] maxcdeg, int[] mincdeg, Map<V, Integer> cdeg) {
        LinkedList<Integer> Colors_split = new LinkedList<>(); // subset of Colors_adj that will be split up into different color classes
        for(Integer c : Colors_adj) {
            if(mincdeg[c] < maxcdeg[c]) { // colors have to be refined as the vertices with that color do not have the same color degree
                Colors_split.add(c);
            }
        }
        Colors_split.sort(Comparator.comparingInt(o -> o)); // sort list because the colors have to be considered in canonical order
        for(Integer s : Colors_split) { // split the colors for all colors in Colors_split
            SplitUpColor(s, color, C, A, S_refine, k, maxcdeg, mincdeg, cdeg);
        }
    }

    /**
     * Helper method for getColoring().
     *
     *  @param s the color to split the color class for
     * @param color the color mapping
     * @param C the mapping from all colors to their classes
     * @param A the mapping from all colors to their classes with cdeg(v) >= 1
     * @param S_refine the stack containing all colors that have to be refined
     * @param k the number of colors
     * @param maxcdeg the mapping from color to its maximum color degree
     * @param mincdeg the mapping from color to its minimum color degree
     * @param cdeg the mapping from vertex to the color degree (number of neighbors with different colors) of the vertex
     */
    private void SplitUpColor(Integer s, Map<V, Integer> color, ArrayList<List<V>> C, ArrayList<List<V>> A, Deque<Integer> S_refine, Integer k, int[] maxcdeg, int[] mincdeg, Map<V, Integer> cdeg) {
        Map<Integer, Integer> numcdeg = new HashMap<>(); // mapping from the color degree to the number of vertices with that color degree
        Map<Integer, Integer> f = new HashMap<>(); // mapping from color degrees that occur in S to newly introduced colors or to color s
        boolean instack; // helper variable that stores if a color is already in the stack S_refine

        int maxcdeg_ = maxcdeg[s]; // maxcdeg_ is the maximum color degree of color s (the color to split the color class for)

        for(int i = 1; i <= maxcdeg_; ++i) { // initialize numcdeg
            numcdeg.put(i, 0);
        }
        numcdeg.put(0, C.get(s).size() - A.get(s).size()); // add the number of vertices with color degree 0 to numcdeg;
        // remember C maps all colors to their classes and A only to vertices in that class with cdeg(v) >= 1

        for(V v : A.get(s)) { // iterate over all vertices and update numcdeg correspondingly
            numcdeg.put(cdeg.get(v), numcdeg.get(cdeg.get(v)) + 1);
        }
        int b = 0; // helper variable storing the index with the maximum number of vertices with the corresponding color degree
        for(int i = 1; i <= maxcdeg_; ++i) { // find b as defined above
            if(numcdeg.get(i) > numcdeg.get(b)) {
                b = i;
            }
        }
        instack = S_refine.contains(s); // is s already in stack S_refine?

        // add new colors to the stack S_refine, which have to be refined further, corresponding to the calculations before
        // calculate the mapping f.
        addColorsToS_refineAndCalculateF(s, maxcdeg_, mincdeg, S_refine, k, numcdeg, f, instack, b);

        for(V v : A.get(s)) { // update C and color for all vertices in the color class of s corresponding to the calculated f
            if(f.get(cdeg.get(v)) != s) { // f assigns v a new color
                C.get(s).remove(v); // remove v from s
                C.get(f.get(cdeg.get(v))).add(v); // add v to the new color class
                color.put(v, f.get(cdeg.get(v))); // give v the new color
            }
        }
    }

    /**
     * Helper method for getColoring().
     * adds all colors to S_refine which have to be refined further and constructs the mapping f.
     *  @param s the current color
     * @param maxcdeg_ maximum color degree of s
     * @param mincdeg the mapping from color to its minimum color degree
     * @param S_refine the stack S_refine that administrates all colors tobe refined
     * @param k the number of currently used colors
     * @param numcdeg mapping from the color degree to the number of vertices with that color degree
     * @param f mapping from color degrees that occur in S to newly introduced colors or to color s
     * @param instack contains whether color s is already in stack
     * @param b index with the maximum number of vertices with the corresponding color degree
     */
    private void addColorsToS_refineAndCalculateF(Integer s, Integer maxcdeg_, int[] mincdeg, Deque<Integer> S_refine, Integer k, Map<Integer, Integer> numcdeg, Map<Integer, Integer> f, boolean instack, int b) {
        for(int i = 0; i <= maxcdeg_; ++i) { // go through all indices (color degrees) of numcdeg
            if(numcdeg.get(i) >= 1) { // if there is a vertex with color degree i
                if(i == mincdeg[s]) { // i is the minimum color degree of s
                    f.put(i, s); // colors with minimum color degree keep color s
                    if(!instack && b != i) { // push s on the stack if it is not in the stack and i is not the index with the maximum number of vertices with the corresponding color degree
                        S_refine.push(f.get(i));
                    }
                } else { // i is not the minimum color degree of s
                    k++; // we add a new color cuh that we have to increase the number of colors
                    f.put(i, k); // we add the new color to f for the index i
                    if(instack && i != b) { // push s on the stack if it is in the stack and i is not the index with the maximum number of vertices with the corresponding color degree
                        S_refine.push(f.get(i));
                    }
                }
            }
        }
    }

    // TODO is there anything that is to be checked?
    /**
     * checks whether alpha is a valid surjective l-coloring for the given graph
     *
     * @param alpha the surjective l-coloring to be checked
     * @param graph the graph that is colored by alpha
     * @return whether alpha is a valid surjective l-coloring for the given graph
     */
    private boolean isAlphaConsistent(Coloring<V> alpha, Graph<V, E> graph) {
        if(alpha.getColors().size() != graph.vertexSet().size()) {
            for(V v : alpha.getColors().keySet()) {
                if(alpha.getColors().get(v) <= alpha.getNumberColors() && alpha.getColors().get(v) >= 1) {
                    return false;
                }
                if(!graph.containsVertex(v)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * returns a coloring such that all vertices have color 1.
     *
     * @param vertices the vertices that should be colored
     * @return the all-1 coloring
     */
    private static <V> Coloring<V> getDefaultAlpha(Set<V> vertices) {
        Map<V, Integer> alpha = new HashMap<>();
        for(V v : vertices) {
            alpha.put(v, 1);
        }
        return new ColoringImpl<>(alpha, 1);
    }

    //TODO is that correct???
    /**
     * returns a canonically sorted stack of all colors of alpha. It is important that alpha is consistent
     *
     * @param alpha the surjective l-coloring
     * @return a canonically sorted stack of all colors of alpha
     */
    private Deque<Integer> getSortedStack(Coloring<V> alpha) {
        int numberColors = alpha.getNumberColors();
        // We use an ArrayDeque since it is fast, can be initialized with the correct size
        // and because Stack is kind of deprecated
        Deque<Integer> stack = new ArrayDeque<>(numberColors);
        for(int i = numberColors; i > 0; --i) {
            stack.push(i);
        }
        return stack;
    }

}
