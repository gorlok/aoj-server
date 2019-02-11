/**
 * CharInfo.java
 *
 * Created on 23 de febrero de 2004, 21:23
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

import org.ArgentumOnline.server.util.*;

/**
 * @author gorlok
 */
public class CharInfo implements Constants {
    public short m_cabeza;
    public short m_cuerpo;
    public short m_arma;
    public short m_escudo;
    public short m_casco;
    public short m_fx;
    public short m_loops;
    public short m_dir;
    
    public CharInfo() {
    	//
    }
    
    public CharInfo(CharInfo ic) {
        this.m_cabeza = ic.m_cabeza;
        this.m_cuerpo = ic.m_cuerpo;
        this.m_arma   = ic.m_arma;
        this.m_escudo = ic.m_escudo;
        this.m_casco  = ic.m_casco;
        this.m_fx     = ic.m_fx;
        this.m_loops  = ic.m_loops;
        this.m_dir    = ic.m_dir;
    }
    
    public void reset() {
        this.m_cabeza = 0;
        this.m_cuerpo = 0;
        this.m_arma   = 0;
        this.m_escudo = 0;
        this.m_casco  = 0;
        this.m_fx     = 0;
        this.m_loops  = 0;
        this.m_dir    = 0;
    }
    
    public short getCabeza()    { return this.m_cabeza; }
    public short getCuerpo()    { return this.m_cuerpo; }
    public short getArma()      { return this.m_arma; }
    public short getEscudo()    { return this.m_escudo; }
    public short getCasco()     { return this.m_casco; }
    public short getFX()        { return this.m_fx; }
    public short getLoops()     { return this.m_loops; }
    public short getDir()       { return this.m_dir; }
    
    public void setDir(short dir) {
        this.m_dir = dir;
    }
    
    public void cuerpoDesnudo(int raza, int genero) {
        switch (raza) {
            case RAZA_HUMANO:
                this.m_cuerpo = (genero == GENERO_HOMBRE) ? (short) 21 : (short) 39;
                break;
            case RAZA_ELFO_OSCURO:
                this.m_cuerpo = (genero == GENERO_HOMBRE) ? (short) 32 : (short) 40;
                break;
            case RAZA_ENANO:
            case RAZA_GNOMO:
                this.m_cuerpo = (genero == GENERO_HOMBRE) ? (short) 53 : (short) 60;
                break;
            default:
                this.m_cuerpo = (genero == GENERO_HOMBRE) ? (short) 21 : (short) 39;
                break;
        }
    }
    
    public void cuerpoYCabeza(int raza, int genero) {
        // DarCuerpoYCabeza
        switch (genero) {
            case GENERO_HOMBRE:
                switch (raza) {
                    case RAZA_HUMANO:
                        this.m_cabeza = (short) Util.Azar(1, 11);
                        this.m_cuerpo = 1;
                        break;
                    case RAZA_ELFO:
                        this.m_cabeza = (short) (Util.Azar(1, 4) + 100);
                        this.m_cuerpo = 2;
                        break;
                    case RAZA_ELFO_OSCURO:
                        this.m_cabeza = (short) (Util.Azar(1, 3) + 200);
                        this.m_cuerpo = 3;
                        break;
                    case RAZA_ENANO:
                        this.m_cabeza = 301;
                        this.m_cuerpo = 52;
                        break;
                    case RAZA_GNOMO:
                        this.m_cabeza = 401;
                        this.m_cuerpo = 52;
                        break;
                    default: // :-?
                        this.m_cabeza = 1;
                        this.m_cuerpo = 1;
                        break;
                }
                break;
            case GENERO_MUJER:
                switch (raza) {
                    case RAZA_HUMANO:
                        this.m_cabeza = (short) (Util.Azar(1, 3) + 69);
                        this.m_cuerpo = 1;
                        break;
                    case RAZA_ELFO:
                        this.m_cabeza = (short) (Util.Azar(1, 3) + 169);
                        this.m_cuerpo = 2;
                        break;
                    case RAZA_ELFO_OSCURO:
                        this.m_cabeza = (short) (Util.Azar(1, 3) + 269);
                        this.m_cuerpo = 3;
                        break;
                    case RAZA_GNOMO:
                        this.m_cabeza = (short) (Util.Azar(1, 2) + 469);
                        this.m_cuerpo = 52;
                        break;
                    case RAZA_ENANO:
                        this.m_cabeza = 370;
                        this.m_cuerpo = 52;
                        break;
                    default: // :-?
                        this.m_cabeza = 70;
                        this.m_cuerpo = 1;
                        break;
                }
                break;
        }
    }
    
}
