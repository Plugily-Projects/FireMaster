package me.tomthedeveloper;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.TomTheDeveloper.Game.GameInstance;
import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.Permmissions.PermStrings;

/**
 * Created by Tom on 5/02/2015.
 */
public class JoinCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private FireMaster plugin;


	public JoinCommand(FireMaster plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(!(commandSender instanceof Player))
			return true;
		Player player = (Player) commandSender;
		if(!(command.getName().equalsIgnoreCase("join")))
			return true;
		if(!(strings.length != 1))
			return true;
		if(!strings[0].equalsIgnoreCase("firemaster"))
			return true;
		for(GameInstance instance: FireMaster.instance.getGameAPI().getGameInstanceManager().getGameInstances() ){
			if(instance.getID().equalsIgnoreCase(strings[1])){
				for(GameInstance gameInstance: FireMaster.instance.getGameAPI().getGameInstanceManager().getGameInstances()){
					if(gameInstance.getPlayers().contains(player)){
						player.sendMessage(ChatManager.getFromLanguageConfig("YouAreAlreadyIngame", ChatColor.RED + "You are already qeued for a game! You can leave a game with /leave."));
						return true;
					}
				}
				if (instance.getMAX_PLAYERS() <= instance.getPlayers().size()) {

					if (player.hasPermission(PermStrings.getVIP()) || player.hasPermission(PermStrings.getJoinFullGames())) {
						boolean b = false;
						for (Player p : instance.getPlayers()) {
							if (p.hasPermission(PermStrings.getVIP()) || p.hasPermission(PermStrings.getJoinFullGames())) {

							} else {
								instance.leaveAttempt(p);
								p.sendMessage(FireMaster.instance.getGameAPI().getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("YouGotKickedToMakePlaceForAPremiumPlayer",ChatColor.RED + "You got kicked out of the game to make place for a premium player!"));
								instance.getChatManager().broadcastMessage(FireMaster.instance.getGameAPI().getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("KickedToMakePlaceForPremiumPlayer","%PLAYER% got removed from the game to make place for a premium players!", p));
								instance.joinAttempt(player);
								b = true;
								return true;
							}

						}
						if (!b) {
							player.sendMessage(FireMaster.instance.getGameAPI().getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("FullGameAlreadyFullWithPermiumPlayers", ChatColor.RED + "This game is already full with premium players! Sorry"));
							return true;
						} else {
							return true;
						}

					}else{
						player.sendMessage(FireMaster.instance.getGameAPI().getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("NoPermissionToJoinFullGames","You don't have the permission to join full games!"));

						return true;
					}
					// instance.joinAttempt(event.getPlayer());

				}else {
					instance.joinAttempt(player);
					return true;
				}

			}
		}
		player.sendMessage(FireMaster.instance.getGameAPI().getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("WrongArenaName", ChatColor.RED + "The arenaName u used doesn't exist! Do /join firemaster <ARENA>"));
		return true;
	}
}
