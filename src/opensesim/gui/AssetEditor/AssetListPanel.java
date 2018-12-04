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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import opensesim.world.AbstractAsset;
import opensesim.world.World;
import opensesim.gui.Globals;
import opensesim.world.WorldAdm;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class AssetListPanel extends javax.swing.JPanel implements GuiSelectionList {

    WorldAdm worldadm;

    JSONObject json_set;

    /**
     * Creates new form AssetList
     */
    public AssetListPanel() {

        initComponents();

        if (Globals.prefs == null) {
            return;
        }

        json_set = new JSONObject(Globals.prefs.get("myassets", "{"
                + "EUR:{name:Euro,decimals:8,type:opensesim.sesim.Assets.FurtureAsset}}"));

        json_set = Globals.getAssets();
      //  reload();

        assetTable.setRowSelectionAllowed(true);

        assetTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        assetTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        assetTable.getColumnModel().getColumn(2).setPreferredWidth(80);
    }

    public AssetListPanel(WorldAdm worldadm) {
 
        this();
        this.worldadm = worldadm;
        reload();
    }

    @Override
    public JSONObject getSelectedObject() {
        int row = assetTable.getSelectedRow();
        String symbol = (String) assetTable.getValueAt(row, 0);
        return json_set.getJSONObject(symbol);
    }

    final void reload() {
        /*      json_set = Globals.getAssets();
        DefaultTableModel m = (DefaultTableModel) assetTable.getModel();


        m.setRowCount(0);
        for (String symbol : json_set.keySet()) {
            JSONObject o = json_set.optJSONObject(symbol);
            if (o == null) {
                continue;
            }

            Class <AbstractAsset> a = Globals.getClassByName(o.optString("type"));
            String type_name;
                    
            try {
                type_name=a.getConstructor(World.class,JSONObject.class).newInstance(null,null).getTypeName();
                
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(AssetListPanel.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
   
 
              
  
            
            m.addRow(new Object[]{

                symbol,
                o.opt("name"),
                type_name
            });

        }*/

        Collection<AbstractAsset> assets;
        assets = worldadm.world.getAssetCollection();
        DefaultTableModel m = (DefaultTableModel) assetTable.getModel();
        m.setRowCount(0);
        for (AbstractAsset asset : assets) {
            JSONObject o;
            o = asset.getJson();
            if (o == null) {
                continue;
            }

            Class<AbstractAsset> a = Globals.getClassByName(o.optString("type"));
            String type_name;

            try {
                type_name = a.getConstructor(World.class, JSONObject.class).newInstance(null, null).getTypeName();

            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(AssetListPanel.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            m.addRow(new Object[]{
                o.opt("symbol"),
                o.opt("name"),
                type_name
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
                new Object[]{"Symbol", "Name", "Type"}, 0
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
