package org.ArgentumOnline.server.net;

import java.util.LinkedList;
import java.util.List;

import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.Skill;
import org.ArgentumOnline.server.classes.CharClass;
import org.ArgentumOnline.server.classes.CharClassManager;
import org.ArgentumOnline.server.map.MapPos.Direction;
import org.ArgentumOnline.server.protocol.ClientPacketID;
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

	List<Player> clientQueue = new LinkedList<Player>();

	public ClientProcessThread() {
		setName("ClientProcessThread");
		r = new BytesReader();
		r.setLittleEndian(true);
	}

	public void addClientQueue(Player value) {
		this.clientQueue.add(value);
	}

	//Una vez decodificado, buscamos qué acción efectuar
	private void handleClientData(byte[] data, Player cliente, int length) {
		r.appendBytes(data);
		try {
			while (r.getPos() < length) {
				r.mark();
				ClientPacketID packet = ClientPacketID.values()[r.readByte()];
				switch (packet) {

				case LoginExistingChar: // logged
					String name = r.readString();
					String password = r.readString();

					if (name.length() == 0 || password.length() == 0) {
						// FIXME
						// cliente.enviar(serverPacketID.test, "Nombre o contraseña incompletos");
						break;
					}
					cliente.connectUser(name, password);
					break;

				case Walk: // move
					byte dir = (byte)r.readByte();
					cliente.mover(Direction.value(dir));
					break;

				case Talk: // talk
					String texto = r.readString();
					cliente.doHablar(texto);
					break;

				case LeftClick: // Left click
					byte x = (byte)r.readByte();
					byte y = (byte)r.readByte();
					cliente.clicIzquierdoMapa(x, y);
					break;

				case DoubleClick: // Right click / Doble clic ?
					x = (byte)r.readByte();
					y = (byte)r.readByte();
					cliente.clicDerechoMapa(x, y);
					break;

				case WorkLeftClick:// WLC
					x = (byte)r.readByte();
					y = (byte)r.readByte();
					short usingskill = r.readShort();
					cliente.doWLC(x, y, usingskill);
					break;

				case ChangeHeading: // change heading
					dir = (byte)r.readByte();
					if (dir > 0)
						cliente.changeHeading(dir);
					break;

				case MoveSpell:
					dir = (byte)r.readByte();
					short nroSpell = r.readShort();
					cliente.moveSpell(dir, nroSpell);
					break;

				case CastSpell:
					nroSpell = (byte)r.readByte();
					cliente.doLanzarHechizo(nroSpell);
					cliente.doUK(Skill.SKILL_Magia);
					break;

				case Work:
					byte skill = (byte)r.readByte();
					cliente.doUK(skill);
					break;

				case PickUp:
					cliente.agarrarObjeto();
					break;

				case Drop:
					int slot = r.readByte();
					short amount = r.readShort();
					cliente.tirarObjeto((short) slot, (int) amount);
					break;

				case EquipItem:
					slot = r.readByte();
					cliente.equiparObjeto((short) slot);
					break;

				case Attack:
					cliente.doAtacar();
					break;

				case UseItem:
					cliente.usarItem((byte) r.readByte());
					break;

				case Quit:
					cliente.cerrarUsuario();
					break;

				case CommerceStart:
					cliente.doComerciar();
					break;

				case UserCommerceEnd:
					cliente.m_comUsu.doFinComerciarUsuario(cliente);
					break;

				case CommerceBuy:
					cliente.doComprar((byte) r.readByte(), r.readShort());
					break;

				case CommerceSell:
					cliente.doVender((byte) r.readByte(), r.readShort());
					break;

				case Meditate:
					cliente.doMeditar();
					break;

				case RequestPositionUpdate:
					cliente.enviarPU();
					break;

				case LoginNewChar:
					String nick = r.readString();
					String pass = r.readString();
					short raza = cliente.indiceRaza(r.readString());
					short genero = r.readShort();
					CharClass clase = CharClassManager.getInstance().getClase(r.readString().toUpperCase());
					String email = r.readString();
					short hogar = cliente.indiceCiudad(r.readString());
					cliente.connectNewUser(nick, pass, raza, genero, clase, email, hogar);
					break;

				case ThrowDices:
					cliente.tirarDados();
					break;

				case RequestMiniStats:
					cliente.doEnviarMiniEstadisticas();
					break;

				case RequestSkills:
					cliente.userAsignaSkill(r.readInt(), r.readByte());
					break;

				case BankStart:
					cliente.doBoveda();
					break;

				case BankEnd:
					cliente.doFinBanco();
					break;

				case BankDeposit:
					cliente.doDepositarBoveda(r.readShort(), r.readInt());
					break;

				case BankExtractItem:
					cliente.doRetirarBoveda(r.readShort(), r.readInt());
					break;

				case SafeToggle:
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
				Thread.sleep(1000); // 1 sec
			} catch (Exception e) {}

			if (this.clientQueue.size() > 0) { 
				// hay alguien esperando ??
				Player client = this.clientQueue.get(0);
				client.readBuffer.flip();

				handleClientData(client.readBuffer.array(), 
						client,
						client.bufferLengths.get(0));

				client.readBuffer.clear();
				client.bufferLengths.remove(0);
				this.clientQueue.remove(0);
			}

		}
	}

}
