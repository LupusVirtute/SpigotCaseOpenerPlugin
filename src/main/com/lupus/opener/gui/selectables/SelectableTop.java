package com.lupus.opener.gui.selectables;

import com.lupus.gui.SelectableItem;
import com.lupus.gui.utils.ItemUtility;
import com.lupus.gui.utils.SkullUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.top.GUITopCase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SelectableTop extends SelectableItem {
	UUID player;
	MinecraftCase minecraftCase;
	public SelectableTop(ItemStack item, MinecraftCase minecraftCase, UUID player) {
		super(true, item);
		this.player = player;
		this.minecraftCase = minecraftCase;
	}

	@Override
	protected void execute(Object... objects) {
		Player p = Bukkit.getPlayer(player);
		if (p == null){
			return;
		}
		List<Map.Entry<UUID,Integer>> keys =  minecraftCase.getTopKeys();
		int limit = Math.min(keys.size(), 50);
		ItemStack[] items = new ItemStack[limit];
		for (int i = 0; i < limit; i++) {
			var entry = keys.get(i);

			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
			String nickName = offlinePlayer.getName();

			if (nickName == null)
				nickName = "Nieznany nick";
			ItemStack skull = SkullUtility.getSkullFromPlayer(entry.getKey());
			ItemUtility.setItemTitle(skull,nickName);

			items[i] = skull;
		}
		var gui = new GUITopCase(TextUtility.color(minecraftCase.getOfficialName()),limit,items);
		gui.open(p);
	}
}
