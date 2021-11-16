package ck.postoffice.rabbit.queue

class RabbitQueueBase {



    /**
     * Specifies whether an exclusive connection should be used for this queue
     */
    protected Boolean _exclusiveConnectionFlag


    /**
     * Specifies whether an exclusive connection should be used for this queue
     */
    Boolean getExclusiveConnectionFlag() {
        return this._exclusiveConnectionFlag
    }



    /**
     * Name of the queue to listen on
     */
    protected String _queueName



    /**
     * Name of the queue to listen on
     */
    String getQueueName() {
        return this._queueName
    }



    /**
     * Specifies if a reply will be sent on message processing completion
     */
    protected Boolean _sendReplyFlag


    /**
     * Specifies if a reply will be sent on message processing completion
     */
    Boolean getSendReplyFlag() {
        return this._sendReplyFlag
    }




    /**
     * When the last consumer for a queue is disconnected, should the queue automatically be deleted?
     */
    protected Boolean _autoDeleteNoConsumerFlag


    /**
     * When the last consumer for a queue is disconnected, should the queue automatically be deleted?
     */
    Boolean getAutoDeleteNoConsumerFlag() {
        return this._autoDeleteNoConsumerFlag
    }



    /**
     * Should replication for the queue be allowed across multiple servers?
     */
    protected Boolean _allowReplicationFlag


    /**
     * Should replication for the queue be allowed across multiple servers?
     */
    Boolean getAllowReplicationFlag() {
        return this._allowReplicationFlag
    }




    /**
     * Automatically delete the queue on Rabbit server restart
     */
    protected Boolean _autoDeleteOnServerRestartFlag



    /**
     * Automatically delete the queue on Rabbit server restart
     */
    Boolean getAutoDeleteOnServerRestartFlag() {
        return this._autoDeleteOnServerRestartFlag
    }



    /**
     * If there is a disconnection, should a reconnect be attempted?
     */
    protected Boolean _autoReconnectFlag



    /**
     * If there is a disconnection, should a reconnect be attempted?
     */
    Boolean getAutoReconnectFlag() {
        return this._autoReconnectFlag
    }


    /**
     * Should an acknowledgement be used
     */
    protected Boolean _useAcknowledgementFlag = false


    /**
     * Should an acknowledgement be used
     * @return
     */
    Boolean getUseAcknowledgementFlag() {
        return this._useAcknowledgementFlag
    }


    /**
     * Listen result handler
     */
    protected Closure _listenResultHandler = null


    /**
     * Listen result handler
     * @return
     */
    Closure getListenResultHandler() {
        return this._listenResultHandler
    }





    /**
     * Callable to execute when a RabbitIncomingEnvelope is received
     */
    protected Closure _envelopeReceivedHandler



    /**
     * Callable to execute when a RabbitIncomingEnvelope is received
     */
    Closure getEnvelopeReceivedHandler() {
        return this._envelopeReceivedHandler
    }



    /**
     * Runnable to execute when the connection is disconnected
     */
    protected Closure _disconnectHandler


    /**
     * Runnable to execute when the connection is disconnected
     */
    Closure getDisconnectHandler() {
        return this._disconnectHandler
    }



}
