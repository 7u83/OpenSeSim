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
package traders;

import javax.swing.JDialog;
import org.json.JSONObject;
import sesim.AutoTraderGui;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class RandomTraderLGui extends AutoTraderGui {

    private final RandomTraderL cfg;

    /**
     * Creates new form RandomTraderConfigForm
     *
     * @param cfg
     */
    public RandomTraderLGui(RandomTraderL cfg) {
        initComponents();

        this.setPreferredSize(new java.awt.Dimension(600, 400));
        this.cfg = cfg;

        this.initialDelayMin.setValue((Float) (Math.round((cfg.initialDelay[0] / 1000f) * 10f) / 10f));
        this.initialDelayMax.setValue((Float) (Math.round((cfg.initialDelay[1] / 1000f) * 10f) / 10f));

        //  this.buyVolMin.setValue(cfg.amountToBuy[0]);
        //  this.buyVolMax.setValue(cfg.amountToBuy[1]);
        //  this.sellVolMin.setValue(cfg.amountToSell[0]);
//        this.sellVolMax.setValue(cfg.amountToSell[1]);
        this.buyVolMin.setValue((Float) (Math.round((cfg.amountToBuy[0] / 10f) * 10f) / 10f));
        this.buyVolMax.setValue((Float) (Math.round((cfg.amountToBuy[1] / 10f) * 10f) / 10f));

        this.sellVolMin.setValue((Float) (Math.round((cfg.amountToSell[0] / 10f) * 10f) / 10f));
        this.sellVolMax.setValue((Float) (Math.round((cfg.amountToSell[1] / 10f) * 10f) / 10f));

        this.buyLimitMin.setValue((Float) (Math.round((cfg.buyLimit[0] / 10f) * 10f) / 10f));
        this.buyLimitMax.setValue((Float) (Math.round((cfg.buyLimit[1] / 10f) * 10f) / 10f));

        this.sellLimitMin.setValue((Float) (Math.round((cfg.sellLimit[0] / 10f) * 10f) / 10f));
        this.sellLimitMax.setValue((Float) (Math.round((cfg.sellLimit[1] / 10f) * 10f) / 10f));

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

        this.bankruptShares.setValue(cfg.bankrupt_shares_cfg);
        this.bankruptCash.setValue(cfg.bankrupt_cash_cfg);
        
        
        this.minAmountToBuyDeviation.setValue(cfg.minAmountToBuyDeviation);
        this.minAmountToSellDeviation.setValue(cfg.minAmountToSellDeviation);
        this.minSelLimitDeviation.setValue(cfg.minSellDeviation);
        this.minbuyLimitDeviation.setValue(cfg.minBuyDeviation);

        //    } catch (Exception e) {
        //  }
    }

    @Override
    public void save() {

        cfg.initialDelay[0] = (long) (1000f * (Float) this.initialDelayMin.getValue());
        cfg.initialDelay[1] = (long) (1000f * (Float) this.initialDelayMax.getValue());

        /*cfg.amountToBuy[0] = (Float) this.buyVolMin.getValue();
        cfg.amountToBuy[1] = (Float) this.buyVolMax.getValue();
        cfg.amountToSell[0] = (Float) this.sellVolMin.getValue();
        cfg.amountToSell[1] = (Float) this.sellVolMax.getValue();*/

        cfg.amountToBuy[0] = (long) (10f * (Float) this.buyVolMin.getValue());
        cfg.amountToBuy[1] = (long) (10f * (Float) this.buyVolMax.getValue());

        cfg.amountToSell[0] = (long) (10f * (Float) this.sellVolMin.getValue());
        cfg.amountToSell[1] = (long) (10f * (Float) this.sellVolMax.getValue());

        cfg.buyLimit[0] = (long) (10f * (Float) this.buyLimitMin.getValue());
        cfg.buyLimit[1] = (long) (10f * (Float) this.buyLimitMax.getValue());

        cfg.sellLimit[0] = (long) (10f * (Float) this.sellLimitMin.getValue());
        cfg.sellLimit[1] = (long) (10f * (Float) this.sellLimitMax.getValue());

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

        cfg.bankrupt_shares_cfg = (Float) this.bankruptShares.getValue();
        cfg.bankrupt_cash_cfg = (Float) this.bankruptCash.getValue();
        
        
        cfg.minAmountToBuyDeviation=(Long) this.minAmountToBuyDeviation.getValue();
        cfg.minAmountToSellDeviation=(Long) this.minAmountToSellDeviation.getValue();
        cfg.minBuyDeviation=(Long) this.minbuyLimitDeviation.getValue();
        cfg.minSellDeviation=(Long) this.minSelLimitDeviation.getValue();

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
        buyWaitMin = new javax.swing.JSpinner();
        sellWaitMin = new javax.swing.JSpinner();
        sellWaitMax = new javax.swing.JSpinner();
        buyWaitMax = new javax.swing.JSpinner();
        buyLimitMin = new javax.swing.JSpinner();
        buyLimitMax = new javax.swing.JSpinner();
        sellLimitMax = new javax.swing.JSpinner();
        sellLimitMin = new javax.swing.JSpinner();
        waitAfterBuyMin = new javax.swing.JSpinner();
        waitAfterSellMin = new javax.swing.JSpinner();
        waitAfterSellMax = new javax.swing.JSpinner();
        waitAfterBuyMax = new javax.swing.JSpinner();
        buyVolMin = new javax.swing.JSpinner();
        buyVolMax = new javax.swing.JSpinner();
        sellVolMax = new javax.swing.JSpinner();
        sellVolMin = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        initialDelayMin = new javax.swing.JSpinner();
        initialDelayMax = new javax.swing.JSpinner();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        bankruptShares = new javax.swing.JSpinner();
        bankruptCash = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        minbuyLimitDeviation = new javax.swing.JSpinner();
        minSelLimitDeviation = new javax.swing.JSpinner();
        minAmountToSellDeviation = new javax.swing.JSpinner();
        minAmountToBuyDeviation = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();

        jTextField1.setText("jTextField1");

        buyWaitMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, -24.0f));

        sellWaitMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        sellWaitMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        buyWaitMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        buyLimitMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, -100.0f, null, 1.0f));

        buyLimitMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, -100.0f, null, 1.0f));

        sellLimitMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, -100.0f, null, 1.0f));

        sellLimitMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, -100.0f, null, 1.0f));

        waitAfterBuyMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        waitAfterSellMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        waitAfterSellMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        waitAfterBuyMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1000.0f));

        buyVolMin.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), Float.valueOf(1.0f)));

        buyVolMax.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), Float.valueOf(1.0f)));

        sellVolMax.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), Float.valueOf(1.0f)));

        sellVolMin.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), Float.valueOf(1.0f)));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Amount to buy (%):");
        jLabel9.setToolTipText("Percentage of available funds used for each buy order.");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Amount to sell (%):");
        jLabel10.setToolTipText("Percentage of owned shares sold per sell order.");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Buy limit (in %):");
        jLabel11.setToolTipText("Maximum price deviation allowed when buying (positive or negative).");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Sell limit (in %):");
        jLabel12.setToolTipText("Maximum price deviation allowed when selling (positive or negative).");

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

        initialDelayMin.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 0.1f));

        initialDelayMax.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 0.1f));

        jLabel25.setText("maximum");
        jLabel25.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel26.setText("minimum:");

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel27.setText("Initial delay (sec):");
        jLabel27.setToolTipText("Time to wait before the bot starts trading.");

        jLabel1.setText("Bankrupt if sahres are less than ");

        bankruptShares.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 0.5f));

        bankruptCash.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 0.1f));

        jLabel2.setText("and cash is below");

        minbuyLimitDeviation.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));

        minSelLimitDeviation.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));

        minAmountToSellDeviation.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));

        minAmountToBuyDeviation.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));

        jLabel3.setText("min. abs. deviation");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(initialDelayMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(buyVolMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sellVolMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(buyLimitMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sellLimitMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(buyWaitMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sellWaitMin, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(sellWaitMax, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buyWaitMax, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(sellLimitMax, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buyLimitMax, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(sellVolMax)
                                        .addComponent(buyVolMax)
                                        .addComponent(initialDelayMax)
                                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(waitAfterBuyMin, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(waitAfterSellMin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(waitAfterSellMax)
                                    .addComponent(waitAfterBuyMax, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(minbuyLimitDeviation)
                            .addComponent(minSelLimitDeviation)
                            .addComponent(minAmountToSellDeviation)
                            .addComponent(minAmountToBuyDeviation)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel2))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(262, 262, 262)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bankruptShares)
                            .addComponent(bankruptCash, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel25)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(initialDelayMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(initialDelayMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buyVolMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buyVolMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(minAmountToBuyDeviation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minAmountToSellDeviation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(sellVolMax, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sellVolMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buyLimitMin, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buyLimitMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(minbuyLimitDeviation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sellLimitMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sellLimitMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(minSelLimitDeviation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buyWaitMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(buyWaitMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sellWaitMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sellWaitMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(waitAfterBuyMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waitAfterBuyMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(waitAfterSellMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waitAfterSellMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bankruptShares, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bankruptCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JSpinner minAmountToBuyDeviation;
    private javax.swing.JSpinner minAmountToSellDeviation;
    private javax.swing.JSpinner minSelLimitDeviation;
    private javax.swing.JSpinner minbuyLimitDeviation;
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
