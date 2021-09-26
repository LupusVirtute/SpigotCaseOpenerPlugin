package com.lupus.opener.gui;

import com.lupus.gui.Paginator;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.selectables.SelectableCase;
import com.lupus.opener.managers.ChestManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Collection;

public class ChestList extends Paginator {
	static DecimalFormat df2 = new DecimalFormat("#.##");
	public ChestList(String invName,Player p) {
		super(invName);
		Collection<MinecraftCase> set =ChestManager.getAllCases();
		for (MinecraftCase mcCase : set) {
			ItemStack chest = mcCase.getItemRepresentation(p);
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
