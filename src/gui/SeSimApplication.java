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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.json.JSONArray;
import org.json.JSONObject;


import sesim.AutoTraderInterface;
import sesim.Exchange;
import sesim.Scheduler;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class SeSimApplication extends javax.swing.JFrame {

    /**
     * Creates new form NewMDIApplication
     */
    public SeSimApplication() {
        initComponents();
        setTitle("");
        boolean init = Globals.prefs.getBoolean("initilized", false);
        if (!init){
            resetToDefaults();
            Globals.prefs.putBoolean("initilized", true);
        }
        this.chartSrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        this.setLocationRelativeTo(null);
    }

    AutoTraderInterface createTraderNew(Exchange se, long id, String name, double money, double shares, JSONObject cfg) {

        String base = cfg.getString("base");
        AutoTraderInterface ac = Globals.tloader.getStrategyBase(base);
        if (ac == null) {
            return null;
        }
        ac.putConfig(cfg);
        ac.init(se, id, name, money, shares, cfg);

        return ac;
    }

    public void startTraders() {

        WaitBox wb = new WaitBox();

        //   Globals.se.setMoneyDecimals(8);
        //    Globals.se.setSharesDecimals(0);        
        JSONArray tlist = Globals.getTraders();

        Double moneyTotal = 0.0;
        Double sharesTotal = 0.0;
        long id = 0;
        for (int i = 0; i < tlist.length(); i++) {
            JSONObject t = tlist.getJSONObject(i);
            String strategy_name = t.getString("Strategy");
            JSONObject strategy = Globals.getStrategy(strategy_name);
            String base = strategy.getString("base");
            AutoTraderInterface ac = Globals.tloader.getStrategyBase(base);

            System.out.printf("Load Strat: %s\n", strategy_name);
            System.out.printf("Base %s\n", base);
            Integer count = t.getInt("Count");
            Double shares = t.getDouble("Shares");
            Double money = t.getDouble("Money");

            Boolean enabled = t.getBoolean("Enabled");
            if (!enabled) {
                continue;
            }

            System.out.printf("Count: %d Shares: %f Money %f\n", count, shares, money);

            for (int i1 = 0; i1 < count; i1++) {
                AutoTraderInterface trader;

                trader = this.createTraderNew(Globals.se, id, t.getString("Name") + i1, money, shares, strategy);

                Globals.se.traders.add(trader);

                moneyTotal += money;
                sharesTotal += shares;

            }

        }

        Globals.se.fairValue = moneyTotal / sharesTotal;

        //  Globals.se.fairValue = 1.0;
        System.out.printf("Failr Value is %f\n", Globals.se.fairValue);

        for (int i = 0; i < Globals.se.traders.size(); i++) {
            Globals.se.traders.get(i).start();
        }

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
        jTextArea1 = new javax.swing.JTextArea();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        orderBookNew1 = new gui.orderbook.OrderBook();
        jPanel2 = new javax.swing.JPanel();
        stopButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        accelSpinner = new javax.swing.JSpinner();
        clock = new gui.Clock();
        jLabel1 = new javax.swing.JLabel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jSplitPane4 = new javax.swing.JSplitPane();
        orderBooksHorizontal1 = new gui.orderbook.OrderBooksHorizontal();
        chartSrollPane = new javax.swing.JScrollPane();
        chart = new gui.MainChart();
        quoteVertical1 = new gui.orderbook.QuoteVertical();
        jSplitPane5 = new javax.swing.JSplitPane();
        statistics1 = new gui.Statistics();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        closeMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        resetToDefaultsMenuItem = new javax.swing.JMenuItem();
        clearMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editExchangeMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        editPreferences = new javax.swing.JMenuItem();
        simMenu = new javax.swing.JMenu();
        simMenuStart = new javax.swing.JMenuItem();
        simMenuPause = new javax.swing.JMenuItem();
        simMenuStop = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        viewTraderListCheckBox = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("Hakke");
        jScrollPane2.setViewportView(jTextArea1);

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane2.setLeftComponent(orderBookNew1);

        jSplitPane1.setTopComponent(jSplitPane2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SeSim - Stock Exchange Simmulator");
        setMinimumSize(new java.awt.Dimension(640, 480));

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/stop.gif"))); // NOI18N
        stopButton.setText("Stop");
        stopButton.setFocusable(false);
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        runButton.setFont(runButton.getFont());
        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/run.gif"))); // NOI18N
        runButton.setText("Run sim!");
        runButton.setToolTipText("Run the simmulation");
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/pause.gif"))); // NOI18N
        jButton2.setText("Pause");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        accelSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.0d, null, 1.0d));
        accelSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                accelSpinnerStateChanged(evt);
            }
        });
        accelSpinner.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                accelSpinnerPropertyChange(evt);
            }
        });

        jLabel1.setText("Acceleration:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(runButton, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 375, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(clock, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(accelSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(runButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(clock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accelSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(0, 5, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jSplitPane4.setDividerLocation(300);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane4.setBottomComponent(orderBooksHorizontal1);

        javax.swing.GroupLayout chartLayout = new javax.swing.GroupLayout(chart);
        chart.setLayout(chartLayout);
        chartLayout.setHorizontalGroup(
            chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        chartLayout.setVerticalGroup(
            chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        chartSrollPane.setViewportView(chart);

        jSplitPane4.setLeftComponent(chartSrollPane);

        jSplitPane3.setRightComponent(jSplitPane4);
        jSplitPane3.setLeftComponent(quoteVertical1);

        jSplitPane5.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane5.setTopComponent(statistics1);

        jSplitPane3.setLeftComponent(jSplitPane5);

        getContentPane().add(jSplitPane3, java.awt.BorderLayout.CENTER);

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);

        closeMenuItem.setMnemonic('c');
        closeMenuItem.setText("Close");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeMenuItem);
        fileMenu.add(jSeparator5);

        resetToDefaultsMenuItem.setMnemonic('r');
        resetToDefaultsMenuItem.setText("Reset to defaults");
        resetToDefaultsMenuItem.setToolTipText("");
        resetToDefaultsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetToDefaultsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(resetToDefaultsMenuItem);

        clearMenuItem.setMnemonic('c');
        clearMenuItem.setText("Clear All");
        clearMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(clearMenuItem);
        fileMenu.add(jSeparator4);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        editExchangeMenuItem.setMnemonic('y');
        editExchangeMenuItem.setText("Exchange ...");
        editExchangeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editExchangeMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(editExchangeMenuItem);
        editMenu.add(jSeparator1);

        pasteMenuItem.setMnemonic('s');
        pasteMenuItem.setText("Strategies ...");
        pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText("Traders ...");
        deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(deleteMenuItem);
        editMenu.add(jSeparator2);

        editPreferences.setMnemonic('p');
        editPreferences.setText("Preferences ...");
        editPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPreferencesActionPerformed(evt);
            }
        });
        editMenu.add(editPreferences);

        menuBar.add(editMenu);

        simMenu.setMnemonic('s');
        simMenu.setText("Sim");

        simMenuStart.setMnemonic('s');
        simMenuStart.setText("Start");
        simMenuStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simMenuStartActionPerformed(evt);
            }
        });
        simMenu.add(simMenuStart);

        simMenuPause.setMnemonic('p');
        simMenuPause.setText("Pause");
        simMenuPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simMenuPauseActionPerformed(evt);
            }
        });
        simMenu.add(simMenuPause);

        simMenuStop.setMnemonic('t');
        simMenuStop.setText("Stop");
        simMenuStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simMenuStopActionPerformed(evt);
            }
        });
        simMenu.add(simMenuStop);

        menuBar.add(simMenu);

        viewMenu.setMnemonic('v');
        viewMenu.setText("View");

        viewTraderListCheckBox.setMnemonic('t');
        viewTraderListCheckBox.setText("Traders");
        viewTraderListCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewTraderListCheckBoxActionPerformed(evt);
            }
        });
        viewMenu.add(viewTraderListCheckBox);

        jCheckBoxMenuItem1.setText("Orderbook");
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem1ActionPerformed(evt);
            }
        });
        viewMenu.add(jCheckBoxMenuItem1);

        menuBar.add(viewMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        Dialog d = new gui.AboutDialog(this, rootPaneCheckingEnabled);
        d.setVisible(rootPaneCheckingEnabled);

    }//GEN-LAST:event_aboutMenuItemActionPerformed

    void pauseSim() {
        Globals.se.timer.pause();
    }

    void startSim() {

        resetSim();
        JSONObject jo = new JSONObject(Globals.prefs.get("Exchange", "{}"));
        Globals.se.putConfig(jo);

        this.stopButton.setEnabled(true);

//        this.orderBookPanel.invalidate();
//        this.orderBookPanel.repaint();
        this.clock.invalidate();
        this.clock.repaint();

        this.startTraders();

        Globals.se.timer.setPause(false);
        Globals.se.timer.start();
        Globals.se.timer.setAcceleration((Double) this.accelSpinner.getValue());

        Scheduler.TimerTaskRunner tt = new Scheduler.TimerTaskRunner() {
            @Override
            public long timerTask() {
                System.out.printf("Hello i will inject money\n");
                // Globals.se.injectMoney();
                return 1000 * 60 * 10;
            }

            @Override
            public long getID() {
                return 7L;
            }

        };
//        Globals.se.timer.startTimerTask(tt, 0);

    }

    void stopSim() {
        Globals.se.timer.terminate();
        this.stopButton.setEnabled(false);
    }

    void resetSim() {
        Globals.se.terminate();
        Globals.se.reset();
        chart.initChart();
        chart.invalidate();
        chart.repaint();
//       this.orderBookPanel.invalidate();
//        this.orderBookPanel.repaint();

    }


    private void editPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPreferencesActionPerformed
        Globals.LOGGER.info("Edit prefs...");

        Dialog d = new gui.EditPreferencesDialog(this, rootPaneCheckingEnabled);
        d.setVisible(rootPaneCheckingEnabled);
    }//GEN-LAST:event_editPreferencesActionPerformed


    private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed
        EditAutoTraderListDialog ed = new EditAutoTraderListDialog(this, true);
        ed.setVisible(rootPaneCheckingEnabled);


    }//GEN-LAST:event_deleteMenuItemActionPerformed

    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
        EditStrategies s = new EditStrategies(this, true);
        s.setVisible(rootPaneCheckingEnabled);


    }//GEN-LAST:event_pasteMenuItemActionPerformed

    private final LoggerDialog log_d = new LoggerDialog(this, false);


    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        JFileChooser fc = getFileChooser();

        while (true) {
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            try {
                File f = fc.getSelectedFile();
                Globals.loadFile(f);
                String workdir = fc.getCurrentDirectory().getAbsolutePath();
                Globals.prefs.put(Globals.PrefKeys.WORKDIR, workdir);
                Globals.prefs.put(Globals.PrefKeys.CURRENTFILE, f.getAbsolutePath());
                setTitle(f.getName());
                return;

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Can't load file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }//GEN-LAST:event_openMenuItemActionPerformed

    // initialize a JFileChose with  worging directory and extension
    private JFileChooser getFileChooser() {
        JFileChooser fc = new JFileChooser();

        String workdir = Globals.prefs.get(Globals.PrefKeys.WORKDIR, "");
        fc.setCurrentDirectory(new File(workdir));

        FileNameExtensionFilter sesim_filter = new FileNameExtensionFilter("SeSim Files", Globals.SESIM_FILEEXTENSION);
        fc.setFileFilter(sesim_filter);
        return fc;
    }

    @Override
    public final void setTitle(String filename) {
        String name;
        name = Globals.SESIM_APPTITLE;
        if (!"".equals(filename)) {
            name += " (" + filename + ")";
        }
        super.setTitle(name);
    }

    private void saveFile(boolean saveAs) {
        JFileChooser fc = getFileChooser();
        FileFilter sesim_filter = fc.getFileFilter();

        while (true) {
            String current_file = Globals.prefs.get(Globals.PrefKeys.CURRENTFILE, "");
            File fobj;

            if (saveAs || "".equals(current_file)) {

                if (!"".equals(current_file)) {
                    fobj = new File(current_file);
                    fc.setSelectedFile(fobj);
                    fc.setCurrentDirectory(fobj);
                }

                saveAs = true;
                if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
            } else {
                fobj = new File(current_file);
                fc.setSelectedFile(fobj);
                fc.setCurrentDirectory(fobj);
            }

            File f = fc.getSelectedFile();
            String workdir = fc.getCurrentDirectory().getAbsolutePath();
            Globals.prefs.put(Globals.PrefKeys.WORKDIR, workdir);
            String fn = f.getAbsolutePath();

            if (f.exists() && saveAs) {
                String s = String.format("File %s already exists. Do you want to overwrite?", fn);
                int dialogResult = JOptionPane.showConfirmDialog(this, s, "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult != JOptionPane.YES_OPTION) {
                    continue;
                }

            }

            FileFilter selected_filter = fc.getFileFilter();
            if (selected_filter == sesim_filter) {
                System.out.printf("Filter", selected_filter.toString());
                if (!fn.toLowerCase().endsWith("." + Globals.SESIM_FILEEXTENSION)) {
                    f = new File(fn + "." + Globals.SESIM_FILEEXTENSION);
                }
            }

            try {
                Globals.saveFile(f);
                Globals.prefs.put(Globals.PrefKeys.CURRENTFILE, fn);
                setTitle(f.getName());
                return;

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, "Can't save file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            }

        }

    }


    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed

        this.saveFile(true);
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    private void editExchangeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editExchangeMenuItemActionPerformed
        EditExchangeDialog ed = new EditExchangeDialog((Frame) this.getParent(), true);
        int rc = ed.showdialog();
        //  System.out.printf("EDRET: %d\n",rc);

    }//GEN-LAST:event_editExchangeMenuItemActionPerformed

    private void resetToDefaults(){
        InputStream is = getClass().getResourceAsStream("/resources/files/defaultcfg.json");
        String df = new Scanner(is, "UTF-8").useDelimiter("\\A").next();

        try {
            Globals.loadString(df);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Can't load file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
        
    }
    
    private void resetToDefaultsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetToDefaultsMenuItemActionPerformed

        int dialogResult = JOptionPane.showConfirmDialog(this, "Are you sure?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult != JOptionPane.YES_OPTION) {
            return;
        }

        this.resetToDefaults();

    }//GEN-LAST:event_resetToDefaultsMenuItemActionPerformed

    private void simMenuStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simMenuStartActionPerformed
        startSim();
    }//GEN-LAST:event_simMenuStartActionPerformed

    private void simMenuPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simMenuPauseActionPerformed
        pauseSim();
    }//GEN-LAST:event_simMenuPauseActionPerformed

    private void simMenuStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simMenuStopActionPerformed
        stopSim();
    }//GEN-LAST:event_simMenuStopActionPerformed

    private void accelSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_accelSpinnerStateChanged
        Double val = (Double) this.accelSpinner.getValue();
        Globals.se.timer.setAcceleration(val);
    }//GEN-LAST:event_accelSpinnerStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Globals.se.timer.pause();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        startSim();
    }//GEN-LAST:event_runButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        stopSim();
    }//GEN-LAST:event_stopButtonActionPerformed

    private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
        JDialog jd = new gui.orderbook.OrderBookDialog(this, false);
        jd.setVisible(rootPaneCheckingEnabled);
    }//GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

    TraderListDialog tld = null;
    private void viewTraderListCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewTraderListCheckBoxActionPerformed

        javax.swing.SwingUtilities.invokeLater(() -> {

            System.out.printf("Trwindow: %s\n", Boolean.toString(this.viewTraderListCheckBox.getState()));
            if (this.viewTraderListCheckBox.getState()) {
                if (tld == null) {
                    tld = new TraderListDialog(this, false);
                    tld.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            super.windowClosing(e);
                            viewTraderListCheckBox.setState(false);
                            System.out.printf("Set menu false\n");
                        }
                    });

                }

                tld.setVisible(true);
            } else if (tld != null) {
                System.out.printf("Set visible = false\n");
                tld.setVisible(false);
            }
        });

    }//GEN-LAST:event_viewTraderListCheckBoxActionPerformed

    private void accelSpinnerPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_accelSpinnerPropertyChange
//        System.out.printf("Accel Spinner PRop Change\n");
    }//GEN-LAST:event_accelSpinnerPropertyChange

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        saveFile(false);

    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
        Globals.prefs.put(Globals.PrefKeys.CURRENTFILE, "");
        setTitle("");
    }//GEN-LAST:event_closeMenuItemActionPerformed

    private void clearMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clearMenuItemActionPerformed

    /**
     * @param args the command line arguments
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InstantiationException
     */
    public static void main(String args[]) throws IllegalAccessException, InstantiationException {

        System.out.printf("Main called\n");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] gs = ge.getScreenDevices();
        for (GraphicsDevice d : gs) {
            System.out.printf("ID %s\n", d.getIDstring());

        }

        //System.exit(0);
        Globals.initGlobals();
        //System.exit(0);
        Globals.se = new Exchange();

        Class<?> c = sesim.Exchange.class;
        Globals.prefs = Preferences.userNodeForPackage(c);
        Globals.prefs.put(Globals.PrefKeys.CURRENTFILE, "");

        Globals.setLookAndFeel(Globals.prefs.get("laf", "Nimbus"));

        JDialog.setDefaultLookAndFeelDecorated(true);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.printf("In run method now\n");

                String x = new java.io.File(SeSimApplication.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath()).toString(); //.getName();

                System.out.printf("Creating Application\n");
                new SeSimApplication().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JSpinner accelSpinner;
    private gui.MainChart chart;
    private javax.swing.JScrollPane chartSrollPane;
    private javax.swing.JMenuItem clearMenuItem;
    private gui.Clock clock;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenuItem editExchangeMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editPreferences;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JSplitPane jSplitPane5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private gui.orderbook.OrderBook orderBookNew1;
    private gui.orderbook.OrderBooksHorizontal orderBooksHorizontal1;
    private javax.swing.JMenuItem pasteMenuItem;
    private gui.orderbook.QuoteVertical quoteVertical1;
    private javax.swing.JMenuItem resetToDefaultsMenuItem;
    private javax.swing.JButton runButton;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenu simMenu;
    private javax.swing.JMenuItem simMenuPause;
    private javax.swing.JMenuItem simMenuStart;
    private javax.swing.JMenuItem simMenuStop;
    private gui.Statistics statistics1;
    private javax.swing.JButton stopButton;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JCheckBoxMenuItem viewTraderListCheckBox;
    // End of variables declaration//GEN-END:variables

}
