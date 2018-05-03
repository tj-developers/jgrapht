package org.jgrapht.alg.treedecomposition;

import java.util.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

public interface TreeDecomposition<V,E>
{
    Graph<Set<V>,DefaultEdge> getTreeDecomposition();
}
