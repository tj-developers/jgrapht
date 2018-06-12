package org.jgrapht.intervalgraph.interval;

/**
 * Model of an integer interval in the interval graph
 */
public class IntegerInterval extends Interval<Integer> {

    /**
     * Construct an integer interval
     *
     * @param start interval start
     * @param end   interval end
     * @throws IllegalArgumentException if interval start or end is null, or if interval start is greater than interval end
     */
    public IntegerInterval(int start, int end) {
        super(start, end);
    }

    /**
     * Get the duration of the interval
     *
     * @return the duration of the interval
     */
    public int length() {
        return end - start;
    }

    @Override
    public String toString() {
        return "IntegerInterval[" + start + ", " + end + "]";
    }
}
