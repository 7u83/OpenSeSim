/*
 * Copyright (c) 2025, tube
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import sesim.MinMax;
import sesim.OHLCDataItem;

/**
 *
 * @author tube
 */
public class VolChartPainter extends OHLCChartPainter {

    @Override
    protected MinMax getMinMax(int first_bar, int last_bar) {
        MinMax m = data.getVolMinMax(first_bar, last_bar);
        m.setMin(0);
        m.setMax(m.getMax()+m.getMax()/10);
        return m;
    }

    @Override
    void drawItem(Graphics2D g, int prevx, int x, OHLCDataItem prev, OHLCDataItem i) {
       
        g.setColor(Color.GRAY);

        g.drawLine(x, (int) getY(0), x, (int) (getY(i.volume)));
        
    //   g.fillRect(x, (int) getY(0), 10, (int) (getY(i.volume)));
   
        g.fillRect(x,(int) (getY(i.volume)), (int)this.iwidth, (int) getY(0)-(int) (getY(i.volume)));
        
  //g.fillR
   
    //    g.fillRect(0, 0, 100, 20);
        
      //  System.out.printf("THE VOLUME Y0:%d Y:%d\n", (int)getY(0),(int) (getY(i.volume)));
        System.out.printf("THE VOLUME:%f \n", i.volume);
        


   //     Rectangle r = ctx.rect;
    //    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
