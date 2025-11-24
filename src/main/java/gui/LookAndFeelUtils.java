/*
 * Copyright (c) 2017, 2025 7u83 <7u83@mail.ru>
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

import java.awt.Window;
import javax.swing.*;

public class LookAndFeelUtils {
    
    public static boolean applyLookAndFeel(String lafNameOrClassName) {
        try {
            // 1. Einfach das LAF setzen
            String className = findClassNameForLAF(lafNameOrClassName);
            UIManager.setLookAndFeel(className);
            
            // 2. Nur die Basics aktualisieren
            updateBasicUI();
            
            // 3. Speichern
            Globals.prefs_new.put("laf", lafNameOrClassName);
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Fehler beim LookAndFeel-Wechsel: " + e.getMessage(), 
                "Fehler", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private static void updateBasicUI() {
        // Nur die offensichtlichen Fenster aktualisieren
        for (Window window : Window.getWindows()) {
            if (window.isDisplayable()) {
                SwingUtilities.updateComponentTreeUI(window);
                
                // Menüleiste separat aktualisieren
                if (window instanceof JFrame) {
                    JMenuBar menuBar = ((JFrame) window).getJMenuBar();
                    if (menuBar != null) {
                        SwingUtilities.updateComponentTreeUI(menuBar);
                    }
                }
            }
        }
        
        // Popup Factory zurücksetzen
        try {
            PopupFactory.setSharedInstance(new PopupFactory());
        } catch (Exception e) {
            // Ignorieren falls fehlschlägt
        }
    }
    
    private static String findClassNameForLAF(String lafName) {
        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo info : lafInfo) {
            if (info.getName().equals(lafName)) {
                return info.getClassName();
            }
        }
        return lafName;
    }
}