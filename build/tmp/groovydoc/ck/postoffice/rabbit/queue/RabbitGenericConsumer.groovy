package ck.postoffice.rabbit.queue

import ck.postoffice.rabbit.envelope.RabbitIncomingEnvelope
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.rabbitmq.client.ShutdownSignalException

import java.util.concurrent.ExecutorService

class RabbitGenericConsumer extends DefaultConsumer {

    /**
     * Envelope receipt handler
     */
    Closure _envelopeReceiptHandler = null


    /**
     * Channel shutdown handler
     */
    Closure _channelShutdownHandler = null


    /**
     * ExecutorService for handling envelope receipts
     */
    ExecutorService _handlerExecutorService = null



    /**
     * Constructor
     * @param channel
     */
    RabbitGenericConsumer(
        Channel channel,
        ExecutorService handlerExecutorService,
        Closure envelopeReceiptHandler,
        Closure channelShutdownHandler
    ) {
        super(channel)

        this._handlerExecutorService = handlerExecutorService
        this._envelopeReceiptHandler = envelopeReceiptHandler
        this._channelShutdownHandler = channelShutdownHandler
    }




    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body)
        throws IOException {

        // execute in a separate thread
        this._handlerExecutorService.submit({
            // acknowledge that the message was received
            this.channel.basicAck(envelope.getDeliveryTag(), false)

            // convert the body to JSON text
            String envelopeJsonText = new String(body, "UTF-8")

            // convert the text to a JSON object
            def objectMapper = new ObjectMapper()
            def envelopeJson = (objectMapper.readValue(envelopeJsonText, Map.class) as Map<String, Object>)

            // generate a rabbit incoming envelope and send it back to the caller
            def rabbitEnvelope = new RabbitIncomingEnvelope(envelopeJson)

            this._envelopeReceiptHandler(rabbitEnvelope)
        })
    }



    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        this._channelShutdownHandler()
    }



}
