/**
 * Forum.java
 *
 * Created on 27 de marzo de 2004, 18:55
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
package org.ArgentumOnline.server;

import java.util.*;

/**
 *
 * @author  Pablo Fernando Lillia
 */
public class Forum {

    final static int MAX_FORUM_MESSAGES = 35;
    
    String foroId;
    Vector<ForumMessage> m_messages = new Vector<ForumMessage>();
    
    /** Creates a new instance of Forum */
    public Forum(String foroId) {
        this.foroId = foroId;
    }
    
    public String getForoId() {
        return this.foroId;
    }
    
    public void addMessage(String titulo, String texto) {
        if (this.m_messages.size() >= MAX_FORUM_MESSAGES) {
            this.m_messages.removeElementAt(0);
        }
        this.m_messages.add(new ForumMessage(titulo, texto));
    }
    
    public int messageCount() {
        return this.m_messages.size();
    }
    
    public ForumMessage getMessage(int index) {
        if (index > 0 && index <= this.m_messages.size()) {
            return this.m_messages.get(index - 1);
        }
        return null;
    }
    
}
