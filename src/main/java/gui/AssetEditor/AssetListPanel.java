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
package gui.AssetEditor;

import gui.Globals;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.json.JSONArray;

import org.json.JSONObject;
import sesim.Config;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class AssetListPanel extends javax.swing.JPanel implements GuiSelectionList {

    /**
     * Creates new form AssetList
     */
    public AssetListPanel() {

        initComponents();

        if (Globals.prefs_new == null) {
            return;
        }

        JSONArray assets = Globals.getConfig().optJSONArray("assets");

        assetTable.setRowSelectionAllowed(true);

        /*        assetTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        assetTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        assetTable.getColumnModel().getColumn(2).setPreferredWidth(80);*/
    }

    public void addRow() {
        DefaultTableModel model = (DefaultTableModel) this.assetTable.getModel();
        model.setRowCount(model.getRowCount() + 1);
        model.setValueAt("New asset", model.getRowCount() - 1, 0);
    }

    public void addAsset(String symbol, String name, int decimals) {
        if (symbol.length() == 0) {
            return;
        }

        JSONObject assets = cfg.optJSONObject("assets");
        if (assets == null) {
            assets = new JSONObject();
        }
        JSONObject a = new JSONObject();
        a.put("name", name);
        a.put("decimals", decimals);
        assets.put(symbol, a);
        this.cfg.put("assets", assets);
        this.updateAll();

    }

    public void delAsset() {

        int row = this.assetTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        this.updateCfg();
        String symbol = (String) this.assetTable.getModel().getValueAt(row, 0);
        JSONObject assets = Config.getAssets(cfg);
        assets.remove(symbol);
        putAssets(assets);
        JSONObject c = Config.getMarkets(cfg);
        c.remove(symbol);
        this.putCurrencies(c);
        this.updateAll();

    }

    public void delCurrency() {
        this.updateCfg();
        String selected = (String) this.currencyCombobox.getSelectedItem();
        JSONObject c = Config.getMarkets(cfg);
        c.remove(selected);
        this.putCurrencies(c);
        this.updateAll();
    }

    /*   private JSONObject getAssets() {
        JSONObject assets = cfg.optJSONObject("assets");
        if (assets == null) {
            assets = new JSONObject();
        }
        return assets;
    }

    private JSONObject getAsset(String symbol) {
        JSONObject assets = getAssets();
        return assets.optJSONObject(symbol);
    }*/
    private void putAssets(JSONObject assets) {
        cfg.put("assets", assets);
    }

    public JSONObject getConfig() {
        this.updateCfg();
        return cfg;
    }

    JSONObject cfg;

    private void updateAll() {
        JSONObject assets = cfg.optJSONObject("assets");

        DefaultTableModel model = (DefaultTableModel) assetTable.getModel();
        model.setRowCount(0);
        if (assets != null) {
            for (String symbol : assets.keySet()) {
                JSONObject jo = assets.optJSONObject(symbol);
                if (jo == null) {
                    jo = new JSONObject();
                }
                model.addRow(new Object[]{
                    symbol,
                    jo.optString("name", null),
                    jo.optInt("decimals", 0)
                });
            }
        }

        this.updateCurrencies();
        this.updateMarkets();
        
        this.defaultAssetComboBox.setSelectedItem(Config.getDefaultAsset(cfg));
        this.defaultcurrencyComboBox.setSelectedItem(Config.getDefaultCurrency(cfg));

    }

    private void updateCfg() {
        JSONObject assets = new JSONObject();
        DefaultTableModel amodel = (DefaultTableModel) this.assetTable.getModel();

        for (int row = 0; row < amodel.getRowCount(); row++) {
            String symbol = (String) amodel.getValueAt(row, 0);
            String name = (String) amodel.getValueAt(row, 1);
            int decimals = (int) amodel.getValueAt(row, 2);
            JSONObject o = new JSONObject();
            o.put("decimals", decimals);
            o.put("name", name);
            assets.put(symbol, o);
        }
        putAssets(assets);

        String currency = (String) this.currencyCombobox.getSelectedItem();
        DefaultTableModel mmodel = (DefaultTableModel) this.marketsTable.getModel();
        for (int row = 0; row < mmodel.getRowCount(); row++) {
            String symbol = (String) mmodel.getValueAt(row, 0);

            Float initial_price = (float) mmodel.getValueAt(row, 1);
            boolean auto_initial_price = (boolean) mmodel.getValueAt(row, 2);
            JSONObject o = new JSONObject();
            o.put("initial_price", initial_price);
            o.put("auto_initial_price", auto_initial_price);
            this.putMarket(currency, symbol, o);
        }
        
        Config.putDefaultCurrency(cfg, (String) this.defaultcurrencyComboBox.getSelectedItem());
        Config.putDefaultAsset(cfg, (String) this.defaultAssetComboBox.getSelectedItem());

    }

    /*    private JSONObject getMarkets() {
        JSONObject currencies = cfg.optJSONObject("currencies");
        if (currencies == null) {
            currencies = new JSONObject();
        }
        return currencies;
    }*/
    private void putCurrencies(JSONObject currencies) {
        cfg.put("markets", currencies);
    }

    JSONObject getMarkets(String currency) {
        JSONObject currencies = Config.getMarkets(cfg);
        JSONObject markets = currencies.optJSONObject(currency);
        if (markets == null) {
            return new JSONObject();
        }
        return markets;
    }

    private void putMarkets(String currency, JSONObject markets) {
        JSONObject currencies = Config.getMarkets(cfg);
        currencies.put(currency, markets);
        putCurrencies(currencies);
    }

    private JSONObject getMarket(String currency, String symbol) {
        JSONObject markets = getMarkets(currency);
        JSONObject market = markets.optJSONObject(symbol);
        if (market == null) {
            return new JSONObject();
        }
        return market;
    }

    private void putMarket(String currency, String symbol, JSONObject data) {
        JSONObject markets = getMarkets(currency);
        markets.put(symbol, data);
        putMarkets(currency, markets);

        /*        JSONObject markets = getMarkets();
        JSONObject cmarket = markets.optJSONObject(currency);
        if (cmarket == null) {
            return;
        }
        cmarket.put(symbol, data);
        markets.put(currency, cmarket);
        putCurrencies(markets);*/
    }

    void updateCurrencies() {
        JSONObject markets = Config.getMarkets(cfg);
        String selected = (String) this.currencyCombobox.getSelectedItem();
        String selectedDefault = (String) this.defaultcurrencyComboBox.getSelectedItem();

        this.currencyCombobox.removeAllItems();
        this.defaultcurrencyComboBox.removeAllItems();

        for (String currency : markets.keySet()) {
            this.currencyCombobox.addItem(currency);
            this.defaultcurrencyComboBox.addItem(currency);
        }
        this.currencyCombobox.setSelectedItem(selected);
        this.defaultcurrencyComboBox.setSelectedItem(selectedDefault);

    }

    void updateMarkets() {
        JSONObject assets = Config.getAssets(cfg);
        String currency = (String) this.currencyCombobox.getSelectedItem();
        JSONObject markets = getMarkets(currency);
        DefaultTableModel model = (DefaultTableModel) this.marketsTable.getModel();
        model.setRowCount(0);

        if (Config.getAsset(cfg, currency) == null) {
            return;
        }

        for (String symbol : assets.keySet()) {

            if (symbol.equals(currency)) {
                continue;
            }
            JSONObject market = getMarket(currency, symbol);
            model.addRow(new Object[]{
                symbol,
                (float) market.optDouble("initial_price", 100.0f),
                market.optBoolean("auto_initial_price", true),
                market.optBoolean("enable", true)

            });
        }

        String selected = (String) this.defaultAssetComboBox.getSelectedItem();
        String defaultCurrency = (String) this.defaultcurrencyComboBox.getSelectedItem();
        this.defaultAssetComboBox.removeAllItems();
        for (String symbol : assets.keySet()) {
            if (symbol.equals(defaultCurrency)) {
                continue;
            }
            this.defaultAssetComboBox.addItem(symbol);
        }
        this.defaultAssetComboBox.setSelectedItem(selected);

    }

    void addSelectedAssetToMarket() {
        int row = this.assetTable.getSelectedRow();
        String symbol = (String) this.assetTable.getModel().getValueAt(row, 0);

        String currency = (String) this.currencyCombobox.getSelectedItem();
        if (currency.equals(symbol)) {
            return;
        }

        JSONObject market = new JSONObject();
        market.put("initial_price", 110);
        this.putMarket(currency, symbol, market);

    }

    public void setConfig(JSONObject cfg) {
        this.cfg = cfg;
        this.updateAll();
    }

    public void createMarket() {
        int row = this.assetTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        String symbol = (String) this.assetTable.getModel().getValueAt(row, 0);

        JSONObject markets = Config.getMarkets(cfg);
        JSONObject m = markets.optJSONObject(symbol);
        if (m != null) {
            return;
        }
        markets.put(symbol, new JSONObject());
        putCurrencies(markets);
        updateAll();
        this.currencyCombobox.setSelectedItem(symbol);
        updateAll();

    }

    /*  public AssetListPanel() {
 
        this();
//        this.worldadm = worldadm;
        reload();ad
    }*/
    @Override
    public JSONObject getSelectedObject() {
        int row = assetTable.getSelectedRow();
        String symbol = (String) assetTable.getValueAt(row, 0);
        //   return worldadm.getAssetBySymbol(symbol).getJson();
        return null;
    }

    final void reload() {

        /*       Collection<AbstractAsset> assets;
        assets = worldadm.getAssetCollection();
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
                type_name = a.getConstructor(GodWorld.class, JSONObject.class).newInstance(null, null).getTypeName();

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
                new Object[]{"Symbol", "Name", "Type"}, 0
        );

        assetTable.setAutoCreateRowSorter(true);
        assetTable.getTableHeader().setReorderingAllowed(false);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        assetTable = new javax.swing.JTable();
        currencyCombobox = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        marketsTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        defaultcurrencyComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        defaultAssetComboBox = new javax.swing.JComboBox<>();

        assetTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Symbol", "Name", "Decimals"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        assetTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(assetTable);

        currencyCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DDM" }));
        currencyCombobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currencyComboboxActionPerformed(evt);
            }
        });

        jLabel1.setText("Currency:");

        jLabel2.setText("Assets:");

        marketsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"DDM/RBTN",  new Float(100.0), null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Pair", "Initial Price", "Auto Initial Price", "Enable"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(marketsTable);

        jLabel3.setText("Markets:");

        defaultcurrencyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        defaultcurrencyComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultcurrencyComboBoxActionPerformed(evt);
            }
        });

        jLabel4.setText("Default market:");

        defaultAssetComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        defaultAssetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultAssetComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addGap(0, 528, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currencyCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(defaultcurrencyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(defaultAssetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(currencyCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(defaultcurrencyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(defaultAssetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void currencyComboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currencyComboboxActionPerformed
            this.updateMarkets();
            //this.updateAll();// TODO add your handling code here:
    }//GEN-LAST:event_currencyComboboxActionPerformed

    private void defaultcurrencyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultcurrencyComboBoxActionPerformed
//        Config.putDefaultCurrency(cfg, (String) this.defaultcurrencyComboBox.getSelectedItem());
        //this.updateAll();
    }//GEN-LAST:event_defaultcurrencyComboBoxActionPerformed

    private void defaultAssetComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultAssetComboBoxActionPerformed
      //  Config.putDefaultAsset(cfg, (String) this.defaultAssetComboBox.getSelectedItem());
      //  this.updateAll();
    }//GEN-LAST:event_defaultAssetComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTable assetTable;
    private javax.swing.JComboBox<String> currencyCombobox;
    private javax.swing.JComboBox<String> defaultAssetComboBox;
    private javax.swing.JComboBox<String> defaultcurrencyComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable marketsTable;
    // End of variables declaration//GEN-END:variables
}
