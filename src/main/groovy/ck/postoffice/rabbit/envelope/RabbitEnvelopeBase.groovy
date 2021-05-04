package ck.postoffice.rabbit.envelope

class RabbitEnvelopeBase {

    /**
     * Date that the envelope was created
     */
    Date _dateCreated = null



    /**
     * Date that the envelope was created
     */
    Date getDateCreated() {
        return this._dateCreated
    }


    /**
     * Unique ID value for this envelope
     */
    String _envelopeID = null


    /**
     * Unique ID value for this envelope
     */
    String getEnvelopeID() {
        return this._envelopeID
    }


    /**
     * Name of the server that sent the envelope
     */
    String _sendingServer = null


    /**
     * Name of the server that sent the envelope
     * @return
     */
    String getSendingServer() {
        return this._sendingServer
    }


    /**
     * CorrelationID used for matching messages
     */
    String _correlationID = null


    /**
     * CorrelationID used for matching messages
     * @return
     */
    String getCorrelationID() {
        return this._correlationID
    }




    /**
     * Queue name to reply to
     */
    String _replyToQueueName = null


    /**
     * Queue name to reply to
     * @return
     */
    String getReplyToQueueName() {
        return this._replyToQueueName
    }


    /**
     * Name of the recipient exchange
     */
    String _recipientExchangeName = null


    /**
     * Name of the recipient exchange
     * @return
     */
    String getRecipientExchangeName() {
        return this._recipientExchangeName
    }



    /**
     * Name of the recipient queue
     */
    String _recipientQueueName = null


    /**
     * Name of the recipient queue
     * @return
     */
    String getRecipientQueueName() {
        return this._recipientQueueName
    }


    /**
     * Name of the recipient queue action
     */
    String _recipientQueueAction = null


    /**
     * Name of the recipient queue action
     * @return
     */
    String getRecipientQueueAction() {
        return this._recipientQueueAction
    }


    /**
     * JSON Text of the envelope content
     */
    String _envelopeContentJsonText = null


    /**
     * JSON Text of the envelope content
     * @return
     */
    String getEnvelopeContentJsonText() {
        return this._envelopeContentJsonText
    }


    /**
     * JSON Object of the envelope content
     */
    Object _envelopeContentObject = null


    /**
     * JSON Object of the envelope content
     * @return
     */
    Object getEnvelopeContentObject() {
        return this._envelopeContentObject
    }


    /**
     * Any error text to be included
     */
    String _errorText = null


    /**
     * Any error text to be included
     * @return
     */
    String getErrorText() {
        return this._errorText
    }







}
