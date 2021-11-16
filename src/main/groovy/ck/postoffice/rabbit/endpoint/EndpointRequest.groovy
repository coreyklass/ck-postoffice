package ck.postoffice.rabbit.endpoint

import com.rabbitmq.client.Channel

class EndpointRequest extends EndpointBase {


    /**
     * Specifies whether an exclusive connection should be used for this listener
     */
    void setExclusiveConnectionFlag(Boolean exclusiveConnectionFlag) {
        this._exclusiveConnectionFlag = exclusiveConnectionFlag
    }


    /**
     * Name of the queue to listen on
     */
    void setEndpointName(String endpointName) {
        this._endpointName = endpointName

        this._ackUrl = EndpointUtils.ackUrlForEndpoint(endpointName)
        this._cmdUrl = EndpointUtils.cmdUrlForEndpoint(endpointName)
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
    void setOnEndpointOpen(Closure onEndpointOpen) {
        this._onEndpointOpen = onEndpointOpen
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


    /**
     * Returns an endpoint result
     * @return
     */
    EndpointResult newEndpointResult(Channel cmdEndpointChannel, Channel ackEndpointChannel) {
        def result = new EndpointResult()

        // copy the contents of the request
        result._exclusiveConnectionFlag = this._exclusiveConnectionFlag
        result._endpointName = this._endpointName
        result._ackUrl = this._ackUrl
        result._cmdUrl = this._cmdUrl
        result._sendReplyFlag = this._sendReplyFlag
        result._autoDeleteNoConsumerFlag = this._autoDeleteNoConsumerFlag
        result._allowReplicationFlag = this._allowReplicationFlag
        result._autoDeleteOnServerRestartFlag = this._autoDeleteOnServerRestartFlag
        result._autoReconnectFlag = this._autoReconnectFlag
        result._useAcknowledgementFlag = this._useAcknowledgementFlag
        result._onEndpointOpen = this._onEndpointOpen
        result._envelopeReceivedHandler = this._envelopeReceivedHandler
        result._disconnectHandler = this._disconnectHandler
        result._endpointErrors = this._endpointErrors.collect { return it }

        result._cmdEndpointChannel = cmdEndpointChannel
        result._ackEndpointChannel = ackEndpointChannel

        return result
    }



}
