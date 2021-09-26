package com.lupus.opener.listeners;

import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.chests.utils.MinecraftCaseUtils;
import com.lupus.opener.managers.ChestManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class InventoryListener implements Listener {
	@EventHandler
	public void onCraftingInventory(PrepareItemCraftEvent e){
		CraftingInventory inv = e.getInventory();
		if (qualifiesAsCobblex(inv.getMatrix()))
			inv.setResult(ChestManager.getRandomCase().getCobblex());
	}
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent e){
		if (e.getSlot() != InventoryType.SlotType.RESULT.ordinal()){
			return;
		}
		if (!(e.getClickedInventory() instanceof CraftingInventory)) {
			return;
		}
		var inv = (CraftingInventory)e.getClickedInventory();
		var res= inv.getResult();
		if (MinecraftCaseUtils.getCobblex(res) == null) {
			return;
		}
		if (!qualifiesAsCobblex(inv.getMatrix()))
			return;
		inv.remove(Material.COBBLESTONE);
		var player = e.getViewers().get(0);
		inv.setResult(res);
		if (player != null){
			((Player)player).updateInventory();
		}
	}
	public boolean qualifiesAsCobblex(ItemStack[] matrix){
		int incrementer = 0;
		for (ItemStack item : matrix) {
			if (item == null)
				return false;
			if (item.getType() == Material.COBBLESTONE) {
				if (item.getAmount() == 64) {
					incrementer++;
				}
			}
		}
		return incrementer >= 9;
	}
}
