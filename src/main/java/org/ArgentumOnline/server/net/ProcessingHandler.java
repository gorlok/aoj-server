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
package org.ArgentumOnline.server.net;

import java.util.Optional;

import org.ArgentumOnline.server.GameServer;
import org.ArgentumOnline.server.map.Heading;
import org.ArgentumOnline.server.protocol.BankDepositGoldRequest;
import org.ArgentumOnline.server.protocol.BankDepositRequest;
import org.ArgentumOnline.server.protocol.BankExtractGoldRequest;
import org.ArgentumOnline.server.protocol.BankExtractItemRequest;
import org.ArgentumOnline.server.protocol.CastSpellRequest;
import org.ArgentumOnline.server.protocol.ChangeHeadingRequest;
import org.ArgentumOnline.server.protocol.ChangePasswordRequest;
import org.ArgentumOnline.server.protocol.ChaosLegionMessageRequest;
import org.ArgentumOnline.server.protocol.ChatColorRequest;
import org.ArgentumOnline.server.protocol.CitizenMessageRequest;
import org.ArgentumOnline.server.protocol.CommentRequest;
import org.ArgentumOnline.server.protocol.CommerceBuyRequest;
import org.ArgentumOnline.server.protocol.CommerceSellRequest;
import org.ArgentumOnline.server.protocol.CraftBlacksmithRequest;
import org.ArgentumOnline.server.protocol.CraftCarpenterRequest;
import org.ArgentumOnline.server.protocol.CreaturesInMapRequest;
import org.ArgentumOnline.server.protocol.DoubleClickRequest;
import org.ArgentumOnline.server.protocol.DropRequest;
import org.ArgentumOnline.server.protocol.EquipItemRequest;
import org.ArgentumOnline.server.protocol.ForumPostRequest;
import org.ArgentumOnline.server.protocol.GMMessageRequest;
import org.ArgentumOnline.server.protocol.GambleRequest;
import org.ArgentumOnline.server.protocol.GoToCharRequest;
import org.ArgentumOnline.server.protocol.LeftClickRequest;
import org.ArgentumOnline.server.protocol.LoginExistingCharRequest;
import org.ArgentumOnline.server.protocol.LoginNewCharRequest;
import org.ArgentumOnline.server.protocol.ModifySkillsRequest;
import org.ArgentumOnline.server.protocol.MoveSpellRequest;
import org.ArgentumOnline.server.protocol.OnlineMapRequest;
import org.ArgentumOnline.server.protocol.ReviveCharRequest;
import org.ArgentumOnline.server.protocol.RoyalArmyMessageRequest;
import org.ArgentumOnline.server.protocol.SOSRemoveRequest;
import org.ArgentumOnline.server.protocol.ServerMessageRequest;
import org.ArgentumOnline.server.protocol.SetCharDescriptionRequest;
import org.ArgentumOnline.server.protocol.SetMOTDRequest;
import org.ArgentumOnline.server.protocol.SpellInfoRequest;
import org.ArgentumOnline.server.protocol.SummonCharRequest;
import org.ArgentumOnline.server.protocol.SystemMessageRequest;
import org.ArgentumOnline.server.protocol.TalkAsNPCRequest;
import org.ArgentumOnline.server.protocol.TalkRequest;
import org.ArgentumOnline.server.protocol.TrainRequest;
import org.ArgentumOnline.server.protocol.UseItemRequest;
import org.ArgentumOnline.server.protocol.UserCommerceOfferRequest;
import org.ArgentumOnline.server.protocol.WalkRequest;
import org.ArgentumOnline.server.protocol.WarpCharRequest;
import org.ArgentumOnline.server.protocol.WhereRequest;
import org.ArgentumOnline.server.protocol.WhisperRequest;
import org.ArgentumOnline.server.protocol.WorkLeftClickRequest;
import org.ArgentumOnline.server.protocol.WorkRequest;
import org.ArgentumOnline.server.protocol.YellRequest;
import org.ArgentumOnline.server.user.Player;
import org.ArgentumOnline.server.util.FontType;
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
		Optional<Player> p = server.findPlayer(ctx.channel());
		if (!p.isPresent()) {
			return;
		}
		Player player = p.get();

		// TODO
		Gson gson = new Gson();
		log.debug("processing handler " + packet.getClass().getCanonicalName());
		System.out.println("<<< procesando paquete " + packet.getClass().getCanonicalName() + " " + gson.toJson(packet));

		
		player.counters().resetIdleCount();
		
		ClientPacket clientPacket = (ClientPacket)packet;
		// Does the packet requires a logged user?
		if (clientPacket.id() != ClientPacketID.LoginExistingChar &&
			clientPacket.id() != ClientPacketID.LoginNewChar &&
			clientPacket.id() != ClientPacketID.ThrowDices) {
				
	        // Is the user actually logged?
	        if ( ! player.isLogged() ) {
	        	player.quitGame();
	        }
		}
		
		switch (clientPacket.id()) {
		case LoginExistingChar:
			handleLoginExistingChar((LoginExistingCharRequest)packet, player);
			break;
			
		case Walk:
			handleWalk((WalkRequest)packet, player);
			break;
			
		case Talk:
			handleTalk((TalkRequest)packet, player);
			break;
			
		case Yell:
			player.yell(((YellRequest)packet).chat);
			break;
			
		case Whisper:
			player.whisper(((WhisperRequest)packet).targetCharIndex, ((WhisperRequest)packet).chat);
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
			player.startQuitGame();
			break;
			
		case CommerceStart:
			player.commerceStart();
			break;
			
		case CommerceEnd:
			player.commerceEnd();
			break;
			
		case CommerceBuy:
			handleCommerceBuy((CommerceBuyRequest)packet, player);
			break;
			
		case CommerceSell:
			handleCommerceSell((CommerceSellRequest)packet, player);
			break;
			
		case UserCommerceOffer:
			player.userTrade.userCommerceOffer(((UserCommerceOfferRequest)packet).slot, ((UserCommerceOfferRequest)packet).amount);
			break;

		case UserCommerceEnd:
			player.userTrade.userCommerceEnd();
			break;
		
		case UserCommerceOk:
			player.userTrade.userCommerceAccept();
			break;
		
		case UserCommerceReject:
			player.userTrade.userCommerceReject();
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
			
		case CombatModeToggle:
			player.toggleCombatMode();
			break;
			
		case TrainList:
			player.doEntrenar();
			break;
			
		case Train:
			player.userTrainWithPet(((TrainRequest)packet).petIndex);
			break;
			
		case Heal:
			player.doCurar();
			break;
			
		case Resuscitate:
			player.doResucitar();
			break;
			
		case RequestAccountState:
			player.doBalance();
			break;
			
		case Gamble:
			player.doApostar(((GambleRequest)packet).amount);
			break;
			
		case ForumPost:
			handleForumPost(((ForumPostRequest)packet), player);
			break;
			
		case RequestFame:
			player.sendFame();
			break;
		
		case RequestAtributes:
			player.sendUserAttributes();
			break;
			
		case ModifySkills:
			player.skills().subirSkills(((ModifySkillsRequest)packet).skills);
			break;
			
		case Rest:
			player.doDescansar();
			break;
			
		case CraftCarpenter:
			player.doConstruyeCarpinteria(((CraftCarpenterRequest)packet).item);
			break;
			
		case CraftBlacksmith:
			player.doConstruyeHerreria(((CraftBlacksmithRequest)packet).item);
			break;
			
		case SpellInfo:
			player.spells().sendSpellInfo(((SpellInfoRequest)packet).spellSlot);
			break;
			
		case GMRequest:
			server.manager().askForHelpToGM(player);
			break;
			
		case Online:
			player.showUsersOnline();
			break;
			
		case ResuscitationToggle:
			player.resuscitationToggle();
			break;
			
		case PetFollow:
			player.petFollowMaster();
			break;
			
		case PetStand:
			player.petStand();
			break;
			
		case ChangePassword:
			player.changePassword(((ChangePasswordRequest)packet).newPassword);
			break;			
			
		default:
			if (player.flags().isGM()) {
				switch (clientPacket.id()) {
				case OnlineGM:
					server.manager().showGmOnline(player);
					break;
					
				case TurnOffServer:
					server.manager().shutdownServer(player);
					break;
					
				case GMPanel:
					server.manager().showGmPanelForm(player);
					break;
					
				case Invisible:
					server.manager().turnInvisible(player);
					break;
					
				case RainToggle:
					server.manager().toggleRain();
					break;
					
				case SystemMessage:
					server.manager().sendSystemMsg(player, ((SystemMessageRequest)packet).message);
					break;
					
				case Uptime:
					server.manager().showUptime(player);
					break;
					
				case WarpChar:
					handleWarpChar((WarpCharRequest)packet, player);
					break;
					
				case Where:
					server.manager().whereIsUser(player, ((WhereRequest)packet).userName);
					break;
					
				case SummonChar:
					server.manager().summonChar(player, ((SummonCharRequest)packet).userName);
					break;
					
				case GoToChar:
					server.manager().goToChar(player, ((GoToCharRequest)packet).userName);
					break;
					
				case ServerTime:
					server.manager().sendServerTime(player);
					break;
			
				case SOSShowList:
					server.manager().sendHelpRequests(player);
					break;
				
				case SOSRemove:
					server.manager().removeHelpRequest(player, ((SOSRemoveRequest)packet).userName);
					break;
					
				case CleanSOS:
					server.manager().clearAllHelpRequestToGm(player);
					break;
					
				case RequestUserList:
					server.manager().sendUserNameList(player);
					break;
					
				case ReviveChar:
					server.manager().reviveUser(player, ((ReviveCharRequest)packet).userName);
					break;
					
				case DoBackUp:
					server.manager().doBackup(player);
					break;
					
				case ChangeMOTD:
					server.getMotd().startUpdateMOTD(player);
					break;
					
				case SetMOTD:
					server.getMotd().updateMOTD(player, ((SetMOTDRequest)packet).newMOTD);
					break;
					
				case TalkAsNPC:
					player.talkAsNpc(((TalkAsNPCRequest)packet).message);
					break;
					
				case GMMessage:
					server.manager().sendMessageToAdmins(player,
							player.getNick() + "> " + ((GMMessageRequest)packet).message, 
							FontType.FONTTYPE_GMMSG);
					break;
					
				case ServerMessage:
					server.manager().sendServerMessage(player, ((ServerMessageRequest)packet).message);
					break;
					
				case RoyalArmyMessage:
					server.manager().sendMessageToRoyalArmy(player, ((RoyalArmyMessageRequest)packet).message);
					break;
					
				case ChaosLegionMessage:
					server.manager().sendMessageToDarkLegion(player, ((ChaosLegionMessageRequest)packet).message);
					break;
					
				case CitizenMessage:
					server.manager().sendMessageToCitizens(player, ((CitizenMessageRequest)packet).message);
					break;
					
				case CreaturesInMap:
					server.manager().sendCreaturesInMap(player, ((CreaturesInMapRequest)packet).map);
					break;
					
				case OnlineMap:
					server.manager().sendUsersOnlineMap(player, ((OnlineMapRequest)packet).map);
					break;
					
				case OnlineRoyalArmy:
					server.manager().sendOnlineRoyalArmy(player);
					break;
					
				case OnlineChaosLegion:
					server.manager().sendOnlineChaosLegion(player);
					break;
					
				case Working:
					server.manager().sendUsersWorking(player);
					break;
					
				case Hiding:
					server.manager().sendUsersHiding(player);
					break;
					
				case NavigateToggle:
					player.navigateToggleGM();
					break;
					
				case Ignored:
					player.ignoreToggleGM();
					break;
					
				case ShowName:
					player.showNameToggleGM();
					break;
					
				case ChatColor:
					handleChatColor((ChatColorRequest)packet, player);
					break;
					
				case SetCharDescription:
					player.changeCharDescription(((SetCharDescriptionRequest)packet).desc);
					break;
					
				case Comment:
					server.manager().saveGmComment(player, ((CommentRequest)packet).comment);
					break;
					
				case CleanWorld:
					server.cleanWorld(player);
					break;
					
				default:
					System.out.println("WARNING!!!! UNHANDLED PACKET: " + packet.getClass().getCanonicalName());
				}
			} else {
				System.out.println("WARNING!!!! UNHANDLED PACKET: " + packet.getClass().getCanonicalName());
			}
		}
		
	}

	private void handleChatColor(ChatColorRequest packet, Player admin) {
		admin.changeChatColor(packet.red & 0xff, packet.green & 0xff, packet.blue & 0xff);
	}

	private void handleWarpChar(WarpCharRequest packet, Player admin) {
		GameServer.instance().manager().warpUserTo(admin, packet.userName, packet.map, packet.x, packet.y);
	}

	private void handleForumPost(ForumPostRequest packet, Player player) {
		player.postOnForum(packet.title, packet.msg);
	}

	private void handleBankExtractItem(BankExtractItemRequest packet, Player player) {
		player.bankExtractItem(packet.slot, packet.amount);
	}

	private void handleBankDepositItem(BankDepositRequest packet, Player player) {
		player.bankDepositItem(packet.slot, packet.amount);
	}

	private void handleLoginNewChar(LoginNewCharRequest packet, Player player) {
		player.connectNewUser(packet.userName, packet.password, packet.race,
				packet.gender, packet.clazz, packet.email, packet.homeland);
	}

	private void handleCommerceSell(CommerceSellRequest packet, Player player) {
		player.commerceSellToMerchant(packet.slot, packet.amount);		
	}

	private void handleCommerceBuy(CommerceBuyRequest packet, Player player) {
		player.commerceBuyFromMerchant(packet.slot, packet.amount);
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
		// Packet.dir is boolean. 
		// - Upwards direction if TRUE(1)
		// - Downward direction if FALSE(0).
		// Packet.spell is Spell's Slot in ( 1 .. MAXSLOT )
		player.moveSpell(packet.spell, 
				(byte) (packet.dir == 1 ? -1 : 1));
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