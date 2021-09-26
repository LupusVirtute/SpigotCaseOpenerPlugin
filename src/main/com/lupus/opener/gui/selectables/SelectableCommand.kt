package com.lupus.opener.gui.selectables;

import com.lupus.gui.PlayerSelectableItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SelectableCommand extends PlayerSelectableItem {
	String command;
	public SelectableCommand(ItemStack itemStack,String command) {
		super(true,itemStack);
		this.command = command;
	}

	@Override
	protected void execute(Player player, Object... objects) {
		player.performCommand(command);
	}
}
