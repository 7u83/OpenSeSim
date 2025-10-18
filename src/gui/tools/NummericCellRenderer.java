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
package gui.tools;

import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JTable;
import static javax.swing.SwingConstants.RIGHT;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class NummericCellRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formatter;

    public NummericCellRenderer(int decimals) {
        super();
        String s = "#0.";
        if (decimals == 0) {
            s = "#";
        } else {
            for (int i = 0; i < decimals; i++) {
                s = s + "0";
            }
        }
        formatter = new DecimalFormat(s);
        this.setHorizontalAlignment(RIGHT);
    }

    /**
     *
     * @param formatter
     */
    public NummericCellRenderer(DecimalFormat formatter){
        this.formatter=formatter;
        this.setHorizontalAlignment(RIGHT);
    }
    
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        
        if (!(value instanceof Float)) {
            return super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
        }

        // Format the cell value as required
        value = formatter.format((Number) value);

        // And pass it on to parent class
        return super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
    }
}
