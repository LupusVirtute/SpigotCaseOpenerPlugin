package com.lupus.opener.managers;

import com.lupus.opener.gui.OpeningCase;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class OpenerManager {
	private static HashMap<UUID, OpeningCase> playerOpener = new HashMap<>();
	public static OpeningCase getPlayerOpeningCase(UUID p){
		return playerOpener.get(p);
	}
	public static OpeningCase getPlayerOpeningCase(Player p){
		return getPlayerOpeningCase(p.getUniqueId());
	}
	public static void setPlayerOpener(Player p,OpeningCase mcCase){
		setPlayerOpener(p.getUniqueId(),mcCase);
	}
	public static void removePlayerOpener(Player p){
		playerOpener.remove(p.getUniqueId());
	}
	public static void setPlayerOpener(UUID p,OpeningCase mcCase){
		playerOpener.put(p,mcCase);
	}
}
