package ck.postoffice.rabbit.endpoint

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection

class EndpointResult extends EndpointBase {


    /**
     * Rabbit Connection
     */
    private Connection _connection = null



    /**
     * Rabbit Connection
     */
    Connection getConnection() {
        return this._connection
    }





    /**
     * Command queue channel
     */
    protected Channel _cmdEndpointChannel = null


    /**
     * Command queue channel
     * @return
     */
    Channel getCmdEndpointChannel() {
        return this._cmdEndpointChannel
    }




    /**
     * The acknowledgement queue channel
     */
    protected Channel _ackEndpointChannel = null


    /**
     * The acknowledgement queue channel
     */
    Channel getAckEndpointChannel() {
        return this._ackEndpointChannel
    }









}
