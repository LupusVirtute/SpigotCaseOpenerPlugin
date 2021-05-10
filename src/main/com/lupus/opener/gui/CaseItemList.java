package com.lupus.opener.gui;

import com.lupus.gui.Paginator;
import com.lupus.gui.utils.TextUtility;
import com.lupus.opener.chests.CaseItem;
import com.lupus.opener.chests.CaseItemHolder;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.selectables.SelectableItemEditor;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaseItemList extends Paginator {
	CaseItemHolder dropItems;
	MinecraftCase mcCase;
	public CaseItemList(MinecraftCase minecraftCase,Player player){
		super(minecraftCase.getOfficialName());
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
			float chance;
			chance = ((float)caseItem.getWeight())/((float)dropItems.getMaxWeight());
			chance *= 100;

			var mrq = new MessageReplaceQuery().
					addQuery("chance",df2.format(chance));

			String[] messages = Message.DROP_CHANCE_LORE.toString(mrq).split("\\n");

			lore.addAll(Arrays.asList(messages));

			meta.setLore(lore);
			item.setItemMeta(meta);

			ItemEditor itemEditor = new ItemEditor(minecraftCase.getOfficialName(), n, mcCase);

			this.addItemStack(new SelectableItemEditor(item,itemEditor,player));
			n++;
		}
		setPage(0);
	}

	private void openNewItemCreation(Player p){
		if (!p.hasPermission("case.admin"))
			return;
		ItemEditor itemEditor = new ItemEditor(getInventoryName(), mcCase.getDropTable().getItemCount(), mcCase);
		itemEditor.open(p);
	}

	@Override
	public void onClickedItemNull(Player player, InventoryClickEvent inventoryClickEvent) {
		openNewItemCreation(player);
	}

	@Override
	public void onSlotInteraction(Player player, InventoryClickEvent e) {
		if (!player.hasPermission("case.admin"))
			return;
		super.onSlotInteraction(player, e);
	}

	@Override
	public void onClose(Player p){
	}
}