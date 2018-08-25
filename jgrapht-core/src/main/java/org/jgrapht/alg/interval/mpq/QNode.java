package org.jgrapht.alg.interval.mpq;

/**
 * A Q-node of a modified PQ-tree
 *
 * @param <V> the element type of the Q-node
 * @author Jiong Fu (magnificent_tony)
 * @author Ira Justus Fesefeldt (PhoenixIra)
 */
public final class QNode<V> extends MPQTreeNode<V> {

    /**
     * The children of a Q-node are stored with a doubly linked list
     * <p>
     * Q-node has two pointers of the outermost sections as the entrances to this list
     */
    private QSectionNode<V> leftmostSection = null;
    private QSectionNode<V> rightmostSection = null;

    /**
     * Get the leftmost section of this Q-node
     *
     * @return the leftmost section of this Q-node
     */
    public QSectionNode<V> getLeftmostSection() {
        return leftmostSection;
    }

    /**
     * Set the leftmost section for this Q-node
     *
     * @param leftmostSection the leftmost section to be set for this Q-node
     */
    public void setLeftmostSection(QSectionNode<V> leftmostSection) {
        this.leftmostSection = leftmostSection;
    }

    /**
     * Get the rightmost section of this Q-node
     *
     * @return the rightmost section of this Q-node
     */
    public QSectionNode<V> getRightmostSection() {
        return rightmostSection;
    }

    /**
     * Set the rightmost section for this Q-node
     *
     * @param rightmostSection the rightmost section to be set for this Q-node
     */
    public void setRightmostSection(QSectionNode<V> rightmostSection) {
        this.rightmostSection = rightmostSection;
    }

}
