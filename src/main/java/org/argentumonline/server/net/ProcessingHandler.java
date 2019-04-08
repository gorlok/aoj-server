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
package org.argentumonline.server.net;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.gm.ChangeMapInfo;
import org.argentumonline.server.gm.EditChar;
import org.argentumonline.server.map.Heading;
import org.argentumonline.server.protocol.*;
import org.argentumonline.server.user.User;
import org.argentumonline.server.util.FontType;

import com.google.gson.Gson;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class ProcessingHandler extends ChannelInboundHandlerAdapter {
	private static Logger log = LogManager.getLogger();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
		var server = GameServer.instance();
		Optional<User> p = server.findUser(ctx.channel());
		if (!p.isPresent()) {
			return;
		}
		User user = p.get();

		// TODO
		Gson gson = new Gson();
		log.debug("processing handler " + packet.getClass().getCanonicalName());
		System.out.println("<<< procesando paquete " + packet.getClass().getCanonicalName() + " " + gson.toJson(packet));

		
		user.getCounters().resetIdleCount();
		
		ClientPacket clientPacket = (ClientPacket)packet;
		// Does the packet requires a logged user?
		if (clientPacket.id() != ClientPacketID.LoginExistingChar &&
			clientPacket.id() != ClientPacketID.LoginNewChar &&
			clientPacket.id() != ClientPacketID.ThrowDices) {
				
	        // Is the user actually logged?
	        if ( !user.isLogged() ) {
	        	user.quitGame();
	        }
		}
		
		switch (clientPacket.id()) {
		case Ping:
			user.ping();
			break;
			
		case LoginExistingChar:
			handleLoginExistingChar((LoginExistingCharRequest)packet, user);
			break;
			
		case Walk:
			handleWalk((WalkRequest)packet, user);
			break;
			
		case Talk:
			handleTalk((TalkRequest)packet, user);
			break;
			
		case Yell:
			user.yell(((YellRequest)packet).chat);
			break;
			
		case Whisper:
			user.whisper(((WhisperRequest)packet).targetCharIndex, ((WhisperRequest)packet).chat);
			break;

		case LeftClick:
			handleLeftClick((LeftClickRequest)packet, user);
			break;

		case DoubleClick:
			handleDoubleClick((DoubleClickRequest)packet, user);
			break;

		case WorkLeftClick:
			handleWorkLeftClick((WorkLeftClickRequest)packet, user);
			break;

		case ChangeHeading:
			handleChangeHeading((ChangeHeadingRequest)packet, user);
			break;

		case MoveSpell:
			handleMoveSpell((MoveSpellRequest)packet, user);
			break;
			
		case CastSpell:
			handleCastSpell((CastSpellRequest)packet, user);
			break;

		case Work:
			handleWork((WorkRequest)packet, user);
			break;

		case PickUp: // pikachu :P
			user.pickUpObject();
			break;

		case Drop:
			handleDrop((DropRequest)packet, user);
			break;

		case EquipItem:
			handleEquipItem((EquipItemRequest)packet, user);
			break;

		case Attack:
			user.attack();
			break;
			
		case UseItem:
			user.useItem(((UseItemRequest)packet).slot);
			break;
			
		case Quit:
			user.startQuitGame();
			break;
			
		case CommerceStart:
			user.getUserTrade().commerceStart();
			break;
			
		case CommerceEnd:
			user.getUserTrade().commerceEnd();
			break;
			
		case CommerceBuy:
			handleCommerceBuy((CommerceBuyRequest)packet, user);
			break;
			
		case CommerceSell:
			handleCommerceSell((CommerceSellRequest)packet, user);
			break;
			
		case UserCommerceOffer:
			user.userTrade.userCommerceOffer(((UserCommerceOfferRequest)packet).slot, ((UserCommerceOfferRequest)packet).amount);
			break;

		case UserCommerceEnd:
			user.userTrade.userCommerceEnd();
			break;
		
		case UserCommerceOk:
			user.userTrade.userCommerceAccept();
			break;
		
		case UserCommerceReject:
			user.userTrade.userCommerceReject();
			break;
			
		case Meditate:
			user.meditate();
			break;
			
		case RequestPositionUpdate:
			user.sendPositionUpdate();
			break;

		case LoginNewChar:
			handleLoginNewChar((LoginNewCharRequest)packet, user);
			break;

		case ThrowDices:
			user.throwDices();
			break;
			
		case RequestMiniStats:
			user.sendMiniStats();
			break;
			
		case RequestSkills:
			user.sendSkills();
			break;
			
		case BankStart:
			user.getBankInventory().bankStart();
			break;

		case BankEnd:
			user.getBankInventory().bankEnd();
			break;

		case BankDeposit:
			handleBankDepositItem((BankDepositRequest)packet, user);
			break;

		case BankExtractItem:
			handleBankExtractItem((BankExtractItemRequest)packet, user);
			break;

		case BankDepositGold:
			user.bankDepositGold(((BankDepositGoldRequest)packet).amount);
			break;
			
		case BankExtractGold:
			user.bankExtractGold(((BankExtractGoldRequest)packet).amount);
			break;
			
		case MoveBank:
			user.getBankInventory().moveBank(((MoveBankRequest)packet).slot, ((MoveBankRequest)packet).dir);
			break;
			
		case SafeToggle:
			user.safeToggle();
			break;
			
		case CombatModeToggle:
			user.toggleCombatMode();
			break;
			
		case TrainList:
			user.trainList();
			break;
			
		case Train:
			user.userTrainWithPet(((TrainRequest)packet).petIndex);
			break;
			
		case Heal:
			user.heal();
			break;
			
		case Resuscitate:
			user.resuscitate();
			break;
			
		case RequestAccountState:
			user.requestAccountState();
			break;
			
		case Gamble:
			user.gamble(((GambleRequest)packet).amount);
			break;
			
		case ForumPost:
			handleForumPost(((ForumPostRequest)packet), user);
			break;
			
		case RequestFame:
			user.sendFame();
			break;
		
		case RequestAtributes:
			user.sendUserAttributes();
			break;
			
		case ModifySkills:
			user.skills().subirSkills(((ModifySkillsRequest)packet).skills);
			break;
			
		case Rest:
			user.rest();
			break;
			
		case CraftCarpenter:
			server.getWork().craftCarpenter(user, ((CraftCarpenterRequest)packet).item);
			break;
			
		case CraftBlacksmith:
			server.getWork().craftBlacksmith(user, ((CraftBlacksmithRequest)packet).item);
			break;
			
		case SpellInfo:
			user.spells().sendSpellInfo(((SpellInfoRequest)packet).spellSlot);
			break;
			
		case Information:
			user.showInformation();
			break;
			
		case GMRequest:
			server.getHelpRequest().askForHelpToGM(user);
			break;
			
		case Online:
			user.showUsersOnline();
			break;
			
		case ResuscitationToggle:
			user.resuscitationToggle();
			break;
			
		case PetFollow:
			user.petFollowMaster();
			break;
			
		case PetStand:
			user.petStand();
			break;
			
		case ChangePassword:
			user.changePassword(((ChangePasswordRequest)packet).newPassword);
			break;
			
		case Denounce:
			user.denounce(((DenounceRequest)packet).text);
			break;
			
		case Help:
			user.showHelp();
			break;
			
		case BugReport:
			server.manager().bugReport(user, ((BugReportRequest)packet).bugReport);
			break;
			
		case RequestMOTD:
			server.motd().showMOTD(user);
			break;
			
		case UseSpellMacro:
			user.useSpellMacro(user);
			break;
			
		case RequestStats:
			server.manager().sendUserStats(user, user);
			break;

		case RoleMasterRequest:
			server.manager().roleMasterRequest(user, ((RoleMasterRequestRequest)packet).request);
			break;
			
		case CouncilMessage:
			server.sendCouncilMessage(user, ((CouncilMessageRequest)packet).chat);
			break;
			
		case Reward:
			user.reward();
			break;
			
		case Enlist:
			user.enlist();
			break;
			
		case LeaveFaction:
			user.leaveFaction();
			break;
			
		case ChangeDescription:
			user.changeDescription(((ChangeDescriptionRequest)packet).description);
			break;
			
		case CentinelReport:
			server.getWorkWatcher().checkCode(user, ((CentinelReportRequest)packet).key);
			break;

		// *****************************************************************************************	
		// *****************************************************************************************	
		// 										GM COMMANDS
		// *****************************************************************************************	
		// *****************************************************************************************	
		
		case OnlineGM:
			server.manager().showGmOnline(user);
			break;
			
		case TurnOffServer:
			server.manager().shutdownServer(user);
			break;
			
		case GMPanel:
			server.manager().showGmPanelForm(user);
			break;
			
		case Invisible:
			server.manager().turnInvisible(user);
			break;
			
		case RainToggle:
			server.manager().toggleRain(user);
			break;
			
		case SystemMessage:
			server.manager().sendSystemMsg(user, ((SystemMessageRequest)packet).message);
			break;
			
		case Uptime:
			server.manager().showUptime(user);
			break;
			
		case WarpChar:
			handleWarpChar((WarpCharRequest)packet, user);
			break;
			
		case Where:
			server.manager().whereIsUser(user, ((WhereRequest)packet).userName);
			break;
			
		case SummonChar:
			server.manager().summonChar(user, ((SummonCharRequest)packet).userName);
			break;
			
		case GoToChar:
			server.manager().goToChar(user, ((GoToCharRequest)packet).userName);
			break;
			
		case WarpMeToTarget:
			server.manager().warpMeToTarget(user);
			break;
			
		case ServerTime:
			server.manager().sendServerTime(user);
			break;
	
		case SOSShowList:
			server.getHelpRequest().sendHelpRequests(user);
			break;
		
		case SOSRemove:
			server.getHelpRequest().removeHelpRequest(user, ((SOSRemoveRequest)packet).userName);
			break;
			
		case CleanSOS:
			server.getHelpRequest().clearAllHelpRequestToGm(user);
			break;
			
		case RequestUserList:
			server.manager().sendUserNameList(user);
			break;
			
		case ReviveChar:
			server.manager().reviveUser(user, ((ReviveCharRequest)packet).userName);
			break;
			
		case DoBackUp:
			server.manager().backupWorld(user);
			break;
			
		case ChangeMOTD:
			server.motd().startUpdateMOTD(user);
			break;
			
		case SetMOTD:
			server.motd().updateMOTD(user, ((SetMOTDRequest)packet).newMOTD);
			break;
			
		case TalkAsNPC:
			user.talkAsNpc(((TalkAsNPCRequest)packet).message);
			break;
			
		case GMMessage:
			server.sendMessageToAdmins(user,
					user.getUserName() + "> " + ((GMMessageRequest)packet).message, 
					FontType.FONTTYPE_GMMSG);
			break;
			
		case ServerMessage:
			server.sendServerMessage(user, ((ServerMessageRequest)packet).message);
			break;
			
		case RoyalArmyMessage:
			server.sendMessageToRoyalArmy(user, ((RoyalArmyMessageRequest)packet).message);
			break;
			
		case ChaosLegionMessage:
			server.sendMessageToDarkLegion(user, ((ChaosLegionMessageRequest)packet).message);
			break;
			
		case CitizenMessage:
			server.sendMessageToCitizens(user, ((CitizenMessageRequest)packet).message);
			break;
			
		case CriminalMessage:
			server.sendMessageToCriminals(user, ((CriminalMessageRequest)packet).message);
			break;
			
		case CreaturesInMap:
			server.manager().sendCreaturesInMap(user, ((CreaturesInMapRequest)packet).map);
			break;
			
		case OnlineMap:
			server.manager().sendUsersOnlineMap(user, ((OnlineMapRequest)packet).map);
			break;
			
		case OnlineRoyalArmy:
			server.manager().sendOnlineRoyalArmy(user);
			break;
			
		case OnlineChaosLegion:
			server.manager().sendOnlineChaosLegion(user);
			break;
			
		case Working:
			server.manager().sendUsersWorking(user);
			break;
			
		case Hiding:
			server.manager().sendUsersHiding(user);
			break;
			
		case NavigateToggle:
			user.navigateToggleGM();
			break;
			
		case Ignored:
			user.ignoreToggleGM();
			break;
			
		case ShowName:
			user.showNameToggleGM();
			break;
			
		case ChatColor:
			handleChatColor((ChatColorRequest)packet, user);
			break;
			
		case SetCharDescription:
			user.changeCharDescription(((SetCharDescriptionRequest)packet).desc);
			break;
			
		case Comment:
			server.manager().saveGmComment(user, ((CommentRequest)packet).comment);
			break;
			
		case CleanWorld:
			server.cleanWorld(user);
			break;
			
		case SpawnListRequest:
			server.manager().sendSpawnCreatureList(user);
			break;
			
		case SpawnCreature:
			server.manager().spawnCreature(user, ((SpawnCreatureRequest)packet).npc);
			break;
			
		case NPCFollow:
			server.manager().npcFollow(user);
			break;

		case KillNPC:
			server.manager().killNpc(user);
			break;
			
		case KillNPCNoRespawn:
			server.manager().killNpcNoRespawn(user);
			break;
			
		case KillAllNearbyNPCs:
			server.manager().killAllNearbyNpcs(user);
			break;
			
		case TeleportCreate:
			handleTeleportCreate((TeleportCreateRequest)packet, user);
			break;
			
		case TeleportDestroy:
			server.manager().destroyTeleport(user);
			break;
			
		case CreateItem:
			server.manager().createItem(user, ((CreateItemRequest)packet).objectIndex);
			break;
			
		case DestroyItems:
			server.manager().destroyItem(user);
			break;
			
		case ItemsInTheFloor:
			server.manager().sendItemsInTheFloor(user);
			break;
			
		case DestroyAllItemsInArea:
			server.manager().destroyAllItemsInArea(user);
			break;
			
		case GoNearby:
			server.manager().goToChar(user, ((GoNearbyRequest)packet).userName);
			break;
			
		case RequestCharInfo:
			server.manager().requestCharInfo(user, ((RequestCharInfoRequest)packet).userName);
			break;
			
		case RequestCharInventory:
			server.manager().requestCharInv(user, ((RequestCharInventoryRequest)packet).userName);
			break;
			
		case RequestCharStats:
			server.manager().requestCharStats(user, ((RequestCharStatsRequest)packet).userName);
			break;
			
		case RequestCharGold:
			server.manager().requestCharGold(user, ((RequestCharGoldRequest)packet).userName);
			break;
			
		case RequestCharBank:
			server.manager().requestCharBank(user, ((RequestCharBankRequest)packet).userName);
			break;
			
		case RequestCharSkills:
			server.manager().requestCharSkills(user, ((RequestCharSkillsRequest)packet).userName);
			break;
			
		case NickToIP:
			server.manager().userNameToIp(user, ((NickToIPRequest)packet).userName);
			break;
			
		case IPToNick:
			handleIpToNick(user, (IPToNickRequest)packet);
			break;
			
		case LastIP:
			server.manager().lastIp(user, ((LastIPRequest)packet).userName);
			break;
			
		case RequestCharMail:
			server.manager().requestCharEmail(user, ((RequestCharMailRequest)packet).userName);
			break;
			
		case MakeDumb:
			server.manager().makeDumb(user, ((MakeDumbRequest)packet).userName);
			break;

		case MakeDumbNoMore:
			server.manager().makeNoDumb(user, ((MakeDumbNoMoreRequest)packet).userName);
			break;
			
		case Execute:
			server.manager().executeUser(user, ((ExecuteRequest)packet).userName);
			break;
			
		case Silence:
			server.manager().silenceUser(user, ((SilenceRequest)packet).userName);
			break;

		case Punishments:
			server.manager().punishments(user, ((PunishmentsRequest)packet).name);
			break;
			
		case WarnUser:
			handleWarnUser(user, (WarnUserRequest)packet);
			break;
			
		case RemovePunishment:
			handleRemovePunishment(user, (RemovePunishmentRequest)packet);
			break;
			
		case Jail:
			handleJail(user, (JailRequest)packet);
			break;
			
		case Forgive:
			server.manager().forgiveUser(user, ((ForgiveRequest)packet).userName);
			break;
			
		case TurnCriminal:
			server.manager().turnCriminal(user, ((TurnCriminalRequest)packet).userName);
			break;
			
		case BanChar:
			handleBanChar(user, (BanCharRequest)packet);
			break;
			
		case UnbanChar:
			server.getBannIP().unbanUser(user, ((UnbanCharRequest)packet).userName);
			break;
			
		case BanIP:
			handleBanIp(user, (BanIPRequest)packet);
			break;
		
		case UnbanIP:
			handleUnbanIp(user, (UnbanIPRequest)packet);
			break;
			
		case BannedIPList:
			server.getBannIP().bannedIPList(user);
			break;
			
		case BannedIPReload:
			server.getBannIP().bannedIPReload(user);
			break;		
			
		case Kick:
			server.manager().kickUser(user, ((KickRequest)packet).userName);
			break;
			
		case KickAllChars:
			server.manager().kickAllUsersNoGm(user);
			break;
			
		case ForceMIDIAll:
			server.manager().playMidiAll(user, ((ForceMIDIAllRequest)packet).midiId);
			break;
			
		case ForceMIDIToMap:
			server.manager().playMidiToMap(user, ((ForceMIDIToMapRequest)packet).map, ((ForceMIDIToMapRequest)packet).midiId);
			break;
			
		case ForceWAVEAll:
			server.manager().playWaveAll(user, ((ForceWAVEAllRequest)packet).waveId);
			break;
			
		case ForceWAVEToMap:
			handleForceWaveToMap(user, (ForceWAVEToMapRequest)packet);
			break;
			
		case ShowServerForm:
			// n/a
			break;

		case SaveChars:
			server.manager().saveChars(user);
			break;
			
		case SaveMap:
			server.manager().saveMap(user);
			break;
			
		case ServerOpenToUsersToggle:
			server.manager().serverOpenToUsersToggle(user);
			break;
			
		case ReloadNPCs:
			// N/A:
			break;
			
		case ReloadObjects:
			server.reloadObjects(user);
			break;
			
		case ReloadSpells:
			server.reloadSpells(user);
			break;
			
		case ReloadServerIni:
			server.manager().reloadServerIni(user);
			break;
			
		case Restart:
			// N/A
			break;
			
		case AlterMail:
			server.manager().alterEmail(user, ((AlterMailRequest)packet).userName, ((AlterMailRequest)packet).newEmail);
			break;
			
		case AlterName:
			server.manager().alterName(user, ((AlterNameRequest)packet).userName, ((AlterNameRequest)packet).newName);
			break;
			
		case AlterPassword:
			// /APASS
			if (user.isGM()) {
				user.sendMessage("Por seguridad, no se puede cambiar passwords de este modo.", FontType.FONTTYPE_INFO);
				// TODO implementar otro mecanismo para cambio de passwords x email
			}
			break;
			
		case TileBlockedToggle:
			server.manager().tileBlockedToggle(user);
			break;

		case SetTrigger:
			server.manager().setTrigger(user, ((SetTriggerRequest)packet).trigger);
			break;
			
		case AskTrigger:
			server.manager().askTrigger(user);
			break;
			
		case CreateNPC:
			server.manager().createNpc(user, ((CreateNPCRequest)packet).npcIndex);
			break;
			
		case CreateNPCWithRespawn:
			server.manager().createNpcWithRespawn(user, ((CreateNPCWithRespawnRequest)packet).npcIndex);
			break;
					
		case ResetNPCInventory:
			server.manager().resetNpcInventory(user);
			break;
			
		case ImperialArmour:
			handleImperialArmour(user, (ImperialArmourRequest)packet);
			break;
			
		case ChaosArmour:
			handleChaosArmour(user, (ChaosArmourRequest)packet);
			break;
			
		case AcceptRoyalCouncilMember:
			server.manager().acceptRoyalCouncilMember(user, ((AcceptRoyalCouncilMemberRequest)packet).userName);
			break;
					
		case AcceptChaosCouncilMember:
			server.manager().acceptChaosCouncilMember(user, ((AcceptChaosCouncilMemberRequest)packet).userName);
			break;
			
		case CouncilKick:
			server.manager().councilKick(user, ((CouncilKickRequest)packet).userName);
			break;
			
		case RoyalArmyKick:
			server.manager().royalArmyKickForEver(user, ((RoyalArmyKickRequest)packet).userName);
			break;
			
		case ChaosLegionKick:
			server.manager().chaosLegionKickForEver(user, ((ChaosLegionKickRequest)packet).userName);
			break;
			
		case ResetFactions:
			server.manager().resetFactions(user, ((ResetFactionsRequest)packet).userName);
			break;

		case ResetAutoUpdate:
			// N/A
			break;
			
		case ToggleCentinelActivated:
			server.getWorkWatcher().workWatcherActivateToggle(user);
			break;
			
		case ChangeMapInfoBackup:
			ChangeMapInfo.changeMapInfoBackup(server, user, ((ChangeMapInfoBackupRequest)packet).doTheBackup == 1);
			break;
			
		case ChangeMapInfoLand:
			ChangeMapInfo.changeMapInfoLand(server, user, ((ChangeMapInfoLandRequest)packet).infoLand);
			break;
			
		case ChangeMapInfoZone:
			ChangeMapInfo.changeMapInfoZone(server, user, ((ChangeMapInfoZoneRequest)packet).infoZone);
			break;
			
		case ChangeMapInfoNoInvi:
			ChangeMapInfo.changeMapInfoNoInvi(server, user, ((ChangeMapInfoNoInviRequest)packet).noInvisible == 1);
			break;
			 
		case ChangeMapInfoNoMagic:
			ChangeMapInfo.changeMapInfoNoMagic(server, user, ((ChangeMapInfoNoMagicRequest)packet).noMagic == 1);
			break;			
			
		case ChangeMapInfoNoResu:
			ChangeMapInfo.changeMapInfoNoResu(server, user, ((ChangeMapInfoNoResuRequest)packet).noResu == 1);
			break;
			
		case ChangeMapInfoPK:
			ChangeMapInfo.changeMapInfoPK(server, user, ((ChangeMapInfoPKRequest)packet).isMapPk == 1);
			break;
			
		case ChangeMapInfoRestricted:
			ChangeMapInfo.changeMapInfoRestricted(server, user, ((ChangeMapInfoRestrictedRequest)packet).status);
			break;
			
		case EditChar:
			handleEditChar(user, (EditCharRequest)packet);
			break;
			
		case CheckSlot:
			handleCheckSlot(user, (CheckSlotRequest)packet);
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

	private void handleCheckSlot(User user, CheckSlotRequest packet) {
		GameServer.instance().manager().handleCheckSlot(user, packet.userName, packet.slot);
	}

	private void handleEditChar(User admin, EditCharRequest packet) {
		EditChar.handleEditCharacter(GameServer.instance(), 
				admin, packet.userName, packet.option, packet.param1, packet.param2);
	}

	private void handleImperialArmour(User admin, ImperialArmourRequest packet) {
		GameServer.instance().manager().royalArmyArmour(admin, packet.index, packet.objIndex);
	}
	
	private void handleChaosArmour(User admin, ChaosArmourRequest packet) {
		GameServer.instance().manager().darkLegionArmour(admin, packet.index, packet.objIndex);
	}

	private void handleIpToNick(User admin, IPToNickRequest packet) {
		String ip = "" + (packet.ip1&0xFF) + "." + (packet.ip2&0xFF) + "." + (packet.ip3&0xFF) + "." + (packet.ip4&0xFF);
		GameServer.instance().manager().ipToUserName(admin, ip);
	}

	private void handleForceWaveToMap(User user, ForceWAVEToMapRequest packet) {
		GameServer.instance().manager().playWavToMap(user, packet.waveId, packet.map, packet.x, packet.y);		
	}

	private void handleUnbanIp(User admin, UnbanIPRequest packet) {
		String bannedIP = "" + (packet.ip1&0xFF) + "." + (packet.ip2&0xFF) + "." + (packet.ip3&0xFF) + "." + (packet.ip4&0xFF);
		GameServer.instance().getBannIP().unbanIP(admin, bannedIP);
	}

	private void handleBanIp(User admin, BanIPRequest packet) {
		if (packet.byIP) {
			String bannedIP = "" + (packet.ip1&0xFF) + "." + (packet.ip2&0xFF) + "." + (packet.ip3&0xFF) + "." + (packet.ip4&0xFF);
			GameServer.instance().getBannIP().banIP(admin, bannedIP, packet.reason);
		} else {
			GameServer.instance().getBannIP().banIPUser(admin, packet.userName, packet.reason);
		}
	}

	private void handleBanChar(User admin, BanCharRequest packet) {
		GameServer.instance().getBannIP().banUser(admin, packet.userName, packet.reason);
	}

	private void handleJail(User admin, JailRequest packet) {
		GameServer.instance().manager().sendUserToJail(admin, packet.userName, packet.reason, packet.jailTime);
	}

	private void handleRemovePunishment(User user, RemovePunishmentRequest packet) {
		GameServer.instance().manager().removePunishment(user, packet.userName, packet.punishment, packet.newText); 
	}

	private void handleWarnUser(User user, WarnUserRequest packet) {
		GameServer.instance().manager().warnUser(user, packet.userName, packet.reason);
	}

	private void handleTeleportCreate(TeleportCreateRequest packet, User admin) {
		GameServer.instance().manager().createTeleport(admin, packet.mapa, packet.x, packet.y);
	}

	private void handleChatColor(ChatColorRequest packet, User admin) {
		admin.changeChatColor(packet.red & 0xff, packet.green & 0xff, packet.blue & 0xff);
	}

	private void handleWarpChar(WarpCharRequest packet, User admin) {
		GameServer.instance().manager().warpUserTo(admin, packet.userName, packet.map, packet.x, packet.y);
	}

	private void handleForumPost(ForumPostRequest packet, User user) {
		user.postOnForum(packet.title, packet.msg);
	}

	private void handleBankExtractItem(BankExtractItemRequest packet, User user) {
		user.getBankInventory().bankExtract(packet.slot, packet.amount);
	}

	private void handleBankDepositItem(BankDepositRequest packet, User user) {
		user.getBankInventory().bankDeposit(packet.slot, packet.amount);
	}

	private void handleLoginNewChar(LoginNewCharRequest packet, User user) {
		user.connectNewUser(packet.userName, packet.password, packet.race,
				packet.gender, packet.clazz, packet.email, packet.homeland);
	}

	private void handleCommerceSell(CommerceSellRequest packet, User user) {
		user.getUserTrade().commerceSellToMerchant(packet.slot, packet.amount);		
	}

	private void handleCommerceBuy(CommerceBuyRequest packet, User user) {
		user.getUserTrade().commerceBuyFromMerchant(packet.slot, packet.amount);
	}

	private void handleEquipItem(EquipItemRequest packet, User user) {
		user.equipItem(packet.itemSlot);
	}

	private void handleDrop(DropRequest packet, User user) {
		user.dropObject(packet.slot, packet.amount);
	}

	private void handleWork(WorkRequest packet, User user) {
		user.handleWork(packet.skill);
	}

	private void handleCastSpell(CastSpellRequest packet, User user) {
		user.castSpell(packet.spell);
	}

	private void handleMoveSpell(MoveSpellRequest packet, User user) {
		// Packet.dir is boolean. 
		// - Upwards direction if TRUE(1)
		// - Downward direction if FALSE(0).
		// Packet.spell is Spell's Slot in ( 1 .. MAXSLOT )
		user.moveSpell(packet.spell, 
				(byte) (packet.dir == 1 ? -1 : 1));
	}

	private void handleChangeHeading(ChangeHeadingRequest packet, User user) {
		user.changeHeading(packet.heading);
	}

	private void handleWorkLeftClick(WorkLeftClickRequest packet, User user) {
		user.workLeftClick(packet.x, packet.y, packet.skill);
	}

	private void handleDoubleClick(DoubleClickRequest packet, User user) {
		user.doubleClickOnMap(packet.x, packet.y);
	}

	private void handleLeftClick(LeftClickRequest packet, User user) {
		user.leftClickOnMap(packet.x, packet.y);		
	}

	private void handleWalk(WalkRequest packet, User user) {
		user.walk(Heading.value(packet.heading));
	}
	
	private void handleTalk(TalkRequest packet, User user) {
		user.talk(packet.chat);
	}

	private void handleLoginExistingChar(LoginExistingCharRequest packet, User user) {
		user.connectUser(packet.userName, packet.password);
	}
	
}