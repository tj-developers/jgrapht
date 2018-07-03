package org.jgrapht.alg.color;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class IndividualizationRefinementAlgorithm<V, E> implements VertexColoringAlgorithm, Serializable {

    private static final long serialVersionUID = -4443873488410934998L;

    /**
     * the input graph
     */
    private final Graph<V, E> graph;

    /**
     * Constructs a new individualization algorithm.
     *
     * @param graph the input graph
     */
    public IndividualizationRefinementAlgorithm(Graph<V, E> graph) {
        this.graph = graph;
    }

    // TODO delete all strings/colorings that are not lexicographically smallest -> it is implemented like this, because it is needed for further improvements

    /**
     * Calculates the lexicographically smallest canonical bijective coloring of the given graph.
     * That is, the coloring is a canonical order of the graph vertices.
     *
     * @return the lexicographically smallest canonical bijective coloring
     */
    @Override
    public Coloring getColoring() {
        // counts the number of nodes in the search tree. This is done to have an index for all nodes.
        Integer countNodes = 1;

        // stack for all created nodes in the search tree
        Deque<Integer> stack = new ArrayDeque<>(graph.vertexSet().size());
        // a list that stores all discrete colorings. The smallest of them is the coloring we want to have.
        LinkedList<Coloring<V>> leaves = new LinkedList<>();

        // mapping from each node to its coloring
        Map<Integer, Coloring<V>> D = new HashMap<>();
        // mapping from nodes of the search tree and the depth in the search tree (length of path from root)
        Map<Integer, Integer> p = new HashMap<>();
        // stores a set of all vertices that will cause children nodes in the search tree
        Map<Integer, Set<V>> E = new HashMap<>();

        // stores all processed vertices of the nodes in the search tree
        Map<Integer, Set<V>> done = new HashMap<>();

        // create a new node r (the root)
        Integer r = 1;

        // calculate coloring for first node with color-refinement
        D.put(r, new ColorRefinementAlgorithm<>(graph).getColoring());
        // root has path length of 1
        p.put(r, 1);

        // r has to be refined
        stack.push(r);

        // process all nodes
        while(!stack.isEmpty()) {

            // get node that has to be processed
            Integer t = stack.peek();

            // if the coloring is discrete we found a leaf node, otherwise, we have to refine the coloring further
            if(isColoringDiscrete(D.get(t))) {
                leaves.add(D.get(t));
                stack.pop();
            } else if (!E.containsKey(t)) { // t produces child nodes. If E does not contain t, add it.
                // add t to E
                E.put(t, calculateCanonicallyFirstRefineColor(D.get(t)));
                // t is processed so add it to done
                done.put(t, new HashSet<>());
            }

            // calculate all vertices that have to be processed
            Set<V> EMinusDone = calculateSetMinus(E.get(t), done.get(t));
            if(!EMinusDone.isEmpty()) { // refine all non-processed vertices of coloring of node t
                // get next vertex that has to be processed
                V v = EMinusDone.iterator().next();
                // add v to the processed vertices
                done.get(t).add(v);
                // create new node in the search tree
                Integer u = countNodes++;
                // refine coloring of t at v with p.get(t)
                Coloring<V> alpha = calculateRefinedColoring(D.get(t), v, p.get(t));
                // execute color refinement with the new refined coloring
                D.put(u, new ColorRefinementAlgorithm<>(graph, alpha).getColoring());
                // path length of node u is path length of t plus one because we added u after t in the search tree
                p.put(u, p.get(t) + 1);
                // u has to be processed further
                stack.push(u);
            } else { // nothing to do
                stack.pop();
            }
        }

        // return the lexicographically smallest coloring
        return calculateLexicographicallyFirstColoring(leaves);
    }

    /**
     * Checks whether the given <code>coloring</code> is discrete, that is every color class has size 1.
     *
     * @param coloring the coloring to check
     *
     * @return whether the given <code>coloring</code> is discrete.
     */
    private boolean isColoringDiscrete(Coloring<V> coloring) {
        List<Set<V>> colorClasses = coloring.getColorClasses();
        // check whether the coloring is inconsistent - fail fast
        if(coloring.getNumberColors() != colorClasses.size()) {
            throw new IllegalArgumentException("Individualization refinement calculated an incorrect coloring. This is a bug.");
        }
        // return if the number of color classes is the same as the number of vertices in the graph, then it is discrete
        return graph.vertexSet().size() == colorClasses.size();
    }

    /**
     * Calculates the first color that has to be refined.
     * This is color i in image(<code>coloring</code>) minimum such that the color class of color i is greater than or equal 2
     * and is smaller or equal than any other color class with greater than or equal 2.
     *
     * @param coloring the coloring to refine
     *
     * @return the color to be refined.
     */
    private Set<V> calculateCanonicallyFirstRefineColor(Coloring<V> coloring) {
        // stores the number of vertices of color class of color i
        int min = -1;
        // stores the color class of i
        Set<V> minimumColorClass = null;

        for(Set<V> colorClass : coloring.getColorClasses()) {
            // consider only non-discrete parts of the coloring, that is the number of vertices in the color class has
            // to be greater than 1
            if(colorClass.size() >= 2) {
                if(
                        // initialise min and minimumColorClass
                        min == -1
                        // normal cases
                        || colorClass.size() < min
                        // safety case because it is unknown whether the list is sorted by the colors
                        || (colorClass.size() <= min
                            && colorClass.iterator().hasNext()
                            && minimumColorClass.iterator().hasNext()
                            && coloring.getColors().get(colorClass.iterator().next())
                                < coloring.getColors().get(minimumColorClass.iterator().next()))
                ) {
                    // store new minimum
                    min = colorClass.size();
                    minimumColorClass = colorClass;
                }
            }
        }

        // safety check, the contrary cannot happen
        if (minimumColorClass != null) {
            return new HashSet<>(minimumColorClass);
        }
        // fail fast
        throw new IllegalStateException("This is a bug in individualization refinement");
    }

    /**
     * Calculates the relative complement of <code>set2</code> in <code>set1</code>, that is <code>set1</code> - <code>set2</code>.
     *
     * @param set1 the first set
     * @param set2 the second set
     *
     * @return the relative complement of <code>set2</code> in <code>set1</code>.
     */
    private Set<V> calculateSetMinus(Set<V> set1, Set<V> set2) {
        Set<V> res = new HashSet<>();
        for(V v1 : set1) {
            if(!set2.contains(v1)) {
                res.add(v1);
            }
        }
        return res;
    }

    /**
     * Calculates C_refinement, the refinement of <code>coloring</code> at color <code>v</code> on <code>i</code>, that is,
     *   C_refinement(w)    = i,                    if w = v;
     *                      = coloring.get(w),      if w != v and coloring.get(w) < i
     *                      = coloring.get(w) + 1,  if w != v and coloring.get(w) >= i
     * and returns it.
     * <code>v</code> has to be in <code>coloring</code>
     *
     * @param coloring the coloring to refine
     * @param v the vertex to refine at
     * @param i the color to refine with
     *
     * @return C_refinement as defined above.
     */
    private Coloring<V> calculateRefinedColoring(Coloring<V> coloring, V v, Integer i) {

        Map<V, Integer> newColoring = new HashMap<>();

        for(V w : coloring.getColors().keySet()) {
            if(w == v) {
                newColoring.put(v, i);
            } else if(coloring.getColors().get(w) < i) {
                newColoring.put(w, coloring.getColors().get(w));
            } else {
                newColoring.put(w, coloring.getColors().get(w) + 1);
            }
        }

        return new ColoringImpl<>(newColoring, coloring.getNumberColors() + 1);
    }

    /**
     * Calculates the lexicographically smallest coloring of all calculated discrete colorings (the leaves).
     *
     * @param leaves the list of all calculated discrete colorings (the leaves)
     *
     * @return the lexicographically smallest coloring of all calculated discrete colorings (the leaves)
     */
    private Coloring<V> calculateLexicographicallyFirstColoring(List<Coloring<V>> leaves) {
        // comparator for the lexicographical ordering on the colorings
        Comparator<Coloring<V>> leafComparator = (o1, o2) -> {
            List<Set<V>> colorClasses1 = o1.getColorClasses();
            List<Set<V>> colorClasses2 = o2.getColorClasses();

            // safety check, this cannot happen
            if(colorClasses1.size() != graph.vertexSet().size()
                    || o1.getNumberColors() != graph.vertexSet().size()
                    || colorClasses1.size() != colorClasses2.size()
                    || o1.getNumberColors() != o2.getNumberColors()) {
                // fail fast
                throw new IllegalArgumentException("Individualization refinement calculated an incorrect coloring. This is a bug.");
            }

            Iterator<Set<V>> it1 = colorClasses1.iterator();
            Iterator<Set<V>> it2 = colorClasses2.iterator();
            while (it1.hasNext()) {
                if(it1.next().size() == it2.next().size()) {
                    break;
                } else if(it1.next().size() < it2.next().size()) {
                    return -1;
                } else if(it1.next().size() > it2.next().size()) {
                    return 1;
                }
            }
            return 0;
        };

        // search for lexicographical minimum
        Coloring<V> first = leaves.get(0);
        for(Coloring<V> leaf : leaves) {
            if(leafComparator.compare(first, leaf) < 0) {
                first = leaf;
            }
        }

        return first;
    }
}
