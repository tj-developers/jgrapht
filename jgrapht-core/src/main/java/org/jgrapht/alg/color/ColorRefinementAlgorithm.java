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
        Integer n = graph.vertexSet().size();
        Integer k; // number of colors used
        Map<V, Integer> alpha = getAlpha(graph.vertexSet());

        Map<Integer, LinkedList<V>> C = new HashMap<>(); //initial colouring
        Map<Integer, LinkedList<V>> A = new HashMap<>(); // mapping from color to all vertices with that color

        Map<Integer, Integer> maxcdeg = new HashMap<>();
        Map<Integer, Integer> mincdeg = new HashMap<>();
        Map<V, Integer> cdeg = new HashMap<>();
        Map<V, Integer> colour = new HashMap<>(); // future coloring

        for(int c = 1; c <= n; ++c) {
            C.put(c, new LinkedList<>()); //initial colouring
            A.put(c, new LinkedList<>()); // mapping from color to all vertices with that color
            maxcdeg.put(c, 0);
        }
        for(V v : graph.vertexSet()) {
            C.get(alpha.get(v)).add(v);
            cdeg.put(v, 0);
            colour.put(v, alpha.get(v));
        }
        k = alpha.size(); // number of colors used
        Stack<Integer> S_refine = getS_refine(alpha);
        //S_refine.sort(Comparator.comparingInt(o -> o)); // sort stack ascendingly (not necessary with this implementation because getS_refine() already returns a sorted stack)
        LinkedList<Integer> Colors_adj = new LinkedList<>();

        while(!S_refine.empty()) {
            Integer r = S_refine.pop();

            //calculate number of neighbours of v of colour r
            calculateColourDegree(r, colour, C, A, Colors_adj, maxcdeg, mincdeg, cdeg);

            //calculate new partition of the colors
            calculatePartition(colour, C, A, S_refine, k, Colors_adj, maxcdeg, mincdeg, cdeg);

            //reset attributes for new iteration
            for(Integer c : Colors_adj) {
                for(V v : A.get(c)) {
                    cdeg.put(v, 0);
                }
                maxcdeg.put(c, 0);
                A.put(c, new LinkedList<>());
                Colors_adj.remove(c);
            }

        }

        return new ColoringImpl<>(colour, colour.size());
    }

    /**
     *
     *
     * @param r
     * @param colour
     * @param C
     * @param A
     * @param Colors_adj
     * @param maxcdeg
     * @param mincdeg
     * @param cdeg
     */
    private void calculateColourDegree(Integer r, Map<V, Integer> colour, Map<Integer, LinkedList<V>> C, Map<Integer, LinkedList<V>> A, LinkedList<Integer> Colors_adj, Map<Integer, Integer> maxcdeg, Map<Integer, Integer> mincdeg, Map<V, Integer> cdeg) {
        for(V v : C.get(r)) {
            Set<E> N_minus = graph.incomingEdgesOf(v);
            for(E e : N_minus) {
                V w = graph.getEdgeTarget(e);
                cdeg.put(w, cdeg.get(w) + 1);
                if(cdeg.get(w) == 1) {
                    A.get(colour.get(w)).add(w);
                }
                if(!Colors_adj.contains(colour.get(w))) {
                    Colors_adj.add(colour.get(w));
                }
                if(cdeg.get(w) > maxcdeg.get(colour.get(w))) {
                    maxcdeg.put(colour.get(w), cdeg.get(w));
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


    private void calculatePartition(Map<V, Integer> colour, Map<Integer, LinkedList<V>> C, Map<Integer, LinkedList<V>> A, Stack<Integer> S_refine, Integer k, LinkedList<Integer> Colors_adj, Map<Integer, Integer> maxcdeg, Map<Integer, Integer> mincdeg, Map<V, Integer> cdeg) {
        LinkedList<Integer> Colors_split = new LinkedList<>(); // subset of Colors_adj (of colours) that will be split up
        for(Integer c : Colors_adj) {
            if(mincdeg.get(c) < maxcdeg.get(c)) {
                Colors_split.add(c);
            }
        }
        Colors_split.sort(Comparator.comparingInt(o -> o));
        for(Integer s : Colors_split) {
            SplitUpColour(s, colour, C, A, S_refine, k, maxcdeg, mincdeg, cdeg);
        }
    }


    private void SplitUpColour(Integer s, Map<V, Integer> colour, Map<Integer, LinkedList<V>> C, Map<Integer, LinkedList<V>> A, Stack<Integer> S_refine, Integer k, Map<Integer, Integer> maxcdeg, Map<Integer, Integer> mincdeg, Map<V, Integer> cdeg) {
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
                colour.put(v, f.get(cdeg.get(v)));
            }
        }
    }


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
     *
     *
     * @param vertices
     * @return
     */
    private Map<V, Integer> getAlpha(Set<V> vertices) {
        Map<V, Integer> alpha = new HashMap<>();
        for(V v : vertices) {
            alpha.put(v, 1);
        }
        return alpha;
    }

    /**
     *
     *
     * @param alpha
     * @return
     */
    private Stack<Integer> getS_refine(Map<V, Integer> alpha) {
        Stack<Integer> S_refine = new Stack<>();
        for(int i = alpha.size(); i > 0; --i) {
            S_refine.push(i);
        }
        return S_refine;
    }

}
