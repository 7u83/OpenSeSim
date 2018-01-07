
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
import java.util.ArrayList;
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

    protected double x_legend_height = 6;

    protected double x_unit_width = 1.0;

    /**
     * width of y legend in em
     */
    protected float yl_width = 10;

    protected int num_bars = 4000;

    protected Rectangle clip_bounds = new Rectangle();

    private int first_bar, last_bar;

    /**
     * Creates new form Chart
     */
    public Chart() {
        if (Globals.se == null) {
            return;
        }

        initComponents();
        if (Globals.se == null) {
            return;
        }

        Globals.se.addQuoteReceiver(this);

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

        //int big_tick = 10;
        // long start;
        XLegendDef() {

        }

        String getAt(int unit) {

            int fs = data.getFrameSize();
            return Scheduler.formatTimeMillis(0 + unit * fs);

        }

    }

    protected void setXLegendHeight(int h) {
        this.x_legend_height = h;
    }

    /**
     * Text Color of X-legend
     */
    protected Color xl_color = null;

    /**
     * Background color of X-legend
     */
    protected Color xl_bgcolor = null;
    /**
     * Height of X-legend
     */
    protected int xl_height;

    /**
     * Draw the one and only one X legend
     *
     * @param g Graphics conext to draw
     * @param xld Definition
     */
    void drawXLegend(Graphics2D g, XLegendDef xld) {

        Color cur = g.getColor(); // save current color

        // Caluclate with of y legend in pixels
        int yl_width_p = (int) (yl_width * em_width);

        g.setClip(clip_bounds.x, clip_bounds.y, clip_bounds.width - yl_width_p, clip_bounds.height);

        int y = clip_bounds.height - em_height * xl_height;

        // Draw background
        if (this.xl_bgcolor != null) {

            g.setColor(xl_bgcolor);
            g.fillRect(clip_bounds.x, y, clip_bounds.width, em_height * xl_height);
            g.drawRect(clip_bounds.y, y, clip_bounds.width, em_height * xl_height);
            g.setColor(cur);
        }

        if (xl_color != null) {
            g.setColor(xl_color);
        }

        g.drawLine(clip_bounds.x, y, clip_bounds.width, y);

        Dimension dim = getSize();

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

        g.setColor(cur);
    }

    class RenderCtx {

        Rectangle rect;
        //float scaling;
        float min;
        Graphics2D g;
        float iwidth;

        float getY(float y) {

            float ys = rect.height / c_mm.getDiff();
            if (c_mm.isLog()) {
                return rect.height + rect.y - ((float) Math.log(y) - c_mm.getMin()) * ys;
            }
            return (rect.height - ((y - c_mm.getMin()) * c_yscaling)) + rect.y;
        }

        double getValAtY(float y) {
            float val = 0;

            if (c_mm.isLog()) {
                float ys = rect.height / c_mm.getDiff();

                return Math.exp((rect.height + rect.y) / ys + c_mm.getMin() - y / ys);

            }

            return (-(y - rect.y - rect.height)) / c_yscaling + c_mm.getMin();

        }

    }

    private void drawLineItem(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        Graphics2D g = ctx.g;
        if (prev == null) {
            prev = i;
        }
        int y1 = (int) ctx.getY(prev.close);
        int y2 = (int) ctx.getY(i.close);
        Color cur = g.getColor();
        g.setColor(Color.RED);
        g.drawLine(prevx, y1, x, y2);
        g.setColor(cur);
    }

    private void drawCandleItem(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {

        Graphics2D g = ctx.g;

        if (i.open < i.close) {
            int xl = (int) (x + ctx.iwidth / 2);

            g.setColor(Color.BLACK);
            g.drawLine(xl, (int) ctx.getY(i.close), xl, (int) ctx.getY(i.high));
            g.drawLine(xl, (int) ctx.getY(i.low), xl, (int) ctx.getY(i.open));

            float w = ctx.iwidth;
            float h = (int) (ctx.getY(i.open) - ctx.getY(i.close));

            g.setColor(Color.GREEN);
            g.fillRect((int) (x), (int) ctx.getY(i.close), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) ctx.getY(i.close), (int) w, (int) h);

        } else {
            int xl = (int) (x + ctx.iwidth / 2);
            g.setColor(Color.RED);
            g.drawLine(xl, (int) ctx.getY(i.high), xl, (int) ctx.getY(i.close));
            g.drawLine(xl, (int) ctx.getY(i.open), xl, (int) ctx.getY(i.low));

            float w = ctx.iwidth;
            float h = (int) (ctx.getY(i.close) - ctx.getY(i.open));

            g.fillRect((int) (x), (int) ctx.getY(i.open), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) ctx.getY(i.open), (int) w, (int) h);

        }

    }

    private void drawBarItem(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        Graphics2D g = ctx.g;
        g.setColor(Color.BLACK);

        g.drawLine(x, (int) ctx.getY(0), x, (int) ctx.getY(i.volume));

        Rectangle r = ctx.rect;
    }

    /**
     * Char types
     */
    protected enum ChartType {
        CANDLESTICK,
        LINE,
        BAR,
        VOL,
    }

    ChartType ct = ChartType.CANDLESTICK;

    private void drawItem(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        switch (ct) {
            case CANDLESTICK:
                this.drawCandleItem(ctx, prevx, x, prev, i);
                break;
            case LINE:
                this.drawLineItem(ctx, prevx, x, prev, i);
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
        int yw = (int) (this.yl_width * this.em_width);

        g.drawLine(dim.width + dim.x - yw, 0, dim.width + dim.x - yw, dim.height);

        float y1 = ctx.getY(c_mm.getMin(false));
        float y2 = ctx.getY(c_mm.getMax(false));
        float ydiff = y1 - y2;
//        System.out.printf("%s y1: %f, y2: %f, diff %f\n", Boolean.toString(c_mm.isLog()), y1, y2, ydiff);

        for (int yp = (int) y2; yp < y1; yp += em_width * 5) {
            g.drawLine(dim.width + dim.x - yw, yp, dim.width + dim.x - yw + em_width, yp);
            double v1 = ctx.getValAtY(yp);
            g.drawString(String.format("%.2f", v1), dim.width + dim.x - yw + em_width * 1.5f, yp + c_font_height / 3);
        }

        double v1, v2;
        v1 = ctx.getValAtY(y1);
        v2 = ctx.getValAtY(y2);

    }

    private MinMax c_mm = null;
//    private Rectangle c_rect;

    void drawChart(RenderCtx ctx) {

        c_yscaling = ctx.rect.height / c_mm.getDiff();

        ctx.g.setClip(null);
        // ctx.g.setColor(Color.ORANGE);
        //     ctx.g.setClip(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);
        //   ctx.g.drawRect(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);     
        this.drawYLegend(ctx);

        ///  ctx.g.setColor(Color.ORANGE);
        int yw = (int) (this.yl_width * this.em_width);

        ctx.g.setClip(clip_bounds.x, clip_bounds.y, clip_bounds.width - yw, clip_bounds.height);
        //       ctx.g.setClip(ctx.rect.x, ctx.rect.y, ctx.rect.width-yw, ctx.rect.height);

        OHLCDataItem prev = null;
        for (int i = first_bar; i < last_bar && i < data.size(); i++) {
            OHLCDataItem di = data.get(i);
            int x = (int) (i * em_width * x_unit_width); //em_width;
            this.drawItem(ctx, (int) (x - em_width * x_unit_width), x, prev, di); //, ctx.scaling, data.getMin());

            //    myi++;
            prev = di;

        }

    }

    boolean autoScroll = true;
    int lastvpos = 0;

    /**
     * definition for a sub-chart window
     */
    public class SubChartDef {

        /**
         * Height of sub-chart in percent
         */
        public float height;

        /**
         * top padding in em_height
         */
        public float padding_top = 0;
        /**
         * bottom padding in em_height (not implemented yet)
         */
        public float padding_bottom = 0;

        public ChartType type;
        public OHLCData data;

        public Color bgcolor = null;

        /**
         * logarithmic scaling
         */
        public boolean log = false;

    }

    protected OHLCData data;

    ArrayList<SubChartDef> charts = new ArrayList<>();

    protected void addChart(SubChartDef d) {

        charts.add(d);

    }

    void drawAll(Graphics2D g) {
        int pwidth = (int) (em_width * x_unit_width * (num_bars + 1)) + clip_bounds.width;
        this.setPreferredSize(new Dimension(pwidth, gdim.height));
        this.revalidate();

        int h1 = 0;

        for (SubChartDef d : charts) {

            if (d.data == null) {
                System.out.printf("Data is null\n");
                System.exit(0);
            }

            // calclulate the min/max values
            switch (d.type) {
                case VOL:
                    c_mm = d.data.getVolMinMax(first_bar, last_bar);
                    c_mm.setMin(0);
                    break;
                default:
                    c_mm = d.data.getMinMax(first_bar, last_bar);
            }

            // Calculate the height for all sub-charts
            // this is the height of out panel minus the height of x-legend
            int chartwin_height = clip_bounds.height - xl_height * em_height;

            // Caclulate the height of our sub-chart 
            int subchartwin_height = (int) (chartwin_height * d.height);

            // Draw background
            if (d.bgcolor != null) {
                Color cur = g.getColor();
                g.setColor(d.bgcolor);
                g.fillRect(clip_bounds.x, h1, clip_bounds.width, subchartwin_height);
                g.drawRect(clip_bounds.x, h1, clip_bounds.width, subchartwin_height);
                g.setColor(cur);
            }

            // Caclulate the top padding 
            int pad_top = (int) (subchartwin_height * d.padding_top);

            RenderCtx ctx = new RenderCtx();

            ctx.rect = new Rectangle(0, h1 + pad_top, pwidth, subchartwin_height - pad_top);
//            ctx.scaling = (float) ctx.rect.height / (c_mm.getMax() - c_mm.getMin());
            ctx.min = c_mm.getMin();
            ctx.g = g;
            ctx.iwidth = (float) ((x_unit_width * em_width) * 0.9f);

            this.ct = d.type;
//            logs = false;
            c_mm.setLog(d.log);

            drawChart(ctx);

            h1 = h1 + subchartwin_height;

        }

    }

    protected void setupSubCharts() {

    }

    private void draw(Graphics2D g) {

        if (data == null) {
            return;
        }
        if (data.size() == 0) {
            return;
        }

//        Point m = MouseInfo.getPointerInfo().getLocation() ;
//        g.drawLine(0, m.y, 1000, m.y);
        int pwidth = (int) (em_width * x_unit_width * (num_bars + 1)) + clip_bounds.width;

        this.setPreferredSize(new Dimension(pwidth, gdim.height));
        this.revalidate();

        int bww = (int) (data.size() * (this.x_unit_width * this.em_width));
        int p0 = pwidth - clip_bounds.width - (clip_bounds.width - (int) (13 * em_width));
        if (p0 < 0) {
            p0 = 0;
        }
        JViewport vp = (JViewport) this.getParent();
   //     Point pp = vp.getViewPosition();
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

        this.charts = new ArrayList<>();
        setupSubCharts();

        num_bars = data.size();

        em_height = g.getFontMetrics().getHeight();
        em_width = g.getFontMetrics().stringWidth("M");

        XLegendDef xld = new XLegendDef();
        this.drawXLegend(g, xld);

        drawAll(g);

    }

    Dimension gdim;

    Rectangle c_rect0;


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
        last_bar = 1 + (int) ((clip_bounds.x + clip_bounds.width - (this.yl_width * em_width)) / (this.x_unit_width * this.em_width));

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

        setBackground(java.awt.Color.white);
        setBorder(null);
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(300, 300));
        setRequestFocusEnabled(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
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

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved

    }//GEN-LAST:event_formMouseMoved

    @Override
    public void UpdateQuote(Quote q) {
        this.repaint();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
