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
import chart.ChartDef;
import chart.ChartPanel;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class YLegendPainter extends ChartPainter {
    
    ChartPanel master;
    
    public YLegendPainter (ChartPanel master){
        this.master=master;
    }

    @Override
    public void drawChart(Graphics2D g, ChartPanel p, ChartDef def) {
        init(g);
        
        Dimension dim = master.getSize();
        int first_bar = getFirstBar(master);
        int bars = getBars(master,def);

        //Rectangle dim;
       // dim = p.getSize();
  //      dim = this.clip_bounds;

        // Dimension rv = this.getSize();
//        int yw = (int) (this.yl_width * em_size);

//        g.drawLine(dim.width + dim.x - yw, 0, dim.width + dim.x - yw, dim.height);

/*
        float y1 = ctx.getY(mm.getMin(false));
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
*/

    }
        

    
}
