package com.lupus.opener.gui;

import com.lupus.gui.Paginator;
import com.lupus.opener.chests.CaseItem;
import com.lupus.opener.chests.CaseItemHolder;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.selectables.SelectableItemEditor;
import com.lupus.utils.ColorUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CaseItemList extends Paginator {
	CaseItemHolder dropItems;
	MinecraftCase mcCase;
	public CaseItemList(MinecraftCase minecraftCase,Player player){
		super(minecraftCase.getName());
		mcCase = minecraftCase;
		this.dropItems = minecraftCase.getDropTable();
		int n=0;
		DecimalFormat df2 = new DecimalFormat("#.####");
		for (CaseItem caseItem : dropItems.items){
			ItemStack item = caseItem.getItem();
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			if(lore == null){
				lore = new ArrayList<>();
			}
			float chance = (((float)caseItem.getWeight()/(float)dropItems.getMaxWeight())*100);
			lore.add(ColorUtil.text2Color("&a&lSzansa: &c" +df2.format(chance)+"%"));
			meta.setLore(lore);
			item.setItemMeta(meta);
			ItemEditor itemEditor = new ItemEditor(getInventoryName(), n, mcCase);

			this.addItemStack(new SelectableItemEditor(item,itemEditor,player));
			n++;
		}
		setPage(0);
	}

	private void openNewItemCreation(Player p){
		ItemEditor itemEditor = new ItemEditor(getInventoryName(), mcCase.getDropTable().getItemCount()-1, mcCase);
		itemEditor.open(p);
	}

	@Override
	public void onSlotInteraction(Player player, InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if (item == null || item.getType() == Material.AIR){
			openNewItemCreation(player);
			return;
		}
		super.onSlotInteraction(player, e);
	}

	@Override
	public void onClose(Player p){
	}
}