package ck.postoffice.rabbit

import ck.postoffice.logging.LogManager
import ck.postoffice.rabbit.connection.RabbitConnectionManager
import ck.postoffice.rabbit.envelope.RabbitEnvelopeManager
import ck.postoffice.rabbit.listener.RabbitListenerManager
import ck.postoffice.rabbit.listener.RabbitListenerRequest
import ck.postoffice.rabbit.queue.RabbitQueueManager

class RabbitPostmaster {


    /**
     * Rabbit Postmaster Config
     */
    private RabbitPostmasterConfig _config = null


    /**
     * Rabbit Postmaster Config
     * @return
     */
    RabbitPostmasterConfig getConfig() {
        return this._config
    }



    /**
     * Indicates if the postmaster has been shut down
     */
    private Boolean _shutdownFlag = false



    /**
     * Where we're storing the connection manager
     */
    private RabbitConnectionManager _connectionManager = null


    /**
     * Returns a reference to the RabbitConnectionManager instance
     * @return
     */
    RabbitConnectionManager getConnectionManager() {
        return this._connectionManager
    }




    /**
     * Rabbit Listener manager instance
     */
    private RabbitQueueManager _queueManager = null



    /**
     * Rabbit Listener manager instance
     * @return
     */
    RabbitQueueManager getQueueManager() {
        return this._queueManager
    }


    /**
     * Rabbit Envelope Manager instance
     */
    private RabbitEnvelopeManager _envelopeManager = null


    /**
     * Rabbit Envelope Manager instance
     * @return
     */
    RabbitEnvelopeManager getEnvelopeManager() {
        return this._envelopeManager
    }


    /**
     * Log manager
     */
    private LogManager _logManager = null


    /**
     * Log manager
     * @return
     */
    LogManager getLogManager() {
        return this._logManager
    }






    /**
     * Constructor
     */
    RabbitPostmaster(RabbitPostmasterConfig config) {
        this._config = config
        this._logManager = new LogManager()
        this._connectionManager = new RabbitConnectionManager(this)
        this._queueManager = new RabbitQueueManager(this)
        this._envelopeManager = new RabbitEnvelopeManager(this)
    }







    /**
     * Opens a listener from a request
     * @param request
     */
    void openListener(RabbitListenerRequest request) {
        // if we're shut down, just cancel
        if (this._shutdownFlag) {
            return
        }

        // if we're not shut down, open the listener
        this.queueManager.listenOnQueue(request)
    }




}
