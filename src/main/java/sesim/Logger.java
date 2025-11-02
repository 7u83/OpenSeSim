package sesim;

import java.util.logging.Level;
import static java.util.logging.Level.*;
import java.util.logging.LogRecord;

public class Logger {

    static public final String NAME = "de.uniadmin.sesim";

    static private final java.util.logging.Logger simLogger
            = java.util.logging.Logger.getLogger(NAME);

    public static java.util.logging.Logger getLogger() {
        return simLogger;
    }

    private static void logf(java.util.logging.Logger logger, Level level, String fmt, Object... args) {
        if (!logger.isLoggable(level)) {
            return;
        }

        // Format the meesage
        String msg = String.format(fmt, args);
        // Create log record erstellen
        LogRecord record = new LogRecord(level, msg);

        logger.log(record);

        //simLogger.log(level, String.format(fmt, args));
    }

    public static void info(String fmt, Object... args) {
        logf(simLogger, INFO, fmt, args);
    }

    public static void debug(String fmt, Object... args) {
        logf(simLogger, FINE, fmt, args);
    }

    public static void error(String fmt, Object... args) {
        logf(simLogger, SEVERE, fmt, args);
    }

}
