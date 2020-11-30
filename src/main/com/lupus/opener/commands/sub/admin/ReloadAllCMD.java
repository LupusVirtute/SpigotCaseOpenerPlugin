package com.lupus.opener.commands.sub.admin;

import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.GeneralMessages;
import com.lupus.utils.ColorUtil;
import org.bukkit.entity.Player;

public class ReloadAllCMD extends PlayerCommand {
	public ReloadAllCMD() {
		super("reloadall",
				usage("/case reloadall"),
				ColorUtil.text2Color("&6 Zapisuje samo tlumaczace sie"),
				0);
	}

	@Override
	public void run(Player executor, String[] args) {
		if (!executor.hasPermission("case.admin.save")) {
			executor.sendMessage(GeneralMessages.INSUFFICIENT_PERMISSIONS.toString());
			return;
		}
		CaseOpener.loadChests();
		return;
	}
}
