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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import org.json.JSONArray;
import org.json.JSONObject;
import sesim.Config;
//import sesim.AutoTraderConfig;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class EditAutoTraderList extends javax.swing.JPanel {

    private String getColumnHeader(int i) {
        JTableHeader th = list.getTableHeader();
        return (String) th.getColumnModel().getColumn(i).getHeaderValue();

    }

    void save() {
        // 1. Hole den Zell-Editor der JTable
        TableCellEditor editor = list.getCellEditor();

        // 2. Prüfe, ob aktuell eine Zelle bearbeitet wird
        if (editor != null) {
            // 3. Stoppe die Bearbeitung und zwinge die Tabelle, den neuen Wert 
            //    in das TableModel zu übernehmen.
            editor.stopCellEditing();
        }

        DefaultTableModel m = (DefaultTableModel) list.getModel();
        JTableHeader th = list.getTableHeader();
        /*
        for (int i = 0; i < model.getColumnCount(); i++) {
            String hw = (String) th.getColumnModel().getColumn(i).getHeaderValue();
        }
         */
        JSONArray ja = new JSONArray();

        for (int i = 0; i < m.getRowCount(); i++) {
            JSONObject jo = new JSONObject();

            Object cw;
            cw = m.getValueAt(i, NAME_COL);
            jo.put("Name", (String) cw);

            cw = m.getValueAt(i, COUNT_COL);
            jo.put("Count", (Integer) cw);

            cw = m.getValueAt(i, STRATEGY_COL);
            jo.put("Strategy", (String) cw);

            cw = m.getValueAt(i, CASH_COL);
            jo.put("Cash", (Double) cw);

            cw = m.getValueAt(i, SHARES_COL);
            jo.put("Shares", (Double) cw);

            cw = m.getValueAt(i, COLOR_COL);
            if (cw == null) {
                jo.put("Color", new JSONArray());
            } else {
                int ca[] = new int[3];

                ca[0] = ((Color) cw).getRed();
                ca[1] = ((Color) cw).getGreen();
                ca[2] = ((Color) cw).getBlue();

                jo.put("Color", new JSONArray(ca));
            }

            cw = m.getValueAt(i, EXCLUDEINITIAL_COL);
            jo.put("ExcludeInitial", (Boolean) cw);

            cw = m.getValueAt(i, ENABLED_COL);
            jo.put("Enabled", (Boolean) cw);

            /*            for (int x = 0; x < m.getColumnCount(); x++) {
                Object cw = m.getValueAt(i, x);

                //System.out.printf("CWSaver: %s\n",cw.getClass().toString());
                if (cw != null) {
                    jo.put((String) th.getColumnModel().getColumn(x).getHeaderValue(), cw.toString());
                }

            } */
            ja.put(jo);
        }

//        Globals.prefs.put(Globals.PrefKeys.TRADERS, ja.toString());
// TUBE GLOB
        Globals.putTraders(ja);

    }

    static final int NAME_COL = 0;
    static final int COUNT_COL = 1;
    static final int STRATEGY_COL = 2;
    static final int CASH_COL = 3;
    static final int SHARES_COL = 4;
    static final int COLOR_COL = 5;
    static final int EXCLUDEINITIAL_COL = 6;
    static final int ENABLED_COL = 7;

    final void load() {

        JSONArray traders = Config.getTraders(Globals.getConfig());
        DefaultTableModel model = (DefaultTableModel) list.getModel();
        model.setRowCount(traders.length());

        for (int row = 0; row < traders.length(); row++) {
            JSONObject rowobj = traders.getJSONObject(row);

            String name = rowobj.optString("Name", "");
            list.getModel().setValueAt(name, row, NAME_COL);

            int count = rowobj.optInt("Count", 0);
            list.getModel().setValueAt(count, row, COUNT_COL);

            String strategy = rowobj.optString("Strategy", "");
            list.getModel().setValueAt(strategy, row, STRATEGY_COL);

            Double val = rowobj.optDouble("Cash", 0.0);
            list.getModel().setValueAt(val, row, CASH_COL);

            val = rowobj.optDouble("Shares", 0.0);
            list.getModel().setValueAt(val, row, SHARES_COL);

            JSONArray a = rowobj.optJSONArray("Color");
            Color c = null;
            if (a != null) {
                if (a.length() == 3) {
                    c = new Color(a.optInt(0, 255), a.optInt(1, 255), a.optInt(2, 255));
                }

            }
            list.getModel().setValueAt(c, row, COLOR_COL);

            boolean b = rowobj.optBoolean("ExcludeInitial", false);
            list.getModel().setValueAt(b, row, EXCLUDEINITIAL_COL);

            b = rowobj.optBoolean("Enabled", false);
            list.getModel().setValueAt(b, row, ENABLED_COL);

            /*      
            

            for (int col = 0; col < list.getColumnCount(); col++) {
                String h = this.getColumnHeader(col);
                System.out.printf("Doing stuff for %s\n", h);

                String val = null;
                try {
                    val = rowobj.getString(h);
                } catch (Exception e) {
                    continue;
                }

                //   System.out.printf("Want to set (%d,%d): %s\n", row, col, val);
                //list.getModel().setValueAt(val, row, col);
                Class cl = list.getModel().getColumnClass(col);
                //   System.out.printf("The Class is: %s\n", cl.getName());
                Object cv = new Object();
                if (cl == Float.class) {
                    cv = (float) rowobj.optDouble(h, 0);
                }
                if (cl == String.class) {
                    cv = rowobj.getString(h);
                }
                if (cl == Integer.class) {
                    cv = rowobj.getInt(h);
                }
                if (cl == Boolean.class) {
                    cv = rowobj.getBoolean(h);
                }
                if (cl == Object.class) {
                    cv = null; //rowobj.getString(h);
                }
                list.getModel().setValueAt(cv, row, col);

            } */
        }

    }

    /**
     * Löscht alle ausgewählten Zeilen aus der JTable 'list' und dem
     * zugrundeliegenden DefaultTableModel.
     */
    public void deleteSelectedRows() {
        // 1. Hole das Model und die ausgewählten Zeilen-Indizes (View-Indizes).
        DefaultTableModel model = (DefaultTableModel) list.getModel();
        int[] selectedRows = list.getSelectedRows();

        // Keine Zeilen ausgewählt
        if (selectedRows.length == 0) {
            // Optionale Meldung, falls keine Zeile ausgewählt ist
            // JOptionPane.showMessageDialog(this, "Bitte wählen Sie mindestens eine Zeile zum Löschen aus.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 2. Konvertiere View-Indizes in Model-Indizes (wichtig bei Sortierung!)
        for (int i = 0; i < selectedRows.length; i++) {
            selectedRows[i] = list.convertRowIndexToModel(selectedRows[i]);
        }

        // 3. Sortiere die Indizes absteigend und lösche von hinten nach vorne
        java.util.Arrays.sort(selectedRows); // Stellt sicher, dass die Indizes sortiert sind

        // Iteriere rückwärts, um Index-Verschiebungen zu vermeiden
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int modelRowIndex = selectedRows[i];
            model.removeRow(modelRowIndex);
        }

        // 4. Cleanup
        list.clearSelection();

        // Da das Model geändert wurde, aktualisiere die Zusammenfassung
        this.summary.setText(String.format("Initial price: %.2f", this.caclculateInitialPrice()));

        System.out.println(selectedRows.length + " Zeile(n) aus der Tabelle gelöscht.");
    }

    DefaultTableModel model;

    private float caclculateInitialPrice() {
        double money = 0.0f;
        double shares = 0.0f;

        for (int r = 0; r < model.getRowCount(); r++) {
            Boolean e = (Boolean) model.getValueAt(r, ENABLED_COL);
            if (e == null) {
                continue;
            }
            if (!e) {
                continue;
            }

            e = (Boolean) model.getValueAt(r, EXCLUDEINITIAL_COL);
            if (e == null) {
                continue;
            }
            if (e) {
                continue;
            }

            int count = 0;
            try {
                count = (int) model.getValueAt(r, COUNT_COL);
            } catch (Exception ex) {

            }

            try {
                money += (double) model.getValueAt(r, CASH_COL) * count;
            } catch (Exception ex) {
            }

            try {
                shares += (double) model.getValueAt(r, SHARES_COL) * count;
            } catch (Exception ex) {
            }

        }
        return (float)(money / shares);
    }

    /**
     * Creates new form NewJPanel
     */
    public EditAutoTraderList() {
        initComponents();

        if (Globals.sim == null) {
            return;
        }

        this.load();
//list.setEditClickCount(2);

        list.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE); // Ältere Methode
        list.setSurrendersFocusOnKeystroke(true);

        JComboBox comboBox = new JComboBox();
        //  JSpinner cashSpinner;
        JSpinner cashSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.1));

        Globals.getStrategiesIntoComboBox(comboBox);

        model = (DefaultTableModel) list.getModel();
        list.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));

        //list.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cashSpinner));
        //list.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cashSpinner));
     //   SpinnerCellEditor cashEditor = new SpinnerCellEditor(0.0, 0.0, Double.MAX_VALUE, 0.01);
     //   list.getColumnModel().getColumn(3).setCellEditor(cashEditor);

        // list.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer());
        //  model.setRowCount(3);
        ColorChooserEditor colorEditor = new ColorChooserEditor();
        ColorCellRenderer colorRenderer = new ColorCellRenderer();

        list.getColumnModel().getColumn(5).setCellEditor(colorEditor);
        list.getColumnModel().getColumn(5).setCellRenderer(colorRenderer);

        list.setRowHeight(30);

        list.getModel().addTableModelListener((TableModelEvent e) -> {
            this.summary.setText(String.format("Initial price: %.2f", this.caclculateInitialPrice()));

        });
        this.summary.setText(String.format("Initial price: %.2f", this.caclculateInitialPrice()));
    }

    void add() {
        DefaultTableModel model = (DefaultTableModel) list.getModel();
        model.setRowCount(model.getRowCount() + 1);
        model.setValueAt("New Trader", model.getRowCount() - 1, 0);
    }

    // JLabel summary = null;
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        list = new AutoTraderTable();
        summary = new javax.swing.JLabel();

        list.setAutoCreateRowSorter(true);
        list.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Count", "Strategy", "Cash", "Shares", "Color", "Exclude", "Enabled"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(list);
        if (list.getColumnModel().getColumnCount() > 0) {
            list.getColumnModel().getColumn(1).setPreferredWidth(30);
        }

        summary.setText("Text");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(summary, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 753, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(summary)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable list;
    private javax.swing.JLabel summary;
    // End of variables declaration//GEN-END:variables

    /**
     * Ein benutzerdefinierter Zell-Editor, der JSpinner für numerische Eingaben
     * verwendet.
     */
    class SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor {

        final JSpinner spinner;
        // Speichere eine Referenz auf das Textfeld
        private final JFormattedTextField textField;

        // ... (Deine Konstruktoren für Double und Integer) ...
        public SpinnerCellEditor(double initialValue, double minimum, double maximum, double stepSize) {
            this.spinner = new JSpinner(new SpinnerNumberModel(initialValue, minimum, maximum, stepSize));
            // NEUE LOGIK FÜR EINMALIGES ENTER          // Initialisiere die Textfeld-Referenz hier
            this.textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();

            addEnterListener();

        }

        public SpinnerCellEditor(int initialValue, int minimum, int maximum, int stepSize) {
            this.spinner = new JSpinner(new SpinnerNumberModel(initialValue, minimum, maximum, stepSize));
            // NEUE LOGIK FÜR EINMALIGES ENTER
            // Initialisiere die Textfeld-Referenz hier
            this.textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
            addEnterListener();
        }

        // NEUE HILFSMETHODE
        private void addEnterListener() {
            // Der eigentliche Editor für die Texteingabe ist das Textfeld des Spinners
            JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();

            // Füge einen KeyListener hinzu
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        // 1. Zuerst muss der Wert des Textfelds im Spinner-Model aktualisiert werden.
                        //    Dies geschieht automatisch, wenn wir stopEdit manuell aufrufen.

                        try {
                            // Wichtig: Diese Zeile forciert den Spinner, den getippten Wert zu akzeptieren.
                            spinner.commitEdit();
                        } catch (java.text.ParseException ex) {
                            // Ignoriere oder behandle Parsing-Fehler, falls der Wert ungültig ist.
                        }

                        // 2. Dann beende die Bearbeitung der JTable-Zelle.
                        fireEditingStopped();

                        // Verhindere, dass das Event weitergegeben wird, um ungewolltes Verhalten 
                        // der JTable zu vermeiden (z.B. Wechsel zur nächsten Zelle).
                        e.consume();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            spinner.setValue(value);

            // NEUE LOGIK: Fokus setzen und Text markieren
            // Wir verwenden invokeLater, da der Editor möglicherweise noch nicht vollständig
            // in der Komponenten-Hierarchie sichtbar ist, wenn diese Methode zurückkehrt.
            SwingUtilities.invokeLater(() -> {
                textField.requestFocusInWindow();
                textField.selectAll();
            });

            return spinner;
        }

        /*    @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            spinner.setValue(value); 
            return spinner;
        }*/
        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }

    /**
     * Ein benutzerdefinierter Zell-Editor, der JSpinner für numerische Eingaben
     * verwendet.
     */
    class old2SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor {

        final JSpinner spinner;

        // Konstruktor für Double-Werte (Cash, Shares)
        public old2SpinnerCellEditor(double initialValue, double minimum, double maximum, double stepSize) {
            this.spinner = new JSpinner(new SpinnerNumberModel(initialValue, minimum, maximum, stepSize));
        }

        // Konstruktor für Integer-Werte (Count) - Optional, aber gut für Count-Spalte
        public old2SpinnerCellEditor(int initialValue, int minimum, int maximum, int stepSize) {
            this.spinner = new JSpinner(new SpinnerNumberModel(initialValue, minimum, maximum, stepSize));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Setze den Wert des Spinners auf den aktuellen Zellenwert
            spinner.setValue(value);
            return spinner;
        }

        @Override
        public Object getCellEditorValue() {
            // Gib den Wert des Spinners zurück. 
            // Die JTable kümmert sich um die Typumwandlung (Float.class/Integer.class), 
            // aber es wird ein Number-Objekt zurückgegeben.
            return spinner.getValue();
        }
    }

    /**
     * Benutzerdefinierter Zell-Editor, der einen JColorChooser beim Klick
     * öffnet. Speichert das gewählte Color-Objekt.
     */
    class ColorChooserEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private Color currentColor;
        private final JButton button;
        private static final String EDIT = "edit";

        public ColorChooserEditor() {
            button = new JButton();
            button.setActionCommand(EDIT);
            button.addActionListener(this);
            button.setBorderPainted(false);
            button.setOpaque(true);
        }

        @Override
        public Object getCellEditorValue() {
            // Gibt das Color-Objekt oder null zurück, wenn der Dialog abgebrochen wurde
            return currentColor;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // value ist entweder ein Color-Objekt oder null
            currentColor = (Color) value;

            // Setze die Hintergrundfarbe des Buttons für die Bearbeitung
            if (currentColor != null) {
                button.setBackground(currentColor);
            } else {
                // Bei keiner gewählten Farbe den Standard-Hintergrund verwenden
                button.setBackground(UIManager.getColor("Button.background"));
            }
            return button;
        }

        //   @Override
        public void old_actionPerformed(ActionEvent e) {
            if (EDIT.equals(e.getActionCommand())) {
                // Öffne den Farbwähler-Dialog
                Color initialColor = (currentColor != null) ? currentColor : Color.WHITE;
                Color newColor = JColorChooser.showDialog(
                        button,
                        "Wähle eine Farbe",
                        initialColor);

                if (newColor != null) {
                    currentColor = newColor; // Farbe gewählt
                } else if (newColor == null && currentColor != null) {
                    // Optional: Wenn der Benutzer 'Abbrechen' klickt, behalte die alte Farbe.
                    // Wenn du die Farbwahl zurücksetzen willst, setze currentColor = null;
                }

                // Beende die Bearbeitung, damit der Renderer den neuen Wert anzeigt
                fireEditingStopped();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (EDIT.equals(e.getActionCommand())) {

                // Wir stoppen die Bearbeitung nicht sofort, sondern erst nach dem Dialog.
                // Erzeuge eine AtomicReference, um die gewählte Farbe zu speichern (null ist möglich)
                AtomicReference<Color> selectedColorRef = new AtomicReference<>(null);

                // --- 1. Erzeuge den JColorChooser
                JColorChooser chooser = new JColorChooser(currentColor != null ? currentColor : Color.WHITE);

                // --- 2. Definiere den OK/Cancel-Handler (Standard-Verhalten)
                ActionListener okListener = evt -> {
                    selectedColorRef.set(chooser.getColor()); // Speichere die gewählte Farbe
                };
                ActionListener cancelListener = evt -> {
                    // Bleibt null, wenn Abbrechen geklickt wird
                };

                // --- 3. Erzeuge den Reset-Handler für "Default Color"
                ActionListener resetListener = evt -> {
                    selectedColorRef.set(null); // Setze die Farbe explizit auf null

                    // Schließe den Dialog, nachdem die Farbe auf null gesetzt wurde
                    ((JDialog) ((JButton) evt.getSource()).getTopLevelAncestor()).dispose();
                };

                // --- 4. Erstelle den Dialog (statt showDialog)
                JDialog dialog = JColorChooser.createDialog(
                        button,
                        "Wähle oder setze Standardfarbe",
                        true, // Modal
                        chooser,
                        okListener,
                        cancelListener);

                // --- 5. Füge den Reset-Button hinzu
                // Wir suchen die Button-Pane und fügen dort den Button ein (kann je nach LookAndFeel variieren)
                // Die sicherste Methode ist, den Dialog-Header zu finden oder einen eigenen Container zu verwenden.
                // Hier fügen wir ihn der Content Pane des Dialogs hinzu (kann Layout-Probleme verursachen)
                // Für eine einfache Lösung suchen wir die Button-Leiste des Dialogs:
                // Suche die Pane, die die OK/Cancel-Buttons enthält (Dies ist HEURISTISCH!)
                Component[] components = dialog.getContentPane().getComponents();
                if (components.length > 0 && components[components.length - 1] instanceof java.awt.Container) {
                    java.awt.Container buttonBar = (java.awt.Container) components[components.length - 1];

                    JButton resetButton = new JButton("Default Color (NULL)");
                    resetButton.addActionListener(resetListener);

                    // Füge den Reset-Button in die Button-Leiste ein
                    buttonBar.add(resetButton, 0); // Füge ihn ganz links ein
                }

                // --- 6. Dialog anzeigen und warten
                dialog.setVisible(true);

                // --- 7. Ergebnis verarbeiten
                Color newColor = selectedColorRef.get();

                if (newColor != null || selectedColorRef.get() == null) {
                    // Update currentColor entweder mit der gewählten Farbe oder mit null (vom Reset-Button)
                    currentColor = newColor;
                }

                // Beende die Bearbeitung und speichere den neuen Wert (inkl. null) im Model
                fireEditingStopped();
            }
        }

    }

    /**
     * Benutzerdefinierter Zell-Renderer, der die Zelle in der gespeicherten
     * Farbe einfärbt. Zeigt die gewählte Farbe an, es sei denn, es ist keine
     * Farbe (null) gewählt.
     */
    class ColorCellRenderer extends DefaultTableCellRenderer {

        // Die Standard-Hintergrundfarbe der Tabelle, wenn keine Farbe gewählt ist
        private final Color DEFAULT_TABLE_BACKGROUND = UIManager.getColor("Table.background");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            // 1. Hole die Standard-Komponente für die Zelle
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // 2. Initialisiere die Darstellung
            JLabel label = (JLabel) component;
            label.setText(""); // Zelle soll leer sein

            // 3. Wenn die Zelle ausgewählt ist, nutze die Selektionsfarben
            if (isSelected && value == null) {
                // Die Basisklasse (super) hat dies bereits korrekt gesetzt
                return component;
            }

            // 4. Farbe prüfen und setzen
            Color color = (Color) value;

            if (color != null) {
                // Farbe wurde gewählt: Hintergrund setzen
                label.setBackground(color);

                // Textfarbe anpassen (optional, falls Text angezeigt würde)
                // Hier setze ich es einfach auf Schwarz
                label.setForeground(Color.black);
            } else {
                // Keine Farbe gewählt: Standard-Tabelle-Hintergrund setzen
                label.setBackground(DEFAULT_TABLE_BACKGROUND);
                label.setForeground(UIManager.getColor("Table.foreground"));
            }

            return component;
        }
    }

    /**
     * Benutzerdefinierter Zell-Editor, der einen JColorChooser beim Klick
     * öffnet. Speichert das gewählte Color-Objekt.
     */
    class oldColorChooserEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private Color currentColor;
        private final JButton button;
        private static final String EDIT = "edit";

        public oldColorChooserEditor() {
            // Wir verwenden einen Button, der den Dialog öffnet
            button = new JButton();
            button.setActionCommand(EDIT);
            button.addActionListener(this);
            button.setBorderPainted(false);
            button.setOpaque(true); // Wichtig für die Farbdarstellung im Editor
        }

        @Override
        public Object getCellEditorValue() {
            return currentColor;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Speichere den aktuellen Wert und setze die Hintergrundfarbe des Buttons
            currentColor = (Color) value;
            if (currentColor == null) {
                currentColor = Color.WHITE; // Verwende Weiß als Standard-Hintergrund für den Button
            }
            button.setBackground(currentColor);
            return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (EDIT.equals(e.getActionCommand())) {
                // Die Bearbeitung stoppen, um den alten Wert zu speichern, bevor der Dialog geöffnet wird
                // Damit die Zelle richtig gezeichnet wird.
                // Fire editing stopped.

                // Öffne den Farbwähler-Dialog
                Color newColor = JColorChooser.showDialog(
                        button,
                        "Wähle eine Farbe für den Trader",
                        currentColor != null ? currentColor : Color.WHITE);

                if (newColor != null) {
                    currentColor = newColor;
                }

                // Beende die Bearbeitung und speichere den neuen Wert im Model
                fireEditingStopped();

            }
        }
    }

    /**
     * Benutzerdefinierter Zell-Renderer, der die Zelle in der gespeicherten
     * Farbe einfärbt.
     */
    class oldColorRenderer extends DefaultTableCellRenderer {

        private final Color DEFAULT_COLOR = UIManager.getColor("Table.background"); // Standard-Hintergrundfarbe der JTable

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Standard-Rendering-Komponente holen (z.B. ein JLabel)
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Color color = (Color) value;

            if (color != null) {
                // Setze die Hintergrundfarbe auf die gespeicherte Farbe
                component.setBackground(color);
                component.setForeground(Color.black); // Setze Textfarbe auf Schwarz

                // Setze den Text der Zelle auf einen leeren String oder den Farbnamen
                ((JLabel) component).setText("");

            } else {
                // Wenn keine Farbe gespeichert ist (null), verwende den Standard
                component.setBackground(DEFAULT_COLOR);
                component.setForeground(UIManager.getColor("Table.foreground")); // Standard-Textfarbe
                ((JLabel) component).setText("");
            }

            // Bei Auswahl die Standard-Selektionsfarbe verwenden
            if (isSelected) {
                component.setBackground(table.getSelectionBackground());
                component.setForeground(table.getSelectionForeground());
            }

            return component;
        }
    }

    /**
     * Erzwingt den Doppelklick für die Spalten mit benutzerdefinierten
     * Editoren.
     */
    class AutoTraderTable extends JTable {

        // Indizes deiner Spalten mit Spinner/ColorChooser-Editoren
        // 1: Count (Spinner), 3: Cash (Spinner), 4: Shares (Spinner), 5: color (ColorChooser)
        private final int[] CUSTOM_EDITOR_COLUMNS = {1, 3, 4, 5};

        public AutoTraderTable() {
            // Ruft den leeren Konstruktor der JTable auf
        }

        public AutoTraderTable(TableModel dm) {
            super(dm);
        }

        /**
         * WICHTIG: Überschreibt die Methode, die entscheidet, ob die
         * Bearbeitung gestartet werden darf. Hier fangen wir das MouseEvent ab.
         */
        @Override
        public boolean editCellAt(int row, int column, EventObject e) {

            // 1. Prüfe, ob es sich um eine unserer Spezial-Spalten handelt
            boolean isCustomEditorColumn = false;
            for (int colIndex : CUSTOM_EDITOR_COLUMNS) {
                if (column == colIndex) {
                    isCustomEditorColumn = true;
                    break;
                }
            }

            // 2. Wenn es eine Spezial-Spalte ist, prüfen wir das Klick-Event
            if (isCustomEditorColumn) {
                if (e instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) e;

                    // Wir starten die Bearbeitung NUR, wenn die Klickanzahl >= 2 ist.
                    if (me.getClickCount() < 2) {
                        // Gib false zurück, um die Bearbeitung bei Einzelklick zu blockieren
                        return false;
                    }
                }

                // 2. Bearbeitung starten, wenn ein KeyEvent (Space) vorliegt
                if (e instanceof KeyEvent) {
                    KeyEvent ke = (KeyEvent) e;
                    if (ke.getKeyCode() != KeyEvent.VK_SPACE) {
                        // Blockieren, wenn nicht Space gedrückt wurde
                        return false;
                    }
                    // HINWEIS: Bei der Leertaste wird die Bearbeitung gestartet.
                    // Das ist die gewünschte Funktion.
                }

            }

            // 3. Für alle anderen Fälle (z.B. Doppelklick in Spezial-Spalten, 
            // oder Standard-Spalten) lassen wir das Standardverhalten zu
            return super.editCellAt(row, column, e);
        }
    }
}
