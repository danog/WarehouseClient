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
import Main.ClientException;
import Main.Container;
import Main.Warehouse;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author root
 */
public class TableButton extends JButton {
    private Boolean isWarehouse = true;
    private Client client;
    public TableButton(Boolean isWarehouse, Client client) {
        super();
        this.isWarehouse = isWarehouse;
        this.client = client;
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
