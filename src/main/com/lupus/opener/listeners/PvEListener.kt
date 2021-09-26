package com.lupus.opener.listeners;

import com.lupus.gui.utils.NBTUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.ChatColor;
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
		if (NBTUtility.hasNBTTag(it,"StarKiller")) {
			int kills  = NBTUtility.getNBTValue(it, "StarKiller",int.class);
			kills++;

			NBTUtility.setNBTDataValue(it,"StarKiller",kills);

			ItemMeta meta = it.getItemMeta();
			List<String> lore = meta.getLore();
			for (int i=0;i<lore.size();i++){
				String s = lore.get(i);
				if (strip(s).contains("Kills")){
					var mrq = new MessageReplaceQuery().
							addQuery("amount",kills+"");
					lore.set(i, Message.STATTRACK_KILLS_FORMATING.toString(mrq));
				}
			}
			meta.setLore(lore);
			it.setItemMeta(meta);
			killerInventory.setItemInMainHand(it);
			killer.updateInventory();
		}
	}
	public String strip(String s){
		return ChatColor.stripColor(s);
	}
}
