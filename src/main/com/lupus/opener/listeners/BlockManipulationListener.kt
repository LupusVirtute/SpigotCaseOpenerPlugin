package com.lupus.opener.listeners;

import com.lupus.gui.utils.NBTUtility;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.chests.utils.MinecraftCaseUtils;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BlockManipulationListener implements Listener {
	public static boolean isTimeForDestroy = false;
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player p = e.getPlayer().getPlayer();
		if (p == null) {
			return;
		}
		Block block = e.getBlock();
		if (block.getType() == Material.MOSSY_COBBLESTONE){
			for (MinecraftCase mcCase : ChestManager.getAllCases()) {
				if (mcCase.breakCobblex(e.getPlayer(),block.getLocation())) {
					e.setDropItems(false);
					return;
				}
			}
		}
		if (block == null) {
			return;
		}
		if (!p.hasPermission("case.admin.break")){
			return;
		}
		if (isTimeForDestroy){
			boolean b = ChestManager.removeCaseLocation(block);
			if (b)
				p.sendMessage(Message.CASE_BREAKED.toString());
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		if (e.getHand() == EquipmentSlot.HAND) {
			if (e.getItem() != null){
				var mcCase = MinecraftCaseUtils.getKeyRedeemCase(e.getItem());
				if (mcCase != null) {
					int b = mcCase.redeemKey(e.getPlayer(), e.getItem());
					if (b > -4)
						e.setCancelled(true);
					if (b == -3)
						Bukkit.broadcast(e.getPlayer().getName() + " - Prawdopodobne Kopiowanie kluczy", "case.moderator");
				}
			}
		}
		if (e.hasBlock()) {
			Player p = e.getPlayer();
			if (p == null) {
				return;
			}
			if (p.hasPermission("case.admin.break") && isTimeForDestroy) {
				return;
			}
			if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK){
				MinecraftCase mcCase = ChestManager.getCaseFromLocation(
						e.getClickedBlock().getLocation()
				);
				if (mcCase != null) {
					e.setCancelled(true);
					mcCase.openCase(p,1);
				}
			}
		}
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		if (e == null)
			return;
		ItemStack item = e.getItemInHand();
		if (item.getType() == Material.MOSSY_COBBLESTONE){
			var mcCase = MinecraftCaseUtils.getCobblex(item);
			if (mcCase != null)
				mcCase.putDownCobblex(item,e.getBlockPlaced().getLocation());
		}
		if (!e.getPlayer().hasPermission("case.admin.place"))
			return;
		String data;
		data = NBTUtility.getNBTValue(item,"case",String.class);
		if (data == null) {
			return;
		}
		MinecraftCase mcCase = ChestManager.getCase(data);
		if (mcCase != null){
			mcCase.addChestLocation(e.getBlockPlaced().getLocation().clone());
			e.getPlayer().sendMessage(Message.CASE_PLACED_PROPERLY.toString());
		}
	}
}
