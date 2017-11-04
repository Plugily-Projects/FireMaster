package me.TomTheDeveloper;

import me.TomTheDeveloper.Game.GameInstance;
import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.Handlers.GameInstanceManager;
import me.TomTheDeveloper.Permmissions.PermStrings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 5/02/2015.
 */
public class joinCommand implements CommandExecutor {

    private FireMaster plugin;


    public joinCommand(FireMaster plugin){
        this.plugin = plugin;
    }



    @Override
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
        for(GameInstance instance: plugin.getGameInstanceManager().getGameInstances() ){
            if(instance.getID().equalsIgnoreCase(strings[1])){
                for(GameInstance gameInstance: plugin.getGameInstanceManager().getGameInstances()){
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
                                p.sendMessage(plugin.getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("YouGotKickedToMakePlaceForAPremiumPlayer",ChatColor.RED + "You got kicked out of the game to make place for a premium player!"));
                                instance.getChatManager().broadcastMessage(plugin.getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("KickedToMakePlaceForPremiumPlayer","%PLAYER% got removed from the game to make place for a premium players!", p));
                                instance.joinAttempt(player);
                                b = true;
                                return true;
                            }

                        }
                        if (!b) {
                            player.sendMessage(plugin.getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("FullGameAlreadyFullWithPermiumPlayers", ChatColor.RED + "This game is already full with premium players! Sorry"));
                            return true;
                        } else {
                            return true;
                        }

                    }else{
                        player.sendMessage(plugin.getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("NoPermissionToJoinFullGames","You don't have the permission to join full games!"));

                        return true;
                    }
                    // instance.joinAttempt(event.getPlayer());

                }else {
                    instance.joinAttempt(player);
                    return true;
                }

            }
        }
        player.sendMessage(plugin.getGameInstanceManager().getGameInstances().get(0).getChatManager().getMessage("WrongArenaName", ChatColor.RED + "The arenaName u used doesn't exist! Do /join firemaster <ARENA>"));
    return true;
    }
}
