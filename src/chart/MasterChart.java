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
import java.util.Objects;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class MasterChart extends javax.swing.JPanel implements QuoteReceiver {

    private ChartDef chartDef;

    class CompressionVal {

        public final String text;
        public final Integer value;

        CompressionVal(String text, Integer val) {
            this.text = text;
            this.value = val;
        }
    }

    private final CompressionVal cvalues[] = {
        new CompressionVal("5 s", 5 * 1000),
        new CompressionVal("10 s", 10 * 1000),
        new CompressionVal("15 s", 15 * 1000),
        new CompressionVal("30 s", 30 * 1000),
        new CompressionVal("1 m", 1 * 60 * 1000),
        new CompressionVal("2 m", 2 * 60 * 1000),
        new CompressionVal("5 m", 5 * 60 * 1000),
        new CompressionVal("10 m", 10 * 60 * 1000),
        new CompressionVal("1 h", 1 * 3660 * 1000),
        new CompressionVal("2 h", 2 * 3660 * 1000),
        new CompressionVal("4 h", 4 * 3660 * 1000),
        new CompressionVal("1 d", 1 * 24 * 3660 * 1000),
        new CompressionVal("2 d", 2 * 24 * 3660 * 1000),
        new CompressionVal("3 d", 3 * 3660 * 1000),};

    private void initCtxMenu() {
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < this.cvalues.length; i++) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(this.cvalues[i].text);

        }
    }

    SMAIndicator sma1, sma2;

    class MyOHLCData extends OHLCData {

        SMAIndicator sma;

        MyOHLCData(SMAIndicator sma) {
            this.sma = sma;
        }

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

        sma1 = new indicators.SMAIndicator();
        sma1.setParent(mydata);

        JSONObject co;
        co = new JSONObject("{\"len\": 60}");
        sma1.putConfig(co);
        MyOHLCData mysma1;
        mysma1 = new MyOHLCData(sma1);
        p = new LineChartPainter();
        p.setOHLCData(mysma1);

        chart.addChartPainter(p);

        sma2 = new indicators.SMAIndicator();
        sma2.setParent(mydata);
        co = new JSONObject("{\"len\": 20}");
        sma2.putConfig(co);
        MyOHLCData mysma2;
        mysma2 = new MyOHLCData(sma2);
        p = new LineChartPainter();
        p.setOHLCData(mysma2);
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

        ctxMenu = new javax.swing.JPopupMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        chart = new sesim.ChartPanel();
        yLegend = new sesim.ChartPanel();
        xLegend = new sesim.ChartPanel();
        xScrollBar = new javax.swing.JScrollBar();

        jMenu1.setText("jMenu1");
        ctxMenu.add(jMenu1);

        jMenuItem1.setText("jMenuItem1");
        ctxMenu.add(jMenuItem1);

        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });

        chart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        chart.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                chartMouseMoved(evt);
            }
        });
        chart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                chartMousePressed(evt);
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
            .addGap(0, 314, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout yLegendLayout = new javax.swing.GroupLayout(yLegend);
        yLegend.setLayout(yLegendLayout);
        yLegendLayout.setHorizontalGroup(
            yLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 95, Short.MAX_VALUE)
        );
        yLegendLayout.setVerticalGroup(
            yLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        xLegend.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout xLegendLayout = new javax.swing.GroupLayout(xLegend);
        xLegend.setLayout(xLegendLayout);
        xLegendLayout.setHorizontalGroup(
            xLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
        );
        xLegendLayout.setVerticalGroup(
            xLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 37, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xLegend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yLegend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(xScrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(yLegend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xLegend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xScrollBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void xScrollBarAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_xScrollBarAdjustmentValueChanged
        repaint();
    }//GEN-LAST:event_xScrollBarAdjustmentValueChanged

    private void showCtxMenu(java.awt.event.MouseEvent evt) {

        this.ctxMenu.setVisible(true);
        this.ctxMenu.show(this, evt.getX(), evt.getY());

        invalidate();
        repaint();
    }


    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }
        System.out.printf("ctx menu pressed\n");
        showCtxMenu(evt);
    }//GEN-LAST:event_formMousePressed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        formMousePressed(evt);
    }//GEN-LAST:event_formMouseReleased

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

    private void chartMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chartMouseMoved

    }//GEN-LAST:event_chartMouseMoved

    private void chartMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chartMousePressed
        System.out.printf("Mauspress\n");
        this.formMousePressed(evt);
    }//GEN-LAST:event_chartMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private sesim.ChartPanel chart;
    private javax.swing.JPopupMenu ctxMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private sesim.ChartPanel xLegend;
    private javax.swing.JScrollBar xScrollBar;
    private sesim.ChartPanel yLegend;
    // End of variables declaration//GEN-END:variables

    @Override
    public void UpdateQuote(Quote q) {
        if (sma1 != null) {
            sma1.update();
        }

        if (sma2 != null) {
            sma2.update();
        }

        int s = mydata.size();
        this.xScrollBar.setMaximum(s);
        repaint();
    }

}
