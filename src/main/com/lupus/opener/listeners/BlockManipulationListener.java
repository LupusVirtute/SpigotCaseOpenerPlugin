package com.lupus.opener.listeners;

import com.lupus.gui.utils.NBTUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
		if (block == null) {
			return;
		}
		if (!p.hasPermission("case.admin.break")){
			return;
		}
		if (isTimeForDestroy){
			boolean b = ChestManager.removeCaseLocation(block);
			if (b)
				p.sendMessage(TextUtility.color("&a&lUsunięto bez problemu lokacje skrzyni"));
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		if (e.hasBlock()) {
			Player p = e.getPlayer().getPlayer();
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
		if (!e.getPlayer().hasPermission("case.admin.place"))
			return;
		ItemStack item = e.getItemInHand();
		String data;
		data = NBTUtility.getNBTValue(item,"case",String.class);
		if (data == null) {
			return;
		}
		MinecraftCase mcCase = ChestManager.getCase(data);
		if (mcCase != null){
			mcCase.addChestLocation(e.getBlockPlaced().getLocation());
			e.getPlayer().sendMessage(Message.CASE_PLACED_PROPERLY.toString());
		}
	}
}
