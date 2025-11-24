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
package traders.RandomTraderM;

import traders.*;
import javax.swing.JDialog;
import org.json.JSONObject;
import sesim.AutoTraderGui;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class RandomTraderMGui extends AutoTraderGui {

    private final RandomTraderM cfg;

    /**
     * Creates new form RandomTraderConfigForm
     *
     * @param cfg
     */
    public RandomTraderMGui(RandomTraderM cfg) {
        initComponents();

      //  this.setPreferredSize(new java.awt.Dimension(600, 400));
        this.cfg = cfg;

        this.initialDelayMin.setValue((Float) (Math.round((cfg.initialDelay[0] / 1000f) * 10f) / 10f));
        this.initialDelayMax.setValue((Float) (Math.round((cfg.initialDelay[1] / 1000f) * 10f) / 10f));

        //  this.buyVolMin.setValue(cfg.amountToBuy[0]);
        //  this.buyVolMax.setValue(cfg.amountToBuy[1]);
        //  this.sellVolMin.setValue(cfg.amountToSell[0]);
//        this.sellVolMax.setValue(cfg.amountToSell[1]);
        this.buyVolMin.setValue((Float) (Math.round((cfg.amountToBuy[0] / 100f) * 10f) / 10f));
        this.buyVolMax.setValue((Float) (Math.round((cfg.amountToBuy[1] / 100f) * 10f) / 10f));

        this.sellVolMin.setValue((Float) (Math.round((cfg.amountToSell[0] / 100f) * 10f) / 10f));
        this.sellVolMax.setValue((Float) (Math.round((cfg.amountToSell[1] / 100f) * 10f) / 10f));

        this.buyLimitMin.setValue((Float) (Math.round((cfg.buyLimit[0] / 100f) * 100f) / 100f));
        this.buyLimitMax.setValue((Float) (Math.round((cfg.buyLimit[1] / 100f) * 100f) / 100f));

        this.sellLimitMin.setValue((Float) (Math.round((cfg.sellLimit[0] / 100f) * 100f) / 100f));
        this.sellLimitMax.setValue((Float) (Math.round((cfg.sellLimit[1] / 100f) * 100f) / 100f));

        // this.buyWaitMin.setValue(cfg.buy_wait[0]);
        //  this.buyWaitMax.setValue(cfg.buy_wait[1]);
        this.buyWaitMin.setValue((Float) (Math.round((cfg.buyOrderTimeout[0] / 1000f) * 10f) / 10f));
        this.buyWaitMax.setValue((Float) (Math.round((cfg.buyOrderTimeout[1] / 1000f) * 10f) / 10f));

        this.sellWaitMin.setValue((Float) (Math.round((cfg.sellOrderTimeout[0] / 1000f) * 10f) / 10f));
        this.sellWaitMax.setValue((Float) (Math.round((cfg.sellOrderTimeout[1] / 1000f) * 10f) / 10f));

        this.waitAfterBuyMin.setValue((Float) (Math.round((cfg.sleepAfterBuy[0] / 1000f) * 10f) / 10f));
        this.waitAfterBuyMax.setValue((Float) (Math.round((cfg.sleepAfterBuy[1] / 1000f) * 10f) / 10f));

        this.waitAfterSellMin.setValue((Float) (Math.round((cfg.sleepAfterSell[0] / 1000f) * 10f) / 10f));
        this.waitAfterSellMax.setValue((Float) (Math.round((cfg.sleepAfterSell[1] / 1000f) * 10f) / 10f));

        this.bankruptShares.setValue(cfg.bankrupt_shares);
        this.bankruptCash.setValue(cfg.bankrupt_cash);
        
        
        this.minAmountToBuyDeviation.setValue(cfg.minAmountToBuyDeviation);
        this.minAmountToSellDeviation.setValue(cfg.minAmountToSellDeviation);
        this.minSelLimitDeviation.setValue(cfg.minSellDeviation);
        this.minbuyLimitDeviation.setValue(cfg.minBuyDeviation);
        
        this.moodCheckBox.setSelected(cfg.moodEnable);
        this.moodFrequencySpinner.setValue(cfg.moodFrequency);
        this.moodinessSpinner.setValue(cfg.moodiness);

        //    } catch (Exception e) {
        //  }
        this.setMood(this.moodCheckBox.isSelected());
    }

    @Override
    public void save() {

        cfg.initialDelay[0] = (long) (1000f * (Float) this.initialDelayMin.getValue());
        cfg.initialDelay[1] = (long) (1000f * (Float) this.initialDelayMax.getValue());

        /*cfg.amountToBuy[0] = (Float) this.buyVolMin.getValue();
        cfg.amountToBuy[1] = (Float) this.buyVolMax.getValue();
        cfg.amountToSell[0] = (Float) this.sellVolMin.getValue();
        cfg.amountToSell[1] = (Float) this.sellVolMax.getValue();*/

        cfg.amountToBuy[0] = (long) (100f * (Float) this.buyVolMin.getValue());
        cfg.amountToBuy[1] = (long) (100f * (Float) this.buyVolMax.getValue());

        cfg.amountToSell[0] = (long) (100f * (Float) this.sellVolMin.getValue());
        cfg.amountToSell[1] = (long) (100f * (Float) this.sellVolMax.getValue());

        cfg.buyLimit[0] = (long) (100f * (Float) this.buyLimitMin.getValue());
        cfg.buyLimit[1] = (long) (100f * (Float) this.buyLimitMax.getValue());

        cfg.sellLimit[0] = (long) (100f * (Float) this.sellLimitMin.getValue());
        cfg.sellLimit[1] = (long) (100f * (Float) this.sellLimitMax.getValue());

        cfg.buyOrderTimeout[0] = (long) (1000f * (Float) this.buyWaitMin.getValue());
        cfg.buyOrderTimeout[1] = (long) (1000f * (Float) this.buyWaitMax.getValue());

        cfg.sellOrderTimeout[0] = (long) (1000f * (Float) this.sellWaitMin.getValue());
        cfg.sellOrderTimeout[1] = (long) (1000f * (Float) this.sellWaitMax.getValue());

        cfg.sleepAfterBuy[0] = (long) (1000f * (Float) this.waitAfterBuyMin.getValue());
        cfg.buyOrderTimeout[1] = (long) (1000f * (Float) this.buyWaitMax.getValue());

        cfg.sellOrderTimeout[0] = (long) (1000f * (Float) this.sellWaitMin.getValue());
        cfg.sellOrderTimeout[1] = (long) (1000f * (Float) this.sellWaitMax.getValue());

        cfg.sleepAfterBuy[0] = (long) (1000f * (Float) this.waitAfterBuyMin.getValue());
        cfg.sleepAfterBuy[1] = (long) (1000f * (Float) this.waitAfterBuyMax.getValue());

        cfg.sleepAfterSell[0] = (long) (1000f * (Float) this.waitAfterSellMin.getValue());
        cfg.sleepAfterSell[1] = (long) (1000f * (Float) this.waitAfterSellMax.getValue());

        cfg.bankrupt_shares =  (Long)this.bankruptShares.getValue();
        cfg.bankrupt_cash =  (Long)this.bankruptCash.getValue();
        
        
        cfg.minAmountToBuyDeviation=(Long) this.minAmountToBuyDeviation.getValue();
        cfg.minAmountToSellDeviation=(Long) this.minAmountToSellDeviation.getValue();
        cfg.minBuyDeviation=(Long) this.minbuyLimitDeviation.getValue();
        cfg.minSellDeviation=(Long) this.minSelLimitDeviation.getValue();
        
        cfg.moodEnable=(Boolean) this.moodCheckBox.isSelected();
        cfg.moodiness=(Float) this.moodinessSpinner.getValue();
        cfg.moodFrequency=(Float) this.moodFrequencySpinner.getValue();

        //JSONObject j = cfg.getConfig();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jSpinner1 = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        initialDelayMin = new javax.swing.JSpinner();
        initialDelayMax = new javax.swing.JSpinner();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        waitAfterBuyMin = new javax.swing.JSpinner();
        waitAfterSellMin = new javax.swing.JSpinner();
        waitAfterSellMax = new javax.swing.JSpinner();
        waitAfterBuyMax = new javax.swing.JSpinner();
        buyVolMin = new javax.swing.JSpinner();
        minbuyLimitDeviation = new javax.swing.JSpinner();
        buyVolMax = new javax.swing.JSpinner();
        minSelLimitDeviation = new javax.swing.JSpinner();
        sellVolMax = new javax.swing.JSpinner();
        minAmountToSellDeviation = new javax.swing.JSpinner();
        sellVolMin = new javax.swing.JSpinner();
        minAmountToBuyDeviation = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        buyWaitMin = new javax.swing.JSpinner();
        sellWaitMin = new javax.swing.JSpinner();
        sellWaitMax = new javax.swing.JSpinner();
        buyWaitMax = new javax.swing.JSpinner();
        buyLimitMin = new javax.swing.JSpinner();
        buyLimitMax = new javax.swing.JSpinner();
        sellLimitMax = new javax.swing.JSpinner();
        sellLimitMin = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        moodynessPannel = new javax.swing.JPanel();
        moodCheckBox = new javax.swing.JCheckBox();
        moodinessSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        moodFrequencySpinner = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        bankruptShares = new javax.swing.JSpinner();
        bankruptCash = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();

        jTextField1.setText("jTextField1");

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel15.setText("Buy order timeout (sec):");
        jLabel15.setToolTipText("Maximum time to wait for a buy order to be filled before it is canceled.");

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel18.setText("Sell order timeout (sec):");
        jLabel18.setToolTipText("Maximum time to wait for a sell order to be filled before it is canceled.");

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel23.setText("Sleep after buy (sec):");
        jLabel23.setToolTipText("Idle time after completing a buy order before placing another trade.");

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel24.setText("Sleep after sell (sec):");
        jLabel24.setToolTipText("Idle time after completing a sell order before placing another trade.");

        initialDelayMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 10.0f));

        initialDelayMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 10.0f));

        jLabel25.setText("maximum");
        jLabel25.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel26.setText("minimum:");

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel27.setText("Initial delay (sec):");
        jLabel27.setToolTipText("Time to wait before the bot starts trading.");

        waitAfterBuyMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        waitAfterSellMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        waitAfterSellMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        waitAfterBuyMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        buyVolMin.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), Float.valueOf(1.0f)));

        minbuyLimitDeviation.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));

        buyVolMax.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), Float.valueOf(1.0f)));

        minSelLimitDeviation.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));

        sellVolMax.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), Float.valueOf(1.0f)));

        minAmountToSellDeviation.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));

        sellVolMin.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), Float.valueOf(1.0f)));

        minAmountToBuyDeviation.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Amount to buy (%):");
        jLabel9.setToolTipText("Percentage of available funds used for each buy order.");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Amount to sell (%):");
        jLabel10.setToolTipText("Percentage of owned shares sold per sell order.");

        buyWaitMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, -24.0f));

        sellWaitMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        sellWaitMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        buyWaitMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        buyLimitMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, -100.0f, null, 1.0f));

        buyLimitMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, -100.0f, null, 1.0f));

        sellLimitMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, -100.0f, null, 1.0f));

        sellLimitMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, -100.0f, null, 1.0f));

        jLabel3.setText("min. abs. deviation");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Buy limit (in %):");
        jLabel11.setToolTipText("Maximum price deviation allowed when buying (positive or negative).");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Sell limit (in %):");
        jLabel12.setToolTipText("Maximum price deviation allowed when selling (positive or negative).");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(waitAfterBuyMin, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(waitAfterSellMin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(waitAfterSellMax)
                            .addComponent(waitAfterBuyMax, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(initialDelayMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buyVolMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sellVolMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buyLimitMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sellLimitMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buyWaitMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sellWaitMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(sellWaitMax, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(buyWaitMax, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(sellLimitMax, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(buyLimitMax, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(sellVolMax)
                                .addComponent(buyVolMax)
                                .addComponent(initialDelayMax)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(minbuyLimitDeviation)
                    .addComponent(minSelLimitDeviation)
                    .addComponent(minAmountToSellDeviation)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(minAmountToBuyDeviation))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel25)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(initialDelayMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(initialDelayMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buyVolMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buyVolMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(minAmountToBuyDeviation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minAmountToSellDeviation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(sellVolMax, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sellVolMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buyLimitMin, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buyLimitMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(minbuyLimitDeviation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sellLimitMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sellLimitMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(minSelLimitDeviation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buyWaitMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(buyWaitMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sellWaitMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sellWaitMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(waitAfterBuyMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waitAfterBuyMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(waitAfterSellMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waitAfterSellMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        moodCheckBox.setText("Mood");
        moodCheckBox.setToolTipText("<html>\nToggle whether traders<br>\nare moody.\n</html>");
        moodCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moodCheckBoxActionPerformed(evt);
            }
        });

        moodinessSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1.0f));
        moodinessSpinner.setToolTipText("<html>\nHow strongly the<br>\ntraders’ mood swings.\n</html>");

        jLabel4.setText("Moodiness:");

        jLabel5.setText("Mood Frequency:");

        moodFrequencySpinner.setModel(new javax.swing.SpinnerNumberModel(50.0f, 1.0f, null, 1.0f));
        moodFrequencySpinner.setToolTipText("<html>\nHow often the traders’ <br>\nmood is updated.\n<html>");

        javax.swing.GroupLayout moodynessPannelLayout = new javax.swing.GroupLayout(moodynessPannel);
        moodynessPannel.setLayout(moodynessPannelLayout);
        moodynessPannelLayout.setHorizontalGroup(
            moodynessPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(moodynessPannelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(moodynessPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(moodynessPannelLayout.createSequentialGroup()
                        .addComponent(moodCheckBox)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(moodynessPannelLayout.createSequentialGroup()
                        .addGroup(moodynessPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(moodynessPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(moodinessSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .addComponent(moodFrequencySpinner)))))
        );
        moodynessPannelLayout.setVerticalGroup(
            moodynessPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(moodynessPannelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(moodCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(moodynessPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(moodinessSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(moodynessPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(moodFrequencySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Bankrupt if sahres are less than ");

        bankruptShares.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));

        bankruptCash.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));

        jLabel2.setText("and cash is below");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(bankruptCash, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(bankruptShares))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bankruptShares, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bankruptCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(moodynessPannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(moodynessPannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void setMood(boolean val){
        this.moodFrequencySpinner.setEnabled(val);
        this.moodinessSpinner.setEnabled(val);
    }
    
    private void moodCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moodCheckBoxActionPerformed
        this.setMood(this.moodCheckBox.isSelected());
    }//GEN-LAST:event_moodCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner bankruptCash;
    private javax.swing.JSpinner bankruptShares;
    private javax.swing.JSpinner buyLimitMax;
    private javax.swing.JSpinner buyLimitMin;
    private javax.swing.JSpinner buyVolMax;
    private javax.swing.JSpinner buyVolMin;
    private javax.swing.JSpinner buyWaitMax;
    private javax.swing.JSpinner buyWaitMin;
    private javax.swing.JSpinner initialDelayMax;
    private javax.swing.JSpinner initialDelayMin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JSpinner minAmountToBuyDeviation;
    private javax.swing.JSpinner minAmountToSellDeviation;
    private javax.swing.JSpinner minSelLimitDeviation;
    private javax.swing.JSpinner minbuyLimitDeviation;
    private javax.swing.JCheckBox moodCheckBox;
    private javax.swing.JSpinner moodFrequencySpinner;
    private javax.swing.JSpinner moodinessSpinner;
    private javax.swing.JPanel moodynessPannel;
    private javax.swing.JSpinner sellLimitMax;
    private javax.swing.JSpinner sellLimitMin;
    private javax.swing.JSpinner sellVolMax;
    private javax.swing.JSpinner sellVolMin;
    private javax.swing.JSpinner sellWaitMax;
    private javax.swing.JSpinner sellWaitMin;
    private javax.swing.JSpinner waitAfterBuyMax;
    private javax.swing.JSpinner waitAfterBuyMin;
    private javax.swing.JSpinner waitAfterSellMax;
    private javax.swing.JSpinner waitAfterSellMin;
    // End of variables declaration//GEN-END:variables
}
