package com.lupus.opener.gui.selectables;

import com.lupus.gui.SelectableItem;
import com.lupus.opener.chests.MinecraftCase;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SelectableCase extends SelectableItem {
	UUID playerUUID;
	MinecraftCase mcCase;
	public SelectableCase(ItemStack item, MinecraftCase mcCase, UUID player) {
		super(true, item);
		this.mcCase = mcCase;
		this.playerUUID = player;
	}

	@Override
	protected void execute(Object... args) {
		mcCase.openCaseEditor(Bukkit.getPlayer(playerUUID));
	}
}
