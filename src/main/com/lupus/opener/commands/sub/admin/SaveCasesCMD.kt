package com.lupus.opener.commands.sub.admin;


import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.managers.ChestManager;
import org.bukkit.entity.Player;


public class SaveCasesCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("saveall").
			setUsage(usage("/case saveall")).
			setDescription(colorText("&6 Zapisuje samo tlumaczace sie")).
			setArgumentAmount(0).
			addPermission("case.admin.save");

	public SaveCasesCMD() {
		super(meta);
	}

	@Override
	public void run(Player executor, ArgumentList args) {
		ChestManager.saveAll(true);
	}
}