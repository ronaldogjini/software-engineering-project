package filters;

import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * A filter that represents the logical OR of its two childs
 */
public class OrFilter implements Filter {
    private final Filter childOne;
    private final Filter childTwo;

    public OrFilter(Filter childOne, Filter childTwo) {
        this.childOne = childOne;
        this.childTwo = childTwo;
    }

    /**
     * An OR filter matches when any of the childs match
     * @param s     the tweet to check
     * @return      whether or not it matches
     */
    @Override
    public boolean matches(Status s) {
        return childOne.matches(s) || childTwo.matches(s);
    }

    @Override
    public List<String> terms() {
        List<String> termList = new ArrayList<>(2);
        termList.add(childOne.terms().get(0));
        termList.add(childTwo.terms().get(0));
        return termList;
    }

    public String toString() {
        return "(" + childOne.toString() + " or " + childTwo.toString() + ")";
    }
}
