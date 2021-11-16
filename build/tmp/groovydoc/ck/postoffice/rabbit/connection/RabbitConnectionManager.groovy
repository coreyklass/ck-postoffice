package ck.postoffice.rabbit.connection

import ck.postoffice.rabbit.RabbitPostmaster
import ck.postoffice.rabbit.RabbitPostmasterConfig
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection

import java.util.concurrent.Semaphore

class RabbitConnectionManager {

    /**
     * Postmaster
     */
    RabbitPostmaster _postmaster = null


    /**
     * Convenience method to return the Postmaster config
     * @return
     */
    RabbitPostmasterConfig getConfig() {
        return this._postmaster.config
    }



    /**
     * When round-robining for Rabbit servers, this is the current index
     */
    private Integer _rabbitServerIndex = null



    /**
     * Gate for preventing duplicate rabbit server index access
     */
    private def _rabbitServerIndexGate = new Semaphore(1)



    /**
     * Outgoing channel
     */
    Channel _outgoingChannel = null


    /**
     * Outgoing channel
     * @return
     */
    Channel getOutgoingChannel() {
        return this._outgoingChannel
    }





    /**
     * Constructor
     * @param postmaster
     * @param rabbitServerAddresses
     * @param rabbitUsername
     * @param rabbitPassword
     */
    RabbitConnectionManager(RabbitPostmaster postmaster) {
        this._postmaster = postmaster

        this._openOutgoingChannel()
    }




    /**
     * Pulls the next rabbit server address
     * @return
     */
    private String _nextRabbitServerAddress() {
        // retrieve the rabbit server addresses
        def rabbitServerAddresses = this.config.rabbitServerAddresses.collect { return it }

        // lock the gate
        this._rabbitServerIndexGate.acquire()

        // where we're storing the index
        Integer rabbitServerIndex = null

        // if the index is NULL, pull a random one
        if (this._rabbitServerIndex == null) {
            def initialIndex = Math.ceil(Math.random() * 1000) % rabbitServerAddresses.size()

            rabbitServerIndex = initialIndex

        // if the index is NULL, increment by one
        } else {
            rabbitServerIndex++

            // if the index is too large, reset it
            if (rabbitServerIndex > (rabbitServerAddresses.size() - 1)) {
                rabbitServerIndex = 0
            }
        }

        // pull the address from the List
        def rabbitServerAddress = rabbitServerAddresses[rabbitServerIndex]

        // store the new index
        this._rabbitServerIndex = rabbitServerIndex

        // release the gate
        this._rabbitServerIndexGate.release()

        // return the address
        return rabbitServerAddress
    }




    /**
     * Opens a connection and performs some action
     */
    void openConnection(Closure connectionSuccessHandler, Closure connectionFailureHandler) {

    }


    /**
     * Opens an outgoing channel
     */
    private void _openOutgoingChannel() {
        def successHandler = { Connection connection ->
            // create a channel
            try {
                // create a channel on the new connection
                def channel = connection.createChannel()

                // if the channel is open, store it
                if (channel?.isOpen()) {
                    this._outgoingChannel = connection.createChannel()
                }

            } catch (error) {
                // do something on error
            }
        }

        def failureHandler = { }

        this.openConnection(successHandler, failureHandler)
    }



}
