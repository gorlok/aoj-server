/**
 * SimpleClientTest.java
 *
 * Created on 5 de septiembre de 2003, 22:30
 * 
    AOJava Server
    Copyright (C) 2003-2007 Pablo Fernando Lillia (alias Gorlok)
    Web site: http://www.aojava.com.ar
    
    This file is part of AOJava.

    AOJava is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    AOJava is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    
 */
package org.ArgentumOnline.server.test;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

/**
 *
 * @author  pablo
 */
public class SimpleClientTest extends Frame implements ActionListener {
    
	private static final long serialVersionUID = 0L;
	
    Socket con;
    DataOutputStream out;
    DataInputStream in;
    
    Panel panel;
    TextField textent;
    TextField textsal;
    Button enviar;
    Button conectar;
    Button desconectar;
    
    /** Creates a new instance of SimpleClientTest */
    public SimpleClientTest(String name) {
        super(name);
        setSize(350, 200);
        this.panel = new Panel();
        this.textsal = new TextField(40);
        this.textent = new TextField(40);
        this.textent.setText("Pulse el botón \"Conectar\"...");
        this.textent.setEditable(false);
        this.enviar = new Button("Enviar");
        this.enviar.setEnabled(false);
        this.conectar = new Button("Conectar");
        this.desconectar = new Button("Desconectar");
        this.desconectar.setEnabled(false);
        this.panel.add(new Label("Datos a enviar"));
        this.panel.add(this.textsal);
        this.panel.add(new Label("Datos recibidos"));
        this.panel.add(this.textent);
        this.panel.add(this.enviar);
        this.panel.add(this.conectar);
        this.panel.add(this.desconectar);
        this.enviar.addActionListener(this);
        this.conectar.addActionListener(this);
        this.desconectar.addActionListener(this);
        add(this.panel);
        setVisible(true);
    }
    
    /** Invoked when an action occurs.
     *
     */
    public void actionPerformed(ActionEvent ev) {
        String com = ev.getActionCommand();
        if (com.equals("Enviar")) {
            System.err.println("enviar...");
            try {
                this.textent.setText("");
                this.out.writeUTF(this.textsal.getText());
                this.textent.setText(this.in.readUTF());
                this.textsal.setText("");
            }
            catch (IOException ex) { ex.printStackTrace(); }
        }
        else if (com.equals("Conectar")) {
            System.err.println("conectar...");
            try {
                this.con = new Socket(InetAddress.getLocalHost(), 7666);
                this.out = new DataOutputStream(this.con.getOutputStream());
                this.in = new DataInputStream(this.con.getInputStream());
                this.conectar.setEnabled(false);
                this.desconectar.setEnabled(true);
                this.enviar.setEnabled(true);
                this.textent.setText("");
            }
            catch (IOException ex) { ex.printStackTrace(); }
        }
        else {
            System.err.println("otro...");
            try {
                this.out.writeUTF("/SALIR");
                this.con.close();
                this.conectar.setEnabled(true);
                this.desconectar.setEnabled(false);
                this.enviar.setEnabled(false);
                this.textent.setText("Pulse el botón \"Conectar\"...");
            }
            catch (IOException ex) { ex.printStackTrace(); }
        }
    }
    
    public static void main(String args[]) {
        new SimpleClientTest("Cliente Blanco");
        new SimpleClientTest("Cliente Azul");
    }
    
}
