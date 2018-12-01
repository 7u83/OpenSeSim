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
package opensesim.gui.AssetEditor;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import opensesim.World;
import opensesim.gui.Globals;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class AssetListPanel extends javax.swing.JPanel implements GuiSelectionList{

    World world;

    JSONObject json_set;

    /**
     * Creates new form AssetList
     */
    public AssetListPanel() {
        world = Globals.world;
        initComponents();

        if (Globals.prefs == null) {
            return;
        }

        json_set = new JSONObject(Globals.prefs.get("myassets", "{"
                + "EUR:{name:Euro,decimals:8,type:opensesim.sesim.Assets.FurtureAsset}}"));
        reload();

        assetTable.setRowSelectionAllowed(true);
        assetTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        assetTable.getColumnModel().getColumn(1).setPreferredWidth(30);
        assetTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        assetTable.getColumnModel().getColumn(3).setPreferredWidth(30);
    }

    @Override
    public JSONObject getSelectedObject() {
        int row = assetTable.getSelectedRow();
        String symbol = (String) assetTable.getValueAt(row, 1);
        return json_set.getJSONObject(symbol);
    }

    final void reload() {
        DefaultTableModel m = (DefaultTableModel) assetTable.getModel();
        /*    m.setRowCount(0);
        Map assets = BasicAsset.getAssets();
        for (Object key : assets.keySet()) {
            addAsset((Id) key);
        }
        m.fireTableDataChanged();
         */
        if (world == null) {
            return;
        }
        m.setRowCount(0);

        /*      for (AbstractAsset a : world.getAssetCollection()) {
            m.addRow(new Object[]{
                a.getID(),
                a.getSymbol(),
                a.getName(),
                a.getTypeName()
            });
        }
         */
        m.setRowCount(0);
        for (String symbol : json_set.keySet()) {
            JSONObject o = json_set.optJSONObject(symbol);
            if (o == null) {
                continue;
            }

            m.addRow(new Object[]{
                o.opt("id"),
                symbol,
                o.opt("name"),
                o.opt("type")
            });

        }

        /*      Collection ac;
        ObjectMapper om = new ObjectMapper();
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        try {
            String s = om.writeValueAsString(world.getAssetCollection());
            System.out.printf("MyValues %s", s);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(AssetListPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
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
                new Object[]{"ID", "Symbol", "Name", "Type"}, 0
        );

        assetTable.setAutoCreateRowSorter(true);
        assetTable.getTableHeader().setReorderingAllowed(false);
        return model;

    }

    public void uppdate() {
        DefaultTableModel m;

        m = (DefaultTableModel) this.assetTable.getModel();
        m.fireTableDataChanged();

    }

    /*    public void addAsset(Id id) {

        BasicAsset a = (BasicAsset) BasicAsset.getAsset(id);
        DefaultTableModel m = (DefaultTableModel) this.assetList.getModel();
        m.addRow(new Object[]{
            a.getID(),
            a.getSymbol(),
            a.getName()});
    }
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        assetTable = new javax.swing.JTable();

        assetTable.setModel(getModel());
        assetTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(assetTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 548, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 362, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTable assetTable;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}