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
import java.awt.Dimension;
import java.awt.Graphics2D;
import sesim.ChartPainterInterface;
import sesim.MinMax;
import sesim.OHLCData;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
abstract public class ChartPainter implements ChartPainterInterface{

    protected int em_size;

    
    protected OHLCData data;
    
    public void setOHLCData(OHLCData data){
        this.data = data;
    }
    
/*    public abstract interface DataProvider {

        abstract OHLCData get();
    }

    DataProvider dataProvider = null;

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
*/
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

    protected int getBars(ChartPanel p, ChartDef def) {
        Dimension dim = p.getSize();
        return (int) (dim.width / (def.x_unit_width * em_size));
    }

    /**
     * Init method scould be called before painting the chart
     *
     * @param g Graphics context
     */
    protected final void init(Graphics2D g) {

        // Calculate the number of pixels for 1 em
        em_size = g.getFontMetrics().stringWidth("M");

    }

    protected float y_scaling;
    protected int y_height;
    protected float y_min;

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
        return (-(y -  y_height)) / y_scaling + y_min;

    }

    void initGetY(MinMax minmax, Dimension dim) {
        y_height = dim.height;
        y_scaling = dim.height / minmax.getDiff();
        y_min = minmax.getMin();
    }

    abstract public void drawChart(Graphics2D g, ChartPanel p, ChartDef def);

}
