package biz.argirc.Magick.ChestProtect;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import biz.argirc.Magick.ChestProtect.commands.GetChestCountCommand;
import biz.argirc.Magick.ChestProtect.commands.UnlockChestCommand;
import biz.argirc.Magick.ChestProtect.database.ChestData;
import biz.argirc.Magick.ChestProtect.listeners.AccessListener;
import biz.argirc.Magick.ChestProtect.listeners.ChestListener;

public class ChestProtect extends JavaPlugin {

	private final AccessListener	playerListener	= new AccessListener(this);
	private final ChestListener		blockListener	= new ChestListener(this);

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable() {

		registerEvents();
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		// set up our database
		setupDatabase();
		// convertDB();
		// get our commands
		getCommand("chestcount").setExecutor(new GetChestCountCommand(this));
		getCommand("unlock").setExecutor(new UnlockChestCommand(this));

	}

	private void setupDatabase() {
		try {
			getDatabase().find(ChestData.class).findRowCount();
		} catch (PersistenceException ex) {
			System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(ChestData.class);
		return list;
	}

	public boolean doesUserOwnChest(String userstring, Location chestLocation) {

		ChestData myChest = getDatabase().find(ChestData.class).where().ieq("location", chestLocation.toString()).ieq("name", userstring).findUnique();
		if (myChest == null) {
			return false;
		}
		return true;
	}

	public boolean isPublicChest(Location chestLocation) {
		ChestData chest = getDatabase().find(ChestData.class).where().ieq("location", chestLocation.toString()).ieq("name", "public").findUnique();
		if (chest == null) {
			return false;
		}
		return true;
	}

	public ChestData getChest(Location chestLocation) {
		ChestData chest = getDatabase().find(ChestData.class).where().ieq("location", chestLocation.toString()).findUnique();
		if (chest == null) {
			return null;
		}
		return chest;
	}

	public String getOwner(Location chestLocation) {
		ChestData chest = getDatabase().find(ChestData.class).where().ieq("location", chestLocation.toString()).findUnique();
		if (chest == null) {
			return "null";
		}
		return chest.getName();
	}

	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Highest, this);

	}

	public void convertDB() {

		String maindirectory = "ARGPlugins/";
		File file = new File(maindirectory + "Chest.dat");
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		// DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);

			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));

			String dataLine;
			String username;
			String location;
			int nameStart;
			ChestData chest;
			// dis.available() returns 0 if the file does not have more lines.
			System.out.println("Starting Convert!!!");
			while (dis.ready()) {

				dataLine = dis.readLine();
				// System.out.println(dataLine);

				nameStart = dataLine.lastIndexOf('=');
				username = dataLine.substring(nameStart + 1);
				location = dataLine.substring(0, nameStart);
				System.out.println(username);
				System.out.println(location);
				chest = new ChestData();
				chest.setName(username);
				chest.setPlayerName(username);
				chest.setLocation(location);
				getDatabase().save(chest);

			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}

	}
}
