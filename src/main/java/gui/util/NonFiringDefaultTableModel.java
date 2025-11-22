/*
 * Copyright (c) 2017, 2025 7u83 <7u83@mail.ru>
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
package gui.util;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author tube
 */
public class NonFiringDefaultTableModel extends DefaultTableModel {
// Felder zur Speicherung der Metadaten

    private Class<?>[] types;
    private boolean[] canEdit;

    // 1. STANDARD-KONSTRUKTOR (Für den NetBeans Designer zur Instanziierung)
    public NonFiringDefaultTableModel() {
        // Muss existieren und PUBLIC sein.
        super(new Object[0][0], new String[0]);
        this.types = new Class[0];
        this.canEdit = new boolean[0];
    }

// 1. PRIVATER Konstruktor: MUSS mit dem 'super' Call beginnen.
    private NonFiringDefaultTableModel(String[] columnNames, Class<?>[] types, boolean[] canEdit) {
        // HIER MUSS DER SUPER-CALL ALS ERSTES STEHEN
        super(new Object[0][columnNames.length], columnNames);

        // Erst DANACH dürfen die eigenen Felder gesetzt werden
        this.types = types;
        this.canEdit = canEdit;
    }

    // 2. STATISCHE FABRIK-METHODE: Hier findet die gesamte Vorbereitung statt.
    public static NonFiringDefaultTableModel createFromDesignerModel(TableModel sourceModel) {

        int colCount = sourceModel.getColumnCount();

        // Alle Daten-Arrays werden HIER vorbereitet, bevor sie übergeben werden
        String[] columnNames = new String[colCount];
        Class<?>[] types = new Class[colCount];
        boolean[] canEdit = new boolean[colCount];

        for (int i = 0; i < colCount; i++) {
            columnNames[i] = sourceModel.getColumnName(i);
            types[i] = sourceModel.getColumnClass(i);
            canEdit[i] = sourceModel.isCellEditable(0, i);
        }

        // Ruft den privaten Konstruktor mit den bereits fertigen Daten auf
        return new NonFiringDefaultTableModel(columnNames, types, canEdit);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }

    @Override
    public void fireTableStructureChanged() {

    }

    @Override
    public void fireTableRowsUpdated(int firstRow, int lastRow) {
    }

    @Override
    public void fireTableCellUpdated(int row, int column) {

    }
}
