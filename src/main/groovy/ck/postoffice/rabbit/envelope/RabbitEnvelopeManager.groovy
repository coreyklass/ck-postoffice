package ck.postoffice.rabbit.envelope

import ck.postoffice.rabbit.RabbitPostmaster
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class RabbitEnvelopeManager {

    /**
     * Rabbit Postmaster
     */
    private RabbitPostmaster _postmaster = null


    /**
     * Outgoing channel
     */
    Channel _outgoingChannel = null


    /**
     * Outgoing channel
     * @return
     */
    Channel getOutgoingChannel() {
        return this._postmaster.connectionManager.outgoingChannel
    }


    /**
     * Response queues
     */
    def responseBlockingQueues = new ConcurrentHashMap<String, BlockingQueue>()


    /**
     * Envelope response timeout
     */
    def envelopeResponseTimeoutMS = 30000




    /**
     * Constructor
     * @param postmaster
     */
    RabbitEnvelopeManager(RabbitPostmaster postmaster) {
        this._postmaster = postmaster
    }


    /**
     * Sends an outgoing envelope with no response
     * @param envelope
     */
    void sendEnvelope(RabbitOutgoingEnvelope envelope) {
        // pull the outgoing channel
        def channel = this.outgoingChannel

        // if the channel is open
        if (channel?.isOpen()) {
            // build a properties object
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .correlationId(envelope.correlationID)
                    .build()


            // we need the recipient detail
            def queueName = envelope.recipientQueueName

            if ((envelope.recipientQueueAction ?: "") != "") {
                queueName += "/" + envelope.recipientQueueAction
            }

            def exchangeName = envelope.recipientExchangeName

            try {
                def jsonBytes = new byte[1]

                // publish the message
                channel.basicPublish(exchangeName, queueName, props, jsonBytes)

            } catch (error) {
                // handle an error
            }


        // if the channel is NOT open
        } else {

        }

    }



    /**
     * Sends an outgoing envelope and waits to receive a message
     * @param outgoingEnvelope
     * @return
     */
    RabbitIncomingEnvelope sendAndReceiveEnvelope(RabbitOutgoingEnvelope outgoingEnvelope) {
        // define a blocking queue to receive the response
        def responseQueue = new ArrayBlockingQueue<RabbitIncomingEnvelope>(1)
        this.responseBlockingQueues[outgoingEnvelope.correlationID] = responseQueue

        // send the message
        this.sendEnvelope(outgoingEnvelope)

        // wait for a response
        def incomingEnvelope = responseQueue.poll(this.envelopeResponseTimeoutMS, TimeUnit.MILLISECONDS)

        // remove the response blocking queue
        this.responseBlockingQueues.remove(outgoingEnvelope.correlationID)

        return incomingEnvelope
    }






}
