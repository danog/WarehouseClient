/* 
 * Copyright (C) 2017 Daniil Gentili
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ui;

import Main.Client;
import Main.Container;
import Main.Warehouse;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author root
 */
public class TableModel extends AbstractTableModel {
    private String[] columnNames = new String [] {"ID", "Description", "Price", "Count", "Buy"};
    private final Container container;
    private final TableButton button;
    
    TableModel(Client client, Container container) {
        Boolean isWarehouse = container instanceof Warehouse;
        this.columnNames[4] = isWarehouse ? "Buy" : "Remove";
        this.container = container;
        this.button = new TableButton(isWarehouse, client);
    }
    public TableButton getButton() {
        return button;
    }
    /*
        Table model methods
    */
    @Override
    public int getRowCount() {
        return container.getProductCount();
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }
    

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return container.getNthProductCollection(rowIndex).getID();
            case 1:
                return container.getNthProductCollection(rowIndex).getDescription();
            case 2:
                return container.getNthProductCollection(rowIndex).getPrice();
            case 3:
                return container.getNthProductCollection(rowIndex).getCount();
            case 4:
                return button;
            default:
                return null;
        }
    }
}
