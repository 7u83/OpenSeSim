/*
 * Copyright (c) 2016, 7u83 <7u83@mail.ru>
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
package Gui;

import java.util.ArrayList;
import java.util.Calendar;
import org.jfree.chart.plot.PlotOrientation;

import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;

import SeSim.Exchange;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Chart extends javax.swing.JPanel implements Exchange.QuoteReceiver {

    SeSim.Exchange se;

    private DefaultHighLowDataset createDataset() {

        System.out.print("Making Data");
        int s = se.quoteHistory.size();
        System.out.print(("SIZE"));
        System.out.println(s);

        int serice = 115;

        Date[] date = new Date[serice];
        double[] high = new double[serice];
        double[] low = new double[serice];
        double[] open = new double[serice];
        double[] close = new double[serice];
        double[] volume = new double[serice];

        Calendar calendar = Calendar.getInstance();
        calendar.set(2008, 5, 1);

        for (int i = 0; i < serice; i++) {
            date[i] = createData(2008, 8, i + 1);
            high[i] = 30 + Math.round(10) + new Double(Math.random() * 20.0);
            low[i] = 30 + Math.round(10) + new Double(Math.random() * 20.0);
            open[i] = 10 + Math.round(10) + new Double(Math.random() * 20.0);
            close[i] = 10 + Math.round(10) + new Double(Math.random() * 20.0);
            volume[i] = 10.0 + new Double(Math.random() * 20.0);
        }

        DefaultHighLowDataset data = new DefaultHighLowDataset("", date, high,
                low, open, close, volume);
        return data;
    }

    private Date createData(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date);
        return calendar.getTime();
    }

    private JFreeChart createChart(final DefaultHighLowDataset dataset) {
//		final JFreeChart chart = ChartFactory.createCandlestickChart(
        //"Candlestick Demo", "Time", "Price", dataset, false);

//		final JFreeChart chart = ChartFactory.createCandlestickChart(
//				"Candlestick Demo", "Time", "Price", dataset, false);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "The Chart", "X axis ", "Y axis", dataset,
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls;
        );

        //"Candlestick Demo", "Time", "Price", dataset, false);
        return chart;
    }

    /**
     * Creates new form Chart
     */
    public Chart() {
        initComponents();

        this.se = MainWin.se;
        if (this.se == null) {
            return;
        }

        final DefaultHighLowDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 350));
        //	setContentPane(chartPanel);
        initChart(chartPanel);

    }

    private void initChart(ChartPanel chart) {

        // orderBook1 = new Gui.OrderBook();
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(16, Short.MAX_VALUE))
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(300, 300));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void UpdateQuote(Exchange.Quote q) {
        System.out.print("Quote received");
        System.out.println(q.price);

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
