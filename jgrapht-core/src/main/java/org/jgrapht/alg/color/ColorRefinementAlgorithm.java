package org.jgrapht.alg.color;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.awt.*;
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
        if(alpha.getColors().size() != graph.vertexSet().size()) {
           for(V v : alpha.getColors().keySet()) {
               if(!graph.containsVertex(v)) {
                   throw new IllegalArgumentException("alpha is not a valid coloring for the given graph.");
               }
           }
        } else {
            throw new IllegalArgumentException("alpha is not a valid coloring for the given graph.");
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

        Map<Integer, LinkedList<V>> C = new HashMap<>(); // mapping from all colors to their classes
        Map<Integer, LinkedList<V>> A = new HashMap<>(); // mapping from color to their classes, whereby every vertex in the classes has cdeg(v) >= 1

        Map<Integer, Integer> maxcdeg = new HashMap<>(); // mapping from color to its maximum color degree
        Map<Integer, Integer> mincdeg = new HashMap<>(); // mapping from color to its minimum color degree
        Map<V, Integer> cdeg = new HashMap<>(); // mapping from vertex to the color degree (number of neighbors with different colors) of the vertex
        Map<V, Integer> color = new HashMap<>(); // stores the coloring (that is returned in the end)

        for(int c = 1; c <= n; ++c) {
            C.put(c, new LinkedList<>()); // init color classes
            A.put(c, new LinkedList<>()); // init color classes with cdeg(v) >= 1
            maxcdeg.put(c, 0); // init the maximum color degree mapping with 0
        }
        for(V v : graph.vertexSet()) {
            C.get(alpha.getColors().get(v)).add(v); // init the color classes corresponding to the given coloring alpha
            cdeg.put(v, 0); // init the color degree for every vertex with 0
            color.put(v, alpha.getColors().get(v)); // assign a color to every vertex (for initialization)
        }
        k = alpha.getNumberColors(); // number of colors used
        Stack<Integer> S_refine = getS_refine(alpha); // get an ascendingly sorted stack of all colors that are predefined by alpha
        //S_refine.sort(Comparator.comparingInt(o -> o)); // TODO sort stack ascendingly (not necessary with this implementation because getS_refine() already returns a sorted stack) -> maybe it will be necessary
        LinkedList<Integer> Colors_adj = new LinkedList<>(); // list of all colors that have at least one vertex with cdeg >= 1

        while(!S_refine.empty()) {
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
                maxcdeg.put(c, 0);
                A.put(c, new LinkedList<>());
                Colors_adj.remove(c);
            }

        }

        return new ColoringImpl<>(color, color.size());
    }

    //TODO comments
    /**
     * calculates the color degree for every vertex and the maximum and minimum color degree for every color.
     *
     * @param r refining color (current color in the iteration)
     * @param color the color mapping
     * @param C the mapping from all colors to their classes
     * @param A the mapping from all colors to their classes with cdeg(v) >= 1
     * @param Colors_adj
     * @param maxcdeg
     * @param mincdeg
     * @param cdeg
     */
    private void calculateColorDegree(Integer r, Map<V, Integer> color, Map<Integer, LinkedList<V>> C, Map<Integer, LinkedList<V>> A, LinkedList<Integer> Colors_adj, Map<Integer, Integer> maxcdeg, Map<Integer, Integer> mincdeg, Map<V, Integer> cdeg) {
        for(V v : C.get(r)) {
            Set<E> N_minus = graph.incomingEdgesOf(v);
            for(E e : N_minus) {
                V w = graph.getEdgeTarget(e);
                cdeg.put(w, cdeg.get(w) + 1);
                if(cdeg.get(w) == 1) {
                    A.get(color.get(w)).add(w);
                }
                if(!Colors_adj.contains(color.get(w))) {
                    Colors_adj.add(color.get(w));
                }
                if(cdeg.get(w) > maxcdeg.get(color.get(w))) {
                    maxcdeg.put(color.get(w), cdeg.get(w));
                }
            }
        }

        for(Integer c : Colors_adj) {
            if(C.get(c).size() != A.get(c).size()) {
                mincdeg.put(c, 0);
            } else {
                mincdeg.put(c, maxcdeg.get(c));
                for(V v : A.get(c)) {
                    if (cdeg.get(v) < mincdeg.get(c)) {
                        mincdeg.put(c, cdeg.get(v));
                    }
                }
            }
        }
    }


    private void calculateColorPartition(Map<V, Integer> color, Map<Integer, LinkedList<V>> C, Map<Integer, LinkedList<V>> A, Stack<Integer> S_refine, Integer k, LinkedList<Integer> Colors_adj, Map<Integer, Integer> maxcdeg, Map<Integer, Integer> mincdeg, Map<V, Integer> cdeg) {
        LinkedList<Integer> Colors_split = new LinkedList<>(); // subset of Colors_adj (of colours) that will be split up
        for(Integer c : Colors_adj) {
            if(mincdeg.get(c) < maxcdeg.get(c)) {
                Colors_split.add(c);
            }
        }
        Colors_split.sort(Comparator.comparingInt(o -> o));
        for(Integer s : Colors_split) {
            SplitUpColor(s, color, C, A, S_refine, k, maxcdeg, mincdeg, cdeg);
        }
    }


    private void SplitUpColor(Integer s, Map<V, Integer> color, Map<Integer, LinkedList<V>> C, Map<Integer, LinkedList<V>> A, Stack<Integer> S_refine, Integer k, Map<Integer, Integer> maxcdeg, Map<Integer, Integer> mincdeg, Map<V, Integer> cdeg) {
        Map<Integer, Integer> numcdeg = new HashMap<>();
        Map<Integer, Integer> f = new HashMap<>();
        boolean instack;

        int maxcdeg_ = maxcdeg.get(s);

        for(int i = 1; i <= maxcdeg_; ++i) {
            numcdeg.put(i, 0);
        }
        numcdeg.put(0, C.get(s).size() - A.get(s).size());

        for(V v : A.get(s)) {
            numcdeg.put(cdeg.get(v), numcdeg.get(cdeg.get(v)) + 1);
        }
        int b = 0;
        for(int i = 1; i <= maxcdeg_; ++i) {
            if(numcdeg.get(i) > numcdeg.get(b)) {
                b = i;
            }
        }
        instack = S_refine.contains(s);

        addColorsToS_refine(s, maxcdeg_, mincdeg, S_refine, k, numcdeg, f, instack, b);

        for(V v : A.get(s)) {
            if(f.get(cdeg.get(v)) != s) {
                C.get(s).remove(v);
                C.get(f.get(cdeg.get(v))).add(v);
                color.put(v, f.get(cdeg.get(v)));
            }
        }
    }

    //TODO comments
    /**
     * adds all colors to S_refine which have to be refined further.
     *
     * @param s the current color
     * @param maxcdeg_ maximum color degree of s
     * @param mincdeg the mapping from color to its minimum color degree
     * @param S_refine the stack S_refine that administrates all colors tobe refined
     * @param k the number of currently used colors
     * @param numcdeg
     * @param f
     * @param instack
     * @param b
     */
    private void addColorsToS_refine(Integer s, Integer maxcdeg_, Map<Integer, Integer> mincdeg, Stack<Integer> S_refine, Integer k, Map<Integer, Integer> numcdeg, Map<Integer, Integer> f, boolean instack, int b) {
        for(int i = 0; i <= maxcdeg_; ++i) {
            if(numcdeg.get(i) >= 1) {
                if(i == mincdeg.get(s)) {
                    f.put(i, s);
                    if(!instack && b != i) {
                        S_refine.push(f.get(i));
                    }
                } else {
                    k++;
                    f.put(i, k);
                    if(instack && i != b) {
                        S_refine.push(f.get(i));
                    }
                }
            }
        }
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

    //TODO comments and implementation
    /**
     *
     *
     * @param alpha
     * @return
     */
    private Stack<Integer> getS_refine(Coloring<V> alpha) {
        Stack<Integer> S_refine = new Stack<>();
        //for(int i = alpha.size(); i > 0; --i) {
        //    S_refine.push(i);
        //}
        return S_refine;
    }

}
