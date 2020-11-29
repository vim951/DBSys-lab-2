package bufmgr;

import java.util.ArrayList;

/**
 * Class used to store the last access of EACH page p;
 * it contains the times of the most recent reference to page p.
 *
 * This class allows the algorithm to respect principles of delegation and encapsulation.
 * Therefore, we could easily upgrade to other data structures as suggested in the paper.
 */
public class LastDataStructure {

    /**
     * Core of the data structures
     */
    private ArrayList<Last> lasts;

    /**
     * Constructor
     */
    public LastDataStructure() {
        this.lasts = new ArrayList<>();
    }

    /**
     * Getter
     * @param pageId n° of the page of which we want the last value
     * @return value
     */
    public long getValue(int pageId){
        for (Last last: lasts){
            if(last.getPageId()==pageId){
                return last.getValues();
            }
        }
        return -1;
    }

    /**
     * Setter
     * @param pageId n° of the page of which we want to set the last value
     * @param value to set
     */
    public void setValue(int pageId, long value){
        for (Last last: lasts){
            if(last.getPageId()==pageId){
                last.setValues(value);
                break;
            }
        }
    }
}
