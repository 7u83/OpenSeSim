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
    long ntime = -1;

    OHLCData data;

    OHLCDataItem current = null;

    long rasterTime(long time) {

        long rt = time / 5000;
        return rt * 5000;

    }

    private void realTimeAdd(long time, float price, float volume) {

        /*     System.out.print("Diff:"
                +(ntime-time)
                +"\n"
        );
         */
        if (time > ntime) {

            System.out.print("new raster ----------------------------------\n");
            current = null;
            ntime = rasterTime(time) + 5000;
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
        this.getSize();

        int pwidth = item_width * items;

        this.setPreferredSize(new Dimension(pwidth, 400));

                g.setColor(Color.RED);
               g.drawLine(0,0,100,100);
               
        for (int i = 0; i < items; i++) {
            int x = i * this.item_width;
            g.drawLine(x, 0, x, 50);

        }
        
 
        
        if (this.current == null) {
            return;
        }
        
        
        g.setColor(Color.BLUE);
        g.drawLine(0, 0, 100, (int) ((this.current.close-80.0)*80.0));

    }

    @Override
    public void paintComponent(Graphics go) {
        super.paintComponent(go);

        Graphics2D g = (Graphics2D) go;

        g.setColor(Color.GRAY);

        g.setBackground(Color.BLACK);
        //   g.get

        Rectangle bounds = g.getDeviceConfiguration().getBounds();
        System.out.print(bounds.width + "\n");

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
        // System.out.print("Quote Received\n");
        this.realTimeAdd(q.time, (float) q.price, (float)q.volume);
    //    this.invalidate();
        this.repaint();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}