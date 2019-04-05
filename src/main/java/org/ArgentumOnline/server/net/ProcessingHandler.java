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
import org.ArgentumOnline.server.protocol.*;
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
	        if ( !player.isLogged() ) {
	        	player.quitGame();
	        }
		}
		
		switch (clientPacket.id()) {
		case Ping:
			player.ping();
			break;
			
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
			
		case MoveBank:
			player.moveBank(((MoveBankRequest)packet).slot, ((MoveBankRequest)packet).dir);
			break;
			
		case SafeToggle:
			player.safeToggle();
			break;
			
		case CombatModeToggle:
			player.toggleCombatMode();
			break;
			
		case TrainList:
			player.trainList();
			break;
			
		case Train:
			player.userTrainWithPet(((TrainRequest)packet).petIndex);
			break;
			
		case Heal:
			player.heal();
			break;
			
		case Resuscitate:
			player.resuscitate();
			break;
			
		case RequestAccountState:
			player.requestAccountState();
			break;
			
		case Gamble:
			player.gamble(((GambleRequest)packet).amount);
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
			player.rest();
			break;
			
		case CraftCarpenter:
			player.craftCarpenter(((CraftCarpenterRequest)packet).item);
			break;
			
		case CraftBlacksmith:
			player.craftBlacksmith(((CraftBlacksmithRequest)packet).item);
			break;
			
		case SpellInfo:
			player.spells().sendSpellInfo(((SpellInfoRequest)packet).spellSlot);
			break;
			
		case Information:
			player.showInformation();
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
			
		case Denounce:
			player.denounce(((DenounceRequest)packet).text);
			break;
			
		case Help:
			player.showHelp();
			break;
			
		case BugReport:
			server.manager().bugReport(player, ((BugReportRequest)packet).bugReport);
			break;
			
		case RequestMOTD:
			server.motd().showMOTD(player);
			break;
			
		case UseSpellMacro:
			player.useSpellMacro(player);
			break;
			
		case RequestStats:
			server.manager().sendUserStats(player, player);
			break;

		case RoleMasterRequest:
			server.manager().roleMasterRequest(player, ((RoleMasterRequestRequest)packet).request);
			break;
			
		case CouncilMessage:
			server.manager().sendCouncilMessage(player, ((CouncilMessageRequest)packet).chat);
			break;
			
		case Reward:
			player.reward();
			break;
			
		case Enlist:
			player.enlist();
			break;
			
		case LeaveFaction:
			player.leaveFaction();
			break;
			
		case ChangeDescription:
			player.changeDescription(((ChangeDescriptionRequest)packet).description);
			break;
			
		case CentinelReport:
			server.getWorkWatcher().checkCode(player, ((CentinelReportRequest)packet).key);
			break;

		// *****************************************************************************************	
		// *****************************************************************************************	
		// 										GM COMMANDS
		// *****************************************************************************************	
		// *****************************************************************************************	
		
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
			server.manager().toggleRain(player);
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
			
		case WarpMeToTarget:
			server.manager().warpMeToTarget(player);
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
			server.manager().backupWorld(player);
			break;
			
		case ChangeMOTD:
			server.motd().startUpdateMOTD(player);
			break;
			
		case SetMOTD:
			server.motd().updateMOTD(player, ((SetMOTDRequest)packet).newMOTD);
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
			
		case CriminalMessage:
			server.manager().sendMessageToCriminals(player, ((CriminalMessageRequest)packet).message);
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
			
		case SpawnListRequest:
			server.manager().sendSpawnCreatureList(player);
			break;
			
		case SpawnCreature:
			server.manager().spawnCreature(player, ((SpawnCreatureRequest)packet).npc);
			break;
			
		case NPCFollow:
			server.manager().npcFollow(player);
			break;

		case KillNPC:
			server.manager().killNpc(player);
			break;
			
		case KillNPCNoRespawn:
			server.manager().killNpcNoRespawn(player);
			break;
			
		case KillAllNearbyNPCs:
			server.manager().killAllNearbyNpcs(player);
			break;
			
		case TeleportCreate:
			handleTeleportCreate((TeleportCreateRequest)packet, player);
			break;
			
		case TeleportDestroy:
			server.manager().destroyTeleport(player);
			break;
			
		case CreateItem:
			server.manager().createItem(player, ((CreateItemRequest)packet).objectIndex);
			break;
			
		case DestroyItems:
			server.manager().destroyItem(player);
			break;
			
		case ItemsInTheFloor:
			server.manager().sendItemsInTheFloor(player);
			break;
			
		case DestroyAllItemsInArea:
			server.manager().destroyAllItemsInArea(player);
			break;
			
		case GoNearby:
			server.manager().goToChar(player, ((GoNearbyRequest)packet).userName);
			break;
			
		case RequestCharInfo:
			server.manager().requestCharInfo(player, ((RequestCharInfoRequest)packet).userName);
			break;
			
		case RequestCharInventory:
			server.manager().requestCharInv(player, ((RequestCharInventoryRequest)packet).userName);
			break;
			
		case RequestCharStats:
			server.manager().requestCharStats(player, ((RequestCharStatsRequest)packet).userName);
			break;
			
		case RequestCharGold:
			server.manager().requestCharGold(player, ((RequestCharGoldRequest)packet).userName);
			break;
			
		case RequestCharBank:
			server.manager().requestCharBank(player, ((RequestCharBankRequest)packet).userName);
			break;
			
		case RequestCharSkills:
			server.manager().requestCharSkills(player, ((RequestCharSkillsRequest)packet).userName);
			break;
			
		case NickToIP:
			server.manager().nick2IP(player, ((NickToIPRequest)packet).userName);
			break;
			
		case IPToNick:
			handleIpToNick(player, (IPToNickRequest)packet);
			break;
			
		case LastIP:
			server.manager().lastIp(player, ((LastIPRequest)packet).userName);
			break;
			
		case RequestCharMail:
			server.manager().requestCharEmail(player, ((RequestCharMailRequest)packet).userName);
			break;
			
		case MakeDumb:
			server.manager().makeDumb(player, ((MakeDumbRequest)packet).userName);
			break;

		case MakeDumbNoMore:
			server.manager().makeNoDumb(player, ((MakeDumbNoMoreRequest)packet).userName);
			break;
			
		case Execute:
			server.manager().executeUser(player, ((ExecuteRequest)packet).userName);
			break;
			
		case Silence:
			server.manager().silenceUser(player, ((SilenceRequest)packet).userName);
			break;

		case Punishments:
			server.manager().punishments(player, ((PunishmentsRequest)packet).name);
			break;
			
		case WarnUser:
			handleWarnUser(player, (WarnUserRequest)packet);
			break;
			
		case RemovePunishment:
			handleRemovePunishment(player, (RemovePunishmentRequest)packet);
			break;
			
		case Jail:
			handleJail(player, (JailRequest)packet);
			break;
			
		case Forgive:
			server.manager().forgiveUser(player, ((ForgiveRequest)packet).userName);
			break;
			
		case TurnCriminal:
			server.manager().turnCriminal(player, ((TurnCriminalRequest)packet).userName);
			break;
			
		case BanChar:
			handleBanChar(player, (BanCharRequest)packet);
			break;
			
		case UnbanChar:
			server.manager().unbanUser(player, ((UnbanCharRequest)packet).userName);
			break;
			
		case BanIP:
			handleBanIp(player, (BanIPRequest)packet);
			break;
		
		case UnbanIP:
			handleUnbanIp(player, (UnbanIPRequest)packet);
			break;
			
		case BannedIPList:
			server.manager().bannedIPList(player);
			break;
			
		case BannedIPReload:
			server.manager().bannedIPReload(player);
			break;		
			
		case Kick:
			server.manager().kickUser(player, ((KickRequest)packet).userName);
			break;
			
		case KickAllChars:
			server.manager().kickAllUsersNoGm(player);
			break;
			
		case ForceMIDIAll:
			server.manager().playMidiAll(player, ((ForceMIDIAllRequest)packet).midiId);
			break;
			
		case ForceMIDIToMap:
			server.manager().playMidiToMap(player, ((ForceMIDIToMapRequest)packet).map, ((ForceMIDIToMapRequest)packet).midiId);
			break;
			
		case ForceWAVEAll:
			server.manager().playWaveAll(player, ((ForceWAVEAllRequest)packet).waveId);
			break;
			
		case ForceWAVEToMap:
			handleForceWaveToMap(player, (ForceWAVEToMapRequest)packet);
			break;
			
		case ShowServerForm:
			// n/a
			break;

		case SaveChars:
			server.manager().saveChars(player);
			break;
			
		case SaveMap:
			server.manager().saveMap(player);
			break;
			
		case ServerOpenToUsersToggle:
			server.manager().serverOpenToUsersToggle(player);
			break;
			
		case ReloadNPCs:
			// N/A:
			break;
			
		case ReloadObjects:
			server.reloadObjects(player);
			break;
			
		case ReloadSpells:
			server.reloadSpells(player);
			break;
			
		case ReloadServerIni:
			server.manager().reloadServerIni(player);
			break;
			
		case Restart:
			// N/A
			break;
			
		case AlterMail:
			server.manager().alterEmail(player, ((AlterMailRequest)packet).userName, ((AlterMailRequest)packet).newEmail);
			break;
			
		case AlterName:
			server.manager().alterName(player, ((AlterNameRequest)packet).userName, ((AlterNameRequest)packet).newName);
			break;
			
		case AlterPassword:
			// /APASS
			if (player.isGM()) {
				player.sendMessage("Por seguridad, no se puede cambiar passwords de este modo.", FontType.FONTTYPE_INFO);
				// TODO implementar otro mecanismo para cambio de passwords x email
			}
			break;
			
		case TileBlockedToggle:
			server.manager().tileBlockedToggle(player);
			break;

		case SetTrigger:
			server.manager().setTrigger(player, ((SetTriggerRequest)packet).trigger);
			break;
			
		case AskTrigger:
			server.manager().askTrigger(player);
			break;
			
		case CreateNPC:
			server.manager().createNpc(player, ((CreateNPCRequest)packet).npcIndex);
			break;
			
		case CreateNPCWithRespawn:
			server.manager().createNpcWithRespawn(player, ((CreateNPCWithRespawnRequest)packet).npcIndex);
			break;
					
		case ResetNPCInventory:
			server.manager().resetNPCInventory(player);
			break;
			
		case ImperialArmour:
			handleImperialArmour(player, (ImperialArmourRequest)packet);
			break;
			
		case ChaosArmour:
			handleChaosArmour(player, (ChaosArmourRequest)packet);
			break;
			
		case AcceptRoyalCouncilMember:
			server.manager().acceptRoyalCouncilMember(player, ((AcceptRoyalCouncilMemberRequest)packet).userName);
			break;
					
		case AcceptChaosCouncilMember:
			server.manager().acceptChaosCouncilMember(player, ((AcceptChaosCouncilMemberRequest)packet).userName);
			break;
			
		case CouncilKick:
			server.manager().councilKick(player, ((CouncilKickRequest)packet).userName);
			break;
			
		case RoyalArmyKick:
			server.manager().royalArmyKickForEver(player, ((RoyalArmyKickRequest)packet).userName);
			break;
			
		case ChaosLegionKick:
			server.manager().chaosLegionKickForEver(player, ((ChaosLegionKickRequest)packet).userName);
			break;
			
		case ResetFactions:
			server.manager().resetFactions(player, ((ResetFactionsRequest)packet).userName);
			break;

		case ResetAutoUpdate:
			// N/A
			break;
			
		case ToggleCentinelActivated:
			server.getWorkWatcher().workWatcherActivateToggle(player);
			break;
			
		case ChangeMapInfoBackup:
			server.manager().changeMapInfoBackup(player, ((ChangeMapInfoBackupRequest)packet).doTheBackup == 1);
			break;
			
		case ChangeMapInfoLand:
			server.manager().changeMapInfoLand(player, ((ChangeMapInfoLandRequest)packet).infoLand);
			break;
			
		case ChangeMapInfoZone:
			server.manager().changeMapInfoZone(player, ((ChangeMapInfoZoneRequest)packet).infoZone);
			break;
			
		case ChangeMapInfoNoInvi:
			server.manager().changeMapInfoNoInvi(player, ((ChangeMapInfoNoInviRequest)packet).noInvisible == 1);
			break;
			 
		case ChangeMapInfoNoMagic:
			server.manager().changeMapInfoNoMagic(player, ((ChangeMapInfoNoMagicRequest)packet).noMagic == 1);
			break;			
			
		case ChangeMapInfoNoResu:
			server.manager().changeMapInfoNoResu(player, ((ChangeMapInfoNoResuRequest)packet).noResu == 1);
			break;
			
		case ChangeMapInfoPK:
			server.manager().changeMapInfoPK(player, ((ChangeMapInfoPKRequest)packet).isMapPk == 1);
			break;
			
		case ChangeMapInfoRestricted:
			server.manager().changeMapInfoRestricted(player, ((ChangeMapInfoRestrictedRequest)packet).status);
			break;
			
		case EditChar:
			handleEditChar(player, (EditCharRequest)packet);
			break;
			
		case CheckSlot:
			handleCheckSlot(player, (CheckSlotRequest)packet);
			break;
			
		case PartyCreate:
		case PartyAcceptMember:
		case PartyJoin:
		case PartyKick:
		case PartyLeave:
		case PartyMessage:
		case PartyOnline:
		case PartySetLeader:

		case SetIniVar:
		case Night:
		case DumpIPTables:

		case Inquiry:
		case InquiryVote:
			
		case ClanCodexUpdate:
		case CreateNewGuild:
		case GuildAcceptAlliance:
		case GuildAcceptNewMember:
		case GuildAcceptPeace:
		case GuildAllianceDetails:
		case GuildAlliancePropList:
		case GuildBan:
		case GuildDeclareWar:
		case GuildFundate:
		case GuildKickMember:
		case GuildLeave:
		case GuildMemberInfo:
		case GuildMemberList:
		case GuildMessage:
		case GuildNewWebsite:
		case GuildOfferAlliance:
		case GuildOfferPeace:
		case GuildOnline:
		case GuildOnlineMembers:
		case GuildOpenElections:
		case GuildPeaceDetails:
		case GuildPeacePropList:
		case GuildRejectAlliance:
		case GuildRejectNewMember:
		case GuildRejectPeace:
		case GuildRequestDetails:
		case GuildRequestJoinerInfo:
		case GuildRequestMembership:
		case GuildUpdateNews:
		case GuildVote:
		case RemoveCharFromGuild:
		case RequestGuildLeaderInfo:
		case ShowGuildMessages:
		
		default:
			System.out.println("WARNING!!!! UNHANDLED PACKET: " + packet.getClass().getCanonicalName());
			break;
		}
		
	}

	private void handleCheckSlot(Player player, CheckSlotRequest packet) {
		GameServer.instance().manager().handleCheckSlot(player, packet.userName, packet.slot);
	}

	private void handleEditChar(Player admin, EditCharRequest packet) {
		GameServer.instance().manager().handleEditCharacter(admin, 
				packet.userName, packet.option, packet.param1, packet.param2);
	}

	private void handleImperialArmour(Player admin, ImperialArmourRequest packet) {
		GameServer.instance().manager().royalArmyArmour(admin, packet.index, packet.objIndex);
	}
	
	private void handleChaosArmour(Player admin, ChaosArmourRequest packet) {
		GameServer.instance().manager().darkLegionArmour(admin, packet.index, packet.objIndex);
	}

	private void handleIpToNick(Player admin, IPToNickRequest packet) {
		String ip = "" + (packet.ip1&0xFF) + "." + (packet.ip2&0xFF) + "." + (packet.ip3&0xFF) + "." + (packet.ip4&0xFF);
		GameServer.instance().manager().ipToNick(admin, ip);
	}

	private void handleForceWaveToMap(Player player, ForceWAVEToMapRequest packet) {
		GameServer.instance().manager().playWavToMap(player, packet.waveId, packet.map, packet.x, packet.y);		
	}

	private void handleUnbanIp(Player admin, UnbanIPRequest packet) {
		String bannedIP = "" + (packet.ip1&0xFF) + "." + (packet.ip2&0xFF) + "." + (packet.ip3&0xFF) + "." + (packet.ip4&0xFF);
		GameServer.instance().manager().unbanIP(admin, bannedIP);
	}

	private void handleBanIp(Player admin, BanIPRequest packet) {
		if (packet.byIP) {
			String bannedIP = "" + (packet.ip1&0xFF) + "." + (packet.ip2&0xFF) + "." + (packet.ip3&0xFF) + "." + (packet.ip4&0xFF);
			GameServer.instance().manager().banIP(admin, bannedIP, packet.reason);
		} else {
			GameServer.instance().manager().banIPUser(admin, packet.userName, packet.reason);
		}
	}

	private void handleBanChar(Player admin, BanCharRequest packet) {
		GameServer.instance().manager().banUser(admin, packet.userName, packet.reason);
	}

	private void handleJail(Player admin, JailRequest packet) {
		GameServer.instance().manager().sendUserToJail(admin, packet.userName, packet.reason, packet.jailTime);
	}

	private void handleRemovePunishment(Player player, RemovePunishmentRequest packet) {
		GameServer.instance().manager().removePunishment(player, packet.userName, packet.punishment, packet.newText); 
	}

	private void handleWarnUser(Player player, WarnUserRequest packet) {
		GameServer.instance().manager().warnUser(player, packet.userName, packet.reason);
	}

	private void handleTeleportCreate(TeleportCreateRequest packet, Player admin) {
		GameServer.instance().manager().createTeleport(admin, packet.mapa, packet.x, packet.y);
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