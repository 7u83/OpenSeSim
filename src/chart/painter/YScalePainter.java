/*
 * Copyright (c) 2017, 7u83 <7u83@mail.ru>
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
package chart.painter;

import chart.Chart;
import java.awt.Color;
import chart.ChartDef;
import chart.ChartPanel;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import sesim.MinMax;
import sesim.OHLCData;
import sesim.OHLCDataItem;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class YScalePainter extends OHLCChartPainter {

    ChartPanel master;

    public YScalePainter(/*ChartPanel master*/) {
        //    this.master=master;
    }
    
    @Override
    protected MinMax getMinMax(int first_bar,int last_bar){
        return this.getData().getMinMax(first_bar, last_bar);
    }

    @Override
public void drawChart(Graphics2D g, ChartPanel p, ChartDef def) {
    boolean logarithmic=false;
    init(g);
    this.master = def.mainChart;

    OHLCData da = getData();

    //Dimension adim = def.mainChart.getSize();
    Dimension dim = this.cparent.getSize();
    int first_bar = getFirstBar(master);
    int last_bar = first_bar + getBars(master, def);
    MinMax minmax = this.getMinMax(first_bar, last_bar);

    this.initGetY(minmax, dim);
    
    

    int minPixelDist = em_size * 2;
    int c_font_height = g.getFontMetrics().getHeight();

    if (!logarithmic) {
        // ---------- LINEAR ----------
        double y_min,y_max, y_height;
        y_min = minmax.getMin();
        y_max = minmax.getMax();
        
        y_height = (int)(getY(minmax.getMin())  - getY(minmax.getMax()) );
        
        double valueRange = y_max - y_min;
        double roughStep = valueRange / (y_height / (double)minPixelDist);

        // Hauptschritt auf 5er oder 10er runden
        double step = niceStep5or10(roughStep);
        double firstValue = Math.ceil(y_min / step) * step;

        for (double v = firstValue; v <= y_max; v += step) {
            int y = (int)getY((float)v);
            // Haupttick + Zahl
            g.drawLine(0, y, em_size, y);
            g.drawString(String.format("%.2f", (float)v), em_size * 1.5f, y + c_font_height / 3);
            
          /*  if (v>70)
                break;*/

            // Zwischenstriche: 4 Striche zwischen Hauptticks
            int numSubTicks = 4;
            for (int i = 1; i < numSubTicks; i++) {
                double sv = v + i * step / numSubTicks;
                if (sv >= y_min && sv <= y_max) {
                    int sy = (int)getY((float)sv);
                    g.drawLine(0, sy, em_size / 2, sy);
                }
            }

        }
    } else {
        // ---------- LOGARITHMISCH ----------
        int expMin = (int)Math.floor(Math.log10(y_min));
        int expMax = (int)Math.ceil(Math.log10(y_max));

        for (int e = expMin; e <= expMax; e++) {
            double base = Math.pow(10, e);

            // Haupttick 10^e
            if (base >= y_min && base <= y_max) {
                int y = (int)getY((float)base);
                g.drawLine(0, y, em_size, y);
                g.drawString(String.format("1e%d", e), em_size * 1.5f, y + c_font_height / 3);
            }

            // Nur 5·10^e als Zwischenstrich
            double v = 5 * base;
            if (v >= y_min && v <= y_max) {
                int sy = (int)getY((float)v);
                g.drawLine(0, sy, em_size / 2, sy);
            }
        }
    }
}

/**
 * Liefert eine „schöne“ Schrittweite nur 5 oder 10, passend zum roughStep
 */
private double niceStep5or10(double rough) {
    double exp = Math.pow(10, Math.floor(Math.log10(rough)));
    double f = rough / exp;
    if (f <= 5) return 5 * exp;
    else return 10 * exp;
}


    
    
    public void drawChart_oldn(Graphics2D g, ChartPanel p, ChartDef def) {
        init(g);
        this.master = def.mainChart;

        OHLCData da = getData();

        Dimension dim = def.mainChart.getSize();
        int first_bar = getFirstBar(master);
        int last_bar = first_bar + getBars(master, def);
    //    MinMax minmax = this.getData().getMinMax(first_bar, last_bar);
        MinMax minmax = this.getMinMax(first_bar, last_bar);

        this.initGetY(minmax, dim);

        // calculate the number of captionable bars
        int ldist = em_size * 2;
        int steps = y_height / ldist;

        // distance between bars
        float stepsize = (y_max - y_min) / steps;

        // round stepsize to power of 10
        float stepsize10 = (float) Math.pow(10, Math.ceil(Math.log10(stepsize)));

        // build inverse of stepsize
        float stepsize10i = 1 / stepsize10;

        // calculate the first y value
        float firstyv = (float) Math.ceil(y_min * stepsize10i) / stepsize10i;

//        float y1 = getY(minmax.getMin(false));
//        float y2 = getY(minmax.getMax(false));

        int c_font_height = g.getFontMetrics().getHeight();

        float lastyv = firstyv-stepsize10;
        for (float yv = firstyv; yv < minmax.getMax(false); yv += stepsize10) {
            float y;

            float ministep = stepsize10/10.0f;
            
            
            for (float yv10 = lastyv+ministep; yv10 < yv; yv10 += ministep) {
                y = this.getY(yv10);
                Color oc = g.getColor();
                g.setColor(Color.RED);
                g.drawLine(0, (int) y, em_size/2, (int) y);
                g.setColor(oc);
                
            }

            y = this.getY(yv);

            g.drawLine(0, (int) y, em_size, (int) y);
            g.drawString(String.format("%.2f", yv), em_size * 1.5f, y + c_font_height / 3);
            lastyv = yv;
        }
 
    }

    @Override
    void drawItem(Graphics2D g, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
        return;
    }

    /*
    public void drawChart_old(Graphics2D g, ChartPanel p, ChartDef def) {
        init(g);
        this.master = def.mainChart;

        OHLCData da = getData();

        Dimension dim = def.mainChart.getSize();
        int first_bar = getFirstBar(master);
        int last_bar = first_bar + getBars(master, def);
        MinMax minmax = this.getData().getMinMax(first_bar, last_bar);

        this.initGetY(minmax, dim);

        this.getRoundNumber(90);

        //Rectangle dim;
        // dim = p.getSize();
        //      dim = this.clip_bounds;
        // Dimension rv = this.getSize();
//        int yw = (int) (this.yl_width * em_size);
//        g.drawLine(dim.width + dim.x - yw, 0, dim.width + dim.x - yw, dim.height);
        float y1 = getY(minmax.getMin(false));
        float y2 = getY(minmax.getMax(false));
        float ydiff = y1 - y2;
//        System.out.printf("%s y1: %f, y2: %f, diff %f\n", Boolean.toString(c_mm.isLog()), y1, y2, ydiff);
        int c_font_height = g.getFontMetrics().getHeight();

        for (int yp = (int) y2; yp < y1; yp += em_size * 3) {
            g.drawLine(0, yp, em_size, yp);
            double v1 = getValAtY(yp);
            g.drawString(String.format("%.2f", v1), em_size * 1.5f, yp + c_font_height / 3);
        }

        double v1, v2;
//        v1 = ctx.getValAtY(y1);
//        v2 = ctx.getValAtY(y2);

    } */

}
