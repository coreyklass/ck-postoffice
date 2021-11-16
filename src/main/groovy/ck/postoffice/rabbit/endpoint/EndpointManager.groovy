package ck.postoffice.rabbit.endpoint

import ck.postoffice.rabbit.Postmaster
import ck.postoffice.rabbit.PostmasterConfig
import ck.postoffice.rabbit.connection.ConnectionManager
import ck.postoffice.rabbit.connection.ConnectionProxy
import ck.postoffice.rabbit.connection.ConnectionRequest
import ck.postoffice.rabbit.queue.GenericConsumer
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection

import java.util.concurrent.ExecutorService

class EndpointManager {


    /**
     * Rabbit Postmaster
     */
    private Postmaster _postmaster = null



    /**
     * Convenience method to return the Postmaster config
     * @return
     */
    PostmasterConfig getConfig() {
        return this._postmaster.config
    }



    /**
     * Shortcut to the connection manager
     * @return
     */
    ConnectionManager getConnectionManager() {
        return this._postmaster.connectionManager
    }



    /**
     * Response Queue Rabbit Listener Result
     */
    private EndpointResult _responseQueueRabbitListener = null


    /**
     * Response Queue Rabbit Listener Result
     * @return
     */
    EndpointResult getResponseQueueRabbitListener() {
        return this._responseQueueRabbitListener
    }



    /**
     * Name of the response queue
     * @return
     */
    String getResponseQueueName() {
        return this.responseQueueRabbitListener.endpointName
    }



    /**
     * Executor service
     */
    private ExecutorService getExecutorService() {
        return this._postmaster.executorService
    }







    /**
     * Constructor
     * @param rabbitConnectionManager
     */
    EndpointManager(Postmaster postmaster) {
        this._postmaster = postmaster
    }


    /**
     * Opens a response queue
     */
    private void _openResponseQueue() {
        // build the response queue name
        def serverName = this.config.serverName ?: "unknown-server"
        def applicationName = this.config.applicationName ?: "unknown-application"
        def uuid = UUID.randomUUID().toString().toLowerCase()

        def responseQueueName = "responseQueue/" + serverName + "/" + applicationName + "/" + uuid

        // create a listener request for the response queue
        def listenerRequest = new EndpointRequest()
        listenerRequest.exclusiveConnectionFlag = true
        listenerRequest.endpointName = responseQueueName
        listenerRequest.sendReplyFlag = false
        listenerRequest.autoDeleteNoConsumerFlag = true
        listenerRequest.allowReplicationFlag = false
        listenerRequest.autoDeleteOnServerRestartFlag = true
        listenerRequest.autoReconnectFlag = true
        listenerRequest.useAcknowledgementFlag = false
        listenerRequest.onCarrierOpenHandler = { EndpointResult listenerResult ->
            this._responseQueueRabbitListener = listenerResult
        }
        listenerRequest.envelopeReceivedHandler = {
        }
        listenerRequest.disconnectHandler = {
        }

        this.openListener(listenerRequest)
    }




    /**
     * Opens an endpoint from a request
     * @param request
     */
    void registerEndpoint(EndpointRequest request) {
        ConnectionProxy connectionProxy = null

        // if the request requires its own connection, create a new proxy
        if (request.exclusiveConnectionFlag) {
            def connectionRequest = new ConnectionRequest()
            connectionProxy = this.connectionManager.newExclusiveConnectionProxy(connectionRequest)

        } else {
            // otherwise pull the next proxy
            connectionProxy = this.connectionManager.endpointSharedConnectionProxy()
        }

        // add the endpoint request to the connection proxy
        connectionProxy.addEndpointRequest(request)
    }




    /**
     * Declares queues on the specified connection
     * @param conn
     * @param request
     */
    EndpointResult createEndpointOnConnectionProxy(ConnectionProxy connectionProxy, EndpointRequest request) {
        // default that there are no errors
        def errorFlag = false

        // pull the connection
        def conn = connectionProxy.connection

        // declare a command channel
        Channel commandChannel = null

        try {
            commandChannel = conn.createChannel()

            // if the channel isn't open, throw an error
            if (!commandChannel?.isOpen()) {
                throw new Error("Unknown error opening command channel")
            }

        } catch (error) {
            // flag that there's an error
            errorFlag = true

            // log the error
            request.logEndpointError(error)
        }


        // declare an ack channel
        Channel ackChannel = null

        // if we're using an acknowledgement
        if (request.useAcknowledgementFlag && !errorFlag) {
            try {
                ackChannel = conn.createChannel()

                // if the channel isn't open, throw an error
                if (!ackChannel?.isOpen()) {
                    throw new Error("Unknown error opening ack channel")
                }

            } catch (error) {
                // flag that there's an error
                errorFlag = true

                // log the error
                request.logEndpointError(error)
            }
        }



        // declare a command queue
        if (!errorFlag) {
            try {
                def durableFlag = !request.autoDeleteOnServerRestartFlag
                def exclusiveFlag = !request.allowReplicationFlag
                def autoDeleteFlag = request.autoDeleteNoConsumerFlag

                commandChannel.queueDeclare(
                        request.cmdUrl,
                        durableFlag,
                        exclusiveFlag,
                        autoDeleteFlag,
                        null
                )

                commandChannel.basicQos(1)

                def envelopeReceiptHandler = ({} as Closure)
                def channelShutdownHandler = ({} as Closure)

                // declare a consumer for the queue
                def consumer = new GenericConsumer(
                        commandChannel,
                        this.executorService,
                        envelopeReceiptHandler,
                        channelShutdownHandler
                )

                // tell the channel to consume for the queue
                commandChannel.basicConsume(request.cmdUrl, false, consumer)


            } catch (error) {
                // flag that there's an error
                errorFlag = true

                // log the error
                request.logEndpointError(error)
            }
        }


        // if we're using an acknowledgement
        if (request.useAcknowledgementFlag && !errorFlag) {
            // declare an acknowledgement queue
            try {
                ackChannel.queueDeclare(
                        request.ackUrl,
                        false,
                        true,
                        true,
                        null
                )

                ackChannel.basicQos(1)

                def envelopeReceiptHandler = ({} as Closure)
                def channelShutdownHandler = ({} as Closure)

                // declare a consumer for the queue
                def consumer = new GenericConsumer(
                        ackChannel,
                        this.executorService,
                        envelopeReceiptHandler,
                        channelShutdownHandler
                )

                // tell the channel to consume for the queue
                ackChannel.basicConsume(request.ackUrl, false, consumer)


            } catch (error) {
                // flag the error
                errorFlag = true

                // log the error
                request.logEndpointError(error)
            }
        }


        // if there were any errors, close everything
        if (errorFlag) {
            commandChannel?.close()
            ackChannel?.close()
        }


        // create a result
        def result = request.newEndpointResult(commandChannel, ackChannel)

        return result
    }




}
