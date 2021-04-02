package com.lupus.opener.managers;


import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.chests.MinecraftKey;
import com.lupus.opener.runnables.ChestSave;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public final class ChestManager {
	private static final HashMap<String, MinecraftCase> mcCases = new HashMap<>();
	private static final HashMap<Location,String> mcCaseLocation = new HashMap<>();

	public static void clear(){
		mcCaseLocation.clear();
		mcCases.clear();
	}
	static int highestWeight = 0;
	public static void addCase(@NotNull MinecraftCase mcCase){
		if (mcCase.getCaseWeight() > highestWeight){
			highestWeight = mcCase.getCaseWeight();
		}
		mcCases.put(mcCase.getName().toLowerCase(),mcCase);
	}
	public static void addCaseLocation(Location loc,String name){
		mcCaseLocation.put(loc,name.toLowerCase());
	}
	public static MinecraftCase getCase(String name){
		return mcCases.get(name.toLowerCase());
	}
	public static boolean contains(String chest){
		return mcCases.containsKey(chest.toLowerCase());
	}
	public static MinecraftKey[] getKeysForPlayer(Player p){
		Collection<MinecraftCase> c = mcCases.values();
		MinecraftKey[] keys = new MinecraftKey[c.size()];
		int i=0;
		for (MinecraftCase minecraftCase : c) {
			keys[i] = new MinecraftKey(minecraftCase.getName(),minecraftCase.getKeyAmount(p));
			i++;
		}
		return keys;
	}
	public static MinecraftCase getCaseFromLocation(Location location){
		return mcCases.get(mcCaseLocation.get(location));
	}
	public static boolean removeCaseLocation(Block block){
		return removeCaseLocation(block.getLocation());
	}
	public static boolean removeCaseLocation(Location loc){
		String r = mcCaseLocation.remove(loc);
		return r != null;
	}
	public static Set<String> getAll(){
		return mcCases.keySet();
	}
	public static Collection<MinecraftCase> getAllCases() { return mcCases.values();}
	public static void saveAll(boolean async){
		if (async) {
			new ChestSave(mcCases).runTaskAsynchronously(CaseOpener.getMainPlugin());
		}
		else{
			new ChestSave(mcCases).run();
		}
	}
}
