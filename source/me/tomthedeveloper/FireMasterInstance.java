package me.tomthedeveloper;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

import me.TomTheDeveloper.GameAPI;
import me.TomTheDeveloper.User;
import me.TomTheDeveloper.Bungee.Bungee;
import me.TomTheDeveloper.Game.GameInstance;
import me.TomTheDeveloper.Game.GameState;
import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.Utils.Items;
import me.TomTheDeveloper.Utils.SchematicPaster;
import me.TomTheDeveloper.Utils.Util;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;

/**
 * Created by Tom on 27/07/2014.
 */
public class FireMasterInstance extends GameInstance {

	@SuppressWarnings("unused")
	private ScoreboardManager scoreboardManager;
	private ArrayList<Location> spawnlocations = new ArrayList<Location>();



	@Override
	public boolean needsPlayers() {
		if(!FireMaster.isDynamicSignsEnabled()){
			return true;
		}else {
			if (((getGameState() == GameState.STARTING && getTimer() >= 15) || getGameState() == GameState.WAITING_FOR_PLAYERS)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public FireMasterInstance(String ID) {
		super(ID);
		if (FireMaster.isMultipleSpawnsEnabled()) {
			if (FireMaster.instance.getConfig().contains("instances." + getID() + ".spawnlocations")) {
				for (String s : FireMaster.instance.getConfig().getConfigurationSection("instances." + getID() + ".spawnlocations").getKeys(false)) {
					String path = "instances." + getID() + ".spawnlocations." + s;
					spawnlocations.add(plugin.getLocation(path));
				}
			}
		} else {
			FireMaster.disableMultipleSpawns();
			System.out.println("You enabled the multiple spawn system but there are no spawns found!");

		}



		setMIN_PLAYERS(1);


	}

	public GameInstance getGameInstance(){
		return this;
	}


	public void start(){
		runTaskTimer(FireMaster.instance, 20L, 20L);
		getStartLocation().getWorld().setGameRuleValue("keepInventory", "on");
		getStartLocation().getWorld().setPVP(false);

		this.setGameState(GameState.RESTARTING);
		if(!FireMaster.isDynamicSignsEnabled())
			plugin.getSignManager().addToQueue(this);




	}

	@SuppressWarnings("deprecation")
	public void run() {

		User.handleCooldowns();
		updateScoreboard();
		//if(plugin.isBarEnabled())
		//  updateBar();
		getStartLocation().getWorld().setTime(0);
		for(Player player:getPlayers()){
			if (player.getLocation().getY() < 0)
				player.setHealth(0);
		}

		switch (getGameState()){

			case WAITING_FOR_PLAYERS:
				showPlayers();
				if(plugin.isBungeeActivated()) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (!getPlayers().contains(player))
							joinAttempt(player);
						for(Player player1:getPlayers()){
							player.showPlayer(player1);
							player1.showPlayer(player);
						}
					}
				}


				if(getPlayers().size() < getMIN_PLAYERS()){

					if(getTimer() <= 0){
						setTimer(15);
						getChatManager().broadcastMessage("WaitingForPlayersMessage");
						return;
					}
				}else{
					getChatManager().broadcastMessage("EnoughPlayersToStartMessage");
					setGameState(GameState.STARTING);
					setTimer(30);
					this.showPlayers();

				}
				setTimer(getTimer()-1);
				break;
			case STARTING:
				showPlayers();
				if(getPlayers().size() < getMIN_PLAYERS()){
					setGameState(GameState.WAITING_FOR_PLAYERS);
				}
				if(getTimer() == 30 || getTimer() == 15 || (getTimer() <10 && getTimer() != 0)){
					getChatManager().broadcastMessage("SecondsLeftUntilGameStartsMessage");
					if(getTimer()<11) {
						for (Player player : getPlayers()) {
							sendTitleMessage(player, ChatManager.HIGHLIGHTED + Integer.toString(getTimer()));
						}
					}

				}
				if(getTimer() == 15){
					if(FireMaster.isMultipleSpawnsEnabled()){
						teleportToSpawns();
					}else {
						this.teleportAllToStartLocation();
					}
					this.sendInfoMessage();
					for(Player player: getPlayers()){
						for(int slot =0; 9>slot; slot++){
							player.getInventory().setItem(slot, getFireItem());
						}
						player.updateInventory();
					}
				}
				if(getTimer() == 2)
					if(FireMaster.isMultipleSpawnsEnabled()){
						teleportToSpawns();
					}else {
						this.teleportAllToStartLocation();
					}
				if(getTimer() <= 0){
					setGameState(GameState.INGAME);
					for(Player player:getPlayers()){
						sendSubTitleMessage(player, getChatManager().getMessage("BattleBeginMessage"));
					}
					this.sendInfoMessage();
					setTimer(1000);
					getChatManager().broadcastMessage("BattleBeginMessage");
					this.showPlayers();

					for(User user:UserManager.getUsers(this)){
						user.setPower(15);
						user.toPlayer().setGameMode(GameMode.ADVENTURE);
						user.setFakeDead(false);
						user.setSpectator(false);
						user.setAllowDoubleJump(true);
						user.addInt("gamesplayed", 1);
						user.toPlayer().getInventory().clear();
						if(user.isPremium())
							user.allowDoubleJump();

					}
					for(Player player: getPlayers()){
						for(int slot =0; 9>slot; slot++){
							player.getInventory().setItem(slot, getFireItem());
						}
						player.updateInventory();
						player.setFoodLevel(20);
						player.setHealth(player.getMaxHealth());
						this.showPlayers();

					}

				}

				setTimer(getTimer()-1);
				break;
			case INGAME:
				if(getTimer() <=0){
					getChatManager().broadcastMessage("NoWinnerMessage");
					setGameState(GameState.ENDING);
					setTimer(10);


				}

				if(getPlayersLeft().size() == 1){
					setFirstPlace(getPlayersLeft().get(0));
					getChatManager().broadcastMessage("WinMessage", getLastPlayerLeft()/*ChatManager.HIGHLIGHTED + getLastPlayerLeft().getName() + ChatManager.NORMAL + " has won!"*/);
					setTimer(10);
					getChatManager().broadcastMessage("TeleportToEndLocationInXSeconds");
					setGameState(GameState.ENDING);
					if(getFirstPlace() != null) {
						getChatManager().broadcastMessage(ChatColor.BOLD + "-----------------------------");
						getChatManager().broadcastMessage("");
						getChatManager().broadcastMessage(getChatManager().getMessage("FirstPlaceWinMessage", ChatManager.NORMAL + "First  place: " + ChatManager.PREFIX + "%PLAYER%", getFirstPlace()).replaceAll("%MONEY%", Integer.toString(FireMaster.getFirstPlaceReward())));
						if(getSecondPlace() != null) {
							UserManager.getUser(getSecondPlace().getUniqueId()).addInt("second", 1);
							getChatManager().broadcastMessage(getChatManager().getMessage("SecondPlaceWinMessage", ChatManager.NORMAL + "Second  place: " + ChatManager.HIGHLIGHTED + "%PLAYER%", getSecondPlace()).replaceAll("%MONEY%", Integer.toString(FireMaster.getSecondPlaceReward())));
						}
						if(getThirdPlace() != null)
							getChatManager().broadcastMessage(getChatManager().getMessage("ThirdPlaceWinMessage", ChatManager.NORMAL + "Third  place: " + ChatManager.HIGHLIGHTED + "%PLAYER%", getThirdPlace()).replaceAll("%MONEY%", Integer.toString(FireMaster.getThirdPlaceReward())));
						getChatManager().broadcastMessage("");

						getChatManager().broadcastMessage(ChatColor.BOLD + "-----------------------------");

						if (FireMaster.isVaultActivated()) {
							FireMaster.getEconomy().depositPlayer(getFirstPlace().getName(), FireMaster.getFirstPlaceReward());
							String firstmessage = getChatManager().getMessage("VaultRewardFirstPlace").replaceAll("%MONEY%", Integer.toString(FireMaster.getFirstPlaceReward()));
							getFirstPlace().sendMessage(firstmessage);
							if(getSecondPlace() != null){
								FireMaster.getEconomy().depositPlayer(getSecondPlace().getName(), FireMaster.getSecondPlaceReward());
								String message = getChatManager().getMessage("VaultRewardSecondPlace").replaceAll("%MONEY%", Integer.toString(FireMaster.getSecondPlaceReward()));
								getSecondPlace().sendMessage(message);
							}
							if(getThirdPlace() != null){
								FireMaster.getEconomy().depositPlayer(getThirdPlace().getName(), FireMaster.getThirdPlaceReward());
								String message = getChatManager().getMessage("VaultRewardThirdPlace").replaceAll("%MONEY%", Integer.toString(FireMaster.getThirdPlaceReward()));
								getThirdPlace().sendMessage(message);
							}
						}
						for(Player player:getPlayers()){
							User user = UserManager.getUser(player.getUniqueId());
							if(getFirstPlace().getUniqueId() == player.getUniqueId()){
								user.addInt("points", 10);
								user.addInt("wins", 1);
							}else{
								user.removeInt("points", 3);
								user.addInt("loses", 1);
							}

						}


					}



				}

				if(getPlayersLeft().size() <=0){
					getChatManager().broadcastMessage("ErrorGameStopMessageNormallyNeverHappens");
					setTimer(10);
					getChatManager().broadcastMessage("TeleportToEndLocationInXSeconds");
					setGameState(GameState.ENDING);
					for(User user: UserManager.getUsers(this)){
						user.removeScoreboard();
						//  BarAPI.removeBar(user.toPlayer());
					}
				}


				for(Player p: getPlayers()){
					if(!UserManager.getUser(p.getUniqueId()).isFakeDead())
						UserManager.getUser(p.getUniqueId()).addPower();

					if(UserManager.getUser(p.getUniqueId()).isSpectator() && p.getInventory().getItem(0)  == null){
						for(int slot = 0; slot<9; slot++){
							p.getInventory().setItem(slot, Items.getSpecatorItemStack());
						}
					}

				}
				setTimer(getTimer()-1);

				break;
			case ENDING:
				this.showPlayers();
				for(Player p: getPlayers()){
					if(!UserManager.getUser(p.getUniqueId()).isSpectator())
						Util.spawnRandomFirework(p.getLocation());
				}
				if(getTimer() <= 0){
					for(User user:UserManager.getUsers(this)){

						user.toPlayer().setFlying(false);
						user.toPlayer().setAllowFlight(false);

						user.setFakeDead(false);
						user.setSpectator(false);
						user.removeScoreboard();
						user.setAllowDoubleJump(false);


					}
					for(Player p: getPlayers()){
						p.setGameMode(GameMode.SURVIVAL);
						p.setWalkSpeed(0.2F);
						p.getInventory().clear();
						p.updateInventory();
						p.setFireTicks(0);
						p.setFlying(false);
						p.setAllowFlight(false);

						// if(BarAPI.hasBar(p))
						//   BarAPI.removeBar(p);
						teleportAllToEndLocation();
						p.setFlying(false);
						p.setAllowFlight(false);


					}
					if(FireMaster.isItemRewardActivated()){
						String firstcommand = FireMaster.instance.getConfig().getString("ItemRewardDispatchCommandFirst");
						String secondcommand = FireMaster.instance.getConfig().getString("ItemRewardDispatchCommandSecond");
						String thirdcommand = FireMaster.instance.getConfig().getString("ItemRewardDispatchCommandThird");
						if(getFirstPlace() == null){
							System.out.print("NO WINNER FOUND! IF THIS HAPPENS A LOT OF TIMES. PLEASE REPORT IT THIS IN THE DISSCUSSION THREAD ON SPIGOT!");
						}else {
							if (firstcommand.contains("%PLAYER%")) {
								firstcommand = firstcommand.replaceAll("%PLAYER%", getFirstPlace().getName());
							}
							FireMaster.instance.getServer().dispatchCommand(FireMaster.instance.getServer().getConsoleSender(), firstcommand);
						}
						if(getSecondPlace() != null) {
							if (secondcommand.contains("%PLAYER%")) {
								secondcommand = secondcommand.replaceAll("%PLAYER%", getSecondPlace().getName());
								FireMaster.instance.getServer().dispatchCommand(FireMaster.instance.getServer().getConsoleSender(), secondcommand);
							}
						}
						if(getThirdPlace() !=null) {
							if (thirdcommand.contains("%PLAYER%")) {
								thirdcommand = thirdcommand.replace("%PLAYER%", getThirdPlace().getName());
								FireMaster.instance.getServer().dispatchCommand(FireMaster.instance.getServer().getConsoleSender(), thirdcommand);
							}
						}
					}

					this.removeAllPlayers();


					this.resetPlaces();
					if(plugin.isBungeeActivated()){
						if(FireMaster.doStopOnFinish()){
							Bukkit.getServer().getScheduler().runTaskLater(FireMaster.instance, new Runnable() {
								public void run() {
									Bukkit.getServer().shutdown();
								}
							}, 20*5);
							return;

						}else{
							setGameState(GameState.RESTARTING);

							for(Player player: Bukkit.getOnlinePlayers()){
								Bungee.connectToHub(player);
							}
							return;
						}
					}
					setGameState(GameState.RESTARTING);
					if(plugin.isBungeeActivated()) {
						for (Player player : Bukkit.getOnlinePlayers()) {
							Bungee.connectToHub(player);
						}
					}

				}
				setTimer(getTimer()-1);
				break;
			case RESTARTING:
				this.showPlayers();
				for(Player p : getPlayers()){
					this.removePlayer(p);
					UserManager.getUser(p.getUniqueId()).removeScoreboard();
					this.teleportToEndLocation(p);
					p.setFlying(false);
					p.setAllowFlight(false);
					User user = UserManager.getUser(p.getUniqueId());
					user.setAllowDoubleJump(false);

				}
				if(plugin.isBungeeActivated()){
					for(Player player: Bukkit.getOnlinePlayers()){
						Bungee.connectToHub(player);
					}
				}
				reInitMap();
				if(!GameAPI.getRestart()) {

					setGameState(GameState.WAITING_FOR_PLAYERS);
					if(FireMaster.isDynamicSignsEnabled())
						plugin.getSignManager().addToQueue(this);
				}

				break;
			default:
				setGameState(GameState.WAITING_FOR_PLAYERS);
		}
	}

	public void sendSubTitleMessage(Player player, String message){
		PlayerConnection titleConnection = ((CraftPlayer) player).getHandle().playerConnection;
		IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleMain);
		titleConnection.sendPacket(packetPlayOutTitle);
	}


	public void updateScoreboard(){
		if(getPlayers() == null)
			return;
		if(getPlayers().size() == 0)
			return;
		for(Player p:getPlayers()){
			if(p == null)
				removePlayer(p);
		}
		for(Player p: getPlayers()) {
			User user = UserManager.getUser(p.getUniqueId());
			if(user.getScoreboard().getObjective("waiting") == null){
				user.getScoreboard().registerNewObjective("waiting","dummy");
				user.getScoreboard().registerNewObjective("starting", "dummy");
				user.getScoreboard().registerNewObjective("ingame", "dummy");

			}
			switch (getGameState()) {
				case WAITING_FOR_PLAYERS:
					Objective waitingobj = user.getScoreboard().getObjective("waiting");
					waitingobj.setDisplayName(getChatManager().getMessage("ScoreboardHeader", ChatManager.PREFIX + "FireMaster"));
					waitingobj.setDisplaySlot(DisplaySlot.SIDEBAR);

					Score playerscore1 = waitingobj.getScore(getChatManager().getMessage("ScoreboardPlayersWaitingMessage"));
					playerscore1.setScore(getPlayers().size());
					Score minplayerscore1 = waitingobj.getScore(getChatManager().getMessage("ScoreboardMinPlayersMessage"));
					minplayerscore1.setScore(getMIN_PLAYERS());
					break;
				case STARTING:
					Objective startingobj = user.getScoreboard().getObjective("starting");
					startingobj.setDisplayName(getChatManager().getMessage("ScoreboardHeader", ChatManager.PREFIX + "FireMaster"));
					startingobj.setDisplaySlot(DisplaySlot.SIDEBAR);
					if(!plugin.isBarEnabled()) {
						Score timerscore = startingobj.getScore(getChatManager().getMessage("ScoreboardStartingInMessage"));
						timerscore.setScore(getTimer());
					}
					Score playerscore = startingobj.getScore(getChatManager().getMessage("ScoreboardPlayersWaitingMessage"));
					playerscore.setScore(getPlayers().size());
					Score minplayerscore = startingobj.getScore(getChatManager().getMessage("ScoreboardMinPlayersMessage"));
					minplayerscore.setScore(getMIN_PLAYERS());


					break;
				case INGAME:
					Objective ingameobj = user.getScoreboard().getObjective("ingame");
					ingameobj.setDisplayName( getChatManager().getMessage("ScoreboardHeader", ChatManager.PREFIX + "FireMaster"));
					ingameobj.setDisplaySlot(DisplaySlot.SIDEBAR);
					Score playerleftscore = ingameobj.getScore(getChatManager().getMessage("ScoreboardPlayersLeftMessage"));
					playerleftscore.setScore(this.getPlayersLeft().size());
					if(!plugin.isBarEnabled()) {
						Score timeleftscore = ingameobj.getScore(getChatManager().getMessage("ScoreboardTimeLeftMessage"));
						timeleftscore.setScore(getTimer());
					}
					/*Score powerscore = ingameobj.getScore(ChatManager.HIGHLIGHTED + "Power: ");
                    powerscore.setScore(user.getPower()); */
					break;
				case ENDING:
					break;
				case RESTARTING:

					break;
				default:
					setGameState(GameState.WAITING_FOR_PLAYERS);
			}
			user.setScoreboard(user.getScoreboard());
		}
	}

	@SuppressWarnings("deprecation")
	public void onDeath( final Player p){
		if(getGameState() == GameState.RESTARTING || getGameState() == GameState.ENDING){
			p.getInventory().clear();
			Bukkit.getScheduler().scheduleSyncDelayedTask(FireMaster.instance, new Runnable() {
				public void run() {
					teleportToEndLocation(p);
					p.setWalkSpeed(0.2F);
					p.getInventory().clear();
					p.updateInventory();
					p.setFireTicks(0);
					p.setFlying(false);
					p.setAllowFlight(false);

					// if(BarAPI.hasBar(p))
					//   BarAPI.removeBar(p);

					p.setFlying(false);
					p.setAllowFlight(false);
					User user = UserManager.getUser(p.getUniqueId());
					user.setFakeDead(false);
					user.setSpectator(false);
					user.removeScoreboard();
					user.setAllowDoubleJump(false);
					removePlayer(p);

				}
			});
			return;

		}
		p.setGameMode(GameMode.ADVENTURE);
		User user = UserManager.getUser(p.getUniqueId());
		if(!user.isSpectator()) {

			getChatManager().broadcastDeathMessage(p);
		}

		if(!user.isSpectator()) {
			p.sendMessage(getChatManager().getMessage("YouAreNowASpectatorMessage"));
		}
		if(!user.isSpectator()){
			user.setFakeDead(true);
			user.setSpectator(true);
			p.teleport(getStartLocation());
			if(getPlayersLeft().size() == 2){
				// FireMaster.getEconomy().depositPlayer(p.getName(), FireMaster.getThirdPlaceReward());
				// String message = getChatManager().getMessage("VaultRewardThirdPlace").replaceAll("%MONEY%", Integer.toString( FireMaster.getThirdPlaceReward()));
				//p.sendMessage(message);
				setThirdPlace(p);
			}
			if(getPlayersLeft().size() == 1){
				// FireMaster.getEconomy().depositPlayer(p.getName(), FireMaster.getSecondPlaceReward());
				// String message = getChatManager().getMessage("VaultRewardSecondPlace").replaceAll("%MONEY%", Integer.toString( FireMaster.getSecondPlaceReward()));
				// p.sendMessage(message);
				setSecondPlace(p);
			}

			//  FireMaster.getEconomy().depositPlayer(p.getName(), FireMaster.getFirstPlaceReward());
			//  String message = getChatManager().getMessage("VaultRewardFirstPlace").replaceAll("%MONEY%", Integer.toString( FireMaster.getFirstPlaceReward()));
			//  p.sendMessage(message);

		}
		user.removeInt("points", 1);
		user.addInt("deaths", 1);
		if(user.getLastHitted() != null){
			UserManager.getUser(user.getLastHitted().getUniqueId()).addInt("points", 2);
			user.getLastHitted().sendMessage(getChatManager().getMessage("KilledPlayerMessage", ChatColor.GREEN + "You have killed "+ChatManager.HIGHLIGHTED +"%PLAYER%" + "!", user.toPlayer()));

		}
		user.setLastHitted(null);

		user.setFakeDead(true);
		user.setSpectator(true);
		p.setAllowFlight(true);
		p.setFlying(true);
		for(Player player:getPlayers()){
			if(UserManager.getUser(player.getUniqueId()).isSpectator()) {
				player.hidePlayer(p);
				p.hidePlayer(player);
			}
		}
		teleportToStartLocation(p);
		Bukkit.getScheduler().scheduleSyncDelayedTask(FireMaster.instance, new Runnable() {
			public void run() {

				p.teleport(getStartLocation());
			}
		});
		p.getInventory().clear();

		for(int i = 0; i<=8; i++){
			p.getInventory().setItem(i, Items.getSpecatorItemStack());
		}

		p.updateInventory();

		hidePlayer(p);
		FireMaster.instance.getServer().getScheduler().runTaskLater(FireMaster.instance, new Runnable(){

			public void run() {
				for (Entity entity : getStartLocation().getWorld().getEntities()) {
					if (entity.getType() == EntityType.DROPPED_ITEM) {
						Item item = (Item) entity;
						if (item.getItemStack().getType() == Material.BLAZE_POWDER || item.getItemStack().getType() == Material.COMPASS)
							item.remove();
					} 
				}
			}
		}, 1L);






	}

	public void sendTitleMessage(Player player, String message){
		PlayerConnection titleConnection = ((CraftPlayer) player).getHandle().playerConnection;
		IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
		titleConnection.sendPacket(packetPlayOutTitle);
	}

	public Player getLastPlayerLeft(){
		Player p = null;
		int i = 0;
		for(User user:UserManager.getUsers(this)){
			if(!user.isFakeDead()){
				p = user.toPlayer();
				i++;
			}
		}
		if(i >1)
			throw new NullPointerException("More than one winner?");
		if(p == null)
			throw new NullPointerException("No winner found!");
		return p;

	}





	public void reInitMap(){

		SchematicPaster.pasteSchematic(getSchematicName(), getStartLocation());
	}

	@SuppressWarnings("unused")
	private void updateBar() {
		switch (getGameState()) {
			case WAITING_FOR_PLAYERS:
				for (Player player : getPlayers()) {

					//  if (BarAPI.hasBar(player))
					//    BarAPI.removeBar(player);
				}
				break;

			case STARTING:

				for (Player player : getPlayers()) {

					float percentage = (float) Math.ceil((double) (100 * getTimer() / 30));

					//  BarAPI.setMessage(player, getChatManager().getMessage("BossBarStartingInMessage"), percentage);

				}
				break;
			case INGAME:
				for (Player p : getPlayers()) {
					//  BarAPI.setMessage(p, getChatManager().getMessage("BossBarTimeLeftMessage"), (float) Math.ceil((double) getTimer() / 1000 * 100));
				}
				break;
			default:
				for (Player player : getPlayers()) {
					// BarAPI.removeBar(player);
				}
		}
	}



	private void sendInfoMessage(){
		for(Player player:getPlayers()){
			player.sendMessage(getChatManager().getMessage("InformationHeadMessage"));
			player.sendMessage(getChatManager().getMessage("InformationLeftClickMessage")  );
			// player.sendMessage(ChatManager.NORMAL + "  Shoots fire!");
			player.sendMessage(getChatManager().getMessage("InformationRightClickMessage")  );
			// player.sendMessage(ChatManager.NORMAL + "  Shoots explosive fire!");
			player.sendMessage(getChatManager().getMessage("InformationShiftedLeftClick") );
			// player.sendMessage(ChatManager.NORMAL + "  Charges a lightning strike!");
			player.sendMessage(getChatManager().getMessage("InformationShiftedRightClickMessage")  );
			// player.sendMessage(ChatManager.NORMAL + "  Heal yourself!");
			player.sendMessage(getChatManager().getMessage("InformationBottomMessage"));

		}
	}



	public ItemStack getFireItem(){
		ItemStack itemStack = new ItemStack(Material.BLAZE_POWDER);
		Util.setItemNameAndLore(itemStack, ChatManager.PREFIX + "INFO", new String[]{
				getChatManager().getMessage("InformationLeftClickMessage") ,
				getChatManager().getMessage("InformationRightClickMessage") ,
				getChatManager().getMessage("InformationShiftedLeftClick"),
				getChatManager().getMessage("InformationShiftedRightClickMessage"),



		});
		return itemStack;
	}


	@SuppressWarnings("unchecked")
	public void teleportToSpawns(){
		// teleportAllToStartLocation(); //this is just safe coding.
		if(spawnlocations.isEmpty()){
			teleportAllToStartLocation();
			System.out.print("You don't have spawns for the multiple spawn system.");
			return;
		}
		List<Location> spawnlocs = (List<Location>) spawnlocations.clone();

		for(Player player:getPlayers()){
			if(spawnlocs.isEmpty() || spawnlocs.get(0) == null){
				spawnlocs = (List<Location>) spawnlocations.clone();
				if(spawnlocs.get(0) == null){
					teleportAllToStartLocation();
					System.out.print("Multiple spawn system isn't working fine! Tell it to the dev!");
					return;
				}
				player.teleport(spawnlocs.get(0));
				spawnlocs.remove(0);
			}else {
				if(spawnlocs.get(0) == null){
					teleportAllToStartLocation();
					System.out.print("Multiple spawn system isn't working fine! Tell it to the dev!");
					return;
				}
				player.teleport(spawnlocs.get(0));
				spawnlocs.remove(0);
			}

		}



	}

}
