package filters;

import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * A filter that represents the logical AND of its two childs
 */
public class AndFilter implements Filter {
    private final Filter childOne;
    private final Filter childTwo;

    public AndFilter(Filter childOne, Filter childTwo) {
        this.childOne = childOne;
        this.childTwo = childTwo;
    }

    /**
     * An AND filter matches when its both of the childs match
     * @param s     the tweet to check
     * @return      whether or not it matches
     */
    @Override
    public boolean matches(Status s) {
        return childOne.matches(s) && childTwo.matches(s);
    }

    @Override
    public List<String> terms() {
        List<String> termList = new ArrayList<>(2);
        termList.add(childOne.terms().get(0));
        termList.add(childTwo.terms().get(0));
        return termList;
    }

    public String toString() {
        return "(" + childOne.toString() + " and " + childTwo.toString() + ")";
    }
}
