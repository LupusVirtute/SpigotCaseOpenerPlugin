package com.lupus.opener.gui;

import com.lupus.gui.Paginator;
import com.lupus.gui.SelectableItem;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.selectables.SelectableCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.utils.ColorUtil;
import com.lupus.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ChestList extends Paginator {
	public ChestList(String invName,Player p) {
		super(invName);
		Collection<MinecraftCase> set =ChestManager.getAllCases();
		for (MinecraftCase mcCase : set) {
			ItemStack chest = ItemStackUtil.setItemTitle(
					new ItemStack(Material.CHEST),
					ColorUtil.text2Color(mcCase.getOfficialName())
			);
			ItemStackUtil.setItemLore(chest,
					new String[]{
							ColorUtil.text2Color("&cCena : &6" + mcCase.getPrice()+ "$"),
							ColorUtil.text2Color("&cWażność : &3" + mcCase.getCaseWeight()),
					}
			);
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
