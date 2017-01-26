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
package gui;

import chart.NewMDIApplication;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import sesim.AutoTraderLoader;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Globals {

    static public sesim.Exchange se;

    static public Preferences prefs;

    static void setLookAndFeel(String selected) {

        try {
            String look = "com.seaglasslookandfeel.SeaGlassLookAndFeel";
            Class.forName(look);
            UIManager.installLookAndFeel("Sea Glass", look);
        } catch (ClassNotFoundException e) {
        }

        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo lafInfo1 : lafInfo) {
            if (lafInfo1.getName().equals(selected)) {
                String lafClassName = lafInfo1.getClassName();
                try {
                    UIManager.setLookAndFeel(lafClassName);
                    //  UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
                    break;
                } catch (Exception e) {

                }
            }
        }
    }
    
    static AutoTraderLoader tloader=new AutoTraderLoader();
    
    

}
