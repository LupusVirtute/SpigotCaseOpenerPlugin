package com.lupus.opener.gui.selectables;

import com.lupus.gui.SelectableItem;
import com.lupus.gui.utils.ItemUtility;
import com.lupus.gui.utils.SkullUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.top.GUITopCase;
import com.lupus.opener.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
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
		if (keys.size() <= 0){
			p.sendMessage(Message.CASE_HAS_0_KEYS.toString());
			return;
		}
		int limit = Math.min(keys.size(), 50);
		ItemStack[] items = new ItemStack[limit];
		for (int i = 0; i < limit; i++) {
			var entry = keys.get(i);

			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
			String nickName = offlinePlayer.getName();

			if (nickName == null)
				nickName = "Nieznany nick";
			nickName = (i+1)+"."+nickName;
			ItemStack skull = SkullUtility.getSkullFromPlayer(entry.getKey());
			ItemUtility.setItemTitle(skull,nickName);

			items[i] = skull;
		}
		ItemStack filler = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
		String[] lore = Message.SELECTABLE_TOP_FILLER_LORE.toString().split("\\n");
		ItemUtility.setItemTitleAndLore(filler,Message.SELECTABLE_TOP_FILLER_NAME.toString(), Arrays.asList(lore));

		var gui = new GUITopCase(TextUtility.color(minecraftCase.getOfficialName()),limit,filler,items);
		gui.open(p);
	}
}
