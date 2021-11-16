package ck.postoffice.rabbit.envelope

import java.text.SimpleDateFormat

class RabbitEnvelopeSerializer {

    private def _utcTimeZone = TimeZone.getTimeZone("UTC")

    private def _jsonDateTimeMask = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    private SimpleDateFormat _jsonDateFormatter = null


    private static def _simpleTypeBoolean = "boolean"
    private static def _simpleTypeDate = "date"
    private static def _simpleTypeNumber = "number"
    private static def _simpleTypeString = "string"
    private static def _simpleTypeUnknown = "unknown"
    private static def _simpleTypeNull = "null"


    private static def _keySimpleType = "simpleType"
    private static def _keyJavaClass = "javaClass"
    private static def _keyValue = "value"





    /**
     * Converts a date to JSON-compatible date
     * @return
     */
    String toJsonDate(Date date) {
        if (!this._jsonDateFormatter) {
            this._jsonDateFormatter = new SimpleDateFormat(this._jsonDateTimeMask)
            this._jsonDateFormatter.setTimeZone(this._utcTimeZone)
        }

        def jsonDate = this._jsonDateFormatter.format(date)

        return jsonDate
    }




    /**
     * Serializes the envelope
     * @param envelope
     * @return
     */
    String serialize(RabbitEnvelopeBase envelope) {
        def envelopeJson = ([:] as Map<String, Object>)
    }




    /**
     * Serializes an object
     * @param object
     * @return
     */
    Object serializeObject(Object object) {
        Object returnObject = null

        if (object instanceof Map) {
            def map = (object as Map<String, Object>)

            returnObject = this.serializeMap(map)

        } else if (object instanceof List) {
            def list = (object as List<Object>)

            returnObject = this.serializeList(list)

        } else if (object instanceof Collection) {
            def collection = (object as Collection<Object>)

            returnObject = this.serializeList(collection.toList())

        } else if (object instanceof Set) {
            def set = (object as Set<Object>)

            returnObject = this.serializeList(set.toList())

        } else {
            returnObject = this.serializeSimpleValue(object)
        }

        return returnObject
    }




    /**
     * Serializes a List
     * @param list
     * @return
     */
    List<Object> serializeList(List<Object> list) {
        def serializedList = ([] as List<Object>)

        list.each { Object indexObject ->
            serializedList.push(this.serializeObject(indexObject))
        }

        return serializedList
    }


    /**
     * Serializes a Map
     * @param map
     * @return
     */
    Map<String, Object> serializeMap(Map<String, Object> map) {
        def serializedMap = ([:] as Map<String, Object>)

        map.each { String indexKey, Object indexObject ->
            serializedMap[indexKey] = this.serializeObject(indexObject)
        }

        return serializedMap
    }


    /**
     * Serializes a simple value
     * @param object
     * @return
     */
    Map<String, Object> serializeSimpleValue(Object object) {
        // where we're storing the metadata
        def valueMetaData = ([:] as Map<String, Object>)

        // if the object is NULL
        if (object == null) {
            valueMetaData[_keySimpleType] = _simpleTypeNull

        // if the object is NOT NULL
        } else {
            // store the java class
            valueMetaData[_keyJavaClass] = (object.class?.name ?: "")

            // determine the simple type
            def simpleType = ""
            def simpleValue = null

            if (object instanceof Boolean) {
                simpleType = _simpleTypeBoolean
                simpleValue = (object as Boolean).toString()

            } else if (object instanceof Date) {
                simpleType = _simpleTypeDate
                simpleValue = this.toJsonDate(object)

            } else if (object instanceof Number) {
                simpleType = _simpleTypeNumber
                simpleValue = (object as Number).toString()

            } else if (object instanceof String) {
                simpleType = _simpleTypeString
                simpleValue = (object as String).toString()

            } else {
                simpleType = _simpleTypeUnknown
                simpleValue = object.toString()
            }

            valueMetaData[_keySimpleType] = simpleType
            valueMetaData[_keyValue] = simpleValue
        }

        return valueMetaData
    }




}
