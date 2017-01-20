/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chart;

import java.awt.*;
import sesim.Exchange.*;
import sesim.Quote;
import gui.MainWin;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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
        if (MainWin.se == null) {
            return;
        }

        MainWin.se.addQuoteReceiver(this);

        //Graphics g = this.getGraphics();
        //g.drawString("Hello world", 0, 0);
    }

    int item_width = 10;
    int items = 350;
    long ntime = 0;

    OHLCData data = new OHLCData();

    OHLCDataItem current = null;

    int min;
    int max;

    int getY(float Y) {

        return 0;
    }

    void drawCandle(Graphics2D g, OHLCData d, int x, int y) {

    }

    class XLegendDef {

        double unit_width = 1;
        int big_tick = 10;
        long start;
        
        
        XLegendDef(){
            
        }
        
        String getAt(int unit){
            Date date = new Date(start);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
            String dateFormatted = formatter.format(date);
            return dateFormatted;
        }

    }

    void drawXLegend(Graphics2D g,XLegendDef xld) {

        //XLegendDef xld = new XLegendDef();

        g = (Graphics2D) g.create();

        int xl_height = 30;
        Dimension dim = this.getSize();

        int em_height = g.getFontMetrics().getHeight();
        int em_width = g.getFontMetrics().stringWidth("M");

        int y = dim.height - em_height * 3;

        g.drawLine(0, y, dim.width, y);

        int n = 0;
        for (double x = 0; x < dim.width; x += em_width * xld.unit_width) {

            if (n % xld.big_tick == 0) {
                g.drawLine((int) x, y, (int) x, y + em_height);
            } else {
                g.drawLine((int) x, y, (int) x, y + em_height / 2);
            }
            
            if (n % xld.big_tick == 0){
                String text = "Hello";
                
                text = xld.getAt(n);                
                int swidth = g.getFontMetrics().stringWidth(text);
                

            
                g.drawString(text, (int)x - swidth / 2, y + em_height * 2);
            }
            
            OHLCDataItem d;
            try {
                d = data.data.get(n);
            } catch (Exception e) {
                d = null;
            }
            
            n++;

        }

        /*
        for (int i = 0; i < items; i ++) {
            int x = i * this.item_width;
            
            if (i%5==0)
                g.drawLine(x, y, x, y + 6);
            else
               g.drawLine(x, y, x, y + 3);

            OHLCDataItem d;
            try {
                d = data.data.get(i);
            } catch (Exception e) {
                d = null;
            }

            String text;
            if (d != null) {
                text = "A";
            } else {
                text = "x";
            }
            


            int swidth = g.getFontMetrics().stringWidth(text);

            g.drawString(text, x - swidth / 2, y + em_height * 2);
  

        }
         */
        //for(int x=0; x)
    }

    private void realTimeAdd(long time, float price, float volume) {

        /*System.out.print("Diff:"
                +(ntime-time)
                +"\n"
        );*/
        if (time > ntime) {

//            System.out.print("new raster ----------------------------------\n");
            current = null;
//            ntime = rasterTime(time) + 5000;
            //       System.out.print(ntime+"\n");
            //      System.out.print((time)+"\n");
            //       System.exit(0);
        }

        if (current == null) {
            current = new OHLCDataItem(price, price, price, price, volume);
            return;
        }

        boolean rc = current.update(price, volume);

        if (rc) {
            System.out.print("Updated -"
                    + " High:"
                    + current.high
                    + " Low:"
                    + current.low
                    + " Volume"
                    + current.volume
                    + "("
                    + time
                    + ")"
                    + "\n"
            );
        }

    }

    private void getData() {

    }

    private void draw(Graphics2D g) {

        OHLCDataItem di0 = data.data.get(0);
        XLegendDef xld= new XLegendDef();
        //xld.start=di.
        this.drawXLegend(g,xld);

        this.getSize();

        int pwidth = item_width * items;
        int phight = 40;

        this.setPreferredSize(new Dimension(pwidth, phight));

        Dimension dim = this.getSize();
        //    System.out.print("Diemension "+dim.width+" "+dim.height+"\n");

        g.setColor(Color.RED);

//        g.drawLine(0, 0, 100, 100);

        /*        for (int i = 0; i < items; i++) {
            int x = i * this.item_width;
            g.drawLine(x, 0, x, 50);

        }
         */
        //   if (this.current == null) {
        //       return;
        //   }
        ArrayList<OHLCDataItem> od = data.data;
        

        //  System.out.print("OD S: " + od.size() + "\n");
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(3));

        Iterator<OHLCDataItem> it = od.iterator();
        int myi = 0;

        int lastx = 0;
        int lasty = 0;

        while (it.hasNext()) {
            OHLCDataItem di = it.next();

            float y = di.close;
            float max = data.max;
            float min = data.min;

            max = max / 10.0f + max;
            min = min - min / 10.0f;

            if (min == max) {
                min = y / 2;
                max = y * 2;

            }

            //   max = 5;
            // min = 0;
            //   System.out.print("Fval: " + y + " " + min + "\n");
            y -= min;
            //  System.out.print("VAL New" + y + "\n");

            //val/ ((data.max-data.min)/dim.height);
            //  System.out.print("MINMAX " + min + " " + max + " " + dim.height + "\n");
            y = dim.height - (dim.height * y / (max - min));

            int x = myi * this.item_width;
            myi++;

            g.drawLine(lastx, lasty, x, (int) y);

            lastx = x;
            lasty = (int) y;

//            System.out.print("Draw Line: " + x + " " + y + "\n");
        }

        //    g.drawLine(0, 0, 100, (int) ((this.current.close-80.0)*80.0));
    }

    @Override
    public void paintComponent(Graphics go) {
        super.paintComponent(go);

        Graphics2D g = (Graphics2D) go;

        g.setColor(Color.GRAY);

        g.setBackground(Color.BLACK);
        //   g.get

        Rectangle bounds = g.getDeviceConfiguration().getBounds();
        // System.out.print(bounds.width + "\n");

        //g.fillRect(0, 0, 100, 100);
        Dimension d = this.getSize();

        //g.drawString("Hello world", 810, 10);
        //g.drawLine(0, 0, d.width, d.height);
        //this.setPreferredSize(new Dimension(2000,4000));
        draw(g);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(java.awt.Color.white);
        setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        setPreferredSize(new java.awt.Dimension(300, 300));
        setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 316, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void UpdateQuote(Quote q) {
        //    System.out.print("Quote Received\n");
//        this.realTimeAdd(q.time, (float) q.price, (float)q.volume);

        data.realTimeAdd(q.time, (float) q.price, (float) q.volume);
        //    this.invalidate();
        this.repaint();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
