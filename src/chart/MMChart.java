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

import chart.painter.CandleStickChartPainter;
import chart.painter.ChartCrossPainter;
import chart.painter.ChartPainter;
import chart.painter.OHLCChartPainter;
import chart.painter.VolChartPainter;
import chart.painter.XScaleDetailPainter;
import chart.painter.XScalePainter;
import chart.painter.YScalePainter;
import gui.Globals;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.Border;
import sesim.Exchange.QuoteReceiver;
import sesim.OHLCData;
import sesim.Quote;
import sesim.Stock;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class MMChart extends JPanel implements QuoteReceiver {

    Stock stock;
    OHLCData mydata;
    private int scaleWidth;
    private int padding;

    /**
     * Creates new form MMChart
     */
    public MMChart() {

        initComponents();

        if (Globals.se == null) {
            return;
        }
        stock = Globals.se.getDefaultStock();
        this.em_width = this.getFontMetrics(this.getFont()).getHeight();
        this.scaleWidth = this.em_width * 7;
        this.padding = (int) (this.em_width * 0.3);

        reset();

    }

    ChartPanel xLegend;
    ChartPanel yLegend;
//    ChartPanel mainChart;

    private int compression = 60000;

    public void reset() {
        mydata = Globals.se.getOHLCdata(Globals.se.getDefaultStock(), 60000 * 1);
        setupLayout();

    }

    // In deiner Klasse:
    private JScrollBar xScrollBar;
    
    

    private void initScrollBar() {
        xScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        xScrollBar.setMinimum(0);
        xScrollBar.setMaximum(1000); // max je nach Daten
        xScrollBar.setVisibleAmount(50); // Größe des sichtbaren "Fensters"

        // Listener, um auf Scrollbewegungen zu reagieren
        xScrollBar.addAdjustmentListener(e -> {
            int value = xScrollBar.getValue();
            // Hier kannst du z.B. chartDef verschieben:
//        chartDef.scrollOffset = value;
         //   mainChart.repaint();
         //   xLegend.repaint();
        });

        // Layout hinzufügen, z.B. unten mit GridBag
        GridBagConstraints gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 3; // unter xLegend
        gbConstraints.gridwidth = 2;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.weightx = 1.0;
        gbConstraints.weighty = 0.0;

        add(xScrollBar, gbConstraints);
    }

    private void setupMainYLegend(int currentGridRow) {
        yLegend = new ChartPanel();
  //      Border redborder = javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0));
  //      yLegend.setBorder(redborder);

        yLegend.setPreferredSize(new Dimension(this.scaleWidth, 110));
        yLegend.setMinimumSize(new Dimension(this.scaleWidth, 110));
        yLegend.setMaximumSize(new Dimension(this.scaleWidth, 110));

        GridBagConstraints gbConstraints;
        gbConstraints = new java.awt.GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = currentGridRow;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 0.0;
        gbConstraints.weighty = 1.0;

        add(yLegend, gbConstraints);
        this.addMouseMotionListener(yLegend);

        OHLCChartPainter ylp = new YScalePainter(/*null*/);
//        OHLCData mydata = stock.getOHLCdata(compression);

        ylp.setOHLCData(mydata);
        yLegend.setChartDef(chartDef);
        yLegend.addChartPainter(ylp);

    }

    private void setupXLegend(int gridRow) {
         xLegend = new ChartPanel();
        //     xLegend.setBackground(Color.blue);

        xLegend.setPreferredSize(new Dimension(em_width * 2, em_width * 3));
        xLegend.setMinimumSize(new Dimension(em_width * 2, em_width * 3));

        xLegend.setChartDef(chartDef);

        GridBagConstraints gbConstraints;
        gbConstraints = new java.awt.GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = gridRow;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1.0;
        gbConstraints.weighty = 0.0;

        add(xLegend, gbConstraints);
        
        xLegend.setXSCrollBar(xScrollBar);

        OHLCChartPainter p;
        //       OHLCData mydata = stock.getOHLCdata(compression);

        // this.xScrollBar.setMaximum(0);
        p = new XScalePainter();
        p.setOHLCData(mydata);
        
        xLegend.addChartPainter(p);

        p = new XScaleDetailPainter();
        p.setOHLCData(mydata);
        xLegend.addChartPainter(p);

        ChartPainter p0;
        p0 = new ChartCrossPainter();
        //xLegend.addChartPainter(p0);
        xLegend.setChartDef(chartDef);

    }

    private void addMouseMotionListener(JPanel panel) {
        panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
    }

    private void setupMainChart(int currentGridRow) {
        ChartPanel mainChart = new ChartPanel();
        mainChart.setDoubleBuffered(true);
  //      mainChart.setBackground(Color.yellow);
        mainChart.setChartDef(chartDef);

        GridBagConstraints gbConstraints;
        gbConstraints = new java.awt.GridBagConstraints();
        gbConstraints.gridx = currentGridRow;
        gbConstraints.gridy = 0;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1.0;
        gbConstraints.weighty = 1.0;

        add(mainChart, gbConstraints);

        this.addMouseMotionListener(mainChart);

        mainChart.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        OHLCChartPainter pc = new CandleStickChartPainter();
        pc.setOHLCData(mydata);

        mainChart.addChartPainter(pc);

        ChartPainter p0;
        p0 = new ChartCrossPainter();
        mainChart.addChartPainter(p0);
        mainChart.setXSCrollBar(xScrollBar);

        Globals.se.addQuoteReceiver(this);

    }

    private void setupVolYLegend(int gridRow) {
        ChartPanel cp = new ChartPanel();
        //   Border redborder = javax.swing.BorderFactory.createLineBorder(Color.BLACK));
        //   cp.setBorder(redborder);

        cp.setPreferredSize(new Dimension(this.scaleWidth, 110));
        cp.setMinimumSize(new Dimension(this.scaleWidth, 110));
        cp.setMaximumSize(new Dimension(this.scaleWidth, 110));

        GridBagConstraints gbConstraints;
        gbConstraints = new java.awt.GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = gridRow;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 0.0;
        gbConstraints.weighty = 0.0;
        gbConstraints.insets = new java.awt.Insets((int) (padding), 0, 0, 0);
        add(cp, gbConstraints);
        this.addMouseMotionListener(cp);

        OHLCChartPainter ylp = new chart.painter.YVolScalePainter();
        ylp.setOHLCData(mydata);
        cp.setChartDef(chartDef);
        cp.addChartPainter(ylp);
        cp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));

    }

    private ChartPanel setupVolChart(int gridRow) {
        ChartPanel cp = new ChartPanel();
        cp.setDoubleBuffered(true);
        //   cp.setBackground(Color.lightGray);
        cp.setChartDef(chartDef);

        int emHeight = cp.getFontMetrics(cp.getFont()).getHeight();

        int fixedHeight = 6 * emHeight;

        cp.setPreferredSize(new Dimension(em_width * 2, em_width * 6));
        cp.setMinimumSize(new Dimension(em_width * 2, em_width * 6));
        cp.setMaximumSize(new Dimension(em_width * 2, em_width * 6));

        GridBagConstraints gbConstraints;
        gbConstraints = new java.awt.GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = gridRow;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1.0;
        gbConstraints.weighty = 0.0;
        gbConstraints.insets = new java.awt.Insets((int) (padding), 0, 0, 0);

        add(cp, gbConstraints);

        OHLCChartPainter pc = new chart.painter.VolChartPainter();
        pc.setOHLCData(mydata);
        cp.addChartPainter(pc);

        cp.addChartPainter(new ChartCrossPainter());

        this.addMouseMotionListener(cp);

        cp.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        cp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        cp.setXSCrollBar(xScrollBar);
        //  Globals.se.addQuoteReceiver(this);
        return cp;

    }

    ChartDef chartDef;

    private void setupLayout() {
        removeAll();

        setLayout(new GridBagLayout());
        this.initScrollBar();

        chartDef = new ChartDef();
        chartDef.x_unit_width = 3.0;

        setupMainChart(0);
        setupMainYLegend(0);

  //      chartDef.mainChart = mainChart;

        this.setupVolChart(1);
       this.setupVolYLegend(1);

       setupXLegend(2);


    }

    int em_width;

    @Override
    public void paint(Graphics g) {
        em_width = g.getFontMetrics().stringWidth("M");
        // this.removeAll();

        if (xLegend == null) {
            super.paint(g);
            return;
        }

        // repaint();
        //  setupLayout();
        xLegend.setPreferredSize(new Dimension(em_width * 2, em_width * 3));
        xLegend.setMinimumSize(new Dimension(em_width * 2, em_width * 3));

        revalidate();
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        //   System.out.printf("Mouse Moved\n");
        // mainChart.repaint();
        //      xLegend.revalidate();
//        xLegend.repaint();
repaint();
    }//GEN-LAST:event_formMouseMoved

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        System.out.printf("The mouse was clicked\n");
    }//GEN-LAST:event_formMouseClicked

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
        System.out.printf("Wheel!!!\n");
    }//GEN-LAST:event_formMouseWheelMoved


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu popupMenu;
    // End of variables declaration//GEN-END:variables

    @Override
    public void UpdateQuote(Quote q) {

        mydata = Globals.se.getOHLCdata(Globals.se.getDefaultStock(), 60000 * 1);
        int s = mydata.size();
        
               
//                 System.out.printf("MasterSize %d\n",s);
        this.xScrollBar.setMaximum(s);
        
        System.out.printf("Size %d\n", s);
        repaint();
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
