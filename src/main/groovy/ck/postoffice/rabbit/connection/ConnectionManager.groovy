package ck.postoffice.rabbit.connection

import ck.postoffice.rabbit.Postmaster
import ck.postoffice.rabbit.PostmasterConfig
import ck.postoffice.rabbit.util.gate.GateCollection
import ck.postoffice.rabbit.util.roundRobin.RoundRobin
import com.rabbitmq.client.ConnectionFactory

import java.util.concurrent.ExecutorService

class ConnectionManager {

    /**
     * Postmaster
     */
    Postmaster _postmaster = null


    /**
     * Convenience method to return the Postmaster config
     * @return
     */
    private PostmasterConfig getConfig() {
        return this._postmaster.config
    }


    /**
     * Executor service
     */
    private ExecutorService getExecutorService() {
        return this._postmaster.executorService
    }


    /**
     * Returns the gate collection
     * @return
     */
    GateCollection getGateCollection() {
        return this._postmaster.gateCollection
    }




    /**
     * Returns the rabbit server addresses
     * @return
     */
    List<String> getHostAddresses() {
        return this._postmaster.config.rabbitHostNames
    }



    /**
     * Gate collection
     */
    private def _hostIndexRotatorGateKey = "HostIndexRotator"



    /**
     * Stores the host index rotator
     */
    private RoundRobin _hostIndexRotator = null

    /**
     * Returns the host index rotator
     * @return
     */
    private RoundRobin getHostIndexRotator() {
        // if no rotator is defined
        if (!this._hostIndexRotator) {
            // lock and try again
            def gate = this.gateCollection.gateForKey(this._hostIndexRotatorGateKey)

            gate.acquire()

            // if there's still no rotator, create one
            if (!this._hostIndexRotator) {
                this._hostIndexRotator = new RoundRobin(0, this.hostAddresses.size() - 1)
            }

            gate.release()
        }

        return this._hostIndexRotator
    }


    /**
     * Connection proxy gate key
     */
    private static _sharedConnectionProxyGateKey = "ConnectionManager:SharedConnectionProxyGate"
    private static _exclusiveConnectionProxyGateKey = "ConnectionManager:ExclusiveConnectionProxyGate"



    /**
     * Stores connection proxies
     */
    private List<ConnectionProxy> _sharedConnectionProxies = []


    /**
     * Exclusive connection proxies
     */
    private List<ConnectionProxy> _exclusiveConnectionProxies = []



    /**
     * Returns the connection proxies
     * @return
     */
    List<ConnectionProxy> getSharedConnectionProxies() {
        def gate = this.gateCollection.gateForKey(_sharedConnectionProxyGateKey)

        gate.acquire()

        def proxies = this._sharedConnectionProxies.collect { return it }

        gate.release()

        return proxies
    }


    /**
     * Registers a connection proxy
     * @param proxy
     */
    void registerSharedConnectionProxy(ConnectionProxy proxy) {
        def gate = this.gateCollection.gateForKey(_sharedConnectionProxyGateKey)

        gate.acquire()

        def proxies = this._sharedConnectionProxies.collect { return it }
        proxies.add(proxy)
        this._sharedConnectionProxies = proxies

        gate.release()
    }



    /**
     * Registers a connection proxy
     * @param proxy
     */
    void registerExclusiveConnectionProxy(ConnectionProxy proxy) {
        def gate = this.gateCollection.gateForKey(_exclusiveConnectionProxyGateKey)

        gate.acquire()

        def proxies = this._exclusiveConnectionProxies.collect { return it }
        proxies.add(proxy)
        this._exclusiveConnectionProxies = proxies

        gate.release()
    }



    /**
     * Returns the current connection proxy
     * @return
     */
    ConnectionProxy endpointSharedConnectionProxy() {
        ConnectionProxy proxy = null

        // if there's an existing proxy
        if (this._sharedConnectionProxies.size()) {
            // pull the last proxy
            def testProxy = this._sharedConnectionProxies.last()

            // if the last proxy doesn't have too many endpoints yet
            if (testProxy.endpointRequests.size() < this.config.maxEndpointsPerConnection) {
                proxy = testProxy
            }
        }

        // if there's no proxy, create one
        if (!proxy) {
            def connectionRequest = new ConnectionRequest()
            proxy = this.newSharedConnectionProxy(connectionRequest)
        }

        return proxy
    }





    /**
     * Constructor
     * @param postmaster
     * @param rabbitServerAddresses
     * @param rabbitUsername
     * @param rabbitPassword
     */
    ConnectionManager(Postmaster postmaster) {
        this._postmaster = postmaster
    }





    /**
     * Pulls the next rabbit server address
     * @return
     */
    private String nextHostAddress() {
        // pull the next host index
        def hostIndex = this.hostIndexRotator.nextIndex()

        // pull the appropriate host
        def host = this.hostAddresses[hostIndex]

        // return the host
        return host
    }



    /**
     * Builds and returns a rabbit connection factory
     */
    protected ConnectionFactory buildConnectionFactory() {
        def factory = new ConnectionFactory()

        factory.setUsername(this.config.rabbitUsername)
        factory.setPassword(this.config.rabbitPassword)
        factory.setHost(this.nextHostAddress())
        factory.setAutomaticRecoveryEnabled(false)
        factory.setTopologyRecoveryEnabled(false)

        return factory
    }





    /**
     * Creates a new connection proxy
     */
    ConnectionProxy newSharedConnectionProxy(ConnectionRequest request) {
        // create and register a new connection proxy
        def proxy = new ConnectionProxy(this._postmaster)
        this.registerSharedConnectionProxy(proxy)

        // open the connection with the request
        proxy.openConnection(request)

        return proxy
    }


    /**
     * Creates a new exclusive connection proxy
     * @param request
     * @return
     */
    ConnectionProxy newExclusiveConnectionProxy(ConnectionRequest request) {
        // create and register a new connection proxy
        def proxy = new ConnectionProxy(this._postmaster)
        this.registerExclusiveConnectionProxy(proxy)

        // open the connection with the request
        proxy.openConnection(request)

        return proxy
    }


}
