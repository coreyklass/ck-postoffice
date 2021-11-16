package ck.postoffice.rabbit.listener

import ck.postoffice.rabbit.RabbitPostmaster
import ck.postoffice.rabbit.RabbitPostmasterConfig
import ck.postoffice.rabbit.connection.RabbitConnectionManager
import ck.postoffice.rabbit.queue.RabbitGenericConsumer
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection

import java.util.concurrent.Executors

class RabbitListenerManager {


    /**
     * Rabbit Postmaster
     */
    private RabbitPostmaster _postmaster = null



    /**
     * Convenience method to return the Postmaster config
     * @return
     */
    RabbitPostmasterConfig getConfig() {
        return this._postmaster.config
    }



    /**
     * Shortcut to the connection manager
     * @return
     */
    RabbitConnectionManager getConnectionManager() {
        return this._postmaster.connectionManager
    }



    /**
     * Response Queue Rabbit Listener Result
     */
    private RabbitListenerResult _responseQueueRabbitListener = null


    /**
     * Response Queue Rabbit Listener Result
     * @return
     */
    RabbitListenerResult getResponseQueueRabbitListener() {
        return this._responseQueueRabbitListener
    }



    /**
     * Name of the response queue
     * @return
     */
    String getResponseQueueName() {
        return this.responseQueueRabbitListener.queueName
    }



    /**
     * Executor service
     */
    private def _envelopeHandlerExecutorService = Executors.newCachedThreadPool()






    /**
     * Constructor
     * @param rabbitConnectionManager
     */
    RabbitListenerManager(RabbitPostmaster postmaster) {
        this._postmaster = postmaster

        // open a response queue
        this._openResponseQueue()
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
        def listenerRequest = new RabbitListenerRequest()
        listenerRequest.exclusiveConnectionFlag = true
        listenerRequest.queueName = responseQueueName
        listenerRequest.sendReplyFlag = false
        listenerRequest.autoDeleteNoConsumerFlag = true
        listenerRequest.allowReplicationFlag = false
        listenerRequest.autoDeleteOnServerRestartFlag = true
        listenerRequest.autoReconnectFlag = true
        listenerRequest.useAcknowledgementFlag = false
        listenerRequest.listenResultHandler = { RabbitListenerResult listenerResult ->
            this._responseQueueRabbitListener = listenerResult
        }
        listenerRequest.envelopeReceivedHandler = {
        }
        listenerRequest.disconnectHandler = {
        }

        this.openListener(listenerRequest)
    }




    /**
     * Opens a listener from a request
     * @param request
     */
    void openListener(RabbitListenerRequest request) {
        // if the caller is requesting an exclusive connection
        if (request.exclusiveConnectionFlag) {
            // what we do on success
            def connectionSuccessHandler = ({ Connection connection ->
                // declare the queue(s)
                def listenerResult = this._declareQueueOnConnection(connection, request)

                // if there's a completion handler, run it with the result
                if (request.listenResultHandler) {
                    request.listenResultHandler(listenerResult)
                }
            } as Closure)

            // what we do on failure
            def connectionFailureHandler = ({ Exception error ->

            } as Closure)

            // try connecting to the rabbit server and perform the resulting operation
            this.connectionManager.openConnection(connectionSuccessHandler, connectionFailureHandler)
        }
    }




    /**
     * Declares queues on the specified connection
     * @param conn
     * @param request
     */
    private RabbitListenerResult _declareQueueOnConnection(Connection conn, RabbitListenerRequest request) {
        // declare a listener result object
        def result = new RabbitListenerResult(request)


        // declare a command channel
        Channel commandChannel = null

        try {
            commandChannel = conn.createChannel()

        } catch (error) {
            // do stuff on error
        }


        // declare a channel for the acknowledgement
        Channel ackChannel = null

        // if we're using an acknowledgement
        if (result.useAcknowledgementFlag) {
            try {
                ackChannel = conn.createChannel()

            } catch (error) {
                // do stuff on error
            }
        }



        // declare a command queue
        try {
            def durableFlag = !result.autoDeleteOnServerRestartFlag
            def exclusiveFlag = !result.allowReplicationFlag
            def autoDeleteFlag = result.autoDeleteNoConsumerFlag

            commandChannel.queueDeclare(
                result.commandQueueName,
                durableFlag,
                exclusiveFlag,
                autoDeleteFlag,
                null
            )

            commandChannel.basicQos(1)

            def envelopeReceiptHandler = ({} as Closure)
            def channelShutdownHandler = ({} as Closure)

            // declare a consumer for the queue
            def consumer = new RabbitGenericConsumer(
                commandChannel,
                this._envelopeHandlerExecutorService,
                envelopeReceiptHandler,
                channelShutdownHandler
            )

            // tell the channel to consume for the queue
            commandChannel.basicConsume(result.commandQueueName, false, consumer)


        } catch (error) {
            // do something on error
        }



        // if we're using an acknowledgement
        if (result.useAcknowledgementFlag) {
            // declare an acknowledgement queue
            try {
                ackChannel.queueDeclare(
                        result.ackQueueName,
                        false,
                        true,
                        true,
                        null
                )

                ackChannel.basicQos(1)

                def envelopeReceiptHandler = ({} as Closure)
                def channelShutdownHandler = ({} as Closure)

                // declare a consumer for the queue
                def consumer = new RabbitGenericConsumer(
                        ackChannel,
                        this._envelopeHandlerExecutorService,
                        envelopeReceiptHandler,
                        channelShutdownHandler
                )

                // tell the channel to consume for the queue
                ackChannel.basicConsume(result.ackQueueName, false, consumer)


            } catch (error) {
                // do something on error
            }
        }

        return result
    }




}
