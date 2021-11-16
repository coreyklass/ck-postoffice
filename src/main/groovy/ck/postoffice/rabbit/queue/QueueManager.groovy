package ck.postoffice.rabbit.queue

import ck.postoffice.rabbit.Postmaster
import ck.postoffice.rabbit.PostmasterConfig
import ck.postoffice.rabbit.connection.ConnectionManager
import ck.postoffice.rabbit.envelope.EnvelopeManager
import ck.postoffice.rabbit.envelope.IncomingEnvelope
import ck.postoffice.rabbit.envelope.OutgoingEnvelope
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection

import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class QueueManager {


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
     * Shortcut to the envelope manager
     * @return
     */
    EnvelopeManager getEnvelopeManager() {
        return this._postmaster.envelopeManager
    }




    /**
     * Response Queue
     */
    private Queue _responseQueue = null




    /**
     * Response blocking queues
     * @return
     */
    ConcurrentHashMap<String, BlockingQueue> getResponseBlockingQueues() {
        return this.envelopeManager.responseBlockingQueues
    }


    /**
     * Ack blocking queues
     * @return
     */
    ConcurrentHashMap<String, BlockingQueue> getAckBlockingQueues() {
        return this.envelopeManager.responseBlockingQueues
    }





    /**
     * Executor service
     */
    private def _envelopeHandlerExecutorService = Executors.newCachedThreadPool()






    /**
     * Constructor
     * @param rabbitConnectionManager
     */
    QueueManager(Postmaster postmaster) {
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
        def request = new QueueRequest()
        request.exclusiveConnectionFlag = true
        request.queueName = responseQueueName
        request.sendReplyFlag = false
        request.autoDeleteNoConsumerFlag = true
        request.allowReplicationFlag = false
        request.autoDeleteOnServerRestartFlag = true
        request.autoReconnectFlag = true
        request.useAcknowledgementFlag = false


        request.listenResultHandler = { Queue resultQueue ->
            this._responseQueue = resultQueue
        }

        // on envelope receipt
        request.envelopeReceivedHandler = { IncomingEnvelope incomingEnvelope ->
            // if this is an Ack request
            if (incomingEnvelope.customData?.get("ackRequestFlag")) {
                // pull the Ack correlation ID
                def ackCorrelationID = incomingEnvelope.customData["ackCorrelationID"]

                // pull the ack queue by correlation ID and add the message to it
                def ackQueue = this.envelopeManager.ackBlockingQueues[ackCorrelationID]
                ackQueue.add(incomingEnvelope)

                // send a response message
                def ackOutgoingEnvelope = new OutgoingEnvelope()
                ackOutgoingEnvelope.recipientQueueName = incomingEnvelope.replyToQueueName
                ackOutgoingEnvelope.customData = [
                        ackProceedFlag: true,
                        ackCorrelationID: ackCorrelationID
                ]

                this.envelopeManager.sendEnvelope(ackOutgoingEnvelope)

            // if this is not an Ack request
            } else {

            }
        }


        request.disconnectHandler = {
        }

        this.listenOnQueue(request)
    }




    /**
     * Opens a queue from a request
     * @param request
     */
    void listenOnQueue(QueueRequest request) {
        // declare a closure for listening on the queue
        def queueListenClosure = { Connection connection ->
            // declare the queue(s)
            def listenerResult = this._declareQueueOnConnection(connection, request)

            // if there's a completion handler, run it with the result
            if (request.listenResultHandler) {
                request.listenResultHandler(listenerResult)
            }
        }

        // if the caller is requesting an exclusive connection
        if (request.exclusiveConnectionFlag) {
            // what we do on success
            def connectionSuccessHandler = ({ Connection connection ->
                // run the queue listen closure
                queueListenClosure(connection)
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
    private Queue _declareQueueOnConnection(Connection conn, QueueRequest request) {
        // declare a listener result object
        def queue = new Queue(request)


        // declare a channel for the queue
        Channel channel = null

        try {
            channel = conn.createChannel()

        } catch (error) {
            // do stuff on error
        }



        // declare a queue
        try {
            def durableFlag = !queue.autoDeleteOnServerRestartFlag
            def exclusiveFlag = !queue.allowReplicationFlag
            def autoDeleteFlag = queue.autoDeleteNoConsumerFlag

            channel.queueDeclare(
                queue.queueName,
                durableFlag,
                exclusiveFlag,
                autoDeleteFlag,
                null
            )

            channel.basicQos(1)

            // define what happens on envelope receipt
            def envelopeReceiptHandler = ({ IncomingEnvelope incomingEnvelope ->
                if (queue.useAcknowledgementFlag) {
                    // if the queue requires acknowledgement, prepare an outgoing envelope
                    def ackOutgoingEnvelope = new OutgoingEnvelope()
                    ackOutgoingEnvelope.recipientQueueName = incomingEnvelope.replyToQueueName
                    ackOutgoingEnvelope.customData = ([
                            ackRequestFlag: true,
                            ackCorrelationID: incomingEnvelope.correlationID
                    ] as Map<String, Object>)

                    // send the envelope and wait for a response
                    def ackIncomingEnvelope = this.envelopeManager.sendAndReceiveEnvelope(ackOutgoingEnvelope)

                    // if the incoming envelope contains the ack proceed message
                    if (ackIncomingEnvelope?.customData?.get("ackProceedFlag")) {
                        queue.envelopeReceivedHandler(incomingEnvelope)
                    }

                } else {
                    // if the envelope can be processed without acknowledgement
                    queue.envelopeReceivedHandler(incomingEnvelope)
                }
            } as Closure)

            // define what happens on channel shutdown
            def channelShutdownHandler = ({} as Closure)

            // declare a consumer for the queue
            def consumer = new GenericConsumer(
                channel,
                this._envelopeHandlerExecutorService,
                envelopeReceiptHandler,
                channelShutdownHandler
            )

            // tell the channel to consume for the queue
            channel.basicConsume(queue.queueName, false, consumer)


        } catch (error) {
            // do something on error
        }


        return queue
    }




}
