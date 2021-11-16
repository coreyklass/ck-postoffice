package ck.postoffice.rabbit

import ck.postoffice.logging.LogManager
import ck.postoffice.rabbit.connection.ConnectionManager
import ck.postoffice.rabbit.endpoint.EndpointManager
import ck.postoffice.rabbit.envelope.EnvelopeManager
import ck.postoffice.rabbit.queue.QueueManager
import ck.postoffice.rabbit.util.gate.GateCollection

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Postmaster {


    /**
     * Rabbit Postmaster Config
     */
    private PostmasterConfig _config = null


    /**
     * Rabbit Postmaster Config
     * @return
     */
    PostmasterConfig getConfig() {
        return this._config
    }



    /**
     * Indicates if the postmaster has been shut down
     */
    private Boolean _shutdownFlag = false


    /**
     * Executor service
     */
    private _executorService = Executors.newCachedThreadPool()


    /**
     * Returns the executor service
     * @return
     */
    ExecutorService getExecutorService() {
        return this._executorService
    }




    /**
     * Where we're storing the connection manager
     */
    private ConnectionManager _connectionManager = null


    /**
     * Returns a reference to the RabbitConnectionManager instance
     * @return
     */
    ConnectionManager getConnectionManager() {
        return this._connectionManager
    }




    /**
     * Rabbit Listener manager instance
     */
    private QueueManager _queueManager = null



    /**
     * Rabbit Listener manager instance
     * @return
     */
    QueueManager getQueueManager() {
        return this._queueManager
    }


    /**
     * Rabbit Envelope Manager instance
     */
    private EnvelopeManager _envelopeManager = null


    /**
     * Rabbit Envelope Manager instance
     * @return
     */
    EnvelopeManager getEnvelopeManager() {
        return this._envelopeManager
    }


    /**
     * Stores the endpoint manager
     */
    private EndpointManager _endpointManager


    /**
     * Returns the endpoint manager
     * @return
     */
    EndpointManager getEndpointManager() {
        return this._endpointManager
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
     * Stores the gate collection
     */
    private GateCollection _gateCollection = new GateCollection()




    /**
     * Returns the gate collection
     * @return
     */
    GateCollection getGateCollection() {
        return this._gateCollection
    }



    /**
     * Constructor
     */
    Postmaster(PostmasterConfig config) {
        this._config = config
        this._logManager = new LogManager()
        this._connectionManager = new ConnectionManager(this)
        this._endpointManager = new EndpointManager(this)
        this._queueManager = new QueueManager(this)
        this._envelopeManager = new EnvelopeManager(this)
    }



    void registerEndpoint() {

    }


}
