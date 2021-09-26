package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.managers.ChestManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GetCraftedCobblex extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("cobblex").
			setArgumentAmount(0).
			setDescription("Craftuje za ciebie cały cobblex").
			setUsage("/cobblex");
	public GetCraftedCobblex() {
		super(meta);
	}

	@Override
	protected void run(Player player, ArgumentList argumentList) throws Exception {
		var inventory = player.getInventory();
		var contains = countItemsInInv(inventory,Material.COBBLESTONE) >= (64*9);
		if (contains){
			removeItemsFromInventory(inventory,Material.COBBLESTONE,64*9);
			inventory.addItem(ChestManager.getRandomCase().getCobblex());
		}
	}
	private int countItemsInInv(Inventory inv,Material mat){
		int count = 0;
		for (ItemStack content : inv.getContents()) {
			if (content != null)
				if (content.getType() == mat) {
					count += content.getAmount();
				}
		}
		return count;
	}
	private void removeItemsFromInventory(Inventory inv, Material mat, int amount){
		for (ItemStack content : inv.getContents()) {
			if (amount <= 0)
				break;
			if (content != null)
				if (content.getType() == mat) {
					var amountNeeded = 0;
					if (amount > content.getAmount())
						amountNeeded = content.getAmount() % amount;
					else
						amountNeeded = content.getAmount();
					content.setAmount(content.getAmount() - amountNeeded);
					amount -= amountNeeded;
				}
		}
	}
}
