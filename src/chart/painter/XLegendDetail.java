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

import java.awt.Graphics2D;
import java.awt.Point;
import sesim.ChartDef;
import sesim.ChartPanel;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class XLegendDetail extends XLegendPainter{
    public XLegendDetail(){
        
    }
    
    static int ctr=0;

    @Override
    public void drawChart(Graphics2D g, ChartPanel p, ChartDef def) {
     //   System.out.printf("Xlegend Deatil drawchart called %d\n",ctr);
     //   ctr++;
        
        init (g);
        if (def==null)
            return;
        if (def.mainChart==null)
            return;
        if (def.mainChart.mouse == null)
                return;
        
        Point mouse = def.mainChart.mouse;
        
        int  x = def.mainChart.mouse.x;
        
        this.x2Time(p, def, x);
        
                g.drawLine(mouse.x, 0, mouse.x, p.getSize().height); 
        
        System.out.printf("Detail Mous X: %d\n",x);
//        super.drawChart(g, p, def); //To change body of generated methods, choose Tools | Templates.
    }

    
}
