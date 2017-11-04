package me.TomTheDeveloper.attacks;

import me.TomTheDeveloper.Attacks.PlayerAttack;
import me.TomTheDeveloper.Utils.ParticleEffect;
import me.TomTheDeveloper.Utils.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;
import java.util.Set;

/**
 * Created by Tom on 25/08/2014.
 */
public class LightningStrikeAttack extends PlayerAttack {

    private Random random;
    private Location strikeloc;
    private int i = 0;


    public LightningStrikeAttack(Player player) {
        super(2, player);
        setCounter(0);
        random = new Random();
        strikeloc = player.getTargetBlock((Set<Material>) null, 50).getLocation();
        plugin.getAttackManager().registerAttack(this);

    }

    @Override
    public void run() {
        if(i >=0 && i <=20){
            getAttacker().setWalkSpeed(-0.15F);

            for(int i = 0; i>=5; i++){
                Location particleloc = getAttacker().getLocation().add(random.nextInt(3) -1, random.nextInt(3) -1, random.nextInt(3) -1);
                ParticleEffect.FLAME.display(0.3F,0.3F,0.3F,1F,20,particleloc, 100);

            }
            ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.FIRE, (byte)0),0.2F,0.2F,0.2F,1,51,strikeloc.clone().add(0,2,0),100);

        }

        if(i == 21){
            getAttacker().setWalkSpeed(0.2F);
            getAttacker().getWorld().strikeLightning(strikeloc);
            getAttacker().getWorld().createExplosion(strikeloc, 6F); //normal 6F (Erikas:9F)
            for(Entity entity1:Util.getNearbyEntities(strikeloc,5)){
                if(entity1.getType() == EntityType.DROPPED_ITEM)
                    entity1.remove();
            }
            plugin.getAttackManager().unregisterAttack(this);
        }

        i++;
    }
}
