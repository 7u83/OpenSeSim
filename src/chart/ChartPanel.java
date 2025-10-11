/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chart;

import gui.Globals;
import javax.swing.JScrollPane;

/**
 *
 * @author tube
 */
public class ChartPanel extends JScrollPane {

    private MainChart chart;
    
    public MainChart getChart(){
        return (chart);
    }

    /**
     *
     */
    public ChartPanel() {
        this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        chart = new chart.MainChart();
        setViewportView(chart);
    }

    public void reset() {
        chart.initChart();
        chart.invalidate();
        chart.repaint();
        Globals.sim.se.addQuoteReceiver(chart);
    }

}
