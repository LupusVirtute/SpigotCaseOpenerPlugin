package com.lupus.opener.gui.selectables;

import com.lupus.gui.SelectableItem;
import com.lupus.opener.gui.ItemEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SelectableItemEditor extends SelectableItem {
	ItemEditor itemEditor;
	UUID player;
	public SelectableItemEditor(ItemStack item, ItemEditor editor, Player p) {
		super(true, item);
		itemEditor = editor;
		this.player = p.getUniqueId();
	}

	@Override
	protected void execute(Object... args) {
		itemEditor.open(Bukkit.getPlayer(player));
	}
}
