package org.jgrapht.intervalgraph.interval;

public class IntegerInterval extends Interval<Integer> {
    public IntegerInterval(int start, int end) {
        super(start, end);
    }

    public int length() {
        return getEnd() - getStart();
    }
}
