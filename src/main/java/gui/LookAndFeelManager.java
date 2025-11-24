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
import javax.swing.*;
import java.awt.*;


/**
 *
 * @author tube
 */


public class LookAndFeelManager {
    
    public static boolean changeLookAndFeel(String lafClassName) {
        LookAndFeel oldLookAndFeel=null;
        try {
            // Altes LAF speichern für Fallback
            oldLookAndFeel = UIManager.getLookAndFeel();
            
            // Neues LAF setzen
            UIManager.setLookAndFeel(lafClassName);
            
            // Alle vorhandenen Fenster aktualisieren
            updateAllWindows();
            
            // Spezialbehandlung für Popup-Menüs
            updatePopupMenus();
            
            // In Preferences speichern
            Globals.prefs_new.put("laf", lafClassName);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: Altes LAF wiederherstellen
            try {
                UIManager.setLookAndFeel(oldLookAndFeel);
                updateAllWindows();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    
    private static void updateAllWindows() {
        // Alle offenen Fenster durchgehen
        for (Window window : Window.getWindows()) {
            if (window.isDisplayable()) {
                SwingUtilities.updateComponentTreeUI(window);
                
                // Fenster neu zeichnen
                window.repaint();
                
                // Für JFrames und JDialoge spezielle Behandlung
                if (window instanceof JFrame) {
                    updateFrame((JFrame) window);
                } else if (window instanceof JDialog) {
                    updateDialog((JDialog) window);
                }
            }
        }
    }
    
    private static void updateFrame(JFrame frame) {
        // Menüleiste aktualisieren (wichtig!)
        JMenuBar menuBar = frame.getJMenuBar();
        if (menuBar != null) {
            SwingUtilities.updateComponentTreeUI(menuBar);
        }
        
        // Fensterdekoration aktualisieren
        frame.getRootPane().updateUI();
    }
    
    private static void updateDialog(JDialog dialog) {
        // Menüleiste aktualisieren falls vorhanden
        JMenuBar menuBar = dialog.getJMenuBar();
        if (menuBar != null) {
            SwingUtilities.updateComponentTreeUI(menuBar);
        }
        
        // Dialogdekoration aktualisieren
        dialog.getRootPane().updateUI();
    }
    
    private static void updatePopupMenus() {
        // PopupFactory zurücksetzen (wichtig für Popup-Menüs)
        PopupFactory.setSharedInstance(new PopupFactory());
        
        // Eventuell vorhandene Popup-Menüs aktualisieren
        // Hier müsstest du ggf. eigene Referenzen zu Popup-Menüs verwalten
    }
}