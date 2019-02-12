/**
 * ClassicProtocol.java
 * 
 * Created 28/jan/2007
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
package org.ArgentumOnline.server.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;

import org.ArgentumOnline.server.classes.CharClass;
import org.ArgentumOnline.server.classes.CharClassManager;
import org.ArgentumOnline.server.util.Log;
import org.ArgentumOnline.server.Constants;

/**
 * @author gorlok
 *
 */
public class ClassicProtocol extends Protocol implements ServerCommands {
	
    private StringBuffer m_bufMsg = new StringBuffer();
    
	@Override
	public boolean decodeData(byte[] data, int length) {
        String str;
        try {
        	str = new String(data, "cp1252"); // REVISAR !!! WINDOWS ANSI !!! TODO: PROBAR EN LINUX.
        } catch (UnsupportedEncodingException uee) {
            Log.serverLogger().fine("ERROR: character encoding cp1252 not supported!");
            str = new String(data);
        }
        try {
            if ("".equals(str) || str.length() < 2) {
                Log.serverLogger().fine("leidos 0 bytes...");
                return false;
            }
            str = str.substring(0, length);
            //Log.info("Recibido: " + str + " len=" + str.length());
            if (str.length() > 0) {
				processStringData(str);
			} else {
            	return false;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        }	
		return true;
	}
	
	public void encodeData(ByteBuffer buf, String msg) {
	    try {
	    	buf.put(msg.getBytes("cp1252")); // REVISAR !!! WINDOWS ANSI !!! TODO: PROBAR EN LINUX.
	    } catch (UnsupportedEncodingException uee) {
            Log.serverLogger().fine("ERROR: character encoding cp1252 not supported!");
            buf.put(msg.getBytes());
	    }
        buf.put((byte) 1 ); // Command separator...
	}
	
	@Override
	public void encodeData(ByteBuffer buf, ClientMessage msg, Object... params) {
    	StringBuffer sb = new StringBuffer(msg.strMessage());
    	if (params != null) {
	    	for (int i = 0; i < params.length; i++) {
	    		if (i > 0) {
					sb.append(msg.separator());
				}
	    		sb.append(params[i]);
	    	}
    	}
	    try {
	    	Log.serverLogger().fine("[encodeData] >>> sb.toStr == " + sb.toString());
	    	///buf.put(sb.toString().getBytes()); // REVISAR !!! WINDOWS ANSI !!! TODO: PROBAR EN LINUX.
	    	buf.put(sb.toString().getBytes("cp1252")); // REVISAR !!! WINDOWS ANSI !!! TODO: PROBAR EN LINUX.
	    } catch (UnsupportedEncodingException uee) {
            Log.serverLogger().fine("ERROR: character encoding cp1252 not supported!");
            buf.put(sb.toString().getBytes());
	    }
        buf.put((byte) 1 ); // Command separator...
	}

    private void processStringData(String data) {
        this.m_bufMsg.append(data);
        String cmd = null;
        int pos = -1;
        // El Ascii(1) es el separador de mensajes en el protocolo clásico.
        while ((pos = this.m_bufMsg.toString().indexOf(1)) != -1) {
            cmd = this.m_bufMsg.substring(0, pos);
            this.m_bufMsg.delete(0, pos + 1);
            processStringCmd(cmd);
        }
    }

    private boolean equalsCmd(String s, String cmd) {
        if (s.length() < cmd.length()) {
			return false;
		}
        return s.substring(0, cmd.length()).equalsIgnoreCase(cmd);
    }
    
    /** Procesar los comandos y mensajes que envian los m_clients */
    public void processStringCmd(String s) {
        Log.serverLogger().fine("[" + this.cliente.getNick() + "] processCmd: " + s);
        this.cliente.getCounters().resetIdleCount();
        if (!this.cliente.isLogged()) {
            // Non-logged client.
        	if (parseUnlogedCmd(s)) {
				return;
			}
            Log.serverLogger().fine("Comando inválido en modo NOT LOGGED: " + s);
        } else {
            // Logged client.
            int crcPos = s.lastIndexOf('~');
            String clientCRC = s.substring(crcPos + 1);
            if (crcPos > -1) {
				s = s.substring(0, crcPos);
			}
            //////////// FIXME
            Log.serverLogger().fine("CRC=" + clientCRC); // FIXME - REMOVE - ONLY FOR DEBUG -
            // FIXME ---- pregunta: realmente hace falta esto???
            int serverCRC = this.cliente.genCRC();
            if (false && Integer.parseInt(clientCRC) != serverCRC) {
                Log.serverLogger().fine("ERROR DE CRC");
                this.cliente.doSALIR();
                return;
            }
            
            if (parseUserCmd(s)) {
				return;
			}
            if (this.cliente.getFlags().Privilegios < 1) {
                Log.serverLogger().fine("Comando inválido para USUARIO: " + s);
                return;
            }
            if (parseConsejeroCmd(s)) {
				return;
			}
            if (this.cliente.getFlags().Privilegios < 2) {
                Log.serverLogger().fine("Comando inválido, GM CONSEJERO: " + s);
                return;
            }
            if (parseSemiDiosCmd(s)) {
				return;
			}
            if (this.cliente.getFlags().Privilegios < 3) {
                Log.serverLogger().fine("Comando inválido, GM SEMIDIOS: " + s);
                return;
            }
            if (parseDiosCmd(s)) {
				return;
			}
            Log.serverLogger().fine("Comando inválido, GM DIOS: " + s);
        }
    }
    
    private boolean parseUnlogedCmd(String s) {
        // Non-logged client.
        if (equalsCmd(s, cmdGIVEMEVALCODE)) {
            this.cliente.enviarValCode();
        } else if (equalsCmd(s, cmdOLOGIN)) {
        	parseOLOGIN(s.substring(cmdOLOGIN.length()));
        } else if (equalsCmd(s, cmdNLOGIN)) {
        	parseNLOGIN(s.substring(cmdNLOGIN.length()));
        } else if (equalsCmd(s, cmdTIRDAD)) {
        	this.cliente.tirarDados();
        } else if (equalsCmd(s, cmdBORR)) {
        	this.cliente.borrarPersonaje(s.substring(cmdBORR.length()));
        } else {
			return false;
		}
        return true;
    }
    
    private boolean parseUserCmd(String s) {
	    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // Loged client.
	    if (equalsCmd(s, cmdSALIR)) {
			this.cliente.cerrarUsuario();
		} else if (equalsCmd(s, cmdTIRDAD)) {
			this.cliente.tirarDados();
		} else if (equalsCmd(s, cmdM)) {
			parseMover(s.substring(cmdM.length()));
		} else if (equalsCmd(s, cmdRPU) || equalsCmd(s, cmdACTUALIZAR)) {
			this.cliente.enviarPU();
		} else if (equalsCmd(s, cmdCHEA)) {
			parseCHEA(s.substring(cmdCHEA.length()));
		} else if (equalsCmd(s, cmdLC)) {
			parseLeftClick(s.substring(cmdLC.length()));
		} else if (equalsCmd(s, cmdRC)) {
			parseRightClick(s.substring(cmdRC.length()));
		} else if (equalsCmd(s, cmdHablar)) {
			this.cliente.doHablar(s.substring(cmdHablar.length()));
		} else if (equalsCmd(s, cmdGritar)) {
			this.cliente.doGritar(s.substring(cmdGritar.length()));
		} else if (equalsCmd(s, cmdSusurrar)) {
			this.cliente.doSusurrar(s.substring(cmdSusurrar.length()));
		} else if (equalsCmd(s, cmdINFS)) {
			this.cliente.doInfoHechizo(s.substring(cmdINFS.length()));
		} else if (equalsCmd(s, cmdEST)) {
			this.cliente.doEnviarEstads();
		} else if (equalsCmd(s, cmdATRI)) {
			this.cliente.doEnviarAtribs();
		} else if (equalsCmd(s, cmdFAMA)) {
			this.cliente.doEnviarFama();
		} else if (equalsCmd(s, cmdESKI)) {
			this.cliente.enviarSkills();
		} else if (equalsCmd(s, cmdUK)) {
			this.cliente.doUK(s.substring(cmdUK.length()));
		} else if (equalsCmd(s, cmdWLC)) {
			this.cliente.doWLC(s.substring(cmdWLC.length()));
		} else if (equalsCmd(s, cmdMEDITAR)) {
			this.cliente.doMeditar();
		} else if (equalsCmd(s, cmdUSA)) {
			parseUsa(s.substring(cmdUSA.length()));
		} else if (equalsCmd(s, cmdAG)) {
			this.cliente.agarrarObjeto();
		} else if (equalsCmd(s, cmdTI)) {
			parseTirarObjeto(s.substring(cmdTI.length()));
		} else if (equalsCmd(s, cmdEQUI)) {
			this.cliente.equiparObjeto(s.substring(cmdEQUI.length()));
		} else if (equalsCmd(s, cmdSKSE)) {
			this.cliente.modificarSkills(s.substring(cmdSKSE.length()));
		} else if (equalsCmd(s, cmdDESCANSAR)) {
			this.cliente.doDescansar();
		} else if (equalsCmd(s, cmdCURAR)) {
			this.cliente.doCurar();
		} else if (equalsCmd(s, cmdRESUCITAR)) {
			this.cliente.doResucitar();
		} else if (equalsCmd(s, cmdINFORMACION)) {
			this.cliente.doInformacion();
		} else if (equalsCmd(s, cmdPASSWD)) {
			this.cliente.cambiarPasswd(s.substring(cmdPASSWD.length()));
		} else if (equalsCmd(s, cmdONLINEGM)) {
			this.cliente.doONLINEGM();
		} else if (equalsCmd(s, cmdONLINE)) {
			this.cliente.doONLINE();
		} else if (equalsCmd(s, cmdDESC)) {
			this.cliente.cambiarDescripcion(s.substring(cmdDESC.length()));
		} else if (equalsCmd(s, cmdAYUDA)) {
			this.cliente.doAyuda();
		} else if (equalsCmd(s, cmdGMSG)) {
			this.cliente.doMensajeALosGM(s.substring(cmdGMSG.length()));
		} else if (equalsCmd(s, cmdGM)) {
			this.cliente.doPedirAyudaAlGM(s.substring(cmdGM.length()));
		} else if (equalsCmd(s, cmdAVENTURA)) {
			this.cliente.doIniciarAventura();
		} else if (equalsCmd(s, cmdREWARD)) {
			this.cliente.doRecompensaAventura();
		} else if (equalsCmd(s, cmdINFOQ)) {
			this.cliente.doInfoAventura();
		} else if (equalsCmd(s, cmdMERINDO)) {
			this.cliente.doRendirseAventura();
		} else if (equalsCmd(s, cmdADIVINA)) {
			this.cliente.doAdivinarAventura();
		} else if (equalsCmd(s, cmdSEG)) {
			this.cliente.cambiarSeguro();
		} else if (equalsCmd(s, cmdTAB)) {
			this.cliente.cambiarModoCombate();
		} else if (equalsCmd(s, cmdENTR)) {
			this.cliente.doEntrenarMascota(s.substring(cmdENTR.length()));
		} else if (equalsCmd(s, cmdAT)) {
			this.cliente.doAtacar();
		} else if (equalsCmd(s, cmdLH)) {
			this.cliente.doLanzarHechizo(s.substring(cmdLH.length()));
		} else if (equalsCmd(s, cmdDESPHE)) {
			parseDesplazarHechizo(s.substring(cmdDESPHE.length()));
		} else if (equalsCmd(s, cmdCOMERCIAR)) {
			this.cliente.doComerciar();
		} else if (equalsCmd(s, cmdCOMP)) {
			this.cliente.doComprar(s.substring(cmdCOMP.length()));
		} else if (equalsCmd(s, cmdVEND)) {
			this.cliente.doVender(s.substring(cmdVEND.length()));
		} else if (equalsCmd(s, cmdFINCOM)) {
			this.cliente.doFinComerciar();
		} else if (equalsCmd(s, cmdFINCOMUSU)) {
			this.cliente.doFinComerciarUsuario();
		} else if (equalsCmd(s, cmdCOMUSUNO)) {
			this.cliente.doRechazarComerciarUsuario();
		} else if (equalsCmd(s, cmdCOMUSUOK)) {
			this.cliente.doAceptarComerciarUsuario();
		} else if (equalsCmd(s, cmdOFRECER)) {
			parseOfrecerComerciarUsuario(s.substring(cmdOFRECER.length()));
		} else if (equalsCmd(s, cmdBALANCE)) {
			this.cliente.doBalance();
		} else if (equalsCmd(s, cmdDEPOSITAR)) {
			parseDepositarOro(s.substring(cmdDEPOSITAR.length()));
		} else if (equalsCmd(s, cmdRETIRAR)) {
			parseRetirar(s.substring(cmdRETIRAR.length()));
		} else if (equalsCmd(s, cmdBOVEDA)) {
			this.cliente.doBoveda();
		} else if (equalsCmd(s, cmdDEPO)) {
			parseDepositarBoveda(s.substring(cmdDEPO.length()));
		} else if (equalsCmd(s, cmdRETI)) {
			parseRetirarBoveda(s.substring(cmdRETI.length()));
		} else if (equalsCmd(s, cmdFINBAN)) {
			this.cliente.doFinBanco();
		} else if (equalsCmd(s, cmdCNS)) {
			parseConstruyeHerreria(s.substring(cmdCNS.length()));
		} else if (equalsCmd(s, cmdCNC)) {
			parseConstruyeCarpinteria(s.substring(cmdCNC.length()));
		} else if (equalsCmd(s, cmdENTRENAR)) {
			this.cliente.doEntrenar();
		} else if (equalsCmd(s, cmdQUIETO)) {
			this.cliente.doQuieto();
		} else if (equalsCmd(s, cmdACOMPAÑAR)) {
			this.cliente.doAcompañar();
		} else if (equalsCmd(s, cmdFEST)) {
			this.cliente.doEnviarMiniEstadisticas();
		} else if (equalsCmd(s, cmdENLISTAR)) {
			this.cliente.doEnlistar();
		} else if (equalsCmd(s, cmdRECOMPENSA)) {
			this.cliente.doRecompensa();
		} else if (equalsCmd(s, cmdDEMSG)) {
			parsePonerMensajeForo(s.substring(cmdDEMSG.length()));
		} else if (equalsCmd(s, cmdDESCOD)) {
			this.cliente.doActualizarMandamientosClan(s.substring(cmdDESCOD.length()));
		} else if (equalsCmd(s, cmdFUNDARCLAN)) {
			this.cliente.doFundarClan();
		} else if (equalsCmd(s, cmdSALIRCLAN)) {
			this.cliente.doSalirClan();
		} else if (equalsCmd(s, cmdGLINFO)) {
			this.cliente.doInfoClan();
		} else if (equalsCmd(s, cmdCIG)) {
			this.cliente.doCrearClan(s.substring(cmdCIG.length()));
		} else if (equalsCmd(s, cmdVOTO)) {
			this.cliente.doVotarClan(s.substring(cmdVOTO.length()));
		} else if (equalsCmd(s, cmdACEPPEAT)) {
			this.cliente.doAceptarOfertaPaz(s.substring(cmdACEPPEAT.length()));
		} else if (equalsCmd(s, cmdPEACEOFF)) {
			this.cliente.doRecibirOfertaPaz(s.substring(cmdPEACEOFF.length()));
		} else if (equalsCmd(s, cmdPEACEDET)) {
			this.cliente.doEnviarPedidoPaz(s.substring(cmdPEACEDET.length()));
		} else if (equalsCmd(s, cmdENVCOMEN)) {
			this.cliente.doEnviarPeticion(s.substring(cmdENVCOMEN.length()));
		} else if (equalsCmd(s, cmdENVPROPP)) {
			this.cliente.doEnviarProposiciones();
		} else if (equalsCmd(s, cmdDECGUERR)) {
			this.cliente.doDeclararGuerra(s.substring(cmdDECGUERR.length()));
		} else if (equalsCmd(s, cmdDECALIAD)) {
			this.cliente.doDeclararAlianza(s.substring(cmdDECALIAD.length()));
		} else if (equalsCmd(s, cmdNEWWEBSI)) {
			this.cliente.doSetNewURL(s.substring(cmdNEWWEBSI.length()));
		} else if (equalsCmd(s, cmdACEPTARI)) {
			this.cliente.doAceptarMiembroClan(s.substring(cmdACEPTARI.length()));
		} else if (equalsCmd(s, cmdRECHAZAR)) {
			this.cliente.doRechazarPedido(s.substring(cmdRECHAZAR.length()));
		} else if (equalsCmd(s, cmdECHARCLA)) {
			this.cliente.doEcharMiembro(s.substring(cmdECHARCLA.length()));
		} else if (equalsCmd(s, cmdACTGNEWS)) {
			this.cliente.doActualizarGuildNews(s.substring(cmdACTGNEWS.length()));
		} else if (equalsCmd(s, cmd1HRINFO)) {
			this.cliente.doCharInfoClan(s.substring(cmd1HRINFO.length()));
		} else if (equalsCmd(s, cmdSOLICITUD)) {
			parseSolicitudIngresoClan(s.substring(cmdSOLICITUD.length()));
		} else if (equalsCmd(s, cmdCLANDETAILS)) {
			this.cliente.doClanDetails(s.substring(cmdCLANDETAILS.length()));
		} else if (equalsCmd(s, cmdUPTIME)) {
			this.cliente.doUptime();
		} else if (equalsCmd(s, cmdAPOSTAR)) {
			parseApostar(s.substring(cmdAPOSTAR.length()));
		} else if (equalsCmd(s, cmdMOTDCAMBIA)) {
			this.cliente.doIniciarCambiarMOTD();
		} else if (equalsCmd(s, cmdMOTD)) {
			this.cliente.doMOTD();
		} else {
			return false;
		}
	    return true;
    }
    
    // >>>>>>>>>>>>>>>>>>>>>> SOLO ADMINISTRADORES <<<<<<<<<<<<<<<<<<<
    // >>>>>>>>>>>>>>>>>>>>>> SOLO ADMINISTRADORES <<<<<<<<<<<<<<<<<<<

    private boolean parseConsejeroCmd(String s) {
        // <<<<<<<<<<<<<<<<<<<< Consejeros <<<<<<<<<<<<<<<<<<<<
        // <<<<<<<<<<<<<<<<<<<< Consejeros <<<<<<<<<<<<<<<<<<<<
        // <<<<<<<<<<<<<<<<<<<< Consejeros <<<<<<<<<<<<<<<<<<<<
		if (equalsCmd(s, cmdPANELGM)) {
			this.cliente.doPanelGM();
		} else if (equalsCmd(s, cmdLISTUSU)) {
			this.cliente.doListaUsuarios();
		} else if (equalsCmd(s, cmdREM)) {
			this.cliente.doGuardarComentario(s.substring(cmdREM.length()));
		} else if (equalsCmd(s, cmdHORA)) {
			this.cliente.doEnviarHora();
		} else if (equalsCmd(s, cmdDONDE)) {
			this.cliente.doDonde(s.substring(cmdDONDE.length()));
		} else if (equalsCmd(s, cmdNENE)) {
			parseEnviarCantidadHostiles(s.substring(cmdNENE.length()));
		} else if (equalsCmd(s, cmdTELEPLOC)) {
			this.cliente.doTeleploc();
		} else if (equalsCmd(s, cmdTELEP)) {
			parseTeleportUsuario(s.substring(cmdTELEP.length()));
		} else if (equalsCmd(s, cmdSHOWSOS)) {
			this.cliente.doMostrarAyuda();
		} else if (equalsCmd(s, cmdSOSDONE)) {
			this.cliente.doFinAyuda(s.substring(cmdSOSDONE.length()));
		} else if (equalsCmd(s, cmdIRA)) {
			this.cliente.doIrAUsuario(s.substring(cmdIRA.length()));
		} else if (equalsCmd(s, cmdINVISIBLE)) {
			this.cliente.doHacerInvisible();
		} else if (equalsCmd(s, cmdTRABAJANDO)) {
			this.cliente.doUsuariosTrabajando();
		} else if (equalsCmd(s, cmdONLINEMAP)) {
			this.cliente.doUsuariosEnMapa();
		} else {
			return false;
		}
		return true;
    }
    
    private boolean parseSemiDiosCmd(String s) {
        //<<<<<<<<<<<<<<<<<< SemiDioses <<<<<<<<<<<<<<<<<<<<<<<<
        //<<<<<<<<<<<<<<<<<< SemiDioses <<<<<<<<<<<<<<<<<<<<<<<<
        //<<<<<<<<<<<<<<<<<< SemiDioses <<<<<<<<<<<<<<<<<<<<<<<<
        
        if (equalsCmd(s, cmdINFOUSER)) {
			this.cliente.doInfoUsuario(s.substring(cmdINFOUSER.length()));
		} else if (equalsCmd(s, cmdINV)) {
			this.cliente.doInvUser(s.substring(cmdINV.length()));
		} else if (equalsCmd(s, cmdBOV)) {
			this.cliente.doBovUser(s.substring(cmdBOV.length()));
		} else if (equalsCmd(s, cmdSKILLS)) {
			this.cliente.doSkillsUser(s.substring(cmdSKILLS.length()));
		} else if (equalsCmd(s, cmdREVIVIR)) {
			this.cliente.doRevivir(s.substring(cmdREVIVIR.length()));
		} else if (equalsCmd(s, cmdCARCEL)) {
			this.cliente.doEncarcelar(s.substring(cmdCARCEL.length()));
		} else if (equalsCmd(s, cmdPERDON)) {
			this.cliente.doPerdonar(s.substring(cmdPERDON.length()));
		} else if (equalsCmd(s, cmdECHAR)) {
			this.cliente.doEchar(s.substring(cmdECHAR.length()));
		} else if (equalsCmd(s, cmdBAN)) {
			parseBan(s.substring(cmdBAN.length()));
		} else if (equalsCmd(s, cmdUNBAN)) {
			this.cliente.doUnban(s.substring(cmdUNBAN.length()));
		} else if (equalsCmd(s, cmdSEGUIR)) {
			this.cliente.doSeguir();
		} else if (equalsCmd(s, cmdSUM)) {
			this.cliente.doSUM(s.substring(cmdSUM.length()));
		} else if (equalsCmd(s, cmdCC)) {
			this.cliente.sendSpawnList();
		} else if (equalsCmd(s, cmdSPA)) {
			parseSPA(s.substring(cmdSPA.length()));
		} else if (equalsCmd(s, cmdRESETINV)) {
			this.cliente.doResetInv();
		} else if (equalsCmd(s, cmdLIMPIAR)) {
			this.cliente.doLimpiarMundo();
		} else if (equalsCmd(s, cmdRMSG)) {
			this.cliente.doRMSG(s.substring(cmdRMSG.length()));
		} else if (equalsCmd(s, cmdIP2NICK)) {
			this.cliente.doIP2Nick(s.substring(cmdIP2NICK.length()));
		} else if (equalsCmd(s, cmdNICK2IP)) {
			this.cliente.doNick2IP(s.substring(cmdNICK2IP.length()));
		} else {
			return false;
		}
        return true;
    }
    
    private boolean parseDiosCmd(String s) {
        //<<<<<<<<<<<<<<<<<< Dioses <<<<<<<<<<<<<<<<<<<<<<<<
        //<<<<<<<<<<<<<<<<<< Dioses <<<<<<<<<<<<<<<<<<<<<<<<
        //<<<<<<<<<<<<<<<<<< Dioses <<<<<<<<<<<<<<<<<<<<<<<<
        if (equalsCmd(s, cmdBANIP)) {
			this.cliente.doBanIP(s.substring(cmdBANIP.length()));
		} else if (equalsCmd(s, cmdUNBANIP)) {
			this.cliente.doUnbanIP(s.substring(cmdUNBANIP.length()));
		} else if (equalsCmd(s, cmdSMSG)) {
			this.cliente.doSystemMsg(s.substring(cmdSMSG.length()));
		} else if (equalsCmd(s, cmdLLUVIA)) {
			this.cliente.doLluvia();
		} else if (equalsCmd(s, cmdAPAGAR)) {
			this.cliente.doApagar();
		} else if (equalsCmd(s, cmdGRABAR)) {
			this.cliente.doGrabar();
		} else if (equalsCmd(s, cmdDOBACKUP)) {
			this.cliente.doBackup();
		} else if (equalsCmd(s, cmdCONDEN)) {
			this.cliente.doCondenar(s.substring(cmdCONDEN.length()));
		} else if (equalsCmd(s, cmdPASSDAY)) {
			this.cliente.doPASSDAY();
		} else if (equalsCmd(s, cmdBORRARSOS)) {
			this.cliente.doBorrarSOS();
		} else if (equalsCmd(s, cmdEcharTodosPjs)) {
			this.cliente.doEcharTodosPjs();
		} else if (equalsCmd(s, cmdZMOTD)) {
			this.cliente.doFinCambiarMOTD(s.substring(cmdZMOTD.length()));
		} else if (equalsCmd(s, cmdMOD)) {
			parseModificarCaracter(s.substring(cmdMOD.length()).trim());
		} else if (equalsCmd(s, cmdMASSDEST)) {
			this.cliente.doMassDest();
		} else if (equalsCmd(s, cmdTRIGGER)) {
			parseTrigger(s.substring(cmdTRIGGER.length()));
		} else if (equalsCmd(s, cmdDEST)) {
			this.cliente.doDestObj();
		} else if (equalsCmd(s, cmdBLOQ)) {
			this.cliente.doBloqPos();
		} else if (equalsCmd(s, cmdCI)) {
			parseCrearItem(s.substring(cmdCI.length()));
		} else if (equalsCmd(s, cmdGUARDAMAPA)) {
			this.cliente.doGuardaMapa();
		} else if (equalsCmd(s, cmdMODMAPINFO)) {
			parseModMapInfo(s.substring(cmdMODMAPINFO.length()));
		} else if (equalsCmd(s, cmdACC)) {
			parseCrearCriatura(s.substring(cmdACC.length()));
		} else if (equalsCmd(s, cmdRACC)) {
			parseCrearCriaturaRespawn(s.substring(cmdRACC.length()));
		} else if (equalsCmd(s, cmdMATA)) {
			this.cliente.doMataNpc();
		} else if (equalsCmd(s, cmdMASSKILL)) {
			this.cliente.doMassKill();
		} else if (equalsCmd(s, cmdCT)) {
			parseCrearTeleport(s.substring(cmdCT.length()));
		} else if (equalsCmd(s, cmdDT)) {
			this.cliente.doDestruirTeleport();
		} else if (equalsCmd(s, cmdAI1)) {
			parseArmaduraImperial1(s.substring(cmdAI1.length()).trim());
		} else if (equalsCmd(s, cmdAI2)) {
			parseArmaduraImperial2(s.substring(cmdAI2.length()).trim());
		} else if (equalsCmd(s, cmdAI3)) {
			parseArmaduraImperial3(s.substring(cmdAI3.length()).trim());
		} else if (equalsCmd(s, cmdAI4)) {
			parseArmaduraImperial4(s.substring(cmdAI4.length()).trim());
		} else if (equalsCmd(s, cmdAI5)) {
			parseArmaduraImperial5(s.substring(cmdAI5.length()).trim());
		} else if (equalsCmd(s, cmdAC1)) {
			parseArmaduraCaos1(s.substring(cmdAC1.length()).trim());
		} else if (equalsCmd(s, cmdAC2)) {
			parseArmaduraCaos2(s.substring(cmdAC2.length()).trim());
		} else if (equalsCmd(s, cmdAC3)) {
			parseArmaduraCaos3(s.substring(cmdAC3.length()).trim());
		} else if (equalsCmd(s, cmdAC4)) {
			parseArmaduraCaos4(s.substring(cmdAC4.length()).trim());
		} else if (equalsCmd(s, cmdAC5)) {
			parseArmaduraCaos5(s.substring(cmdAC5.length()).trim());
		} else if (equalsCmd(s, cmdMASCOTAS)) {
			this.cliente.doMascotas(s.substring(cmdMASCOTAS.length()).trim());
		} else if (equalsCmd(s, cmdVERSION)) {
			this.cliente.doServerVersion();
		} else if (equalsCmd(s, cmdDEBUG)) {
			this.cliente.doDebug(s.substring(cmdDEBUG.length()).trim());
		} else if (equalsCmd(s, cmdNAVE)) {
			this.cliente.doComandoNave();
		} else if (equalsCmd(s, cmdShowInt)) {
			this.cliente.doShowInt();
		} else {
			return false;
		}
        return true;
    }    	
    
	/** TODO: Pendiente de implementar...
	 * 
    'Ultima ip de un char
    If UCase(Left(rdata, 8)) = "/LASTIP " Then
        Call LogGM(UserList(UserIndex).Name, rdata, False)
        rdata = Right(rdata, Len(rdata) - 8)

        'No se si sea MUY necesario, pero por si las dudas... ;)
        rdata = Replace(rdata, "\", "")
        rdata = Replace(rdata, "/", "")

        If FileExist(CharPath & rdata & ".chr", vbNormal) Then
            Call SendData(ToIndex, UserIndex, 0, "||La ultima IP de """ & rdata & """ fue : " & GetVar(CharPath & rdata & ".chr", "INIT", "LastIP") & FONTTYPE_INFO)
        Else
            Call SendData(ToIndex, UserIndex, 0, "||Charfile """ & rdata & """ inexistente." & FONTTYPE_INFO)
        End If
        Exit Sub
    End If

    If UCase(rdata) = "/BANIPLIST" Then
        Call LogGM(UserList(UserIndex).Name, rdata, False)
        tStr = "||"
        For LoopC = 1 To BanIps.Count
            tStr = tStr & BanIps.Item(LoopC) & ", "
        Next LoopC
        tStr = tStr & FONTTYPE_INFO
        Call SendData(ToIndex, UserIndex, 0, tStr)
        Exit Sub
    End If

    If UCase(rdata) = "/BANIPRELOAD" Then
        Call BanIpGuardar
        Call BanIpCargar
        Exit Sub
    End If

    'Ban x IP
    If UCase(Left(rdata, 7)) = "/BANIP " Then
        Dim BanIP As String, XNick As Boolean
        rdata = Right(rdata, Len(rdata) - 7)
        'busca primero la ip del nick
        tIndex = NameIndex(rdata)
        If tIndex <= 0 Then
            XNick = False
            Call LogGM(UserList(UserIndex).Name, "/BanIP " & rdata, False)
            BanIP = rdata
        Else
            XNick = True
            Call LogGM(UserList(UserIndex).Name, "/BanIP " & UserList(tIndex).Name & " - " & UserList(tIndex).ip, False)
            BanIP = UserList(tIndex).ip
        End If
        If BanIpBuscar(BanIP) > 0 Then
            Call SendData(ToIndex, UserIndex, 0, "||La IP " & BanIP & " ya se encuentra en la lista de bans." & FONTTYPE_INFO)
            Exit Sub
        End If
        Call BanIpAgrega(BanIP)
        Call SendData(ToAdmins, UserIndex, 0, "||" & UserList(UserIndex).Name & " Baneo la IP " & BanIP & FONTTYPE_FIGHT)
        If XNick = True Then
            Call LogBan(tIndex, UserIndex, "Ban por IP desde Nick")
            Call SendData(ToAdmins, 0, 0, "||" & UserList(UserIndex).Name & " echo a " & UserList(tIndex).Name & "." & FONTTYPE_FIGHT)
            Call SendData(ToAdmins, 0, 0, "||" & UserList(UserIndex).Name & " Banned a " & UserList(tIndex).Name & "." & FONTTYPE_FIGHT)
            'Ponemos el flag de ban a 1
            UserList(tIndex).flags.Ban = 1
            Call LogGM(UserList(UserIndex).Name, "Echo a " & UserList(tIndex).Name, False)
            Call LogGM(UserList(UserIndex).Name, "BAN a " & UserList(tIndex).Name, False)
            Call CloseSocket(tIndex)
        End If
        Exit Sub
    End If

    'Desbanea una IP
    If UCase(Left(rdata, 9)) = "/UNBANIP " Then
        rdata = Right(rdata, Len(rdata) - 9)
        Call LogGM(UserList(UserIndex).Name, "/UNBANIP " & rdata, False)
        If BanIpQuita(rdata) Then
            Call SendData(ToIndex, UserIndex, 0, "||La IP """ & rdata & """ se ha quitado de la lista de bans." & FONTTYPE_INFO)
        Else
            Call SendData(ToIndex, UserIndex, 0, "||La IP """ & rdata & """ NO se encuentra en la lista de bans." & FONTTYPE_INFO)
        End If
        Exit Sub
    End If
	
	If UCase$(Left$(rdata, 7)) = "/RAJAR " Then
	    rdata = Right$(rdata, Len(rdata) - 7)
	    tIndex = NameIndex(UCase$(rdata))
	    If tIndex > 0 Then
	        Call ResetFacciones(tIndex)
	    End If
	    Exit Sub
	End If

    If UCase$(Left$(rdata, 11)) = "/RAJARCLAN " Then
        rdata = Right$(rdata, Len(rdata) - 11)
        tIndex = NameIndex(UCase$(rdata))
        If tIndex > 0 Then
            Call EacharMember(tIndex, UserList(UserIndex).Name)
            UserList(tIndex).GuildInfo.GuildName = ""
            UserList(tIndex).GuildInfo.EsGuildLeader = 0
        End If
        Exit Sub
    End If

     */
    
    private void parseNLOGIN(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        String nick = st.nextToken();
        String passwd = st.nextToken();
        st.nextToken(); // cuerpo
        st.nextToken(); // cabeza
        st.nextToken(); // version - FIXME
        String nombre_raza = st.nextToken();
        short raza = this.cliente.indiceRaza(nombre_raza);
        String nombre_genero = st.nextToken();
        short genero = 0;
        if (nombre_genero.equalsIgnoreCase("HOMBRE")) {
            genero = 0;
        } else {
            genero = 1;
        }
        String nombre_clase = st.nextToken();
        CharClass clase = CharClassManager.getInstance().getClase(nombre_clase.toUpperCase());
        byte atribs[] = new byte[5];
        for (int i = 0; i < atribs.length; i++) {
            atribs[i] = Byte.parseByte(st.nextToken());
        }
        byte skills[] = new byte[Constants.MAX_SKILLS + 1]; // 1-based array.
        for (int i = 1; i <= Constants.MAX_SKILLS; i++) {
            skills[i] = Byte.parseByte(st.nextToken());
        }
        String email = st.nextToken();
        String hogar = st.nextToken();
        short ciudad_hogar = this.cliente.indiceCiudad(hogar);
        this.cliente.connectNewUser(nick, passwd, raza, genero, clase, skills, email, ciudad_hogar);
    }
    
    private void parseOLOGIN(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        String nick = st.nextToken();
        String passwd = st.nextToken();
        this.cliente.connectUser(nick, passwd);
    }
    
    private void parseMover(String s) {
        // Comando M
        StringTokenizer st = new StringTokenizer(s, ",");
        short dir = Short.parseShort(st.nextToken());
        if (dir > 0 && dir < 9) {
            this.cliente.mover(dir);
        }
    }
            
    private void parseCHEA(String s) {
        // Comando CHEA
        StringTokenizer st = new StringTokenizer(s, ",");
        short dir = Short.parseShort(st.nextToken());
        this.cliente.changeDir(dir);
    }
    
    private void parseLeftClick(String s) {
        // Clic con el botón primario del mouse
        StringTokenizer st = new StringTokenizer(s, ",");
        short x = Short.parseShort(st.nextToken());
        short y = Short.parseShort(st.nextToken());
        this.cliente.clicIzquierdoMapa(x, y);
    }
    
    private void parseRightClick(String s) {
        // Clic con el botón secundario del mouse
        // Sub Accion(ByVal UserIndex As Integer, ByVal Map As Integer, ByVal X As Integer, ByVal Y As Integer)
        // Viene de ACCIONES.BAS
        StringTokenizer st = new StringTokenizer(s, ",");
        short x = Short.parseShort(st.nextToken());
        short y = Short.parseShort(st.nextToken());
        this.cliente.clicDerechoMapa(x, y);
    }
    
    private void parseUsa(String s) {
        // Comando "USA"
        short slot = Short.parseShort(s);
        this.cliente.usarItem(slot);
    }
    
    private void parseTirarObjeto(String s) {
	    StringTokenizer st = new StringTokenizer(s, ",");
	    short slot = Short.parseShort(st.nextToken());
	    int cant  = Integer.parseInt(st.nextToken());
	    this.cliente.tirarObjeto(slot, cant);
    }
    
    private void parseDesplazarHechizo(String s) {
        // Comando DESPHE
        // Mover Hechizo de lugar
        StringTokenizer st = new StringTokenizer(s, ",");
        short dir = Short.parseShort(st.nextToken());
        short nro_hechizo = Short.parseShort(st.nextToken());
        this.cliente.desplazarHechizo(dir, nro_hechizo);
    }
    
    private void parseOfrecerComerciarUsuario(String s) {
        // Comando OFRECER
        StringTokenizer st = new StringTokenizer(s, ",");
        short slot = Short.parseShort(st.nextToken());
        int cant = Short.parseShort(st.nextToken());
        this.cliente.doOfrecerComerciarUsuario(slot, cant);
    }
    
    private void parseDepositarOro(String s) {
        int cant = Integer.parseInt(s);
        this.cliente.doDepositarOro(cant);
    }

    private void parseRetirar(String s) {
    	int cant = 0;
    	if (!"".equals(s)) {
    		cant = Integer.parseInt(s);
    	}
        this.cliente.doRetirar(cant);
    }
    
    private void parseDepositarBoveda(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        short slot = Short.parseShort(st.nextToken());
        int cant = Short.parseShort(st.nextToken());
        this.cliente.doDepositarBoveda(slot, cant);
    }

    private void parseRetirarBoveda(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        short slot = Short.parseShort(st.nextToken());
        int cant = Short.parseShort(st.nextToken());
        this.cliente.doRetirarBoveda(slot, cant);
    }
    
    private void parseConstruyeHerreria(String s) {
        // Comando CNS
        // Construye herreria
        short objid = Short.parseShort(s);
        this.cliente.doConstruyeHerreria(objid);
    }
    
    private void parseConstruyeCarpinteria(String s) {
        // Comando CNC
        // Construye carpinteria
        short objid = Short.parseShort(s);
        this.cliente.doConstruyeCarpinteria(objid);
    }
    
    private void parsePonerMensajeForo(String s) {
        int sep = s.indexOf((char) 176);
        String titulo = s.substring(0, sep);
        String texto = (s.length() > sep) ? s.substring(sep+1) : "";
        this.cliente.doPonerMensajeForo(titulo, texto);
    }
    
    private void parseSolicitudIngresoClan(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        String guildName = st.nextToken();
        String desc = st.nextToken();
        this.cliente.doSolicitudIngresoClan(guildName, desc);
    }
    
    private void parseApostar(String s) {
        int cant;
        try {
        	cant = Integer.parseInt(s);
        } catch (Exception ex) {
        	cant = -1;
        }
        this.cliente.doApostar(cant);
    }
    
    private void parseEnviarCantidadHostiles(String s) {
        // Comando /NENE 
        // Nro de enemigos en un mapa.
    	s = s.trim();
    	short m;
    	try {
    		m = Short.parseShort(s);
    	} catch (Exception ex) {
    		m = 0;
    	}
        this.cliente.doEnviarCantidadHostiles(m);
    }
    
    private void parseTeleportUsuario(String s) {
        // Comando /TELEP 
        // Teleportar
        StringTokenizer st = new StringTokenizer(s, " ");
        String nombre = "";
        short m = 0;
        short x = 0;
        short y = 0;
        try {
            nombre = st.nextToken();
            m = Short.parseShort(st.nextToken().trim());
            x = Short.parseShort(st.nextToken().trim());
            y = Short.parseShort(st.nextToken().trim());
        } catch (java.util.NoSuchElementException e) {
        	m = -1;
        }
        this.cliente.doTeleportUsuario(nombre, m, x, y);
    }
    
    private void parseBan(String s) {
        String nombre = "";
        String motivo = "";
        if (nombre.indexOf(' ') == -1) {
            nombre = s;
        } else {
            nombre = s.substring(0, s.indexOf(' '));
            motivo = s.substring(s.indexOf(' ') + 1);
        }
        this.cliente.doBan(nombre, motivo);
    }
    
    private void parseSPA(String s) {
        short index = Short.parseShort(s);
        this.cliente.doSPA(index);
    }
    
    private void parseModificarCaracter(String s) {
        StringTokenizer st = new StringTokenizer(s, " ");
        String nick = "";
        String accion = "";
        int valor = 0;
        try {
	        nick = st.nextToken();
	        accion = st.nextToken();
	        valor = Integer.parseInt(st.nextToken());
        } catch (Exception e) {
        	nick = "";
        }
        this.cliente.doModificarCaracter(nick, accion, valor);
    }
    
    private void parseTrigger(String s) {
        if (s.length() > 0) {
	        try {
	            this.cliente.doTrigger(Byte.parseByte(s.trim()));
	        } catch (Exception e) {
	        	//
	        }
        }
    }
    
    private void parseCrearItem(String s) {
    	short objid = Short.parseShort(s.trim());
        this.cliente.doCrearItem(objid);
    }

    private void parseModMapInfo(String s) {
        StringTokenizer st = new StringTokenizer(s, " ");
        String accion = "";
        int valor = 0;
        try {
	        accion = st.nextToken();
	        if (st.hasMoreTokens()) {
				valor = Integer.parseInt(st.nextToken());
			} else {
				valor = -1;
			}
        } catch (Exception e) {
        	// ignorar
        }
        this.cliente.doModMapInfo(accion, valor);
    }
    
    private void parseCrearCriatura(String s) {
        // Crear criatura, toma directamente el indice
        // Comando /ACC indiceNpc
        short indiceNpc = Short.parseShort(s);
        this.cliente.doCrearCriatura(indiceNpc);
    }
    
    private void parseCrearCriaturaRespawn(String s) {
        // Crear criatura con respawn, toma directamente el indice
        // Comando /RACC indiceNpc
        short indiceNpc = Short.parseShort(s);
        this.cliente.doCrearCriaturaRespawn(indiceNpc);
    }
    
    private void parseCrearTeleport(String s) {
        // Comando /CT mapa_dest x_dest y_dest
        // Crear Teleport
        StringTokenizer st = new StringTokenizer(s, " ");
        short dest_mapa = Short.parseShort(st.nextToken());
        short dest_x = Short.parseShort(st.nextToken());
        short dest_y = Short.parseShort(st.nextToken());
        this.cliente.doCrearTeleport(dest_mapa, dest_x, dest_y);
    }

    private void parseArmaduraImperial1(String s) {
    	short armadura = "".equals(s) ? -1 : Short.parseShort(s);
        this.cliente.doArmaduraImperial1(armadura);
    }
    
    private void parseArmaduraImperial2(String s) {
    	short armadura = "".equals(s) ? -1 : Short.parseShort(s);
        this.cliente.doArmaduraImperial2(armadura);
    }
    
    private void parseArmaduraImperial3(String s) {
    	short armadura = "".equals(s) ? -1 : Short.parseShort(s);
        this.cliente.doArmaduraImperial3(armadura);
    }
    
    private void parseArmaduraImperial4(String s) {
    	short armadura = "".equals(s) ? -1 : Short.parseShort(s);
        this.cliente.doArmaduraImperial4(armadura);
    }
    
    private void parseArmaduraImperial5(String s) {
    	short armadura = "".equals(s) ? -1 : Short.parseShort(s);
        this.cliente.doArmaduraImperial5(armadura);
    }
    
    private void parseArmaduraCaos1(String s) {
    	short armadura = "".equals(s) ? -1 : Short.parseShort(s);
        this.cliente.doArmaduraCaos1(armadura);
    }
    
    private void parseArmaduraCaos2(String s) {
    	short armadura = "".equals(s) ? -1 : Short.parseShort(s);
        this.cliente.doArmaduraCaos2(armadura);
    }
    
    private void parseArmaduraCaos3(String s) {
    	short armadura = "".equals(s) ? -1 : Short.parseShort(s);
        this.cliente.doArmaduraCaos3(armadura);
    }
    
    private void parseArmaduraCaos4(String s) {
    	short armadura = "".equals(s) ? -1 : Short.parseShort(s);
        this.cliente.doArmaduraCaos4(armadura);
    }
    
    private void parseArmaduraCaos5(String s) {
    	short armadura = "".equals(s) ? -1 : Short.parseShort(s);
        this.cliente.doArmaduraCaos5(armadura);
    }
}
