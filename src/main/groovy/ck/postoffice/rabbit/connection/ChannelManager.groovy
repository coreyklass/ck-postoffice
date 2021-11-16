package ck.postoffice.rabbit.connection

import ck.postoffice.rabbit.Postmaster
import ck.postoffice.rabbit.PostmasterConfig
import com.rabbitmq.client.Connection

class ChannelManager {


    /**
     * Postmaster
     */
    Postmaster _postmaster = null


    /**
     * Convenience method to return the Postmaster config
     * @return
     */
    private PostmasterConfig getConfig() {
        return this._postmaster.config
    }



    void openChannel(String channelKey) {

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
