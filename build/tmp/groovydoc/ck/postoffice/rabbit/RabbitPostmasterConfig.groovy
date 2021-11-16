package ck.postoffice.rabbit

class RabbitPostmasterConfig {

    /**
     * Name of the current server
     */
    String serverName

    /**
     * Name of the current application
     */
    String applicationName



    /**
     * List of rabbit server addresses to round robin through
     */
    private List<String> _rabbitServerAddresses = null


    /**
     * List of rabbit server addresses to round robin through
     * @return
     */
    List<String> getRabbitServerAddresses() {
        return this._rabbitServerAddresses
    }

    void setRabbitServerAddresses(List<String> rabbitServerAddresses) {
        this._rabbitServerAddresses = rabbitServerAddresses?.collect { return it }
    }




    /**
     * Rabbit Username
     */
    String rabbitUsername


    /**
     * Rabbit Password
     */
    String rabbitPassword


}
