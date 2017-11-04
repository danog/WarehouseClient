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
import Payloads.ServerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

/**
 *
 * @author Daniil Gentili
 */
public class CheckoutButton extends JButton implements ActionListener {
    private Client client;
    public CheckoutButton(Client client) {
        super("Checkout");
        this.client = client;
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        try {
            client.commit();
            JOptionPane.showMessageDialog(null, "OK!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore I/O", ERROR_MESSAGE);
        } catch (ServerException ex) {
            JOptionPane.showMessageDialog(null, String.format("%d: %s", ex.getPayload().getResponseCode(), ex.getPayload().getResponseDescription()), "Errore del server", ERROR_MESSAGE);
        }
        getRootPane().repaint();
    }
}
