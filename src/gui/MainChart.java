/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import chart.Chart;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import sesim.Exchange;
import sesim.OHLCData;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class MainChart extends chart.Chart {
    
    ButtonGroup typeGroup=new ButtonGroup();

    /**
     * Creates new form MainChart
     */
    public MainChart() {
        System.out.printf("This is the main chart constructor\n");

        initComponents();

        initCtxMenu();

        setCompression();
        
        this.candleTypeMEnuItem.setSelected(true);

      //xl_bgcolor = Color.ORANGE;
      //  xl_color = Color.RED;
        //this.xl_height = 3;

    }

    //OHLCData data;
    @Override
    protected void setupSubCharts() {
        //  charts = new ArrayList<>();
        Chart.SubChartDef main = new Chart.SubChartDef();
        main.height = 0.8f;
        main.type = this.chart_type;
        main.data = data;
        main.bgcolor = Color.WHITE;
        main.paddingTop = 0.02f;
        //main.rightYData(data);
        main.log = logMenu.isSelected();
        main.rightYData=data;
        main.rightYColor=Color.WHITE;
        
        addChart(main);

        Chart.SubChartDef vol = new Chart.SubChartDef();
        vol.height = 0.2f;
        vol.paddingTop = 0.08f;
       vol.type = ChartType.VOL;
        vol.data = data;
        vol.bgcolor = Color.WHITE;
           vol.rightYData=data;
    vol.rightYColor=Color.WHITE;

       addChart(vol);
    }

    private void showCtxMenu(java.awt.event.MouseEvent evt) {
        //    this.invalidate();
        this.ctxMenu.setVisible(true);
        this.ctxMenu.show(this, evt.getX(), evt.getY());

        invalidate();
        repaint();
    }
    private final String[] ctxMenuCompressionText = {
        "5 s", "10 s", "15 s", "30 s",
        "1 m", "2 m", "5 m", "10 m", "15 m", "30 m",
        "1 h", "2 h", "4 h",
        "1 d", "2 d"
    };
    private final Integer[] ctxMenuCompressionValues = {
        5 * 1000, 10 * 1000, 15 * 1000, 30 * 1000,
        60 * 1000, 2 * 60 * 1000, 5 * 60 * 1000, 10 * 60 * 1000, 15 * 60 * 1000, 30 * 60 * 1000,
        1 * 3600 * 1000, 2 * 3600 * 1000, 4 * 3600 * 1000,
        1 * 24 * 3600 * 1000, 2 * 24 * 3600 * 1000
    };

    private final Integer default_cmopression = 60 * 60 * 1000;

    private void initCtxMenu() {
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < this.ctxMenuCompressionValues.length; i++) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(this.ctxMenuCompressionText[i]);

            group.add(item);
            if (Objects.equals(this.ctxMenuCompressionValues[i], this.default_cmopression)) {
                item.setSelected(true);
            }

            item.addActionListener((java.awt.event.ActionEvent evt) -> {
                ctxMenuCompActionPerformed(evt);
            });
            this.compMenu.add(item);
        }
    }

    protected final void setCompression0(int timeFrame) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            data = Globals.se.getOHLCdata(timeFrame);
            invalidate();
            repaint();
        });

    }

    void setCompression() {
        for (int i = 0; i < this.ctxMenuCompressionText.length; i++) {
            JRadioButtonMenuItem item = (JRadioButtonMenuItem) compMenu.getItem(i);
            if (item.isSelected()) {
                setCompression0(this.ctxMenuCompressionValues[i]);
            }
        }
    }
    
    private ChartType chart_type=ChartType.CANDLESTICK;

/*
    void setType(){
        for (int i = 0; i < this.typeMenu.getItemCount(); i++) {
            JRadioButtonMenuItem item = (JRadioButtonMenuItem) compMenu.getItem(i);
            if (item.isSelected()) {
                if ("LINE".equals(item.getActionCommand())){
                    chart_type=ChartType.LINE;
                }
                if ("CNADLESTICK".equals(item.getActionCommand())){
                    chart_type=ChartType.CANDLESTICK;
                }
                        
                
            }
        } 
        doRedraw();
    }
    */

   // boolean log = false;

    protected void doRedraw() {
    //    log = this.logMenu.isSelected();
        javax.swing.SwingUtilities.invokeLater(() -> {
            invalidate();
            repaint();
        });
        
    }

    public void initChart() {
        setCompression();
        doRedraw();
        
    }

    private void ctxMenuCompActionPerformed(java.awt.event.ActionEvent evt) {
        setCompression();
        String cmd = evt.getActionCommand();
        for (int i = 0; i < this.ctxMenuCompressionText.length; i++) {
            if (this.ctxMenuCompressionText[i].equals(cmd)) {

                setCompression0(this.ctxMenuCompressionValues[i]);
            }
        }

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
        typeMenu = new javax.swing.JMenu();
        lineTypeItem = new javax.swing.JRadioButtonMenuItem();
        candleTypeMEnuItem = new javax.swing.JRadioButtonMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        logMenu = new javax.swing.JCheckBoxMenuItem();
        typeButtonGroup = new javax.swing.ButtonGroup();

        compMenu.setText("Compression");
        ctxMenu.add(compMenu);

        typeMenu.setText("Chart Type");

        typeButtonGroup.add(lineTypeItem);
        lineTypeItem.setMnemonic('l');
        lineTypeItem.setText("Line");
        lineTypeItem.setActionCommand("LINE");
        lineTypeItem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lineTypeItemItemStateChanged(evt);
            }
        });
        lineTypeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineTypeItemActionPerformed(evt);
            }
        });
        typeMenu.add(lineTypeItem);

        typeButtonGroup.add(candleTypeMEnuItem);
        candleTypeMEnuItem.setMnemonic('c');
        candleTypeMEnuItem.setText("Candle Stick");
        candleTypeMEnuItem.setActionCommand("CNADLESTICK");
        candleTypeMEnuItem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                candleTypeMEnuItemItemStateChanged(evt);
            }
        });
        typeMenu.add(candleTypeMEnuItem);

        ctxMenu.add(typeMenu);
        ctxMenu.add(jSeparator1);

        logMenu.setMnemonic('l');
        logMenu.setText("Log Scale");
        logMenu.setToolTipText("");
        logMenu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                logMenuItemStateChanged(evt);
            }
        });
        ctxMenu.add(logMenu);

        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
        double n = evt.getPreciseWheelRotation() * (-1.0);
        double u = this.getXUnitWidth();
        
        if (n < 0) {
            if (u > 0.3) {
                this.setXUnitWidth(u + 0.1 * n);
            }
        } else {
            //this.x_unit_width += 0.1 * n;
            this.setXUnitWidth(u + 0.1 * n);
        }

        this.invalidate();
        this.repaint();
    }//GEN-LAST:event_formMouseWheelMoved

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        if (!evt.isPopupTrigger()) {
            return;
        }
        showCtxMenu(evt);
    }//GEN-LAST:event_formMouseReleased

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        if (!evt.isPopupTrigger()) {
            return;
        }
        showCtxMenu(evt);
    }//GEN-LAST:event_formMousePressed

    private void logMenuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_logMenuItemStateChanged
        doRedraw();
    }//GEN-LAST:event_logMenuItemStateChanged

    private void lineTypeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineTypeItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lineTypeItemActionPerformed

    private void candleTypeMEnuItemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_candleTypeMEnuItemItemStateChanged
       if (this.candleTypeMEnuItem.isSelected()){
            this.chart_type=ChartType.CANDLESTICK;
            System.out.printf("Set Set Candlestick\n");
        }
       doRedraw();
    }//GEN-LAST:event_candleTypeMEnuItemItemStateChanged

    private void lineTypeItemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lineTypeItemItemStateChanged
       if (this.lineTypeItem.isSelected()){
            this.chart_type=ChartType.LINE;
            System.out.printf("Set LIne\n");
        }
       doRedraw();
    }//GEN-LAST:event_lineTypeItemItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButtonMenuItem candleTypeMEnuItem;
    private javax.swing.JMenu compMenu;
    private javax.swing.JPopupMenu ctxMenu;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JRadioButtonMenuItem lineTypeItem;
    private javax.swing.JCheckBoxMenuItem logMenu;
    private javax.swing.ButtonGroup typeButtonGroup;
    private javax.swing.JMenu typeMenu;
    // End of variables declaration//GEN-END:variables
}
