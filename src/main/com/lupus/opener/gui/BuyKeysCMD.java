package com.lupus.opener.gui;

import com.lupus.gui.GUI;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.selectables.SelectableCommand;
import com.lupus.opener.managers.ChestManager;
import com.lupus.utils.ColorUtil;
import com.lupus.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class BuyKeysCMD extends GUI {
	static DecimalFormat df2 = new DecimalFormat("#.##");
	public BuyKeysCMD() {
		super("Klucze",36);

		for (MinecraftCase allCase : ChestManager.getAllCases()) {
			ItemStack item = new ItemStack(Material.CHEST);
			ItemStackUtil.setItemTitle(item,allCase.getOfficialName());
			ItemStackUtil.setItemLore(item, new String[]{
					ColorUtil.text2Color("&cCena: &6" + df2.format(allCase.getPrice()))
			});
			this.addItemStack(new SelectableCommand(item,"kupklucz " + allCase.getName() + " 1"));
		}
	}

	@Override
	public void onClose(Player player) {

	}
}
