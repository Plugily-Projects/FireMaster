package me.tomthedeveloper.attacks;

import java.util.Queue;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.TomTheDeveloper.User;
import me.TomTheDeveloper.Attacks.PlayerShootAttack;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.Utils.ParticleEffect;
import me.TomTheDeveloper.Utils.Util;

/**
 * Created by Tom on 23/08/2014.
 */
public class FireExlpodeAttack extends PlayerShootAttack {
	private Location location = null;
	private Queue<Block> blocks;
	private Block block;

	public FireExlpodeAttack(Player player) {
		super(1, player);
		blocks = Util.getLineOfSight(player, null, 40, 40);
		plugin.getAttackManager().registerAttack(this);
		setCounter(0);

	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void run() {
		block = blocks.poll();
		if(block == null){

			plugin.getAttackManager().unregisterAttack(this);
			return;
		}
		location = block.getLocation();

		if(blocks.size() <=0){
			block.getLocation().getWorld().createExplosion(block.getLocation(), 3F);
			for(Entity entity1:Util.getNearbyEntities(block.getLocation(),20)){
				if(entity1.getType() == EntityType.DROPPED_ITEM)
					entity1.remove();
			}
			return;
		}

		ParticleEffect.LAVA.display(0,0,0,1,10,block.getLocation(),100);

		for(Entity entity: Util.getNearbyEntities(block.getLocation(), 2)){
			if(entity instanceof Player){
				Player player = (Player) entity;
				if((player.getUniqueId() == getAttacker().getUniqueId()) || UserManager.getUser(player.getUniqueId()).isSpectator())
					continue;
			}
			entity.getWorld().createExplosion(entity.getLocation(), 3F); //normal 3F (Erikas:7F
			for(Entity entity1:Util.getNearbyEntities(entity.getLocation(),10)){
				if(entity1.getType() == EntityType.DROPPED_ITEM)
					entity1.remove();
			}
			if(entity instanceof Player){
				Player player = (Player) entity;
				User user = UserManager.getUser(player.getUniqueId());
				user.setLastHitted(getAttacker());
			}
			blocks.clear();

		}

	}
}
