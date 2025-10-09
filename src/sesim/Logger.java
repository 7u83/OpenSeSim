package sesim;


import java.util.logging.Level;
import static java.util.logging.Level.*;

public class Logger {

    static public final String NAME = "de.uniadmin.sesim";

    static private final java.util.logging.Logger simLogger
            = java.util.logging.Logger.getLogger(NAME);

    public static void logf(java.util.logging.Logger logger, Level level, String fmt, Object... args) {
        if (logger.isLoggable(level)) {
            simLogger.log(level, String.format(fmt, args));
        }
    }

    public static void info(String fmt, Object... args) {
        logf(simLogger, INFO, fmt, args);
    }

    public static void debug(String fmt, Object... args) {
        logf(simLogger, FINE, fmt, args);
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
