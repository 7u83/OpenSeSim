/*
 * Copyright (c) 2016, 7u83 <7u83@mail.ru>
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
package gui;

import sesim.Quote;
import java.awt.Color;
import javax.swing.SwingUtilities;
import java.util.*;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class QuotePanel extends javax.swing.JPanel implements sesim.Exchange.QuoteReceiver{

    /**
     * Creates new form QuotePanel
     */
    public QuotePanel() {
        initComponents();
        if (Globals.se==null)
            return;
        Globals.se.addQuoteReceiver(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel2 = new javax.swing.JLabel();
        lastPrice = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setBorder(null);
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWidths = new int[] {0, 5, 0, 5, 0};
        layout.rowHeights = new int[] {0};
        setLayout(layout);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(jLabel2, gridBagConstraints);

        lastPrice.setFont(lastPrice.getFont().deriveFont(lastPrice.getFont().getStyle() | java.awt.Font.BOLD, lastPrice.getFont().getSize()+4));
        lastPrice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lastPrice.setText("0.00");
        lastPrice.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        add(lastPrice, gridBagConstraints);

        jLabel3.setPreferredSize(new java.awt.Dimension(30, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        add(jLabel3, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lastPrice;
    // End of variables declaration//GEN-END:variables

    @Override
    public void UpdateQuote(Quote q) {
        class Updater implements Runnable {
            QuotePanel qp;
            String text="";
            Color color = Color.BLUE;
            

            @Override
            public void run() {
                qp.lastPrice.setText(text);
                qp.lastPrice.setForeground(color);
            }
            
            
        }
        Updater  u= new Updater();
        u.qp=this;
        
        if (q.price==q.bid){
            u.color=new Color(172,0,0);
        }
        if (q.price==q.ask){
            u.color=new Color(0,120,0); //.; //new Color(30,0,0);
        }
               
        
                
        
        u.text = String.format("%.8f\n(%.0f)", q.price,q.volume);
        
        SwingUtilities.invokeLater(u);
        
//        SortedSet s = MainWin.se.getQuoteHistory(5);
        
 //       System.out.print(
 //               "SortedSet size:"
 //               +s.size()
 //               +"\n"
 //       );
        
        //this.lastPrice.setText(lp);
    }
}
