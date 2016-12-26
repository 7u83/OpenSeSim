package SeSim;

public class Logger {

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
