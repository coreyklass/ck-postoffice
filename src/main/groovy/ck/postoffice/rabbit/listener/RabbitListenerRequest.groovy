package ck.postoffice.rabbit.listener

class RabbitListenerRequest extends RabbitListenerBase {


    /**
     * Specifies whether an exclusive connection should be used for this listener
     */
    void setExclusiveConnectionFlag(Boolean exclusiveConnectionFlag) {
        this._exclusiveConnectionFlag = exclusiveConnectionFlag
    }


    /**
     * Name of the queue to listen on
     */
    void setQueueName(String queueName) {
        this._queueName = queueName
    }



    /**
     * Specifies if a reply will be sent on message processing completion
     */
    void setSendReplyFlag(Boolean sendReplyFlag) {
        this._sendReplyFlag = sendReplyFlag
    }




    /**
     * When the last consumer for a queue is disconnected, should the queue automatically be deleted?
     */
    void setAutoDeleteNoConsumerFlag(Boolean autoDeleteNoConsumerFlag) {
        this._autoDeleteNoConsumerFlag = autoDeleteNoConsumerFlag
    }




    /**
     * Should replication for the queue be allowed across multiple servers?
     */
    void setAllowReplicationFlag(Boolean allowReplicationFlag) {
        this._allowReplicationFlag = allowReplicationFlag
    }





    /**
     * Automatically delete the queue on Rabbit server restart
     */
    void setAutoDeleteOnServerRestartFlag(Boolean autoDeleteOnServerRestartFlag) {
        this._autoDeleteOnServerRestartFlag = autoDeleteOnServerRestartFlag
    }






    /**
     * If there is a disconnection, should a reconnect be attempted?
     */
    void setAutoReconnectFlag(Boolean autoReconnectFlag) {
        this._autoReconnectFlag = autoReconnectFlag
    }




    /**
     * Should we use acknowledgement?
     * @param useAcknowledgementFlag
     */
    void setUseAcknowledgementFlag(Boolean useAcknowledgementFlag) {
        this._useAcknowledgementFlag = useAcknowledgementFlag
    }


    /**
     * When listening is complete
     * @param listenResultHandler
     */
    void setListenResultHandler(Closure listenResultHandler) {
        this._listenResultHandler = listenResultHandler
    }



    /**
     * Callable to execute when a RabbitIncomingEnvelope is received
     */
    void setEnvelopeReceivedHandler(Closure envelopeReceivedHandler) {
        this._envelopeReceivedHandler = envelopeReceivedHandler
    }




    /**
     * Runnable to execute when the connection is disconnected
     */
    void setDisconnectHandler(Closure disconnectHandler) {
        this._disconnectHandler = disconnectHandler
    }






}
