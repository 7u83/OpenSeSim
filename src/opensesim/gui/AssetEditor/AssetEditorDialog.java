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

import java.awt.Dialog;
import java.awt.Window;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import opensesim.world.AbstractAsset;
import opensesim.world.World;

import opensesim.gui.util.EscDialog;
import opensesim.gui.Globals;
import opensesim.gui.util.Json;
import opensesim.gui.util.Json.Export;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class AssetEditorDialog extends EscDialog {

  


    /**
     * Creates new form CreateAssetDialog
     */
    public AssetEditorDialog(Dialog parent, AbstractAsset asset) {

        super(parent, true);
        initComponents();

    }

    public AssetEditorDialog(Window parent) {
        super(parent, true);
        initComponents();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        assetEditorPanel = new opensesim.gui.AssetEditor.AssetEditorPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(assetEditorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(assetEditorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

        JSONObject result = Json.get(assetEditorPanel);    
        System.out.printf("JSON: %s\n", result.toString(5));
        JSONObject all = Globals.getAssets();
        all.put(result.getString("symbol"), result);
        Globals.putAssets(all);
        
    /*    if (this.asset == null) {
            try {
                int selected = this.assetEditorPanel.assetTypesComboBox.getSelectedIndex();
                Class<AbstractAsset> cls = (Class<AbstractAsset>) this.assetEditorPanel.asset_types.get(selected);
                asset = AbstractAsset.create(world, cls, assetEditorPanel.symField.getText());


            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
*/
    
        /*else {
            if (!this.asset.getSymbol().equals(assetEditor.symField.getText())) {
                try {
                    BasicAsset.rename(asset, assetEditor.symField.getText());
        
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage(), 
                            "Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
            }

        }*/
      //  asset.setName(assetEditorPanel.nameField.getText());
      //  asset.setDecimals((int) assetEditorPanel.decimalsField.getValue());

     //   JSONObject cfg = world.getConfig();

     /*   try {
            String jstr = cfg.toString(5);
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.printf("JSONARRAY %s\n", cfg.toString(3));
        JSONObject world_cfg = Globals.world.getConfig();
        Globals.prefs.put("world", world_cfg.toString());
       */ dispose();

    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        dispose();

    }//GEN-LAST:event_cancelButtonActionPerformed

    static public boolean runDialog(Window parent, JSONObject o, JSONObject all) {

        AssetEditorDialog d = new AssetEditorDialog(parent);
        if (o!=null)
            Json.put(d.assetEditorPanel, o);
        d.pack();
        d.revalidate();
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
        d.dispose();
        
        return true;
    }

    /* 
    static public Id runDialog(Dialog parent, AbstractAsset asset) {
        AssetEditorDialog dialog = new AssetEditorDialog(parent, asset);
        dialog.assetEditorPanel.initFields(asset);

        dialog.assetEditorPanel.dialog = dialog;
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        ObjectMapper mapper = new ObjectMapper();
        // disable auto detection
        mapper.disable(MapperFeature.AUTO_DETECT_CREATORS,
                MapperFeature.AUTO_DETECT_FIELDS,
                MapperFeature.AUTO_DETECT_GETTERS,
                MapperFeature.AUTO_DETECT_IS_GETTERS);
        // if you want to prevent an exception when classes have no annotated properties
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        String vs;
        try {

            vs = mapper.writeValueAsString(dialog.assetEditorPanel);
            System.out.print(vs);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(AssetEditorDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        ObjectNode n = mapper.valueToTree(dialog.assetEditorPanel);

        return dialog.newId;
    }
     */
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AssetEditorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AssetEditorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AssetEditorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AssetEditorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
 /*        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               CreateAssetDialog dialog = new CreateAssetDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        //</editor-fold>

        /* Create and display the dialog */
 /*        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               CreateAssetDialog dialog = new CreateAssetDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        //</editor-fold>

        /* Create and display the dialog */
 /*        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               CreateAssetDialog dialog = new CreateAssetDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        //</editor-fold>

        /* Create and display the dialog */
 /*        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               CreateAssetDialog dialog = new CreateAssetDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });*/
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private opensesim.gui.AssetEditor.AssetEditorPanel assetEditorPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
