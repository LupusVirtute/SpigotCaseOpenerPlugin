package com.lupus.opener.listeners;

import com.lupus.utils.ColorUtil;
import com.lupus.utils.NBTEditor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PvEListener implements Listener {
	@EventHandler
	public void onPlayerKill(EntityDeathEvent e){
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = ((Player) e.getEntity());
		Player killer = p.getKiller();
		if (killer == null){
			return;
		}
		PlayerInventory killerInventory = killer.getInventory();
		ItemStack it = killerInventory.getItemInMainHand();
		if (NBTEditor.contains(it,"StarKiller")) {
			int kills = NBTEditor.getInt(it, "StarKiller");
			kills++;
			it = NBTEditor.set(it,kills,"StarKiller");
			ItemMeta meta = it.getItemMeta();
			List<String> lore = meta.getLore();
			for (int i=0;i<lore.size();i++){
				String s = lore.get(i);
				if (ColorUtil.strip(s).contains("Kills")){
					lore.set(i,ColorUtil.text2Color("&cKills : &4"+kills));
				}
			}
			meta.setLore(lore);
			it.setItemMeta(meta);
			killerInventory.setItemInMainHand(it);
			killer.updateInventory();
		}
	}
}
