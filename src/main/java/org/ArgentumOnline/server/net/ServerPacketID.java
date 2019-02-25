package org.ArgentumOnline.server.net;

/**
* @author gorlok
* @author JAO (userforos: agushh/thorkes)
*/
public enum ServerPacketID { 
	LoggedMessage           ,// LOGGED
	RemoveAllDialogs        ,// QTDL
    RemoveCharDialog        ,// QDL
    NavigateToggle          ,// NAVEG
    Disconnect              ,// FINOK
    CommerceEnd             ,// FINCOMOK
    BankEnd                 ,// FINBANOK
    CommerceInit            ,// INITCOM
    BankInit                ,// INITBANCO
    UserCommerceInit        ,// INITCOMUSU
    UserCommerceEnd         ,// FINCOMUSUOK
    ShowBlacksmithForm      ,// SFH
    ShowCarpenterForm       ,// SFC
    NPCSwing                ,// N1
    NPCKillUser             ,// 6
    BlockedWithShieldUser   ,// 7
    BlockedWithShieldOther  ,// 8
    UserSwing               ,// U1
    UpdateNeeded            ,// REAU
    SafeModeOn              ,// SEGON
    SafeModeOff             ,// SEGOFF
    ResuscitationSafeOn		,
    ResuscitationSafeOff	,
    NobilityLost            ,// PN
    CantUseWhileMeditating  ,// M!
    UpdateSta               ,// ASS
    UpdateMana              ,// ASM
    UpdateHP                ,// ASH
    UpdateGold              ,// ASG
    UpdateExp               ,// ASE
    ChangeMap               ,// CM
    PosUpdate               ,// PU
    NPCHitUser              ,// N2
    UserHitNPC              ,// U2
    UserAttackedSwing       ,// U3
    UserHittedByUser        ,// N4
    UserHittedUser          ,// N5
    ChatOverHead            ,// ||
    ConsoleMsg              ,// || - Beware!! its the same as above, but it was properly splitted
    GuildChat               ,// |+
    ShowMessageBox          ,// !!
    UserIndexInServer       ,// IU
    UserCharIndexInServer   ,// IP
    CharacterCreate         ,// CC
    CharacterRemove         ,// BP
    CharacterChangeNick		,
    CharacterMove           ,// MP, +, * and _ ,//
    ForceCharMove			,
    CharacterChange         ,// CP
    ObjectCreate            ,// HO
    ObjectDelete            ,// BO
    BlockPosition           ,// BQ
    PlayMidi                ,// TM
    PlayWave                ,// TW
    GuildList               ,// GL
    AreaChanged             ,// CA
    PauseToggle             ,// BKW
    RainToggle              ,// LLU
    CreateFX                ,// CFX
    UpdateUserStats         ,// EST
    WorkRequestTarget       ,// T01
    ChangeInventorySlot     ,// CSI
    ChangeBankSlot          ,// SBO
    ChangeSpellSlot         ,// SHS
    Attributes              ,// ATR
    BlacksmithWeapons       ,// LAH
    BlacksmithArmors        ,// LAR
    CarpenterObjects        ,// OBR
    RestOK                  ,// DOK
    ErrorMsg                ,// ERR
    Blind                   ,// CEGU
    Dumb                    ,// DUMB
    ShowSignal              ,// MCAR
    ChangeNPCInventorySlot  ,// NPCI
    UpdateHungerAndThirst   ,// EHYS
    Fame                    ,// FAMA
    MiniStats               ,// MEST
    LevelUp                 ,// SUNI
    AddForumMsg             ,// FMSG
    ShowForumForm           ,// MFOR
    SetInvisible            ,// NOVER
    DiceRoll                ,// DADOS
    MeditateToggle          ,// MEDOK
    BlindNoMore             ,// NSEGUE
    DumbNoMore              ,// NESTUP
    SendSkills              ,// SKILLS
    TrainerCreatureList     ,// LSTCRI
    GuildNews               ,// GUILDNE
    OfferDetails            ,// PEACEDE & ALLIEDE
    AlianceProposalsList    ,// ALLIEPR
    PeaceProposalsList      ,// PEACEPR
    CharacterInfo           ,// CHRINFO
    GuildLeaderInfo         ,// LEADERI
    GuildDetails            ,// CLANDET
    ShowGuildFundationForm  ,// SHOWFUN
    ParalizeOK              ,// PARADOK
    ShowUserRequest         ,// PETICIO
    TradeOK                 ,// TRANSOK
    BankOK                  ,// BANCOOK
    ChangeUserTradeSlot     ,// COMUSUINV
    SendNight               ,// NOC
    Pong					,
    UpdateTagAndStatus		,
    
    //GM messages
    SpawnList               ,// SPL
    ShowSOSForm             ,// MSOS
    ShowMOTDEditionForm     ,// ZMOTD
    ShowGMPanelForm         ,// ABPANEL
    UserNameList            ;// LISTUSU
    
    public byte id() {
    	return (byte)this.ordinal();
    }
    
	// cache values() because performance
	private final static ServerPacketID[] values = ServerPacketID.values();
	
	public static ServerPacketID value(int index) {
		return values[index];
	}

    /*
    public static void main(String[] args) {
		for (ServerPacketID e : ServerPacketID.values()) {
			System.out.println(e.ordinal() + " - " + e.name());
		}
	}
	*/
}


