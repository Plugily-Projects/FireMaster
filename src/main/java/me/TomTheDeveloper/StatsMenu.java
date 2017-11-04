package me.TomTheDeveloper;

import me.TomTheDeveloper.Handlers.ConfigurationManager;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.MenuAPI.IconMenu;
import org.apache.logging.log4j.core.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.*;

/**
 * Created by Tom on 8/09/2014.
 */
public class StatsMenu {

    private FileConfiguration config ;
    private FireMaster plugin;





    public StatsMenu(FireMaster plugin){
        this.plugin = plugin;
    }

    public Inventory createMenu(OfflinePlayer player){
        User user = UserManager.getUser(player.getUniqueId());



        Inventory inventory = Bukkit.getServer().createInventory(null, 36, "Stats from " + player.getName());
        inventory.setItem(11, convertStatInName(player, "points"));
        inventory.setItem(12, convertStatInName(player, "gamesplayed"));
        inventory.setItem(13, convertStatInName(player, "wins"));
        inventory.setItem(14, convertStatInName(player, "loses"));
        inventory.setItem(15, convertStatInName(player, "deaths"));
        inventory.setItem(24, convertStatInName(player, "fireblast"));
        inventory.setItem(22, convertStatInName(player, "firelightning"));
        inventory.setItem(21, convertStatInName(player, "fireexplosion"));
        inventory.setItem(23, convertStatInName(player, "fireheal"));
        inventory.setItem(20, convertStatInName(player, "second"));
        return inventory;

    }

    private ItemStack convertStatInName(OfflinePlayer player, String statname){
        ItemStack itemStack = plugin.getStatItems().getItem(statname);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replaceAll("%STAT%", Integer.toString(UserManager.getUser(player.getUniqueId()).getInt(statname))).replaceAll("(&([a-f0-9]))", "\u00A7$2"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }






}
