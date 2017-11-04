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
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author root
 */
public class TableScrollPane extends JScrollPane {
    public TableScrollPane(Client client, Container container) {
        super();
        final JTable table = new JTable(new TableModel(client, container));
        final TableCellRenderer renderer = new TableButtonRenderer();
        final TableMouseListener listener = new TableMouseListener(table);
        table.setRowHeight(30);
        TableColumn column;
        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            switch (i) {
                case 1:
                    column.setPreferredWidth(150); // second column is bigger
                    break;
                case 4:
                    column.setPreferredWidth(30); // second column is bigger
                    column.setCellRenderer(renderer);
                    break;
                default:
                    column.setPreferredWidth(1);
                    break;
            }
        }
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        table.addMouseListener(listener);
        
        this.setBorder(BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (),
                                                            container.getClass().getSimpleName(),
                                                            TitledBorder.CENTER,
                                                            TitledBorder.TOP));
        
        this.setViewportView(table);
    }
}
