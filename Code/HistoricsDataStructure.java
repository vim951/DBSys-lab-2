package bufmgr;

import java.util.ArrayList;

/**
 * Class used to store the history control block of EACH page p;
 * it contains the times of the K most recent references to page p,
 * discounting correlated references.
 *
 * This class allows the algorithm to respect principles of delegation and encapsulation.
 * Therefore, we could easily upgrade to other data structures as suggested in the paper.
 */
public class HistoricsDataStructure {

    /**
     * Number of references to keep in memory per page
     */
    private final int k;

    /**
     * Core of the data structures
     */
    private final ArrayList<Historic> historics;

    /**
     * Constructor
     * @param k number of references to keep in memory per page
     */
    public HistoricsDataStructure(int k) {
        this.k = k;
        this.historics = new ArrayList<>();
    }

    /**
     * Create an entry in the data structure for page n° pageId.
     */
    public void addEntry(int pageId){
        historics.add(new Historic(pageId, k));
    }

    /**
     * @return the i-th reference of page n° pageId.
     */
    public long getValue(int pageId, int i){
        for (Historic historic: historics){
            if(historic.getPageId()==pageId){
                return historic.getValue(i);
            }
        }
        return -1;
    }

    /**
     * Sets the i-th reference of page n° pageId to value.
     */
    public void setValue(int pageId, int i, long value){
        for (Historic historic: historics){
            if(historic.getPageId()==pageId){
                historic.setValue(i, value);
            }
        }
    }

    /**
     * Checks if page n° pageId has an entry in the data structure.
     */
    public boolean historicExists(int pageId){
        for (Historic historic: historics){
            if(historic.getPageId()==pageId){
                return true;
            }
        }
        return false;
    }
}
