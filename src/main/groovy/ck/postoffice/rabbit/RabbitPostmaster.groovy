package ck.postoffice.rabbit

import ck.postoffice.rabbit.connection.RabbitConnectionManager
import ck.postoffice.rabbit.listener.RabbitQueueManager
import ck.postoffice.rabbit.listener.RabbitListenerRequest

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
     * Constructor
     */
    RabbitPostmaster(RabbitPostmasterConfig config) {
        this._config = config
        this._connectionManager = new RabbitConnectionManager(this)
        this._queueManager = new RabbitQueueManager(this)
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
        this.queueManager.openListener(request)
    }




}
