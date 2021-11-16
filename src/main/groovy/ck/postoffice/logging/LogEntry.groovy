package ck.postoffice.logging

class LogEntry {


    /**
     * Log entry date
     */
    protected Date _logDate = new Date()


    /**
     * Log entry date
     * @return
     */
    Date getLogDate() {
        return this._logDate
    }


    /**
     * Log entry type
     */
    protected Integer _logEntryType = LogEntryType.INFO


    /**
     * Log entry type
     * @return
     */
    Integer getLogEntryType() {
        return this._logEntryType
    }


    /**
     * Log entry type description
     * @return
     */
    String getLogEntryTypeDesc() {
        def typeDesc = ""

        switch (this._logEntryType) {
            case LogEntryType.INFO:
                typeDesc = "Info"
                break

            case LogEntryType.WARNING:
                typeDesc = "Warning"
                break

            case LogEntryType.ERROR:
                typeDesc = "Error"
                break
        }

        return typeDesc
    }



    /**
     * Log message
     */
    protected String _message = null


    /**
     * Log message
     * @return
     */
    String getMessage() {
        return this._message
    }


    /**
     * Returns a JSON representation of the object
     * @return
     */
    Map<String, Object> json() {
        def json = ([
                logDate: this._logDate,
                logEntryType: this._logEntryType,
                logEntryTypeDesc: this.logEntryTypeDesc,
                message: this.message
        ] as Map<String, Object>)

        return json
    }


    /**
     * Writes the log out as a string
     * @return
     */
    String toString() {
        return this.message
    }



}
