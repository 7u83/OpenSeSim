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
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import sesim.MinMax;
import sesim.Scheduler;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Chart extends javax.swing.JPanel implements QuoteReceiver, Scrollable {

    private int em_height;
    private int em_width;

    protected double x_legend_height = 3;

    protected double x_unit_width = 1.0;

    /**
     * width of y legend in em
     */
    protected float y_legend_width = 10;

    protected int num_bars = 4000;

    protected Rectangle clip_bounds = new Rectangle();
    protected Dimension gdim;

    private int first_bar, last_bar;

    public final void initChart() {
//        data = new OHLCData(60000*30);        
        //data = new OHLCData(60000*30);        
        //data = Globals.se.getOHLCdata(60000 * 30);
        this.setCompression(10000);
    }

    /**
     * Creates new form Chart
     */
    public Chart() {
        if (Globals.se == null) {
            return;
        }

        initComponents();
        initChart();
        initCtxMenu();
        //setCompression(60000);
        if (Globals.se == null) {
            return;
        }

        Globals.se.addQuoteReceiver(this);

//                scrollPane=new JScrollPane();
        //     scrollPane.setViewportView(this);
    }

    private String[] ctxMenuCompressionText = {
        "5 s", "10 s", "15 s", "30 s",
        "1 m", "2 m", "5 m", "10 m", "15 m", "30 m",
        "1 h", "2 h", "4 h",
        "1 d", "2 d"
    };
    private Integer[] ctxMenuCompressionValues = {
        5 * 1000, 10 * 1000, 15 * 1000, 30 * 1000,
        60 * 1000, 2 * 60 * 1000, 5 * 60 * 1000, 10 * 60 * 1000, 15 * 60 * 1000, 30 * 60 * 1000,
        1 * 3600 * 1000, 2 * 3600 * 1000, 4 * 3600 * 1000,
        1 * 24 * 3600 * 1000, 2 * 24 * 3600 * 1000
    };

    void initCtxMenu() {
        for (int i = 0; i < this.ctxMenuCompressionValues.length; i++) {
            JMenuItem item = new JMenuItem(this.ctxMenuCompressionText[i]);

            item.addActionListener((java.awt.event.ActionEvent evt) -> {
                ctxMenuCompActionPerformed(evt);
            });
            this.compMenu.add(item);
        }
    }

    private void ctxMenuCompActionPerformed(java.awt.event.ActionEvent evt) {
        String cmd = evt.getActionCommand();
        for (int i = 0; i < this.ctxMenuCompressionText.length; i++) {
            if (this.ctxMenuCompressionText[i].equals(cmd)) {

                this.setCompression(this.ctxMenuCompressionValues[i]);
            }
        }

    }

    OHLCData data;

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

        int big_tick = 10;
        long start;

        XLegendDef() {

        }

        String getAt(int unit) {

            int fs = data.getFrameSize();
            return Scheduler.formatTimeMillis(0 + unit * fs);

        }

    }

    /**
     * Draw the one and only one X legend
     *
     * @param g Graphics conext to draw
     * @param xld Definition
     */
    void drawXLegend(Graphics2D g, XLegendDef xld) {

        //g = (Graphics2D) g.create();
        int yw = (int) (this.y_legend_width * this.em_width);

        g.setClip(clip_bounds.x, clip_bounds.y, clip_bounds.width - yw, clip_bounds.height);

        Dimension dim = getSize();
        int y = dim.height - em_height * 3;

        g.drawLine(0, y, dim.width, y);

        int n;
        double x;

        long big_tick = 1;

        double btl, xxx;
        do {
            big_tick++;
            btl = em_width * big_tick * x_unit_width;
            xxx = 7 * em_width;

        } while (btl < xxx);

        for (n = 0, x = 0; x < dim.width; x += em_width * x_unit_width) {

            if (n % big_tick == 1) {
                g.drawLine((int) x, y, (int) x, y + em_width);
                String text;
                text = xld.getAt(n);

                int swidth = g.getFontMetrics().stringWidth(text);

                g.drawString(text, (int) x - swidth / 2, y + em_height * 2);
            } else {
                g.drawLine((int) x, y, (int) x, y + em_width / 2);
            }

            if (n % big_tick == 0) {

            }

            n += 1;

        }
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

    boolean logs = false;

    float getY(float y) {

        float ys = c_rect.height / c_mm.getDiff();
        //        ys = c_rect.height / c_mm.getDiff();

        if (c_mm.isLog()) {

            // c_mm.setLog(true);
            //     ys = c_rect.height / c_mm.getDiff();
            //     return (c_rect.height - (((float)Math.log(y) - c_mm.getMin()) * ys)) + c_rect.y;
            return c_rect.height + c_rect.y - ((float) Math.log(y) - c_mm.getMin()) * ys;

            /*  float m = c_mm.getMax() / c_mm.getMin();

            float fac = (float) c_rect.height / (float) Math.log(m);

            float fmin = c_rect.height - ((float) Math.log((y / c_mm.getMin())) * fac);


            return fmin;
             */
            //return c_rect.height - ((float) Math.log((y - c_mm.getMin()) * c_yscaling) * fac);
        }

        return (c_rect.height - ((y - c_mm.getMin()) * c_yscaling)) + c_rect.y;

//        return c_rect.height - ((y - c_mm.getMin()) * c_yscaling);
    }

    double getValAtY(float y) {
        float val = 0;

        //  y = (c_rect.height - ((val - c_mm.getMin()) * c_yscaling)) + c_rect.y;
        // y-c_rect.y = c_rect.height - ((val - c_mm.getMin()) * c_yscaling)
        // y-c_rect.y-c_rect.height = - ((val - c_mm.getMin()) * c_yscaling)
        // -(y-c_rect.y-c_rect.heigh) = (val - c_mm.getMin()) * c_yscaling
        // (-(y-c_rect.y-c_rect.heigh))/c_yscaling = (val - c_mm.getMin())
        if (c_mm.isLog()) {
            float ys = c_rect.height / c_mm.getDiff();

            //val = return c_rect.height + c_rect.y - ((float)Math.log(y) - c_mm.getMin()) * ys;            
            // val + ((float)Math.log(y) - c_mm.getMin()) * ys = c_rect.height + c_rect.y
            // val/ys + ((float)Math.log(y) - c_mm.getMin()) = (c_rect.height + c_rect.y)/ys
            // val/ys  + ((float)Math.log(y) = (c_rect.height + c_rect.y)/ys + c_mm.getMin()) 
            //return (-(Math.exp(y)-c_rect.y-c_rect.height))/ys+c_mm.getMin();      
            return Math.exp((c_rect.height + c_rect.y) / ys + c_mm.getMin() - y / ys);

        }

        return (-(y - c_rect.y - c_rect.height)) / c_yscaling + c_mm.getMin();

        // return (y+c_rect.y-c_rect.height)/c_yscaling+c_mm.getMin();
    }


  
    private void drawCandleItem(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {

        Graphics2D g = ctx.g;

        Rectangle r = ctx.rect;

        if (i.open < i.close) {
            int xl = (int) (x + ctx.iwidth / 2);

            g.setColor(Color.BLACK);
            g.drawLine(xl, (int) ctx.getYc(i.close), xl, (int) ctx.getYc(i.high));
            g.drawLine(xl, (int) ctx.getYc(i.low), xl, (int) ctx.getYc(i.open));

            float w = ctx.iwidth;
            float h = (int) (ctx.getYc(i.open) - ctx.getYc(i.close));

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

    }

    private void drawBarItem(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        Graphics2D g = ctx.g;
        g.setColor(Color.BLACK);

        g.drawLine(x, (int) ctx.getYc(0), x, (int) ctx.getYc(i.volume));

        Rectangle r = ctx.rect;
    }

    /**
     * Char types
     */
    protected enum ChartType {
        CANDLESTICK,
        BAR,
        VOL,
    }

    ChartType ct = ChartType.CANDLESTICK;

    private void drawItem(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        switch (ct) {
            case CANDLESTICK:
                this.drawCandleItem(ctx, prevx, x, prev, i);
                break;
            case VOL:
                this.drawBarItem(ctx, prevx, x, prev, i);
                break;

        }
    }

    float c_yscaling;

    private void drawYLegend(RenderCtx ctx) {

        Graphics2D g = ctx.g;

        Rectangle dim;
        dim = this.clip_bounds;

       // Dimension rv = this.getSize();

        int yw = (int) (this.y_legend_width * this.em_width);

        g.drawLine(dim.width + dim.x - yw, 0, dim.width + dim.x - yw, dim.height);

        float y1 = getY(c_mm.getMin(false));
        float y2 = getY(c_mm.getMax(false));
        float ydiff = y1 - y2;
        System.out.printf("%s y1: %f, y2: %f, diff %f\n", Boolean.toString(c_mm.isLog()), y1, y2, ydiff);

        for (int yp = (int) y2; yp < y1; yp += em_width * 5) {
            g.drawLine(dim.width + dim.x - yw, yp, dim.width + dim.x - yw + em_width, yp);
            double v1 = getValAtY(yp);
            g.drawString(String.format("%.2f", v1), dim.width + dim.x - yw + em_width * 1.5f, yp + c_font_height / 3);
        }

        // c_yscaling = c_rect.height / c_mm.getDiff();
//System.out.printf("Step: %f\n",step);
        double v1, v2;
        v1 = getValAtY(y1);
        v2 = getValAtY(y2);
        System.out.printf("v1 %f, v2 %f\n", v1, v2);


        /*  for (float y = c_mm.getMin(); y < c_mm.getMax(); y += step) {

            int my = (int) getY(y); //c_rect.height - (int) ((y - c_mm.getMin()) * c_yscaling);

            g.drawLine(dim.width + dim.x - yw, my, dim.width + dim.x - yw + em_width, my);

            g.drawString(String.format("%.2f", y), dim.width + dim.x - yw + em_width * 1.5f, my + c_font_height / 3);
        }
         */
    }

    private MinMax c_mm = null;
    private Rectangle c_rect;

    void drawChart(RenderCtx ctx) {

        c_yscaling = c_rect.height / c_mm.getDiff();

        ctx.g.setClip(null);
        // ctx.g.setColor(Color.ORANGE);
        //     ctx.g.setClip(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);
        //   ctx.g.drawRect(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);     
        this.drawYLegend(ctx);

        ///  ctx.g.setColor(Color.ORANGE);
        int yw = (int) (this.y_legend_width * this.em_width);

        ctx.g.setClip(clip_bounds.x, clip_bounds.y, clip_bounds.width - yw, clip_bounds.height);
        //       ctx.g.setClip(ctx.rect.x, ctx.rect.y, ctx.rect.width-yw, ctx.rect.height);

        OHLCDataItem prev = null;
        for (int i = first_bar; i < last_bar && i < data.size(); i++) {
            OHLCDataItem di = data.get(i);
            int x = (int) (i * em_width * x_unit_width); //em_width;
            this.drawItem(ctx, x - em_width, x, prev, di); //, ctx.scaling, data.getMin());

            //    myi++;
            prev = di;

        }

    }

    boolean autoScroll = true;
    int lastvpos = 0;

    private void draw(Graphics2D g) {

        if (data == null) {
            return;
        }
        if (data.size() == 0) {
            return;
        }

        num_bars = data.size();

        c_mm = data.getMinMax(first_bar, last_bar);
        if (c_mm == null) {
            return;
        }

//        c_mm.min/= 1; //-= c_mm.getMin()/ 2.0f;
//       c_mm.max *= 1; //+= c_mm.getMax() / 10.0f;
        em_height = g.getFontMetrics().getHeight();
        em_width = g.getFontMetrics().stringWidth("M");

        XLegendDef xld = new XLegendDef();
        this.drawXLegend(g, xld);

        int pwidth = (int) (em_width * x_unit_width * (num_bars + 1)) + clip_bounds.width;
        //   int phight = 400;
        //   phight=this.getVisibleRect().height;

        this.setPreferredSize(new Dimension(pwidth, gdim.height));
        this.revalidate();

        int bww = (int) (data.size() * (this.x_unit_width * this.em_width));
        int p0 = pwidth - clip_bounds.width - (clip_bounds.width - (int) (13 * em_width));
        if (p0 < 0) {
            p0 = 0;
        }
        JViewport vp = (JViewport) this.getParent();
        Point pp = vp.getViewPosition();
        Point cp = vp.getViewPosition();

        if (autoScroll && this.lastvpos != cp.x) {
            autoScroll = false;
        }

        if (!autoScroll && cp.x >= p0) {
            autoScroll = true;
        }

        if (autoScroll) {
            vp.setViewPosition(new Point(p0, 0));
            lastvpos = p0;

        }

        int cheight = gdim.height - 6 * em_width;

        int h = (int) (cheight * 0.8);

        Rectangle r = new Rectangle(0, 0, pwidth, h);
        c_rect = r;

        RenderCtx ctx = new RenderCtx();
        //   c_rect.x = 0;
        //  c_rect.y = 50;
        //  c_rect.height = ;
        ctx.rect = c_rect;
        ctx.scaling = (float) r.height / (c_mm.getMax() - c_mm.getMin());
        ctx.min = c_mm.getMin();
        ctx.g = g;
        ctx.iwidth = (float) ((x_unit_width * em_width) * 0.9f);

        this.ct = ChartType.CANDLESTICK;
        logs = true;
        c_mm.setLog(true);
        drawChart(ctx);

        c_mm = data.getVolMinMax(first_bar, last_bar);

//        c_mm.min = 0f;
        c_mm.setMin(0);

        int h1 = h + em_width;
        h = (int) (cheight * 0.2);

        r = new Rectangle(0, h1, pwidth, h);
        c_rect = r;

        //   c_rect.x = 0;
        //   c_rect.y = 250;
        //   c_rect.height = 50;
        ctx.rect = c_rect;
        ctx.scaling = (float) r.height / (c_mm.getMax() - c_mm.getMin());
        ctx.min = c_mm.getMin();
        ctx.g = g;
        ctx.iwidth = (float) ((x_unit_width * em_width) * 0.9f);

        logs = false;
        c_mm.setLog(false);
        this.ct = ChartType.VOL;
        drawChart(ctx);

    }

    private float c_font_height;

    @Override
    public final void paintComponent(Graphics g) {
        if (Globals.se == null) {
            return;
        }

        super.paintComponent(g);

        // Calculate the number of pixels for 1 em
        em_width = g.getFontMetrics().stringWidth("M");

        this.gdim = this.getParent().getSize(gdim);
        this.getParent().setPreferredSize(gdim);

        //Object o = this.getParent();
        JViewport vp = (JViewport) this.getParent();

        //this.clip_bounds=g.getClipBounds();
        this.clip_bounds = vp.getViewRect();

        first_bar = (int) (clip_bounds.x / (this.x_unit_width * this.em_width));
        last_bar = 1 + (int) ((clip_bounds.x + clip_bounds.width - (this.y_legend_width * em_width)) / (this.x_unit_width * this.em_width));

//        num_bars = data.size(); // + (int) (clip_bounds.width / (this.x_unit_width * this.em_width))+5;
//        num_bars=1;
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

        ctxMenu = new javax.swing.JPopupMenu();
        compMenu = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();

        compMenu.setText("Compression");
        ctxMenu.add(compMenu);

        jCheckBoxMenuItem1.setMnemonic('l');
        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("Log Scale");
        jCheckBoxMenuItem1.setToolTipText("");
        ctxMenu.add(jCheckBoxMenuItem1);

        setBackground(java.awt.Color.white);
        setBorder(null);
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(300, 300));
        setRequestFocusEnabled(false);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 589, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        System.out.printf("There was a mosue event\n");

        if (!evt.isPopupTrigger() || true) {
            System.out.printf("But there was no pupe trigger\n");
            return;
        }

        //    this.invalidate();
        this.ctxMenu.setVisible(true);
        this.ctxMenu.show(this, evt.getX(), evt.getY());

        this.invalidate();
        this.repaint();


    }//GEN-LAST:event_formMousePressed

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved

        double n = evt.getPreciseWheelRotation() * (-1.0);

        if (n < 0) {
            if (this.x_unit_width > 0.3) {
                this.x_unit_width += 0.1 * n;
            }
        } else {
            this.x_unit_width += 0.1 * n;
        }

        this.invalidate();
        this.repaint();
    }//GEN-LAST:event_formMouseWheelMoved

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased

        System.out.printf("There was a mosue event released\n");

        if (!evt.isPopupTrigger()) {
            System.out.printf("But there was no pupe trigger\n");
            return;
        }

        //    this.invalidate();
        this.ctxMenu.setVisible(true);
        this.ctxMenu.show(this, evt.getX(), evt.getY());

        this.invalidate();
        this.repaint();


    }//GEN-LAST:event_formMouseReleased

    void setCompression(int timeFrame) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            data = Globals.se.getOHLCdata(timeFrame);
            invalidate();
            repaint();
        });

    }

    @Override
    public void UpdateQuote(Quote q) {
        //    System.out.print("Quote Received\n");
//        this.realTimeAdd(q.time, (float) q.price, (float)q.volume);

//        data.realTimeAdd(q.time, (float) q.price, (float) q.volume);
        //    this.invalidate();
        this.repaint();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu compMenu;
    private javax.swing.JPopupMenu ctxMenu;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    // End of variables declaration//GEN-END:variables
}
