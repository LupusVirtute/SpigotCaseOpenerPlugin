package com.lupus.opener.runnables;

import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.messages.GeneralMessages;
import com.lupus.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ASyncSave extends BukkitRunnable {
	HashMap<String, MinecraftCase> mcCases;
	public ASyncSave(HashMap<String,MinecraftCase> mcCases){
		this.mcCases = mcCases;
	}
	@Override
	public void run() {
		long time = System.currentTimeMillis();
		Bukkit.broadcastMessage(ColorUtil.text2Color(GeneralMessages.LOGO.toString() + " Zapisywanie skrzyn moze wystapic lag"));
		for (MinecraftCase actualCase : mcCases.values()) {
			actualCase.save();
		}
		Bukkit.broadcastMessage(ColorUtil.text2Color(GeneralMessages.LOGO.toString() + " &aPoprawnie zapisano skrzynie czas: &6 " + (System.currentTimeMillis()	- time)/1000));

	}
}
