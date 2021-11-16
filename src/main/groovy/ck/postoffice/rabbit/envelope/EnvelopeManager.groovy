package ck.postoffice.rabbit.envelope

import ck.postoffice.rabbit.Postmaster
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class EnvelopeManager {

    /**
     * Rabbit Postmaster
     */
    private Postmaster _postmaster = null


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
     * Ack queues
     */
    def ackBlockingQueues = new ConcurrentHashMap<String, BlockingQueue>()



    /**
     * Envelope response timeout
     */
    def envelopeResponseTimeoutMS = 30000


    /**
     * Acknowledgement response timeout
     */
    def ackResponseTimeoutMS = 5000



    /**
     * Constructor
     * @param postmaster
     */
    EnvelopeManager(Postmaster postmaster) {
        this._postmaster = postmaster
    }


    /**
     * Sends an outgoing envelope with no response
     * @param envelope
     */
    void sendEnvelope(OutgoingEnvelope envelope) {
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


            // is an acknowledgement required?
            def ackRequiredFlag = true


            def jsonBytes = new byte[1]


            // if an ack is required
            if (ackRequiredFlag) {
                try {
                    // create an ack blocking queue
                    def ackQueue = new ArrayBlockingQueue<IncomingEnvelope>(1)
                    this.ackBlockingQueues[envelope.correlationID] = ackQueue

                    // define an ack envelope
                    IncomingEnvelope ackEnvelope = null

                    // repeat sending until an ack is received
                    while (!ackEnvelope) {
                        // publish the message
                        channel.basicPublish(exchangeName, queueName, props, jsonBytes)

                        // wait for a response
                        ackEnvelope = ackQueue.poll(this.ackResponseTimeoutMS, TimeUnit.MILLISECONDS)

                    }

                    // remove the ack blocking queue
                    this.ackBlockingQueues.remove(envelope.correlationID)


                } catch (error) {
                    // handle an error
                }


            // if an ack is not required
            } else {
                try {
                    // publish the message
                    channel.basicPublish(exchangeName, queueName, props, jsonBytes)

                } catch (error) {
                    // handle an error
                }
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
    IncomingEnvelope sendAndReceiveEnvelope(OutgoingEnvelope outgoingEnvelope) {
        // define a blocking queue to receive the response
        def responseQueue = new ArrayBlockingQueue<IncomingEnvelope>(1)
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
