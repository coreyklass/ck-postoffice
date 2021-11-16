package ck.postoffice.rabbit

class PostmasterConfig {

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
    private List<String> _rabbitHostNames = []


    /**
     * List of rabbit server addresses to round robin through
     * @return
     */
    List<String> getRabbitHostNames() {
        return this._rabbitHostNames.collect { return it }
    }

    void setRabbitHostNames(List<String> rabbitHostNames) {
        this._rabbitHostNames = rabbitHostNames.collect { return it }
    }


    /**
     * Rabbit Username
     */
    String rabbitUsername


    /**
     * Rabbit Password
     */
    String rabbitPassword


    /**
     * Maximum number of endpoints per connection
     */
    Integer maxEndpointsPerConnection = 10



}
