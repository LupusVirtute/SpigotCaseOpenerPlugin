package com.lupus.opener.gui;

import com.lupus.gui.GUI;
import com.lupus.gui.utils.ItemUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.selectables.SelectableCommand;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuyKeysCMD extends GUI {
	static DecimalFormat df2 = new DecimalFormat("#.##");
	public BuyKeysCMD() {
		super("Klucze",36);

		for (MinecraftCase allCase : ChestManager.getAllCases()) {
			ItemStack item = new ItemStack(Material.CHEST);
			var mrq = new MessageReplaceQuery().
					addQuery("price",df2.format(allCase.getPrice()));
			String[] messages = Message.BUY_CASE_PRICE_LORE.toString(mrq).split("\\n");

			List<String> lore = new ArrayList<>(Arrays.asList(messages));

			ItemUtility.setItemTitleAndLore(
					item,
					allCase.getOfficialName(),
					lore
			);


			this.addItemStack(new SelectableCommand(item,"kupklucz " + allCase.getName() + " 1"));
		}
	}

	@Override
	public void onClose(Player player) {

	}
}
