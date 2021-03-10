package com.lupus.opener.runnables;

import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ChestSave extends BukkitRunnable {
	HashMap<String, MinecraftCase> mcCases;
	public ChestSave(HashMap<String,MinecraftCase> mcCases){
		this.mcCases = mcCases;
	}
	@Override
	public void run() {
		long time = System.currentTimeMillis();
		Bukkit.broadcastMessage(Message.SAVING_INIT.toString());
		for (MinecraftCase actualCase : mcCases.values()) {
			actualCase.save();
		}
		time = (System.currentTimeMillis()	- time)/1000;
		var mrq = new MessageReplaceQuery().
				addQuery("amount",time+"");
		Bukkit.broadcastMessage(Message.SAVING_END.toString());

	}
}
