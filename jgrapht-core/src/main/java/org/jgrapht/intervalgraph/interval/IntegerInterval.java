package org.jgrapht.intervalgraph.interval;

/**
 * An implementation of integer intervals
 */
public class IntegerInterval extends Interval<Integer> {

    /**
     * Constructs a new instance of the class
     * @param start The start point of the interval
     * @param end The end point of the interval
     */
    public IntegerInterval(int start, int end) {
        super(start, end);
    }

    /**
     * Gets the length of the interval
     * @return The length of the interval
     */
    public int length() {
        return getEnd() - getStart();
    }

    /**
     * Returns a string representation for the interval
     * @return A string representation for the interval
     */
    @Override
    public String toString() {
        return "[" + getStart() + ", " + getEnd() + "]";
    }
}
