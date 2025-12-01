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
import sesim.Market.*;

import gui.Globals;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import sesim.util.MinMax;
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
        return this.getVisibleRect().width / 10;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return this.getVisibleRect().width / 2;
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
    protected Color xl_bgcolor = Globals.colors.bg;

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
        /*     if (this.xl_bgcolor != null) {
            g.setColor(xl_bgcolor);
            g.fillRect(clip.x, clip.y, clip.width, (emWidth * this.xAxisAreaHight));
            g.drawRect(clip.y, clip.y, clip.width, (emWidth * this.xAxisAreaHight));
            g.setColor(cur);
        }*/
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

        if (this.xl_bgcolor != null) {

            Color cur = g.getColor();
            g.setColor(xl_bgcolor);
            g.fillRect(clip.x, clip.height - (emWidth * this.xAxisAreaHight)-1,
                    clip.width, (emWidth * this.xAxisAreaHight)+1);
            g.drawRect(clip.x, clip.height - (emWidth * this.xAxisAreaHight)-1,
                    clip.width, (emWidth * this.xAxisAreaHight)+1);
            // g.fillRect(clip.x, clip.y, clip.width, clip.height /*(emWidth * this.xAxisAreaHight)*/);
//            g.drawRect(clip.y, clip.y, clip.width, (emWidth * this.xAxisAreaHight));
            g.setColor(cur);
        }

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
        public int height;

        Rectangle rect;
        //    float scaling;
        //    float min;
        Graphics2D g;
        //   float candleWidth;
        private Graphics2D gyr;
        private int width;
        boolean log;

        float getDiff() {
            if (log) {
                return (float) (Math.log(c_mm.getMax()) - Math.log(c_mm.getMin()));
            }
            return c_mm.getDiff();
        }

        float getY(float y) {

            float ys = rect.height / getDiff();
            if (log) {
                return rect.height + rect.y - ((float) Math.log(y) - (float) Math.log(c_mm.getMin())) * ys;
            }
            return (rect.height - ((y - c_mm.getMin()) * c_yscaling)) + rect.y;
        }

        float getValAtY(float y) {
            //  float val = 0;

            if (log) {
                float ys = rect.height / getDiff();

                return (float) Math.exp((rect.height + rect.y) / ys + (float) Math.log(c_mm.getMin()) - y / ys);

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

    private void drawCandleItem(DrawCtx ctx, SubChartDef d, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {

        //    System.out.printf("Draw ohlc: %f %f %f %f\n", i.getOpen(), i.getHigh(),i.getLow(),i.getClose());
        Graphics2D g = ctx.g;

        if (i.getOpen() < i.getClose()) {
            int xl = (int) (x + candleWidth / 2);

            g.setColor(d.textcolor);
            g.drawLine(xl, (int) ctx.getY(i.getClose()), xl, (int) ctx.getY(i.getHigh()));
            g.drawLine(xl, (int) ctx.getY(i.getLow()), xl, (int) ctx.getY(i.getOpen()));

            float w = candleWidth;
            float h = (int) (ctx.getY(i.getOpen()) - ctx.getY(i.getClose()));

            g.setColor(Color.GREEN);
            g.fillRect((int) (x), (int) ctx.getY(i.getClose()), (int) w, (int) h);
            g.setColor(d.textcolor);
            g.drawRect((int) (x), (int) ctx.getY(i.getClose()), (int) w, (int) h);

        } else {
            int xl = (int) (x + candleWidth / 2);
            g.setColor(d.textcolor);
            g.drawLine(xl, (int) ctx.getY(i.getHigh()), xl, (int) ctx.getY(i.getClose()));
            g.drawLine(xl, (int) ctx.getY(i.getOpen()), xl, (int) ctx.getY(i.getLow()));

            float w = candleWidth;
            float h = (int) (ctx.getY(i.getClose()) - ctx.getY(i.getOpen()));
            g.setColor(Color.RED);
            g.fillRect(x, (int) ctx.getY(i.getOpen()), (int) w, (int) h);
            g.setColor(d.textcolor);
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

    private void drawItem(DrawCtx ctx, SubChartDef d, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        switch (ct) {
            case CANDLESTICK:
                this.drawCandleItem(ctx, d, prevx, x, prev, i);
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

        Graphics2D g = ctx.gyr;
        Rectangle clip = g.getClipBounds();

        // Draw background
        if (d.bgcolor != null) {
            Color cur = ctx.g.getColor();

            g.setColor(d.rightYColor);
//            ctx.g.fillRect(clip.x, clip.y, clip.width, clip.height);
            //     ctx.g.fillRect(0, 0, ctx.rect.width, ctx.rect.height);
            g.fillRect(0, 0, clip.width, clip.height);

            g.setColor(cur);
        }
        g.setColor(d.textcolor);
        // Dimension rv = this.getSize();
        int yw = (this.rightYAxisAreaWidth * this.emWidth);

        // Draw the left border
        /*     g.drawLine(clip.width + clip.x - yw, 0, clip.width
                + clip.x - yw, clip.height);*/
        g.drawLine(0, 0, 0, clip.height); //ctx.rect.x, ctx.rect.height);
        //    ctx.g.fillRect(0, 0, 100, 100);
        // g.drawLine(0,0,clip.width,clip.height);

        float y1 = ctx.getY(ctx.c_mm.getMin());
        float y2 = ctx.getY(ctx.c_mm.getMax());

        // nice first val
        double R = ctx.c_mm.getMax() - ctx.c_mm.getMin();
        int anzahlTicks = (int) (clip.height / (fontHeight * 1.2)); // Beispiel: 5 Hauptunterteilungen
        double S_strich = R / anzahlTicks;

        // Basis P (Potenz von 10)
        double P = Math.pow(10, Math.floor(Math.log10(S_strich)));

        double S_normiert = S_strich / P;
        double S; // Der finale "schöne" Schritt

        if (S_normiert <= 1.0) {
            S = 1.0 * P;
        } else if (S_normiert <= 2.0) {
            S = 2.0 * P;
        } else if (S_normiert <= 5.0) {
            S = 5.0 * P;
        } else {
            S = 10.0 * P;
        }

        double Y_min = Math.floor(ctx.c_mm.getMin() / S) * S;
        double Y_max = Math.ceil(ctx.c_mm.getMax() / S) * S;

        //  if (!ctx.log) {
        {
            int last_y = clip.height;
            for (double label = Y_min; label <= ctx.c_mm.getMax() + S * 0.001; label += S) {
                //System.out.printf("MIN %f\n", P);
                //g.setColor(Color.RED);
                int y = (int) ctx.getY((float) label);
                if (y + fontHeight > clip.height) {
                    continue;
                }
                if (y + fontHeight > last_y) {
                    continue;
                }
                //g.drawLine(0, y, 100, y);
                //g.drawString(String.format("%.2f", label), 0, y + fontHeight / 3);  

                g.drawLine(clip.width + clip.x - yw, y, clip.width + clip.x
                        - yw + emWidth, y);
                //float v1 = ctx.getValAtY(yp);
                g.drawString(d.yformatter.format(label), clip.width + clip.x
                        - yw + emWidth * 1.5f, y + fontHeight / 3);

                last_y = y;
            }

            /*      System.out.printf("MIN %f\n", P);
            g.setColor(Color.RED);
            int y = (int) ctx.getY((float) P);
            g.drawLine(0, y, 100, y);
            g.drawString(String.format("%.2f", P), 0, y + fontHeight / 3);*/
            return;
        }

        /*      for (int yp = (int) y2; yp < y1; yp += emWidth * 5) {
            g.drawLine(clip.width + clip.x - yw, yp, clip.width + clip.x
                    - yw + emWidth, yp);
            float v1 = ctx.getValAtY(yp);
            g.drawString(String.format("%.2f", v1), clip.width + clip.x
                    - yw + emWidth * 1.5f, yp + fontHeight / 3);
        }*/
    }

    void drawMainChart(DrawCtx ctx, SubChartDef d) {

        //  Rectangle clip = getVisibleRect();
        // Fill background
        if (d.bgcolor != null) {
            Color cur = ctx.g.getColor();
            ctx.g.setColor(d.bgcolor);
            //ctx.g.fillRect(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);
            ctx.g.fillRect(0, 0, ctx.rect.width, ctx.height);
            ctx.g.setColor(Color.BLACK);

//            ctx.g.drawLine(ctx.rect.x, ctx.rect.y, ctx.rect.x + ctx.rect.width, ctx.rect.height);
            ctx.g.drawLine(0, 0, ctx.width, 0);

            //g.drawRect(clip_bounds.x, h1, clip.width, subchartwin_height);
            ctx.g.setColor(cur);
        }

        OHLCDataItem prev = null;
        for (int i = first_bar; i < last_bar && i < data.size(); i++) {
            OHLCDataItem di = data.get(i);
            int x = (int) ((i - first_bar) * emWidth * x_unit_width); //em_width;
            this.drawItem(ctx, d, (int) (x - emWidth * x_unit_width), x, prev, di); //, ctx.scaling, data.getMin());
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
        public Color textcolor = null;

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

        public DecimalFormat yformatter;
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


        long n; 

        double bars = clip.width / (emWidth * x_unit_width) * data.getFrameSize();

        n = (long) (bars * (mouseX + 1) / clip.width);
        //  System.out.printf("bars: %f, em*uw: %d, pwidth: %d, clip.width: %d\n",
        //             bars, (int) (emWidth * x_unit_width), 0, clip.width);

        String text = formatTimeMillis(n); // first_bar* data.getFrameSize()+(long)(mouseX*emWidth * x_unit_width));
        //     System.out.printf("Current X: %s - ? %d %d\n", textcolor, mouseX, first_bar);

        // FontMetrics für die aktuelle Schriftart
        FontMetrics fm = g.getFontMetrics();

        // Breite und Höhe des Textes
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight(); // Gesamthöhe (Ascent + Descent + Leading)

        // Optional: Offset für besseren Abstand
        int padding = 4;

        DrawCtx ctx = this.makeDrawCtx(cd, g, h1);
        if (ctx == null) {
            return;
        }

        // Rechteck zeichnen
        int x = mouseX - (textWidth + 2 * padding) / 2;
        if (x < 0) {
            x = 0;
        }
        if (x + (textWidth + 2 * padding) >= ctx.g.getClipBounds().width) {
            x = ctx.g.getClipBounds().width - (textWidth + 2 * padding);
        }

// Beispielkoordinaten
        int y = clip.height - (textHeight + 2 * padding);
        g.setColor(Globals.colors.bgLightYellow);
        g.fillRect(x, y, textWidth + 2 * padding, textHeight + 2 * padding);
        g.setColor(Globals.colors.text);
        g.drawRect(x, y, textWidth + 2 * padding, textHeight + 2 * padding);
        // Text innerhalb des Rechtecks zeichnen
        g.drawString(text, x + padding, y + fm.getAscent() + padding);


        
        // 1. Hole die globale Skalierung/Transformation von g, die von FlatLaF gesetzt wurde
AffineTransform gTransform = g.getTransform();

// 2. Wende diese Skalierung auf die Mauskoordinaten an (nur wenn g transformiert ist)
Point2D mouse = new Point(mouseX, mouseY); // Untransformierte Mauskoordinaten

if (gTransform != null && !gTransform.isIdentity()) {
    // Die Mauskoordinaten in das skalierte Koordinatensystem bringen
    mouse = gTransform.transform(mouse, null);
}
        
        
        
        AffineTransform inverse = null;
        try {
            inverse = ctx.gyr.getTransform().createInverse();
        } catch (Exception e) {
        }



        float val = ctx.getValAtY(mouseY - h1);
        text = String.format("%.2f", val);

        textWidth = fm.stringWidth(text);


     //   Point2D mouse = new Point(mouseX, mouseY);
        Point2D p = inverse.transform(mouse, null);

        // System.out.printf("DrawCrss Point %f, %f\n", p.getX(), p.getY());
        y = (int) p.getY() - (textHeight + 2 * padding) / 2;
        x = this.emWidth * this.rightYAxisAreaWidth - (textWidth + 2 * padding);
        if (y < 0) {
            y = 0;
        }
        if ((y + textHeight + 2 * padding) >= ctx.height) {
            y = ctx.height - (textHeight + 2 * padding) - 1;
        }

        //ctx.gyr.setColor(Color.WHITE);
        ctx.gyr.setColor(Globals.colors.bgLightYellow);
        ctx.gyr.fillRect(x, y, textWidth + 2 * padding, textHeight + 2 * padding);
        ctx.gyr.setColor(Globals.colors.text);
        ctx.gyr.drawRect(x, y, textWidth + 2 * padding, textHeight + 2 * padding);
        // Text innerhalb des Rechtecks zeichnen
        ctx.gyr.drawString(text, x + padding, y + fm.getAscent() + padding);

        
    }

    DrawCtx makeDrawCtx(SubChartDef d, Graphics2D g, int h) {
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

        ctx.log = d.log;

        // Calculate the height for all sub-charts
        // this is the height of out panel minus the height of x-legend
        int chartwin_height = clip.height - this.xAxisAreaHight * emWidth;

        // Caclulate the height of our sub-chart 
        ctx.height = (int) (chartwin_height * d.height);

        int pad_top = (int) (this.emWidth * d.pad_top);
        int pad_bot = (int) (this.emWidth * d.pad_bot);

        //   pad_top = pad_bot = 0;
        //     int pwidth = (int) (emWidth * x_unit_width * (data.size() + 1));
        //+ clip.width - 100;
        int chartWidth = (clip.width - (this.leftYAxisAreaWidth * this.emWidth + this.rightYAxisAreaWidth * this.emWidth));
        ctx.rect = new Rectangle(0, pad_top, chartWidth, ctx.height - pad_top - pad_bot);
        ctx.width = chartWidth;

        ctx.c_yscaling = ctx.rect.height / ctx.c_mm.getDiff();

        Graphics2D g2 = (Graphics2D) g.create();
        ctx.g = g2;

        //  ctx.g.translate(this.leftYAxisAreaWidth * this.emWidth + chartWidth, h);
        ctx.g.translate(this.leftYAxisAreaWidth * this.emWidth + clip.x, h);

        ctx.g.setClip(0, 0, chartWidth, ctx.height);

        g2 = (Graphics2D) g.create();
        ctx.gyr = g2;
 /*       
// 1. Hole die aktuelle (globale) Transformation von g, die Skalierung und Translation enthält
AffineTransform currentTx = g.getTransform();


if (currentTx != null && !currentTx.isIdentity()) {
    // 2. Erzeuge eine NEUE Transformation, die nur die Skalierung (Scale/Shear) beibehält.
    //    Die Translationskomponenten (Translate X/Y) werden auf 0.0 gesetzt.
    AffineTransform cleanTx = new AffineTransform(
        currentTx.getScaleX(), currentTx.getShearY(),
        currentTx.getShearX(), currentTx.getScaleY(),
        0.0, 0.0 
    );
    // 3. Setze diese bereinigte Transformation auf ctx.gyr
    ctx.gyr.setTransform(cleanTx);
} else {
    // 4. Fallback für Standard-LaFs (Nimbus, Metal), bei denen g keine Transformation hat
    ctx.gyr.setTransform(new AffineTransform());
}        
   */     
        
       
        
        ctx.gyr.translate(clip.x + this.leftYAxisAreaWidth * this.emWidth + chartWidth, h);
        //ctx.gyr.setClip(0,0,this.leftYAxisAreaWidth * this.emWidth,100);

        ctx.gyr.setClip(0, 0, rightYAxisAreaWidth * this.emWidth, ctx.height);
        // int chartwin_height = clip.height - this.xAxisAreaHight * emWidth;
        //  int subchartwin_height = (int) (chartwin_height * cd.height);
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
                //   System.out.printf("Data is null\n");
                //System.exit(0);
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
            //         int chartwin_height = clip.height - this.xAxisAreaHight * emWidth;
            // Caclulate the height of our sub-chart 
            this.ct = d.type;
//            logs = false;
            //     ctx.c_mm.setLog(d.log);

            ctx = this.makeDrawCtx(d, g, h1);
            //        int subchartwin_height = (int) (chartwin_height * d.height);

            //     Graphics2D g2 = (Graphics2D) g.create();
            //       ctx.g = g2;
            //     g2.translate(this.leftYAxisAreaWidth * this.emWidth, h1);
            //       ctx.g.setClip(clip.x, clip.y, chartWidth, subchartwin_height);
            //   g2.setClip(0, 0, w, subchartwin_height);
            if (d.bgcolor == Color.WHITE) {
                //System.out.print("White");
            }
            drawMainChart(ctx, d);
            //      ctx.g.drawLine(0, 0, 60, 60);
            ctx.g.dispose();

            //        ctx.c_yscaling = ctx.rect.height / ctx.c_mm.getDiff();
            Graphics2D g2;
            g2 = (Graphics2D) g.create();
//            ctx.g = g2;
            //          ctx.g.translate(clip.x + this.leftYAxisAreaWidth * this.emWidth + chartWidth, h1);
            //   ctx.g.setClip(clip.x, clip.y, this.rightYAxisAreaWidth * this.emWidth, subchartwin_height);
            // System.out.printf("Draw %d\n", h1);
            this.drawYLegend(ctx, d);

            //   g2.drawLine(0, 0, 60, 60);
            //    g2.dispose();
            h1 = h1 + ctx.height;

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

        // let set the subcharts setup by overridden method
        this.charts = new ArrayList<>();
        setupSubCharts();

        // Draw the "X-legend"
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

//    @Override
    public void aUpdateQuote(Quote q) {
        return;
        /*SwingUtilities.invokeLater(() -> {
    updateView();
    this.revalidate();
    this.repaint();
});*/
    }

    ExecutorService executor = Executors.newSingleThreadExecutor();
    boolean busy = false;
    boolean update = false;

    @Override
    public void UpdateQuote(Quote q) {

        if (busy) {
            update = true;
            return;
        }
        busy = true;
        update = true;

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    while (update) {
                        update = false;
                        SwingUtilities.invokeLater(() -> {
                            updateView();
                            revalidate();
                            repaint();
                        });

                        try {
                            Thread.sleep(20);   // update rate is limited 50 Hz
                        } catch (InterruptedException e) {
                        }
                    }

                } finally {
                    busy = false;
                }
            }
        });

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
