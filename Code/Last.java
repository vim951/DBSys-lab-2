package bufmgr;

/**
 * Class used to store the last access of ONE page p;
 * it contains the times of the most recent reference to page p.
 *
 * This class allows the algorithm to respect principles of delegation and encapsulation.
 * Therefore, we could easily upgrade to other data structures as suggested in the paper.
 */
public class Last {

    /**
     * Stores the id of the page related to the Last object.
     */
    private final int pageId;

    /**
     * Core of the object
     */
    private long values;

    /**
     * Constructor
     * @param pageId id of the page related to the Last object
     */
    public Last(int pageId) {
        this.pageId = pageId;
    }

    /**
     * Getter
     * @return pageId
     */
    public int getPageId() {
        return pageId;
    }

    /**
     * Getter
     * @return values
     */
    public long getValues() {
        return values;
    }

    /**
     * Setter
     * @param values to store
     */
    public void setValues(long values) {
        this.values = values;
    }
}
