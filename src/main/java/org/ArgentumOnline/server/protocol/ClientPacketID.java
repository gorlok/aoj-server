package org.ArgentumOnline.server.protocol;

/**
* @author gorlok
* @author JAO (userforos: agushh/thorkes)
*/
public enum ClientPacketID {
    LoginExistingChar       ,//  OLOGIN
    ThrowDices              ,//  TIRDAD
    LoginNewChar            ,//  NLOGIN
    Talk                    ,//  ;
    Yell                    ,//  -
    Whisper                 ,//  \
    Walk                    ,//  M
    RequestPositionUpdate   ,//  RPU
    Attack                  ,//  AT
    PickUp                  ,//  AG
    CombatModeToggle        ,//  TAB        - SHOULD BE HANLDED JUST BY THE CLIENT!!
    SafeToggle              ,//  /SEG & SEG  (SEG,//s behaviour has to be coded in the client)
    ResuscitationSafeToggle	,
    RequestGuildLeaderInfo  ,//  GLINFO
    RequestAtributes        ,//  ATR
    RequestFame             ,//  FAMA
    RequestSkills           ,//  ESKI
    RequestMiniStats        ,//  FEST
    CommerceEnd             ,//  FINCOM
    UserCommerceEnd         ,//  FINCOMUSU
    BankEnd                 ,//  FINBAN
    UserCommerceOk          ,//  COMUSUOK
    UserCommerceReject      ,//  COMUSUNO
    Drop                    ,//  TI
    CastSpell               ,//  LH
    LeftClick               ,//  LC
    DoubleClick             ,//  RC
    Work                    ,//  UK
    UseSpellMacro           ,//  UMH
    UseItem                 ,//  USA
    CraftBlacksmith         ,//  CNS
    CraftCarpenter          ,//  CNC
    WorkLeftClick           ,//  WLC
    CreateNewGuild          ,//  CIG
    SpellInfo               ,//  INFS
    EquipItem               ,//  EQUI
    ChangeHeading           ,//  CHEA
    ModifySkills            ,//  SKSE
    Train                   ,//  ENTR
    CommerceBuy             ,//  COMP
    BankExtractItem         ,//  RETI
    CommerceSell            ,//  VEND
    BankDeposit             ,//  DEPO
    ForumPost               ,//  DEMSG
    MoveSpell               ,//  DESPHE
    MoveBank				,
    ClanCodexUpdate         ,//  DESCOD
    UserCommerceOffer       ,//  OFRECER
    GuildAcceptPeace        ,//  ACEPPEAT
    GuildRejectAlliance     ,//  RECPALIA
    GuildRejectPeace        ,//  RECPPEAT
    GuildAcceptAlliance     ,//  ACEPALIA
    GuildOfferPeace         ,//  PEACEOFF
    GuildOfferAlliance      ,//  ALLIEOFF
    GuildAllianceDetails    ,//  ALLIEDET
    GuildPeaceDetails       ,//  PEACEDET
    GuildRequestJoinerInfo  ,//  ENVCOMEN
    GuildAlliancePropList   ,//  ENVALPRO
    GuildPeacePropList      ,//  ENVPROPP
    GuildDeclareWar         ,//  DECGUERR
    GuildNewWebsite         ,//  NEWWEBSI
    GuildAcceptNewMember    ,//  ACEPTARI
    GuildRejectNewMember    ,//  RECHAZAR
    GuildKickMember         ,//  ECHARCLA
    GuildUpdateNews         ,//  ACTGNEWS
    GuildMemberInfo         ,//  1HRINFO<
    GuildOpenElections      ,//  ABREELEC
    GuildRequestMembership  ,//  SOLICITUD
    GuildRequestDetails     ,//  CLANDETAILS
    Online                  ,//  /ONLINE
    Quit                    ,//  /SALIR
    GuildLeave              ,//  /SALIRCLAN
    RequestAccountState     ,//  /BALANCE
    PetStand                ,//  /QUIETO
    PetFollow               ,//  /ACOMPAÑAR
    TrainList               ,//  /ENTRENAR
    Rest                    ,//  /DESCANSAR
    Meditate                ,//  /MEDITAR
    Resucitate              ,//  /RESUCITAR
    Heal                    ,//  /CURAR
    Help                    ,//  /AYUDA
    RequestStats            ,//  /EST
    CommerceStart           ,//  /COMERCIAR
    BankStart               ,//  /BOVEDA
    Enlist                  ,//  /ENLISTAR
    Information             ,//  /INFORMACION
    Reward                  ,//  /RECOMPENSA
    RequestMOTD             ,//  /MOTD
    Uptime                  ,//  /UPTIME
    PartyLeave              ,//  /SALIRPARTY
    PartyCreate             ,//  /CREARPARTY
    PartyJoin               ,//  /PARTY
    Inquiry                 ,//  /ENCUESTA ( with no params )
    GuildMessage            ,//  /CMSG
    PartyMessage            ,//  /PMSG
    CentinelReport          ,//  /CENTINELA
    GuildOnline             ,//  /ONLINECLAN
    PartyOnline             ,//  /ONLINEPARTY
    CouncilMessage          ,//  /BMSG
    RoleMasterRequest       ,//  /ROL
    GMRequest               ,//  /GM
    bugReport               ,//  /_BUG
    ChangeDescription       ,//  /DESC
    GuildVote               ,//  /VOTO
    Punishments             ,//  /PENAS
    ChangePassword          ,//  /CONTRASEÑA
    Gamble                  ,//  /APOSTAR
    InquiryVote             ,//  /ENCUESTA ( with parameters )
    LeaveFaction            ,//  /RETIRAR ( with no arguments )
    BankExtractGold         ,//  /RETIRAR ( with arguments )
    BankDepositGold         ,//  /DEPOSITAR
    Denounce                ,//  /DENUNCIAR
    GuildFundate            ,//  /FUNDARCLAN
    PartyKick               ,//  /ECHARPARTY
    PartySetLeader          ,//  /PARTYLIDER
    PartyAcceptMember       ,//  /ACCEPTPARTY
    Ping                    ,//  /PING
    
    //GM messages
    GMMessage               ,//  /GMSG
    showName                ,//  /SHOWNAME
    OnlineRoyalArmy         ,//  /ONLINEREAL
    OnlineChaosLegion       ,//  /ONLINECAOS
    GoNearby                ,//  /IRCERCA
    comment                 ,//  /REM
    serverTime              ,//  /HORA
    Where                   ,//  /DONDE
    CreaturesInMap          ,//  /NENE
    WarpMeToTarget          ,//  /TELEPLOC
    WarpChar                ,//  /TELEP
    Silence                 ,//  /SILENCIAR
    SOSShowList             ,//  /SHOW SOS
    SOSRemove               ,//  SOSDONE
    GoToChar                ,//  /IRA
    invisible               ,//  /INVISIBLE
    GMPanel                 ,//  /PANELGM
    RequestUserList         ,//  LISTUSU
    Working                 ,//  /TRABAJANDO
    Hiding                  ,//  /OCULTANDO
    Jail                    ,//  /CARCEL
    KillNPC                 ,//  /RMATA
    WarnUser                ,//  /ADVERTENCIA
    EditChar                ,//  /MOD
    RequestCharInfo         ,//  /INFO
    RequestCharStats        ,//  /STAT
    RequestCharGold         ,//  /BAL
    RequestCharInventory    ,//  /INV
    RequestCharBank         ,//  /BOV
    RequestCharSkills       ,//  /SKILLS
    ReviveChar              ,//  /REVIVIR
    OnlineGM                ,//  /ONLINEGM
    OnlineMap               ,//  /ONLINEMAP
    Forgive                 ,//  /PERDON
    Kick                    ,//  /ECHAR
    Execute                 ,//  /EJECUTAR
    BanChar                 ,//  /BAN
    UnbanChar               ,//  /UNBAN
    NPCFollow               ,//  /SEGUIR
    SummonChar              ,//  /SUM
    SpawnListRequest        ,//  /CC
    SpawnCreature           ,//  SPA
    ResetNPCInventory       ,//  /RESETINV
    CleanWorld              ,//  /LIMPIAR
    ServerMessage           ,//  /RMSG
    NickToIP                ,//  /NICK2IP
    IPToNick                ,//  /IP2NICK
    GuildOnlineMembers      ,//  /ONCLAN
    TeleportCreate          ,//  /CT
    TeleportDestroy         ,//  /DT
    RainToggle              ,//  /LLUVIA
    SetCharDescription      ,//  /SETDESC
    ForceMIDIToMap          ,//  /FORCEMIDIMAP
    ForceWAVEToMap          ,//  /FORCEWAVMAP
    RoyalArmyMessage        ,//  /REALMSG
    ChaosLegionMessage      ,//  /CAOSMSG
    CitizenMessage          ,//  /CIUMSG
    CriminalMessage         ,//  /CRIMSG
    TalkAsNPC               ,//  /TALKAS
    DestroyAllItemsInArea   ,//  /MASSDEST
    AcceptRoyalCouncilMember ,//  /ACEPTCONSE
    AcceptChaosCouncilMember ,//  /ACEPTCONSECAOS
    ItemsInTheFloor         ,//  /PISO
    MakeDumb                ,//  /ESTUPIDO
    MakeDumbNoMore          ,//  /NOESTUPIDO
    DumpIPTables            ,//  /DUMPSECURITY
    CouncilKick             ,//  /KICKCONSE
    SetTrigger              ,//  /TRIGGER
    AskTrigger              ,//  /TRIGGER with no arguments
    BannedIPList            ,//  /BANIPLIST
    BannedIPReload          ,//  /BANIPRELOAD
    GuildMemberList         ,//  /MIEMBROSCLAN
    GuildBan                ,//  /BANCLAN
    BanIP                   ,//  /BANIP
    UnbanIP                 ,//  /UNBANIP
    CreateItem              ,//  /CI
    DestroyItems            ,//  /DEST
    ChaosLegionKick         ,//  /NOCAOS
    RoyalArmyKick           ,//  /NOREAL
    ForceMIDIAll            ,//  /FORCEMIDI
    ForceWAVEAll            ,//  /FORCEWAV
    RemovePunishment        ,//  /BORRARPENA
    TileBlockedToggle       ,//  /BLOQ
    KillNPCNoRespawn        ,//  /MATA
    KillAllNearbyNPCs       ,//  /MASSKILL
    LastIP                  ,//  /LASTIP
    ChangeMOTD              ,//  /MOTDCAMBIA
    SetMOTD                 ,//  ZMOTD
    SystemMessage           ,//  /SMSG
    CreateNPC               ,//  /ACC
    CreateNPCWithRespawn    ,//  /RACC
    ImperialArmour          ,//  /AI1 - 4
    ChaosArmour             ,//  /AC1 - 4
    NavigateToggle          ,//  /NAVE
    ServerOpenToUsersToggle ,//  /HABILITAR
    TurnOffServer           ,//  /APAGAR
    TurnCriminal            ,//  /CONDEN
    ResetFactions           ,//  /RAJAR
    RemoveCharFromGuild     ,//  /RAJARCLAN
    RequestCharMail         ,//  /LASTEMAIL
    AlterPassword           ,//  /APASS
    AlterMail               ,//  /AEMAIL
    AlterName               ,//  /ANAME
    ToggleCentinelActivated ,//  /CENTINELAACTIVADO
    DoBackUp                ,//  /DOBACKUP
    ShowGuildMessages       ,//  /SHOWCMSG
    SaveMap                 ,//  /GUARDAMAPA
    ChangeMapInfoPK         ,//  /MODMAPINFO PK
    ChangeMapInfoBackup     ,//  /MODMAPINFO BACKUP
    ChangeMapInfoRestricted ,//  /MODMAPINFO RESTRINGIR
    ChangeMapInfoNoMagic    ,//  /MODMAPINFO MAGIASINEFECTO
    ChangeMapInfoNoInvi     ,//  /MODMAPINFO INVISINEFECTO
    ChangeMapInfoNoResu     ,//  /MODMAPINFO RESUSINEFECTO
    ChangeMapInfoLand       ,//  /MODMAPINFO TERRENO
    ChangeMapInfoZone       ,//  /MODMAPINFO ZONA
    SaveChars               ,//  /GRABAR
    CleanSOS                ,//  /BORRAR SOS
    ShowServerForm          ,//  /SHOW INT
    night                   ,//  /NOCHE
    KickAllChars            ,//  /ECHARTODOSPJS
    ReloadNPCs              ,//  /RELOADNPCS
    ReloadServerIni         ,//  /RELOADSINI
    ReloadSpells            ,//  /RELOADHECHIZOS
    ReloadObjects           ,//  /RELOADOBJ
    Restart                 ,//  /REINICIAR
    ResetAutoUpdate         ,//  /AUTOUPDATE
    ChatColor               ,//  /CHATCOLOR
    Ignored                 ,//  /IGNORADO
    CheckSlot               ,//  /SLOT
    SetIniVar               ;//   /SETINIVAR LLAVE CLAVE VALOR
	
    public static void main(String[] args) {
		for (ClientPacketID e : ClientPacketID.values()) {
			System.out.println(e.ordinal() + " - " + e.name());
		}
	}
	
}
