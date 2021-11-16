package ck.postoffice.rabbit.listener

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection

class RabbitListenerResult extends RabbitListenerBase {


    /**
     * Rabbit Connection
     */
    private Connection _rabbitConnection = null



    /**
     * Rabbit Connection
     */
    Connection getRabbitConnection() {
        return this._rabbitConnection
    }





    /**
     * Command queue channel
     */
    private Channel _commandQueueChannel = null


    /**
     * Command queue channel
     * @return
     */
    Channel getCommandQueueChannel() {
        return this._commandQueueChannel
    }





    /**
     * The name of the command queue
     */
    private String _commandQueueName = null


    /**
     * The name of the command queue
     */
    String getCommandQueueName() {
        if (!this._commandQueueName) {
            this._commandQueueName = (this.useAcknowledgementFlag ? "cmd://" : "") + this.queueName
        }

        return this._commandQueueName
    }



    /**
     * The acknowledgement queue channel
     */
    private Channel _ackQueueChannel = null


    /**
     * The acknowledgement queue channel
     */
    Channel getAckQueueChannel() {
        return this._ackQueueChannel
    }





    /**
     * The name of the acknowledgment queue
     */
    private String _ackQueueName = null



    /**
     * The name of the acknowledgment queue
     */
    String getAckQueueName() {
        if (!this._ackQueueName) {
            this._ackQueueName = "ack://" + this.queueName + "/" + UUID.randomUUID().toString()
        }

        return this._ackQueueName
    }














    /**
     * Empty constructor
     */
    RabbitListenerResult() {
        def exception = new Exception("RabbitListenerResult cannot be called with an empty constructor")
    }



    /**
     * Constructor with a request
     * @param request
     */
    RabbitListenerResult(RabbitListenerRequest request) {
        // copy the contents of the request
        this._exclusiveConnectionFlag = request.exclusiveConnectionFlag
        this._queueName = request.queueName
        this._sendReplyFlag = request.sendReplyFlag
        this._autoDeleteNoConsumerFlag = request.autoDeleteNoConsumerFlag
        this._allowReplicationFlag = request.allowReplicationFlag
        this._autoDeleteOnServerRestartFlag = request.autoDeleteOnServerRestartFlag
        this._autoReconnectFlag = request.autoReconnectFlag
        this._useAcknowledgementFlag = request.useAcknowledgementFlag
        this._envelopeReceivedHandler = request.envelopeReceivedHandler
        this._disconnectHandler = request.disconnectHandler
    }


}
