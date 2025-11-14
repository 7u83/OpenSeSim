/*
 * Copyright (c) 2025, 7u83 <7u83@mail.ru>
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
package gui.orderbook;

import gui.Globals;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import sesim.Exchange;
import sesim.Exchange.CompOrderBookEntry;
import sesim.OrderBookEntry;
import sesim.Order;

/**
 *
 * @author tube
 */
public class OrderBook extends RawOrderBook {

    TreeMap<Long, OrderBookEntry> lastMap = new TreeMap<>();

    ;
    
    class ColoredOrderBookEntry extends CompOrderBookEntry {

        public String color = new String();

        private ColoredOrderBookEntry(OrderBookEntry oe) {
            super(oe);
        }

        @Override
        public String getOwnerName() {
            return color;
        }

    }

    @Override
    public void setGodMode(boolean on) {
        super.setGodMode(false);
    }

    private OrderBookEntry getDiffedEntry(OrderBookEntry oe) {
        OrderBookEntry prev = lastMap.get(oe.getLimit_Long());
        ColoredOrderBookEntry ce = new ColoredOrderBookEntry(oe);
        if (prev == null) {
            ce.color = "Gray";
        } else {
            if (prev.getVolume() > oe.getVolume()) {
                ce.color = "Red";
            } else if (prev.getVolume() < oe.getVolume()) {
                ce.color = "Green";
            } else {
                ce.color = "  ---  ";
            }
        }
        return ce;
    }

    @Override
    protected ArrayList<? extends OrderBookEntry> getOrderBook() {
        TreeMap<Long, OrderBookEntry> map = Globals.sim.getExchange().getCompressedOrderBook(type, depth);

        //TreeMap<Float, OrderBookEntry> cmap = new TreeMap<>();
        ArrayList<OrderBookEntry> r = new ArrayList<>();

        if (type == Order.BUYLIMIT) {
            for (Map.Entry<Long, OrderBookEntry> oe : ((TreeMap<Long, OrderBookEntry>) map).descendingMap().entrySet()) {
                r.add(this.getDiffedEntry(oe.getValue()));
            }
        } else {

            for (Map.Entry<Long, OrderBookEntry> oe : map.entrySet()) {
                r.add(this.getDiffedEntry(oe.getValue()));

            }
        }
        this.lastMap = map;
        /*new TreeMap<>();
        for (OrderBookEntry e: r){
            this.lastMap.put(e.limit, e);
        }*/

        return r;
    }

    class MyModel extends RawOrderBookModel {

    }

    class MyList extends JTable {

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);

            // Zugriff auf das Model (nicht die View-Spalte)
            int modelRow = convertRowIndexToModel(row);
            Object firstColValue = getModel().getValueAt(modelRow, 0);

            if ("Red".equals(firstColValue)) {
                c.setBackground(new Color(255, 200, 200));
            } else if ("Gray".equals(firstColValue)) {
                c.setBackground(new Color(220, 220, 220));
            } else if ("Green".equals(firstColValue)) {
                c.setBackground(new Color(200, 255, 200));
            } else {
                c.setBackground(list.getBackground());
            }

            return c;
        }
    }

    @Override
    protected JTable createList() {
        return new MyList();
    }

    @Override
    protected AbstractTableModel createModel() {
        return new MyModel();
    }

}
