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
package opensesim.gui.PreferencesEditor;

import opensesim.gui.Globals;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class EditPreferencesDialog extends opensesim.gui.util.EscDialog {

    UIManager.LookAndFeelInfo[] lafInfo;

    LookAndFeel old_laf;
    LookAndFeel new_laf;

    /**
     * Creates new form EditPreferencesDialog
     */
    public EditPreferencesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(this.getParent());

        old_laf = UIManager.getLookAndFeel();

        lafInfo = UIManager.getInstalledLookAndFeels();
        lafComboBox.removeAllItems();
        for (UIManager.LookAndFeelInfo lafInfo1 : lafInfo) {
            lafComboBox.addItem(lafInfo1.getName());
        }
        lafComboBox.setSelectedItem(Globals.prefs.get(Globals.PrefKeys.LAF, "Nimbus"));

        String selstr;
        selstr = Globals.prefs.get(Globals.DEVELSTATUS, "false");
        this.jDevleopmentFeaturesCheckBox.setSelected(selstr.equals("true"));

        selstr = Globals.prefs.get(Globals.GODMODE, "false");
        this.godmodeCheckBox.setSelected(selstr.equals("true"));

        xClassPath.setText(Globals.prefs.get(Globals.PrefKeys.XCLASSPATH, ""));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lafComboBox = new javax.swing.JComboBox<>();
        cancelButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jDevleopmentFeaturesCheckBox = new javax.swing.JCheckBox();
        godmodeCheckBox = new javax.swing.JCheckBox();
        classPathChoseButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        xClassPath = new javax.swing.JTextField();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OpenSeSim Preferences");
        setMinimumSize(new java.awt.Dimension(551, 343));

        lafComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        lafComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lafComboBoxActionPerformed(evt);
            }
        });

        cancelButton.setMnemonic('c');
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        applyButton.setMnemonic('a');
        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        okButton.setMnemonic('o');
        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Look and Feel:");

        jDevleopmentFeaturesCheckBox.setText("Develeopment");

        godmodeCheckBox.setText("Godmode");

        classPathChoseButton.setText("Choose ...");
        classPathChoseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classPathChoseButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Additional Path:");

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jLabel3.setText("Number of threads:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(418, 418, 418))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(okButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(applyButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(xClassPath)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(classPathChoseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(3, 3, 3))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(godmodeCheckBox)
                            .addComponent(jDevleopmentFeaturesCheckBox)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lafComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lafComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(godmodeCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDevleopmentFeaturesCheckBox)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(classPathChoseButton)
                            .addComponent(xClassPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addContainerGap(42, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(applyButton)
                            .addComponent(okButton))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lafComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lafComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lafComboBoxActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if (old_laf != new_laf) {
            try {
                  JFrame.setDefaultLookAndFeelDecorated(true);   
  JDialog.setDefaultLookAndFeelDecorated(true); 
                    JFrame.setDefaultLookAndFeelDecorated(true);   
  JDialog.setDefaultLookAndFeelDecorated(true); 
                UIManager.setLookAndFeel(old_laf);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(EditPreferencesDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
            resetUI();
        }
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    void resetUI() {
        for (Window w : Window.getWindows()) {

            SwingUtilities.updateComponentTreeUI(w);
            w.pack();
        }
    }

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed

        String selected = (String) this.lafComboBox.getSelectedItem();

        Globals.setLookAndFeel(selected);
        new_laf = UIManager.getLookAndFeel();
        resetUI();


    }//GEN-LAST:event_applyButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.applyButtonActionPerformed(evt);
        String selected = (String) this.lafComboBox.getSelectedItem();
        Globals.prefs.put("laf", selected);
        String sel;
        sel = this.jDevleopmentFeaturesCheckBox.isSelected() == true ? "true" : "false";
        Globals.prefs.put(Globals.DEVELSTATUS, sel);
        sel = this.godmodeCheckBox.isSelected() == true ? "true" : "false";
        Globals.prefs.put(Globals.GODMODE, sel);

        Globals.prefs.put(Globals.PrefKeys.XCLASSPATH, xClassPath.getText());

        Globals.notifyCfgListeners();
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void classPathChoseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classPathChoseButtonActionPerformed

        JFileChooser chooser = new JFileChooser();

        File dir = null;
        if (!"".equals(xClassPath.getText())) {
            dir = new File(xClassPath.getText());
            if (!dir.exists()) {
                dir = null;
            }
        }
        if (dir == null) {
            dir = new File(System.getProperty("user.home"));
        }

        chooser.setCurrentDirectory(dir);

        chooser.setDialogTitle("Chose Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);


        int rc = chooser.showOpenDialog(null);
        if ( rc == JFileChooser.APPROVE_OPTION){
            xClassPath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
        
        
    }//GEN-LAST:event_classPathChoseButtonActionPerformed

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
            java.util.logging.Logger.getLogger(EditPreferencesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditPreferencesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditPreferencesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditPreferencesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditPreferencesDialog dialog = new EditPreferencesDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton classPathChoseButton;
    private javax.swing.JCheckBox godmodeCheckBox;
    private javax.swing.JCheckBox jDevleopmentFeaturesCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JComboBox<String> lafComboBox;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField xClassPath;
    // End of variables declaration//GEN-END:variables
}
