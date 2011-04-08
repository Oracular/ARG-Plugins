package biz.argirc.Minecraft;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import biz.argirc.Minecraft.commands.ChestHelpCommand;
import biz.argirc.Minecraft.commands.GetChestCountCommand;
import biz.argirc.Minecraft.commands.KillFarmAnimalsCommand;
import biz.argirc.Minecraft.commands.KillHostileMobsCommand;
import biz.argirc.Minecraft.commands.ListCommand;
import biz.argirc.Minecraft.commands.SetCompassCommand;
import biz.argirc.Minecraft.commands.SetRankCommand;
import biz.argirc.Minecraft.commands.SetSpawnLocationCommand;
import biz.argirc.Minecraft.commands.SpawnMobCommand;
import biz.argirc.Minecraft.commands.TeleportCommand;
import biz.argirc.Minecraft.commands.UnlockChestCommand;
import biz.argirc.Minecraft.database.ChestData;
import biz.argirc.Minecraft.database.RankData;
import biz.argirc.Minecraft.listeners.ChestBlockListener;
import biz.argirc.Minecraft.listeners.ChestInteractListener;
import biz.argirc.Minecraft.listeners.OnJoinListener;
import biz.argirc.Minecraft.listeners.WorldProtectListener;

public class MagickMod extends JavaPlugin {
	public final ChestFunctions			chestFunctions			= new ChestFunctions(this);
	public final RankFunctions			rankFunctions			= new RankFunctions(this);
	private final ChestInteractListener	chestInteractListener	= new ChestInteractListener(this);
	private final ChestBlockListener	chestBlockListener		= new ChestBlockListener(this);
	private final OnJoinListener		onJoinListener			= new OnJoinListener(this);
	private final WorldProtectListener	worldProtectListener	= new WorldProtectListener(this);

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {

		setupDatabase();
		registerEvents();
		getCommands();
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}

	public void getCommands() {

		getCommand("setcompass").setExecutor(new SetCompassCommand());
		getCommand("setserverspawn").setExecutor(new SetSpawnLocationCommand());
		getCommand("killfriendly").setExecutor(new KillFarmAnimalsCommand());
		getCommand("killhostile").setExecutor(new KillHostileMobsCommand());
		getCommand("spawnmob").setExecutor(new SpawnMobCommand());
		getCommand("list").setExecutor(new ListCommand(this.getServer()));
		getCommand("teleport").setExecutor(new TeleportCommand());
		getCommand("setrank").setExecutor(new SetRankCommand(rankFunctions));
		getCommand("chesthelp").setExecutor(new ChestHelpCommand());
		getCommand("chestcount").setExecutor(new GetChestCountCommand(chestFunctions));
		getCommand("unlock").setExecutor(new UnlockChestCommand(chestFunctions));

	}

	public void setupDatabase() {
		try {
			getDatabase().find(ChestData.class).findRowCount();
		} catch (PersistenceException ex) {
			System.out.println("Initializing database for " + getDescription().getName() + " chest protection");
			installDDL();
		}
		try {
			getDatabase().find(RankData.class).findRowCount();
		} catch (PersistenceException ex) {
			System.out.println("Initializing database for " + getDescription().getName() + " rank system");
			installDDL();
		}
	}

	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		// Block events
		pm.registerEvent(Event.Type.BLOCK_CANBUILD, worldProtectListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, chestBlockListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, chestBlockListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, worldProtectListener, Priority.Lowest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, worldProtectListener, Priority.Lowest, this);
		// Player Events
		pm.registerEvent(Event.Type.PLAYER_JOIN, onJoinListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, chestInteractListener, Priority.Normal, this);

	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(ChestData.class);
		list.add(RankData.class);
		return list;
	}

	public Player getPlayer(String player) {
		List<Player> players = this.getServer().matchPlayer(player);
		if (players.isEmpty()) {
			return null;
		} else {
			return players.get(0);
		}
	}

}