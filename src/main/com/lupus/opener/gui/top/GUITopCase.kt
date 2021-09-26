package com.lupus.opener.gui.top;

import com.lupus.gui.TopPyramidGUI;
import org.bukkit.inventory.ItemStack;

public class GUITopCase extends TopPyramidGUI {
	public GUITopCase(String invName, int invSlots,ItemStack filler, ItemStack... top) {
		super(invName, invSlots,filler, top);
	}
}
