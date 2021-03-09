package com.lupus.opener.gui;

import com.lupus.gui.GUI;
import com.lupus.gui.utils.ItemUtility;
import com.lupus.gui.utils.SkullUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.gui.utils.nbt.InventoryUtility;
import com.lupus.opener.chests.CaseItem;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.processing.Messager;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ItemEditor extends GUI {
	public static final int MAX_VALUE = 1000000000;
	int value = 1;
	int weightMax;
	int index;
	MinecraftCase mcCase;
	ItemStack itemEdited;
	ItemStack info = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
	public ItemEditor(String invName,
					  int index,
					  MinecraftCase caseEdited) {
		super(invName, 27);
		this.index = index;

		mcCase = caseEdited;

		CaseItem itemAt = mcCase.getDropTable().getItemAt(index);
		this.weightMax = mcCase.getDropTable().getMaxWeight();
		int itemWeight = 0;
		value = 1;
		if (itemAt != null) {
			this.itemEdited = itemAt.getItem();
			itemWeight = itemAt.getWeight();
			if (itemWeight > 0 && itemWeight < MAX_VALUE) {
				value = itemWeight;
			}
		}

		SkullUtility.intToSkullConverter(inv,value,0,8);
		ItemMeta meta;
		ItemStack accept = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		meta = accept.getItemMeta();
		meta.setDisplayName(Message.ITEM_EDITOR_ACCEPT_NAME.toString());
		accept.setItemMeta(meta);
		inv.setItem(23,accept);

		ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		meta = cancel.getItemMeta();
		meta.setDisplayName(Message.ITEM_EDITOR_DENY_NAME.toString());
		accept.setItemMeta(meta);
		inv.setItem(21,cancel);

		inv.setItem(22,itemEdited);

		meta = info.getItemMeta();
		meta.setDisplayName(Message.ITEM_EDITOR_INFO_NAME.toString());
		updateInfoMeta();
		info.setItemMeta(meta);
		inv.setItem(13,info);

	}
	static DecimalFormat format = new DecimalFormat("#.#####");
	public void updateInfoMeta(){
		float chance = (float)value/ (float)weightMax;
		chance *= 100;
		var mrq = new MessageReplaceQuery().
				addQuery("chance",format.format(chance)).
				addQuery("chest", mcCase.getOfficialName());

		String[] messages = Message.ITEM_EDITOR_INFO_LORE.toString(mrq).split("\\n");

		ItemUtility.setItemLore(info,messages);

		inv.setItem(13,info);
	}

	@Override
	public void onClickedItemNull(Player player, InventoryClickEvent e) {
		click(player, e);
	}

	@Override
	public void click(Player player, InventoryClickEvent e) {
		if (e.getRawSlot() >= getInventory().getSize()) {
			e.setCancelled(false);
		}
		ItemStack clickedItem = e.getCurrentItem();
		int clickedSlot = e.getRawSlot();
		switch (clickedSlot){
			case 21:{
				mcCase.openCaseEditor(player);
				return;
			}
			case 22:{
				if(e.getClick().isRightClick()){
					if (clickedItem == null)
						return;
					InventoryUtility.addItemStackToPlayerInventory(player,e.getCurrentItem());
				}
				if (e.getClick().isLeftClick() && e.getCursor() != null){
					inv.setItem(22,new ItemStack(e.getCursor()));
					e.getView().setCursor(new ItemStack(Material.AIR));
				}
				return;
			}
			case 23:{
				updateCase();
				mcCase.openCaseEditor(player);
				return;
			}

		}
		if (clickedItem == null)
			return;
		if (SkullUtility.isThisItemNumberSkull(new ItemStack(clickedItem))) {
			double pow = Math.pow(10, Math.abs(clickedSlot % 9 - 8));
			if (e.getClick().isLeftClick()) {
				addValue((int) pow);
			}
			else if (e.getClick().isRightClick()){
				addValue((int)-pow);
			}
			return;
		}
		return;
	}
	public void addValue(int valueAmount){
		value += valueAmount;
		if (value >= MAX_VALUE)
			value = 1;
		else if(value <= 0) {
			value = 1;
		}
		SkullUtility.intToSkullConverter(inv,value,0,8);
		updateInfoMeta();
	}
	@Override
	public void onClose(Player p){
	}
	public void updateCase(){
		ItemStack item = inv.getItem(22);
		if (item == null)
			return;

		mcCase.setItemAt(new CaseItem(new ItemStack(item),value),index);
	}
}
