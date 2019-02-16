package org.ArgentumOnline.server.protocol;

import java.util.LinkedList;
import java.util.List;

import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.classes.CharClass;
import org.ArgentumOnline.server.classes.CharClassManager;
import org.ArgentumOnline.server.map.MapPos.Direction;
import org.ArgentumOnline.server.util.BytesReader;
import org.ArgentumOnline.server.util.NotEnoughDataException;

/**
 *
 * @author: JAO (Juan Agustín Oliva)
 * @userforos: Agushh, Thorkes Clase destinada a manejar cuestiones del
 *             protocolo en otro thread
 */
public class ClientProcessThread extends Thread {

	private BytesReader r;

	List<Client> clientQueue = new LinkedList<Client>();
	List<byte[]> colaProcesos = new LinkedList<byte[]>();

	public ClientProcessThread() {
		r = new BytesReader();
		r.setLittleEndian(true);
	}

	public void addClientQueue(Client value) {
		this.clientQueue.add(value);
	}

	//Una vez decodificado, buscamos qué acción efectuar
	private void handleClientData(byte[] data, Client cliente, int length) {
		r.appendBytes(data);
		try {
			while (r.getPos() < length) {
				r.mark();
				ClientPacketID.ID packet = ClientPacketID.ID.values()[r.readByte()];
				switch (packet) {

				case logged: // logged
					String name = r.readString();
					String password = r.readString();

					if (name.length() == 0 || password.length() == 0) {
						// FIXME
						// cliente.enviar(serverPacketID.test, "Nombre o contraseña incompletos");
						break;
					}
					cliente.connectUser(name, password);
					break;

				case move: // move
					short dir = r.readShort();
					cliente.mover(Direction.value(dir));
					break;

				case talk: // talk
					String texto = r.readString();
					cliente.doHablar(texto);
					break;

				case LC: // Left click
					short x = r.readShort();
					short y = r.readShort();
					cliente.clicIzquierdoMapa(x, y);
					break;

				case RC: // Right click
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

				case cDir: // change dir
					dir = r.readShort();
					if (dir > 0)
						cliente.changeDir(dir);
					break;

				case dirSP:
					dir = r.readShort();
					short nroSpell = r.readShort();
					cliente.desplazarHechizo(dir, nroSpell);
					break;

				case LH:
					nroSpell = r.readShort();
					// dir = r.readShort();
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
					cliente.tirarObjeto((short) slot, (int) amount);
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
					cliente.m_comUsu.doFinComerciarUsuario(cliente);
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
					// cliente.enviar(ClientMessage.MSG_ERR, "El paquete nro " + msg_id + " no
					// existe.");
					System.out.println("ERROR: El paquete id " + packet + " no existe.");
					break;
				}
			}

			r.reset();
			r.clear();

		} catch (NotEnoughDataException ex) {
			r.reset();
		}
	}

	boolean actived = true;
	public void endThread() {
		this.actived = false;
	}

	@Override
	public void run() {

		while (actived) {
			try {
				Thread.sleep(1); // pausa de unos ms...
			} catch (Exception e) {}

			if (this.clientQueue.size() > 0) { 
				// hay alguien esperando ??
				Client client = this.clientQueue.get(0);
				client.colaClient.flip();

				handleClientData(client.colaClient.array(), 
						client,
						client.lengthClient.get(0));

				client.colaClient.clear();
				client.lengthClient.remove(0);
				this.clientQueue.remove(0);
			}

		}
	}

}
