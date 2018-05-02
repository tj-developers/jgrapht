package org.jgrapht.alg.treedecomposition;

import java.util.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

public interface TreeDecomposition<V,E,T>
{
    Graph<Set<V>,DefaultEdge> getTreeDecomposition();
}
