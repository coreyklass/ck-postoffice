package ck.postoffice.rabbit.queue

class Queue extends QueueBase {

    Queue(QueueRequest request) {
        super()

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
