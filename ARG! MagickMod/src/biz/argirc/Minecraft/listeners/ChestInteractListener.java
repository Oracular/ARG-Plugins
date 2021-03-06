package biz.argirc.Minecraft.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import biz.argirc.Minecraft.MagickMod;
import biz.argirc.Minecraft.Functions.HelperFunctions;

public class ChestInteractListener extends PlayerListener {
	private final MagickMod	plugin;

	public ChestInteractListener(MagickMod plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {

		switch (event.getAction()) {
			case RIGHT_CLICK_BLOCK:
				Block myBlock = event.getClickedBlock();
				if (myBlock.getTypeId() == 54) {
					Player player = event.getPlayer();
					if (HelperFunctions.isAdmin(player)) {
						player.sendMessage("This is " + plugin.chestFunctions.getOwner(myBlock.getLocation()) + "s Chest");
						return;
					}

					if (plugin.chestFunctions.getOwner(myBlock.getLocation()).equalsIgnoreCase("public")) {
						player.sendMessage("-Public Chest-");
						return;
					} else if (plugin.chestFunctions.doesUserOwnChest(player.getName(), event.getClickedBlock().getLocation())) {
						player.sendMessage("-Access Granted-");
						return;
					} else {
						player.sendMessage("This chest belongs to " + plugin.chestFunctions.getOwner(myBlock.getLocation()));
						player.sendMessage("-Access Denied-");
						event.setCancelled(true);
						return;
					}
				}
			default:
				return;
		}
	}
}
