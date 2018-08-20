package org.jgrapht.alg.interval.mpq;

/**
 * A Q-node of a modified PQ-tree
 */
public final class QNode<V> extends MPQTreeNode<V> {

    /**
     * The children of a Q-node are stored with a doubly linked list
     * <p>
     * Q-node has two pointers of the outermost sections as the entrances to this list
     */
    private QSectionNode leftmostSection = null;
    private QSectionNode rightmostSection = null;

    /**
     * Instantiate a Q node associating with a set of graph vertices
     */
    public QNode() {
    }

    @Override
    public boolean hasAtMostOneSon() {
        // TODO: check the correctness of this comparison
        return leftmostSection == rightmostSection;
    }

    public QSectionNode getLeftmostSection() {
        return leftmostSection;
    }

    public void setLeftmostSection(QSectionNode leftmostSection) {
        this.leftmostSection = leftmostSection;
    }

    public QSectionNode getRightmostSection() {
        return rightmostSection;
    }

    public void setRightmostSection(QSectionNode rightmostSection) {
        this.rightmostSection = rightmostSection;
    }

}
