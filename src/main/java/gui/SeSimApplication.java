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

import java.awt.AWTEvent;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.json.JSONObject;
import sesim.Order;
import javafx.application.Platform;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import sesim.AppHelp;
import traders.RandomTraderL;

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

        ToolTipManager.sharedInstance().setInitialDelay(200); // Zeit bis Tooltip erscheint (ms)
        ToolTipManager.sharedInstance().setDismissDelay(50000); // Zeit bis Tooltip verschwindet (ms)
        ToolTipManager.sharedInstance().setReshowDelay(200);   // Zeit zwischen Tooltips (ms)

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        //Window w = screens[1].getFullScreenWindow();
//            JFrame dummy = new JFrame(screens[1].getDefaultConfiguration());
        //          setLocationRelativeTo(dummy);
        //          dummy.dispose();
        for (GraphicsDevice gd : screens) {
            //Window w = gd.getFullScreenWindow();

        }

        initSim();
        setTitle("");
        boolean init = Globals.prefs_new.getBoolean("initilized", false);
        if (!init) {
            resetToDefaults();
            Globals.prefs_new.putBoolean("initilized", true);
        }

        if (logDialog == null) {
            logDialog = new LogDialog(this, false);
            logDialog.setLocationRelativeTo(this);

        }
        logDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                viewLog.setState(false);
                logDialog.setVisible(false);
            }
        });

        logDialog.getRootPane().registerKeyboardAction(e -> {
            // Simuliere X-Button-Klick
            logDialog.dispatchEvent(new WindowEvent(logDialog, WindowEvent.WINDOW_CLOSING));
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        HelpBroker hb = AppHelp.getHelpBroker();
        HelpSet hs = AppHelp.getHelpSet();

        //     hb.enableHelpKey(getRootPane(), "intro", hs);
        CustomHelpHandler.installHelp(this, hs);

        this.meinToolBar.setFloatable(false);
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
        orderBookNew1 = new gui.orderbook.RawOrderBook();
        meinToolBar = new javax.swing.JToolBar();
        jPanel2 = new javax.swing.JPanel();
        clock = new gui.Clock();
        runControls = new javax.swing.JPanel();
        stopButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        accelerationPanel1 = new gui.AccelerationPanel();
        tradingLogCheckBox = new javax.swing.JCheckBox();
        jSplitPane3 = new javax.swing.JSplitPane();
        jSplitPane4 = new javax.swing.JSplitPane();
        orderBooksHorizontal = new gui.orderbook.OrderBooksHorizontal();
        chartPanel = new chart.ChartPanel();
        quoteVertical1 = new gui.orderbook.QuoteVertical();
        jSplitPane5 = new javax.swing.JSplitPane();
        statistics1 = new gui.Statistics();
        clock1 = new gui.Clock();
        traderListPanel = new TraderListPanel(this,
            new TraderListPanel.Column[]  {
                TraderListPanel.Column.NAME,
                TraderListPanel.Column.SHARES,
                TraderListPanel.Column.CASH,
                TraderListPanel.Column.PNL

            });
            menuBar = new javax.swing.JMenuBar();
            fileMenu = new javax.swing.JMenu();
            fileOpenMenuItem = new javax.swing.JMenuItem();
            fileSaveMenuItem = new javax.swing.JMenuItem();
            fileSaveAsMenuItem = new javax.swing.JMenuItem();
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
            jSeparator3 = new javax.swing.JPopupMenu.Separator();
            randomSeed = new javax.swing.JMenuItem();
            viewMenu = new javax.swing.JMenu();
            viewTraderListCheckBox = new javax.swing.JCheckBoxMenuItem();
            viewRawOrderBook = new javax.swing.JCheckBoxMenuItem();
            viewUnlimitedOrdes = new javax.swing.JCheckBoxMenuItem();
            viewStopOrders = new javax.swing.JCheckBoxMenuItem();
            viewLog = new javax.swing.JCheckBoxMenuItem();
            viewTradingLog = new javax.swing.JCheckBoxMenuItem();
            helpMenu = new javax.swing.JMenu();
            aboutMenuItem = new javax.swing.JMenuItem();

            jTextArea1.setColumns(20);
            jTextArea1.setRows(5);
            jTextArea1.setText("Hakke");
            jScrollPane2.setViewportView(jTextArea1);

            jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

            jSplitPane2.setLeftComponent(orderBookNew1);

            jSplitPane1.setTopComponent(jSplitPane2);

            meinToolBar.setRollover(true);

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            setTitle("SeSim - Stock Exchange Simmulator");
            setMinimumSize(new java.awt.Dimension(640, 480));
            setPreferredSize(new java.awt.Dimension(1024, 768));

            stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/stop.gif"))); // NOI18N
            stopButton.setText("Stop");
            stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            stopButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    stopButtonActionPerformed(evt);
                }
            });

            pauseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pause.gif"))); // NOI18N
            pauseButton.setText("Pause");
            pauseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            pauseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            pauseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    pauseButtonActionPerformed(evt);
                }
            });

            runButton.setFont(runButton.getFont());
            runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/run.gif"))); // NOI18N
            runButton.setText("Run sim!");
            runButton.setToolTipText("Run the simmulation");
            runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            runButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    runButtonActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout runControlsLayout = new javax.swing.GroupLayout(runControls);
            runControls.setLayout(runControlsLayout);
            runControlsLayout.setHorizontalGroup(
                runControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(runControlsLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(runButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(pauseButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(stopButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(accelerationPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );
            runControlsLayout.setVerticalGroup(
                runControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(stopButton, javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pauseButton, javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(runButton, javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(accelerationPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            );

            tradingLogCheckBox.setText("Trading Log");
            tradingLogCheckBox.setToolTipText("<html>\nIf checked, a log of all created order, <br>\ntransactions between traders and more details<br>\n will be written to disk. You can later anlyze the details.<br> \n<font color='red'>ATTENTION: <br>\nturning this option on, A LOT of date will be written<br>\n to your disk. You may run out of disk space. </font>\n</html>\n ");
            tradingLogCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
            tradingLogCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    tradingLogCheckBoxActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(runControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tradingLogCheckBox, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(clock, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tradingLogCheckBox)
                            .addComponent(runControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(clock, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 10, Short.MAX_VALUE))
            );

            getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

            jSplitPane4.setDividerLocation(300);
            jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
            jSplitPane4.setRightComponent(orderBooksHorizontal);
            jSplitPane4.setLeftComponent(chartPanel);

            jSplitPane3.setRightComponent(jSplitPane4);
            jSplitPane3.setLeftComponent(quoteVertical1);

            jSplitPane5.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
            jSplitPane5.setTopComponent(statistics1);
            jSplitPane5.setRightComponent(clock1);
            jSplitPane5.setRightComponent(traderListPanel);

            jSplitPane3.setLeftComponent(jSplitPane5);

            getContentPane().add(jSplitPane3, java.awt.BorderLayout.CENTER);

            fileMenu.setMnemonic('f');
            fileMenu.setText("File");

            fileOpenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            fileOpenMenuItem.setMnemonic('o');
            fileOpenMenuItem.setText("Open");
            fileOpenMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    fileOpenMenuItemActionPerformed(evt);
                }
            });
            fileMenu.add(fileOpenMenuItem);

            fileSaveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            fileSaveMenuItem.setMnemonic('s');
            fileSaveMenuItem.setText("Save");
            fileSaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    fileSaveMenuItemActionPerformed(evt);
                }
            });
            fileMenu.add(fileSaveMenuItem);

            fileSaveAsMenuItem.setMnemonic('a');
            fileSaveAsMenuItem.setText("Save As ...");
            fileSaveAsMenuItem.setDisplayedMnemonicIndex(5);
            fileSaveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    fileSaveAsMenuItemActionPerformed(evt);
                }
            });
            fileMenu.add(fileSaveAsMenuItem);

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

            exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
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
            simMenu.add(jSeparator3);

            randomSeed.setText("Random seed");
            randomSeed.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    randomSeedActionPerformed(evt);
                }
            });
            simMenu.add(randomSeed);

            menuBar.add(simMenu);

            viewMenu.setMnemonic('v');
            viewMenu.setText("View");

            viewTraderListCheckBox.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, 0));
            viewTraderListCheckBox.setMnemonic('t');
            viewTraderListCheckBox.setText("Traders");
            viewTraderListCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    viewTraderListCheckBoxActionPerformed(evt);
                }
            });
            viewMenu.add(viewTraderListCheckBox);

            viewRawOrderBook.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, 0));
            viewRawOrderBook.setMnemonic('R');
            viewRawOrderBook.setText("Level 3 Orde Book");
            viewRawOrderBook.setToolTipText("");
            viewRawOrderBook.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    viewRawOrderBookActionPerformed(evt);
                }
            });
            viewMenu.add(viewRawOrderBook);

            viewUnlimitedOrdes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, 0));
            viewUnlimitedOrdes.setText("Market Orders View");
            viewUnlimitedOrdes.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    viewUnlimitedOrdesActionPerformed(evt);
                }
            });
            viewMenu.add(viewUnlimitedOrdes);

            viewStopOrders.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 0));
            viewStopOrders.setText("Stop Orders View");
            viewStopOrders.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    viewStopOrdersActionPerformed(evt);
                }
            });
            viewMenu.add(viewStopOrders);

            viewLog.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, 0));
            viewLog.setText("Log Window");
            viewLog.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    viewLogActionPerformed(evt);
                }
            });
            viewMenu.add(viewLog);

            viewTradingLog.setText("Trading Log");
            viewTradingLog.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    viewTradingLogActionPerformed(evt);
                }
            });
            viewMenu.add(viewTradingLog);

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

        if (Globals.sim.getPause()) {
            this.runButton.setEnabled(false);
            this.pauseButton.setEnabled(true);
            Globals.sim.setPause(false);

        } else {
            this.runButton.setEnabled(true);
            this.pauseButton.setEnabled(false);
            Globals.sim.setPause(true);
        }

    }

    void startSim() {

        if (Globals.sim.getPause()) {

            pauseSim();
            return;
        }

        this.runButton.setEnabled(false);
        this.stopButton.setEnabled(true);

        //Globals.sim.getExchange().terminate();
        Globals.sim.reset();
        Globals.sim.startTraders(Globals.getConfig());

        this.setTradingLogFile();
        try {
            Globals.sim.getExchange().setTradingLog(tradingLogCheckBox.isSelected());
        } catch (FileNotFoundException ex) {
            sesim.Logger.error("Cannot write log %s: %s", logFileName, ex.getMessage());
            tradingLogCheckBox.setSelected(false);
        }

        Globals.sim.setPause(false);
        Globals.sim.startScheduler();

        this.accelerationPanel1.initAcceleration();

        Globals.sim.setAcceleration((Double) this.accelerationPanel1.accelSpinner.getValue());

        chartPanel.reset();
        if (this.rawOrderBookDialog != null) {
            rawOrderBookDialog.start(Globals.sim.getExchange(), Order.BUYLIMIT, Order.SELLLIMIT);
        }

        this.orderBooksHorizontal.start();

        this.stopButton.setEnabled(true);
        this.pauseButton.setEnabled(true);

        this.orderBooksHorizontal.invalidate();
        this.orderBooksHorizontal.repaint();
        this.clock.invalidate();
        this.clock.repaint();

        this.chartPanel.reset();

        this.clock.invalidate();
        this.clock.repaint();

    }

    void stopSim() {
        Globals.sim.stop();
        this.stopButton.setEnabled(false);
        this.pauseButton.setEnabled(false);
        this.runButton.setEnabled(true);
    }

    void resetSim() {
        //      Globals.sim.getExchange().terminate();
        Globals.sim.reset();
        chartPanel.reset();
        if (this.rawOrderBookDialog != null) {
            this.rawOrderBookDialog.start(Globals.sim.getExchange(), Order.BUYLIMIT, Order.SELLLIMIT);
        }

        this.orderBooksHorizontal.start();

//        chart.initChart();
//        chart.invalidate();
//        chart.repaint();
    }

    private void initSim() {
        this.runButton.setEnabled(true);
        this.stopButton.setEnabled(false);
        this.pauseButton.setEnabled(false);
    }


    private void editPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPreferencesActionPerformed
        //      Globals.LOGGER.info("Edit prefs...");

        Dialog d = new gui.EditPreferencesDialog(this, rootPaneCheckingEnabled);
        d.setVisible(rootPaneCheckingEnabled);
    }//GEN-LAST:event_editPreferencesActionPerformed


    private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed
        EditAutoTraderListDialog ed = new EditAutoTraderListDialog(this, true);
        ed.setVisible(rootPaneCheckingEnabled);


    }//GEN-LAST:event_deleteMenuItemActionPerformed

    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
        EditStrategiesDialog s = new EditStrategiesDialog(this, false);

        s.setVisible(rootPaneCheckingEnabled);


    }//GEN-LAST:event_pasteMenuItemActionPerformed

    //   private final LoggerDialog log_d = new LoggerDialog(this, false);

    private void fileOpenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileOpenMenuItemActionPerformed
        JFileChooser fc = getFileChooser();

        while (true) {
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            try {
                File f = fc.getSelectedFile();
                JSONObject cfg = Globals.loadConfigFromFile(f);
                Globals.putConfig(cfg);
                String workdir = fc.getCurrentDirectory().getAbsolutePath();
                Globals.prefs_new.put(Globals.PrefKeys.WORKDIR, workdir);
                Globals.prefs_new.put(Globals.PrefKeys.CURRENTFILE, f.getAbsolutePath());
                setTitle(f.getName());
                return;

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Can't load file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }//GEN-LAST:event_fileOpenMenuItemActionPerformed

    // initialize a JFileChoser with  working directory and extension
    private JFileChooser getFileChooser() {
        JFileChooser fc = new JFileChooser();

        String workdir = Globals.prefs_new.get(Globals.PrefKeys.WORKDIR, "");
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
            String current_file = Globals.prefs_new.get(Globals.PrefKeys.CURRENTFILE, "");
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
            Globals.prefs_new.put(Globals.PrefKeys.WORKDIR, workdir);
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
                // System.out.printf("Filter", selected_filter.toString());
                if (!fn.toLowerCase().endsWith("." + Globals.SESIM_FILEEXTENSION)) {
                    f = new File(fn + "." + Globals.SESIM_FILEEXTENSION);
                }
            }

            try {
                Globals.saveFile(f, Globals.getConfig());
                //Globals.prefs_new.put(Globals.PrefKeys.CURRENTFILE, fn);
                setTitle(f.getName());
                return;

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, "Can't save file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            }

        }

    }


    private void fileSaveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileSaveAsMenuItemActionPerformed

        this.saveFile(true);
    }//GEN-LAST:event_fileSaveAsMenuItemActionPerformed

    private void editExchangeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editExchangeMenuItemActionPerformed
        EditExchangeDialog ed = new EditExchangeDialog((Frame) this.getParent(), true);
        int rc = ed.showdialog();
        //  System.out.printf("EDRET: %d\n",rc);

    }//GEN-LAST:event_editExchangeMenuItemActionPerformed

    private void resetToDefaults() {
        InputStream is = getClass().getResourceAsStream("/files/defaultcfg.json");
        String df = new Scanner(is, "UTF-8").useDelimiter("\\A").next();

        try {
            JSONObject cfg = Globals.loadConfigFromString(df);
            Globals.putConfig(cfg);
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

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        //Globals.sim.getExchange().timer.pause();
        pauseSim();
    }//GEN-LAST:event_pauseButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        startSim();
    }//GEN-LAST:event_runButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        stopSim();
    }//GEN-LAST:event_stopButtonActionPerformed

    RawOrderBookDialog rawOrderBookDialog = null;

//new gui.orderbook.OrderBookDialog(this, false);

    private void viewRawOrderBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewRawOrderBookActionPerformed

        javax.swing.SwingUtilities.invokeLater(() -> {
            //  TraderListDialog traderListDialog = null;

            if (this.viewRawOrderBook.getState()) {
                if (rawOrderBookDialog == null) {

                    rawOrderBookDialog = new RawOrderBookDialog(this, false);

                    rawOrderBookDialog.start(Globals.sim.getExchange(), Order.BUYLIMIT, Order.SELLLIMIT);
                    rawOrderBookDialog.setTitle("Level 3 Order Book");

                    rawOrderBookDialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            super.windowClosing(e);

                            //  System.out.printf("Closingn");
                            rawOrderBookDialog.dispose();
                            rawOrderBookDialog.stop();
                            rawOrderBookDialog = null;
                            viewRawOrderBook.setState(false);
                        }
                    });

                }

                rawOrderBookDialog.setVisible(true);
            } else if (rawOrderBookDialog != null) {
                //         System.out.printf("Set visible = false\n");
                rawOrderBookDialog.setVisible(false);

            }
        });

        //  rawOrderBookDialog.setVisible(rootPaneCheckingEnabled);
    }//GEN-LAST:event_viewRawOrderBookActionPerformed


    private void viewTraderListCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewTraderListCheckBoxActionPerformed

        javax.swing.SwingUtilities.invokeLater(() -> {
            TraderListDialog traderListDialog = null;

            if (this.viewTraderListCheckBox.getState()) {
                if (traderListDialog == null) {
                    traderListDialog = new TraderListDialog(this, false);
                    traderListDialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            super.windowClosing(e);
                            viewTraderListCheckBox.setState(false);
                            System.out.printf("Set menu false\n");
                        }
                    });

                }

                traderListDialog.setVisible(true);
            } else if (traderListDialog != null) {
                //  System.out.printf("Set visible = false\n");
                traderListDialog.setVisible(false);
            }
        });

    }//GEN-LAST:event_viewTraderListCheckBoxActionPerformed

    private void fileSaveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileSaveMenuItemActionPerformed
        saveFile(false);

    }//GEN-LAST:event_fileSaveMenuItemActionPerformed

    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
        Globals.prefs_new.put(Globals.PrefKeys.CURRENTFILE, "");
        setTitle("");
    }//GEN-LAST:event_closeMenuItemActionPerformed

    private void clearMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMenuItemActionPerformed

        int dialogResult = JOptionPane.showConfirmDialog(this, "Are you sure?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult != JOptionPane.YES_OPTION) {
            return;
        }
        Globals.clearAll();
    }//GEN-LAST:event_clearMenuItemActionPerformed

    RawOrderBookDialog stopOrderBookDialog = null;

    RawOrderBookDialog unlimitedOrdersDialog = null;

    private void viewUnlimitedOrdesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewUnlimitedOrdesActionPerformed

        javax.swing.SwingUtilities.invokeLater(() -> {
            //  TraderListDialog traderListDialog = null;

            if (this.viewUnlimitedOrdes.getState()) {
                if (unlimitedOrdersDialog == null) {

                    unlimitedOrdersDialog = new RawOrderBookDialog(this, false);

                    unlimitedOrdersDialog.start(Globals.sim.getExchange(), Order.BUY, Order.SELL);
                    unlimitedOrdersDialog.setTitle("Unlimited Orders");

                    unlimitedOrdersDialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            super.windowClosing(e);

                            //                   System.out.printf("Closingn");
                            unlimitedOrdersDialog.dispose();
                            unlimitedOrdersDialog.stop();
                            unlimitedOrdersDialog = null;
                            viewUnlimitedOrdes.setState(false);
                        }
                    });

                }

                unlimitedOrdersDialog.setVisible(true);
            } else if (unlimitedOrdersDialog != null) {
                //    System.out.printf("Set visible = false\n");
                unlimitedOrdersDialog.setVisible(false);

            }
        });


    }//GEN-LAST:event_viewUnlimitedOrdesActionPerformed

    private void viewStopOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewStopOrdersActionPerformed

        javax.swing.SwingUtilities.invokeLater(() -> {
            //  TraderListDialog traderListDialog = null;

            if (this.viewStopOrders.getState()) {
                if (stopOrderBookDialog == null) {

                    stopOrderBookDialog = new RawOrderBookDialog(this, false);

                    stopOrderBookDialog.start(Globals.sim.getExchange(), Order.BUYSTOP, Order.SELLSTOP);
                    stopOrderBookDialog.setTitle("Stop Orders");
                    stopOrderBookDialog.setTitles("Stop Buy", "Stop Sell");
                    stopOrderBookDialog.setPriceColumn(Order.STOP);

                    stopOrderBookDialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            super.windowClosing(e);

                            //      System.out.printf("Closingn");
                            stopOrderBookDialog.dispose();
                            stopOrderBookDialog.stop();
                            stopOrderBookDialog = null;
                            viewStopOrders.setState(false);
                        }
                    });

                }

                stopOrderBookDialog.setVisible(true);
            } else if (unlimitedOrdersDialog != null) {
                //  System.out.printf("Set visible = false\n");
                stopOrderBookDialog.setVisible(false);

            }
        });

    }//GEN-LAST:event_viewStopOrdersActionPerformed


    private void randomSeedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomSeedActionPerformed
        JDialog d = new RandomSeedDialog(this, true);
        d.setVisible(true);
    }//GEN-LAST:event_randomSeedActionPerformed

    LogDialog logDialog;
    private void viewLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewLogActionPerformed
        if (logDialog == null) {
            logDialog = new LogDialog(this, false);
            logDialog.setLocationRelativeTo(this);
        }

        logDialog.setVisible(viewLog.isSelected());

    }//GEN-LAST:event_viewLogActionPerformed

    String logFileName = null;

    void setTradingLogFile() {

        String logDir = Globals.prefs_new.get(Globals.DATADIR, "");
        Path directoryPath = Paths.get(logDir);

        if (Files.notExists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);

            } catch (IOException e) {
                sesim.Logger.error("Creating data directory %s: %s", logDir, e.getMessage());
                this.tradingLogCheckBox.setSelected(false);
                return;
            }
        }

        logFileName = directoryPath.resolve("tradinglog.dat").toString();
        Globals.sim.getExchange().setTradingLogFile(logFileName);
    }


    private void tradingLogCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradingLogCheckBoxActionPerformed

        try {
            Globals.sim.getExchange().setTradingLog(tradingLogCheckBox.isSelected());
        } catch (FileNotFoundException ex) {
            sesim.Logger.error("Cannot write log %s: %s", logFileName, ex.getMessage());
            tradingLogCheckBox.setSelected(false);
        }

    }//GEN-LAST:event_tradingLogCheckBoxActionPerformed

    TradingLogDialog tradingLogDialog = null;

    private void viewTradingLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewTradingLogActionPerformed
        javax.swing.SwingUtilities.invokeLater(() -> {

            if (this.viewTradingLog.getState()) {
                if (tradingLogDialog == null) {
                    tradingLogDialog = new TradingLogDialog(this, false);
                    tradingLogDialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            super.windowClosing(e);
                            viewTraderListCheckBox.setState(false);
                            System.out.printf("Set menu false\n");
                        }
                    });

                }
                tradingLogDialog.setLocationRelativeTo(SeSimApplication.this);
                tradingLogDialog.setVisible(true);
            } else if (tradingLogDialog != null) {
                //  System.out.printf("Set visible = false\n");
                tradingLogDialog.setVisible(false);
            }
        });
        // TODO add your handling code here:
    }//GEN-LAST:event_viewTradingLogActionPerformed
    static boolean f = false;




    /**
     * @param args the command line arguments
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InstantiationException
     */
    public static void main(String args[]) throws IllegalAccessException, InstantiationException {

     /*   Platform.startup(() -> {
            // Initialize JavaFX Runtime 
        });*/

        // Initialize logging
        Logger rootLogger = sesim.Logger.getLogger();

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new SimpleFormatter());

        // Append handler
        rootLogger.addHandler(consoleHandler);
        rootLogger.setLevel(Level.ALL);

        // init globals
        Globals.initGlobals();
        Globals.prefs_new = Preferences.userRoot().node("/opensesim");
        
        sesim.Logger.info("Data directory is %s", Globals.getDataDir());

        Globals.setLookAndFeel();

        Globals.prefs_new.put(Globals.PrefKeys.CURRENTFILE, "");

        // Create a Sim instance
        Globals.sim = new sesim.Sim();

    /*    JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(false);
        JPopupMenu.setDefaultLightWeightPopupEnabled(true);*/

     /*   UIManager.installLookAndFeel("FlatLaf Light", "com.formdev.flatlaf.FlatLightLaf");
        UIManager.installLookAndFeel("FlatLaf Dark", "com.formdev.flatlaf.FlatDarkLaf");
        UIManager.installLookAndFeel("FlatLaf IntelliJ", "com.formdev.flatlaf.FlatIntelliJLaf");
        UIManager.installLookAndFeel("FlatLaf Darcula", "com.formdev.flatlaf.FlatDarculaLaf");*/

        /*          try {
            Class.forName("mdlaf.MaterialLookAndFeel");
            UIManager.installLookAndFeel("Material UI", "mdlaf.MaterialLookAndFeel");
        } catch (ClassNotFoundException e) {
            // Material-UI nicht verfügbar
        }*/
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SeSimApplication app = new SeSimApplication();
                app.setLocationRelativeTo(null);
                app.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private gui.AccelerationPanel accelerationPanel1;
    private chart.ChartPanel chartPanel;
    private javax.swing.JMenuItem clearMenuItem;
    private gui.Clock clock;
    private gui.Clock clock1;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenuItem editExchangeMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editPreferences;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fileOpenMenuItem;
    private javax.swing.JMenuItem fileSaveAsMenuItem;
    private javax.swing.JMenuItem fileSaveMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JSplitPane jSplitPane5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToolBar meinToolBar;
    private javax.swing.JMenuBar menuBar;
    private gui.orderbook.RawOrderBook orderBookNew1;
    private gui.orderbook.OrderBooksHorizontal orderBooksHorizontal;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JButton pauseButton;
    private gui.orderbook.QuoteVertical quoteVertical1;
    private javax.swing.JMenuItem randomSeed;
    private javax.swing.JMenuItem resetToDefaultsMenuItem;
    private javax.swing.JButton runButton;
    private javax.swing.JPanel runControls;
    private javax.swing.JMenu simMenu;
    private javax.swing.JMenuItem simMenuPause;
    private javax.swing.JMenuItem simMenuStart;
    private javax.swing.JMenuItem simMenuStop;
    private gui.Statistics statistics1;
    private javax.swing.JButton stopButton;
    private gui.TraderListPanel traderListPanel;
    private javax.swing.JCheckBox tradingLogCheckBox;
    private javax.swing.JCheckBoxMenuItem viewLog;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JCheckBoxMenuItem viewRawOrderBook;
    private javax.swing.JCheckBoxMenuItem viewStopOrders;
    private javax.swing.JCheckBoxMenuItem viewTraderListCheckBox;
    private javax.swing.JCheckBoxMenuItem viewTradingLog;
    private javax.swing.JCheckBoxMenuItem viewUnlimitedOrdes;
    // End of variables declaration//GEN-END:variables

}
