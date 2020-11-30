package bufmgr;

/**
 * Class used to store the history control block of ONE page p;
 * it contains the times of the K most recent references to page p,
 * discounting correlated references.
 *
 * This class allows the algorithm to respect principles of delegation and encapsulation.
 * Therefore, we could easily upgrade to other data structures as suggested in the paper.
 */
public class Historic {

    /**
     * Stores the id of the page related to the Historic object.
     */
    private final int pageId;

    /**
     * Number of references to keep in memory per page
     */
    private final int k;

    /**
     * Core of the object
     */
    private final long[] values;

    /**
     * Constructor
     * @param pageId id of the page related to the Historic object
     * @param k number of references to keep in memory per page
     */
    public Historic(int pageId, int k) {
        this.pageId = pageId;
        this.k = k;
        this.values = new long[this.k];
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
     * @return i-th value
     */
    public long getValue(int i){
        return values[i];
    }

    /**
     * Setter;
     * sets i-th value to value
     */
    public void setValue(int i, long value){
        values[i] = value;
    }
}