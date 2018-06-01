package org.jgrapht.alg.cycle;

import org.jgrapht.util.TypeUtil;

import java.util.*;

public class LabeledPath<V> {

    private LinkedList<V> vertices;
    private Map<V, Integer> labels;

    private double cost;

    public LabeledPath(LinkedList<V> vertices, double cost, Map<V, Integer> labels) {
        this.vertices = vertices;
        this.cost = cost;
        this.labels = labels;
    }

    public LabeledPath() {
        this(new LinkedList<>(), Double.MAX_VALUE, new LinkedHashMap<>());
    }

    public void addVertex(V v, double edgeCost, int label) {
        this.vertices.add(v);
        this.cost += edgeCost;
        this.labels.put(v, label);
    }

    public V getHead() {
        return vertices.getFirst();
    }

    public V getTail() {
        return vertices.getLast();
    }

    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    public LinkedList<V> getVertices() {
        return vertices;
    }

    public Collection<Integer> getLabels() {
        return labels.values();
    }

    public double getCost() {
        return cost;
    }

    public LabeledPath<V> clone() {
        try {
            LabeledPath<V> newLabeledPath = TypeUtil.uncheckedCast(super.clone());
            newLabeledPath.vertices = (LinkedList<V>) this.vertices.clone();
            newLabeledPath.cost = this.cost;

            return newLabeledPath;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
