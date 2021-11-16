package ck.postoffice.rabbit.connection

import ck.postoffice.rabbit.Postmaster
import ck.postoffice.rabbit.endpoint.EndpointRequest
import com.rabbitmq.client.Connection

class ConnectionProxy {

    /**
     * Stores a reference to the connection manager
     */
    private Postmaster _postmaster = null


    /**
     * Stores the connection
     */
    private Connection _connection = null


    /**
     * Returns a reference to the connection
     * @return
     */
    Connection getConnection() {
        return this._connection
    }


    /**
     * Is the connection open?
     * @return
     */
    Boolean isConnectionOpen() {
        return this._connection?.isOpen()
    }


    /**
     * Stores the connection result
     */
    private ConnectionResult _connectionResult


    /**
     * Returns the connection result
     * @return
     */
    ConnectionResult getConnectionResult() {
        return this._connectionResult
    }


    /**
     * Returns the gate collection
     */
    private def _gateCollection = this._postmaster.gateCollection


    /**
     * Endpoint request gate key
     */
    static def _endpointRequestGateKey = "ConnectionProxy:EndpointRequestGate"



    /**
     * Stores endpoint requests for this connection
     */
    private List<EndpointRequest> _endpointRequests = []



    /**
     * Returns the endpoint requests for this connection
     */
    List<EndpointRequest> getEndpointRequests() {
        def gate = this._gateCollection.gateForKey(_endpointRequestGateKey)

        gate.acquire()

        def requests = this._endpointRequests.collect { return it }

        gate.release()

        return requests
    }







    /**
     * Constructor
     * @param connectionManager
     */
    ConnectionProxy(Postmaster postmaster) {
        this._postmaster = postmaster
    }





    /**
     * Opens a connection and performs some action
     */
    void openConnection(ConnectionRequest request) {
        def connectionManager = this._postmaster.connectionManager

        if (this._connection?.isOpen()) {
            // if the connection is open already, return the result
            this._connectionResult.onConnectionSuccess(this)

        } else {
            // the connection is closed, so open it
            Connection conn = null

            try {
                // pull a new connection factory
                def connFactory = connectionManager.buildConnectionFactory()

                // open a connection
                conn = connFactory.newConnection()

                if (!conn?.isOpen()) {
                    // if the connection isn't open, throw an error
                    def error = new Error("Unknown error while opening connection")
                    throw error

                } else {
                    // the connection is open, create a result to return
                    this._connectionResult = request.newConnectionResult(conn)

                    // store the connection here
                    this._connection = conn

                    // register any endpoints
                    this._endpointRequests.each { EndpointRequest indexEndpointRequest ->
                        this._postmaster.endpointManager.createEndpointOnConnectionProxy(this, indexEndpointRequest)
                    }

                    // call the connection success handler
                    this._connectionResult.onConnectionSuccess(this)
                }

            } catch (error) {
                // log the error in the connection request
                request.logConnectionError(error)

                // increment the retry count
                request.incrementRetryCount()

                if (request.currentRetryCount <= request.maxRetryCount) {
                    // if the max retry count hasn't been reached, pause and try again
                    if (request.retryPauseMS) {
                        Thread.sleep(request.retryPauseMS)
                    }

                    // run the connection attempt again
                    this.openConnection(request)

                } else {
                    // if the max retry count has been reached, return the failure
                    def result = request.newConnectionResult()

                    result.onConnectionFailure(result)
                }

            }
        }
    }


    /**
     * Adds an endpoint request
     * @param request
     */
    void addEndpointRequest(EndpointRequest request) {
        def gate = this._gateCollection.gateForKey(_endpointRequestGateKey)

        gate.acquire()

        def requests = this._endpointRequests.collect { return it }
        requests.add(request)
        this._endpointRequests = requests

        gate.release()

        // if the connection is open, register the endpoint
        if (this.isConnectionOpen()) {
            this._postmaster.endpointManager.createEndpointOnConnectionProxy(this, request)
        }
    }




}
