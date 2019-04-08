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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.GameServer;
import org.argentumonline.server.protocol.*;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;

class RequestDecoder extends ReplayingDecoder<ClientPacket> {
	private static Logger log = LogManager.getLogger();
	
	GameServer server = GameServer.instance();
	ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug("=> opened client connection");
		clients.add(ctx.channel());
		server.createUser(ctx.channel());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.debug("=> closed client connection");
		var server = GameServer.instance();
		var user = server.findUser(ctx.channel());
		if (user.isPresent()) {
			user.get().quitGame();
		}
		clients.remove(ctx.channel()); 
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		in.markReaderIndex();

		int id = in.readByte();
		id = id & 0xFF; // signed byte to unsigned
		ClientPacketID packetId = ClientPacketID.value(id);
		Packet packet = null;

		switch (packetId) {
		case LoginExistingChar:
			packet = LoginExistingCharRequest.decode(in);
			break;
		case ThrowDices:
			packet = ThrowDicesRequest.decode(in);
			break;
		case LoginNewChar:
			packet = LoginNewCharRequest.decode(in);
			break;
		case Talk:
			packet = TalkRequest.decode(in);
			break;
		case Yell:
			packet = YellRequest.decode(in);
			break;
		case Whisper:
			packet = WhisperRequest.decode(in);
			break;
		case Walk:
			packet = WalkRequest.decode(in);
			break;
		case RequestPositionUpdate:
			packet = RequestPositionUpdateRequest.decode(in);
			break;
		case Attack:
			packet = AttackRequest.decode(in);
			break;
		case PickUp:
			packet = PickUpRequest.decode(in);
			break;
		case CombatModeToggle:
			packet = CombatModeToggleRequest.decode(in);
			break;
		case SafeToggle:
			packet = SafeToggleRequest.decode(in);
			break;
		case ResuscitationToggle:
			packet = ResuscitationToggleRequest.decode(in);
			break;
		case RequestGuildLeaderInfo:
			packet = RequestGuildLeaderInfoRequest.decode(in);
			break;
		case RequestAtributes:
			packet = RequestAtributesRequest.decode(in);
			break;
		case RequestFame:
			packet = RequestFameRequest.decode(in);
			break;
		case RequestSkills:
			packet = RequestSkillsRequest.decode(in);
			break;
		case RequestMiniStats:
			packet = RequestMiniStatsRequest.decode(in);
			break;
		case CommerceEnd:
			packet = CommerceEndRequest.decode(in);
			break;
		case UserCommerceEnd:
			packet = UserCommerceEndRequest.decode(in);
			break;
		case BankEnd:
			packet = BankEndRequest.decode(in);
			break;
		case UserCommerceOk:
			packet = UserCommerceOkRequest.decode(in);
			break;
		case UserCommerceReject:
			packet = UserCommerceRejectRequest.decode(in);
			break;
		case Drop:
			packet = DropRequest.decode(in);
			break;
		case CastSpell:
			packet = CastSpellRequest.decode(in);
			break;
		case LeftClick:
			packet = LeftClickRequest.decode(in);
			break;
		case DoubleClick:
			packet = DoubleClickRequest.decode(in);
			break;
		case Work:
			packet = WorkRequest.decode(in);
			break;
		case UseSpellMacro:
			packet = UseSpellMacroRequest.decode(in);
			break;
		case UseItem:
			packet = UseItemRequest.decode(in);
			break;
		case CraftBlacksmith:
			packet = CraftBlacksmithRequest.decode(in);
			break;
		case CraftCarpenter:
			packet = CraftCarpenterRequest.decode(in);
			break;
		case WorkLeftClick:
			packet = WorkLeftClickRequest.decode(in);
			break;
		case CreateNewGuild:
			packet = CreateNewGuildRequest.decode(in);
			break;
		case SpellInfo:
			packet = SpellInfoRequest.decode(in);
			break;
		case EquipItem:
			packet = EquipItemRequest.decode(in);
			break;
		case ChangeHeading:
			packet = ChangeHeadingRequest.decode(in);
			break;
		case ModifySkills:
			packet = ModifySkillsRequest.decode(in);
			break;
		case Train:
			packet = TrainRequest.decode(in);
			break;
		case CommerceBuy:
			packet = CommerceBuyRequest.decode(in);
			break;
		case BankExtractItem:
			packet = BankExtractItemRequest.decode(in);
			break;
		case CommerceSell:
			packet = CommerceSellRequest.decode(in);
			break;
		case BankDeposit:
			packet = BankDepositRequest.decode(in);
			break;
		case ForumPost:
			packet = ForumPostRequest.decode(in);
			break;
		case MoveSpell:
			packet = MoveSpellRequest.decode(in);
			break;
		case MoveBank:
			packet = MoveBankRequest.decode(in);
			break;
		case ClanCodexUpdate:
			packet = ClanCodexUpdateRequest.decode(in);
			break;
		case UserCommerceOffer:
			packet = UserCommerceOfferRequest.decode(in);
			break;
		case GuildAcceptPeace:
			packet = GuildAcceptPeaceRequest.decode(in);
			break;
		case GuildRejectAlliance:
			packet = GuildRejectAllianceRequest.decode(in);
			break;
		case GuildRejectPeace:
			packet = GuildRejectPeaceRequest.decode(in);
			break;
		case GuildAcceptAlliance:
			packet = GuildAcceptAllianceRequest.decode(in);
			break;
		case GuildOfferPeace:
			packet = GuildOfferPeaceRequest.decode(in);
			break;
		case GuildOfferAlliance:
			packet = GuildOfferAllianceRequest.decode(in);
			break;
		case GuildAllianceDetails:
			packet = GuildAllianceDetailsRequest.decode(in);
			break;
		case GuildPeaceDetails:
			packet = GuildPeaceDetailsRequest.decode(in);
			break;
		case GuildRequestJoinerInfo:
			packet = GuildRequestJoinerInfoRequest.decode(in);
			break;
		case GuildAlliancePropList:
			packet = GuildAlliancePropListRequest.decode(in);
			break;
		case GuildPeacePropList:
			packet = GuildPeacePropListRequest.decode(in);
			break;
		case GuildDeclareWar:
			packet = GuildDeclareWarRequest.decode(in);
			break;
		case GuildNewWebsite:
			packet = GuildNewWebsiteRequest.decode(in);
			break;
		case GuildAcceptNewMember:
			packet = GuildAcceptNewMemberRequest.decode(in);
			break;
		case GuildRejectNewMember:
			packet = GuildRejectNewMemberRequest.decode(in);
			break;
		case GuildKickMember:
			packet = GuildKickMemberRequest.decode(in);
			break;
		case GuildUpdateNews:
			packet = GuildUpdateNewsRequest.decode(in);
			break;
		case GuildMemberInfo:
			packet = GuildMemberInfoRequest.decode(in);
			break;
		case GuildOpenElections:
			packet = GuildOpenElectionsRequest.decode(in);
			break;
		case GuildRequestMembership:
			packet = GuildRequestMembershipRequest.decode(in);
			break;
		case GuildRequestDetails:
			packet = GuildRequestDetailsRequest.decode(in);
			break;
		case Online:
			packet = OnlineRequest.decode(in);
			break;
		case Quit:
			packet = QuitRequest.decode(in);
			break;
		case GuildLeave:
			packet = GuildLeaveRequest.decode(in);
			break;
		case RequestAccountState:
			packet = RequestAccountStateRequest.decode(in);
			break;
		case PetStand:
			packet = PetStandRequest.decode(in);
			break;
		case PetFollow:
			packet = PetFollowRequest.decode(in);
			break;
		case TrainList:
			packet = TrainListRequest.decode(in);
			break;
		case Rest:
			packet = RestRequest.decode(in);
			break;
		case Meditate:
			packet = MeditateRequest.decode(in);
			break;
		case Resuscitate:
			packet = ResuscitateRequest.decode(in);
			break;
		case Heal:
			packet = HealRequest.decode(in);
			break;
		case RequestStats:
			packet = RequestStatsRequest.decode(in);
			break;
		case Help:
			packet = HelpRequest.decode(in);
			break;
		case CommerceStart:
			packet = CommerceStartRequest.decode(in);
			break;
		case BankStart:
			packet = BankStartRequest.decode(in);
			break;
		case Enlist:
			packet = EnlistRequest.decode(in);
			break;
		case Information:
			packet = InformationRequest.decode(in);
			break;
		case Reward:
			packet = RewardRequest.decode(in);
			break;
		case RequestMOTD:
			packet = RequestMOTDRequest.decode(in);
			break;
		case Uptime:
			packet = UptimeRequest.decode(in);
			break;
		case PartyLeave:
			packet = PartyLeaveRequest.decode(in);
			break;
		case PartyCreate:
			packet = PartyCreateRequest.decode(in);
			break;
		case PartyJoin:
			packet = PartyJoinRequest.decode(in);
			break;
		case Inquiry:
			packet = InquiryRequest.decode(in);
			break;
		case GuildMessage:
			packet = GuildMessageRequest.decode(in);
			break;
		case PartyMessage:
			packet = PartyMessageRequest.decode(in);
			break;
		case CentinelReport:
			packet = CentinelReportRequest.decode(in);
			break;
		case GuildOnline:
			packet = GuildOnlineRequest.decode(in);
			break;
		case PartyOnline:
			packet = PartyOnlineRequest.decode(in);
			break;
		case CouncilMessage:
			packet = CouncilMessageRequest.decode(in);
			break;
		case RoleMasterRequest:
			packet = RoleMasterRequestRequest.decode(in);
			break;
		case GMRequest:
			packet = GMRequestRequest.decode(in);
			break;
		case BugReport:
			packet = BugReportRequest.decode(in);
			break;
		case ChangeDescription:
			packet = ChangeDescriptionRequest.decode(in);
			break;
		case GuildVote:
			packet = GuildVoteRequest.decode(in);
			break;
		case Punishments:
			packet = PunishmentsRequest.decode(in);
			break;
		case ChangePassword:
			packet = ChangePasswordRequest.decode(in);
			break;
		case Gamble:
			packet = GambleRequest.decode(in);
			break;
		case InquiryVote:
			packet = InquiryVoteRequest.decode(in);
			break;
		case BankExtractGold:
			packet = BankExtractGoldRequest.decode(in);
			break;
		case LeaveFaction:
			packet = LeaveFactionRequest.decode(in);
			break;
		case BankDepositGold:
			packet = BankDepositGoldRequest.decode(in);
			break;
		case Denounce:
			packet = DenounceRequest.decode(in);
			break;
		case GuildFundate:
			packet = GuildFundateRequest.decode(in);
			break;
		case PartyKick:
			packet = PartyKickRequest.decode(in);
			break;
		case PartySetLeader:
			packet = PartySetLeaderRequest.decode(in);
			break;
		case PartyAcceptMember:
			packet = PartyAcceptMemberRequest.decode(in);
			break;
		case GuildMemberList:
			packet = GuildMemberListRequest.decode(in);
			break;
		case GMMessage:
			packet = GMMessageRequest.decode(in);
			break;
		case ShowName:
			packet = ShowNameRequest.decode(in);
			break;
		case OnlineRoyalArmy:
			packet = OnlineRoyalArmyRequest.decode(in);
			break;
		case OnlineChaosLegion:
			packet = OnlineChaosLegionRequest.decode(in);
			break;
		case GoNearby:
			packet = GoNearbyRequest.decode(in);
			break;
		case Comment:
			packet = CommentRequest.decode(in);
			break;
		case ServerTime:
			packet = ServerTimeRequest.decode(in);
			break;
		case Where:
			packet = WhereRequest.decode(in);
			break;
		case CreaturesInMap:
			packet = CreaturesInMapRequest.decode(in);
			break;
		case WarpMeToTarget:
			packet = WarpMeToTargetRequest.decode(in);
			break;
		case WarpChar:
			packet = WarpCharRequest.decode(in);
			break;
		case Silence:
			packet = SilenceRequest.decode(in);
			break;
		case SOSShowList:
			packet = SOSShowListRequest.decode(in);
			break;
		case SOSRemove:
			packet = SOSRemoveRequest.decode(in);
			break;
		case GoToChar:
			packet = GoToCharRequest.decode(in);
			break;
		case Invisible:
			packet = InvisibleRequest.decode(in);
			break;
		case GMPanel:
			packet = GMPanelRequest.decode(in);
			break;
		case RequestUserList:
			packet = RequestUserListRequest.decode(in);
			break;
		case Working:
			packet = WorkingRequest.decode(in);
			break;
		case Hiding:
			packet = HidingRequest.decode(in);
			break;
		case Jail:
			packet = JailRequest.decode(in);
			break;
		case KillNPC:
			packet = KillNPCRequest.decode(in);
			break;
		case WarnUser:
			packet = WarnUserRequest.decode(in);
			break;
		case EditChar:
			packet = EditCharRequest.decode(in);
			break;
		case RequestCharInfo:
			packet = RequestCharInfoRequest.decode(in);
			break;
		case RequestCharStats:
			packet = RequestCharStatsRequest.decode(in);
			break;
		case RequestCharGold:
			packet = RequestCharGoldRequest.decode(in);
			break;
		case RequestCharInventory:
			packet = RequestCharInventoryRequest.decode(in);
			break;
		case RequestCharBank:
			packet = RequestCharBankRequest.decode(in);
			break;
		case RequestCharSkills:
			packet = RequestCharSkillsRequest.decode(in);
			break;
		case ReviveChar:
			packet = ReviveCharRequest.decode(in);
			break;
		case OnlineGM:
			packet = OnlineGMRequest.decode(in);
			break;
		case OnlineMap:
			packet = OnlineMapRequest.decode(in);
			break;
		case Forgive:
			packet = ForgiveRequest.decode(in);
			break;
		case Kick:
			packet = KickRequest.decode(in);
			break;
		case Execute:
			packet = ExecuteRequest.decode(in);
			break;
		case BanChar:
			packet = BanCharRequest.decode(in);
			break;
		case UnbanChar:
			packet = UnbanCharRequest.decode(in);
			break;
		case NPCFollow:
			packet = NPCFollowRequest.decode(in);
			break;
		case SummonChar:
			packet = SummonCharRequest.decode(in);
			break;
		case SpawnListRequest:
			packet = SpawnListRequestRequest.decode(in);
			break;
		case SpawnCreature:
			packet = SpawnCreatureRequest.decode(in);
			break;
		case ResetNPCInventory:
			packet = ResetNPCInventoryRequest.decode(in);
			break;
		case CleanWorld:
			packet = CleanWorldRequest.decode(in);
			break;
		case ServerMessage:
			packet = ServerMessageRequest.decode(in);
			break;
		case NickToIP:
			packet = NickToIPRequest.decode(in);
			break;
		case IPToNick:
			packet = IPToNickRequest.decode(in);
			break;
		case GuildOnlineMembers:
			packet = GuildOnlineMembersRequest.decode(in);
			break;
		case TeleportCreate:
			packet = TeleportCreateRequest.decode(in);
			break;
		case TeleportDestroy:
			packet = TeleportDestroyRequest.decode(in);
			break;
		case RainToggle:
			packet = RainToggleRequest.decode(in);
			break;
		case SetCharDescription:
			packet = SetCharDescriptionRequest.decode(in);
			break;
		case ForceMIDIToMap:
			packet = ForceMIDIToMapRequest.decode(in);
			break;
		case ForceWAVEToMap:
			packet = ForceWAVEToMapRequest.decode(in);
			break;
		case RoyalArmyMessage:
			packet = RoyalArmyMessageRequest.decode(in);
			break;
		case ChaosLegionMessage:
			packet = ChaosLegionMessageRequest.decode(in);
			break;
		case CitizenMessage:
			packet = CitizenMessageRequest.decode(in);
			break;
		case CriminalMessage:
			packet = CriminalMessageRequest.decode(in);
			break;
		case TalkAsNPC:
			packet = TalkAsNPCRequest.decode(in);
			break;
		case DestroyAllItemsInArea:
			packet = DestroyAllItemsInAreaRequest.decode(in);
			break;
		case AcceptRoyalCouncilMember:
			packet = AcceptRoyalCouncilMemberRequest.decode(in);
			break;
		case AcceptChaosCouncilMember:
			packet = AcceptChaosCouncilMemberRequest.decode(in);
			break;
		case ItemsInTheFloor:
			packet = ItemsInTheFloorRequest.decode(in);
			break;
		case MakeDumb:
			packet = MakeDumbRequest.decode(in);
			break;
		case MakeDumbNoMore:
			packet = MakeDumbNoMoreRequest.decode(in);
			break;
		case DumpIPTables:
			packet = DumpIPTablesRequest.decode(in);
			break;
		case CouncilKick:
			packet = CouncilKickRequest.decode(in);
			break;
		case SetTrigger:
			packet = SetTriggerRequest.decode(in);
			break;
		case AskTrigger:
			packet = AskTriggerRequest.decode(in);
			break;
		case BannedIPList:
			packet = BannedIPListRequest.decode(in);
			break;
		case BannedIPReload:
			packet = BannedIPReloadRequest.decode(in);
			break;
		case GuildBan:
			packet = GuildBanRequest.decode(in);
			break;
		case BanIP:
			packet = BanIPRequest.decode(in);
			break;
		case UnbanIP:
			packet = UnbanIPRequest.decode(in);
			break;
		case CreateItem:
			packet = CreateItemRequest.decode(in);
			break;
		case DestroyItems:
			packet = DestroyItemsRequest.decode(in);
			break;
		case ChaosLegionKick:
			packet = ChaosLegionKickRequest.decode(in);
			break;
		case RoyalArmyKick:
			packet = RoyalArmyKickRequest.decode(in);
			break;
		case ForceMIDIAll:
			packet = ForceMIDIAllRequest.decode(in);
			break;
		case ForceWAVEAll:
			packet = ForceWAVEAllRequest.decode(in);
			break;
		case RemovePunishment:
			packet = RemovePunishmentRequest.decode(in);
			break;
		case TileBlockedToggle:
			packet = TileBlockedToggleRequest.decode(in);
			break;
		case KillNPCNoRespawn:
			packet = KillNPCNoRespawnRequest.decode(in);
			break;
		case KillAllNearbyNPCs:
			packet = KillAllNearbyNPCsRequest.decode(in);
			break;
		case LastIP:
			packet = LastIPRequest.decode(in);
			break;
		case ChatColor:
			packet = ChatColorRequest.decode(in);
			break;
		case Ignored:
			packet = IgnoredRequest.decode(in);
			break;
		case CheckSlot:
			packet = CheckSlotRequest.decode(in);
			break;
		case ResetAutoUpdate:
			packet = ResetAutoUpdateRequest.decode(in);
			break;
		case Restart:
			packet = RestartRequest.decode(in);
			break;
		case ReloadObjects:
			packet = ReloadObjectsRequest.decode(in);
			break;
		case ReloadSpells:
			packet = ReloadSpellsRequest.decode(in);
			break;
		case ReloadServerIni:
			packet = ReloadServerIniRequest.decode(in);
			break;
		case ReloadNPCs:
			packet = ReloadNPCsRequest.decode(in);
			break;
		case KickAllChars:
			packet = KickAllCharsRequest.decode(in);
			break;
		case Night:
			packet = NightRequest.decode(in);
			break;
		case ShowServerForm:
			packet = ShowServerFormRequest.decode(in);
			break;
		case CleanSOS:
			packet = CleanSOSRequest.decode(in);
			break;
		case SaveChars:
			packet = SaveCharsRequest.decode(in);
			break;
		case ChangeMapInfoBackup:
			packet = ChangeMapInfoBackupRequest.decode(in);
			break;
		case ChangeMapInfoPK:
			packet = ChangeMapInfoPKRequest.decode(in);
			break;
		case ChangeMapInfoRestricted:
			packet = ChangeMapInfoRestrictedRequest.decode(in);
			break;
		case ChangeMapInfoNoMagic:
			packet = ChangeMapInfoNoMagicRequest.decode(in);
			break;
		case ChangeMapInfoNoInvi:
			packet = ChangeMapInfoNoInviRequest.decode(in);
			break;
		case ChangeMapInfoNoResu:
			packet = ChangeMapInfoNoResuRequest.decode(in);
			break;
		case ChangeMapInfoLand:
			packet = ChangeMapInfoLandRequest.decode(in);
			break;
		case ChangeMapInfoZone:
			packet = ChangeMapInfoZoneRequest.decode(in);
			break;
		case SaveMap:
			packet = SaveMapRequest.decode(in);
			break;
		case ShowGuildMessages:
			packet = ShowGuildMessagesRequest.decode(in);
			break;
		case DoBackUp:
			packet = DoBackUpRequest.decode(in);
			break;
		case ToggleCentinelActivated:
			packet = ToggleCentinelActivatedRequest.decode(in);
			break;
		case AlterName:
			packet = AlterNameRequest.decode(in);
			break;
		case AlterMail:
			packet = AlterMailRequest.decode(in);
			break;
		case AlterPassword:
			packet = AlterPasswordRequest.decode(in);
			break;
		case CreateNPC:
			packet = CreateNPCRequest.decode(in);
			break;
		case CreateNPCWithRespawn:
			packet = CreateNPCWithRespawnRequest.decode(in);
			break;
		case ImperialArmour:
			packet = ImperialArmourRequest.decode(in);
			break;
		case ChaosArmour:
			packet = ChaosArmourRequest.decode(in);
			break;
		case NavigateToggle:
			packet = NavigateToggleRequest.decode(in);
			break;
		case ServerOpenToUsersToggle:
			packet = ServerOpenToUsersToggleRequest.decode(in);
			break;
		case TurnOffServer:
			packet = TurnOffServerRequest.decode(in);
			break;
		case TurnCriminal:
			packet = TurnCriminalRequest.decode(in);
			break;
		case ResetFactions:
			packet = ResetFactionsRequest.decode(in);
			break;
		case RemoveCharFromGuild:
			packet = RemoveCharFromGuildRequest.decode(in);
			break;
		case RequestCharMail:
			packet = RequestCharMailRequest.decode(in);
			break;
		case SystemMessage:
			packet = SystemMessageRequest.decode(in);
			break;
		case SetMOTD:
			packet = SetMOTDRequest.decode(in);
			break;
		case ChangeMOTD:
			packet = ChangeMOTDRequest.decode(in);
			break;
		case Ping:
			packet = PingRequest.decode(in);
			break;
		case SetIniVar:
			packet = SetIniVarRequest.decode(in);
			break;
		}

		if (packet == null) {
			in.resetReaderIndex();
			return;
		}

		out.add(packet); // add packet to handle it
	}
}
