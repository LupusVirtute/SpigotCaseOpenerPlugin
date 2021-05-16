package com.lupus.opener.gui;

import com.lupus.gui.Paginator;
import com.lupus.gui.SelectableItem;
import com.lupus.gui.TopPyramidGUI;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.selectables.SelectableTop;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TopKeysGUI extends Paginator {
	public static List<SelectableTop> selectableTops = new ArrayList<>();
	public TopKeysGUI(Player caller) {
		super(Message.TOP_KEYS_MAIN_INVENTORY_NAME.toString());
		for (SelectableTop item : selectableTops) {
			addItemStack(item);
		}
		setPage(0);
	}

	@Override
	public void onClose(Player player) {

	}
}
