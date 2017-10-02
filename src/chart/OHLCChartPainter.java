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
package chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import sesim.MinMax;
import sesim.OHLCData;
import sesim.OHLCDataItem;
import static sun.awt.geom.Curve.prev;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class OHLCChartPainter extends ChartPainter {

    private float iwidth;
    private MinMax mm;
    private Dimension dim;
    private float y_scaling;


    float getY(float y) {
//c_yscaling = ctx.rect.height / c_mm.getDiff();
//               float ys = dim.height / mm.getDiff();
        if (mm.isLog()) {
//            return rect.height + rect.y - ((float) Math.log(y) - c_mm.getMin()) * ys;
        }
        return (dim.height - ((y - mm.getMin()) * y_scaling)) ;
        
        
    }

    private void drawCandleItem(Graphics2D g, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {

        if (i.open < i.close) {
            int xl = (int) (x + iwidth / 2);

            g.setColor(Color.BLACK);
            g.drawLine(xl, (int) getY(i.close), xl, (int) getY(i.high));
            g.drawLine(xl, (int) getY(i.low), xl, (int) getY(i.open));

            float w = iwidth;
            float h = (int) (getY(i.open) - getY(i.close));

            g.setColor(Color.GREEN);
            g.fillRect((int) (x), (int) getY(i.close), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) getY(i.close), (int) w, (int) h);

        } else {
            int xl = (int) (x + iwidth / 2);
            g.setColor(Color.RED);
            g.drawLine(xl, (int) getY(i.high), xl, (int) getY(i.close));
            g.drawLine(xl, (int) getY(i.open), xl, (int) getY(i.low));

            float w = iwidth;
            float h = (int) (getY(i.close) - getY(i.open));

            g.fillRect((int) (x), (int) getY(i.open), (int) w, (int) h);
            g.setColor(Color.BLACK);
            g.drawRect((int) (x), (int) getY(i.open), (int) w, (int) h);

        }
         
    }

    @Override
    public void drawChart(Graphics2D g, JScrollBar sb, OHLCData data, JPanel p, ChartDef def) {
        init(g);
        
        iwidth = (float) ((def.x_unit_width * em_width) * 0.9f);
        
        int first_bar = def.x_scrollbar.getValue();
        dim = p.getSize();
        int bars = (int) (dim.width / (def.x_unit_width * em_width));
        
        int last_bar = first_bar+bars;
        
        mm = data.getMinMax(first_bar, first_bar+bars);
        
        y_scaling = dim.height / mm.getDiff();

        OHLCDataItem prevd=null;
        int prevx;
        
        if (data.size()>0 && first_bar<data.size()){
            prevd = data.get(first_bar);
        }
        
        for (int b = first_bar,n=0; b < last_bar && b<data.size(); b++,n++) {
            OHLCDataItem d = data.get(b);
            
            int x = (int) (n * em_width * def.x_unit_width); //em_width;
            this.drawCandleItem(g, (int) (x - em_width * def.x_unit_width), x, prevd, d); 

            
            //this.drawCandleItem(g, (int)((n-1)*def.x_unit_width*em_width), (int)(n*def.x_unit_width*em_width), prevd, d);
            

        }
    }

}
