/*
 * (C) Copyright 2015-2018, by Alexey Kudinkin and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.alg.flow;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PushRelabelMFImplTest
    extends
    MaximumFlowAlgorithmTest
{
    @Override
    MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> createSolver(
        Graph<Integer, DefaultWeightedEdge> network)
    {
        return new PushRelabelMFImpl<>(network);
    }

    @Test
    public void testSimpleDirectedWeightedGraph(){
        SimpleDirectedWeightedGraph<Integer, DefaultEdge> graph = new SimpleDirectedWeightedGraph<>(DefaultEdge.class);

        graph.addVertex(-1);
        graph.addVertex(-2);
        graph.addVertex(0);
        graph.addVertex(1);

        graph.addEdge(-1, 0);
        graph.setEdgeWeight(graph.getEdge(-1, 0), 1.0);

        graph.addEdge(0, -2);
        graph.setEdgeWeight(graph.getEdge(0, -2), 0.9999999999999999);

        graph.addEdge(-1, 1);
        graph.setEdgeWeight(graph.getEdge(-1, 1), 1.0);

        graph.addEdge(1, -2);
        graph.setEdgeWeight(graph.getEdge(1, -2), 1.66498);

        graph.addEdge(0, 1);
        graph.setEdgeWeight(graph.getEdge(0, 1), 0.66498);

        graph.addEdge(1, 0);
        graph.setEdgeWeight(graph.getEdge(1, 0), 0.66498);

        PushRelabelMFImpl<Integer, DefaultEdge> mf = new PushRelabelMFImpl<>(graph);

        Assert.assertEquals(2.0, mf.calculateMinCut(-1, -2), 1e-9);
    }

    @Test
    public void testPushRelabelWithNonIdenticalNode()
    {
        SimpleDirectedGraph<String, DefaultEdge> g1 =
            new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        g1.addVertex("v0");
        g1.addVertex("v1");
        g1.addVertex("v2");
        g1.addVertex("v3");
        g1.addVertex("v4");
        g1.addEdge("v0", "v2");
        g1.addEdge("v3", "v4");
        g1.addEdge("v1", "v0");
        g1.addEdge("v0", "v4");
        g1.addEdge("v0", "v1");
        g1.addEdge("v2", "v1");

        MaximumFlowAlgorithm<String, DefaultEdge> mf1 = new PushRelabelMFImpl<>(g1);
        String sourceFlow = "v3";
        String sinkFlow = "v0";
        double flow = mf1.calculateMaximumFlow(sourceFlow, sinkFlow);
        assertEquals(0.0, flow, 0);
    }
}
