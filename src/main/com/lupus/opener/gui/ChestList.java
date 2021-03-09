package com.lupus.opener.gui;

import com.lupus.gui.Paginator;
import com.lupus.gui.utils.ItemUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.selectables.SelectableCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ChestList extends Paginator {
	static DecimalFormat df2 = new DecimalFormat("#.##");
	public ChestList(String invName,Player p) {
		super(invName);
		Collection<MinecraftCase> set =ChestManager.getAllCases();
		for (MinecraftCase mcCase : set) {
			ItemStack chest = ItemUtility.setItemTitle(
					new ItemStack(Material.CHEST),
					TextUtility.color(mcCase.getOfficialName())
			);

			var mrq = new MessageReplaceQuery().
					addQuery("price",df2.format(mcCase.getPrice())).
					addQuery("weight",mcCase.getCaseWeight()+"");

			String[] playerMessages = Message.CHEST_LIST_PLAYER_LORE.toString(mrq).split("\\n");
			List<String> lore = new ArrayList<>(Arrays.asList(playerMessages));


			if (p.hasPermission("case.admin")){
				String[] adminMessages = Message.CHEST_LIST_ADMIN_LORE.toString(mrq).split("\\n");
				lore.addAll(Arrays.asList(adminMessages));
			}
			ItemUtility.setItemLore(chest,lore);

			addItemStack(new SelectableCase(chest,mcCase,p.getUniqueId()));
		}
		setPage(0);

	}

	@Override
	public void onSlotInteraction(Player player, InventoryClickEvent e) {
		super.onSlotInteraction(player, e);
	}

	@Override
	public void onClose(Player p){
		return;
	}
}
