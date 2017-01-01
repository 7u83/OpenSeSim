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

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Iterator;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.XYDataset;

import SeSim.Exchange.*;
import SeSim.Quote;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Chart extends javax.swing.JPanel implements QuoteReceiver {

    /**
     * Creates new form Chart
     */
    public Chart() {
        initComponents();

    //    String stockSymbol = "Schliemanz Koch AG";
        String stockSymbol = "MSFT";

        DateAxis domainAxis = new DateAxis("Date");
        NumberAxis rangeAxis = new NumberAxis("Price");
        CandlestickRenderer renderer = new CandlestickRenderer();
        XYDataset dataset = getDataSet(stockSymbol);

        XYPlot mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        //Now create the chart and chart panel
        JFreeChart chart = new JFreeChart(stockSymbol, null, mainPlot, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));

        add(chartPanel);
        System.out.print("Hallo Welt\n");

        if (MainWin.se == null) {
            return;
        }

        MainWin.se.addQuoteReceiver(this);

    }

    protected AbstractXYDataset getDataSet(String stockSymbol) {
        //This is the dataset we are going to create
        DefaultOHLCDataset result = null;
        //This is the data needed for the dataset
        OHLCDataItem[] data;

        //This is where we go get the data, replace with your own data source
        data = getData();

        //Create a dataset, an Open, High, Low, Close dataset
        result = new DefaultOHLCDataset(stockSymbol, data);

        return result;
    }

    
    
    
    protected OHLCDataItem getOhlcData(long first, long last, SortedSet <Quote> quotes ){
        Quote e=new Quote(); 
        e.time=first;
        e.id=0;
        
        Quote qq = quotes.first();
        System.out.print(String.format
        ("Quote First %f %d %d \n", qq.price,qq.time,qq.id)
        );
        
        
        System.out.print("Qzitesn"+quotes.size() +"\n");

        
        
        
        Quote z = new Quote();
        z.id=-1;
        z.time=0;
     
          
       SortedSet<Quote> l = quotes.tailSet(z);
  
             System.exit(0);        
       
        double open=0;
        double high=0;
        double low=0;
        double close=0;
        double volume;
              
        Iterator <Quote>it = l.iterator();
        
        
        Quote q;
        q = it.next();
        open=q.price;

        high=q.price;
        low=q.price;
        volume = q.volume;
        
        
        while (it.hasNext() && q.time<last){
            q = it.next();

            
            if (q.price>high)
                high = q.price;
            if (q.price<low)
                low=q.price;
            
            volume += q.volume;
 
        }
        close=q.price;       
        
        Date date = new Date(first);
        return new OHLCDataItem(
                date, open, high, low, close, volume
        );
    }
    
    
    
    protected OHLCDataItem[] getData() {
        List<OHLCDataItem> data = new ArrayList<>();
        
        SortedSet <Quote>s = MainWin.se.getQuoteHistory(60);
        this.getOhlcData(0, System.currentTimeMillis(), s);
    
        
        
        Iterator <Quote>i = s.iterator();
        
        
        
        
        
//               OHLCDataItem item = new OHLCDataItem();
               
        
                long t=0;
  //              if ()
                
                
    //            Quote q  = i.next();
      //          OHLCDataItem item = new OHLCDataItem(
        //                date, open, high, low, close, volume);

/*                double open = 
                double high = 
                double low = 
                double close = 
                double volume = Double.parseDouble(st.nextToken());
                double adjClose = Double.parseDouble(st.nextToken());
           */
                
//                data.add(item);            

        
        
        //return data.toArray(new <OHLCDataItem> rdata[]);
        return data.toArray(new OHLCDataItem[data.size()]);

    }

    //This method uses yahoo finance to get the OHLC data
    protected OHLCDataItem[] getData_old() {
        String stockSymbol = "MSFT";
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
        try {
            String strUrl = "http://ichart.finance.yahoo.com/table.csv?s=" + stockSymbol + "&a=0&b=1&c=2008&d=3&e=30&f=2008&ignore=.csv";
            URL url = new URL(strUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            DateFormat df = new SimpleDateFormat("y-M-d");

            String inputLine;
            in.readLine();
            while ((inputLine = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                Date date = df.parse(st.nextToken());
                double open = Double.parseDouble(st.nextToken());
                double high = Double.parseDouble(st.nextToken());
                double low = Double.parseDouble(st.nextToken());
                double close = Double.parseDouble(st.nextToken());
                double volume = Double.parseDouble(st.nextToken());
                double adjClose = Double.parseDouble(st.nextToken());

                OHLCDataItem item = new OHLCDataItem(date, open, high, low, close, volume);
                dataItems.add(item);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Data from Yahoo is from newest to oldest. Reverse so it is oldest to newest
        Collections.reverse(dataItems);

        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);
        
        System.out.print("Return oghls old data items\n");

        return data;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton2 = new javax.swing.JToggleButton();

        setLayout(new java.awt.BorderLayout());

        jToggleButton2.setText("jToggleButton2");
        add(jToggleButton2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton jToggleButton2;
    // End of variables declaration//GEN-END:variables

    @Override
    public void UpdateQuote(Quote q) {
        return;
        //q.print();
  /*      SortedSet s = MainWin.se.getQuoteHistory(60);
        System.out.print(
                "SortedSet size:"
                + s.size()
                + "\n"
        );
*/
    }
}
