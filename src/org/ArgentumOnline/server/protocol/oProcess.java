
package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.util.BytesReader;

import org.ArgentumOnline.server.util.NotEnoughDataException;
import org.ArgentumOnline.server.util.IniFile;
import org.ArgentumOnline.server.util.Util;
import org.ArgentumOnline.server.AojServer;
import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.classes.CharClass;
import org.ArgentumOnline.server.classes.CharClassManager;

import java.util.LinkedList;

/**
*
* @author: JAO (Juan Agustín Oliva)
* @userforos: Agushh, Thorkes
* Clase destinada a manejar cuestiones del protocolo en otro thread
*/

public class oProcess extends Thread {
	
	private BytesReader r;
	AojServer servidor = new AojServer();
	
	LinkedList <Client> clientQueue = new LinkedList();

    LinkedList colaProcesos = new LinkedList();
    
    public void addStatusQueueClient(Client cliente, byte[] bytes, int length) {
    	cliente.lengthClient.add(length);
    	cliente.colaClient.put(bytes);
    }
    
    public void removeCola() {
    	this.colaProcesos.remove(0);
    }

    public LinkedList getColaProcesos() {
        return this.colaProcesos;
    }

    public void clearColaProcesos() {
        this.colaProcesos.clear();
    }

    public void addIntegerQueue(int value){
        this.colaProcesos.add(value);
    }

    public Object getIntegerQueue() {return this.colaProcesos.get(0);}

    public void deleteIntegerQueue(int value){
        this.colaProcesos.remove(value);
    }

    public void addByteQueue(byte[] value){
        this.colaProcesos.add(value);
    }

    public Object getByteQueue(int value) {return this.colaProcesos.get(value);}

    public void deleteByteQueue(int value){
        this.colaProcesos.remove(value);
    }

    public void addClientQueue(Client value){
        this.clientQueue.add(value);
    }

    public Object getClientQueue(int value) {return this.clientQueue.get(value);}

    public void deleteClientQueue(int value){
        this.clientQueue.remove(value);
    }

    public int getProcessQueue() {return this.clientQueue.size();}

    public void setearVariables(AojServer servidor){
          this.servidor = servidor;

          r = new BytesReader();
          r.setLittleEndian(true);

    }

    //Con el decodeData decodificamos los datos recibidos.
public boolean decodeData(byte[] data, int length, Client cliente){
    int msg_offset = 0;
    //tenemos en cuenta que ya deducimos el msg_id
    msg_offset ++;

    byte msg_id = 0;
    byte[] msg_data = new byte[length];

    msg_id = data[0];

    for (int i = 1; i < length; i++) {
        msg_data[i] = data[i];
        msg_offset++;
    }

    if (msg_offset == length) {
        return true;
    } else {
        return false;
    }

}

//Una vez decodificado, buscamos qué acción efectuar
public void handleData(byte[] data, Client cliente, int length){
    boolean broken = false;

    r.appendBytes(data);

    try {
  		
    	while (r.getPos() < length) {
  			r.mark();
  			
  			clientPacketID.ID packet = clientPacketID.ID.values()[r.readByte()];
  		
  		switch (packet) {
  		
  		case logged: // logged
  			
  			String name = r.readString();
  			String password = r.readString();
  			
  			if (name.length() == 0 || password.length() == 0) {
  			//	cliente.enviar(serverPacketID.test, "Nombre o contraseña incompletos");
  				break;
  			}
  			
  			cliente.connectUser(name, password);
  			
  			break;
  		
  		case move: //move
  			short dir = r.readShort();
  			
  			cliente.mover(dir);
  			
  			break;
  		
  		case talk: //talk
  			String texto = r.readString();

  			cliente.doHablar(texto);
  			
  			break;
  			
  		case LC: //Left click
  			short x = r.readShort();
  			short y = r.readShort();
  			
  			cliente.clicIzquierdoMapa(x, y);
  			
  			break;
  			
  		case RC: //Right click
  			x = r.readShort();
  			y = r.readShort();
  			
  			cliente.clicDerechoMapa(x, y);
  			
  			break;
  			
  		case WLC:// WLC
  			x = r.readShort();
  			y = r.readShort();
  			
  			short usingskill = r.readShort();
  			
  			cliente.doWLC(x, y, usingskill);
  			
  			break;
  			
  		case cDir: //change dir
  			
  			dir = r.readShort();
  			
  			if (dir > 0 ) cliente.changeDir(dir);
  			
  			break;
  			
  		case dirSP:
  			
  			dir = r.readShort();
  			short nroSpell = r.readShort();
  			
  			cliente.desplazarHechizo(dir, nroSpell);
  			
  			break;
  			
  		case LH:
  			
  			nroSpell = r.readShort();
  			//dir = r.readShort();
  			cliente.doLanzarHechizo(nroSpell);
  			cliente.doUK((short) 2);
  			
  			break;
  			
  		case doUK:
  			dir = r.readShort();
  			
  			cliente.doUK(dir);
  			
  			break;
  			
  		case pickUp:
  			cliente.agarrarObjeto();
  			break;
  			
  		case Drop:
  			int slot = r.readByte();
  			short amount = r.readShort();
  			
  			cliente.tirarObjeto((short)slot, (int) amount);
  			
  			break;
  			
  		case Equip:
  			slot = r.readByte();
  			
  			cliente.equiparObjeto((short) slot);
  			
  			break;
  			
  		case Attack:
  			
  			cliente.doAtacar();
  			
  			break;
  			
  		case useItem:
  			
  			cliente.usarItem((byte) r.readByte());
  		
  		    break;
  		
  		case endGame:
  		
  			cliente.cerrarUsuario();
  			
  			break;
  			
  		case commerceStart:
  			
  			cliente.doComerciar();
  			
  			break;
  			
  		case commerceEnd:
  			
  			cliente.doFinComerciarUsuario();
  			
  			break;
  			
  		case commerceBuy:
  			
  			cliente.doComprar((byte) r.readByte(), r.readShort());
  			
  			break;
  			
  		case commerceSell:
  			
  			cliente.doVender((byte) r.readByte(), r.readShort());
  			
  			break;
  			
  		case meditate:
  			
  			cliente.doMeditar();
  			
  			break;
  			
  		case refreshPos:
  			
  			cliente.enviarPU();
  			
  			break;
  			
  		case createCharacter:
  			String nick = r.readString();
  			String pass = r.readString();
  			short raza = cliente.indiceRaza(r.readString());
  			short genero = r.readShort();
  			CharClass clase = CharClassManager.getInstance().getClase(r.readString().toUpperCase());
  			String email = r.readString();
  			short hogar = cliente.indiceCiudad(r.readString());
  			
  			cliente.connectNewUser(nick, pass, raza, genero, clase, email, hogar);
  			
  			break;
  			
  		case throwDices:
  			
  			cliente.tirarDados();
  			
  			break;
  			
  		case reciveUserStats:
  			
  			cliente.doEnviarMiniEstadisticas();
  			
  			break;
  			
  		case assignSkills:
  			
  			cliente.userAsignaSkill(r.readInt(), r.readByte());
  			
  			break;
  			
  		case doBank:
  			
  			cliente.doBoveda();
  			
  			break;
  			
  		case finBank:
  			
  			cliente.doFinBanco();
  			
  			break;
  			
  		case sellBank:
  			
  			cliente.doDepositarBoveda(r.readShort(), r.readInt());
  			
  			break;
  			
  		case buyBank:
  			
  			cliente.doRetirarBoveda(r.readShort(), r.readInt());
  			
  			break;
  			
  		case safeToggle:
  			
  			cliente.cambiarSeguro();
  			
  			break;
  			
  		default:
  			
  			//cliente.enviar(ClientMessage.MSG_ERR, "El paquete nro " + msg_id + " no existe.");
  			broken = true;
  			break;
  		}
  		
  		}
  		
    	r.reset();
  		r.clear();

    } catch (NotEnoughDataException ex) {
        r.reset();

    }

}

@Override
public void run() {
    boolean actived = true;

    while (actived) {

try {
     Thread.sleep(1); //pausa de unos ms...
} catch (Exception e) {
	 System.out.println("Error pausando thread");
}
            
            if (this.clientQueue.size() > 0) { // hay alguien esperando ??
            	
            	    this.clientQueue.getFirst().colaClient.flip();
                
                    handleData(this.clientQueue.getFirst().colaClient.array(), this.clientQueue.getFirst(),
                    		this.clientQueue.getFirst().lengthClient.getFirst());
            	    
                    this.clientQueue.getFirst().colaClient.clear();
            	    this.clientQueue.getFirst().lengthClient.removeFirst();
            	    this.clientQueue.removeFirst();

            }
            
        
    }
}


}


