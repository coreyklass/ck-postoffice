package ck.postoffice.rabbit.envelope

import com.fasterxml.jackson.databind.ObjectMapper

class OutgoingEnvelope extends EnvelopeBase {


    /**
     * Constructor
     */
    OutgoingEnvelope() {
        this._dateCreated = new Date()
        this._envelopeID = UUID.randomUUID().toString().toLowerCase()
        this._sendingServer = "" // ** TO DO **
        this._correlationID = UUID.randomUUID().toString().toLowerCase()
    }


    /**
     * Queue name to reply to
     * @param replyToQueueName
     */
    void setReplyToQueueName(String replyToQueueName) {
        this._replyToQueueName = replyToQueueName
    }


    /**
     * Name of the recipient exchange
     * @param recipientExchangeName
     */
    void setRecipientExchangeName(String recipientExchangeName) {
        this._recipientExchangeName = recipientExchangeName
    }


    /**
     * Name of the recipient queue
     * @param recipientQueueName
     */
    void setRecipientQueueName(String recipientQueueName) {
        this._recipientQueueAction = recipientQueueName
    }


    /**
     * Name of the recipient queue action
     * @param recipientQueueAction
     */
    void setRecipientQueueAction(String recipientQueueAction) {
        this._recipientQueueAction = recipientQueueAction
    }



    /**
     * JSON Text of the envelope content
     * @param envelopeContentJsonText
     */
    void setEnvelopeContentJsonText(String envelopeContentJsonText) {
        // convert JSON to a native object
        def objectMapper = new ObjectMapper()

        def envelopeContentObject = null

        // try parsing as a Map
        if (envelopeContentObject == null) {
            try {
                envelopeContentObject = (objectMapper.readValue(envelopeContentJsonText, Map.class) as Map<String, Object>)

            } catch (error) {

            }
        }

        // try parsing as a List
        if (envelopeContentObject == null) {
            try {
                envelopeContentObject = (objectMapper.readValue(envelopeContentJsonText, List.class) as List<Object>)

            } catch (error) {

            }
        }

        // try parsing as a simple value
        if (envelopeContentObject == null) {
            try {
                envelopeContentObject = (objectMapper.readValue(envelopeContentJsonText, String.class) as String)

            } catch (error) {

            }
        }


        // if the JSON was parsed properly
        if (envelopeContentObject != null) {
            this._envelopeContentObject = envelopeContentObject
            this._envelopeContentJsonText = envelopeContentJsonText
        }

    }



    /**
     * JSON Object of the envelope content
     * @param envelopeContentObject
     */
    void setEnvelopeContent(Object envelopeContentObject) {
        try {
            def objectMapper = new ObjectMapper()

            this._envelopeContentJsonText = objectMapper.writeValueAsString(envelopeContentObject)
            this._envelopeContentObject = envelopeContentObject

        } catch (error) {
            // ** TO DO **

            throw error
        }
    }


    /**
     * Custom data
     * @param customData
     */
    void setCustomData(Map<String, Object> customData) {
        this._customData = customData
    }




}
