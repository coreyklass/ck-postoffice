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

        } else if (object != null) {
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
        def valueDetail = ([:] as Map<String, Object>)
        valueDetail["javaClass"] = (object.class.name ?: "")

        // determine the simple type
        def simpleType = ""
        def simpleValue = null

        if (object instanceof Boolean) {
            simpleType = _simpleTypeBoolean

        } else if (object instanceof Date) {
            simpleType = _simpleTypeDate
            simpleValue = this.toJsonDate(object)

        } else if (object instanceof Number) {
            simpleType = _simpleTypeNumber

        } else if (object instanceof String) {
            simpleType = _simpleTypeString

        } else {
            simpleValue = object.toString()
        }

        valueDetail["valueType"] = simpleType
        valueDetail["value"] = simpleValue

        return valueDetail
    }




}
