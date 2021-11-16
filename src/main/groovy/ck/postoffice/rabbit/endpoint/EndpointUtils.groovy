package ck.postoffice.rabbit.endpoint

class EndpointUtils {

    /**
     * Generates an acknowlegment queue name
     * @param endpoint
     * @return
     */
    static String ackUrlForEndpoint(String endpoint) {
        return "ack://" + endpoint
    }


    /**
     * Generates a command queue name
     * @param endpoint
     * @return
     */
    static String cmdUrlForEndpoint(String endpoint) {
        return "cmd://" + endpoint
    }



}
