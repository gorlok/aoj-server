package org.ArgentumOnline.server.protocol;

public interface IProtocol {
	
	void handleLoginexistingchar(String userName, String password, byte version1, byte version2, byte version3,
			short versionGrafs, short versionWavs, short versionMidis, short versionInits, short versionMapas,
			short versionAoExe, short versionExtras);

	void handleThrowdices();

	void handleLoginnewchar(String userName, String password, byte version1, byte version2, byte version3,
			short versionGrafs, short versionWavs, short versionMidis, short versionInits, short versionMapas,
			short versionAoExe, short versionExtras, byte race, byte gender, byte clazz, byte[] skills, String email,
			byte homeland);

	void handleTalk(String chat);

	void handleYell(String chat);

	void handleWhisper(short targetCharIndex, String chat);

	void handleWalk(byte heading);

	void handleRequestpositionupdate();

	void handleAttack();

	void handlePickup();

	void handleCombatmodetoggle();

	void handleSafetoggle();

	void handleResuscitationtoggle();

	void handleRequestguildleaderinfo();

	void handleRequestatributes();

	void handleRequestfame();

	void handleRequestskills();

	void handleRequestministats();

	void handleCommerceend();

	void handleUsercommerceend();

	void handleBankend();

	void handleUsercommerceok();

	void handleUsercommercereject();

	void handleDrop(byte slot, short amount);

	void handleCastspell(byte spell);

	void handleLeftclick(byte x, byte y);

	void handleDoubleclick(byte x, byte y);

	void handleWork(byte skill);

	void handleUsespellmacro();

	void handleUseitem(byte slot);

	void handleCraftblacksmith(short item);

	void handleCraftcarpenter(short item);

	void handleWorkleftclick(byte x, byte y, byte skill);

	void handleCreatenewguild(String desc, String guildName, String site, String codex);

	void handleSpellinfo(byte spellSlot);

	void handleEquipitem(byte itemSlot);

	void handleChangeheading(byte heading);

	void handleModifyskills(byte[] skills);

	void handleTrain(byte petIndex);

	void handleCommercebuy(byte slot, short amount);

	void handleBankextractitem(byte slot, short amount);

	void handleCommercesell(byte slot, short amount);

	void handleBankdeposit(byte slot, short amount);

	void handleForumpost(String title, String msg);

	void handleMovespell(byte dir, byte spell);

	void handleMovebank(byte dir, byte slot);

	void handleClancodexupdate(String desc, String codex);

	void handleUsercommerceoffer(byte slot, int amount);

	void handleGuildacceptpeace(String guild);

	void handleGuildrejectalliance(String guild);

	void handleGuildrejectpeace(String guild);

	void handleGuildacceptalliance(String guild);

	void handleGuildofferpeace(String guild, String proposal);

	void handleGuildofferalliance(String guild, String proposal);

	void handleGuildalliancedetails(String guild);

	void handleGuildpeacedetails(String guild);

	void handleGuildrequestjoinerinfo(String userName);

	void handleGuildallianceproplist();

	void handleEguildpeaceproplist();

	void handleGuilddeclarewar(String guild);

	void handleGuildnewwebsite(String website);

	void handleGuildacceptnewmember(String userName);

	void handleGuildrejectnewmember(String userName, String reason);

	void handleGuildkickmember(String userName);

	void handleGuildupdatenews(String news);

	void handleGuildmemberinfo(String userName);

	void handleGuildopenelections();

	void handleGuildrequestmembership(String guild, String application);

	void handleGuildrequestdetails(String guild);

	void handleOnline();

	void handleQuit();

	void handleGuildleave();

	void handleRequestaccountstate();

	void handlePetstand();

	void handlePetfollow();

	void handleTrainlist();

	void handleRest();

	void handleMeditate();

	void handleResucitate();

	void handleHeal();

	void handleRequeststats();

	void handleHelp();

	void handleCommercestart();

	void handleBankstart();

	void handleEnlist();

	void handleInformation();

	void handleReward();

	void handleRequestmotd();

	void handleUptime();

	void handlePartyleave();

	void handlePartycreate();

	void handlePartyjoin();

	void handleInquiry();

	void handleGuildmessage(String chat);

	void handlePartymessage(String chat);

	void handleCentinelreport(short key);

	void handleGuildonline();

	void handlePartyonline();

	void handleCouncilmessage(String chat);

	void handleRolemasterrequest(String request);

	void handleGmrequest();

	void handleBugreport(String bugReport);

	void handleChangedescription(String description);

	void handleGuildvote(String vote);

	void handlePunishments(String name);

	void handleChangepassword(String oldPassword, String newPassword);

	void handleGamble(short amount);

	void handleInquiryvote(byte opt);

	void handleBankextractgold(int amount);

	void handleLeavefaction();

	void handleBankdepositgold(int amount);

	void handleDenounce(String text);

	void handleGuildfundate(byte clanType);

	void handlePartykick(String userName);

	void handlePartysetleader(String userName);

	void handlePartyacceptmember(String userName);

	void handleGuildmemberlist(String guild);

	void handleGmmessage(String message);

	void handleShowname();

	void handleOnlineroyalarmy();

	void handleOnlinechaoslegion();

	void handleGonearby(String userName);

	void handleComment(String comment);

	void handleServertime();

	void handleWhere(String userName);

	void handleCreaturesinmap(short map);

	void handleWarpmetotarget();

	void handleWarpchar(String userName, byte x, byte y);

	void handleSilence(String userName);

	void handleSosshowlist();

	void handleSosremove(String userName);

	void handleGotochar(String userName);

	void handleInvisible();

	void handleGmpanel();

	void handleRequestuserlist();

	void handleWorking();

	void handleHiding();

	void handleJail(String userName, String reason, byte jailTime);

	void handleKillnpc();

	void handleWarnuser(String userName, String reason);

	void handleEditchar(String userName, byte option, String param1, String param2);

	void handleRequestcharinfo(String userName);

	void handleRequestcharstats(String userName);

	void handleRequestchargold(String userName);

	void handleRequestcharinventory(String userName);

	void handleRequestcharbank(String userName);

	void handleRequestcharskills(String userName);

	void handleRevivechar(String userName);

	void handleOnlinegm();

	void handleOnlinemap(short map);

	void handleForgive(String userName);

	void handleKick(String userName);

	void handleExecute(String userName);

	void handleBanchar(String userName, String reason);

	void handleUnbanchar(String userName);

	void handleNpcfollow();

	void handleSummonchar(String userName);

	void handleSpawnlistrequest();

	void handleSpawncreature(short npc);

	void handleResetnpcinventory();

	void handleCleanworld();

	void handleServermessage(String message);

	void handleNicktoip(String userName);

	void handleIptonick(byte ip1, byte ip2, byte ip3, byte ip4);

	void handleGuildonlinemembers(String guildName);

	void handleTeleportcreate(short mapa, byte x, byte y);

	void handleTeleportdestroy();

	void handleRaintoggle();

	void handleSetchardescription(String desc);

	void handleForcemiditomap(byte midiId, short map);

	void handleForcewavetomap(byte waveId, short map, byte x, byte y);

	void handleRoyalarmymessage(String message);

	void handleChaoslegionmessage(String message);

	void handleCitizenmessage(String message);

	void handleCriminalmessage(String message);

	void handleTalkasnpc(String message);

	void handleDestroyallitemsinarea();

	void handleAcceptroyalcouncilmember(String userName);

	void handleAcceptchaoscouncilmember(String userName);

	void handleItemsinthefloor();

	void handleMakedumb(String userName);

	void handleDumbnomore(String userName);

	void handleDumpiptables();

	void handleCouncilkick(String userName);

	void handleSettrigger();

	void handleAsktrigger();

	void handleBannediplist();

	void handleBannedipreload();

	void handleGuildban(String guildName);

	void handleBanip_1(byte ip1, byte ip2, byte ip3, byte ip4, String reason);

	void handleBanip_0(String userName, String reason);

	void handleUnbanip(byte ip1, byte ip2, byte ip3, byte ip4);

	void handleCreateitem(short objectIndex);

	void handleDestroyitems();

	void handleChaoslegionkick(String userName);

	void handleRoyalarmykick(String userName);

	void handleForcemidiall(byte midiId);

	void handleForcewaveall(byte waveId);

	void handleRemovepunishment(String userName, byte punishment, String newText);

	void handleTileblockedtoggle();

	void handleKillnpcnorespawn();

	void handleKillallnearbynpcs();

	void handleLastip(String userName);

	void handleChatcolor(byte red, byte green, byte blue);

	void handleIgnored();

	void handleCheckslot(String userName, byte slot);

	void handleResetautoupdate();

	void handleRestart();

	void handleReloadobjects();

	void handleReloadspells();

	void handleReloadserverini();

	void handleReloadnpcs();

	void handleKickallchars();

	void handleNight();

	void handleShowserverform();

	void handleCleansos();

	void handleSavechars();

	void handleChangemapinfobackup(byte doTheBackup);

	void handleChangemapinfopk(byte isMapPk);

	void handleChangemapinforestricted(String status);

	void handleChangemapinfonomagic(byte noMagic);

	void handleChangemapinfonoinvi(byte noInvisible);

	void handleChangemapinfonoresu(byte noResu);

	void handleChangemapinfoland(String infoLand);

	void handleChangemapinfozone(String infoZone);

	void handleSavemap();

	void handleShowguildmessages(String guild);

	void handleDobackup();

	void handleTogglecentinelactivated();

	void handleAltername(String userName, String newName);

	void handleAltermail(String userName, String newEmail);

	void handleAlterpassword(String userName, String copyFrom);

	void handleCreatenpc(short npcIndex);

	void handleCreatenpcwithrespawn(short npcIndex);

	void handleImperialarmour(byte index, short objIndex);

	void handleChaosarmour(byte index, short objIndex);

	void handleNavigatetoggle();

	void handleServeropentouserstoggle();

	void handleTurnoffserver();

	void handleTurncriminal(String userName);

	void handleResetfactions(String userName);

	void handleRemovecharfromguild(String userName);

	void handleRequestcharmail(String userName);

	void handleSystemmessage(String message);

	void handleSetmotd(String newMOTD);

	void handleChangemotd();

	void handlePing();

	void handleSetinivar(String section, String key, String value);

	void writeLoggedmessage();

	void writeRemovealldialogs();

	void writeRemovechardialog(short charIndex);

	void writeNavigatetoggle();

	void writeDisconnect();

	void writeCommerceend();

	void writeBankend();

	void writeCommerceinit();

	void writeBankinit();

	void writeUsercommerceinit();

	void writeUsercommerceend();

	void writeShowblacksmithform();

	void writeShowcarpenterform();

	void writeNpcswing();

	void writeNpckilluser();

	void writeBlockedwithshielduser();

	void writeBlockedwithshieldother();

	void writeUserswing();

	void writeUpdateneeded();

	void writeSafemodeon();

	void writeSafemodeoff();

	void writeResuscitationsafeon();

	void writeResuscitationsafeoff();

	void writeNobilitylost();

	void writeCantusewhilemeditating();

	void writeUpdatesta(short minSta);

	void writeUpdatemana(short minMan);

	void writeUpdatehp(short minHP);

	void writeUpdategold(int gold);

	void writeUpdateexp(int exp);

	void writeChangemap(short map, short version);

	void writePosupdate(byte x, byte y);

	void writeNpchituser(byte target, short damage);

	void writeUserhitnpc(int damage);

	void writeUserattackedswing(short charIndex);

	void writeUserhittedbyuser(short attackerChar, byte target, short damage);

	void writeUserhitteduser(short attackedChar, byte target, short damage);

	void writeChatoverhead(String chat, short charIndex, byte red, byte green, byte blue);

	void writeConsolemsg(String chat, byte fontIndex);

	void writeGuildchat(String chat);

	void writeShowmessagebox(String message);

	void writeUserindexinserver(short userIndex);

	void writeUsercharindexinserver(short charIndex);

	void writeCharactercreate(short charIndex, short body, short head, byte heading, byte x, byte y, short weapon,
			short shield, short helmet, short fx, short fxLoops, String name, byte criminal, byte privileges);

	void writeCharacterremove(short charIndex);

	void writeCharactermove(short charIndex, byte x, byte y);

	void writeForcecharmove(byte heading);

	void writeCharacterchange(short charIndex, short body, short head, byte heading, short weapon, short shield,
			short helmet, short fx, short fxLoops);

	void writeObjectcreate(byte x, byte y, short grhIndex);

	void writeObjectdelete(byte x, byte y);

	void writeBlockposition(byte x, byte y, byte blocked);

	void writePlaymidi(byte midi, short loops);

	void writePlaywave(byte wave, byte x, byte y);

	void writeGuildlist(String members);

	void writeAreachanged(byte x, byte y);

	void writePausetoggle();

	void writeRaintoggle();

	void writeCreatefx(short charIndex, short fx, short fxLoops);

	void writeUpdateuserstats(short maxHP, short minHP, short maxMAN, short minMAN, short maxSTA, short minSTA,
			int gold, byte elv, int elu, int exp);

	void writeWorkrequesttarget(byte skill);

	void writeChangeinventoryslot(byte slot, short objIndex, String name, short amount, byte equiped, short grhIndex,
			byte objType, short maxHIT, short minHIT, short def, float valor);

	void writeChangebankslot(byte slot, short objIndex, String name, short amount, short grhIndex, byte objType,
			short maxHIT, short minHIT, short def, int valor);

	void writeChangespellslot(byte slot, short spell, String name);

	void writeAttributes(byte fuerza, byte agilidad, byte inteligencia, byte carisma, byte constitucion);

	class BlacksmithItem {
		String name;
		short lingH;
		short lingP;
		short lingO;
		short index;
	}

	class CarpenterItem {
		String name;
		short madera;
		short index;
	}

	void writeBlacksmithweapons(short count, BlacksmithItem[] weapons);

	void writeBlacksmitharmors(short count, BlacksmithItem[] armors);

	void writeCarpenterobjects(short count, CarpenterItem[] objects);

	void writeRestok();

	void writeErrormsg(String msg);

	void writeBlind();

	void writeDumb();

	void writeShowsignal(String texto, short grhSecundario);

	void writeChangenpcinventoryslot(byte slot, String name, short amount, float price, short grhIndex, short objIndex,
			byte objType, short maxHIT, short minHIT, short def);

	void writeUpdatehungerandthirst(byte maxAGU, byte minAGU, byte maxHAM, byte minHAM);

	void writeFame(int asesinoRep, int bandidoRep, int burguesRep, int ladronRep, int nobleRep, int pebleRep,
			int promedio);

	void writeMinistats(int ciudadanosMatados, int criminalesMatados, int usuariosMatados, short npcsMatados,
			byte clase, int pena);

	void writeLevelup(short skillPoints);

	void writeAddforummsg(String title, String message);

	void writeShowforumform();

	void writeSetinvisible(short charIndex, byte invisible);

	void writeDiceroll(byte fuerza, byte agilidad, byte inteligencia, byte carisma, byte constitucion);

	void writeMeditatetoggle();

	void writeBlindnomore();

	void writeDumbnomore();

	void writeSendskills(byte[] skills);

	void writeTrainercreaturelist(String npcList);

	void writeGuildnews(String guildNews, String enemiesList, String alliesList);

	void writeOfferdetails(String details);

	void writeAlianceproposalslist(String guildsList);

	void writePeaceproposalslist(String guildsList);

	void writeCharacterinfo(String charName, byte race, byte clazz, byte gender, byte level, int gold, int bank,
			int reputation, String previousPetitions, String currentGuild, String previousGuilds, byte royalArmy,
			byte caosLegion, int citizensKilled, int criminalsKilled);

	void writeGuildleaderinfo(String guildList, String memberList, String guildNews, String requestsList);

	void writeGuilddetails(String guildName, String founder, String foundationDate, String leader, String url,
			short memberCount, byte electionsOpen, String alignment, short enemiesCount, short alliesCount,
			String antifactionPoints, String codex, String guildDesc);

	void writeShowguildfundationform();

	void writeParalizeok();

	void writeShowuserrequest(String details);

	void writeTradeok();

	void writeBankok();

	void writeChangeusertradeslot(short objIndex, String name, int amount, short grhIndex, byte objType, short maxHIT,
			short minHIT, short def, int valor);

	void writeSendnight(byte night);

	void writeSpawnlist(String npcNamesList);

	void writeShowsosform(String sosList);

	void writeShowmotdeditionform(String currentMOTD);

	void writeShowgmpanelform();

	void writeUsernamelist(String userNamesList);

	void writePong();
	
}
