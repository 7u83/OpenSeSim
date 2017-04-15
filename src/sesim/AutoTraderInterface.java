/*
 * Copyright (c) 2017, 7u83 <7u83@mail.ru>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package sesim;

import javax.swing.JDialog;
import org.json.JSONObject;

/**
 * Interface for auto traders
 * @author 7u83 <7u83@mail.ru>
 */
public interface AutoTraderInterface {

    public abstract boolean getDevelStatus();

    public abstract String getDisplayName();

    /**
     * Get a graphical user interface to configure the auto trader.
     * @return an AutoTraderGui object or null if there is no graphical
     * user interface available.
     */
    public abstract AutoTraderGui getGui();

    public abstract JDialog getGuiConsole();

    /**
     * Get current configuration of the auto trader as JSON object.
     * @return JSONObject containing the configuration
     */
    public abstract JSONObject getConfig();

    /**
     * Set the configuration for auto trader by a JSON object.
     * @param cfg the configuration
     */
    public abstract void putConfig(JSONObject cfg);

    /**
     * Return the name of the auto trader.
     * @return name
     */
    public abstract String getName();

    /**
     * Initialize the auto trader
     * @param se Exechange to trade on
     * @param id 
     * @param name Name of auto trader
     * @param money Money 
     * @param shares Number of shares
     * @param cfg 
     */
    public void init(Exchange se, long id, String name, double money, double shares, JSONObject cfg);

    public Exchange.Account getAccount();

    public void start();

}
