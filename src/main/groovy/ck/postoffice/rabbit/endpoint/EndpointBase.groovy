package ck.postoffice.rabbit.endpoint

import ck.postoffice.rabbit.util.gate.Gate

class EndpointBase {


    /**
     * Specifies whether an exclusive connection should be used for this listener
     */
    protected Boolean _exclusiveConnectionFlag


    /**
     * Specifies whether an exclusive connection should be used for this listener
     */
    Boolean getExclusiveConnectionFlag() {
        return this._exclusiveConnectionFlag
    }



    /**
     * Name of the endpoint to listen on
     */
    protected String _endpointName


    /**
     * Name of the endpoint to listen on
     */
    String getEndpointName() {
        return this._endpointName
    }


    /**
     * Acknowledgement URL
     */
    protected String _ackUrl


    /**
     * Returns the acknowledgement URL
     * @return
     */
    String getAckUrl() {
        return this._ackUrl
    }



    /**
     * Command URL
     */
    protected String _cmdUrl


    /**
     * Returns the command URL
     * @return
     */
    String getCmdUrl() {
        return this._cmdUrl
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
     * Carrier open result handler
     */
    protected Closure _onEndpointOpen = null




    /**
     * Carrier open result handler
     * @return
     */
    Closure getOnEndpointOpen() {
        return this._onEndpointOpen
    }



    /**
     * Callable to execute when an IncomingEnvelope is received
     */
    protected Closure _envelopeReceivedHandler



    /**
     * Callable to execute when an IncomingEnvelope is received
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






    /**
     * Gate for blocking errors
     */
    protected def _endpointErrorGate = new Gate("EndpointErrorGate")



    /**
     * Stores the endpoint errors
     */
    protected List<Exception> _endpointErrors = []


    /**
     * Returns endpoint errors
     * @return
     */
    List<Exception> getEndPointErrors() {
        this._endpointErrorGate.acquire()

        def errors = this._endpointErrors.collect { return it }

        this._endpointErrorGate.release()

        return errors
    }


    /**
     * Logs an endpoint error
     * @param error
     */
    void logEndpointError(Exception error) {
        this._endpointErrorGate.acquire()

        this._endpointErrors.add(error)

        this._endpointErrorGate.release()
    }





}
