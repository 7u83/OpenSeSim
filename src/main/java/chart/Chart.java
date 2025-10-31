/*
 * Copyright (c) 2025, 7u83 <7u83@mail.ru>
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

import sesim.OHLCDataItem;
import sesim.OHLCData;
import java.awt.*;
import sesim.Exchange.*;

import gui.Globals;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import sesim.MinMax;
import sesim.Quote;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Chart extends javax.swing.JPanel implements QuoteReceiver, Scrollable {

    private int emWidth;

    // hight of x-axis area in em 
    private int xAxisAreaHight = 3;

    private double x_unit_width = 1.0f;

    private float candleWidth = (float) ((x_unit_width * emWidth) * 0.9f);

    /**
     * width of y legend in em
     */
    private int leftYAxisAreaWidth = 0;
    private int rightYAxisAreaWidth = 10;

    //   protected int num_bars = 4000;
    //protected Rectangle clip_bounds = new Rectangle();

    private int first_bar, last_bar;

    private int mouseX = -1;
    private int mouseY = -1;
    private boolean mouseInside = false;

    /**
     * Creates new form Chart
     */
    public Chart() {
        initComponents();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint(); // neu zeichnen, wenn sich der Mauszeiger bewegt
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseInside = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseInside = false;
                repaint();
            }
        });
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
    String formatTimeMillis(long t) {
        //    Date date = new Date(t);
        //  DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        // formatter = new SimpleDateFormat("d. MMM, yyyy HH:mm:ss");
        //   String dateFormatted = formatter.format(date);

        // Datum ohne Zeit
//        DateFormat dateFormatter = new SimpleDateFormat("d. MMM, yyyy");
        // Nur Zeit
        //      DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        //   String formatted = dateFormatter.format(date) + "\n" + timeFormatter.format(date);
        long seconds = (t / 1000) % 60;
        long minutes = (t / 1000 / 60) % 60;
        long hours = (t / 1000) / (60 * 60);
//System.out.printf("BARTIME %d\n",t);
        //return dateFormatted;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);

    }

    /**
     * Draw the one and only one X legend
     *
     * @param g Graphics conext to draw
     */
    private void drawXLegend_r(Graphics2D g, XLegendDef xld) {

        Rectangle clip; // = g.getClipBounds();

        clip = getVisibleRect();

        Color cur = g.getColor(); // save current color

        // Caluclate with of y legend in pixels
        //   int yl_width_p = (int) (rightYAxisAreaWidth * emWidth);
        //      g.setClip(clip_bounds.x, clip_bounds.y, clip_bounds.width - yl_width_p, clip_bounds.height);
        //     int y = clip.height - emWidth * xl_height;
        //    y = 0;
        // Draw background
        if (this.xl_bgcolor != null) {
            g.setColor(xl_bgcolor);
            g.fillRect(clip.x, clip.y, clip.width, (emWidth * this.xAxisAreaHight));
            g.drawRect(clip.y, clip.y, clip.width, (emWidth * this.xAxisAreaHight));
            g.setColor(cur);
        }

        if (xl_color != null) {
            g.setColor(xl_color);
        }

        g.drawLine(clip.x, clip.y, clip.width, clip.y);

        Dimension dim = getSize();

        long n;
        double x;

        long big_tick = 1;

        double btl, xxx;
        do {
            big_tick++;
            btl = emWidth * big_tick * x_unit_width;
            xxx = 7 * emWidth;

        } while (btl < xxx);

        //     System.out.printf("FIRST/LAST %d/%d\n", first_bar,last_bar);
        for (n = first_bar, x = emWidth * x_unit_width * first_bar; n < last_bar && x < dim.width; x += emWidth * x_unit_width) {

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

    private void drawXLegend(Graphics2D g, XLegendDef xld) {

        Rectangle clip; // = g.getClipBounds();
        clip = this.getVisibleRect();

        //    System.out.printf("X: %d, Y:%d, W: %d, H:%d\b", clip.x, clip.y, clip.width, clip.height);
        Graphics2D g2 = (Graphics2D) g.create();

        int w = (clip.width - (this.leftYAxisAreaWidth * this.emWidth + this.rightYAxisAreaWidth * this.emWidth));
        int y = clip.height - (this.xAxisAreaHight * this.emWidth);

        g2.translate(this.leftYAxisAreaWidth * this.emWidth, y);
        g2.setClip(clip.x, clip.y, w, (this.xAxisAreaHight * this.emWidth));

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

        float getValAtY(float y) {
            //  float val = 0;

            if (c_mm.isLog()) {
                float ys = rect.height / c_mm.getDiff();

                return (float) Math.exp((rect.height + rect.y) / ys + c_mm.getMin() - y / ys);

            }

            return (-(y - rect.y - rect.height)) / c_yscaling + c_mm.getMin();

        }

    }

    private void drawLineItem(DrawCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        Graphics2D g = ctx.g;
        if (prev == null) {
            prev = i;
        }
        int y1 = (int) ctx.getY(prev.getClose());
        int y2 = (int) ctx.getY(i.getClose());
        Color cur = g.getColor();
        g.setColor(Color.RED);
        g.drawLine(prevx, y1, x, y2);
        g.setColor(cur);
    }

    private void drawCandleItem(DrawCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {

        //    System.out.printf("Draw ohlc: %f %f %f %f\n", i.getOpen(), i.getHigh(),i.getLow(),i.getClose());
        Graphics2D g = ctx.g;

        if (i.getOpen() < i.getClose()) {
            int xl = (int) (x + candleWidth / 2);

            g.setColor(Color.BLACK);
            g.drawLine(xl, (int) ctx.getY(i.getClose()), xl, (int) ctx.getY(i.getHigh()));
            g.drawLine(xl, (int) ctx.getY(i.getLow()), xl, (int) ctx.getY(i.getOpen()));

            float w = candleWidth;
            float h = (int) (ctx.getY(i.getOpen()) - ctx.getY(i.getClose()));

            g.setColor(Color.GREEN);
            g.fillRect((int) (x), (int) ctx.getY(i.getClose()), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) ctx.getY(i.getClose()), (int) w, (int) h);

        } else {
            int xl = (int) (x + candleWidth / 2);
            g.setColor(Color.RED);
            g.drawLine(xl, (int) ctx.getY(i.getHigh()), xl, (int) ctx.getY(i.getClose()));
            g.drawLine(xl, (int) ctx.getY(i.getOpen()), xl, (int) ctx.getY(i.getLow()));

            float w = candleWidth;
            float h = (int) (ctx.getY(i.getClose()) - ctx.getY(i.getOpen()));

            g.fillRect(x, (int) ctx.getY(i.getOpen()), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((x), (int) ctx.getY(i.getOpen()), (int) w, (int) h);

        }

    }

    private void drawBarItem(DrawCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        Graphics2D g = ctx.g;
        g.setColor(Color.gray);

        g.drawLine(x, (int) ctx.getY(0), x, (int) ctx.getY(i.getVolume()));
        float w = candleWidth;
        //float h = (int) (ctx.getY(0) - ctx.getY(i.getClose()));

        int h = (int) ctx.getY(0) - (int) ctx.getY(i.getVolume());

        //   g.fillRect(x, (int) ctx.getY(i.getVolume()), (int) w, (int) ctx.getY(0));
        g.fillRect(x, (int) ctx.getY(0) - h, (int) w, h);
        Rectangle r = ctx.rect;
    }

    /**
     * Char types
     */
    protected enum ChartType {
        CANDLESTICK,
        LINE,
        BAR,
        VOL

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
        int yw = (this.rightYAxisAreaWidth * this.emWidth);

        g.drawLine(dim.width + dim.x - yw, 0, dim.width + dim.x - yw, dim.height);

        float y1 = ctx.getY(ctx.c_mm.getMin(false));
        float y2 = ctx.getY(ctx.c_mm.getMax(false));
        //     float ydiff = y1 - y2;

        for (int yp = (int) y2; yp < y1; yp += emWidth * 5) {
            g.drawLine(dim.width + dim.x - yw, yp, dim.width + dim.x - yw + emWidth, yp);
            float v1 = ctx.getValAtY(yp);
            g.drawString(String.format("%.2f", v1), dim.width + dim.x
                    - yw + emWidth * 1.5f, yp + fontHeight / 3);
        }

        /*     float v1, v2;
        v1 = ctx.getValAtY(y1);
        v2 = ctx.getValAtY(y2);*/
    }


    void drawMainChart(DrawCtx ctx, SubChartDef d) {

        Rectangle clip = getVisibleRect();

        // Fill background
        if (d.bgcolor != null) {
            Color cur = ctx.g.getColor();
            ctx.g.setColor(d.bgcolor);
            ctx.g.fillRect(clip.x, clip.y, clip.width, clip.height);
            ctx.g.setColor(Color.BLACK);
            ctx.g.drawLine(clip.x, clip.y, clip.x + clip.width, clip.y);
            //g.drawRect(clip_bounds.x, h1, clip.width, subchartwin_height);
            ctx.g.setColor(cur);
        }

        OHLCDataItem prev = null;
        for (int i = first_bar; i < last_bar && i < data.size(); i++) {
            OHLCDataItem di = data.get(i);
            int x = (int) (i * emWidth * x_unit_width); //em_width;
            this.drawItem(ctx, (int) (x - emWidth * x_unit_width), x, prev, di); //, ctx.scaling, data.getMin());
            prev = di;
        }

    }

    boolean autoScroll = true;
    //  int lastvpos = 0;

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
        public Color rightYColor = Color.GREEN;

        public OHLCData lefttYData = null;

        /**
         * logarithmic scaling
         */
        public boolean log = false;

        public Color crossColor;

        public float pad_top = 0;
        public float pad_bot = 0;

    }

    protected OHLCData data;

    ArrayList<SubChartDef> charts = new ArrayList<>();

    protected void addChart(SubChartDef d) {

        charts.add(d);

    }

    void drawCross(Graphics2D g) {
        if (!mouseInside) {
            return;
        }

        // Mausposition absolut auf dem Bildschirm
        Point mouseOnScreen = MouseInfo.getPointerInfo().getLocation();

        // Position relativ zum Panel
        Point panelLocationOnScreen = this.getLocationOnScreen();
        mouseX = mouseOnScreen.x - panelLocationOnScreen.x;
        mouseY = mouseOnScreen.y - panelLocationOnScreen.y;

        // Kreuz zeichnen
        int size = 10;
        g.drawLine(mouseX - size, mouseY, mouseX + size, mouseY);
        g.drawLine(mouseX, mouseY - size, mouseX, mouseY + size);

        Rectangle clip = getVisibleRect();

        int h1 = 0;

        Color c = Color.BLUE;
        SubChartDef cd = null;

        for (SubChartDef d : charts) {
            // Calculate the height for all sub-charts
            // this is the height of out panel minus the height of x-legend
            int chartwin_height = clip.height - this.xAxisAreaHight * emWidth;
            // Caclulate the height of our sub-chart 
            int subchartwin_height = (int) (chartwin_height * d.height);

            if (mouseY < h1 + subchartwin_height) {
                c = d.crossColor;
                cd = d;
                break;
            }

            h1 = h1 + subchartwin_height;
        }

        if (mouseX >= 0 && mouseY >= 0) {
            g.setColor(c);
            g.drawLine(0, mouseY, getWidth(), mouseY);   // horizontale Linie
            g.drawLine(mouseX, 0, mouseX, getHeight());  // vertikale Linie
        }

        /*         long x  =        (long)(emWidth * x_unit_width * time);
         x/time =  (long)(emWidth * x_unit_width);
         1/time = (long)(emWidth * x_unit_width)/x;*/
        long n; //= (long) (mouseX * emWidth * x_unit_width);

        double bars = clip.width / (emWidth * x_unit_width) * data.getFrameSize();

        n = (long) (bars * (mouseX + 1) / clip.width);
        //  System.out.printf("bars: %f, em*uw: %d, pwidth: %d, clip.width: %d\n",
        //             bars, (int) (emWidth * x_unit_width), 0, clip.width);

        String text = formatTimeMillis(n); // first_bar* data.getFrameSize()+(long)(mouseX*emWidth * x_unit_width));
        //     System.out.printf("Current X: %s - ? %d %d\n", text, mouseX, first_bar);

        // FontMetrics für die aktuelle Schriftart
        FontMetrics fm = g.getFontMetrics();

        // Breite und Höhe des Textes
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight(); // Gesamthöhe (Ascent + Descent + Leading)

        // Optional: Offset für besseren Abstand
        int padding = 4;

        // Rechteck zeichnen
        int x = mouseX - (textWidth + 2 * padding) / 2; // Beispielkoordinaten
        int y = clip.height - (textHeight + 2 * padding);
        g.setColor(Color.WHITE);
        g.fillRect(x, y, textWidth + 2 * padding, textHeight + 2 * padding);
        g.setColor(c);
        g.drawRect(x, y, textWidth + 2 * padding, textHeight + 2 * padding);
        // Text innerhalb des Rechtecks zeichnen
        g.drawString(text, x + padding, y + fm.getAscent() + padding);

        DrawCtx ctx = this.makeDrawCtx(cd);
        if (ctx == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        ctx.g = g2;
        int w = (clip.width - (this.leftYAxisAreaWidth * this.emWidth + this.rightYAxisAreaWidth * this.emWidth));
        ctx.g.translate(this.leftYAxisAreaWidth * this.emWidth + w, h1);
        int chartwin_height = clip.height - this.xAxisAreaHight * emWidth;
        int subchartwin_height = (int) (chartwin_height * cd.height);
        ctx.g.setClip(clip.x, clip.y, this.rightYAxisAreaWidth * this.emWidth, subchartwin_height);

        float val = ctx.getValAtY(mouseY - h1);
        text = String.format("%.2f", val);

        textWidth = fm.stringWidth(text);
        x = clip.x + 20; //mouseX - (textWidth + 2 * padding) / 2; // Beispielkoordinaten
        y = mouseY - h1; //clip.height - (textHeight + 2 * padding);
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, textWidth + 2 * padding, textHeight + 2 * padding);
        g2.setColor(c);
        g2.drawRect(x, y, textWidth + 2 * padding, textHeight + 2 * padding);
        // Text innerhalb des Rechtecks zeichnen
        g2.drawString(text, x + padding, y + fm.getAscent() + padding);
        g2.dispose();

        //    System.out.printf("yval %f\n", val);
    }

    DrawCtx makeDrawCtx(SubChartDef d) {
        if (d == null) {
            return null;
        }
        Rectangle clip = this.getVisibleRect();
        DrawCtx ctx = new DrawCtx();
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
        int chartwin_height = clip.height - this.xAxisAreaHight * emWidth;

        // Caclulate the height of our sub-chart 
        int subchartwin_height = (int) (chartwin_height * d.height);

        
        int pad_top = (int) (this.emWidth * d.pad_top);
        int pad_bot = (int) (this.emWidth * d.pad_bot);

        int pwidth = (int) (emWidth * x_unit_width * (data.size() + 1))
                + clip.width - 100;
        
        ctx.rect = new Rectangle(0, pad_top, pwidth, subchartwin_height - pad_top - pad_bot);
        ctx.c_yscaling = ctx.rect.height / ctx.c_mm.getDiff();

        return ctx;
    }

    void drawAll(Graphics2D g) {

        Rectangle clip = getVisibleRect();

        int chartWidth = (clip.width - (this.leftYAxisAreaWidth * this.emWidth
                + this.rightYAxisAreaWidth * this.emWidth));
        first_bar = (int) (clip.x / (this.x_unit_width * this.emWidth));
        last_bar = 1 + (int) ((clip.x + chartWidth) / (this.x_unit_width * this.emWidth));
        
        int h1 = 0;

        for (SubChartDef d : charts) {
            DrawCtx ctx = new DrawCtx();
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
            int chartwin_height = clip.height - this.xAxisAreaHight * emWidth;

            // Caclulate the height of our sub-chart 
            int subchartwin_height = (int) (chartwin_height * d.height);

            this.ct = d.type;
//            logs = false;
            ctx.c_mm.setLog(d.log);

            ctx = this.makeDrawCtx(d);

            Graphics2D g2 = (Graphics2D) g.create();
            ctx.g = g2;
            g2.translate(this.leftYAxisAreaWidth * this.emWidth, h1);
            g2.setClip(clip.x, clip.y, chartWidth, subchartwin_height);
            //   g2.setClip(0, 0, w, subchartwin_height);
            if (d.bgcolor == Color.WHITE) {
                //System.out.print("White");
            }
            drawMainChart(ctx, d);
            g2.dispose();

            //        ctx.c_yscaling = ctx.rect.height / ctx.c_mm.getDiff();
            g2 = (Graphics2D) g.create();
            ctx.g = g2;
            ctx.g.translate(this.leftYAxisAreaWidth * this.emWidth + chartWidth, h1);
            ctx.g.setClip(clip.x, clip.y, this.rightYAxisAreaWidth * this.emWidth, subchartwin_height);

            this.drawYLegend(ctx, d);
            g2.dispose();

            h1 = h1 + subchartwin_height;

        }

        this.drawCross(g);

    }

    protected void setupSubCharts() {

    }

    //    Dimension gdim;
    private float fontHeight;

    @Override
    public final void paintComponent(Graphics g) {

        if (Globals.sim == null || data == null || data.size() == 0) {
            return;
        }

        super.paintComponent(g);

        this.updateView();

        //  setup some constants
        fontHeight = g.getFontMetrics().getHeight();
        emWidth = g.getFontMetrics().charWidth('M');

        candleWidth = (float) ((x_unit_width * emWidth) * 0.9f);


        this.charts = new ArrayList<>();
        setupSubCharts();

        // draw the "X-legend"
        XLegendDef xld = new XLegendDef();
        drawXLegend((Graphics2D) g, xld);

        drawAll((Graphics2D) g);

    }

    private int lastMaxPos = 0;

    @Override
    public void repaint() {
        updateView();
        super.repaint();
    }

    void updateView() {
        JViewport vp = (JViewport) this.getParent();
        if (vp == null) {
            return;
        }
        Rectangle clip = vp.getViewRect();

        Point pp = vp.getViewPosition();

        JScrollPane scrollPane = (JScrollPane) vp.getParent();

        // 3. Zugriff auf die horizontale JScrollBar
        JScrollBar hsb = scrollPane.getHorizontalScrollBar();

        int maxPos;
        maxPos = this.getPreferredSize().width - vp.getWidth();

        int num_bars = data.size();
        int pwidth = (int) (x_unit_width * (emWidth * (num_bars + 1)) + emWidth * (20 + this.leftYAxisAreaWidth));
        if (pwidth < clip.width) {
            pwidth = clip.width;
        }

        Dimension gdim;
        gdim = this.getParent().getSize();
        this.setPreferredSize(new Dimension(pwidth, gdim.height));

        if (autoScroll) {
            //System.out.printf("LASTMAX: %d, MAX:%d,PPX: %d\n", lastMaxPos, maxPos, pp.x);
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
