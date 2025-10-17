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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.json.JSONArray;
import org.json.JSONObject;
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
        DefaultTableModel model = (DefaultTableModel) list.getModel();
        JTableHeader th = list.getTableHeader();
        /*
        for (int i = 0; i < model.getColumnCount(); i++) {
            String hw = (String) th.getColumnModel().getColumn(i).getHeaderValue();
        }
         */
        JSONArray ja = new JSONArray();

        for (int i = 0; i < model.getRowCount(); i++) {
            JSONObject jo = new JSONObject();
            for (int x = 0; x < model.getColumnCount(); x++) {
                Object cw = model.getValueAt(i, x);

                //System.out.printf("CWSaver: %s\n",cw.getClass().toString());
                if (cw != null) {
                    jo.put((String) th.getColumnModel().getColumn(x).getHeaderValue(), cw.toString());
                }

            }
            ja.put(jo);
        }

//        Globals.prefs.put(Globals.PrefKeys.TRADERS, ja.toString());
// TUBE GLOB
        Globals.putTraders(ja);
        

    }

    final void load() {

        JSONArray traders = Globals.getTraders();
        DefaultTableModel model = (DefaultTableModel) list.getModel();
        model.setRowCount(traders.length());

        for (int row = 0; row < traders.length(); row++) {
            JSONObject rowobj = traders.getJSONObject(row);
            for (int col = 0; col < list.getColumnCount(); col++) {
                String h = this.getColumnHeader(col);
                // System.out.printf("Doing stuff for %s\n", h);

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
                if (cl == Double.class) {
                    cv = rowobj.getDouble(h);
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
                    cv = rowobj.getString(h);
                }
                list.getModel().setValueAt(cv, row, col);

            }

        }

    }

    DefaultTableModel model;

    private double getFairValue() {
        Double money = 0.0;
        Double shares = 0.0;

        for (int r = 0; r < model.getRowCount(); r++) {
            Boolean e = (Boolean) list.getValueAt(r, list.getColumn("Enabled").getModelIndex());
            if (e == null) {
                continue;
            }
            if (!e) {
                continue;
            }
            money += (Double) list.getValueAt(r, list.getColumn("Cash").getModelIndex());
            shares += (Double) list.getValueAt(r, list.getColumn("Shares").getModelIndex());
//            System.out.printf("Row: %d %f %f\n", r, money, shares);
        }
        return money / shares;
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

        JComboBox comboBox = new JComboBox();

        Globals.getStrategiesIntoComboBox(comboBox);

        model = (DefaultTableModel) list.getModel();
        list.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));
        // list.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer());
        //  model.setRowCount(3);

        list.setRowHeight(30);

        list.getModel().addTableModelListener((TableModelEvent e) -> {
            this.summary.setText(String.format("Fair Value: %.5f", this.getFairValue()));

        });
        this.summary.setText(String.format("Fair Value: %.5f", this.getFairValue()));
    }

    void add() {
        DefaultTableModel model = (DefaultTableModel) list.getModel();
        model.setRowCount(model.getRowCount() + 1);
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
        list = new javax.swing.JTable();
        summary = new javax.swing.JLabel();

        list.setAutoCreateRowSorter(true);
        list.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Count", "Strategy", "Cash", "Shares", "Enabled"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(list);
        if (list.getColumnModel().getColumnCount() > 0) {
            list.getColumnModel().getColumn(1).setPreferredWidth(30);
        }

        summary.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(summary, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(summary)
                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable list;
    private javax.swing.JLabel summary;
    // End of variables declaration//GEN-END:variables
}
