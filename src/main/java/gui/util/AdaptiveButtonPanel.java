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
package gui.util;



import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.FlowLayout;

/**
 * Ein JPanel, das OK- und Cancel-Buttons in der korrekten
 * Look-and-Feel-abhängigen Reihenfolge anordnet (z.B. Ok|Cancel vs. Cancel|Ok).
 */
public class AdaptiveButtonPanel extends JPanel {

    // Die Buttons als public final deklarieren, damit sie 
    // im NetBeans-Designer einfach zugänglich sind und bearbeitet werden können.
    public final JButton okButton = new JButton("OK");
    public final JButton cancelButton = new JButton("Abbrechen");

    /**
     * Konstruktor, der das Layout initialisiert und die Buttons anordnet.
     */
    public AdaptiveButtonPanel() {
        // FlowLayout mit rechter Ausrichtung (FlowLayout.RIGHT), um die Buttons
        // an den rechten Rand des Dialogs zu verschieben.
        super(new FlowLayout(FlowLayout.RIGHT, 5, 5)); 
           okButton.putClientProperty("JButton.buttonType", "ok");
    cancelButton.putClientProperty("JButton.buttonType", "cancel");
        okButton.putClientProperty("action-role", "accept");
    cancelButton.putClientProperty("action-role", "cancel");

    // Optional: Falls Sie einen Delete-Button hätten
    // deleteButton.putClientProperty("action-role", "destructive-action");
    
        // Buttons direkt nach dem Erzeugen anordnen
        setupButtonOrder();
    }
    
    /**
     * Bestimmt die LaF-spezifische Reihenfolge und fügt die Buttons hinzu.
     */
    private void setupButtonOrder() {
        // Standardmäßig: Windows/Metal (OK dann Cancel)
        boolean isCancelOkOrder = false;
        
        // 1. UIManager-Wert abfragen (dies ist die robusteste Methode)
        Object orientation = UIManager.get("OptionPane.buttonOrientation");
        
        if (orientation instanceof Integer) {
            // Integer 1 bedeutet Left-to-Right Button Order für Mac/Aqua (Cancel | OK)
            isCancelOkOrder = ((Integer) orientation) > 0; 
        }
        
        // 2. Buttons in der korrekten Reihenfolge hinzufügen
        this.removeAll(); // Zur Sicherheit

        if (isCancelOkOrder) {
            // macOS/Aqua-Konvention: Cancel links, OK rechts
            this.add(cancelButton);
            this.add(okButton);
        } else {
            // Windows/Metal/Nimbus-Konvention: OK links, Cancel rechts
            this.add(okButton);
            this.add(cancelButton);
        }
        
        this.revalidate();
        this.repaint();
    }
    
    // Optional: Methoden zum Ändern des Textes, falls im Designer gewünscht
    public String getOkButtonText() { return okButton.getText(); }
    public void setOkButtonText(String text) { okButton.setText(text); }
    
    public String getCancelButtonText() { return cancelButton.getText(); }
    public void setCancelButtonText(String text) { cancelButton.setText(text); }
}