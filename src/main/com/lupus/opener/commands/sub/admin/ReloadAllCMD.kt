package com.lupus.opener.commands.sub.admin;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.messages.Message;
import org.bukkit.entity.Player;

public class ReloadAllCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().setName("reloadall").
			setUsage(usage("/case reloadall")).
			setDescription(colorText("&6 Zapisuje samo tlumaczace sie")).
			addPermission("case.admin.save").
			setArgumentAmount(0);
	public ReloadAllCMD() {
		super(meta);
	}

	@Override
	public void run(Player executor, ArgumentList args) {
		CaseOpener.loadChests();
		Message.load();
	}
}
