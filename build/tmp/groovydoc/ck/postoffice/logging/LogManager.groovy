package ck.postoffice.logging

class LogManager {


    /**
     * When a log entry is created
     */
    Closure onLogEntry = null



    /**
     * Writes an Info log entry
     */
    void info(String message) {
        this.log(LogEntryType.INFO, message)
    }


    /**
     * Writes a Warning log entry
     */
    void warning(String message) {
        this.log(LogEntryType.WARNING, message)
    }


    /**
     * Writes an Error log entry
     */
    void error(String message) {
        this.log(LogEntryType.ERROR, message)
    }


    /**
     * Writes a generic log entry
     * @param logEntryType
     */
    void log(Integer logEntryType, String message) {
        def logEntry = new LogEntry()
        logEntry._logEntryType = logEntryType
        logEntry._message = message

        this.onLogEntry(logEntry)
    }


}
