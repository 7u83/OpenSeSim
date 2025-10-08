
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import sesim.MinMax;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Chart extends javax.swing.JPanel implements QuoteReceiver, Scrollable {


    private int emWidth;

    // hight of x-axis area in em 
    private int xAxisAreaHight = 3;

    
    private double x_unit_width = 1.0;

    private float candleWidth = (float) ((x_unit_width * emWidth) * 0.9f);

    /**
     * width of y legend in em
     */
    private int leftYAxisAreaWidth = 0;
    private int rightYAxisAreaWidth = 10;

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

    public void setXUnitWidth(double w) {
        this.x_unit_width = w;
        candleWidth = (float) ((x_unit_width * emWidth) * 0.9f);
        this.updateView();
        this.revalidate();
        this.repaint();
    }

    public double getXUnitWidth() {
        return this.x_unit_width;
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
            return formatTimeMillis(0 + unit * fs);

        }

    }

    protected void setXLegendHeight(int h) {
        this.xAxisAreaHight = h;
    }

    /**
     * Text Color of X-legend
     */
    protected Color xl_color = null;

    /**
     * Background color of X-legend
     */
    protected Color xl_bgcolor = Color.white;

    /**
     * Height of X-legend
     */
    //   protected int xl_height;
    public String formatTimeMillis(long t) {
        Date date = new Date(t);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        // formatter = new SimpleDateFormat("d. MMM, yyyy HH:mm:ss");
        String dateFormatted = formatter.format(date);

        // Datum ohne Zeit
        DateFormat dateFormatter = new SimpleDateFormat("d. MMM, yyyy");
        // Nur Zeit
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

        String formatted = dateFormatter.format(date) + "\n" + timeFormatter.format(date);

        long seconds = (t / 1000) % 60;
        long minutes = (t / 1000 / 60) % 60;
        long hours = (t / 1000) / (60 * 60);

        //return dateFormatted;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);

    }

    /**
     * Draw the one and only one X legend
     *
     * @param g Graphics conext to draw
     */
    void drawXLegend_r(Graphics2D g, XLegendDef xld) {

        Rectangle clip = g.getClipBounds();

        Color cur = g.getColor(); // save current color

        // Caluclate with of y legend in pixels
        //   int yl_width_p = (int) (rightYAxisAreaWidth * emWidth);
        //      g.setClip(clip_bounds.x, clip_bounds.y, clip_bounds.width - yl_width_p, clip_bounds.height);
        //     int y = clip.height - emWidth * xl_height;
        //    y = 0;
        // Draw background
        if (this.xl_bgcolor != null) {
            g.setColor(xl_bgcolor);
            g.fillRect(clip.x, clip.y, clip.width, (int) (emWidth * this.xAxisAreaHight));
            g.drawRect(clip.y, clip.y, clip.width, (int) (emWidth * this.xAxisAreaHight));
            g.setColor(cur);
        }

        if (xl_color != null) {
            g.setColor(xl_color);
        }

        g.drawLine(clip.x, clip.y, clip.width, clip.y);

        Dimension dim = getSize();

        int n;
        double x;

        long big_tick = 1;

        double btl, xxx;
        do {
            big_tick++;
            btl = emWidth * big_tick * x_unit_width;
            xxx = 7 * emWidth;

        } while (btl < xxx);

        for (n = 0, x = 0; x < dim.width; x += emWidth * x_unit_width) {

            if (n % big_tick == 1) {
                g.drawLine((int) x, clip.y, (int) x, clip.y + emWidth);
                String text;
                text = formatTimeMillis(0 + n * data.getFrameSize());

                int swidth = g.getFontMetrics().stringWidth(text);

                g.drawString(text, (int) x - swidth / 2, clip.y + emWidth * 2);
            } else {
                g.drawLine((int) x, clip.y, (int) x, clip.y + emWidth / 2);
            }

            if (n % big_tick == 0) {

            }

            n += 1;
        }

        g.setColor(cur);
    }

    void drawXLegend(Graphics2D g, XLegendDef xld) {

        Rectangle clip = g.getClipBounds();

        //    System.out.printf("X: %d, Y:%d, W: %d, H:%d\b", clip.x, clip.y, clip.width, clip.height);
        Graphics2D g2 = (Graphics2D) g.create();

        int w = (int) (clip.width - (this.leftYAxisAreaWidth * this.emWidth + this.rightYAxisAreaWidth * this.emWidth));
        int y = clip.height - (int) (this.xAxisAreaHight * this.emWidth);

        g2.translate(this.leftYAxisAreaWidth * this.emWidth, y);
        g2.setClip(clip.x, clip.y, w, (int) (this.xAxisAreaHight * this.emWidth));

        drawXLegend_r(g2, xld);
        g2.dispose(); // alte Graphics2D wiederherstellen

    }

    class DrawCtx {

        MinMax c_mm = null;
        float c_yscaling;
        Rectangle rect;
        //    float scaling;
        //    float min;
        Graphics2D g;
        //   float candleWidth;

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

    private void drawLineItem(DrawCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
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

    private void drawCandleItem(DrawCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {

        Graphics2D g = ctx.g;

        if (i.open < i.close) {
            int xl = (int) (x + candleWidth / 2);

            g.setColor(Color.BLACK);
            g.drawLine(xl, (int) ctx.getY(i.close), xl, (int) ctx.getY(i.high));
            g.drawLine(xl, (int) ctx.getY(i.low), xl, (int) ctx.getY(i.open));

            float w = candleWidth;
            float h = (int) (ctx.getY(i.open) - ctx.getY(i.close));

            g.setColor(Color.GREEN);
            g.fillRect((int) (x), (int) ctx.getY(i.close), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) ctx.getY(i.close), (int) w, (int) h);

        } else {
            int xl = (int) (x + candleWidth / 2);
            g.setColor(Color.RED);
            g.drawLine(xl, (int) ctx.getY(i.high), xl, (int) ctx.getY(i.close));
            g.drawLine(xl, (int) ctx.getY(i.open), xl, (int) ctx.getY(i.low));

            float w = candleWidth;
            float h = (int) (ctx.getY(i.close) - ctx.getY(i.open));

            g.fillRect((int) (x), (int) ctx.getY(i.open), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) ctx.getY(i.open), (int) w, (int) h);

        }

    }

    private void drawBarItem(DrawCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
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

    private void drawItem(DrawCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
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

    private void drawYLegend(DrawCtx ctx, SubChartDef d) {

        Graphics2D g = ctx.g;
        Rectangle clip = g.getClipBounds();
        
        
        
                // Draw background
        if (d.bgcolor != null) {
            Color cur = ctx.g.getColor();
            ctx.g.setColor(d.rightYColor);
            ctx.g.fillRect(clip.x, clip.y, clip.width, clip.height);
            //g.drawRect(clip_bounds.x, h1, clip.width, subchartwin_height);
            ctx.g.setColor(cur);
        }
        

        Rectangle dim;
        dim = clip; //this.clip_bounds;

        // Dimension rv = this.getSize();
        int yw = (int) (this.rightYAxisAreaWidth * this.emWidth);

        g.drawLine(dim.width + dim.x - yw, 0, dim.width + dim.x - yw, dim.height);

        float y1 = ctx.getY(ctx.c_mm.getMin(false));
        float y2 = ctx.getY(ctx.c_mm.getMax(false));
        float ydiff = y1 - y2;
//        System.out.printf("%s y1: %f, y2: %f, diff %f\n", Boolean.toString(c_mm.isLog()), y1, y2, ydiff);

        for (int yp = (int) y2; yp < y1; yp += emWidth * 5) {
            g.drawLine(dim.width + dim.x - yw, yp, dim.width + dim.x - yw + emWidth, yp);
            double v1 = ctx.getValAtY(yp);
            g.drawString(String.format("%.2f", v1), dim.width + dim.x - yw + emWidth * 1.5f, yp + c_font_height / 3);
        }

        double v1, v2;
        v1 = ctx.getValAtY(y1);
        v2 = ctx.getValAtY(y2);

    }

    void drawChart(Graphics2D g) {

    }

    void drawMainChart(DrawCtx ctx, SubChartDef d) {

        Rectangle clip = ctx.g.getClipBounds();

        // Draw background
        if (d.bgcolor != null) {
            Color cur = ctx.g.getColor();
            ctx.g.setColor(d.bgcolor);
            ctx.g.fillRect(clip.x, clip.y, clip.width, clip.height);
            //g.drawRect(clip_bounds.x, h1, clip.width, subchartwin_height);
            ctx.g.setColor(cur);
        }

        ctx.c_yscaling = ctx.rect.height / ctx.c_mm.getDiff();
//        ctx.g.setClip(null);
        // ctx.g.setColor(Color.ORANGE);
        //     ctx.g.setClip(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);
        //   ctx.g.drawRect(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);     
        
        
        
    //  this.drawYLegend(ctx);
        ///  ctx.g.setColor(Color.ORANGE);

     //   ctx.g.setClip(clip_bounds.x, clip_bounds.y, clip_bounds.width - yw, clip_bounds.height);
        //       ctx.g.setClip(ctx.rect.x, ctx.rect.y, ctx.rect.width-yw, ctx.rect.height);

        //       first_bar = (int) (clip.x / (this.x_unit_width * this.emWidth));
        //      last_bar = 1 + (int) ((clip.x + clip.width - (this.rightYAxisAreaWidth * emWidth)) / (this.x_unit_width * this.emWidth));
        //       g.translate(this.leftYAxisAreaWidth*this.emWidth, 0);
        OHLCDataItem prev = null;
        for (int i = first_bar; i < last_bar && i < data.size(); i++) {
            OHLCDataItem di = data.get(i);
            int x = (int) (i * emWidth * x_unit_width); //em_width;
            this.drawItem(ctx, (int) (x - emWidth * x_unit_width), x, prev, di); //, ctx.scaling, data.getMin());

//            System.out.printf("DRAW BARD %d AT X%d\n",i, x);
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
         * top padding in emWidth
         */
        public float paddingTop = 0;

        /**
         * bottom padding in emWidth
         */
        public float paddingBottom = 0;

        public ChartType type;
        public OHLCData data;

        public Color bgcolor = null;

        public boolean leftYAxis = false;
        public boolean righYAxis = true;
        
        public OHLCData rightYData = null;
        public Color rightYColor=Color.GREEN;
        
        public OHLCData lefttYData = null;

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

        DrawCtx ctx = new DrawCtx();
        Rectangle clip = g.getClipBounds();
        
        for (SubChartDef d : charts){
            
            
        }

        int w = (int) (clip.width - (this.leftYAxisAreaWidth * this.emWidth + this.rightYAxisAreaWidth * this.emWidth));
        int y = clip.height - (int) (this.xAxisAreaHight * this.emWidth);

        first_bar = (int) (clip.x / (this.x_unit_width * this.emWidth));
        last_bar = 1 + (int) ((clip.x + w) / (this.x_unit_width * this.emWidth));

        int pwidth = (int) (emWidth * x_unit_width * (num_bars + 1))
                + clip.width - 100;
        //this.leftYAxisAreaWidth*this.emWidth;

        //    this.setPreferredSize(new Dimension(pwidth, gdim.height));
        //    this.revalidate();
        int h1 = 0;

        for (SubChartDef d : charts) {

            if (d.data == null) {
                System.out.printf("Data is null\n");
                System.exit(0);
            }

            // calclulate the min/max values
            switch (d.type) {
                case VOL:
                    ctx.c_mm = d.data.getVolMinMax(first_bar, last_bar);
                    ctx.c_mm.setMin(0);
                    break;
                default:
                    ctx.c_mm = d.data.getMinMax(first_bar, last_bar);
            }

            // Calculate the height for all sub-charts
            // this is the height of out panel minus the height of x-legend
            int chartwin_height = clip.height - (int) this.xAxisAreaHight * emWidth;

            // Caclulate the height of our sub-chart 
            int subchartwin_height = (int) (chartwin_height * d.height);

            /*        // Draw background
            if (d.bgcolor != null) {
                Color cur = g.getColor();
                g.setColor(d.bgcolor);
                g.fillRect(clip.x, h1, w, subchartwin_height);
                //g.drawRect(clip_bounds.x, h1, clip.width, subchartwin_height);
                g.setColor(cur);
            }
             */
            // Caclulate the top padding 
            int pad_top = (int) (subchartwin_height * d.paddingTop);
            ctx.rect = new Rectangle(0, h1 + pad_top, pwidth, subchartwin_height - pad_top);
//            ctx.scaling = (float) ctx.rect.height / (c_mm.getMax() - c_mm.getMin());
//            ctx.min = c_mm.getMin();
            //ctx.g = g;
//            ctx.candleWidth = (float) ((x_unit_width * emWidth) * 0.9f);

            this.ct = d.type;
//            logs = false;
            ctx.c_mm.setLog(d.log);

            Graphics2D g2 = (Graphics2D) g.create();
            ctx.g = g2;
            g2.translate(this.leftYAxisAreaWidth * this.emWidth, 0);
            g2.setClip(clip.x, clip.y, w, subchartwin_height);
            drawMainChart(ctx, d);
            g2.dispose();
            
            
            ctx.c_yscaling = ctx.rect.height / ctx.c_mm.getDiff();
//        ctx.g.setClip(null);
        // ctx.g.setColor(Color.ORANGE);
        //     ctx.g.setClip(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);
        //   ctx.g.drawRect(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);     
        
        
            g2 = (Graphics2D)g.create();
            ctx.g = g2;
            ctx.g.translate(this.leftYAxisAreaWidth*this.emWidth+w,0);
            ctx.g.setClip(clip.x,clip.y, this.rightYAxisAreaWidth*this.emWidth, subchartwin_height);
        
          this.drawYLegend(ctx,d);
            g2.dispose();

            h1 = h1 + subchartwin_height;

        }

    }

    protected void setupSubCharts() {

    }

    private void draw(Graphics2D g) {

        emWidth = g.getFontMetrics().charWidth('M');
        candleWidth = (float) ((x_unit_width * emWidth) * 0.9f);

        if (data == null) {
            return;
        }
        if (data.size() == 0) {
            return;
        }

        /*    int pwidth = (int) (emWidth * x_unit_width * (num_bars + 1)) + clip_bounds.width;
        
     

        this.setPreferredSize(new Dimension(pwidth, gdim.height));
         this.revalidate();

        int bww = (int) (data.size() * (this.x_unit_width * this.emWidth));
        int p0 = pwidth - clip_bounds.width - (clip_bounds.width - (int) (13 * emWidth));
        if (p0 < 0) {
            p0 = 0;
        }*/
 /*    JViewport vp = (JViewport) this.getParent();
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
         */
        this.charts = new ArrayList<>();
        setupSubCharts();

        num_bars = data.size();

        //    emWidth = g.getFontMetrics().getHeight();
        // stringWidth("M");
        XLegendDef xld = new XLegendDef();
        this.drawXLegend(g, xld);

        drawAll(g);

    }

//    Dimension gdim;
    private float c_font_height;

    @Override
    public final void paintComponent(Graphics g) {
        if (Globals.se == null) {
            return;
        }

        super.paintComponent(g);

        this.setDoubleBuffered(true);

        // Calculate the number of pixels for 1 em
        //    emWidth = g.getFontMetrics().stringWidth("M");
        //      this.gdim = this.getParent().getSize(gdim);
        //       this.getParent().setPreferredSize(gdim);
        //Object o = this.getParent();
        JViewport vp = (JViewport) this.getParent();

        //this.clip_bounds=g.getClipBounds();
        this.clip_bounds = vp.getViewRect();

        first_bar = (int) (clip_bounds.x / (this.x_unit_width * this.emWidth));
        last_bar = 1 + (int) ((clip_bounds.x + clip_bounds.width - (this.rightYAxisAreaWidth * emWidth)) / (this.x_unit_width * this.emWidth));

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

    private int lastMaxPos = 0;

    void updateView() {
        JViewport vp = (JViewport) this.getParent();
        Rectangle clip = vp.getViewRect();

        Point pp = vp.getViewPosition();

        JScrollPane scrollPane = (JScrollPane) vp.getParent();

        // 3. Zugriff auf die horizontale JScrollBar
        JScrollBar hsb = scrollPane.getHorizontalScrollBar();

        // 4. Die maximale Verschiebungsposition (MaxScrollPos)
        //    ist der Maximum-Wert (Gesamtbreite) minus der Extent (sichtbare Breite).
        //    Dieser Wert ist identisch mit hsb.getMaximum() - hsb.getExtent().
        int maxPos = hsb.getMaximum();
        maxPos = this.getPreferredSize().width - vp.getWidth();

        // Da hsb.getValue() immer zwischen hsb.getMinimum()
        /*        

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
         */
  //      clip.width = clip.width - this.emWidth * (this.leftYAxisAreaWidth + this.rightYAxisAreaWidth);

        int pwidth = (int) (x_unit_width * (emWidth * (num_bars + 1)) + emWidth * (20 + this.leftYAxisAreaWidth));
        if (pwidth < clip.width) {
            pwidth = clip.width;
        }

        //System.out.printf("SETUP: UNITW:%f EM:%d BARS%d, CLIP: %d, PWIDT %d\n",x_unit_width,emWidth,num_bars,clip.width, pwidth);
//        System.out.printf("GET PWIDTH: %d, VIEW POS: %d, Scroll MAX: %d\n",pwidth-vp.getWidth(),pp.x, maxPos);
//        System.out.printf("VIEW RECT: X:%d, W: %d PP-X: %d\n", clip.x,clip.width, pp.x-clip.width);
        Dimension gdim;
        gdim = this.getParent().getSize();
        this.setPreferredSize(new Dimension(pwidth, gdim.height));

        if (autoScroll) {
            System.out.printf("LASTMAX: %d, MAX:%d,PPX: %d\n", lastMaxPos, maxPos, pp.x);
            if (pp.x == lastMaxPos || pp.x == maxPos) {
                lastMaxPos = pwidth - vp.getWidth();
                int currentYPos = vp.getViewPosition().y;
                vp.setViewPosition(new Point(lastMaxPos, currentYPos));
            }
        }

    }

    @Override
    public void UpdateQuote(Quote q) {
        updateView();

        this.revalidate();
        this.repaint();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
