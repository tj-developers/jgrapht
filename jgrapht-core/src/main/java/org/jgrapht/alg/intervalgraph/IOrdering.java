package org.jgrapht.alg.intervalgraph;

import org.jgrapht.Graph;

public class IOrdering<V>{

    /**
     * Calculates if the given sweep is an I-Ordering
     * (according to the Graph graph)
     *
     * @param sweep the order we want to check if its an I-Order
     * @param graph the graph we want to check if its an I-Order
     * @return true, if sweep is an I-Order according to graph
     */
    public boolean IsIOrder(V[] sweep, Graph graph)
    {
        for (int i=0; i<sweep.length-2; i++) {
            for (int j=i+1; j<sweep.length-1; j++) {
                for (int k=j+1; k<sweep.length; k++) {
                    boolean edgeIJ = graph.containsEdge(sweep[i], sweep[j]);
                    boolean edgeIK = graph.containsEdge(sweep[i], sweep[k]);
                    if (edgeIK) {
                        if (edgeIJ) { } else {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
