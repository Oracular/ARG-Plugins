package biz.argirc.Minecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import biz.argirc.Minecraft.ArenaFunctions;
import biz.argirc.Minecraft.HelperFunctions;
import biz.argirc.Minecraft.MagickMod;

public class RestoreInventoryCommand implements CommandExecutor {
	private final MagickMod	plugin;

	public RestoreInventoryCommand(MagickMod plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!HelperFunctions.isAdmin((Player) sender)) {
			sender.sendMessage("You are not an admin.");
			return true;
		}
		Player player = Bukkit.getServer().getPlayer(args[0]);
		ArenaFunctions.restoreInventory(player);

		return true;
	}

}