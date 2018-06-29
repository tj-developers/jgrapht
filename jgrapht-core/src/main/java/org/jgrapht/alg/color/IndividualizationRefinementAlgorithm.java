package org.jgrapht.alg.color;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.io.Serializable;
import java.util.*;

public class IndividualizationRefinementAlgorithm<V, E> implements VertexColoringAlgorithm, Serializable {

    private static final long serialVersionUID = -4443873488410934998L;

    private final Graph<V, E> graph;

    public IndividualizationRefinementAlgorithm(Graph<V, E> graph) {
        this.graph = graph;
    }

    /**
     * calculates the lexicographically smallest canonical bijective coloring of the given graph.
     * That is, the coloring is a canonical order of the graph vertices.
     *
     * @return the calculated coloring
     */
    @Override
    public Coloring getColoring() {
        Integer countNodes = 1;

        Deque<Integer> stack = new ArrayDeque<>(graph.vertexSet().size());
        LinkedList<Coloring<V>> leaves = new LinkedList<>();

        Map<Integer, Coloring<V>> D = new HashMap<>();
        Map<Integer, Integer> p = new HashMap<>();
        Map<Integer, Set<V>> E = new HashMap<>();

        Map<Integer, Set<V>> done = new HashMap<>();

        Integer r = 1;

        D.put(r, new ColorRefinementAlgorithm<>(graph).getColoring());
        p.put(r, 1);

        stack.push(r);

        while(!stack.isEmpty()) {
            Integer t = stack.peek();
            if(isColoringDiscrete(D.get(t))) {
                leaves.add(D.get(t));
                stack.pop();
            } else if (!E.containsKey(t)) {
                E.put(t, calculateCanonicallyFirstRefineColor(t));
                done.put(t, new HashSet<>());
            }

            Set<V> EMinusDone = calculateSetMinus(E.get(t), done.get(t));
            if(!EMinusDone.isEmpty()) {
                V v = EMinusDone.iterator().next();
                done.get(t).add(v);
                Integer u = countNodes++;
                Coloring<V> alpha = calculateRefinedColoring(D.get(t), v);
                D.put(u, new ColorRefinementAlgorithm<>(graph, alpha).getColoring());
                p.put(u, p.get(t) + 1);
                stack.push(u);
            } else {
                stack.pop();
            }
        }

        return calculateLexicographicallyFirstColoring(leaves);
    }

    private boolean isColoringDiscrete(Coloring<V> coloring) {
        return graph.vertexSet().size() == coloring.getColorClasses().size();
    }

    private Set<V> calculateCanonicallyFirstRefineColor(Integer t) {
        return null;
    }

    private Set<V> calculateSetMinus(Set<V> set1, Set<V> set2) {
        Set<V> res = new HashSet<>();
        for(V v1 : set1) {
            if(!set2.contains(v1)) {
                res.add(v1);
            }
        }
        return res;
    }

    private Coloring<V> calculateRefinedColoring(Coloring<V> coloring, V v) {
        return null;
    }

    private Coloring<V> calculateLexicographicallyFirstColoring(LinkedList<Coloring<V>> leaves) {
        Comparator<Coloring<V>> leafComparator = new Comparator<Coloring<V>>() {
            @Override
            public int compare(Coloring<V> o1, Coloring<V> o2) {
                return 0;
            }
        };

        Coloring<V> first = leaves.getFirst();
        for(Coloring<V> leaf : leaves) {
            if(leafComparator.compare(first, leaf) < 1) {
                first = leaf;
            }
        }

        return first;
    }
}
