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

        // Nachricht formatieren
        String msg = String.format(fmt, args);
        // LogRecord erstellen
        LogRecord record = new LogRecord(level, msg);
        
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    // stack[0] = getStackTrace, stack[1] = logf, stack[2] = aufrufende Methode
    if (stack.length > 3) {
        record.setSourceClassName(stack[3].getClassName());
        record.setSourceMethodName(stack[3].getMethodName());
    }
        // simLogger.log(record);
         logger.log(record);

        //simLogger.log(level, String.format(fmt, args));

    }

    public static void info(String fmt, Object... args) {
        logf(simLogger, INFO, fmt, args);
    }

    public static void debug(String fmt, Object... args) {
        logf(simLogger, FINE, fmt, args);
    }
    
    public static void error (String fmt, Object... args){
         logf(simLogger, SEVERE, fmt, args);
    }

    static boolean dbg = true;
    static boolean info = true;

    static void dbg(String s) {
        if (!dbg) {
            return;
        }
        System.out.print("DBG: ");
        System.out.println(s);
    }

    static void info(String s) {
        if (!info) {
            return;
        }
        System.out.print("INFO: ");
        System.out.println(s);
    }
}
