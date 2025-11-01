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

import javax.help.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class CustomHelpHandler {

    private static JHelp helpViewer;
    // private static JFrame helpFrame;
    private static JDialog helpDialog;
    private static boolean firstTime = true;

    public static void installHelp(final JFrame mainFrame, HelpSet hs) {
        // JHelp-Komponente erstellen
        helpViewer = new JHelp(hs);

        // Eigenes JFrame für Hilfe
        /*      helpFrame = new JFrame("Hilfe");
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.getContentPane().add(new JScrollPane(helpViewer));

        // Größe setzen (anpassen nach Bedarf)
        helpFrame.setSize(800, 600);*/
        // 2. JDialog erstellen (immer über mainFrame)
        helpDialog = new JDialog(mainFrame, "OpenSeSim Help", false); // false = nicht modal
        helpDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
     //   helpDialog.getContentPane().add(new JScrollPane(helpViewer));
      helpDialog.getContentPane().add(helpViewer);
        helpDialog.setSize(800, 600);
        // Zentriert über dem Hauptfenster

        // Immer im Vordergrund
        //  helpFrame.setAlwaysOnTop(true);
        // F1-Taste global für das Hauptfenster registrieren
        InputMap inputMap = mainFrame.getRootPane().getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mainFrame.getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "showHelp");
        actionMap.put("showHelp", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showHelpCentered(mainFrame);
            }
        });
    }

    private static void showHelpCentered(JFrame parent) {
        if (helpDialog == null) {
            return;
        }

        if (helpDialog.isVisible()) {
            helpDialog.toFront();
            helpDialog.requestFocus();
            return;
        }

        if (firstTime) {
            helpDialog.setLocationRelativeTo(parent);
            firstTime = false;
        }
        helpDialog.setVisible(true);
        
        // Startseite öffnen
        try {
         //   helpViewer.setCurrentID("intro");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
