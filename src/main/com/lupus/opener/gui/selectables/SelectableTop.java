package com.lupus.opener.gui.selectables;

import com.lupus.command.LupusCommandFrameWork;
import com.lupus.gui.SelectableItem;
import com.lupus.gui.utils.ItemUtility;
import com.lupus.gui.utils.SkullUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.top.GUITopCase;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
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
		if (keys == null){
			p.sendMessage(Message.CASE_IS_CALCULATING_TOP.toString());
			return;
		}
		if (keys.size() <= 0){
			p.sendMessage(Message.CASE_HAS_0_KEYS.toString());
			return;
		}
		int limit = Math.min(keys.size(), 50)+1;
		ItemStack[] items = new ItemStack[limit];
		for (int i = 1; i < limit; i++) {
			var entry = keys.get(i-1);

			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
			String nickName = offlinePlayer.getName();

			if (nickName == null)
				nickName = Message.UNKNOWN_NICKNAME.toString();
			var mrq = new MessageReplaceQuery().
					addQuery("place",i+"").
					addQuery("name",nickName);
			nickName = Message.PLAYER_PLACE_TEMPLATE.toString(mrq);
			ItemStack skull = SkullUtility.getSkullFromPlayer(entry.getKey());
			ItemUtility.setItemTitle(skull,nickName);
			mrq = new MessageReplaceQuery().
					addQuery("amount",entry.getValue() + "");
			ItemUtility.setItemLore(skull,new String[]{Message.PLAYER_CASE_KEY_FORMAT.toString(mrq)});
			if(skull == null)
				continue;

			items[i-1] = skull;
		}
		ItemStack filler = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
		String[] lore = Message.SELECTABLE_TOP_FILLER_LORE.toString().split("\\n");
		ItemUtility.setItemTitleAndLore(filler,Message.SELECTABLE_TOP_FILLER_NAME.toString(), Arrays.asList(lore));

		var gui = new GUITopCase(TextUtility.color(minecraftCase.getOfficialName()),limit,filler,items);
		gui.open(p);
	}
}
