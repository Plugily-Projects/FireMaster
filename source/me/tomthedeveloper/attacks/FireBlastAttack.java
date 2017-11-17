package me.tomthedeveloper.attacks;

import java.util.Queue;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.TomTheDeveloper.User;
import me.TomTheDeveloper.Attacks.PlayerShootAttack;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.Utils.ParticleEffect;
import me.TomTheDeveloper.Utils.Util;

/**
 * Created by Tom on 30/07/2014.
 */
public class FireBlastAttack extends PlayerShootAttack {


	private  Queue<Block> blocks = null;
	private Block block;


	public FireBlastAttack( Player player) {
		super(1,player );
		blocks = Util.getLineOfSight(getAttacker(), null, 40, 40);
		plugin.getAttackManager().registerAttack(this);
		setCounter(0);
	}


	@Override
	public Location getLocation(){
		return block.getLocation();
	}

	@Override
	public void run() {
		block = blocks.poll();
		if(block == null){
			plugin.getAttackManager().unregisterAttack(this);
			return;
		}



		if(getCounter() == 0 || getCounter() != 1 ){

			// ParticleEffect.WAKE.display(block.getLocation(), 0.001F,0.001F,0.001F,2F,20);
			//ParticleEffect.displayBlockCrack(block.getLocation(),51, (byte) 1, 0.1F,0.1F,0.1F,40);
			ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.FIRE, (byte)0),0.1F,0.1F,0.1F,1,40,block.getLocation(), 100);

		}

		if((!blocks.isEmpty()) && getCounter() != 0 || getCounter() != 1){


			if(block.getType() != Material.AIR)
				return;
			// ParticleEffect.displayBlockCrack(block.getLocation(),51, (byte) 1, 0.1F,0.1F,0.1F,40);

			ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.FIRE, (byte)0),0.1F,0.1F,0.1F,1,40,block.getLocation(), 100);

			//test

			for(Entity entity: Util.getNearbyEntities(block.getLocation(), 2)){
				if(entity instanceof Player){

					Player player = (Player) entity;
					if((player.getUniqueId() == getAttacker().getUniqueId()) || UserManager.getUser(player.getUniqueId()).isSpectator())
						continue;
				}
				Vector vector = getAttacker().getLocation().getDirection();
				entity.setFireTicks(100);
				entity.setVelocity(entity.getVelocity().add(vector.clone().multiply(1.2).add(new Vector(0,0.3,0))));
				if(entity instanceof Player){
					Player player = (Player) entity;
					User user = UserManager.getUser(player.getUniqueId());
					user.setLastHitted(getAttacker());
				}

			}
		}






		setCounter(getCounter() +1);




	}
}
