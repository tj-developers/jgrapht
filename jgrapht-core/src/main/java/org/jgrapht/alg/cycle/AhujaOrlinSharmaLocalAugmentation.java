package org.jgrapht.alg.cycle;

import org.jgrapht.Graph;

import java.util.*;

public class AhujaOrlinSharmaLocalAugmentation<V, E> {

    private Graph<V, E> graph;
    private Map<V, Integer> labels;
    private int lengthBound;

    public AhujaOrlinSharmaLocalAugmentation(Graph<V, E> graph, int lengthBound, Map<V, Integer> labels) {
        this.graph = graph;
        this.lengthBound = lengthBound;
        this.labels = labels;
    }

    public LabeledPath<V> getLocalAugmentationCycle() {

        int k = 1;
        LabeledPath<V> C = new LabeledPath<>();

        Set<LabeledPath<V>> PathsLengthK = new LinkedHashSet<>();
        Set<LabeledPath<V>> PathsLengthKplus1 = new LinkedHashSet<>();

        // initialize PathsLengthK for k = 1
        for(E e : graph.edgeSet()) {
            if(graph.getEdgeWeight(e) < 0) {
                // initialize all paths of cost < 0
                LinkedList<V> pathVertices = new LinkedList<>();
                Map<V, Integer> pathLabels = new LinkedHashMap<>();
                V sourceVertex = graph.getEdgeSource(e);
                V targetVertex = graph.getEdgeTarget(e);
                pathVertices.add(sourceVertex);
                pathVertices.add(targetVertex);
                pathLabels.put(sourceVertex, labels.get(sourceVertex));
                pathLabels.put(targetVertex, labels.get(targetVertex));
                LabeledPath<V> path = new LabeledPath<>(pathVertices, graph.getEdgeWeight(e), pathLabels);

                // add path to set of paths of length 1
                PathsLengthK.add(path);
            }
        }

        while(k < lengthBound && C.getCost() >= 0) {
            while(!PathsLengthK.isEmpty()) {
                for(LabeledPath<V> path : PathsLengthK) {
                    V head = path.getHead();
                    V tail = path.getTail();

                    if(graph.containsEdge(tail, head) && path.getCost() + graph.getEdgeWeight(graph.getEdge(tail, head)) < C.getCost()) {
                        C.addVertex(head, graph.getEdgeWeight(graph.getEdge(tail, head)), labels.get(head));
                    }

                    for(E e : graph.outgoingEdgesOf(tail)) {
                        V currentVertex = graph.getEdgeTarget(e);
                        if(path.getLabels().contains(labels.get(currentVertex)) && path.getCost() + graph.getEdgeWeight(e) < 0) {
                            LabeledPath<V> newPath = path.clone();
                            newPath.addVertex(currentVertex, graph.getEdgeWeight(e), labels.get(currentVertex));
                            PathsLengthKplus1.add(newPath);

                            testDomination(path,  PathsLengthKplus1, PathsLengthK);
                        }
                    }
                }
                k += 1;
                PathsLengthK = PathsLengthKplus1;
                PathsLengthKplus1.clear();
            }
        }

        if(!C.isEmpty()) {
            return C;
        }
        return null;
    }

    private void testDomination(LabeledPath<V> path, Set<LabeledPath<V>> PathsLengthKplus1, Set<LabeledPath<V>> PathsLengthK) {
        // TODO
    }

    private boolean dominates(LabeledPath<V> path1, LabeledPath<V> path2) {

        if(!(path1.getCost() < path2.getCost())) {
            return false;
        }
        if(!path1.getTail().equals(path2.getTail())) {
            return false;
        }
        if(!path1.getHead().equals(path2.getHead())) {
            return false;
        }
        if(!path2.getLabels().containsAll(path1.getLabels())) {
            return false;
        }

        return true;
    }

}
