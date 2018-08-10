package org.jgrapht.alg.interval;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.graph.interval.*;

/**
 * TODO: better Javadoc
 * @author Ira Justus Fesefeldt (PhoenixIra)
 * @author Timofey Chudakov
 *
 * @param <V> the vertex type of the graph
 * @param <E> the edge type of the graph
 */
public class KorteMoehringIntervalGraphRecognizer<V, E> implements IntervalGraphRecognizerInterface<V>
{

	// The recognized graph
	private Graph<V, E> graph;

	ChordalityInspector<V, E> chorInspec;

	private MPQTreeNode treeRoot;

	// private HashMap<V,Set<MPQTreeNodeSetElement>> vertexToListPositionMap;

	private boolean isIntervalGraph;
	private boolean isChordal;

	/**
	 * Constructor for the algorithm
	 * @param graph the graph which should be recognized
	 */
	public KorteMoehringIntervalGraphRecognizer(Graph<V, E> graph) {
		this.graph = graph;
		chorInspec = new ChordalityInspector<>(graph);
		treeRoot = new PNode(null);
	}

	/**
	 * TODO: better Javadoc
	 * 
	 * the Korte-Moehring Algorithm, which tests the graphs with an MPQ tree for an interval representation.
	 * If the algorithm returns true, we can computed an interval representation of the MPQ Tree
	 * If the algorithm returns false, we can computed an counter example of the MPQ Tree
	 */
	private void testIntervalGraph()
	{

		//check for chordality
		isChordal = chorInspec.isChordal();
		if(!isChordal) 
		{
			isIntervalGraph = false;
			return;
		}

		// init all relevant objects
		Map<V, Integer> vertexOrder = getVertexInOrder(chorInspec.getPerfectEliminationOrder());
		// iterate over the perfect elimination order
		for (V u : chorInspec.getPerfectEliminationOrder()) {
			// calculate Adj(u) - the predecessors of u
			Set<V> predecessors = getPredecessors(vertexOrder, u);

			// special case for predecessors is empty
			if (predecessors.isEmpty()) {
				addEmptyPredecessors(u);
				continue;
			}

			// labeling phase: 
			// 1 if one but not all vertices in a PQNode is a predecessor
			// 2/inf if all vertices in a PQNode is a predecessor
			Map<MPQTreeNode,Integer> positiveLabels = labelTree(predecessors);

			// test phase:
			// check for path of positive labels
			if(!testPath(positiveLabels.keySet()) 
					//check if outer sections of Q nodes N contain predecessors intersection V(N)
					| !testOuterSectionsOfQNodes(positiveLabels.keySet(), predecessors))
			{
				//then this is not an interval graph
				isIntervalGraph = false;
				return;
			}

			// update phase:
			// generate the path
			List<MPQTreeNode> path = getPath(positiveLabels.keySet());

			//get lowest positive node in path
			MPQTreeNode Nsmall = getNSmall(path, positiveLabels);

			//get highest non-inf node in path
			MPQTreeNode Nbig = getNBig(path, positiveLabels);

			//this part needs to change, no need for nsmall=nbig
			//update MPQ Tree
			if(Nsmall.equals(Nbig))
				addVertexToLeaf(u,path);
			else
				changedPathToTemplates(u,path,Nsmall,Nbig);

		}
	}

	/**
	 * Returns the predecessors of {@code vertex} in the order defined by {@code map}. More
	 * precisely, returns those of {@code vertex}, whose mapped index in {@code map} is less then
	 * the index of {@code vertex}.
	 *
	 * @param vertexInOrder defines the mapping of vertices in {@code graph} to their indices in
	 *        order.
	 * @param vertex the vertex whose predecessors in order are to be returned.
	 * @return the predecessors of {@code vertex} in order defines by {@code map}.
	 */
	private Set<V> getPredecessors(Map<V, Integer> vertexInOrder, V vertex)
	{
		Set<V> predecessors = new HashSet<>();
		Integer vertexPosition = vertexInOrder.get(vertex);
		Set<E> edges = graph.edgesOf(vertex);
		for (E edge : edges) {
			V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
			Integer destPosition = vertexInOrder.get(oppositeVertex);
			if (destPosition < vertexPosition) {
				predecessors.add(oppositeVertex);
			}
		}
		return predecessors;
	}

	/**
	 * Returns a map containing vertices from the {@code vertexOrder} mapped to their indices in
	 * {@code vertexOrder}.
	 *
	 * @param vertexOrder a list with vertices.
	 * @return a mapping of vertices from {@code vertexOrder} to their indices in
	 *         {@code vertexOrder}.
	 */
	private Map<V, Integer> getVertexInOrder(List<V> vertexOrder)
	{
		Map<V, Integer> vertexInOrder = new HashMap<>(vertexOrder.size());
		int i = 0;
		for (V vertex : vertexOrder) {
			vertexInOrder.put(vertex, i++);
		}
		return vertexInOrder;
	}

	/**
	 * Changed the MPQ Tree if u has no predecessors.
	 * Adds a new leaf node with the bag of this vertex to the root.
	 * 
	 * @param u the vertex to be added to the MPQ Tree
	 */
	private void addEmptyPredecessors(V u) {
		HashSet<V> elements = new HashSet<>();  // to be implemented by the doubly linked circular list
		elements.add(u);
		MPQTreeNode leaf = new PNode(elements);
		treeRoot.addChild(leaf);
		leaf.parent = treeRoot;
	}

	/**
	 * TODO: Better Javadoc
	 * Label every positive vertex in the MPQ Tree
	 * 
	 * @param predecessors the predecessors which are used to label the vertices in the tree
	 * @return the labeling of all positive labeled vertices
	 */
	private Map<MPQTreeNode,Integer> labelTree(Set<V> predecessors)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO: Better Javadoc
	 * tests if positiveLabels form a path
	 * 
	 * @param positiveLabels the vertices which should form a path
	 * @return true iff it forms a path
	 */
	private boolean testPath(Set<MPQTreeNode> positiveLabels)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * TODO: Better Javadoc
	 * tests if an outer section of every Q nodes N in positive labels contains predecessors intersection V(N)
	 * 
	 * @param positiveLabels the positive vertices
	 * @param predecessors the predecessors of u
	 * @return true iff it fulfills the condition
	 */
	private boolean testOuterSectionsOfQNodes(Set<MPQTreeNode> positiveLabels, Set<V> predecessors)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * TODO: better Javadoc
	 * computes a path from the root to a leaf, containing all positive vertices
	 * 
	 * @param positiveLabels the vertices which forms a path
	 * @return the path from root to a leaf
	 */
	private List<MPQTreeNode> getPath(Set<MPQTreeNode> positiveLabels)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO: better Javadoc
	 * computes the smallest vertex N of the Tree which has a positive label
	 * 
	 * @param path the path from root to leaf
	 * @param positiveLabels the map from nodes to positive labels
	 * @return smalles vertex N with positive label
	 */
	private MPQTreeNode getNSmall(List<MPQTreeNode> path, Map<MPQTreeNode, Integer> positiveLabels)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO: better Javadoc
	 * computes the highest vertex N of the tree which is non-empty and non-inf
	 * 
	 * @param path the path from root to leaf
	 * @param positiveLabels the map from nodes to positive labels
	 * @return highest non-empty, non-inf vertex N
	 */
	private MPQTreeNode getNBig(List<MPQTreeNode> path, Map<MPQTreeNode, Integer> positiveLabels)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO: better Javadoc
	 * Adds the vertex u to the leaf of the path
	 * 
	 * @param u the vertex to be added
	 * @param path the path of the leaf
	 */
	//here the path is modified, not the tree 
	private void addVertexToLeaf(V u, List<MPQTreeNode> path)
	{
		//get the last vertex 
		//todo: better naming for the varibles in terms of the paper
		int lastIndexofPath= path.size()-1;
		MPQTreeNode lastNodeInPath = path.get(lastIndexofPath);
		//check if P or Q or leaf

		if(lastNodeInPath.getClass()== Leaf.class) {


			HashMap<Integer, HashSet<V>> partitionedVertexSet =partitionVertexSet(u,graph, lastNodeInPath);

			//check if B is empty

			if(partitionedVertexSet.get(1).isEmpty()) {

				path.get(lastIndexofPath).elements.add(u);
				
			}
			
			else {
				//transform the leaf containing A+B into a PNode containing A
				
				path.remove(lastIndexofPath);
				MPQTreeNode newPNode = new PNode( partitionedVertexSet.get(0));
				//Create two leaves for PNode children, add the children to the PNode
				HashSet<V> leafElement = new HashSet<>();
				leafElement.add(u);
				MPQTreeNode leaf1 = new Leaf(leafElement);
				MPQTreeNode leaf2 = new Leaf( partitionedVertexSet.get(1));
				leaf1.parent=newPNode;
				leaf2.parent=newPNode;
				
				//add children to the PNode
				
				//add the PNode to the tree


			}

		} else if (lastNodeInPath.getClass()== PNode.class) {

			HashMap<Integer, HashSet<V>> partitionedVertexSet =partitionVertexSet(u,graph, lastNodeInPath);
			if(partitionedVertexSet.get(1).isEmpty()) {
				//swap P node containing A+B with P Node containing just A,since B is empty makes no change
				HashSet<V> leafElement = new HashSet<>();
				leafElement.add(u);
				MPQTreeNode newLeaf= new Leaf(leafElement);
				newLeaf.parent=lastNodeInPath;
				
				//add child to the PNode
				//add the PNode to the tree

			}else {
				//update the previous PNode elements with elements in A
			//	lastNodeInPath.elements = partitionedVertexSet.get(0);
				path.get(lastIndexofPath).elements=partitionedVertexSet.get(0);
				//create a new PNode and add the B set
				HashSet<V> newPNodeElements = new HashSet<>();
				newPNodeElements.addAll(partitionedVertexSet.get(1));

				MPQTreeNode newPNode = new PNode(newPNodeElements);
				//newPNode.parent=lastNodeInPath;
				
				
				//add this to the tree
				path.a
				
				
				
				//all children of leafNodeinPath will become newPNode's children
				
				
				
				

			}


		} else if(lastNodeInPath.getClass()== QNode.class) {
			//do we need to check for Qsections as well}
		}









	}

	//make this lessless  ugly and javadoc

	HashMap<Integer, HashSet<V>> partitionVertexSet(V u, Graph graph, MPQTreeNode node){

		//find the vertices associated with the node
		HashSet<V> elementsInNode = node.elements;

		//find vertices adjacent to u in graph G
		List<V> neighbourVerticesofV   = Graphs.neighborListOf(graph, u);

		HashSet<V> vertexPartitionSetA = new HashSet<>();
		HashSet<V> vertexPartitionSetB = new HashSet<>();
		Map<Integer, HashSet<V>> vertexPartitionMap= new HashMap<Integer, HashSet<V>>();

		//make this better //check complexity
		if(elementsInNode.size() == neighbourVerticesofV.size() && elementsInNode.containsAll(neighbourVerticesofV) ) {
			//if this fails, change the list to hashset
			vertexPartitionSetA =  (HashSet<V>) neighbourVerticesofV; 
			vertexPartitionMap.put(1,(HashSet<V>) vertexPartitionSetA);
			vertexPartitionMap.put(2,(HashSet<V>) vertexPartitionSetB);

		}else {
			for(V vertex:elementsInNode ) {
				V v= vertex;
				if(neighbourVerticesofV.contains(vertex)) {

					vertexPartitionSetA.add(vertex);
				}else {
					vertexPartitionSetB.add(vertex);
				}
			}

			vertexPartitionMap.put(1,vertexPartitionSetA);
			vertexPartitionMap.put(2,vertexPartitionSetB);


		}







		return (HashMap<Integer, HashSet<V>>) vertexPartitionMap;

	}

	/**
	 * TODO: better Javadoc
	 * Checks the path for specific patterns and changes every node accordingly
	 * 
	 * @param u the vertex to add to the tree
	 * @param path the path of vertices to be changed
	 * @param nSmall the smalles positive node in path
	 * @param nBig the highest non-empty, non-inf node in path
	 */
	private void changedPathToTemplates(V u, List<MPQTreeNode> path, MPQTreeNode nSmall, MPQTreeNode nBig)
	{
		//traverse from nSmall to nBig
		int currentIndex = path.indexOf(nSmall);
		int maxIndex = path.indexOf(nBig);


		//case L2
		//for each node from nsmall to nBig, get the vertexSpit, and then do accordingly

		while(currentIndex != maxIndex ) {

			HashSet<V> elements = path.get(currentIndex).elements;
			HashMap<Integer, HashSet<V>> vertexSplitMap = partitionVertexSet(u, graph, path.get(currentIndex));


			//create a Qnode with two Q sections each containing A

			QSectionNode qSectionA= new QSectionNode(vertexSplitMap.get(0));
			QSectionNode qSectionB = qSectionA;
			qSectionA.rightSibling=qSectionB;
			qSectionB.leftSibling=qSectionA;
			//how to add the elements of u? ->
			//qSectionA.child=new Leaf();



			QNode newQNode = new QNode(qSectionA); 
			newQNode.rightmostSection = qSectionB;



			// add u to one section and B to the other


		}





	}

	@Override
	public boolean isIntervalGraph()
	{
		return isIntervalGraph;
	}

	@Override
	public List<Interval<Integer>> getIntervalsSortedByStartingPoint()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<V, IntervalVertexPair<V, Integer>> getVertexToIntervalMap()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Interval<Integer>, V> getIntervalToVertexMap()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO: better javadoc
	 * 
	 * the hole in the graph as an counter example for chordality
	 * @return a hole if the graph is not chordal, or null if the graph is chordal.
	 */
	public GraphPath<V,E> getHole()
	{
		return chorInspec.getHole();
	}

	/**
	 * TODO: better javadoc
	 * 
	 * the Umbrella sub graph in the graph iff the graph is chordal but not an interval graph
	 * @return an umbrella if the graph is not an intervalgraph, or null if the graph is an intervalgraph.
	 */
	public Graph<V,E> getUmbrellaSubGraph()
	{
		// TODO implement
		return null;
	}

	/**
	 * A node of a modified PQ-tree
	 */
	private abstract class MPQTreeNode {

		/**
		 * The parent of the current node
		 */
		MPQTreeNode parent;

		/**
		 * The graph vertices associated with the current tree node
		 *
		 * The associated set of vertices is given by a doubly linked circular list
		 */
		HashSet<V> elements = null;

		/**
		 * Instantiate a tree node associating with no graph vertex
		 */
		MPQTreeNode() { }

		/**
		 * Instantiate a tree node associating with a set of graph vertices
		 * TODO: replace this with an addElement method later
		 *
		 * @param elements a set of graph vertices associated with this tree node
		 */
		MPQTreeNode(HashSet<V> elements) {
			this.elements = elements;
		}

	}

	/**
	 * A P-node of a modified PQ-tree
	 */
	private class PNode extends MPQTreeNode {

		/**
		 * The children of a P-node are stored with a doubly linked circular list
		 * <p>
		 * P-node has a pointer of the current child as the entrance to this list
		 */
		MPQTreeNode currentChild;

		/**
		 * Instantiate a P node associating with a set of graph vertices
		 *
		 * @param elements a set of graph vertices associated with this P node
		 */
		PNode(HashSet<V> elements) {
			super(elements);
		}

		/**
		 * add child for the current P-node
		 *
		 * @param child the child node to be added
		 */
		void addChild(MPQTreeNode child) {
			// TODO: add child according to the template operations
		}

	}

	/**
	 * A Q-node of a modified PQ-tree
	 */
	private class QNode extends MPQTreeNode {

		/**
		 * The children of a Q-node are stored with a doubly linked list
		 * <p>
		 * Q-node has two pointers of the outermost sections as the entrances to this list
		 */
		QSectionNode leftmostSection;
		QSectionNode rightmostSection;

		/**
		 * Instantiate a Q node associating with a set of graph vertices
		 */
		QNode(QSectionNode section) {
			super(null); // elements of Q-node are currently stored in the corresponding section nodes, make this null here
			this.leftmostSection = section;
			this.rightmostSection = section;
		}

	}

	/**
	 * A section node of a Q-node
	 */
	private class QSectionNode extends MPQTreeNode {

		/**
		 * The child of the current Q section node
		 *
		 * Each section has a pointer to its son
		 */
		MPQTreeNode child;

		/**
		 * The sections have a pointer to their neighbor sections
		 * <p>
		 * For the left most section, the left sibling is null
		 * For the right most section, the right sibling is null
		 */
		QSectionNode leftSibling;
		QSectionNode rightSibling;

		QSectionNode(HashSet<V> elements) {
			super(elements);
		}

	}

	/**
	 * A leaf node of a modified PQ-tree
	 */
	private class Leaf extends MPQTreeNode {

		Leaf(HashSet<V> elements) {
			this.elements = elements;
		}

	}

	/**
	 * the label of a node N or a section S of a Q-node
	 */
	private enum Label {

		ALL(2), SOME(1), NONE(0);

		private int value;

		Label(int value) {
			this.value = value;
		}
	}

}
