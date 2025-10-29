package sesim;

import javax.help.*;
import java.net.URL;

public class AppHelp {
    private static HelpSet hs;
    private static HelpBroker hb;

    static {
        try {
            URL hsURL = AppHelp.class.getResource("/help/help.hs");
            hs = new HelpSet(null, hsURL);
            hb = hs.createHelpBroker();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HelpSet getHelpSet() { return hs; }
    public static HelpBroker getHelpBroker() { return hb; }
}
