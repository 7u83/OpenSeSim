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

import chart.painter.ChartPainter;
import chart.painter.OHLCChartPainter;
import chart.painter.XLegendPainter;
import gui.Globals;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import sesim.ChartDef;
import sesim.ChartPanel;
import sesim.OHLCData;
import sesim.Stock;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class MMChart extends javax.swing.JPanel {

    Stock stock;

    /**
     * Creates new form MMChart
     */
    public MMChart() {
        stock = Globals.se.getDefaultStock();
        initComponents();
        this.em_width=10;
        setupLayout();

    }

    ChartPanel xLegend;
    JPanel yLegend;
    JPanel mainChart;

    private void setupYLegend() {
        yLegend = new ChartPanel();
        Border redborder = javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0));
        yLegend.setBorder(redborder);
        yLegend.setPreferredSize(new Dimension(this.em_width * 10, 110));
        yLegend.setMinimumSize(new Dimension(em_width * 10, 110));

        GridBagConstraints gbConstraints;
        gbConstraints = new java.awt.GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 0;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 0.0;
        gbConstraints.weighty = 1.0;

        add(yLegend, gbConstraints);
    }

    private void setupXLegend() {
        xLegend = new ChartPanel();
        //     xLegend.setBackground(Color.blue);

        xLegend.setPreferredSize(new Dimension(em_width * 2, em_width * 3));
        xLegend.setMinimumSize(new Dimension(em_width * 2, em_width * 3));

        xLegend.setChartDef(chartDef);

        GridBagConstraints gbConstraints;
        gbConstraints = new java.awt.GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 1;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1.0;
        gbConstraints.weighty = 0.0;

        add(xLegend, gbConstraints);

        OHLCChartPainter p;
        OHLCData mydata = stock.getOHLCdata(60000);
                


        // this.xScrollBar.setMaximum(0);
        p = new XLegendPainter();
        p.setOHLCData(mydata);
        xLegend.addChartPainter(p);

    }

    private void setupMainChart() {
        mainChart = new ChartPanel();
        mainChart.setDoubleBuffered(true);
        mainChart.setBackground(Color.green);

        GridBagConstraints gbConstraints;
        gbConstraints = new java.awt.GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1.0;
        gbConstraints.weighty = 1.0;

        add(mainChart, gbConstraints);
        
        
        
        
        
    }

    ChartDef chartDef;

    private void setupLayout() {
        removeAll();

        Border redborder = javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0));
        Border blueborder = javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 255));

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

                chartDef = new ChartDef();
        chartDef.x_unit_width = 3.0;

        
        setupYLegend();
        setupXLegend();
        setupMainChart();


        java.awt.GridBagConstraints gbConstraints;

        mainChart = new JPanel();
        mainChart.setPreferredSize(new Dimension(100, 40));
        mainChart.setBackground(Color.blue);
    }

    int em_width;

    @Override
    public void paint(Graphics g) {
        em_width = g.getFontMetrics().stringWidth("M");
        // this.removeAll();

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

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
