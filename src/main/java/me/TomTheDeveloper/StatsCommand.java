package me.TomTheDeveloper;

import com.mongodb.BasicDBObject;
import me.TomTheDeveloper.Handlers.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 8/09/2014.
 */
public class StatsCommand implements CommandExecutor {

    private FireMaster plugin;

    public StatsCommand(FireMaster plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String b, String[] strings) {
        if(!(commandSender instanceof Player)){
            System.out.println(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + "This is a player command!"));
            return true;
        }
        if(!FireMaster.isDatabasActivated())
            return true;
        if(command.getLabel().equalsIgnoreCase("stats")){
            if(strings.length == 1){

                if(Bukkit.getOfflinePlayer(strings[0]) == null){
                    commandSender.sendMessage(ChatColor.RED + "That player doesn't exists!");
                    return true;
                }
                if(plugin.getMyDatabase().getSingle(new BasicDBObject("UUID", Bukkit.getOfflinePlayer(strings[0]).getUniqueId().toString()))== null){
                    commandSender.sendMessage(ChatColor.RED + "This player hasn't played FireMaster yet!");
                    return true;
                }else{
                    for(Player player:Bukkit.getOnlinePlayers()){
                        if(player.getUniqueId() == Bukkit.getOfflinePlayer(strings[0]).getUniqueId()) {
                            ((Player) commandSender).openInventory(plugin.getStatsMenu().createMenu(player));
                            return true;
                        }
                    }
                    User user = UserManager.getUser(Bukkit.getOfflinePlayer(strings[0]).getUniqueId());
                    if(plugin.getMyDatabase().getSingle(new BasicDBObject().append("UUID",Bukkit.getOfflinePlayer(strings[0]).getUniqueId().toString())) == null){
                        plugin.getMyDatabase().insertDocument(new String[]{"UUID", "gamesplayed", "points", "fireblast", "fireexplosion", "firelightning", "fireheal", "wins", "loses", "deaths", "second"},
                                new Object[]{Bukkit.getOfflinePlayer(strings[0]).getUniqueId().toString(), 0,0,0,0,0,0,0,0,0, 0});
                    }

                    List<String> temp = new ArrayList<String>();
                    temp.add("gamesplayed");
                    temp.add("points");
                    temp.add("fireblast");
                    temp.add("fireexplosion");
                    temp.add("firelightning");
                    temp.add("fireheal");
                    temp.add("wins");
                    temp.add("loses");
                    temp.add("deaths");
                    temp.add("second");
                    for(String s:temp){
                        user.setInt(s, (Integer) plugin.getMyDatabase().getSingle(new BasicDBObject("UUID", Bukkit.getOfflinePlayer(strings[0]).getUniqueId().toString())).get(s));
                    }
                    ((Player) commandSender).openInventory(plugin.getStatsMenu().createMenu(Bukkit.getOfflinePlayer(strings[0])));
                    UserManager.removeUser(Bukkit.getOfflinePlayer(strings[0]).getUniqueId());
                }
            }else {
                ((Player) commandSender).openInventory(plugin.getStatsMenu().createMenu((Player) commandSender));
                System.out.print("Showed stats succesfully!");
                return true;
            }
        }
        return false;
    }
}
