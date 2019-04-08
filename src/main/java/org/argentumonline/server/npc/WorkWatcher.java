/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia «gorlok» 
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.argentumonline.server.npc;

import java.text.SimpleDateFormat;

import org.argentumonline.server.Constants;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.map.Map;
import org.argentumonline.server.protocol.ConsoleMsgResponse;
import org.argentumonline.server.protocol.CreateFXResponse;
import org.argentumonline.server.user.User;
import org.argentumonline.server.user.UserStorage;
import org.argentumonline.server.util.Color;
import org.argentumonline.server.util.FontType;
import org.argentumonline.server.util.Log;
import org.argentumonline.server.util.Util;

public class WorkWatcher {
	
	final static int NPC_CENTINELA_TIERRA = 16;  // Índice del NPC en el .dat

	final static int NPC_CENTINELA_AGUA = 16; // Ídem anterior, pero en mapas de agua
	
	final static int TIEMPO_INICIAL = 2; // Tiempo inicial en minutos. No reducir sin antes revisar el timer que maneja estos datos.
	
	
	private boolean activated = false;
	
	private User userWatching;

	private int askingCode;
	
	private long spawnTime;
	
	private int userRemainingMinutes;
	
	private int secondsToCallUserAttention;

	private Npc workWatcher;
	
	private GameServer server;
	
	public WorkWatcher(GameServer server) {
		this.server = server;
	}
	
	public boolean watchingUser(String userName) {
		return userWatching != null 
				&& userWatching.getUserName().equalsIgnoreCase(userName);
	}
	
	public Npc getNpc() {
		return workWatcher;
	}
	
	private void callUserAttention() {
		// ############################################################
		// Makes noise and FX to call the user's attention.
		// ############################################################
		if (System.currentTimeMillis() - spawnTime >= 5000) {
	        if (userWatching != null && activated) {
	        	Map map = server.getMap(workWatcher.pos().map);
	            if (!userWatching.getFlags().workWatcherRepliedOK) {
	            	map.sendPlayWave(Constants.SOUND_WARP, workWatcher.pos().x, workWatcher.pos().y);
	            	userWatching.sendPacket(new CreateFXResponse(workWatcher.getId(), (short)Constants.FXWARP, (short)0));
	                
	                // Resend the key
	                sendCode(userWatching);
	            }
	        }
		}
	}

	private void goToNextWorkingUser() {
		// ############################################################
		// Va al siguiente usuario que se encuentre trabajando
		// ############################################################
		server.getUsers().stream().forEach(p -> {
			if (p.isLogged() && p.isWorking() && !p.isGM()) {
				if (!p.getFlags().workWatcherRepliedOK) {
	                // Inicializamos
	                userWatching = p;
	                userRemainingMinutes = TIEMPO_INICIAL;
	                askingCode = Util.random(1, 32000);
	                spawnTime = System.currentTimeMillis();
	                secondsToCallUserAttention = 0;
	                
	                // Ponemos al centinela en posición
	                warpWorkWatcher(p);
	                
	                if (workWatcher != null) {
	                    // Mandamos el mensaje (el centinela habla y aparece en consola para que no haya dudas)
	                	workWatcher.talkToUser(p, "Saludos " + p.getUserName() 
	                		+ ", soy el Centinela de estas tierras. Me gustaría que escribas /CENTINELA " 
	                		+ askingCode + " en no más de dos minutos.", Color.COLOR_VERDE);
	                    p.sendMessage("El centinela intenta llamar tu atención. ¡Respóndele rápido!", FontType.FONTTYPE_CENTINELA);
	                }
	                return;
				}
			}
		});
	    
	    // No hay chars trabajando, eliminamos el NPC si todavía estaba en algún lado y esperamos otro minuto
	    if (workWatcher != null) {
	    	workWatcher.quitarNPC();
	        workWatcher= null;
	    }
	    
	    // No estamos revisando a nadie
	    userWatching = null;
	}

	private void finalCheck() {
		// ############################################################
		// Al finalizar el tiempo, se retira y realiza la acción
		// pertinente dependiendo del caso
		// ############################################################
	    
		String userName;
	    
	    if (!userWatching.getFlags().workWatcherRepliedOK) {
	        // Logueamos el evento
	        Log.logCentinela("Centinela baneo a " + userWatching.getUserName() + " por uso de macro inasistido.");
	        
	        // Ponemos el ban
	        userWatching.banned = true;
	        
	        userName = userWatching.getUserName();
	        
	        // Avisamos a los admins
	        server.sendToAdmins(new ConsoleMsgResponse("Servidor> El centinela ha baneado a " + userName, FontType.FONTTYPE_SERVER.id()));
	        
	        // Echamos al usuario
	        userWatching.quitGame();
	        
	        // ban y ponemos la pena
	        var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        String reason = "CENTINELA : BAN POR MACRO INASISTIDO " + sdf.format(new java.util.Date());
	        UserStorage.banUser(userName, "CENTINELA", reason);
			UserStorage.addPunishment(userName, reason);
	    }
	    
	    askingCode = 0;
	    userRemainingMinutes = 0;
	    userWatching = null;
	    
	    if (workWatcher != null) {
	    	workWatcher.quitarNPC();
	        workWatcher = null;
	    }
	}

	public void checkCode(User user, int enteredCode) {
		// ############################################################
		// Corrobora la clave que le envia el usuario
		// ############################################################
	    if (enteredCode == this.askingCode && user == userWatching) {
	        userWatching.getFlags().workWatcherRepliedOK = true;
	        workWatcher.talkToUser(userWatching, "¡Muchas gracias " + userWatching.getUserName() 
	        	+ "! Espero no haber sido una molestia", Color.COLOR_BLANCO);
	        userWatching = null;
	    } else {
	        sendCode(user);
	        
	        // Logueamos el evento
	        if (user != userWatching) {
	            Log.logCentinela("El usuario " + user.getUserName() + " respondió aunque no se le hablaba a él.");
	        } else {
	        	Log.logCentinela("El usuario " + user.getUserName() + " respondió una clave incorrecta: " 
	        			+ enteredCode + " - Se esperaba : " + askingCode);
	        }
	    }
	}
	
	public void reset() {
		// ############################################################
		// Reset del Centinela
		// ############################################################
		server.getUsers().stream().forEach(p -> {
			if (p.isLogged() && !p.getUserName().isBlank() && p != userWatching) {
				p.getFlags().workWatcherRepliedOK = false;
			}
		});
	}
	
	public void sendCode(User user) {
		// ############################################################
		// Enviamos al usuario la clave vía el personaje centinela
		// ############################################################
		
		if (workWatcher == null) {
			return;
		}
	    
	    if (user == userWatching) {
	    	if (!user.getFlags().workWatcherRepliedOK) {
	    		workWatcher.talkToUser(user, "¡Te he dicho que escribas /CENTINELA " + askingCode + ", escríbelo rápido!", Color.COLOR_VERDE);
	            user.sendMessage("El centinela está llamando tu atención. ¡Respóndele sin demora!", FontType.FONTTYPE_CENTINELA);
	    	} else {
	            // Logueamos el evento
	            Log.logCentinela("El usuario " + userWatching.getUserName() + " respondió más de una vez la contraseña correcta.");
	            workWatcher.talkToUser(user, "Te agradezco, pero ya me has respondido. Me retiraré pronto.", Color.COLOR_VERDE);
	    	}
	    } else {
	    	workWatcher.talkToUser(user, "No es a ti a quien estoy hablando, ¿no ves?", Color.COLOR_BLANCO);
	    }
	}
	
	public void passSecond() {
	    if (!activated) {
	    	return;
	    }
		secondsToCallUserAttention++;
		if (secondsToCallUserAttention >= 5) {
			secondsToCallUserAttention = 0;
			callUserAttention();
		}
	}

	public void passMinute() {
		// ############################################################
		// Control del timer. Llamado cada un minuto.
		// ############################################################
	    if (!activated) {
	    	return;
	    }
	    
	    if (userWatching == null) {
	        goToNextWorkingUser();
	    } else {
	        userRemainingMinutes = userRemainingMinutes - 1; // one minute
	        
	        if (userRemainingMinutes <= 0) {
	            finalCheck();
	            goToNextWorkingUser();
	        } else {
	            // Recordamos al user que debe escribir
	            if (workWatcher.pos().distance(userWatching.pos()) > 5) {
	                warpWorkWatcher(userWatching);
	            }
	            
	            // El centinela habla y se manda a consola para que no quepan dudas
	            workWatcher.talkToUser(userWatching, "¡" + userWatching.getUserName() + 
	            		", tienes un minuto más para responder! Debes escribir /CENTINELA " + askingCode + ".", 
	            		Color.COLOR_ROJO);
	            userWatching.sendMessage("¡" + userWatching.getUserName() + ", tienes un minuto más para responder!", 
	            		FontType.FONTTYPE_CENTINELA);
	        }
	    }
	}
	
	private void warpWorkWatcher(User user) {
		// ############################################################
		// Inciamos la revisión del usuario UserIndex
		// ############################################################
	    // Evitamos conflictos de índices
	    if (workWatcher != null) {
	    	workWatcher.quitarNPC();
	    }
	    
	    Map map = server.getMap(user.pos().map);
	    if (map != null) {
		    if (map.isWater(user.pos().x,  user.pos().y)) {
		        workWatcher = Npc.spawnNpc(NPC_CENTINELA_AGUA, user.pos(), true, false);
		    } else {
		    	workWatcher = Npc.spawnNpc(NPC_CENTINELA_TIERRA, user.pos(), true, false);
		    }
	    }
	}


	public void userLogout(User user) {
		// ############################################################
		// El usuario al que revisabamos se desconectó
		// ############################################################
	    if (userWatching != null && userWatching == user) {
	        // Logueamos el evento
	    	Log.logCentinela("El usuario " + userWatching.getUserName() + " se desconectó al solicitarle la clave");
	        
	        // Reseteamos y esperamos a otro PasarMinuto para ir al siguiente user
	        askingCode = 0;
	        userRemainingMinutes = 0;
	        userWatching = null;
	        
	        if (workWatcher != null) {
	        	workWatcher.quitarNPC();
	        }
	    }
	}

	public void workWatcherActivateToggle(User admin) {
		// Command /CENTINELAACTIVADO
		if (!admin.isGod() && !admin.isAdmin()) {
			return;
		}
		
		this.activated = !this.activated;
		
        this.userWatching = null;
        this.askingCode = 0;
        this.userRemainingMinutes = 0;
	
        if (this.workWatcher != null) {
	        this.workWatcher.quitarNPC();
	        this.workWatcher = null;
        }
	    
	    if (this.activated) {
	    	server.sendToAdmins(new ConsoleMsgResponse("El centinela ha sido activado.", FontType.FONTTYPE_SERVER.id()));
	    } else {
	    	server.sendToAdmins(new ConsoleMsgResponse("El centinela ha sido desactivado.", FontType.FONTTYPE_SERVER.id()));
	    }
	}

}
