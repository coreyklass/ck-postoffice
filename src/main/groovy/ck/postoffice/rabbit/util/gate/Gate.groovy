package ck.postoffice.rabbit.util.gate

import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class Gate extends Semaphore {

    private def _createDate = new Date()
    private String _gateKey = null

    private Semaphore _lastAccessDateGate = null
    private Date _lastAccessDate = null

    private Semaphore _acquisitionInProcessCountGate = null
    private Integer _acquisitionInProcessCount = 0

    private Integer _permitCount = null

    private Semaphore _inactivitySecondsGate = null
    Integer _inactivitySeconds = null


    /**
     * Default constructor
     */
    Gate(String gateKey) {
        super(1)
        this._gateKey = gateKey
        this._permitCount = 1
        this._createDate = new Date()
    }


    /**
     * Constructor with a custom permit count
     * @param accessCount
     */
    Gate(String gateKey, Integer accessCount) {
        super(accessCount)
        this._gateKey = gateKey
        this._permitCount = accessCount
        this._createDate = new Date()
    }


    /**
     * Returns the gate key
     * @return
     */
    String getGateKey() {
        return this._gateKey
    }


    /**
     * Retrieves the gate for the last access date
     * @return
     */
    private Semaphore _getLastAccessDateGate() {
        if (!this._lastAccessDateGate) {
            this._lastAccessDateGate = new Semaphore(1)
        }

        return this._lastAccessDateGate
    }


    /**
     * Updates the last access date
     */
    private void _setLastAccessDate() {
        this._getLastAccessDateGate().acquire()

        this._lastAccessDate = new Date()

        this._getLastAccessDateGate().release()
    }


    /**
     * Retrieves the last access date
     * @return
     */
    Date getLastAccessDate() {
        this._getLastAccessDateGate().acquire()

        def theDate = this._lastAccessDate

        this._getLastAccessDateGate().release()

        return theDate
    }


    /**
     * Returns the gate for the acquisitions in process count
     * @return
     */
    private Semaphore _getAcquisitionsInProcessCountGate() {
        if (!this._acquisitionInProcessCountGate) {
            this._acquisitionInProcessCountGate = new Semaphore(1)
        }

        return this._acquisitionInProcessCountGate
    }


    /**
     * Returns the acquisition count in process
     * @return
     */
    private Integer getAcquisitionInProcessCount() {
        this._getAcquisitionsInProcessCountGate().acquire()

        def theCount = this._acquisitionInProcessCount

        this._getAcquisitionsInProcessCountGate().release()

        return theCount
    }



    /**
     * Increments the acquisitions in process
     */
    private void _incrementAcquisitionsInProcess() {
        this._getAcquisitionsInProcessCountGate().acquire()

        this._acquisitionInProcessCount++

        this._getAcquisitionsInProcessCountGate().release()
    }


    /**
     * Decrement the applications in process
     */
    private void _decrementAcquisitionsInProcess() {
        this._getAcquisitionsInProcessCountGate().acquire()

        this._acquisitionInProcessCount--

        this._getAcquisitionsInProcessCountGate().release()
    }


    /**
     * Returns the gate for expiration in seconds
     * @return
     */
    private Semaphore _getInactivitySecondsGate() {
        if (!this._inactivitySecondsGate) {
            this._inactivitySecondsGate = new Semaphore(1)
        }

        return this._inactivitySecondsGate
    }


    /**
     * Returns the expiration in seconds
     * @return
     */
    Integer getInactivitySeconds() {
        this._getInactivitySecondsGate().acquire()

        def seconds = this._inactivitySeconds

        this._getInactivitySecondsGate().release()

        return seconds
    }


    /**
     * Sets the expiration in seconds
     * @param seconds
     */
    void setInactivitySeconds(Integer seconds) {
        this._getInactivitySecondsGate().acquire()

        this._inactivitySeconds = seconds

        this._getInactivitySecondsGate().release()
    }



    /**
     * Returns the total number of permits
     * @return
     */
    Integer totalPermits() {
        return this._permitCount
    }


    /**
     * Is this gate empty?
     * @return
     */
    Boolean isEmpty() {
        return (this.totalPermits() == this.availablePermits())
    }


    /**
     * Returns an expiration date
     * @return
     */
    Date inactivityDate() {
        Date inactivityDate = null

        // if an expiration date has been specified
        if (this.inactivitySeconds != null) {
            // return the inactivity date
            inactivityDate = new Date(this.lastAccessDate.getTime() + (this.inactivitySeconds * 1000))
        }

        return inactivityDate
    }


    /**
     * Is this gate expired?
     * @return
     */
    Boolean isInactive() {
        def inactiveFlag = false
        def inactivityDate = this.inactivityDate()

        if (inactivityDate) {
            inactiveFlag = inactivityDate.before(new Date())
        }

        return inactiveFlag
    }




    @Override
    void acquire() throws InterruptedException {
        this._setLastAccessDate()
        this._incrementAcquisitionsInProcess()

        super.acquire()

        this._decrementAcquisitionsInProcess()
    }


    @Override
    void acquire(int permits) throws InterruptedException {
        this._setLastAccessDate()
        this._incrementAcquisitionsInProcess()

        super.acquire(permits)

        this._decrementAcquisitionsInProcess()
    }



    @Override
    void acquireUninterruptibly() {
        this._setLastAccessDate()
        this._incrementAcquisitionsInProcess()

        super.acquireUninterruptibly()

        this._decrementAcquisitionsInProcess()
    }




    @Override
    void acquireUninterruptibly(int permits) {
        this._setLastAccessDate()
        this._incrementAcquisitionsInProcess()

        super.acquireUninterruptibly(permits)

        this._decrementAcquisitionsInProcess()
    }



    @Override
    boolean tryAcquire() {
        this._setLastAccessDate()

        this._incrementAcquisitionsInProcess()

        def flag = super.tryAcquire()

        this._decrementAcquisitionsInProcess()

        return flag
    }



    @Override
    boolean tryAcquire(int permits) {
        this._setLastAccessDate()

        this._incrementAcquisitionsInProcess()

        def flag = super.tryAcquire(permits)

        this._decrementAcquisitionsInProcess()

        return flag
    }



    @Override
    boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        this._setLastAccessDate()

        this._incrementAcquisitionsInProcess()

        def flag = super.tryAcquire(timeout, unit)

        this._decrementAcquisitionsInProcess()

        return flag
    }



    @Override
    boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
        this._setLastAccessDate()

        this._incrementAcquisitionsInProcess()

        def flag = super.tryAcquire(permits, timeout, unit)

        this._decrementAcquisitionsInProcess()

        return flag
    }



    @Override
    void release() {
        this._setLastAccessDate()

        super.release()
    }

    @Override
    void release(int permits) {
        this._setLastAccessDate()

        super.release(permits)
    }



}


