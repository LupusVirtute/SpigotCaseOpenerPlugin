package com.lupus.opener.listeners;

import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.chests.utils.MinecraftCaseUtils;
import com.lupus.opener.managers.ChestManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class InventoryListener implements Listener {
	@EventHandler
	public void onCraftingInventory(PrepareItemCraftEvent e){
		CraftingInventory inv = e.getInventory();
		int incrementer = 0;
		for (ItemStack matrix : inv.getMatrix()) {
			if (matrix == null)
				return;
			if (matrix.getType() == Material.COBBLESTONE) {
				if (matrix.getAmount() == 64) {
					incrementer++;
				}
			}
		}
		if (incrementer >= 9) {
			var arr = ChestManager.getAllCases().toArray(new MinecraftCase[0]);
			int randomEl = new Random().nextInt(arr.length);
			inv.setResult(arr[randomEl].getCobblex());
		}
	}
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent e){
		if (!(e.getInventory() instanceof CraftingInventory)) {
			return;
		}
		if (e.getSlot() != InventoryType.SlotType.CRAFTING.ordinal()){
			return;
		}
		var inv = (CraftingInventory) e.getInventory();
		var res = inv.getResult();
		if (MinecraftCaseUtils.getCobblex(res) != null) {
			inv.setMatrix(new ItemStack[9]);
		}
	}
}
