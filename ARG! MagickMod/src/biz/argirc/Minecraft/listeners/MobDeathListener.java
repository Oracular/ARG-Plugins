package biz.argirc.Minecraft.listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

import biz.argirc.Minecraft.MagickMod;

public class MobDeathListener extends EntityListener {
	private final MagickMod	plugin;

	public MobDeathListener(MagickMod plugin) {
		this.plugin = plugin;
	}

	public ArrayList<String>	lastDamagePlayer	= new ArrayList<String>();
	public ArrayList<String>	lastDamageType		= new ArrayList<String>();
	public String				beforedamage		= "";
	public String				damageType			= "";

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Monster) {
			Monster monster = (Monster) event.getEntity();
			if (monster.getTarget() instanceof Player) {
				lastDamageDone(monster, event);
			}
		}
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		beforedamage = "";
		damageType = "";
		Entity myEntity = event.getEntity();
		try {
			if (myEntity instanceof Monster) {
				Monster monster = (Monster) myEntity;
				if (monster.getTarget() instanceof Player) {
					Player player = (Player) monster.getTarget();

					if (plugin.bankFunctions.hasAccount(player.getName()) == true) {
						String myAttacker = lastDamageType.get(lastDamagePlayer.indexOf(monster.toString()));
						if (myAttacker == player.getName()) {
							// plugin.pluginSettings.loadMobValues();
							int mobValue = 0;
							if (monster instanceof Creeper) {
								mobValue = plugin.pluginSettings.creeperValue;
							} else if (monster instanceof Zombie) {
								mobValue = plugin.pluginSettings.zombieValue;
							} else if (monster instanceof Spider) {
								mobValue = plugin.pluginSettings.spiderValue;
							} else if (monster instanceof Skeleton) {
								mobValue = plugin.pluginSettings.skelValue;
							}
							ItemStack myItem = player.getItemInHand();
							if (myItem.getTypeId() == 0) {
								player.sendMessage("BONUS " + plugin.pluginSettings.multi + "X for kill with 'BEAR' HANDS");
								mobValue = mobValue * plugin.pluginSettings.multi;
							}
							int newbalance = plugin.bankFunctions.getBalance(player.getName()) + mobValue;
							plugin.bankFunctions.setBalance(player.getName(), newbalance);
							player.sendMessage("Total " + ChatColor.GOLD + mobValue + ChatColor.WHITE + "  " + plugin.pluginSettings.credit + " for Kill");
							player.sendMessage("Total: " + newbalance);
						}
					}
				}
			} else {
				// if myEntity is not a player or a monster
				// it is most likely a friendly mob
				// add any code for friendly mob here
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void lastDamageDone(Monster monster, EntityDamageEvent event) {
		String lastdamage = event.getCause().name();
		Entity attacker;
		if (event instanceof EntityDamageByProjectileEvent || event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent mobevent = (EntityDamageByEntityEvent) event;
			attacker = mobevent.getDamager();
			if (attacker instanceof Player) {
				Player p = (Player) attacker;
				lastdamage = p.getName();
			}
		}
		if (!lastDamagePlayer.contains(monster.toString())) {
			lastDamagePlayer.add(monster.toString());
			lastDamageType.add(event.getCause().name());
		} else {
			lastDamageType.set(lastDamagePlayer.indexOf(monster.toString()), lastdamage);
		}
		beforedamage = lastdamage;
		damageType = event.getCause().name();
	}

}
