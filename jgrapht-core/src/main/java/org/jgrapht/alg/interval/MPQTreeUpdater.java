package org.jgrapht.alg.interval;

import org.jgrapht.alg.interval.mpq.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * The helper class providing static methods to update the MPQ tree
 *
 * @author Suchanda Bhattacharyya (dia007)
 * @author Jiong Fu (magnificent_tony)
 */
final class MPQTreeUpdater {

    /**
     * Add the vertex to the given tree node
     *
     * @param vertex        the vertex to be added
     * @param currentNode   the current node N
     * @param lowerMostNode the lowermost node N-
     * @param upperMostNode the uppermost node N+
     * @param vertexNodeMap the vertex to tree node map for quick access
     */
    static <V> void addVertexToNode(V vertex, MPQTreeNode<V> currentNode, MPQTreeNode<V> lowerMostNode, MPQTreeNode<V> upperMostNode, HashMap<V, Set<MPQTreeNode<V>>> vertexNodeMap) {
        if (currentNode.getClass() == Leaf.class) {
            Leaf<V> leaf = (Leaf<V>) currentNode;
            if (leaf == lowerMostNode) {
                if (lowerMostNode == upperMostNode) {
                    templateL1(vertex, leaf, vertexNodeMap);
                } else {
                    templateL2(vertex, leaf, vertexNodeMap);
                }
            }
        } else if (currentNode.getClass() == PNode.class) {
            PNode<V> pNode = (PNode<V>) currentNode;
            if (pNode == lowerMostNode) {
                if (lowerMostNode == upperMostNode) {
                    templateP1(vertex, pNode, vertexNodeMap);
                } else {
                    templateP2(vertex, pNode, vertexNodeMap);
                }
            } else {
                templateP3(pNode, vertexNodeMap);
            }
        } else if (currentNode.getClass() == QNode.class) {
            QNode<V> qNode = (QNode<V>) currentNode;
            if (qNode == lowerMostNode) {
                if (allSectionsContainsElementSet(qNode, currentNode.getSetA())) {
                    templateQ1(vertex, qNode);
                } else {
                    templateQ2(vertex, qNode);
                }
            } else {
                // N != N_, Template Q3
                // TODO: incomplete template operation
            }
        }
    }

    /**
     * Q-node template for the case N == N_ and A in V_m
     *
     * @param vertex the vertex to be added
     * @param qNode  the Q-node
     * @param <V>    the concrete type of the vertex
     */
    private static <V> void templateQ1(V vertex, QNode<V> qNode) {
        // TODO: incomplete template operation
    }

    /**
     * Q-node template for the case N == N_ and A not in V_m
     *
     * @param vertex the vertex to be added
     * @param qNode  the Q-node
     * @param <V>    the concrete type of the vertex
     */
    private static <V> void templateQ2(V vertex, QNode<V> qNode) {
        // TODO: incomplete template operation
    }

    /**
     * P-node template for the case N == N_ == N+
     *
     * @param <V>           the concrete type of the vertex
     * @param vertex        the vertex to be added
     * @param pNode         the P-node
     * @param vertexNodeMap the vertex to tree node map for quick access
     */
    private static <V> void templateP1(V vertex, PNode<V> pNode, HashMap<V, Set<MPQTreeNode<V>>> vertexNodeMap) {
        Leaf<V> leaf = new Leaf<>(vertex);
        leaf.setParent(pNode);
        if (pNode.getSetB().isEmpty()) {
            pNode.addChild(leaf);
        } else {
            // TODO: incomplete template operation
            PNode<V> newPNode = new PNode<>(pNode.getSetB());
        }
    }

    /**
     * P-node template for the case N == N_ != N+
     *
     * @param <V>           the concrete type of the vertex
     * @param vertex        the vertex to be added
     * @param pNode         the P-node
     * @param vertexNodeMap the vertex to tree node map for quick access
     */
    private static <V> void templateP2(V vertex, PNode<V> pNode, HashMap<V, Set<MPQTreeNode<V>>> vertexNodeMap) {
        // compose a new Q node
        QNode<V> qNode = new QNode<>();
        MPQTreeNode<V> parent = pNode.getParent();
        qNode.setParent(parent);

        // if the parent node is a P node, add the new Q node as a child
        if (parent.getClass() == PNode.class) {
            PNode<V> parentPNode = (PNode<V>) parent;
            parentPNode.addChild(qNode);
        }

        // compose leaf and link to the left section
        QSectionNode<V> sectionNode1 = new QSectionNode<>(pNode.getSetA());
        Leaf<V> leaf1 = new Leaf<>(vertex);
        leaf1.setParent(sectionNode1);
        sectionNode1.setChild(leaf1);
        qNode.setLeftmostSection(sectionNode1);

        // compose P-node and link to the right section
        QSectionNode<V> sectionNode2 = new QSectionNode<>(pNode.getSetA());
        pNode.setParent(sectionNode2);
        sectionNode2.setChild(pNode);
        qNode.setRightmostSection(sectionNode2);

        // link the left section and the right section
        sectionNode1.setRightSibling(sectionNode2);
        sectionNode2.setLeftSibling(sectionNode1);
    }

    /**
     * P-node template for the case N != N_
     *
     * @param <V>           the concrete type of the vertex
     * @param pNode         the P-node
     * @param vertexNodeMap the vertex to tree node map for quick access
     */
    private static <V> void templateP3(PNode<V> pNode, HashMap<V, Set<MPQTreeNode<V>>> vertexNodeMap) {
        MPQTreeNode<V> currentChild = pNode.getCurrentElement();
        QNode<V> qNodeChild = (QNode<V>) currentChild;
        qNodeChild.setParent(pNode.getParent());

        // update the leftmost section
        QSectionNode<V> leftmostSection = qNodeChild.getLeftmostSection();
        leftmostSection.getSetB().addAll(pNode.getSetA());
        leftmostSection.getSetB().forEach(element -> addToVertexNodeMap(element, leftmostSection, vertexNodeMap));

        // update the rightmost section
        QSectionNode<V> currentRightmostSection = qNodeChild.getRightmostSection();
        QSectionNode<V> newRightmostSection = new QSectionNode<>(pNode.getSetA());
        newRightmostSection.getSetB().addAll(pNode.getSetB());
        newRightmostSection.getSetB().forEach(element -> addToVertexNodeMap(element, newRightmostSection, vertexNodeMap));

        // update the pointers
        currentRightmostSection.setRightSibling(newRightmostSection);
        newRightmostSection.setLeftSibling(currentRightmostSection);
        newRightmostSection.setParent(qNodeChild);
        newRightmostSection.setChild(pNode);
        pNode.setParent(currentRightmostSection);
        qNodeChild.setRightmostSection(newRightmostSection);

        // update the Q section nodes between
        QSectionNode<V> currentSection = leftmostSection.getRightSibling();
        while (currentSection != newRightmostSection) {
            currentSection.getSetB().addAll(pNode.getSetA());
            currentSection.getSetB().addAll(pNode.getSetB());
            for (V element : currentSection.getSetB()) {
                addToVertexNodeMap(element, currentSection, vertexNodeMap);
            }
            currentSection = currentSection.getRightSibling();
        }

        // update the P node
        pNode.getSetA().clear();
        pNode.getSetB().forEach(element -> removeFromVertexNodeMap(element, pNode, vertexNodeMap));
        pNode.getSetB().clear();
    }

    /**
     * Leaf template for the case N == N_ == N+
     *
     * @param <V>           the concrete type of the vertex
     * @param vertex        the vertex to be added
     * @param leaf          the leaf node
     * @param vertexNodeMap the vertex to tree node map for quick access
     */
    private static <V> void templateL1(V vertex, Leaf<V> leaf, HashMap<V, Set<MPQTreeNode<V>>> vertexNodeMap) {
        if (leaf.getSetB().isEmpty()) {
            leaf.moveSetAToSetB();
            leaf.addToSetB(vertex);
            leaf.getSetB().forEach(element -> addToVertexNodeMap(element, leaf, vertexNodeMap));
        } else {
            // compose a new P node
            PNode<V> pNode = new PNode<>(leaf.getSetA());
            pNode.getSetB().forEach(element -> addToVertexNodeMap(element, pNode, vertexNodeMap));
            pNode.setParent(leaf.getParent());

            // compose a new leaf with the vertex
            Leaf<V> leaf1 = new Leaf<>(vertex);
            addToVertexNodeMap(vertex, leaf1, vertexNodeMap);
            leaf1.setParent(pNode);
            pNode.addChild(leaf1);

            // link the old leaf containing setB with the new P node
            leaf.getSetA().forEach(element -> removeFromVertexNodeMap(element, leaf, vertexNodeMap));
            leaf.getSetB().forEach(element -> addToVertexNodeMap(element, leaf, vertexNodeMap));
            leaf.setParent(pNode);
            pNode.addChild(leaf);
        }
    }

    /**
     * Leaf template for the case N == N_ != N+
     *
     * @param <V>           the concrete type of the vertex
     * @param vertex        the vertex to be added
     * @param leaf          the leaf node
     * @param vertexNodeMap the vertex to tree node map for quick access
     */
    private static <V> void templateL2(V vertex, Leaf<V> leaf, HashMap<V, Set<MPQTreeNode<V>>> vertexNodeMap) {
        // compose a new Q node
        QNode<V> qNode = new QNode<>();
        MPQTreeNode<V> parent = leaf.getParent();
        qNode.setParent(parent);

        // if the parent node is a P node, add the new Q node as a child
        if (parent.getClass() == PNode.class) {
            PNode<V> parentPNode = (PNode<V>) parent;
            parentPNode.addChild(qNode);
        }

        // compose a new leaf and link to the left section
        QSectionNode<V> sectionNode1 = new QSectionNode<>(leaf.getSetA());
        sectionNode1.setParent(qNode);
        sectionNode1.getSetB().forEach(element -> addToVertexNodeMap(element, sectionNode1, vertexNodeMap));
        Leaf<V> leaf1 = new Leaf<>(vertex);
        addToVertexNodeMap(vertex, leaf1, vertexNodeMap);
        leaf1.setParent(sectionNode1);
        sectionNode1.setChild(leaf1);
        qNode.setLeftmostSection(sectionNode1);

        // compose a new leaf and link to the right section
        QSectionNode<V> sectionNode2 = new QSectionNode<>(leaf.getSetA());
        sectionNode2.setParent(qNode);
        sectionNode2.getSetB().forEach(element -> addToVertexNodeMap(element, sectionNode2, vertexNodeMap));
        Leaf<V> leaf2 = new Leaf<>(leaf.getSetB());
        leaf.getSetA().forEach(element -> removeFromVertexNodeMap(element, leaf, vertexNodeMap));
        leaf2.getSetB().forEach(element -> addToVertexNodeMap(element, leaf2, vertexNodeMap));
        leaf2.setParent(sectionNode2);
        sectionNode2.setChild(leaf2);
        qNode.setRightmostSection(sectionNode2);

        // link the left section and the right section
        sectionNode1.setRightSibling(sectionNode2);
        sectionNode2.setLeftSibling(sectionNode1);
    }

    /**
     * Add the vertex / tree node entry to the map
     *
     * @param vertex        the vertex as key in the map
     * @param treeNode      the tree node to be added
     * @param vertexNodeMap the vertex / tree node map
     * @param <V>           the concrete type of the vertex
     */
    static <V> void addToVertexNodeMap(V vertex, MPQTreeNode<V> treeNode, HashMap<V, Set<MPQTreeNode<V>>> vertexNodeMap) {
        if (!vertexNodeMap.containsKey(vertex)) {
            vertexNodeMap.put(vertex, new HashSet<>());
        }
        vertexNodeMap.get(vertex).add(treeNode);
    }

    /**
     * Remove the vertex / tree node entry from the map
     *
     * @param vertex        the vertex as key in the map
     * @param treeNode      the tree node to be removed
     * @param vertexNodeMap the vertex / tree node map
     * @param <V>           the concrete type of the vertex
     */
    static <V> void removeFromVertexNodeMap(V vertex, MPQTreeNode<V> treeNode, HashMap<V, Set<MPQTreeNode<V>>> vertexNodeMap) {
        if (vertexNodeMap.containsKey(vertex)) {
            vertexNodeMap.get(vertex).remove(treeNode);
        }
    }

    /**
     * Adds the vertex u to the leaf of the path
     *
     * @param <V>
     * @param <E>
     * @param u     the vertex to be added
     * @param path  the path of the leaf
     * @param graph the interval graph
     * @return adds the vertex u at the end of the path after making changes according to the template
     */
//    public static <V, E> void addVertexToLeaf(V u, List<MPQTreeNode> path, Graph graph) {
//        int lastIndexofPath = path.size() - 1;
//        MPQTreeNode lastNodeInPath = path.get(lastIndexofPath);
//        HashMap<Integer, HashSet<V>> partitionedVertexSet = partitionVertexSet(u, graph, lastNodeInPath);
//
//        //Addition if the last node in path is the PNode
//        else if (lastNodeInPath.getClass() == PNode.class) {
//
//            PNode tempPNode = (PNode) lastNodeInPath;
//            if (partitionedVertexSet.get(1).isEmpty()) {
//                //swap P node containing A+B with P Node containing just A,since B is empty makes no change
//                //keep it the same, just add the new leaves there
//                Leaf newLeaf = new Leaf(u);
//                tempPNode.addCurrentChild(newLeaf);
//
//                path.remove(lastIndexofPath);
//                path.add(tempPNode);
//
//            } else {
//                //update the previous PNode elements with elements in A
//
//                PNode newPNodeA = new PNode(partitionedVertexSet.get(0));
//                //create a new PNode and add the B set
//
//                PNode newPNodeB = tempPNode;
//                newPNodeB.elements.removeAll(partitionedVertexSet.get(0));
//                newPNodeA.addChild(newPNodeB);
//
//                Leaf newChildLeaf = new Leaf(u);
//                newPNodeA.addCurrentChild(newChildLeaf);
//
//                path.remove(lastIndexofPath);
//                path.add(newPNodeA);
//
//            }
//
//        }
//
//
//        //last node is a Qnode
//
//        else if (lastNodeInPath.getClass() == QNode.class) {
//
//
//            //test if all sections contains A or not
//
//            QNode currentQNode = (QNode) lastNodeInPath;
//            boolean containsA = allSectionsContainsElementSet(currentQNode, partitionedVertexSet.get(0));
//            //if containsA == true Q1, else Q2 (both the cases of Q2)
//
//
//            if (containsA) {
//                //Q1
//
//                //create a new PNode A
//                PNode newElementPNode = new PNode(partitionedVertexSet.get(0));
//
//                Leaf newLeaf = new Leaf(u);
//                newElementPNode.addChild(newLeaf);
//                newElementPNode.addChild(newLeaf);
//
//
//                QNode newChildQNode = extractQNodeElements(currentQNode, partitionedVertexSet.get(0));
//                newElementPNode.addChild(newChildQNode);
//
//
//                path.remove(currentQNode);
//                path.add(newElementPNode);
//
//            } else {
//
//                //TODO: implement the helper functions
//
//                //check if B is null
//
//                if (partitionedVertexSet.get(1).isEmpty()) {
//
//                    PNode newChildPNode = new PNode(null);
//
//                    //take the existing childsubtree of the QNode and add it to the PNode
//                    newChildPNode.addChild(currentQNode.leftmostSection.child);
//
//
//                    Leaf newChildLeaf = new Leaf(u);
//                    newChildPNode.addCurrentChild(newChildLeaf);
//
//
//                    currentQNode.leftmostSection.addChild(newChildPNode);
//
//                } else {
//
//                    //create a new QSection
//
//                    QSectionNode newQSectionNode = new QSectionNode(partitionedVertexSet.get(0));
//                    newQSectionNode.rightSibling = currentQNode.leftmostSection;
//                    currentQNode.leftmostSection.leftSibling = newQSectionNode;
//                    currentQNode.leftmostSection = newQSectionNode;
//
//                    Leaf newChildLeaf = new Leaf(u);
//                    currentQNode.leftmostSection.addChild(newChildLeaf);
//
//                }
//
//            }
//
//        }
//
//    }

    /**
     * Removes the given elementset from each section of the given qNode
     *
     * @param qNode      the given qNode
     * @param elementSet the elements to be removed from all sections of the qNode
     * @return the qNode after removing the elementset from each section's elements
     */

//    public static <V> QNode extractQNodeElements(QNode qNode, HashSet<V> elementSet) {
//
//        for (QSectionNode curerntQSectionNode = qNode.leftmostSection; curerntQSectionNode.rightSibling != null; curerntQSectionNode = curerntQSectionNode.rightSibling) {
//            curerntQSectionNode.elements.removeAll(elementSet);
//        }
//
//
//        return qNode;
//    }


    /**
     * Checks if all sections of the qNode contains the element Set
     *
     * @param newQNode the given qNode
     * @param elements the elements to be contained all sections of the qNode
     * @return true if all sections contain the elementSet, else false
     */

    public static <V> boolean allSectionsContainsElementSet(QNode newQNode, HashSet<V> elements) {
        //traverse all sections of the QNode and check if every section contains an A
        //should there be a section identifier

        QSectionNode leftSection = newQNode.getLeftmostSection();
        QSectionNode rightSection = newQNode.getRightmostSection();
        boolean isContained = false;

        while (leftSection != rightSection) {
            if (leftSection.getSetB().contains(elements) && rightSection.getSetB().contains(elements)) {
                leftSection = leftSection.getRightSibling();
                rightSection = rightSection.getLeftSibling();
                isContained = true;
            } else {
                isContained = false;
            }
        }

        return isContained;
    }

    /**
     * Checks the path for specific patterns and changes every node accordingly
     *
     * @param <V>
     * @param <E>
     * @param u      the vertex to add to the tree
     * @param path   the path of vertices to be changed
     * @param nSmall the smalles positive node in path
     * @param nBig   the highest non-empty, non-inf node in path
     */
//    public static <V, E> void changedPathToTemplates(V u, List<MPQTreeNode> path, MPQTreeNode nSmall, MPQTreeNode nBig, Graph graph) {
//        //traverse from nSmall to nBig
//        int minIndex = path.indexOf(nSmall);
//        int currentIndex = path.indexOf(nSmall);
//        int maxIndex = path.indexOf(nBig);
//        //   while(currentIndex != maxIndex )
//
//        for (int i = currentIndex; i <= maxIndex; i++) {
//
//            MPQTreeNode currentNode = path.get(currentIndex);
//            //split the vertex initially
//            HashMap<Integer, HashSet<V>> partitionedVertexSet = partitionVertexSet(u, graph, currentNode);
//
//
//            //CHECK the type of node
//            if (currentNode.getClass() == Leaf.class) {
//
//
//                if (currentIndex == minIndex) {
//
//                    //create a Qnode with 2 section nodes
//
//                    QSectionNode newQSection1 = new QSectionNode(partitionedVertexSet.get(0));
//                    QSectionNode newQSection2 = new QSectionNode(partitionedVertexSet.get(0));
//
//                    QNode newElementQNode = new QNode(newQSection1);
//                    //     newElementQNode.addSection(newQSection2);
//                    newElementQNode.leftmostSection = newQSection1;
//                    newElementQNode.rightmostSection = newQSection2;
//
//                    newQSection1.rightSibling = newQSection2;
//                    newQSection1.parent = newElementQNode;
//
//                    newQSection2.leftSibling = newQSection1;
//
//
//                    Leaf leftSectionChildLeaf = new Leaf(u);
//                    Leaf righSectiontChildLeaf = new Leaf(partitionedVertexSet.get(1));
//
//                    newQSection1.addChild(leftSectionChildLeaf);
//
//                    newQSection2.addChild(righSectiontChildLeaf);
//
//
//                    path.remove(currentIndex);
//                    path.add(newElementQNode);
//
//                }
//
//            } else if (path.get(currentIndex).getClass() == PNode.class) {
//                PNode tempPNode = (PNode) path.get(currentIndex);
//
//
//                if (currentIndex == minIndex) {
//
//                    QSectionNode newQSectionNode1 = new QSectionNode(partitionedVertexSet.get(0));
//                    QSectionNode newQSectionNode2 = new QSectionNode(partitionedVertexSet.get(0));
//
//                    QNode newQNode = new QNode(newQSectionNode1);
//                    //   newQNode.addSection(newQSectionNode2);
//
//                    newQNode.leftmostSection = newQSectionNode1;
//
//                    newQNode.rightmostSection = newQSectionNode2;
//
//                    newQSectionNode1.rightSibling = newQSectionNode2;
//                    newQSectionNode2.leftSibling = newQSectionNode1;
//
//                    Leaf leftChildLeaf = new Leaf(u);
//
//                    PNode rightChildPNode = tempPNode;
//                    rightChildPNode.elements.remove(partitionedVertexSet.get(0));
//
//                    newQSectionNode1.addChild(leftChildLeaf);
//
//                    newQSectionNode2.addChild(rightChildPNode);
//
//
//                    path.remove(currentIndex);
//                    path.add(newQNode);
//
//                } else {
//                    //TODO: implement helper classes here
//                    QSectionNode rightMostQSection = new QSectionNode(tempPNode.elements);
//                    PNode rightmostQSectionChildPNode = tempPNode;
//                    rightmostQSectionChildPNode.elements.removeAll(tempPNode.elements);
//                    rightMostQSection.addChild(rightmostQSectionChildPNode);
//
//                    QNode tempQNode = (QNode) tempPNode.currentChild;
//                    QNode newElementQNode = tempQNode;
//
//
//                    for (QSectionNode currentSection = newElementQNode.leftmostSection; currentSection.rightSibling != null; currentSection = currentSection.rightSibling) {
//
//                        if (currentSection == newElementQNode.leftmostSection) {
//
//
//                            currentSection.elements.addAll(partitionedVertexSet.get(0));
//
//                        } else {
//
//                            currentSection.elements.addAll(tempPNode.elements);
//
//                        }
//                    }
//
//
//                    //need to add addSection for QNode here
//
//                    newElementQNode.rightmostSection.rightSibling = rightMostQSection;
//                    rightMostQSection.leftSibling = newElementQNode.rightmostSection;
//                    newElementQNode.rightmostSection = rightMostQSection;
//
//
//                    path.remove(tempPNode);
//                    path.add(newElementQNode);
//
//                }
//
//            } else if (path.get(currentIndex).getClass() == QNode.class) {
//
//                QNode tempQNode = (QNode) path.get(currentIndex);
//
//                //first check:current node = NSmall, second check A is present in everything
//
//
//                if (currentIndex == minIndex) {
//
//                    //second check A is present in everything
//
//                    if (allSectionsContainsElementSet(tempQNode, partitionedVertexSet.get(0))) {
//                        //create Qnode1
//
//                        QSectionNode rightMostSection = new QSectionNode(partitionedVertexSet.get(0));
//                        QSectionNode leftMostSection = new QSectionNode(partitionedVertexSet.get(0));
//
//                        QNode newQNodeA = new QNode(leftMostSection);
//                        //need to add rightmost section in newQNodeA
//                        newQNodeA.leftmostSection = leftMostSection;
//                        newQNodeA.rightmostSection = rightMostSection;
//
//                        leftMostSection.rightSibling = rightMostSection;
//                        rightMostSection.leftSibling = leftMostSection;
//
//                        Leaf newChild = new Leaf(u);
//
//                        leftMostSection.addChild(newChild);
//
//
//                        //create QNode2
//
//
//                        QNode newQNodeB = tempQNode;
//
//
//                        for (QSectionNode currentSection = newQNodeB.leftmostSection; currentSection.rightSibling != null; currentSection = currentSection.rightSibling) {
//
//
//                            currentSection.elements.removeAll(partitionedVertexSet.get(0));
//
//                        }
//
//                        rightMostSection.addChild(newQNodeB);
//
//
//                        path.remove(tempQNode);
//                        path.add(newQNodeA);
//
//                    } else {
//
//
//                        QSectionNode qSectionNode = new QSectionNode(partitionedVertexSet.get(0));
//                        tempQNode.leftmostSection.leftSibling = qSectionNode;
//                        qSectionNode.rightSibling = tempQNode.leftmostSection;
//                        tempQNode.leftmostSection = qSectionNode;
//
//                        Leaf newChild = new Leaf(u);
//                        tempQNode.addChild(newChild);
//
//                    }
//
//                } else {
//
//                    QNode tempChildQNode = (QNode) tempQNode.leftmostSection.child;
//
//                    for (QSectionNode currentSection = tempChildQNode.leftmostSection; currentSection.rightSibling != null; currentSection = currentSection.rightSibling) {
//                        if (currentSection == tempChildQNode.leftmostSection) {
//
//                            currentSection.elements.addAll(partitionedVertexSet.get(0));
//                        } else {
//                            currentSection.elements.addAll(tempQNode.leftmostSection.elements);
//                        }
//
//                        tempChildQNode.rightmostSection.rightSibling = tempQNode.leftmostSection.rightSibling;
//                        tempQNode.leftmostSection = tempChildQNode.leftmostSection;
//
//                    }
//
//                }
//
//            }
//
//        }
//
//    }

}
