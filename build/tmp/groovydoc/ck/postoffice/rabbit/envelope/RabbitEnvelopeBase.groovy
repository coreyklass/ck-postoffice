package ck.postoffice.rabbit.envelope

class RabbitEnvelopeBase {


    private static def _jsonDateCreated = "dateCreated"
    private static def _jsonEnvelopeID = "envelopeID"
    private static def _jsonSendingServer = "sendingServer"
    private static def _jsonCorrelationID = "correlationID"
    private static def _jsonReplyToQueueName = "replyToQueueName"
    private static def _jsonRecipientExchangeName = "recipientExchangeName"
    private static def _jsonRecipientQueueName = "recipientQueueName"
    private static def _jsonRecipientQueueAction = "recipientQueueAction"
    private static def _jsonEnvelopeContentJsonText = "envelopeContentJsonText"
    private static def _jsonEnvelopeContentObject = "envelopeContentObject"
    private static def _jsonErrorText = "errorText"
    private static def _jsonCustomData = "customData"



    /**
     * Date that the envelope was created
     */
    protected Date _dateCreated = null



    /**
     * Date that the envelope was created
     */
    Date getDateCreated() {
        return this._dateCreated
    }


    /**
     * Unique ID value for this envelope
     */
    protected String _envelopeID = null


    /**
     * Unique ID value for this envelope
     */
    String getEnvelopeID() {
        return this._envelopeID
    }


    /**
     * Name of the server that sent the envelope
     */
    protected String _sendingServer = null


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
    protected String _correlationID = null


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
    protected String _replyToQueueName = null


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
    protected String _recipientExchangeName = null


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
    protected String _recipientQueueName = null


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
    protected String _recipientQueueAction = null


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
    protected String _envelopeContentJsonText = null


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
    protected Object _envelopeContentObject = null


    /**
     * JSON Object of the envelope content
     * @return
     */
    Object getEnvelopeContent() {
        return this._envelopeContentObject
    }


    /**
     * Any error text to be included
     */
    protected String _errorText = null


    /**
     * Any error text to be included
     * @return
     */
    String getErrorText() {
        return this._errorText
    }


    /**
     * Custom data to store
     */
    protected Map<String, Object> _customData = null


    /**
     * Custom data to store
     * @return
     */
    Map<String, Object> getCustomData() {
        return this._customData
    }




    /**
     * Properties for a JSON representation
     * @return
     */
    Map<String, Object> jsonProps() {
        def jsonProps = ([:] as Map<String, Object>)

        jsonProps[_jsonDateCreated] = this.dateCreated
        jsonProps[_jsonEnvelopeID] = this.envelopeID
        jsonProps[_jsonSendingServer] = this.sendingServer
        jsonProps[_jsonCorrelationID] = this.correlationID
        jsonProps[_jsonReplyToQueueName] = this.replyToQueueName
        jsonProps[_jsonRecipientExchangeName] = this.recipientExchangeName
        jsonProps[_jsonRecipientQueueName] = this.recipientQueueName
        jsonProps[_jsonRecipientQueueAction] = this.recipientQueueAction
        jsonProps[_jsonEnvelopeContentJsonText] = this.envelopeContentJsonText
        jsonProps[_jsonEnvelopeContentObject] = this.envelopeContent
        jsonProps[_jsonErrorText] = this.errorText

        if (this._customData) {
            jsonProps[_jsonCustomData] = this._customData
        }

        return jsonProps
    }



}
