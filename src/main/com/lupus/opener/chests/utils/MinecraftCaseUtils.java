package com.lupus.opener.chests.utils;

import com.lupus.gui.utils.NBTUtility;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MinecraftCaseUtils {
	public static MinecraftCase getKeyRedeemCase(ItemStack itemStack){
		return getMaterialCaseNBT(itemStack,Material.TRIPWIRE_HOOK,"CaseKey");
	}
	private static MinecraftCase getMaterialCaseNBT(ItemStack itemStack, Material identifier,String nbtKey){
		if (itemStack == null)
			return null;
		if (itemStack.getType() != identifier){
			return null;
		}
		String mcCase = NBTUtility.getNBTValue(itemStack,nbtKey,String.class);
		if (mcCase == null){
			return null;
		}
		return ChestManager.getCase(mcCase);
	}

	public static MinecraftCase getCobblex(ItemStack itemStack){
		return getMaterialCaseNBT(itemStack,Material.MOSSY_COBBLESTONE,"Cobblex");
	}
	public static int sortCompare(Map.Entry<UUID, Integer> o1, Map.Entry<UUID, Integer> o2) {
		return ((Comparable<Integer>) o2.getValue()).compareTo(o1.getValue());
	}
}
