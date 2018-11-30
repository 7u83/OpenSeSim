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
package opensesim.old_sesim;

import opensesim.gui.Globals;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class AutoTraderLoader extends SeSimClassLoader<AutoTraderInterface> {

    private ArrayList<Class<AutoTraderInterface>> traders_cache = null;

    public AutoTraderLoader(ArrayList<String> pathlist) {
        super(AutoTraderInterface.class, pathlist);
    }

    private ClassLoader cl;

    @Override
    public AutoTraderInterface newInstance(Class<?> cls) {
        //      ClassLoader cur = Thread.currentThread().getContextClassLoader();
        //      Thread.currentThread().setContextClassLoader(cl);

        AutoTraderInterface ai;

        try {
            ai = (AutoTraderInterface) cls.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {

            Logger.getLogger(AutoTraderLoader.class.getName()).log(Level.SEVERE, null, ex);
            ai = null;
        }
        //      Thread.currentThread().setContextClassLoader(cur);

        return ai;
    }


    
    /**
     * Get a list of all traders found in class path
     *
     * @return List of traders
     */
    public ArrayList<Class<AutoTraderInterface>> getInstalledTraders() {

        return getInstalledClasses(new ArrayList<String>());

    }

    public ArrayList<String> getDefaultStrategyNames(boolean devel) {
        ArrayList<Class<AutoTraderInterface>> trclasses;
        //    trclasses = this.getInstalledTraders();

        ArrayList<String> ret = new ArrayList<>();
        trclasses = getInstalledTraders();

        for (int i = 0; i < trclasses.size(); i++) {
            try {

                //AutoTraderInterface ac = trclasses.get(i).newInstance();
                AutoTraderInterface ac = this.newInstance(trclasses.get(i));

                if (ac.getDevelStatus() && devel == false) {
                    continue;
                }
                ret.add(ac.getClass().getCanonicalName());
            } catch (Exception e) {
                System.out.printf("Can't load \n");

            }

        }

        return ret;
    }

    public ArrayList<String> getDefaultStrategyNames() {
        return this.getDefaultStrategyNames(true);
    }

    public AutoTraderInterface getAutoTraderInterface(String name) {
        ArrayList<Class<AutoTraderInterface>> traders = this.getInstalledTraders();
        for (int i = 0; i < traders.size(); i++) {
            try {

                if (!name.equals(traders.get(i).getCanonicalName())) {
                    //     System.out.printf("Contnue trader\n");
                    continue;
                }
                //          System.out.printf("Canon name %s\n", traders.get(i).getCanonicalName());
                //          System.exit(0);

                //        Globals.LOGGER.info(String.format("Making lll instance of %s", traders.get(i).getCanonicalName()));
                //     if (traders.get(i)==null){
                //         Globals.LOGGER.info("We have null");
                //     }
//                AutoTraderInterface ac = traders.get(i).newInstance();
                AutoTraderInterface ac = newInstance(traders.get(i));

                return ac;
                //       System.out.printf("Looking for in %s == %s\n", ac.getClass().getCanonicalName(), name);

                //     if (ac.getClass().getCanonicalName().equals(name)) {
                //         return ac;
                // if (ac.getDisplayName().equals(name)) {
                //     return ac;}
                //   }
            } catch (Exception ex) {
                Globals.LOGGER.info(String.format("Instance failed %s", ex.getMessage()));
            }
        }

        return null;
    }

}
