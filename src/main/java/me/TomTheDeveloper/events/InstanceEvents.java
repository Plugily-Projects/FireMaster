package me.TomTheDeveloper.events;

import com.mongodb.BasicDBObject;
import me.TomTheDeveloper.Events.PlayerAddSpawnCommandEvent;
import me.TomTheDeveloper.FireMaster;
import me.TomTheDeveloper.FireMasterInstance;
import me.TomTheDeveloper.Game.GameInstance;
import me.TomTheDeveloper.Game.GameState;
import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.MenuAPI.SpectatorMenu;
import me.TomTheDeveloper.User;
import me.TomTheDeveloper.Utils.Items;
import me.TomTheDeveloper.Utils.ParticleEffect;
import me.TomTheDeveloper.attacks.FireBlastAttack;
import me.TomTheDeveloper.attacks.FireExlpodeAttack;
import me.TomTheDeveloper.attacks.HealAttack;
import me.TomTheDeveloper.attacks.LightningStrikeAttack;
//import me.confuser.barapi.BarAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Tom on 25/08/2014.
 */
public class InstanceEvents implements Listener {


    private FireMaster plugin;

    public InstanceEvents(FireMaster plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFireBlast(PlayerInteractEvent event){
        if(!(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK))
            return;
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        if(event.getPlayer().isSneaking())
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance.getGameState() != GameState.INGAME)
            return;
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        if(user.isFakeDead() || user.isSpectator())
            return;
        if(user.getCooldown("fireblast") > 0){
            event.getPlayer().sendMessage(gameInstance.getChatManager().getMessage("FireBlastAttackCooldownMessage", event.getPlayer(), "fireblast"));
            return;

        }

        FireBlastAttack fireBlastAttack = new FireBlastAttack(event.getPlayer());
        user.setCooldown("fireblast", 1);
        user.addInt("fireblast", 1);


    }

    @EventHandler
     public void onPlayerDamage(EntityDamageEvent event) {
        Entity ent = event.getEntity();
        if(!(ent instanceof Player))
            return;
        if(plugin.getGameInstanceManager().getGameInstance((Player) ent) == null)
            return;

        if (ent instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHeal(PlayerInteractEvent event){
        if(!event.hasItem())
            return;
        if(!event.getItem().hasItemMeta())
            return;
        if(!event.getItem().getItemMeta().hasDisplayName())
            return;
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(event.getItem().getType() != ((FireMasterInstance) gameInstance).getFireItem().getType())
            return;
        if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
            return;
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        if(!event.getPlayer().isSneaking())
            return;

        if(gameInstance.getGameState() != GameState.INGAME)
            return;
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        if(user.isFakeDead() || user.isSpectator())
            return;
        if(user.getCooldown("heal") > 0){
            event.getPlayer().sendMessage(gameInstance.getChatManager().getMessage("HealCooldownMessage", event.getPlayer(), "heal"));
            return;

        }

        HealAttack fireBlastAttack = new HealAttack(event.getPlayer());
        user.addInt("fireheal", 1);
        user.setCooldown("heal", 35); //normal number 35 (Erikas:10)
        if(user.isPremium())
            user.setCooldown("heal",20); //normal number 20 (Erikas:10)

    }

    @EventHandler
    public void onFireExplode(PlayerInteractEvent event){
        if(!event.hasItem())
            return;
        if(!event.getItem().hasItemMeta())
            return;
        if(!event.getItem().getItemMeta().hasDisplayName())
            return;
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        if(event.getPlayer().isSneaking())
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(!event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(((FireMasterInstance)gameInstance).getFireItem().getItemMeta().getDisplayName()))
            return;
        if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
            return;

        if(gameInstance.getGameState() != GameState.INGAME)
            return;
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        if(user.isFakeDead() || user.isSpectator())
            return;
        if(user.getCooldown("fireexplode") > 0){
        event.getPlayer().sendMessage(gameInstance.getChatManager().getMessage("FireExplosionAttackCooldownMessage", event.getPlayer(), "fireexplode"));
            return;

        }


        FireExlpodeAttack fireExlpodeAttack = new FireExlpodeAttack(event.getPlayer());
        user.addInt("fireexplosion", 1);
        user.setCooldown("fireexplode", 10); //normal number:10 (Erikas:1)
        if(user.isPremium())
            user.setCooldown("fireexplode", 5); //normal number:5 (Erikas:1)


    }

    @EventHandler
    public void onLightningStrike(PlayerInteractEvent event){
        if(!(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK))
            return;
        if(!event.getPlayer().isSneaking())
            return;
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance.getGameState() != GameState.INGAME)
            return;
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        if(user.isFakeDead() || user.isSpectator())
            return;
        if(user.getCooldown("strike") > 0){
            event.getPlayer().sendMessage(ChatManager.getMessage("LightningExplosionAttackCooldownMessage", event.getPlayer(), "strike"));
            return;

        }
        if(event.getPlayer().getTargetBlock((Set<Material>) null, 50) == null) {
            event.getPlayer().sendMessage(gameInstance.getChatManager().getMessage("UnableToStrikeThereMessage"));
            return;
        }
        if( event.getPlayer().getTargetBlock((Set<Material>) null, 50).getType() == Material.AIR){
            event.getPlayer().sendMessage(gameInstance.getChatManager().getMessage("UnableToStrikeThereMessage"));
            return;
        }

        LightningStrikeAttack lightningStrikeAttack = new LightningStrikeAttack(event.getPlayer());
        user.addInt("firelightning", 1);
        user.setCooldown("strike", 25); //normal number:25 (Erikas:15)
        if(user.isPremium())
            user.setCooldown("strike", 15); //normal number 15 (Erikas:15)
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDie(final PlayerDeathEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;
        final Player p = event.getEntity();
        p.getInventory().clear();
        if (plugin.getGameInstanceManager().getGameInstance(p) == null)
            return;



        final GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(p);
        event.setDeathMessage("");
        if (gameInstance.getGameState() == GameState.WAITING_FOR_PLAYERS || (gameInstance.getGameState() == GameState.STARTING && gameInstance.getTimer() >15)){
            gameInstance.teleportToLobby(p);
            this.respawn(p);
            return;
        }


        if (gameInstance.getGameState() != GameState.INGAME && gameInstance.getGameState() != GameState.ENDING){

            if(gameInstance.getGameState() != GameState.STARTING && gameInstance.getGameState() != GameState.WAITING_FOR_PLAYERS)
                p.teleport(gameInstance.getEndLocation());
            this.respawn(p);
            return;
        }
        p.getInventory().clear();
        FireMasterInstance avatarInstance = (FireMasterInstance) gameInstance;
        p.getInventory().clear();
        p.setHealth(20);
        p.teleport(gameInstance.getStartLocation());

        avatarInstance.onDeath(p);
        UserManager.getUser(p.getUniqueId()).setPower(0);



        this.respawn(p);





    }

    @EventHandler
    public void onCommandEventWhileIngame(PlayerCommandPreprocessEvent event){
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(!event.getMessage().contains("leave") || !event.getPlayer().isOp()){
            event.getPlayer().sendMessage(gameInstance.getChatManager().getMessage("BlockCommandWhileIngame"));
            event.setCancelled(true);
        }
    }

    public void respawn(final Player p) {

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                if (p.isDead())
                    p.setHealth(20);
            }
        });

    }

    @EventHandler
    public void onEntityExplode(BlockExplodeEvent e){
        boolean boo = false;
        for(Player player: e.getBlock().getLocation().getWorld().getPlayers()){
            if(plugin.getGameInstanceManager().getGameInstance(player) != null){
                boo = true;
                break;
            }
        }
        if(boo) {
            e.setCancelled(true);

            for (Block b : e.blockList()) {
                bounceBlock(b);
            }
        }
    }

    public void onEntityExplode(EntityExplodeEvent e){
        boolean boo = false;
        for(Player player: e.getLocation().getWorld().getPlayers()){
            if(plugin.getGameInstanceManager().getGameInstance(player) != null){
                boo = true;
                break;
            }
        }
        if(boo) {
            e.setCancelled(true);

            for (Block b : e.blockList()) {
                bounceBlock(b);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        if((plugin.getGameInstanceManager().getGameInstance((Player) event.getEntity())) == null)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) event.getEntity());
        if(gameInstance.getGameState() == GameState.STARTING || gameInstance.getGameState() == GameState.WAITING_FOR_PLAYERS)
            event.setCancelled(true);
    }

    @EventHandler
    public void onDamamge(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        if((plugin.getGameInstanceManager().getGameInstance((Player) event.getEntity())) == null)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) event.getEntity());
        if(gameInstance.getGameState() == GameState.STARTING || gameInstance.getGameState() == GameState.WAITING_FOR_PLAYERS)
            event.setCancelled(true);
    }

    public void bounceBlock(Block b)
    {
        Object fb;
        if(b == null) return;
        if(b.getType() == Material.TNT){
            fb =  b.getWorld().spawn(b.getLocation().add(0,1,0), TNTPrimed.class);


        }else {
            fb =  b.getWorld()
                    .spawnFallingBlock(b.getLocation().add(0, 1, 0), b.getType(), b.getData());
            ((FallingBlock) fb).setDropItem(false);
        }


        b.setType(Material.AIR);

        float x = (float) -1 + (float) (Math.random() * ((1 - -1) + 1));
        float y =  (float)0.5;//(float) -5 + (float)(Math.random() * ((5 - -5) + 1));
        float z = (float) -0.3 + (float)(Math.random() * ((0.3 - -0.3) + 1));

        ((Entity)fb).setVelocity(new Vector(x, y, z));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onOpenMenu(final PlayerInteractEvent event){

        if(!event.hasItem())
            return;

        if(event.getItem().getType() == Material.COMPASS)

            if(!(event.getItem().hasItemMeta()))
                return;
        if(!(event.getItem().getItemMeta().hasDisplayName()))
            return;
        if(!(event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Items.getSpecatorItemStack().getItemMeta().getDisplayName())))
            return;
        if(plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        final GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        SpectatorMenu spectatorMenu = new SpectatorMenu(plugin.getGameInstanceManager().getGameInstance(event.getPlayer())) {
            @Override
            public String[] getDescription(Player player) {
                return new String[]{gameInstance.getChatManager().getMessage("SpectatorMenuLoreItemsMessage", player)};
            }


        };

        spectatorMenu.open(event.getPlayer());
    }


    @EventHandler
    public void onSpectatorOptionClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player))
            return;
        if((event.getCurrentItem() == null))
            return;
        Player whoclicked = (Player) event.getWhoClicked();
        if(plugin.getGameInstanceManager().getGameInstance(whoclicked) == null)
            return;
        GameInstance gameInstance;
        if(plugin.isBungeeActivated()){
            gameInstance = plugin.getGameInstanceManager().getGameInstances().get(0);
        }else {
            gameInstance = plugin.getGameInstanceManager().getGameInstance(whoclicked);
        }
        if(!(event.getInventory().getName().contains(gameInstance.getChatManager().getMessage("SpectatorMenuName")))){

            return;
        }
        event.setCancelled(true);


        if(!(event.getCurrentItem().getType() == Material.SKULL_ITEM))
            return;
        SkullMeta skullMeta = (SkullMeta) event.getCurrentItem().getItemMeta();

        String playername = skullMeta.getOwner();

        Player p = null;
        for(Player player: gameInstance.getPlayers()){
            if(player.getName().equalsIgnoreCase(playername)){
                p = player;
                break;
            }

        }
        if(p == null) {
            if(whoclicked != null) {
                whoclicked.closeInventory();
                whoclicked.sendMessage(gameInstance.getChatManager().getMessage("SpectatorMenuPlayerNotFoundMessage"));
                return;
            }
        }

        event.getWhoClicked().teleport(p);

        whoclicked.sendMessage(gameInstance.getChatManager().getMessage("SpectatorMenuTeleportedToPlayerMessage", p));

        whoclicked.closeInventory();

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event){



        Player p = event.getPlayer();
        if (plugin.getGameInstanceManager().getGameInstance(p) == null)
            return;

        final GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(p);

        if (gameInstance.getGameState() != GameState.INGAME && gameInstance.getGameState() != GameState.ENDING && gameInstance.getGameState() != GameState.RESTARTING){
            gameInstance.teleportToLobby(p);
            return;
        }

        if (gameInstance.getGameState() != GameState.INGAME && gameInstance.getGameState() != GameState.ENDING){

            if(gameInstance.getGameState() != GameState.STARTING && gameInstance.getGameState() != GameState.WAITING_FOR_PLAYERS)
                p.teleport(gameInstance.getEndLocation());

            return;
        }
        FireMasterInstance avatarInstance = (FireMasterInstance) gameInstance;
        p.setHealth(20);
        p.setAllowFlight(true);
        p.setFlying(true);
        avatarInstance.onDeath(p);
        UserManager.getUser(p.getUniqueId()).setPower(0);
        event.setRespawnLocation(gameInstance.getStartLocation());

        p.teleport(gameInstance.getStartLocation());







    }

    @EventHandler
    public void onAddSpawn(PlayerAddSpawnCommandEvent event){
        if(plugin.getConfig().contains("spawnlocations." + event.getSpawnName())){
            event.getPlayer().sendMessage(ChatColor.RED + "This spawn already existed. This is just a warning. ");
        }
        plugin.saveLoc("instances."+ event.getArenaID() + ".spawnlocations." + event.getSpawnName(), event.getPlayer().getLocation());
        plugin.saveConfig();
        event.getPlayer().sendMessage( ChatColor.GREEN + "Spawn location " + event.getSpawnName() + " set!");

    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(plugin.getGameInstanceManager().getGameInstance(player ) == null)
            return;
        event.setCancelled(true);
        player.setFoodLevel(20);
    }

    @EventHandler
    public void onFallDamage(EntityDamageByBlockEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(plugin.getGameInstanceManager().getGameInstance(player ) == null)
            return;
        if(event.getEntity().getLocation().getY() < 1)
            return;
        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        if (event.getPlayer().getLocation().getY() > 0)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if (gameInstance.getGameState() == GameState.WAITING_FOR_PLAYERS || (gameInstance.getGameState() == GameState.STARTING && gameInstance.getTimer() > 15)) {
            event.getPlayer().teleport(gameInstance.getLobbyLocation());
            return;
        }
        if (gameInstance.getGameState() == GameState.STARTING) {
            event.getPlayer().teleport(gameInstance.getStartLocation());
            return;
        }
        if (gameInstance.getGameState() == GameState.ENDING) {
            event.getPlayer().teleport(gameInstance.getEndLocation());
            event.getPlayer().sendMessage(gameInstance.getChatManager().getMessage("TeleportToEndLocationMessage"));
            gameInstance.removePlayer(event.getPlayer());
            event.getPlayer().getInventory().clear();
            event.getPlayer().updateInventory();
            event.getPlayer().setFireTicks(0);
            UserManager.getUser(event.getPlayer().getUniqueId()).removeScoreboard();
           // if (plugin.isBarEnabled())
                //BarAPI.removeBar(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){


    }

    @EventHandler
    public void onKick(PlayerKickEvent event){
        UserManager.removeUser(event.getPlayer().getUniqueId());
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(!plugin.isDatabasActivated())
            return;
        User user = UserManager.getUser(event.getPlayer().getUniqueId());

        if(plugin.getMyDatabase().getSingle(new BasicDBObject().append("UUID", event.getPlayer().getUniqueId().toString())) == null){
            plugin.getMyDatabase().insertDocument(new String[]{"UUID", "gamesplayed", "points", "fireblast", "fireexplosion", "firelightning", "fireheal", "wins", "loses", "deaths", "second"},
                    new Object[]{event.getPlayer().getUniqueId().toString(), 0,0,0,0,0,0,0,0,0, 0});
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
            user.setInt(s, (Integer) plugin.getMyDatabase().getSingle(new BasicDBObject("UUID", event.getPlayer().getUniqueId().toString())).get(s));
        }
    }

    @EventHandler
    public void onQuitSaveStats(PlayerQuitEvent event){
        if(!plugin.isDatabasActivated()){
            return;
        }
        User user = UserManager.getUser(event.getPlayer().getUniqueId());

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
            plugin.getMyDatabase().updateDocument(new BasicDBObject("UUID", event.getPlayer().getUniqueId().toString()), new BasicDBObject(s, user.getInt(s)));

        }

        UserManager.removeUser(event.getPlayer().getUniqueId());
    }


    @EventHandler
    public void onClickEvent(InventoryClickEvent event){
        if(event.getInventory().getName().contains("Stats") || event.getInventory().getName().contains("stats"))
            event.setCancelled(true);
    }




}
