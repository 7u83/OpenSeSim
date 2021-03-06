/*
 * Copyright (c) 2018, 7u83 <7u83@mail.ru>
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
package opensesim.gui.exchangeeditor;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import opensesim.world.RealWorld;
import opensesim.gui.Globals;
import opensesim.world.Exchange;
import opensesim.world.GodWorld;
import opensesim.world.World;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class ExchangeListPanel extends javax.swing.JPanel {

    GodWorld world=null;
    
    /**
     * Creates new form ExchangeList
     * @param world
     */
    public ExchangeListPanel(GodWorld world) {
        this.world=world;
        initComponents();
        reload();
    }
    
        public ExchangeListPanel() {
        //world = Globals.world;
        initComponents();
    }

    final void reload() {
        DefaultTableModel m = (DefaultTableModel) exchangeTable.getModel();

      //  JSONObject jex = Globals.getExchanges();

        m.setRowCount(0);
        for (Exchange ex : world.getExchangeCollection()) {
        //    JSONObject e = jex.getJSONObject(esym);
            
            
            m.addRow(new Object[]{
                ex.getSymbol(),
                ex.getName()
                
            });

        }
    }
    
    

    private TableModel getModel() {

        class TModel extends DefaultTableModel {

            private TModel(Object[] object, int i) {
                super(object, i);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        }

        DefaultTableModel model = new TModel(
                new Object[]{"Symbol", "Name"}, 0
        );

        exchangeTable.setAutoCreateRowSorter(true);
        exchangeTable.getTableHeader().setReorderingAllowed(false);

        return model;

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        exchangeTable = new javax.swing.JTable();

        exchangeTable.setModel(getModel());
        jScrollPane2.setViewportView(exchangeTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable exchangeTable;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
