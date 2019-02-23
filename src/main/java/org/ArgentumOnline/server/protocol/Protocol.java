package org.ArgentumOnline.server.protocol;

public class Protocol implements IProtocol {

	@Override
	public void handleLoginexistingchar(String userName, String password, byte version1, byte version2, byte version3,
			short versionGrafs, short versionWavs, short versionMidis, short versionInits, short versionMapas,
			short versionAoExe, short versionExtras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleThrowdices() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLoginnewchar(String userName, String password, byte version1, byte version2, byte version3,
			short versionGrafs, short versionWavs, short versionMidis, short versionInits, short versionMapas,
			short versionAoExe, short versionExtras, byte race, byte gender, byte clazz, byte[] skills, String email,
			byte homeland) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTalk(String chat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleYell(String chat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWhisper(short targetCharIndex, String chat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWalk(byte heading) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestpositionupdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleAttack() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePickup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCombatmodetoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSafetoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleResuscitationtoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestguildleaderinfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestatributes() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestfame() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestskills() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestministats() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCommerceend() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUsercommerceend() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBankend() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUsercommerceok() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUsercommercereject() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDrop(byte slot, short amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCastspell(byte spell) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLeftclick(byte x, byte y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDoubleclick(byte x, byte y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWork(byte skill) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUsespellmacro() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUseitem(byte slot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCraftblacksmith(short item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCraftcarpenter(short item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWorkleftclick(byte x, byte y, byte skill) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCreatenewguild(String desc, String guildName, String site, String codex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSpellinfo(byte spellSlot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEquipitem(byte itemSlot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangeheading(byte heading) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleModifyskills(byte[] skills) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTrain(byte petIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCommercebuy(byte slot, short amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBankextractitem(byte slot, short amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCommercesell(byte slot, short amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBankdeposit(byte slot, short amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleForumpost(String title, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMovespell(byte dir, byte spell) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMovebank(byte dir, byte slot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleClancodexupdate(String desc, String codex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUsercommerceoffer(byte slot, int amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildacceptpeace(String guild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildrejectalliance(String guild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildrejectpeace(String guild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildacceptalliance(String guild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildofferpeace(String guild, String proposal) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildofferalliance(String guild, String proposal) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildalliancedetails(String guild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildpeacedetails(String guild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildrequestjoinerinfo(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildallianceproplist() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEguildpeaceproplist() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuilddeclarewar(String guild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildnewwebsite(String website) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildacceptnewmember(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildrejectnewmember(String userName, String reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildkickmember(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildupdatenews(String news) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildmemberinfo(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildopenelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildrequestmembership(String guild, String application) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildrequestdetails(String guild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOnline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleQuit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildleave() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestaccountstate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePetstand() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePetfollow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTrainlist() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMeditate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleResucitate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleHeal() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequeststats() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleHelp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCommercestart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBankstart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEnlist() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleInformation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleReward() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestmotd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUptime() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePartyleave() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePartycreate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePartyjoin() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleInquiry() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildmessage(String chat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePartymessage(String chat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCentinelreport(short key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildonline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePartyonline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCouncilmessage(String chat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRolemasterrequest(String request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGmrequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBugreport(String bugReport) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangedescription(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildvote(String vote) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePunishments(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangepassword(String oldPassword, String newPassword) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGamble(short amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleInquiryvote(byte opt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBankextractgold(int amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLeavefaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBankdepositgold(int amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDenounce(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildfundate(byte clanType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePartykick(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePartysetleader(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePartyacceptmember(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildmemberlist(String guild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGmmessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleShowname() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOnlineroyalarmy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOnlinechaoslegion() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGonearby(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleComment(String comment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleServertime() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWhere(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCreaturesinmap(short map) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWarpmetotarget() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWarpchar(String userName, byte x, byte y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSilence(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSosshowlist() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSosremove(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGotochar(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleInvisible() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGmpanel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestuserlist() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWorking() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleHiding() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleJail(String userName, String reason, byte jailTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleKillnpc() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWarnuser(String userName, String reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEditchar(String userName, byte option, String param1, String param2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestcharinfo(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestcharstats(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestchargold(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestcharinventory(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestcharbank(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestcharskills(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRevivechar(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOnlinegm() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOnlinemap(short map) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleForgive(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleKick(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleExecute(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBanchar(String userName, String reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUnbanchar(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleNpcfollow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSummonchar(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSpawnlistrequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSpawncreature(short npc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleResetnpcinventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCleanworld() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleServermessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleNicktoip(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleIptonick(byte ip1, byte ip2, byte ip3, byte ip4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildonlinemembers(String guildName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTeleportcreate(short mapa, byte x, byte y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTeleportdestroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRaintoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSetchardescription(String desc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleForcemiditomap(byte midiId, short map) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleForcewavetomap(byte waveId, short map, byte x, byte y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRoyalarmymessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChaoslegionmessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCitizenmessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCriminalmessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTalkasnpc(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDestroyallitemsinarea() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleAcceptroyalcouncilmember(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleAcceptchaoscouncilmember(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleItemsinthefloor() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMakedumb(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDumbnomore(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDumpiptables() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCouncilkick(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSettrigger() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleAsktrigger() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBannediplist() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBannedipreload() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleGuildban(String guildName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBanip_1(byte ip1, byte ip2, byte ip3, byte ip4, String reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBanip_0(String userName, String reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUnbanip(byte ip1, byte ip2, byte ip3, byte ip4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCreateitem(short objectIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDestroyitems() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChaoslegionkick(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRoyalarmykick(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleForcemidiall(byte midiId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleForcewaveall(byte waveId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRemovepunishment(String userName, byte punishment, String newText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTileblockedtoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleKillnpcnorespawn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleKillallnearbynpcs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLastip(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChatcolor(byte red, byte green, byte blue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleIgnored() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCheckslot(String userName, byte slot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleResetautoupdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRestart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleReloadobjects() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleReloadspells() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleReloadserverini() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleReloadnpcs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleKickallchars() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleNight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleShowserverform() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCleansos() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSavechars() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangemapinfobackup(byte doTheBackup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangemapinfopk(byte isMapPk) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangemapinforestricted(String status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangemapinfonomagic(byte noMagic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangemapinfonoinvi(byte noInvisible) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangemapinfonoresu(byte noResu) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangemapinfoland(String infoLand) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangemapinfozone(String infoZone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSavemap() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleShowguildmessages(String guild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDobackup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTogglecentinelactivated() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleAltername(String userName, String newName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleAltermail(String userName, String newEmail) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleAlterpassword(String userName, String copyFrom) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCreatenpc(short npcIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCreatenpcwithrespawn(short npcIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleImperialarmour(byte index, short objIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChaosarmour(byte index, short objIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleNavigatetoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleServeropentouserstoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTurnoffserver() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTurncriminal(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleResetfactions(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRemovecharfromguild(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequestcharmail(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSystemmessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSetmotd(String newMOTD) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChangemotd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSetinivar(String section, String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeLoggedmessage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeRemovealldialogs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeRemovechardialog(short charIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeNavigatetoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeDisconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCommerceend() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBankend() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCommerceinit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBankinit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUsercommerceinit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUsercommerceend() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeShowblacksmithform() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeShowcarpenterform() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeNpcswing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeNpckilluser() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBlockedwithshielduser() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBlockedwithshieldother() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUserswing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUpdateneeded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeSafemodeon() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeSafemodeoff() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeResuscitationsafeon() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeResuscitationsafeoff() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeNobilitylost() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCantusewhilemeditating() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUpdatesta(short minSta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUpdatemana(short minMan) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUpdatehp(short minHP) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUpdategold(int gold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUpdateexp(int exp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeChangemap(short map, short version) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writePosupdate(byte x, byte y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeNpchituser(byte target, short damage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUserhitnpc(int damage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUserattackedswing(short charIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUserhittedbyuser(short attackerChar, byte target, short damage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUserhitteduser(short attackedChar, byte target, short damage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeChatoverhead(String chat, short charIndex, byte red, byte green, byte blue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeConsolemsg(String chat, byte fontIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeGuildchat(String chat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeShowmessagebox(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUserindexinserver(short userIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUsercharindexinserver(short charIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCharactercreate(short charIndex, short body, short head, byte heading, byte x, byte y,
			short weapon, short shield, short helmet, short fx, short fxLoops, String name, byte criminal,
			byte privileges) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCharacterremove(short charIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCharactermove(short charIndex, byte x, byte y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeForcecharmove(byte heading) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCharacterchange(short charIndex, short body, short head, byte heading, short weapon, short shield,
			short helmet, short fx, short fxLoops) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeObjectcreate(byte x, byte y, short grhIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeObjectdelete(byte x, byte y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBlockposition(byte x, byte y, byte blocked) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writePlaymidi(byte midi, short loops) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writePlaywave(byte wave, byte x, byte y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeGuildlist(String members) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeAreachanged(byte x, byte y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writePausetoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeRaintoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCreatefx(short charIndex, short fx, short fxLoops) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUpdateuserstats(short maxHP, short minHP, short maxMAN, short minMAN, short maxSTA, short minSTA,
			int gold, byte elv, int elu, int exp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeWorkrequesttarget(byte skill) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeChangeinventoryslot(byte slot, short objIndex, String name, short amount, byte equiped,
			short grhIndex, byte objType, short maxHIT, short minHIT, short def, float valor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeChangebankslot(byte slot, short objIndex, String name, short amount, short grhIndex, byte objType,
			short maxHIT, short minHIT, short def, int valor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeChangespellslot(byte slot, short spell, String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeAttributes(byte fuerza, byte agilidad, byte inteligencia, byte carisma, byte constitucion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBlacksmithweapons(short count, BlacksmithItem[] weapons) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBlacksmitharmors(short count, BlacksmithItem[] armors) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCarpenterobjects(short count, CarpenterItem[] objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeRestok() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeErrormsg(String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBlind() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeDumb() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeShowsignal(String texto, short grhSecundario) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeChangenpcinventoryslot(byte slot, String name, short amount, float price, short grhIndex,
			short objIndex, byte objType, short maxHIT, short minHIT, short def) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUpdatehungerandthirst(byte maxAGU, byte minAGU, byte maxHAM, byte minHAM) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeFame(int asesinoRep, int bandidoRep, int burguesRep, int ladronRep, int nobleRep, int pebleRep,
			int promedio) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeMinistats(int ciudadanosMatados, int criminalesMatados, int usuariosMatados, short npcsMatados,
			byte clase, int pena) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeLevelup(short skillPoints) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeAddforummsg(String title, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeShowforumform() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeSetinvisible(short charIndex, byte invisible) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeDiceroll(byte fuerza, byte agilidad, byte inteligencia, byte carisma, byte constitucion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeMeditatetoggle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBlindnomore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeDumbnomore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeSendskills(byte[] skills) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeTrainercreaturelist(String npcList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeGuildnews(String guildNews, String enemiesList, String alliesList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeOfferdetails(String details) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeAlianceproposalslist(String guildsList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writePeaceproposalslist(String guildsList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCharacterinfo(String charName, byte race, byte clazz, byte gender, byte level, int gold, int bank,
			int reputation, String previousPetitions, String currentGuild, String previousGuilds, byte royalArmy,
			byte caosLegion, int citizensKilled, int criminalsKilled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeGuildleaderinfo(String guildList, String memberList, String guildNews, String requestsList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeGuilddetails(String guildName, String founder, String foundationDate, String leader, String url,
			short memberCount, byte electionsOpen, String alignment, short enemiesCount, short alliesCount,
			String antifactionPoints, String codex, String guildDesc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeShowguildfundationform() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeParalizeok() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeShowuserrequest(String details) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeTradeok() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBankok() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeChangeusertradeslot(short objIndex, String name, int amount, short grhIndex, byte objType,
			short maxHIT, short minHIT, short def, int valor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeSendnight(byte night) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeSpawnlist(String npcNamesList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeShowsosform(String sosList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeShowmotdeditionform(String currentMOTD) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeShowgmpanelform() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUsernamelist(String userNamesList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writePong() {
		// TODO Auto-generated method stub

	}

}
