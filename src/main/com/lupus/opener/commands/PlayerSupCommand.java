package com.lupus.opener.commands;

import com.lupus.command.framework.commands.LupusCommand;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.commands.sub.admin.OpenEditorCMD;
import com.lupus.opener.gui.ChestList;
import com.lupus.utils.ColorUtil;
import org.bukkit.entity.Player;

public class PlayerSupCommand extends PlayerCommand {
	public PlayerSupCommand() {
		super(
				"skrzynie",
				"/skrzynie",
				"Pokazuje drop i liste itemów",
				0
				);
	}
	@Override
	protected void run(Player player, String[] strings) {
		ChestList list = new ChestList(ColorUtil.text2Color("&a&lSkrzynki"),player);
		list.open(player);
	}
}
