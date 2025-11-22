/*
 * Copyright (c) 2017, 2025 7u83 <7u83@mail.ru>
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
package traders.MarginTraderL;

import sesim.AutoTraderGui;
import static sesim.util.Math.toFixedLong;

/**
 *
 * @author tube
 */
public class MarginTraderGui extends AutoTraderGui {

    MarginTraderL.Config cfg;

    /**
     * Creates new form MarginTraderGui
     */
    public MarginTraderGui(MarginTraderL.Config cfg) {
        initComponents();
        this.cfg = cfg;

        this.initialDelayMin.setValue(cfg.initialDelay / 1000.0);
        this.initalDelayMax.setValue((cfg.initialDelay + cfg.initialDelayRange) / 1000.0);
        this.minShortLimit.setValue((double) (cfg.minShortLimit) / 100.0);
        this.maxShortLimit.setValue((double) (cfg.maxShortLimit) / 100.0);
        this.minLongLimit.setValue((double) (cfg.minLongLimit) / 100.0);
        this.maxLongLimit.setValue((double) (cfg.maxLongLimit) / 100.0);

        this.minWaitforFill.setValue(cfg.waitForFill / 1000.0);
        this.maxWaitForFill.setValue((cfg.waitForFill + cfg.waitForFillRange) / 1000.0);

        this.holdshortMin.setValue((cfg.holdShortPositionTime) / 1000.0);
        this.holdshortMax.setValue((cfg.holdShortPositionTime + cfg.holdShortPositionTimeRange) / 1000.0);
        this.holdLongMin.setValue((cfg.holdLongPositionTime) / 1000.0);
        this.holdLongMax.setValue((cfg.holdLongPositionTime + cfg.holdShortPositionTimeRange) / 1000.0);

        this.coolDownMin.setValue((cfg.coolDownTime) / 1000.0);
        this.coolDownMax.setValue((cfg.coolDownTime + cfg.coolDownTimeRange) / 1000.0);
        
        this.leverage.setValue(cfg.leverage);

    }

    @Override
    public void save() {
        cfg.initialDelay = (long) ((Double) this.initialDelayMin.getValue() * 1000.0);
        cfg.initialDelayRange = (long) ((Double) this.initalDelayMax.getValue() * 1000.0) - cfg.initialDelay;
        if (cfg.initialDelayRange < 0) {
            cfg.initialDelayRange = 0;
        }

        cfg.minShortLimit = toFixedLong((double) this.minShortLimit.getValue(), 100);
        cfg.maxShortLimit = toFixedLong((double) this.maxShortLimit.getValue(), 100);
        cfg.minLongLimit = toFixedLong((double) this.minLongLimit.getValue(), 100);
        cfg.maxLongLimit = toFixedLong((double) this.maxLongLimit.getValue(), 100);

        cfg.waitForFill = toFixedLong((double) this.minWaitforFill.getValue(), 1000);
        cfg.waitForFillRange = toFixedLong((double) this.maxWaitForFill.getValue(), 1000) - cfg.waitForFill;
        if (cfg.waitForFillRange < 0) {
            cfg.waitForFillRange = 0;
        }

        cfg.holdShortPositionTime = toFixedLong((double) this.holdshortMin.getValue(), 1000);
        cfg.holdShortPositionTimeRange = toFixedLong((double) this.holdshortMax.getValue(), 1000) - cfg.holdShortPositionTime;
        if (cfg.holdShortPositionTimeRange < 0) {
            cfg.holdShortPositionTimeRange = 0;
        }
        cfg.holdLongPositionTime = toFixedLong((double) this.holdLongMin.getValue(), 1000);
        cfg.holdLongPositionTimeRange = toFixedLong((double) this.holdLongMax.getValue(), 1000) - cfg.holdLongPositionTime;
        if (cfg.holdLongPositionTimeRange < 0) {
            cfg.holdLongPositionTimeRange = 0;
        }
        cfg.coolDownTime = toFixedLong((double) this.coolDownMin.getValue(), 1000);
        cfg.coolDownTimeRange = toFixedLong((double) this.coolDownMax.getValue(), 1000) - cfg.coolDownTime;
        if (cfg.coolDownTimeRange < 0) {
            cfg.coolDownTimeRange = 0;
        }
        
        cfg.leverage=(int)this.leverage.getValue();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        initialDelayMin = new javax.swing.JSpinner();
        initalDelayMax = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        minShortLimit = new javax.swing.JSpinner();
        maxShortLimit = new javax.swing.JSpinner();
        jSpinner5 = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        minLongLimit = new javax.swing.JSpinner();
        maxLongLimit = new javax.swing.JSpinner();
        jSpinner8 = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        minWaitforFill = new javax.swing.JSpinner();
        maxWaitForFill = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        holdshortMin = new javax.swing.JSpinner();
        holdshortMax = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        holdLongMin = new javax.swing.JSpinner();
        holdLongMax = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        coolDownMin = new javax.swing.JSpinner();
        coolDownMax = new javax.swing.JSpinner();
        leverage = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();

        jLabel1.setText("Initial delay:");

        initialDelayMin.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 1.0d));

        initalDelayMax.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 1.0d));

        jLabel2.setText("short limit:");

        minShortLimit.setModel(new javax.swing.SpinnerNumberModel(0.0d, -100.0d, null, 1.0d));

        maxShortLimit.setModel(new javax.swing.SpinnerNumberModel(0.0d, -100.0d, null, 1.0d));

        jLabel3.setText("long limit");

        minLongLimit.setModel(new javax.swing.SpinnerNumberModel(0.0d, -100.0d, null, 1.0d));

        maxLongLimit.setModel(new javax.swing.SpinnerNumberModel(0.0d, -100.0d, null, 1.0d));

        jLabel4.setText("wait for fill:");

        minWaitforFill.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));

        maxWaitForFill.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));

        jLabel5.setText("hold short");

        holdshortMin.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));

        holdshortMax.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));

        jLabel6.setText("hold long");

        holdLongMin.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));

        holdLongMax.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));

        jLabel7.setText("Cool down:");

        coolDownMin.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));

        coolDownMax.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));

        leverage.setModel(new javax.swing.SpinnerNumberModel(1, 1, 10000, 1));

        jLabel8.setText("Leverage:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(holdshortMin, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(minWaitforFill))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(initialDelayMin, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel3))
                            .addGap(20, 20, 20)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(minShortLimit, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                .addComponent(minLongLimit))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(28, 28, 28)
                        .addComponent(holdLongMin))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(leverage)
                            .addComponent(coolDownMin))))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(holdLongMax)
                    .addComponent(initalDelayMax, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maxShortLimit, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                    .addComponent(maxLongLimit, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maxWaitForFill, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(holdshortMax, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(coolDownMax))
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSpinner5, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                    .addComponent(jSpinner8))
                .addContainerGap(123, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(initialDelayMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(initalDelayMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(minShortLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxShortLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(minLongLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxLongLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(minWaitforFill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxWaitForFill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(holdshortMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(holdshortMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(holdLongMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(holdLongMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(coolDownMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coolDownMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(leverage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(113, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner coolDownMax;
    private javax.swing.JSpinner coolDownMin;
    private javax.swing.JSpinner holdLongMax;
    private javax.swing.JSpinner holdLongMin;
    private javax.swing.JSpinner holdshortMax;
    private javax.swing.JSpinner holdshortMin;
    private javax.swing.JSpinner initalDelayMax;
    private javax.swing.JSpinner initialDelayMin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSpinner jSpinner5;
    private javax.swing.JSpinner jSpinner8;
    private javax.swing.JSpinner leverage;
    private javax.swing.JSpinner maxLongLimit;
    private javax.swing.JSpinner maxShortLimit;
    private javax.swing.JSpinner maxWaitForFill;
    private javax.swing.JSpinner minLongLimit;
    private javax.swing.JSpinner minShortLimit;
    private javax.swing.JSpinner minWaitforFill;
    // End of variables declaration//GEN-END:variables

}
