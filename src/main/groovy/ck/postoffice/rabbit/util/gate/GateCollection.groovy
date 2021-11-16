package ck.postoffice.rabbit.util.gate

import groovy.json.JsonBuilder

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

class GateCollection {

    private def _gatesByKey = new ConcurrentHashMap<String, Gate>()

    private def _gateFactoryGate = new Semaphore(1)

    private Semaphore _defaultInactivitySecondsGate = new Semaphore(1)
    private Integer _defaultInactivitySeconds = 600

    def cleanupFlag = true
    private def _cleanupExecutorService = Executors.newSingleThreadExecutor()
    private def _cleanupIntervalSeconds = 300

    private def _cleanupRunningFlag = false
    private def _killCleanupFlag = false
    private def _shutdownFlag = false

    def logActivityFlag = false



    GateCollection() {
    }


    GateCollection(Integer defaultInactivitySeconds) {
        this._defaultInactivitySeconds = defaultInactivitySeconds
    }


    /**
     * Starts the cleanup thread
     */
    private void _startCleanupThread() {
        // if the cleanup isn't running
        if (!this._cleanupRunningFlag && this.cleanupFlag) {
            // start the cleanup
            this._cleanupExecutorService.submit({
                this._cleanupRunningFlag = true

                while (!this._shutdownFlag && !this._killCleanupFlag && this.cleanupFlag) {
                    try {
                        if (this.logActivityFlag) {
                            println("Processing gate cleanup")
                        }

                        def gateKeys = this._gatesByKey.keys().toList()

                        // loop over the gates, looking for an expired one
                        gateKeys.each { String indexKey ->
                            def indexGate = this._gatesByKey[indexKey]

                            // if the gate is expired and empty, erase it
                            if (indexGate.isInactive() && indexGate.isEmpty()) {
                                if (this.logActivityFlag) {
                                    println("Erasing gate: " + indexKey)
                                }

                                this._gateFactoryGate.acquire()

                                try {
                                    this._gatesByKey.remove(indexKey)

                                } catch (error) {
                                    def errorText = "Error cleaning gate " + indexKey + ": " + error.message

                                    println(errorText)

                                    error.printStackTrace()
                                }

                                this._gateFactoryGate.release()
                            }
                        }

                    } catch (error) {
                        def errorText = "Error cleaning gate collection: " + error.message

                        println(errorText)

                        error.printStackTrace()
                    }

                    Thread.sleep(this._cleanupIntervalSeconds * 1000)

                    // if there are no gates left, kill the cleanup job
                    if (!this._gatesByKey.keys().toList().size()) {
                        this._killCleanupFlag = true

                        if (this.logActivityFlag) {
                            println("Killing cleanup job")
                        }
                    }
                }

                this._killCleanupFlag = false
                this._cleanupRunningFlag = false

            })
        }
    }


    /**
     * Shuts the gate cleanup down
     */
    void shutdown() {
        if (this.logActivityFlag) {
            println("Shutting gate collection down")
        }

        this._shutdownFlag = true
    }


    /**
     * Sets the default expiration seconds
     * @param seconds
     */
    void setDefaultInactivitySeconds(Integer seconds) {
        this._defaultInactivitySecondsGate.acquire()

        this._defaultInactivitySeconds = seconds

        this._defaultInactivitySecondsGate.release()
    }


    /**
     * Returns the default expiration seconds
     * @return
     */
    Integer getDefaultInactivitySeconds() {
        this._defaultInactivitySecondsGate.acquire()

        def seconds = this._defaultInactivitySeconds

        this._defaultInactivitySecondsGate.release()

        return seconds
    }


    /**
     * Returns a gate for the given key
     * @param key
     * @return
     */
    Gate gateForKey(String key) {
        // if the gate doesn't exist by key
        if (!this._gatesByKey[key]) {
            def startCleanupThreadFlag = false

            // lock to create a new gate
            this._gateFactoryGate.acquire()

            try {
                // if the gate still doesn't exist by key
                if (!this._gatesByKey[key]) {
                    // create a new gate
                    def gate = new Gate(key)

                    if (this.defaultInactivitySeconds != null) {
                        gate.inactivitySeconds = this.defaultInactivitySeconds
                    }

                    // store the gate by key
                    this._gatesByKey[key] = gate

                    // if the cleanup routine isn't running, start it
                    if (!this._cleanupRunningFlag) {
                        startCleanupThreadFlag = true
                    }
                }

            } catch (error) {
                def errorText = "Error creating new gate: " + error.message

                println(errorText)

                error.printStackTrace()
            }

            // unlock
            this._gateFactoryGate.release()

            // if we're starting a cleanup thread
            if (startCleanupThreadFlag) {
                this._startCleanupThread()
            }
        }

        return this._gatesByKey[key]
    }



}
