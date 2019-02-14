/**
 * CharClassManager.java
 * 
 * Created on 13 de marzo de 2004, 11:54
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
package org.ArgentumOnline.server.classes;

import java.util.HashMap;

/**
 * Contains and manage all avaibles character clases.
 *
 * @author  Pablo Fernando Lillia
 */
public class CharClassManager {
    
    /** Singleton instance */
    private static CharClassManager instance = null;
    
    /** Contains all avaibles character clases */
    private HashMap<String,CharClass> clases = new HashMap<String,CharClass>();
    
    /** Creates a new instance of Clases and 
     *  initialize the clases collection.
     */
    private CharClassManager() {
        addClase(AssassinClass.getInstance());
        addClase(BanditClass.getInstance());
        addClase(BardoClass.getInstance());
        addClase(CarpenterClass.getInstance());
        addClase(HunterClass.getInstance());
        addClase(PriestClass.getInstance());
        addClase(DruidClass.getInstance());
        addClase(WarriorClass.getInstance());
        addClase(BlacksmithClass.getInstance());
        addClase(ThiefClass.getInstance());
        addClase(WoodcutterClass.getInstance());
        addClase(WizardClass.getInstance());
        addClase(MinerClass.getInstance());
        addClase(PaladinClass.getInstance());
        addClase(FishermanClass.getInstance());
        addClase(PirateClass.getInstance());
        addClase(TailorClass.getInstance());
    }

    /** Add a instance of Clase to clases collection */
    private void addClase(CharClass c) {
        this.clases.put(c.getName(), c);
    }

    /** Return singleton's instance */
    public static CharClassManager getInstance() {
        if (instance == null) {
			instance = new CharClassManager();
		}
        return instance;
    }
    
    public CharClass getClase(String name) {
        return this.clases.get(name);
    }
    
}
