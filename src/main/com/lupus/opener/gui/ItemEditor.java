package com.lupus.opener.gui;

import com.lupus.gui.GUI;
import com.lupus.opener.chests.CaseItem;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.utils.ColorUtil;
import com.lupus.utils.PlayerRelated;
import com.lupus.utils.Skulls;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

		Skulls.intToSkullConverter(inv,value,0,8);
		ItemMeta meta;
		ItemStack accept = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		meta = accept.getItemMeta();
		meta.setDisplayName(ColorUtil.text2Color("&a&lAkceptuj Zmiany"));
		accept.setItemMeta(meta);
		inv.setItem(23,accept);

		ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		meta = cancel.getItemMeta();
		meta.setDisplayName(ColorUtil.text2Color("&4&lAnuluj"));
		accept.setItemMeta(meta);
		inv.setItem(21,cancel);

		inv.setItem(22,itemEdited);

		meta = info.getItemMeta();
		meta.setDisplayName(ColorUtil.text2Color("&9&lInformacje!"));
		updateInfoMeta();
		info.setItemMeta(meta);
		inv.setItem(13,info);

	}
	public void updateInfoMeta(){
		ItemMeta meta = info.getItemMeta();
		float chance = (((float)value/((float)weightMax))*100);
		meta.setLore(Arrays.asList(
				ColorUtil.text2Color("&6&lSzansa &7&l: &4&l " + chance),
				ColorUtil.text2Color("&6&lW skrzyni &7&l: &4&l"+ mcCase.getOfficialName())
				)
		);
		info.setItemMeta(meta);
		inv.setItem(13,info);
	}

	@Override
	public void onClickedItemNull(Player player, InventoryClickEvent e) {
		click(player, e);
	}

	@Override
	public void click(Player player, InventoryClickEvent e) {
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
					PlayerRelated.addItemToPlayerInventory(player,e.getCurrentItem());
				}
				if (e.getClick().isLeftClick() && e.getCursor() != null){
					inv.setItem(22,e.getCursor());
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
		if (Skulls.isThisItemANumberSkull(new ItemStack(clickedItem))) {
			double pow = Math.pow(10, Math.abs(clickedSlot % 9 - 8));
			System.out.println("Pow:"+pow);
			if (e.getClick().isLeftClick()) {
				addValue((int) pow);
			}
			else if (e.getClick().isRightClick()){
				addValue((int)-pow);
			}
			return;
		}
		e.setCancelled(false);
		return;
	}
	public void addValue(int valueAmount){
		value += valueAmount;
		System.out.println("Value:"+value);
		if (value >= MAX_VALUE)
			value = 1;
		else if(value <= 0) {
			value = 1;
		}
		Skulls.intToSkullConverter(inv,value,0,8);
		updateInfoMeta();
	}
	@Override
	public void onClose(Player p){
		return;
	}
	public void updateCase(){
		mcCase.setItemAt(new CaseItem(inv.getItem(22).clone(),value),index);
	}
}
