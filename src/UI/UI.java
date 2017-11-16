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
package UI;

import Main.Client;
import Main.ClientException;
import Main.Container;
import Main.Warehouse;
import Payloads.ServerException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author Daniil Gentili
 */
public class UI {

    JFrame frame;
    Client client;
    JLabel grandTotal;
    TableScrollPane cartScrollPane;
    TableScrollPane warehouseScrollPane;

    public UI(Client client, JFrame frame) {
        this.frame = frame;
        this.client = client;
        this.grandTotal = new JLabel("Grand total: " + client.getCart().getPriceTotal().toString());
        this.warehouseScrollPane = new TableScrollPane(client.getWarehouse());
        this.cartScrollPane = new TableScrollPane(client.getCart());

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                try {
                    client.close();
                } catch (IOException ex) {
                    Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                }
                frame.dispose();
                System.exit(0);
            }
        });
        JPanel panel = new JPanel();
        panel.add(this.grandTotal);
        panel.add(new CheckoutButton());
        frame.getContentPane().add(this.warehouseScrollPane, BorderLayout.PAGE_START);
        frame.getContentPane().add(this.cartScrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.PAGE_END);
        frame.pack();
        frame.setVisible(true);
    }

    private void refresh() {
        this.grandTotal.setText("Grand total: " + client.getCart().getPriceTotal().toString());
        cartScrollPane.repaint();
        warehouseScrollPane.repaint();
    }

    private class CheckoutButton extends JButton implements ActionListener {
        public CheckoutButton() {
            super("Checkout");
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                client.checkout();
                JOptionPane.showMessageDialog(null, "OK!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore I/O", ERROR_MESSAGE);
            } catch (ServerException ex) {
                JOptionPane.showMessageDialog(null, String.format("%d: %s", ex.getPayload().getResponseCode(), ex.getPayload().getResponseDescription()), "Errore del server", ERROR_MESSAGE);
            }
            refresh();
        }
    }

    private class TableScrollPane extends JScrollPane {

        public TableScrollPane(Container container) {
            super();
            final JTable table = new JTable(new TableModel(container));
            final TableCellRenderer renderer = new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    return (JButton) value;
                }
            };
            final MouseAdapter listener = new MouseAdapterImpl(table);
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

            this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                    container.getClass().getSimpleName(),
                    TitledBorder.CENTER,
                    TitledBorder.TOP));

            this.setViewportView(table);
        }
    }

    private class TableModel extends AbstractTableModel {

        private String[] columnNames = new String[]{"ID", "Description", "Price", "Count", "Buy"};
        private final Container container;
        private final TableButton button;

        TableModel(Container container) {
            Boolean isWarehouse = container instanceof Warehouse;
            this.columnNames[4] = isWarehouse ? "Buy" : "Remove";
            this.container = container;
            this.button = new TableButton(isWarehouse);
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

    private class TableButton extends JButton {

        private Boolean isWarehouse = true;

        public TableButton(Boolean isWarehouse) {
            super();
            this.isWarehouse = isWarehouse;
            this.setText(isWarehouse ? "Buy" : "Remove");
        }

        public void press(Integer productID) {
            try {
                if (isWarehouse) {
                    client.addToCart(productID);
                } else {
                    client.removeFromCart(productID);
                }
            } catch (ClientException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", ERROR_MESSAGE);
            }
        }

    }

    private class MouseAdapterImpl extends MouseAdapter {

        private final JTable table;

        public MouseAdapterImpl(JTable table) {
            this.table = table;
        }

        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
            int row = e.getY() / table.getRowHeight(); //get the row of the button
            /*Checking the row or column is valid or not*/
            if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                Object value = table.getValueAt(row, column);
                if (value instanceof TableButton) {
                    ((TableButton) value).press((Integer) table.getValueAt(row, 0));
                    refresh(); // Redraw all tables
                }
            }
        }
    }
}
