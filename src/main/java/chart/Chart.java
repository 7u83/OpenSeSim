/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chart;

import sesim.OHLCDataItem;
import sesim.OHLCData;
import java.awt.*;
import sesim.Exchange.*;
import sesim.Quote;
import gui.Globals;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.swing.Scrollable;
import sesim.MinMax;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Chart extends javax.swing.JPanel implements QuoteReceiver, Scrollable {

    protected int em_size = 1;

    protected float bar_width = 0.8f;
    protected float bar_width_em = 1;
    protected float y_legend_width = 8;

    protected int num_bars = 4000;

    protected Rectangle clip_bounds = new Rectangle();
    protected Dimension dim;

    protected int first_bar, last_bar;

    /**
     * Creates new form Chart
     */
    public Chart() {
        initComponents();
        if (Globals.se == null) {
            return;
        }

        Globals.se.addQuoteReceiver(this);

    }

    // int item_width = 10;
    //int items = 350;
    //long ntime = 0;
    OHLCData data = new OHLCData(16000);

    OHLCDataItem current = null;

    void drawCandle(Graphics2D g, OHLCData d, int x, int y) {

    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    class XLegendDef {

        double unit_width = 1;
        int big_tick = 10;
        long start;

        XLegendDef() {

        }

        String getAt(int unit) {
            Date date = new Date(sesim.Scheduler.timeStart + unit * 5000);
//            DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            String dateFormatted = formatter.format(date);
            return dateFormatted;
        }

    }

    void drawOHLC(Graphics2D g, int x, OHLCDataItem di) {

    }

    void drawXLegend(Graphics2D g, XLegendDef xld) {

        //XLegendDef xld = new XLegendDef();
        g = (Graphics2D) g.create();

        int xl_height = 30;
        Dimension dim = this.getSize();

        int em_height = g.getFontMetrics().getHeight();
        int em_width = g.getFontMetrics().stringWidth("M");

        int y = dim.height - em_height * 3;

        g.drawLine(0, y, dim.width, y);

        int n;
        double x;

        for (n = 0, x = 0; x < dim.width; x += em_width * xld.unit_width) {

            if (n % xld.big_tick == 0) {
                g.drawLine((int) x, y, (int) x, y + em_height);
            } else {
                g.drawLine((int) x, y, (int) x, y + em_height / 2);
            }

            if (n % xld.big_tick == 0) {
                String text = "Hello";

                text = xld.getAt(n);
                int swidth = g.getFontMetrics().stringWidth(text);

                g.drawString(text, (int) x - swidth / 2, y + em_height * 2);
            }

            OHLCDataItem d;
            try {
                d = data.data.get(n);
            } catch (Exception e) {
                d = null;
            }

            n++;

        }
    }

    private void getData() {

    }

    class RenderCtx {

        Rectangle rect;
        float scaling;
        float min;
        Graphics2D g;
        float iwidth;

        float getYc(float y) {
            return getY(y);
            //return rect.height - ((y - min) * scaling);
        }
    }

    boolean logs = true;

    float getY(float y) {

        if (logs) {
            
            float m = c_mm.max/c_mm.min;
            
            System.out.printf("Min: %f  Max: %f M: %f\n",c_mm.min,c_mm.max,m);
            
            
            //float fac = (float) c_rect.height /(float) Math.log(c_mm.max * c_yscaling);
            float fac = (float) c_rect.height /(float)Math.log(m);
            

            float fmin = c_rect.height - ((float) Math.log((y / c_mm.min)) * fac);
            
            
            System.out.printf("Fac: %f fmin: %f\n", fac, fmin);
            return fmin;

            //return c_rect.height - ((float) Math.log((y - c_mm.min) * c_yscaling) * fac);
            
            
        }

        return c_rect.height - ((y - c_mm.min) * c_yscaling);

//        return c_rect.height - ((y - c_mm.min) * c_yscaling);
    }

    /*   private void old_drawItem(Graphics2D g, Rectangle r, int prevx, int x, OHLCDataItem prev, OHLCDataItem item, float s, float min) {

        if (prev == null) {
            prev = item;
        }

        g.drawLine(prevx, (int) getYc(prev.close, min, s, r), x, (int) getYc(item.close, min, s, r));
        g.drawLine(r.x, r.height + r.y, r.x + r.width, r.height + r.y);
    }
     */
    private void drawItem_l(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem item) {

        if (prev == null) {
            prev = item;
        }
        Graphics2D g = ctx.g;

        Rectangle r = ctx.rect;
        g.drawLine(prevx, (int) ctx.getYc(prev.close), x, (int) ctx.getYc(item.close));
        g.drawLine(r.x, r.height + r.y, r.x + r.width, r.height + r.y);
    }

    private void drawItem(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {

        if (prev == null) {
            prev = i;
        }
        Graphics2D g = ctx.g;

        Rectangle r = ctx.rect;
//        g.drawLine(prevx, (int) ctx.getYc(prev.close), x, (int) ctx.getYc(item.close));

//        g.drawLine(x,(int)ctx.getYc(i.high),x,(int)ctx.getYc(i.low));
        if (i.open < i.close) {
            int xl = (int) (x + ctx.iwidth / 2);

            g.setColor(Color.BLACK);
            g.drawLine(xl, (int) ctx.getYc(i.close), xl, (int) ctx.getYc(i.high));
            g.drawLine(xl, (int) ctx.getYc(i.low), xl, (int) ctx.getYc(i.open));

            float w = ctx.iwidth;
            float h = (int) (ctx.getYc(i.open) - ctx.getYc(i.close));

            //   System.out.printf("CLO: %f %f \n", w, h);
            g.setColor(Color.GREEN);
            g.fillRect((int) (x), (int) ctx.getYc(i.close), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) ctx.getYc(i.close), (int) w, (int) h);

        } else {
            int xl = (int) (x + ctx.iwidth / 2);
            g.setColor(Color.RED);
            g.drawLine(xl, (int) ctx.getYc(i.high), xl, (int) ctx.getYc(i.close));
            g.drawLine(xl, (int) ctx.getYc(i.open), xl, (int) ctx.getYc(i.low));

            float w = ctx.iwidth;
            float h = (int) (ctx.getYc(i.close) - ctx.getYc(i.open));

            g.fillRect((int) (x), (int) ctx.getYc(i.open), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) ctx.getYc(i.open), (int) w, (int) h);

        }

//        g.drawLine(r.x, r.height + r.y, r.x + r.width, r.height + r.y);
    }

//    float getYc(float y) {
    //       return c_rect.height - ((y - c_mm.min) * scaling);
    //   }
    float c_yscaling;

    private void drawYLegend(Graphics2D g) {

        Dimension dim0 = this.getSize();
        Rectangle dim = g.getClipBounds();

        int yw = (int) (this.y_legend_width * this.em_size);

//        System.out.printf("MinMax: %f %f\n", c_mm.min, c_mm.max);

        g.drawLine(dim.width + dim.x - yw, 0, dim.width + dim.x - yw, dim.height);

//        float yscale = dim.height / c_mm.getDiff();
        c_yscaling = c_rect.height / c_mm.getDiff();

//        System.out.printf("yscale %f\n", c_yscaling);

        for (float y = c_mm.min; y < c_mm.max; y += c_mm.getDiff() / 10.0) {

            int my = (int) getY(y); //c_rect.height - (int) ((y - c_mm.min) * c_yscaling);

            g.drawLine(dim.width + dim.x - yw, my, dim.width + dim.x - yw + em_size, my);

            g.drawString(String.format("%.2f", y), dim.width + dim.x - yw + em_size * 1.5f, my + c_font_height / 3);
        }

//        g.setColor(Color.red);
//        g.drawLine(0,(int)getYc(c_mm.min), 1000, (int)getYc(c_mm.min));
        //g.setColor(Color.green);
        //g.drawRect(c_rect.x, c_rect.y, c_rect.width, c_rect.height);
        // System.out.printf("Size: %d %d\n",dim.width,dim.height);
        //  System.exit(0);
    }

    private MinMax c_mm = null;
    private Rectangle c_rect;

    private void draw(Graphics2D g) {

        if (data == null) {
            return;
        }
        if (data.size() == 0) {
            return;
        }

        c_mm = data.getMinMax(first_bar, last_bar);
        if (c_mm == null) {
            return;
        }

        c_mm.min /= 1.5; //-= c_mm.min/ 2.0f;
        c_mm.max *= 1.2; //+= c_mm.max / 10.0f;

        OHLCDataItem di0 = data.get(0);
        XLegendDef xld = new XLegendDef();
        this.drawXLegend(g, xld);

        int em_height = g.getFontMetrics().getHeight();
        int em_width = g.getFontMetrics().stringWidth("M");

        //this.getSize();
        int pwidth = em_width * num_bars;
        int phight = 400;

        this.setPreferredSize(new Dimension(pwidth, dim.height));
        this.revalidate();

        Rectangle r = new Rectangle(0, 0, pwidth, dim.height - 6 * em_width);
        c_rect = r;
        this.drawYLegend(g);

        //       Dimension dim = this.getSize();
        //    Iterator<OHLCDataItem> it = data.iterator();
        OHLCDataItem prev = null;
        //  int myi = 0;

        RenderCtx ctx = new RenderCtx();

//        MinMax mm = data.getMinMax(first_bar, last_bar);
//        if(mm==null)
//            return ;
        ctx.rect = c_rect;
        ctx.scaling = (float) r.height / (c_mm.getMax() - c_mm.getMin());
        ctx.min = c_mm.getMin();
        ctx.g = g;
        ctx.iwidth = (bar_width * em_size) * 0.9f; // em_width - em_width / 5f;

        //g.setClip(clip_bounds.x, clip_bounds.y, (int)(clip_bounds.width-this.y_legend_width*this.em_size), clip_bounds.height);
        for (int i = first_bar; i < last_bar && i < data.size(); i++) {
            OHLCDataItem di = data.get(i);
            int x = (int) (i * em_size * bar_width); //em_width;
            this.drawItem(ctx, x - em_width, x, prev, di); //, ctx.scaling, data.getMin());

            //    myi++;
            prev = di;

        }

        //System.out.printf("Scaling: %f  %f %f %f %f\n",diff,(float)r.height,data.getMin(),data.getMax(),yscaling);
/*        while (it.hasNext()) {
            OHLCDataItem di = it.next();

            int x = myi * em_width;
            this.drawItem(ctx, x - em_width, x, prev, di); //, ctx.scaling, data.getMin());

            myi++;
            prev = di;

        }
         */
    }

    protected void initEmSize(Graphics g) {

        em_size = g.getFontMetrics().stringWidth("M");

    }

    private float c_font_height;

    @Override
    public final void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.initEmSize(g);
        this.dim = this.getSize(dim);
        this.clip_bounds = g.getClipBounds(this.clip_bounds);

//        System.out.printf("X:%d %d\n",dim.width,dim.height);
        first_bar = (int) (clip_bounds.x / (this.bar_width * this.em_size));
        last_bar = 1 + (int) ((clip_bounds.x + clip_bounds.width - (this.y_legend_width * em_size)) / (this.bar_width * this.em_size));

        c_font_height = g.getFontMetrics().getHeight();

        draw((Graphics2D) g);
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
        setBorder(null);
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(300, 300));
        setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
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
