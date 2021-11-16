package ck.postoffice.rabbit.connection

import com.rabbitmq.client.Connection

class ConnectionRequest extends ConnectionBase {

    /**
     * Set the max retry count
     * @param maxRetryCount
     */
    void setMaxRetryCount(Integer maxRetryCount) {
        this._maxRetryCount = maxRetryCount
    }


    /**
     * Set the connection success handler
     * @param onConnectionSuccess
     */
    void setOnConnectionSuccess(Closure onConnectionSuccess) {
        this._onConnectionSuccess = onConnectionSuccess
    }


    /**
     * Set the connection failure handler
     * @param onConnectionFailure
     */
    void setConnectionFailure(Closure onConnectionFailure) {
        this._onConnectionFailure = onConnectionFailure
    }



    /**
     * Returns a connection result
     * @return
     */
    ConnectionResult newConnectionResult() {
        return this.newConnectionResult(null)
    }


    /**
     * Returns a connection result
     * @return
     */
    ConnectionResult newConnectionResult(Connection connection) {
        def result = new ConnectionResult()

        result._connection = connection
        result._retryPauseMS = this._retryPauseMS
        result._maxRetryCount = this._maxRetryCount
        result._currentRetryCount = this._currentRetryCount
        result._connectionErrors = this._connectionErrors.collect { return it }
        result._onConnectionSuccess = this._onConnectionSuccess
        result._onConnectionFailure = this._onConnectionFailure

        return result
    }




}
