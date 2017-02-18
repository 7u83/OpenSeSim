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

    class ChartDef {

    }

    protected int em_size = 1;
    protected double x_unit_width = 1.0;

    protected float bar_width = 0.9f * 2.0f;

    public void setBarWidth(float bw) {
        bar_width = bw;
    }

    //protected float bar_width_em = 1;
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
                System.out.printf("Equality to %s\n", cmd);
                this.setCompression(this.ctxMenuCompressionValues[i]);
            }
        }
        System.out.printf("ACtion %s\n", cmd);
        //this.setCompression(1000 * 5);
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

        //  double unit_width = 1.5;
        int big_tick = 8;
        long start;

        XLegendDef() {

        }

        String getAt(int unit) {

            int fs = data.getFrameSize();
            return Scheduler.formatTimeMillis(0 + unit * fs);

        }

    }

    void drawOHLC(Graphics2D g, int x, OHLCDataItem di) {

    }

    /**
     * Draw the one and only one X legend
     *
     * @param g Graphics conext to draw
     * @param xld Definition
     */
    void drawXLegend(Graphics2D g, XLegendDef xld) {

        g = (Graphics2D) g.create();

        Dimension dim = getSize();
        int y = dim.height - em_height * 3;

        g.drawLine(0, y, dim.width, y);

        int n;
        double x;

        for (n = 0, x = 0; x < dim.width; x += em_size * x_unit_width) {

            if (n % xld.big_tick == 0) {
                g.drawLine((int) x, y, (int) x, y + em_size);
            } else {
                g.drawLine((int) x, y, (int) x, y + em_size / 2);
            }

            if (n % xld.big_tick == 0) {
                String text;
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

    boolean logs = false;

    float getY(float y) {

        if (logs) {

            float m = c_mm.max / c_mm.min;

            //System.out.printf("Min: %f  Max: %f M: %f\n",c_mm.min,c_mm.max,m);
            //float fac = (float) c_rect.height /(float) Math.log(c_mm.max * c_yscaling);
            float fac = (float) c_rect.height / (float) Math.log(m);

            float fmin = c_rect.height - ((float) Math.log((y / c_mm.min)) * fac);

            //System.out.printf("Fac: %f fmin: %f\n", fac, fmin);
            return fmin;

            //return c_rect.height - ((float) Math.log((y - c_mm.min) * c_yscaling) * fac);
        }

        return (c_rect.height - ((y - c_mm.min) * c_yscaling)) + c_rect.y;

//        return c_rect.height - ((y - c_mm.min) * c_yscaling);
    }

    private void drawItem_l(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem item) {

        if (prev == null) {
            prev = item;
        }
        Graphics2D g = ctx.g;

        Rectangle r = ctx.rect;
        g.drawLine(prevx, (int) ctx.getYc(prev.close), x, (int) ctx.getYc(item.close));
        g.drawLine(r.x, r.height + r.y, r.x + r.width, r.height + r.y);
    }

    private void drawCandleItem(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {

        if (prev == null) {
            prev = i;
        }
        Graphics2D g = ctx.g;

        Rectangle r = ctx.rect;

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

    }

    private void drawBarItem(RenderCtx ctx, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        Graphics2D g = ctx.g;

        
        g.drawLine(x, (int) ctx.getYc(0), x, (int) ctx.getYc(i.volume));

        Rectangle r = ctx.rect;
    }

    enum ChartType {
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

        Graphics2D g=ctx.g;
        
        //   g.setClip(null);
        System.out.printf("Drawing legend\n");
        //Dimension dim0 = this.getSize();
        Rectangle dim ;
        dim = this.clip_bounds;

        Dimension rv = this.getSize();
        System.out.printf("W: %d,%d\n", rv.width, rv.height);

        int yw = (int) (this.y_legend_width * this.em_size);

        g.setColor(Color.BLUE);
        g.drawLine(dim.width + dim.x - yw, 0, dim.width + dim.x - yw, dim.height);
        g.setColor(Color.YELLOW);
        System.out.printf("Dim: %d %d %d %d\n", dim.x, dim.y, dim.width, dim.height);

        c_yscaling = c_rect.height / c_mm.getDiff();

//        System.out.printf("yscale %f\n", c_yscaling);
        for (float y = c_mm.min; y < c_mm.max; y += c_mm.getDiff() / 10.0) {

            int my = (int) getY(y); //c_rect.height - (int) ((y - c_mm.min) * c_yscaling);

            g.drawLine(dim.width + dim.x - yw, my, dim.width + dim.x - yw + em_size, my);

            g.drawString(String.format("%.2f", y), dim.width + dim.x - yw + em_size * 1.5f, my + c_font_height / 3);
        }

    }

    private MinMax c_mm = null;
    private Rectangle c_rect;

    private int em_height;
    private int em_width;

    void drawChart(RenderCtx ctx) {

        c_yscaling = c_rect.height / c_mm.getDiff();

        ctx.g.setClip(null);
        ctx.g.setColor(Color.ORANGE);
     //     ctx.g.setClip(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);
      //   ctx.g.drawRect(ctx.rect.x, ctx.rect.y, ctx.rect.width, ctx.rect.height);     
        this.drawYLegend(ctx);

        ctx.g.setColor(Color.ORANGE);
        int yw=(int) (this.y_legend_width * this.em_size);
        
System.out.printf("YYYYYYYYYYYYYYYYYYYYw %d",yw) ;       
        ctx.g.setClip(clip_bounds.x, clip_bounds.y, clip_bounds.width-yw, clip_bounds.height);
        //       ctx.g.setClip(ctx.rect.x, ctx.rect.y, ctx.rect.width-yw, ctx.rect.height);

        OHLCDataItem prev = null;
        for (int i = first_bar; i < last_bar && i < data.size(); i++) {
            OHLCDataItem di = data.get(i);
            int x = (int) (i * em_size * x_unit_width); //em_width;
            this.drawItem(ctx, x - em_width, x, prev, di); //, ctx.scaling, data.getMin());

            //    myi++;
            prev = di;

        }

    }

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

        c_mm.min /= 1; //-= c_mm.min/ 2.0f;
        c_mm.max *= 1; //+= c_mm.max / 10.0f;

        em_height = g.getFontMetrics().getHeight();
        em_width = g.getFontMetrics().stringWidth("M");

        XLegendDef xld = new XLegendDef();
        this.drawXLegend(g, xld);

        int pwidth = (int) (em_width*x_unit_width * num_bars);
        int phight = 400;
        //   phight=this.getVisibleRect().height;

        this.setPreferredSize(new Dimension(pwidth, gdim.height));
        this.revalidate();
        

        int bww = (int) (data.size()*(this.x_unit_width * this.em_size));
        int p0 = bww-clip_bounds.width;
        if (p0<0)
            p0=0;
        JViewport vp=(JViewport) this.getParent();
  //      vp.setViewPosition(new Point(pwidth-400,0));
        
        
        

        Rectangle r = new Rectangle(0, 0, pwidth, gdim.height - 16 * em_width);
        c_rect = r;

        //       Dimension gdim = this.getSize();
        //    Iterator<OHLCDataItem> it = data.iterator();
        OHLCDataItem prev = null;
        //  int myi = 0;

        RenderCtx ctx = new RenderCtx();

        c_rect.x = 0;
        c_rect.y = 50;
        c_rect.height = 100;
        ctx.rect = c_rect;
        ctx.scaling = (float) r.height / (c_mm.getMax() - c_mm.getMin());
        ctx.min = c_mm.getMin();
        ctx.g = g;
        ctx.iwidth = (float) ((x_unit_width * em_size) * 0.9f);

        this.ct = ChartType.CANDLESTICK;
        drawChart(ctx);

        c_mm = data.getVolMinMax(first_bar, last_bar);

        System.out.printf("Volminmax: %f %f\n", c_mm.min, c_mm.max);
        c_mm.min = 0f;

        c_rect.x = 0;
        c_rect.y = 250;
        c_rect.height = 50;
        ctx.rect = c_rect;
        ctx.scaling = (float) r.height / (c_mm.getMax() - c_mm.getMin());
        ctx.min = c_mm.getMin();
        ctx.g = g;
        ctx.iwidth = (float) ((x_unit_width * em_size) * 0.9f);

        this.ct = ChartType.VOL;
        drawChart(ctx);
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

    private float c_font_height;

    @Override
    public final void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Calculate the number of pixels for 1 em
        em_size = g.getFontMetrics().stringWidth("M");

        this.gdim = this.getParent().getSize(gdim);
        this.getParent().setPreferredSize(gdim);

        //Object o = this.getParent();
        JViewport vp = (JViewport) this.getParent();

        //this.clip_bounds=g.getClipBounds();
        this.clip_bounds = vp.getViewRect();
        
//        vp.setViewPosition(new Point(0,0));

//        System.out.printf("X:%d %d\n",gdim.width,gdim.height);
        first_bar = (int) (clip_bounds.x / (this.x_unit_width * this.em_size));
        last_bar = 1 + (int) ((clip_bounds.x + clip_bounds.width - (this.y_legend_width * em_size)) / (this.x_unit_width * this.em_size));

        
//        int vpwidth=(int) ((last_bar-first_bar)*x_unit_width*em_size);
        num_bars=data.size() + (int) (clip_bounds.width / (this.x_unit_width * this.em_size))+5;
        
        
        c_font_height = g.getFontMetrics().getHeight();
        


        System.out.printf("First %d, last %d\n", first_bar, last_bar);

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

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");
        ctxMenu.add(jCheckBoxMenuItem1);

        setBackground(java.awt.Color.white);
        setBorder(null);
        setDoubleBuffered(false);
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(300, 300));
        setRequestFocusEnabled(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
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
        System.out.printf("Mouse ohlc was pressed\n");
        if (!evt.isPopupTrigger()) {
            return;
        };

        //    this.invalidate();
        this.ctxMenu.setVisible(true);
        this.ctxMenu.show(this, evt.getX(), evt.getY());

        this.invalidate();
        this.repaint();


    }//GEN-LAST:event_formMousePressed

    void setCompression(int timeFrame) {
        data = Globals.se.getOHLCdata(timeFrame);
        invalidate();
        repaint();
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
