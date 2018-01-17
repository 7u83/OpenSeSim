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

import sesim.ChartDef;
import sesim.ChartPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import sesim.MinMax;
import sesim.OHLCData;
import sesim.OHLCDataItem;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public abstract class OHLCChartPainter extends ChartPainter {

    protected float iwidth;

    abstract void drawItem(Graphics2D g, int prevx, int x, OHLCDataItem prev, OHLCDataItem i);

    protected OHLCData data;

    public void setOHLCData(OHLCData data) {
        this.data = data;
    }

    protected OHLCData getData() {
        return this.data;
        /*if (dataProvider == null) {
            return null;
        }
        return dataProvider.get();
         */
    }

    protected int getFirstBar(ChartPanel p) {
        if (p.x_scrollbar != null) {
            return p.x_scrollbar.getValue();
        }
        return 0;
    }

    protected long x2Time(ChartPanel p, ChartDef def, int x) {
     //   int first_bar = getFirstBar(p);
     //   OHLCDataItem d = data.get(first_bar);
       
        long t = 0;
     
        double xw = def.x_unit_width*em_size;
        
        int fs = data.getFrameSize();
        
        int xbar = (int)((float)x/(xw));
        int xrest = (int)((float)x%(xw));
        
        
    //    System.out.printf("XBAR: %d  %f\n",xbar,def.x_unit_width);
        
        return xbar*fs + fs/(int)xw*xrest;
    }

    /**
     * Get the number of bars needed to fill the ChartPanel object
     * @param p ChartPanel object
     * @param def ChartDef
     * @return Number of bars
     */
    protected int getBars(ChartPanel p, ChartDef def) {
        Dimension dim = p.getSize();
        return (int) (dim.width / (def.x_unit_width * em_size));
    }

    protected float y_scaling;
    protected int y_height;
    protected float y_min;
    protected float y_max;

 

       protected float getRoundNumber(float n){
        

        
        int ldist = this.em_size*2;
        int steps = y_height/ldist;
        
        System.out.printf("Yheight: %d \n",y_height);
        
        System.out.printf("Steps = %d, h: %d\n", steps, this.y_height);
        
        float stepsize = (y_max - y_min) / steps; 
        
        
     
  //      stepsize = 2;
      
     //  double minl10 = Math.log10(y_min);
 //      double maxl10 = Math.log10(y_max);
       
        double  lo = Math.ceil(Math.log10(stepsize));
        double rss = Math.pow(10, lo);
        
            
        double st1 = 1/rss;
      
        
        
        double dr = Math.ceil(y_min*st1)/st1;

   
   
        
       // double f = y_min 
                
       System.out.printf("Ste size %f %f %f %f %f\n",stepsize,lo, y_min, rss, dr);
      
      
    
       
        return (float)0.0;
            
    }

    void initGetY(MinMax minmax, Dimension dim) {
        y_height = dim.height;
        y_scaling = dim.height / minmax.getDiff();
        y_min = minmax.getMin();
        y_max = minmax.getMax();

    }
   
    
    
    float getY(float y) {
//c_yscaling = ctx.rect.height / c_mm.getDiff();
//               float ys = dim.height / mm.getDiff();
        /*  if (minmax.isLog()) {
//            return rect.height + rect.y - ((float) Math.log(y) - c_mm.getMin()) * ys;
        }
         */
//        return (dim.height - ((y - minmax.getMin()) * y_scaling));
        return (y_height - ((y - y_min) * y_scaling));

    }

    double getValAtY(float y) {
        float val = 0;

        /*            if (c_mm.isLog()) {
                float ys = rect.height / c_mm.getDiff();

                return Math.exp((rect.height + rect.y) / ys + c_mm.getMin() - y / ys);

            }
         */
        return (-(y - y_height)) / y_scaling + y_min;

    }
  
  /*  void initGetY(MinMax minmax, Dimension dim) {
        y_height = dim.height;
        y_scaling = dim.height / minmax.getDiff();
        y_min = minmax.getMin();
    }
*/
    
    @Override
    public void drawChart(Graphics2D g, ChartPanel p, ChartDef def) {
        OHLCData data = getData();
        if (data == null) {
            return;
        }

        init(g);

        iwidth = (float) ((def.x_unit_width * em_size) * 0.9f);

        int first_bar = getFirstBar(p);

        Dimension dim = p.getSize();
        //int bars = (int) (dim.width / (def.x_unit_width * em_size));
        int bars = this.getBars(p, def);

        int last_bar = first_bar + bars + 1;

        MinMax minmax = data.getMinMax(first_bar, last_bar);

        this.initGetY(minmax, dim);

        // y_scaling = dim.height / minmax.getDiff();
        OHLCDataItem prevd = null;
        int prevx;

        if (data.size() > 0 && first_bar < data.size()) {
            prevd = data.get(first_bar);
        }

        for (int b = first_bar, n = 0; b < last_bar && b < data.size(); b++, n++) {
            OHLCDataItem d = data.get(b);

            int x = (int) (n * em_size * def.x_unit_width); //em_width;
            drawItem(g, (int) (x - em_size * def.x_unit_width), x, prevd, d);
            prevd = d;

            //this.drawCandleItem(g, (int)((n-1)*def.x_unit_width*em_size), (int)(n*def.x_unit_width*em_size), prevd, d);
        }
    }

}
