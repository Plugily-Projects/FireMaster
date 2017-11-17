package me.tomthedeveloper;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.TomTheDeveloper.CommandsInterface;
import me.TomTheDeveloper.GameAPI;
import me.TomTheDeveloper.Attacks.AttackListener;
import me.TomTheDeveloper.Database.MyDatabase;
import me.TomTheDeveloper.Game.GameInstance;
import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.Handlers.ConfigurationManager;
import me.tomthedeveloper.events.InstanceEvents;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

/**
 * Created by Tom on 22/08/2014.
 */
public class FireMaster extends JavaPlugin implements CommandsInterface {

	private static boolean stopOnFinish = false;
	private static boolean multiplespawns = false;
	private static boolean vaultactivated = false;
	private static boolean databasActivated = false;
	private static boolean dynamicsigns = false;
	private static boolean itemRewardActivated = false;
	private GameAPI gameAPI;
	public static FireMaster instance;

	private StatsMenu statsMenu;
	private StatItem statItems;

	private MyDatabase myDatabase;

	public static boolean isDynamicSignsEnabled(){
		return dynamicsigns;
	}
	public void setupMessageConfig() {
		loadLanguageFile();

		ChatManager.getFromLanguageConfig("WinMessage", ChatManager.HIGHLIGHTED + "%PLAYER% " + ChatManager.NORMAL + "won the game!");
		ChatManager.getFromLanguageConfig("JoinMessage", ChatManager.HIGHLIGHTED + "%PLAYER%" + ChatManager.NORMAL + " joined the game (%PLAYERSIZE%/%MAXPLAYERS%)!");
		ChatManager.getFromLanguageConfig("LeaveMessage", ChatManager.HIGHLIGHTED + "%PLAYER% " + ChatManager.NORMAL + "left the game (%PLAYERSIZE%/%MAXPLAYERS%)!");
		ChatManager.getFromLanguageConfig("DeathMessage", ChatManager.HIGHLIGHTED +"%PLAYER% " + ChatManager.NORMAL + "died!");
		ChatManager.getFromLanguageConfig("NoWinnerMessage", ChatManager.NORMAL + "Time's up! Nobody won!");
		ChatManager.getFromLanguageConfig("BattleBeginMessage", ChatManager.NORMAL + "Let the battle begin!");
		ChatManager.getFromLanguageConfig("SecondsLeftUntilGameStartsMessage", "The game starts in " + ChatManager.HIGHLIGHTED + "%TIME%" + ChatManager.NORMAL + " seconds!");
		ChatManager.getFromLanguageConfig("WaitingForPlayersMessage", "Waiting for players... We need at least " + ChatManager.HIGHLIGHTED + "%MINPLAYERS%"+ ChatManager.NORMAL +" players to start.");
		ChatManager.getFromLanguageConfig("EnoughPlayersToStartMessage", "We now have enough players. The game is starting soon!");
		ChatManager.getFromLanguageConfig("TeleportToEndLocationInXSeconds", "You will be teleported to the lobby in " + ChatManager.HIGHLIGHTED + "%TIME%" + ChatManager.NORMAL + " seconds");
		ChatManager.getFromLanguageConfig("ErrorGameStopMessageNormallyNeverHappens", "Nobody has won!");
		ChatManager.getFromLanguageConfig("ScoreboardStartingInMessage", ChatManager.NORMAL + "Starting in :");
		ChatManager.getFromLanguageConfig("ScoreboardPlayersWaitingMessage", ChatManager.HIGHLIGHTED + "Players:");
		ChatManager.getFromLanguageConfig("ScoreboardMinPlayersMessage", ChatManager.HIGHLIGHTED + "Min Players:");
		ChatManager.getFromLanguageConfig("ScoreboardPlayersLeftMessage", ChatManager.HIGHLIGHTED + "Players left:");
		ChatManager.getFromLanguageConfig("ScoreboardTimeLeftMessage", ChatManager.HIGHLIGHTED + "Time left:");
		ChatManager.getFromLanguageConfig("YouAreNowASpectatorMessage", ChatColor.GREEN + "You died! You are now in spectator mode! You are able to fly now!");
		ChatManager.getFromLanguageConfig("BossBarStartingInMessage", ChatManager.NORMAL + "Starting in: " + ChatManager.HIGHLIGHTED + "%TIME%");
		ChatManager.getFromLanguageConfig("BossBarTimeLeftMessage", ChatManager.NORMAL + "Time left -> " + ChatManager.HIGHLIGHTED + "%FORMATTEDTIME%");
		ChatManager.getFromLanguageConfig("InformationHeadMessage", ChatColor.BOLD + "----------- INFORMATION ------------");
		ChatManager.getFromLanguageConfig("InformationLeftClickMessage",ChatManager.HIGHLIGHTED + "LEFT CLICK: " +ChatManager.NORMAL + "Shoots fire!");
		ChatManager.getFromLanguageConfig("InformationRightClickMessage", ChatManager.HIGHLIGHTED + "RIGHT CLICK: " + ChatManager.NORMAL + "Shoots explosive fire!");
		ChatManager.getFromLanguageConfig("InformationShiftedLeftClick", ChatManager.HIGHLIGHTED + "SHIFTED LEFT CLICK:" + ChatManager.NORMAL + " Charges a lightning strike!");
		ChatManager.getFromLanguageConfig("InformationShiftedRightClickMessage", ChatManager.HIGHLIGHTED + "SHIFTED RIGHT CLICK: " + ChatManager.NORMAL + "Heal yourself!");

		ChatManager.getFromLanguageConfig("InformationBottomMessage", ChatColor.BOLD + "-----------------------------------");
		ChatManager.getFromLanguageConfig("HealCooldownMessage",ChatColor.RED + "Your Heal is on cooldown for %COOLDOWN%  more seconds!" );
		ChatManager.getFromLanguageConfig("FireBlastAttackCooldownMessage",ChatColor.RED + "Your Fire Blast Attack is on cooldown for %COOLDOWN%  more seconds!" );
		ChatManager.getFromLanguageConfig("FireExplosionAttackCooldownMessage",ChatColor.RED + "Your Fire Explosion Attack is on cooldown for %COOLDOWN%  more seconds!" );
		ChatManager.getFromLanguageConfig("LightningExplosionAttackCooldownMessage",ChatColor.RED + "Your Lightning Explosion Attack is on cooldown for %COOLDOWN%  more seconds!" );
		ChatManager.getFromLanguageConfig("UnableToStrikeThereMessage", ChatColor.RED + "Unable to strike there!");

		ChatManager.getFromLanguageConfig("SpectatorMenuName", "Spectator Menu");
		ChatManager.getFromLanguageConfig("SpectatorMenuLoreItemsMessage", ChatColor.GRAY + "Click to teleport to " + ChatColor.GOLD + "%PLAYER%");
		ChatManager.getFromLanguageConfig("SpectatorMenuPlayerNotFoundMessage", ChatColor.RED + "It seems like the player your looking for left the game!");
		ChatManager.getFromLanguageConfig("SpectatorMenuTeleportedToPlayerMessage", ChatManager.NORMAL + "Teleported to " + ChatManager.HIGHLIGHTED +  "%PLAYER%");

		ChatManager.getFromLanguageConfig("TeleportToEndLocationMessage", ChatColor.GRAY + "Teleported to the lobby!");

		ChatManager.getFromLanguageConfig("VaultRewardFirstPlace", ChatColor.GREEN + "You have been rewarded %MONEY% dollar because you became first!");
		ChatManager.getFromLanguageConfig("VaultRewardSecondPlace", ChatColor.GREEN + "You have been rewarded %MONEY% dollar because you became second!!");
		ChatManager.getFromLanguageConfig("VaultRewardThirdPlace", ChatColor.GREEN + "You have been rewarded %MONEY% dollar because you became third!");
		ChatManager.getFromLanguageConfig("ScoreboardHeader", ChatColor.GOLD + "FireMaster");
		ChatManager.getFromLanguageConfig("NoPermissionToJoinFullGames","You don't have the permission to join full games!");
		ChatManager.getFromLanguageConfig("FullGameAlreadyFullWithPermiumPlayers", ChatColor.RED + "This game is already full with premium players! Sorry");
		ChatManager.getFromLanguageConfig("KickedToMakePlaceForPremiumPlayer","%PLAYER% got removed from the game to make place for a premium players!");
		ChatManager.getFromLanguageConfig("YouGotKickedToMakePlaceForAPremiumPlayer",ChatColor.RED + "You got kicked out of the game to make place for a premium player!");

		ChatManager.getFromLanguageConfig("BlockCommandWhileIngame", ChatColor.RED + "You can only use the /leave command ingame!");
	}

	public void onPreStart(){


		//databasActivated = this.getConfig().getBoolean("StatsActivated");



	}

	public static boolean isMultipleSpawnsEnabled(){
		return multiplespawns;
	}


	@Override
	public void onEnable() {
		instance = this;
		gameAPI = new GameAPI();
		gameAPI.setAbreviation("fm");
		gameAPI.setGameName("FireMaster");
		if(!this.getConfig().contains("MultipleSpawns")){
			this.getConfig().set("MultipleSpawns", false);
		}
		multiplespawns = this.getConfig().getBoolean("MultipleSpawns");
		if(!this.getConfig().contains("VaultActivated")){
			this.getConfig().set("VaultActivated", false);
		}
		// if(!this.getConfig().contains("StatsActivated"))
		//    this.getConfig().set("StatsActivated", false);
		if(!this.getConfig().contains("DynamicSignSystem"))
			this.getConfig().set("DynamicSignSystem", false);
		if(!this.getConfig().contains("ItemRewardWithCommand")) {
			this.getConfig().set("ItemRewardWithCommand", false);
			this.getConfig().set("ItemRewardDispatchCommandFirst", "say %PLAYER% won game!");
			this.getConfig().set("ItemRewardDispatchCommandSecond", "say %PLAYER% became second!");
			this.getConfig().set("ItemRewardDispatchCommandThird", "say %PLAYER% became third!");
		}
		dynamicsigns = this.getConfig().getBoolean("DynamicSignSystem");
		multiplespawns = this.getConfig().getBoolean("MultipleSpawns");
		vaultactivated = this.getConfig().getBoolean("VaultActivated");
		itemRewardActivated = this.getConfig().getBoolean("ItemRewardWithCommand");
		gameAPI.onSetup(this, this);

		gameAPI.setNeedsMapRestore(true);
		gameAPI.setAllowBuilding(false);
		this.getServer().getPluginManager().registerEvents(new InstanceEvents(this), this);
		gameAPI.setAttackListener(new AttackListener());

		//gameAPI.getAttackListener().start();

		loadInstances();
		System.out.print(gameAPI.getGameInstanceManager().getGameInstances().size() + " ARENAS LOADED!");
		loadLanguageFile();

		FileConfiguration config = ConfigurationManager.getConfig("Bungee");
		if(!config.contains("StopServerOnFinish"))
			config.set("StopServerOnFinish", false);
		try {
			config.save(ConfigurationManager.getFile("Bungee"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		stopOnFinish = config.getBoolean("StopServerOnFinish");

		if(isVaultActivated()){
			setupVaultEconomy();
		}

		if(databasActivated){
			StatItem.plugin = this;
			statItems = new StatItem(this);
			statItems.setup();

			myDatabase = new MyDatabase();

			statsMenu = new StatsMenu(this);

		}
		getCommand("stats").setExecutor(new StatsCommand(this));
		getCommand("join").setExecutor(new JoinCommand(this));

	}

	public static boolean doStopOnFinish(){
		return stopOnFinish;
	}

	public GameAPI getGameAPI() {
		return gameAPI;
	}

	public void onStop() {

	}

	public StatItem getStatItems(){
		return statItems;
	}

	public void loadInstances(){
		if(gameAPI.getGameInstanceManager().getGameInstances() != null) {
			if (gameAPI.getGameInstanceManager().getGameInstances().size() > 0) {
				for (GameInstance gameInstance : gameAPI.getGameInstanceManager().getGameInstances()) {
					gameAPI.getSignManager().removeSign(gameInstance);
				}
			}
		}
		gameAPI.getGameInstanceManager().getGameInstances().clear();
		for(String ID:this.getConfig().getConfigurationSection("instances").getKeys(false)){
			FireMasterInstance earthMasterInstance;
			String s = "instances." + ID + ".";
			if(s.contains("default"))
				continue;


			earthMasterInstance = new FireMasterInstance(ID );


			if(getConfig().contains(s + "minimumplayers"))
				earthMasterInstance.setMIN_PLAYERS(getConfig().getInt(s +"minimumplayers"));
			else
				earthMasterInstance.setMIN_PLAYERS(getConfig().getInt("instances.default.minimumplayers"));
			if(getConfig().contains(s + "maximumplayers"))
				earthMasterInstance.setMAX_PLAYERS(getConfig().getInt(s + "maximumplayers"));
			else
				earthMasterInstance.setMAX_PLAYERS(getConfig().getInt("instances.default.maximumplayers"));
			if(getConfig().contains(s + "mapname"))
				earthMasterInstance.setMapName(getConfig().getString(s + "mapname"));
			else
				earthMasterInstance.setMapName(getConfig().getString("instances.default.mapname"));
			if(getConfig().contains(s + "lobbylocation"))
				earthMasterInstance.setLobbyLocation(gameAPI.getLocation(s + "lobbylocation"));
			if(getConfig().contains(s + "Startlocation"))
				earthMasterInstance.setStartLocation(gameAPI.getLocation(s + "Startlocation"));
			else{
				System.out.print(ID + " doesn't contains an start location!");
				gameAPI.getGameInstanceManager().registerGameInstance(earthMasterInstance);
				continue;
			}
			if(getConfig().contains(s + "Endlocation"))
				earthMasterInstance.setEndLocation(gameAPI.getLocation(s + "Endlocation"));
			else{
				if(!gameAPI.isBungeeActivated()) {
					System.out.print(ID + " doesn't contains an end location!");
					gameAPI.getGameInstanceManager().registerGameInstance(earthMasterInstance);
					continue;
				}
			}


			if(gameAPI.needsMapRestore() && getConfig().contains(s + "schematic")){
				if(!getConfig().getString(s + "schematic").contains(" schematic")) {
					earthMasterInstance.setSchematicName(getConfig().getString(s + "schematic"));
				}else{
					System.out.print("You need to assign a schematic file to the arena" + s+ ". You can do this in the config or with the ingame-command /earthmaster <arena> set schematic <name of file without .schematic!>");
					gameAPI.getGameInstanceManager().registerGameInstance(earthMasterInstance);

					continue;

				}
			}else{
				if(gameAPI.needsMapRestore()){
					System.out.print("No schematic found for arena " + s + ". You need to assign an schematic file to that arena! You can do this with the ingame-command /earthmaster <arena> set schematic <name of file without .schematic!>");
					gameAPI.getGameInstanceManager().registerGameInstance(earthMasterInstance);

					continue;
				}
			}
			gameAPI.getGameInstanceManager().registerGameInstance(earthMasterInstance);
			earthMasterInstance.start();


		}
	}


	public void loadLanguageFile(){
		FileConfiguration config = ConfigurationManager.getConfig("language");
		try {
			config.save("language");
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private static Economy economy;
	private static int thirdPlaceReward, secondPlaceReward, firstPlaceReward;


	public void setupVaultEconomy(){




		if(Bukkit.getPluginManager().getPlugin("Vault") instanceof Vault)
		{
			RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

			if(service != null)
				economy = service.getProvider();
		}else{
			System.out.print("Vault isn't on your server!");
		}

		FileConfiguration config = ConfigurationManager.getConfig("vaultconfig");
		if(!config.contains("VaultRewardThirdPlace"))
			config.set("VaultRewardThirdPlace", 10);
		if(!config.contains("VaultRewardSecondPlace"))
			config.set("VaultRewardSecondPlace", 25);
		if(!config.contains("VaultRewardFirstPlace"))
			config.set("VaultRewardFirstPlace", 100);
		try {
			config.save(ConfigurationManager.getFile("vaultconfig"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		firstPlaceReward = config.getInt("VaultRewardFirstPlace");
		secondPlaceReward = config.getInt("VaultRewardSecondPlace");
		thirdPlaceReward = config.getInt("VaultRewardThirdPlace");
	}

	public static Economy getEconomy(){
		return economy;
	}

	public static int getFirstPlaceReward(){
		return  firstPlaceReward;
	}

	public static int getThirdPlaceReward() {
		return thirdPlaceReward;
	}

	public static int getSecondPlaceReward() {
		return secondPlaceReward;
	}

	public static boolean isVaultActivated() {
		return vaultactivated;
	}

	public MyDatabase getMyDatabase(){
		return myDatabase;
	}

	public StatsMenu getStatsMenu(){
		return statsMenu;
	}

	public static boolean isItemRewardActivated(){
		return itemRewardActivated;
	}

	public static  boolean isDatabasActivated(){
		return databasActivated;
	}

	public static void disableMultipleSpawns(){
		multiplespawns = false;
	}
	public boolean checkPlayerCommands(Player arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean checkSpecialCommands(Player arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return false;
	}


}
