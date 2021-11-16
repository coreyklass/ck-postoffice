package ck.postoffice.rabbit.connection

import com.rabbitmq.client.Connection

class ConnectionResult extends ConnectionBase {

    /**
     * Stores the connection
     */
    protected Connection _connection = null


    /**
     * Returns the current connection
     * @return
     */
    Connection getConnection() {
        return this._connection
    }





}
