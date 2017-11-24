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
package chart;

import sesim.ChartDef;
import chart.painter.CandleStickChartPainter;
import chart.painter.ChartPainter;
import chart.painter.XLegendPainter;
import chart.painter.ChartCrossPainter;
import chart.painter.LineChartPainter;
import chart.painter.YLegendPainter;
import gui.Globals;
import java.util.ArrayList;
import sesim.Exchange.QuoteReceiver;
import sesim.Indicator;
import sesim.MinMax;
import sesim.OHLCData;
import sesim.OHLCDataItem;
import sesim.Quote;
import indicators.SMAIndicator;



/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class MasterChart extends javax.swing.JPanel implements QuoteReceiver {

    private ChartDef chartDef;

    SMAIndicator sma;

    class MyOHLCData extends OHLCData {

        
        @Override
        public int size() {
            return sma.getData().size();
        }
        
        @Override
        public OHLCDataItem get(int n) {
            return sma.getData().get(n);
        }

        @Override
        public MinMax getMinMax(int first, int last) {
            return mydata.getMinMax(first, last);
        }

    }

    /**
     * Creates new form MasterChart
     */
    public MasterChart() {
        initComponents();

        chartDef = new ChartDef();
        chartDef.x_unit_width = 3.0;

        if (Globals.se == null) {
            return;
        }

        Globals.se.addQuoteReceiver(this);

        this.chart.setChartDef(chartDef);
        this.xLegend.setChartDef(chartDef);
        this.yLegend.setChartDef(chartDef);

        //  this.yLegend.addChartPainter(p);
        //this.yLegend.addChartPainter(pc);
    }
    OHLCData mydata;

    public void reset() {
        this.chart.deleteAllChartPinters();
        this.xLegend.deleteAllChartPinters();
        this.yLegend.deleteAllChartPinters();

        this.chart.setChartDef(chartDef);
        this.xLegend.setChartDef(chartDef);
        this.yLegend.setChartDef(chartDef);

        ChartPainter p;
        mydata = Globals.se.getOHLCdata(60000 * 20);

        this.xScrollBar.setMaximum(0);

        p = new XLegendPainter();
        p.setOHLCData(mydata);

        xLegend.addChartPainter(p);
        xLegend.setXSCrollBar(xScrollBar);

        ChartPainter pc = new CandleStickChartPainter();
        //pc.setDataProvider(this);
        pc.setOHLCData(mydata);

        chart.addChartPainter(pc);
        chart.setXSCrollBar(xScrollBar);
        chart.addChartPainter(new ChartCrossPainter());

        sma = new indicators.SMAIndicator(mydata);
        MyOHLCData mysma = new MyOHLCData();
        p = new LineChartPainter();
        p.setOHLCData(mysma);
        //p.setDataProvider(new SMA(get()));        
        chart.addChartPainter(p);

        ChartPainter yp = new YLegendPainter(chart);
//        yp.setDataProvider(this);
        yp.setOHLCData(mydata);

        this.yLegend.addChartPainter(yp);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chart = new sesim.ChartPanel();
        yLegend = new sesim.ChartPanel();
        xLegend = new sesim.ChartPanel();
        xScrollBar = new javax.swing.JScrollBar();

        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });

        chart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        chart.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                chartMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout chartLayout = new javax.swing.GroupLayout(chart);
        chart.setLayout(chartLayout);
        chartLayout.setHorizontalGroup(
            chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        chartLayout.setVerticalGroup(
            chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        yLegend.setBorder(null);

        javax.swing.GroupLayout yLegendLayout = new javax.swing.GroupLayout(yLegend);
        yLegend.setLayout(yLegendLayout);
        yLegendLayout.setHorizontalGroup(
            yLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 83, Short.MAX_VALUE)
        );
        yLegendLayout.setVerticalGroup(
            yLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 326, Short.MAX_VALUE)
        );

        xLegend.setBorder(null);

        javax.swing.GroupLayout xLegendLayout = new javax.swing.GroupLayout(xLegend);
        xLegend.setLayout(xLegendLayout);
        xLegendLayout.setHorizontalGroup(
            xLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 576, Short.MAX_VALUE)
        );
        xLegendLayout.setVerticalGroup(
            xLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 61, Short.MAX_VALUE)
        );

        xScrollBar.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        xScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                xScrollBarAdjustmentValueChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xScrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(xLegend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yLegend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yLegend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xLegend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(xScrollBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chartMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chartMouseMoved

    }//GEN-LAST:event_chartMouseMoved

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
        double n = evt.getPreciseWheelRotation() * (-1.0);

        if (n < 0) {
            if (chartDef.x_unit_width > 0.3) {
                chartDef.x_unit_width += 0.1 * n;
            }
        } else {
            chartDef.x_unit_width += 0.1 * n;
        }

        this.invalidate();
        this.repaint();
    }//GEN-LAST:event_formMouseWheelMoved

    private void xScrollBarAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_xScrollBarAdjustmentValueChanged
        repaint();
    }//GEN-LAST:event_xScrollBarAdjustmentValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private sesim.ChartPanel chart;
    private sesim.ChartPanel xLegend;
    private javax.swing.JScrollBar xScrollBar;
    private sesim.ChartPanel yLegend;
    // End of variables declaration//GEN-END:variables

    @Override
    public void UpdateQuote(Quote q) {
        if (sma != null) {
            sma.update();
        }
        int s = mydata.size();
        this.xScrollBar.setMaximum(s);
        repaint();
    }

}
