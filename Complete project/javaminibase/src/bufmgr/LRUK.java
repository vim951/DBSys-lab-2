package bufmgr;

/**
* class LRUK is a subclass of class Replacer using LRUK
* algorithm for page replacement
*/
public class LRUK extends  Replacer {

    /**
    * An array to hold number of frames in the buffer pool
    */
    private int[] frames;
 
    /**
    * number of frames used
    */
    private int  nframes;

    private int k;

    /**
     * Stores the time of the most recent reference
     * to each page p, regardless of whether this is a
     * correlated reference or not.
     * @see LastDataStructure
     */
    private LastDataStructure last;

    /**
     * Stores the history control block of each page p; it
     * contains the times of the K most recent references to
     * page p, discounting correlated references.
     * @see HistoricsDataStructure
     */
    private HistoricsDataStructure historic;

    /**
     * Defines the duration of the correlated reference period
     * (in milliseconds)
     */
    private static final int CORRELATED_REFERENCE_PERIOD = 0;

    /**
    * Calling super class the same method
    * Initializing the frames[] with number of buffer allocated
    * by buffer manager
    * set number of frame used to zero
    *
    * @param mgr a BufMgr object
    * @see	BufMgr
    * @see	Replacer
    */
    public void setBufferManager( BufMgr mgr )
    {
        super.setBufferManager(mgr);
        frames = new int [mgr.getNumBuffers()];
        nframes = 0;
        historic = new HistoricsDataStructure(k);
        last = new LastDataStructure();
    }

    /**
    * Class constructor
    * Initializing frames[] pointer = null.
    */
    public LRUK(BufMgr mgrArg)
    {
        super(mgrArg);
        frames = null;
        last = null;
        historic = null;
        k = 3;
    }
  
    /**
    * Calling super class the same method
    * pin the page in the given frame number
    * move the page to the end of list
    *
    * @param frameNo the frame number to pin
    * @exception InvalidFrameNumberException
    */
    public void pin(int frameNo) throws InvalidFrameNumberException
    {
        super.pin(frameNo);
        update(frameNo);
    }

    /**
     * Updates the entries of a given page in 'historic' and 'last' data structures
     * @param p id of the page
     */
    private void updateHistory(int p){
        //Get current time
        long t = System.currentTimeMillis();
        //If last reference was more than CORRELATED_REFERENCE_PERIOD ms ago, we need to update the historic
        if(t - last.getValue(p) >= CORRELATED_REFERENCE_PERIOD){
            long correlPeriodOfRefdPage = last.getValue(p) - historic.getValue(p, 1);
            //historic values are updated
            for(int i=1 ; i<k ; i++){
                historic.setValue(p, i, historic.getValue(p, i-1) + correlPeriodOfRefdPage);
            }
            historic.setValue(p, 1, t);
            //Lastly, we update the last reference value
            last.setValue(p, t);
        //Otherwise, we just update the last reference value
        }else{
            last.setValue(p, t);
        }
    }

    /**
     * Depending of whether the page is already in buffer or not:
     * updates the entries of a given page in 'historic' and 'last' data structures
     * OR
     * Add a page to the buffer pool, and create the required entries in data structures
     * @param p id of the page
     * @throws InvalidFrameNumberException if all pages are pinned
     */
    private void update(int p) throws InvalidFrameNumberException {
        boolean pInBuffer = false;
        //Try to find page n° p in the buffer pool
        for(int frameNo: frames){
            if(frameNo==p){
                pInBuffer = true;
                break;
            }
        }
        //If page n° p is in buffer, update its historic and/or last reference value
        if(pInBuffer){
            updateHistory(p);
        //Otherwise, add it to the buffer pool, and create the required entries in data structures
        }else{
            fetchPageAndUpdate(p);
        }
    }

    /**
     * Add a page to the buffer pool, and create the required entries in data structures
     * @param p id of the page
     * @throws InvalidFrameNumberException if all pages are pinned
     */
    private void fetchPageAndUpdate(int p) throws InvalidFrameNumberException {
        long t = System.currentTimeMillis();
        int victim = -1;
        //If a page is unpinned, select it as victim
        for(int q: frames){
            if(state_bit[q].state != Pinned){
                victim=q;
                break;
            }
        }
        //If all pages are pinned, throw an InvalidFrameNumberException
        if(victim==-1){
            throw new InvalidFrameNumberException(null, "LRUK.fetchPageAndUpdate: InvalidFrameNumberException");
        }
        //Stores page n° p in the selected slot
        frames[victim]=p;
        //If page n° p has an entry in the 'historic' data structure, updates it
        if(historic.historicExists(p)){
            for(int i=1 ; i<k ; i++){
                historic.setValue(p, i, historic.getValue(p, i-1));
            }
        //Otherwise, create an entry
        }else{
            historic.addEntry(p);
            for(int i=1 ; i<k ; i++){
                historic.setValue(p, i, 0);
            }
        }
        historic.setValue(p, 1, t);
        last.setValue(p, t);

        //Pin the page
        state_bit[p].state = Pinned;
        (mgr.frameTable())[p].pin();
    }

    /**
     * Finding a free frame in the buffer pool or choosing a page to replace using LRU policy
     * @return 	return the frame number
     * @throws BufferPoolExceededException if failed
     */
    public int pick_victim() throws BufferPoolExceededException
    {
        return pick_victim(System.currentTimeMillis());
    }

    /**
     * Finding a free frame in the buffer pool or choosing a page to replace using LRU policy
     * @param t current time
     * @return 	return the frame number
     * @throws BufferPoolExceededException if failed
     */
    public int pick_victim(long t) throws BufferPoolExceededException
    {
        //If buffer is not full
        if (nframes < mgr.getNumBuffers()) {
            int frame = nframes++;
            frames[frame] = frame;
            state_bit[frame].state = Pinned;
            (mgr.frameTable())[frame].pin();
            updateHistory(frame);
            return frame;

        //If buffer is full
        }else{
            long min = t;
            int victim = -1;

            for (int q: frames){
                if (state_bit[q].state != Pinned && t - last.getValue(q) >= CORRELATED_REFERENCE_PERIOD && historic.getValue(q, k-1) <= min){
                    victim = q;
                    min = historic.getValue(q, k-1);
                }
            }

            if (victim == -1){
                throw new BufferPoolExceededException(null, "BUFMGR: BUFFER_EXCEEDED.");
            }
            
            state_bit[victim].state = Pinned;
            (mgr.frameTable())[victim].pin();

            return victim;
        }
    }
 
    /**
    * get the page replacement policy name
    * @return	return the name of replacement policy used
    */
    public String name() { return "LRUK"; }
 
    /**
    * print out the information of frame usage
    */
    public void info()
    {
        super.info();
        System.out.print( "LRUK REPLACEMENT");

        for (int i = 0; i < nframes; i++) {
        if (i % 5 == 0)
            System.out.println( );
        System.out.print( "\t" + frames[i]);
        }
        System.out.println();
    }

    /**
     * frames getter
     */
    public int[] getFrames() {
        return frames;
    }

    /**
     * last getter
     */
    public LastDataStructure getLast() {
        return last;
    }

    /**
     * historic getter
     */
    public HistoricsDataStructure getHistoric() {
        return historic;
    }
}
