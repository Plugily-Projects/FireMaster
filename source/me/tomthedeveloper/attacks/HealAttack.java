package me.tomthedeveloper.attacks;

import me.TomTheDeveloper.Attacks.PlayerAttack;
import me.TomTheDeveloper.Utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Tom on 25/08/2014.
 */
public class HealAttack extends PlayerAttack {


	private double x,y,z, radius, angle;
	private int i;


	public HealAttack( Player player) {
		super(1, player);
		plugin.getAttackManager().registerAttack(this);
		getAttacker().setWalkSpeed(-0.15F);
		y = 200*0.02;
		setCounter(0);
	}

	@Override
	public void run() {

		if(i > 200){
			plugin.getAttackManager().unregisterAttack(this);
			getAttacker().setWalkSpeed(0.2F);
			return;
		}
		radius = 1.5F;
		angle = 2 * Math.PI * i / 80;
		x = Math.cos(angle) * radius;
		z = Math.sin(angle) * radius;

		Location particlelocation = getAttacker().getEyeLocation();
		particlelocation.add(x, y, z);
		ParticleEffect.LAVA.display(0, 0, 0, 1, 1,particlelocation,100);
		ParticleEffect.VILLAGER_HAPPY.display( 0,0,0,1, 1,particlelocation,100);
		ParticleEffect.VILLAGER_HAPPY.display( 0,0,0,1, 1,particlelocation.clone().add(-2*x, 0, -2*z),100);
		ParticleEffect.DRIP_LAVA.display( 0, 0, 0, 1, 1,getAttacker().getEyeLocation().add(0, y, 0),100);
		i++;

		if(i%5 == 0 && i >100){
			if(getAttacker().getHealth() != getAttacker().getMaxHealth()) {
				getAttacker().setHealth(getAttacker().getHealth() + 1);
				if(i%10 == 0){
					getAttacker().damage(0.0);
				}
			}
			if(getAttacker().getMaxHealth() == getAttacker().getHealth()) {
				plugin.getAttackManager().unregisterAttack(this);
				getAttacker().setWalkSpeed(0.2F);
				ParticleEffect.FLAME.display( 1,1,1,1,150,getAttacker().getEyeLocation(),100);
			}


		}



	}
}
