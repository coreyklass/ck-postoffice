package ck.postoffice.rabbit.envelope

class RabbitOutgoingEnvelope extends RabbitEnvelopeBase {


    // constructor
    RabbitOutgoingEnvelope() {
        this._dateCreated = new Date()
        this._envelopeID = UUID.randomUUID().toString().toLowerCase()
        this._correlationID = UUID.randomUUID().toString().toLowerCase()
    }

}
