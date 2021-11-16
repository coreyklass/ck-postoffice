package ck.postoffice.rabbit.util.roundRobin

import ck.postoffice.rabbit.util.gate.Gate

class RoundRobin {


    private def _indexGate = new Gate("IndexGate")



    private Integer _currentIndex = null

    Integer getCurrentIndex() {
        return this._currentIndex
    }



    private Integer _minValue = null


    Integer getMinValue() {
        return this._minValue
    }



    private Integer _maxValue = null

    Integer getMaxValue() {
        return this._maxValue
    }




    /**
     * Constructor
     * @param minValue
     * @param maxValue
     */
    RoundRobin(Integer minValue, Integer maxValue) {
        this._minValue = minValue
        this._maxValue = maxValue

        // lock the gate
        this._indexGate.acquire()

        // pull a random value to init the index
        def initialIndex = (Math.ceil(Math.random() * 1000) % (maxValue - minValue)) + minValue
        this._currentIndex = initialIndex

        // unlock the gate
        this._indexGate.release()
    }



    /**
     * Increments the index and returns it
     * @return
     */
    Integer nextIndex() {
        this._indexGate.acquire()

        // increment the index
        def returnIndex = this._currentIndex + 1

        // if the new index is too large, reset it
        if (returnIndex > this._maxValue) {
            returnIndex = this._minValue
        }

        this._currentIndex = returnIndex

        this._indexGate.release()

        return returnIndex
    }




}
