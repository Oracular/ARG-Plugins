package com.bukkit.Magick.ARGAntiPirate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class ARG_ThiefProtect {
	static String		maindirectory	= "argantipirate/";
	static File			ChestData		= new File(maindirectory + "Chest.dat");
	private Properties	ChestDatabase	= new Properties();

	public ARG_ThiefProtect() {
		try {
			if (!ChestData.exists()) {

				new File(maindirectory).mkdir();
				ChestData.createNewFile();
				FileInputStream chestDataFile = new FileInputStream(ChestData);
				ChestDatabase.load(chestDataFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getOwner(Location l) {
		String myLocation = l.toString();
		String owner = ChestDatabase.getProperty(myLocation);
		if (owner == null) {
			return "null";
		}
		return owner;

	}

	public boolean unlockIt(Player p) {

		Block chestToLock = p.getTargetBlock(null, 5);
		if (chestToLock.getTypeId() == 54) {
			String myOwner = getOwner(chestToLock.getLocation());
			if (myOwner.equalsIgnoreCase(p.getName())) {

				try {

					BlockFace[] faces = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
							BlockFace.WEST };
					for (BlockFace blockFace : faces) {
						Block face = chestToLock.getFace(blockFace);
						// They're placing it beside a chest
						if (face.getTypeId() == 54) {
							if (myOwner.toString().equalsIgnoreCase(p.getName())) {
								ChestDatabase.setProperty(face.getLocation().toString(), "Public");
							}
						}
					}
					p.sendMessage(ChatColor.RED + "This Chest is now PUBLIC.");
					ChestDatabase.setProperty(chestToLock.getLocation().toString(), "Public");
					ChestDatabase.store(new FileOutputStream(ChestData), null);
				} catch (IOException e) {
					p.sendMessage(ChatColor.RED + "Something went wrong!");
				}
				return true;
			} else {
				p.sendMessage(ChatColor.RED + "This is not your chest");
				return true;
			}
		} else {
			return true;
		}
	}

	public boolean removeChest(Player p, Block chestLocation) {
		try {
			String myOwner = ARGAntiPirate.chestMachine.getOwner(chestLocation.getLocation());
			if (myOwner.equals(p.getName()) || myOwner.equals("Public") || myOwner.equals("null")) {
				ChestDatabase.remove(chestLocation.getLocation().toString());
				ChestDatabase.store(new FileOutputStream(ChestData), null);
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			p.sendMessage(ChatColor.RED + "Something went wrong");
			return false;
		}

	}

	public boolean openChest(Player player, Block targetChest) {

		String myOwner = ARGAntiPirate.chestMachine.getOwner(targetChest.getLocation());
		if (myOwner.equals(player.getName()) || ARGAntiPirate.rankMachine.getRank(player) > 4){
			player.sendMessage("Owner:" + myOwner);
			return true;
		} else if(myOwner.equals("null") || myOwner.equalsIgnoreCase("Public")){
			player.sendMessage("This is a Public Chest");
			return true;
		}
		return false;
	}

	public boolean lockIt(Player p, Block placedBlock) {
		try {

			BlockFace[] faces = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
			for (BlockFace blockFace : faces) {
				Block face = placedBlock.getFace(blockFace);
				if (face.getTypeId() == 54) {
					String myOwner = ChestDatabase.getProperty(face.getLocation().toString());
					if (myOwner.equalsIgnoreCase("Public")) {
						p.sendMessage(ChatColor.GOLD + "You have expanded a public chest.");
						ChestDatabase.setProperty(placedBlock.getLocation().toString(), "Public");
						ChestDatabase.store(new FileOutputStream(ChestData), null);
						return true;
					} else if (!myOwner.equalsIgnoreCase(p.getName())) {
						p.sendMessage(ChatColor.RED + "You do not have permission to place a chest here");
						return false;
					}
				}
			}
			p.sendMessage(ChatColor.GOLD + "You are now the owner of this chest");
			ChestDatabase.setProperty(placedBlock.getLocation().toString(), p.getName());
			ChestDatabase.store(new FileOutputStream(ChestData), null);
			return true;
		} catch (IOException e) {
			p.sendMessage(ChatColor.RED + "Something went wrong!");
			return false;
		}
	}
}