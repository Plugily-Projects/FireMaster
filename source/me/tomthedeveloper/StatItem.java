package me.tomthedeveloper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.TomTheDeveloper.Handlers.ConfigurationManager;
import me.TomTheDeveloper.Utils.Util;

/**
 * Created by Tom on 8/09/2014.
 */
public class StatItem {

	private HashMap<ItemStack, String> itemnames = new HashMap<ItemStack, String>();
	private HashMap<String, Material> materials = new HashMap<String, Material>();
	@SuppressWarnings("unused")
	private HashMap<String, Integer> places = new HashMap<String, Integer>();
	private HashMap<String, String> names = new HashMap<String, String>();
	private HashMap<String, String[]> lores = new HashMap<String, String[]>();

	public static FireMaster plugin;
	private static FileConfiguration config;


	@SuppressWarnings("static-access")
	public StatItem(FireMaster plugin){
		this.plugin = plugin;
		config = ConfigurationManager.getConfig("StatsMenu");
	}

	public void setup(){
		config = ConfigurationManager.getConfig("StatsMenu");
		if(!config.contains("items")) {
			setToConfig("gamesplayed", ChatColor.GOLD + "Games Played: " + ChatColor.GRAY + "%STAT%",
					new String[]{ChatColor.GRAY + "How many games have", ChatColor.GRAY + "played?"},
					Material.CHEST);
			setToConfig("fireblast", ChatColor.GOLD + "Fire Blasts Fired: " + ChatColor.GRAY + "%STAT%",
					new String[]{ChatColor.GRAY + "How many fire blasts have", ChatColor.GRAY + "fired?"},
					Material.BLAZE_ROD);
			setToConfig("fireexplosion", ChatColor.GOLD + "Fire Explosions Fired: " + ChatColor.GRAY + "%STAT%",
					new String[]{ChatColor.GRAY + "How many fire explosions have", ChatColor.GRAY + "fired?"},
					Material.TNT);
			setToConfig("firelightning", ChatColor.GOLD + "Lightnings Striked: " + ChatColor.GRAY + "%STAT%",
					new String[]{ChatColor.GRAY + "How many lightnings", ChatColor.GRAY + "have you striked?"},
					Material.FLINT_AND_STEEL);
			setToConfig("fireheal", ChatColor.GOLD + "Heals performed: " + ChatColor.GRAY + "%STAT%",
					new String[]{ChatColor.GRAY + "How many heals", ChatColor.GRAY + "have you done?"},
					Material.RED_ROSE);
			setToConfig("wins", ChatColor.GOLD + "Wins: " + ChatColor.GRAY + "%STAT%",
					new String[]{ChatColor.GRAY + "How good", ChatColor.GRAY + "are you?"},
					Material.EMERALD_BLOCK);
			setToConfig("loses", ChatColor.GOLD + "Loses: " + ChatColor.GRAY + "%STAT%",
					new String[]{ChatColor.GRAY + "How bad are", ChatColor.GRAY + "you?"},
					Material.REDSTONE_BLOCK);
			setToConfig("points", ChatColor.GOLD + "Points: " + ChatColor.GRAY + "%STAT%",
					new String[]{ChatColor.GRAY + "How good", ChatColor.GRAY + "are you?"},
					Material.DIAMOND_BLOCK);
			setToConfig("deaths", ChatColor.GOLD + "Deaths: " + ChatColor.GRAY + "%STAT%",
					new String[]{ChatColor.GRAY + "You died so", ChatColor.GRAY + "much?"},
					Material.SKULL_ITEM);
			setToConfig("second", ChatColor.GOLD + "Second Place: " + ChatColor.GRAY + "¨%STAT%",
					new String[]{ChatColor.GRAY + "How many times", ChatColor.GRAY + "were you second?"},
					Material.SIGN);

		}
		loadFromConfig("points");
		loadFromConfig("gamesplayed");
		loadFromConfig("wins");
		loadFromConfig("loses");
		loadFromConfig("fireblast");
		loadFromConfig("fireexplosion");
		loadFromConfig("fireheal");
		loadFromConfig("firelightning");
		loadFromConfig("deaths");
		loadFromConfig("second");
	}





	public  ItemStack getItem(String name){
		ItemStack itemStack = new ItemStack(materials.get(name));
		Util.setItemNameAndLore(itemStack, names.get(name), lores.get(name));
		itemnames.put(itemStack, name);
		/* if(itemStack.getType() == Material.BOW){
            ItemStack itemStack1 = WeaponHelper.getEnchanted(itemStack, new Enchantment[]{Enchantment.ARROW_INFINITE} , new int[]{1});
            return itemStack1;
        } */

		return itemStack;


	}



	public  void setToConfig(String itemname, String name, String[] lore, Material material){
		config.set("items." + itemname + ".name", name);
		config.set("items." + itemname + ".material", material.toString()/*.replaceAll("!! org.bukkit.material ", "")*/);
		config.set("items." + itemname + ".lore", lore);
		try {
			config.save(ConfigurationManager.getFile("StatsMenu"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFromConfig(String itemname){
		names.put(itemname, config.getString("items." + itemname + ".name"));
		List<String> strings =  config.getStringList("items." + itemname + ".lore");

		lores.put(itemname, strings.toArray(new String[strings.size()]));
		materials.put(itemname, Material.getMaterial(config.getString("items." + itemname + ".material")));

	}

}
