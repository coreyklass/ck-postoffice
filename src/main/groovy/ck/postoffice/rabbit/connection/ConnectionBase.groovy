package ck.postoffice.rabbit.connection

import ck.postoffice.rabbit.util.gate.Gate

class ConnectionBase {

    /**
     * Stores the connection attempt pause
     */
    protected Integer _retryPauseMS = 500


    /**
     * Returns the connection attempt pause, in MS
     * @return
     */
    Integer getRetryPauseMS() {
        return this._retryPauseMS
    }


    /**
     * Unlimited retry value
     */
    static Integer UnlimitedRetryCount = -1


    /**
     * Maximum number of retries
     */
    protected Integer _maxRetryCount = UnlimitedRetryCount

    /**
     * Returns the maximum number of retries
     * @return
     */
    Integer getMaxRetryCount() {
        return this._maxRetryCount
    }


    /**
     * Gate for retrying counter
     */
    def _retryCountGate = new Gate("RetryCount")



    /**
     * Stores the current retry count
     */
    protected Integer _currentRetryCount = 0


    /**
     * Returns the current retry count
     * @return
     */
    Integer getCurrentRetryCount() {
        this._retryCountGate.acquire()

        def currentRetryCount = this._currentRetryCount

        this._retryCountGate.release()

        return currentRetryCount
    }


    /**
     * Increments the retry counter and returns it
     * @return
     */
    protected Integer incrementRetryCount() {
        this._retryCountGate.acquire()

        def newRetryCount = this._currentRetryCount + 1
        this._currentRetryCount = newRetryCount

        this._retryCountGate.release()

        return newRetryCount
    }


    /**
     * Gate for blocking connection errors
     */
    protected def _connectionErrorGate = new Gate("ConnectionErrorGate")



    /**
     * Stores the connection errors
     */
    protected List<Exception> _connectionErrors = []


    /**
     * Returns connection errors
     * @return
     */
    List<Exception> getConnectionErrors() {
        this._connectionErrorGate.acquire()

        def errors = this._connectionErrors.collect { return it }

        this._connectionErrorGate.release()

        return errors
    }


    /**
     * Logs a connection error
     * @param error
     */
    void logConnectionError(Exception error) {
        this._connectionErrorGate.acquire()

        this._connectionErrors.add(error)

        this._connectionErrorGate.release()
    }







    /**
     * Executed on connection success
     */
    protected Closure _onConnectionSuccess


    /**
     * Returns the connection success handler
     * @return
     */
    Closure getOnConnectionSuccess() {
        return this._onConnectionSuccess
    }



    /**
     * Executed on connection failure
     */
    protected Closure _onConnectionFailure


    /**
     * Returns the connection failure handler
     * @return
     */
    Closure getOnConnectionFailure() {
        return this.onConnectionFailure
    }





    /**
     * Returns a cloned connection request
     * @return
     */
    ConnectionRequest cloneRequest() {
        def request = new ConnectionRequest()

        request._retryPauseMS = this._retryPauseMS
        request._maxRetryCount = this._maxRetryCount
        request._onConnectionSuccess = this._onConnectionSuccess
        request._onConnectionFailure = this._onConnectionFailure

        return request
    }






}
