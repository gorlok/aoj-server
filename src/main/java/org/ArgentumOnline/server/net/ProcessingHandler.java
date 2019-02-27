package org.ArgentumOnline.server.net;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.map.MapPos.Heading;
import org.ArgentumOnline.server.protocol.BankDepositGoldRequest;
import org.ArgentumOnline.server.protocol.BankDepositRequest;
import org.ArgentumOnline.server.protocol.BankExtractGoldRequest;
import org.ArgentumOnline.server.protocol.BankExtractItemRequest;
import org.ArgentumOnline.server.protocol.CastSpellRequest;
import org.ArgentumOnline.server.protocol.ChangeHeadingRequest;
import org.ArgentumOnline.server.protocol.CommerceBuyRequest;
import org.ArgentumOnline.server.protocol.CommerceSellRequest;
import org.ArgentumOnline.server.protocol.DoubleClickRequest;
import org.ArgentumOnline.server.protocol.DropRequest;
import org.ArgentumOnline.server.protocol.EquipItemRequest;
import org.ArgentumOnline.server.protocol.LeftClickRequest;
import org.ArgentumOnline.server.protocol.LoginExistingCharRequest;
import org.ArgentumOnline.server.protocol.LoginNewCharRequest;
import org.ArgentumOnline.server.protocol.MoveSpellRequest;
import org.ArgentumOnline.server.protocol.TalkRequest;
import org.ArgentumOnline.server.protocol.UseItemRequest;
import org.ArgentumOnline.server.protocol.WalkRequest;
import org.ArgentumOnline.server.protocol.WorkLeftClickRequest;
import org.ArgentumOnline.server.protocol.WorkRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class ProcessingHandler extends ChannelInboundHandlerAdapter {
	private static Logger log = LogManager.getLogger();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
		var server = GameServer.instance();
		var player = server.findClient(ctx.channel());

		// TODO
		Gson gson = new Gson();
		log.debug("processing handler " + packet.getClass().getCanonicalName());
		System.out.println("<<< procesando paquete " + packet.getClass().getCanonicalName() + " " + gson.toJson(packet));
		
		switch (((ClientPacket)packet).id()) {
		case LoginExistingChar:
			handleLoginExistingChar((LoginExistingCharRequest)packet, player);
			break;
			
		case Walk:
			handleWalk((WalkRequest)packet, player);
			break;
			
		case Talk:
			handleTalk((TalkRequest)packet, player);
			break;

		case LeftClick:
			handleLeftClick((LeftClickRequest)packet, player);
			break;

		case DoubleClick:
			handleDoubleClick((DoubleClickRequest)packet, player);
			break;

		case WorkLeftClick:
			handleWorkLeftClick((WorkLeftClickRequest)packet, player);
			break;

		case ChangeHeading:
			handleChangeHeading((ChangeHeadingRequest)packet, player);
			break;

		case MoveSpell:
			handleMoveSpell((MoveSpellRequest)packet, player);
			break;
			
		case CastSpell:
			handleCastSpell((CastSpellRequest)packet, player);
			break;

		case Work:
			handleWork((WorkRequest)packet, player);
			break;

		case PickUp: // pikachu :P
			player.pickUpObject();
			break;

		case Drop:
			handleDrop((DropRequest)packet, player);
			break;

		case EquipItem:
			handleEquipItem((EquipItemRequest)packet, player);
			break;

		case Attack:
			player.atack();
			break;
			
		case UseItem:
			player.useItem(((UseItemRequest)packet).slot);
			break;
			
		case Quit:
			player.quitGame();
			break;
			
		case CommerceStart:
			player.commerceStart();
			break;
			
		case UserCommerceEnd:
			player.m_comUsu.commerceEnd(player);
			break;
			
		case CommerceBuy:
			handleCommerceBuy((CommerceBuyRequest)packet, player);
			break;
			
		case CommerceSell:
			handleCommerceSell((CommerceSellRequest)packet, player);
			break;

		case Meditate:
			player.meditate();
			break;
			
		case RequestPositionUpdate:
			player.sendPositionUpdate();
			break;

		case LoginNewChar:
			handleLoginNewChar((LoginNewCharRequest)packet, player);
			break;

		case ThrowDices:
			player.throwDices();
			break;
			
		case RequestMiniStats:
			player.sendMiniStats();
			break;
			
		case RequestSkills:
			player.sendSkills();
			break;
			
		case BankStart:
			player.bankStart();
			break;

		case BankEnd:
			player.bankEnd();
			break;

		case BankDeposit:
			handleBankDepositItem((BankDepositRequest)packet, player);
			break;

		case BankExtractItem:
			handleBankExtractItem((BankExtractItemRequest)packet, player);
			break;

		case BankDepositGold:
			player.bankDepositGold(((BankDepositGoldRequest)packet).amount);
			break;
			
		case BankExtractGold:
			player.bankExtractGold(((BankExtractGoldRequest)packet).amount);
			break;
			
		case SafeToggle:
			player.safeToggle();
			break;
			
		default:
			System.out.println("WARNING!!!! UNHANDLED PACKET: " + packet.getClass().getCanonicalName());
		}
		
	}

	private void handleBankExtractItem(BankExtractItemRequest packet, Player player) {
		player.bankExtractItem(packet.slot, packet.amount);
	}

	private void handleBankDepositItem(BankDepositRequest packet, Player player) {
		player.bankDepositItem(packet.slot, packet.amount);
	}

	private void handleLoginNewChar(LoginNewCharRequest packet, Player player) {
		player.connectNewUser(packet.userName, packet.email, packet.race, 
				packet.gender, packet.clazz, packet.email, packet.homeland);
	}

	private void handleCommerceSell(CommerceSellRequest packet, Player player) {
		player.commerceSell(packet.slot, packet.amount);		
	}

	private void handleCommerceBuy(CommerceBuyRequest packet, Player player) {
		player.commerceBuy(packet.slot, packet.amount);
	}

	private void handleEquipItem(EquipItemRequest packet, Player player) {
		player.equipItem(packet.itemSlot);
	}

	private void handleDrop(DropRequest packet, Player player) {
		player.dropObject(packet.slot, packet.amount);
	}

	private void handleWork(WorkRequest packet, Player player) {
		player.handleWork(packet.skill);
	}

	private void handleCastSpell(CastSpellRequest packet, Player player) {
		player.castSpell(packet.spell);
	}

	private void handleMoveSpell(MoveSpellRequest packet, Player player) {
		player.moveSpell(packet.dir, packet.spell);
	}

	private void handleChangeHeading(ChangeHeadingRequest packet, Player player) {
		player.changeHeading(packet.heading);
	}

	private void handleWorkLeftClick(WorkLeftClickRequest packet, Player player) {
		player.workLeftClick(packet.x, packet.y, packet.skill);
	}

	private void handleDoubleClick(DoubleClickRequest packet, Player player) {
		player.clicDerechoMapa(packet.x, packet.y);
	}

	private void handleLeftClick(LeftClickRequest packet, Player player) {
		player.leftClickOnMap(packet.x, packet.y);		
	}

	private void handleWalk(WalkRequest packet, Player player) {
		player.walk(Heading.value(packet.heading));
	}
	
	private void handleTalk(TalkRequest packet, Player player) {
		player.talk(packet.chat);
	}

	private void handleLoginExistingChar(LoginExistingCharRequest packet, Player player) {
		player.connectUser(packet.userName, packet.password);
	}
	
}