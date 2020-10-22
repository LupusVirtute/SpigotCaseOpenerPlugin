package com.lupus.opener.commands.sub.admin;

import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.GeneralMessages;
import com.lupus.utils.ColorUtil;
import org.bukkit.entity.Player;

public class OpenCaseCMD extends PlayerCommand {
	public OpenCaseCMD(){
		super(
				"open",
				usage("/case open","[name]"),
				ColorUtil.text2Color("&6Otwierasz skrzynie test&5O&bw&5O"),
				1
		);
	}
	@Override
	public void run(Player executor, String[] args) {
		if (!executor.hasPermission("case.admin.open")) {
			executor.sendMessage(GeneralMessages.INSUFFICIENT_PERMISSIONS.toString());
			return;
		}
		int amount = 1;
		if (args.length >= 2) {
			amount = Integer.parseInt(args[1]);
			amount = amount <= 0 ? 1 : amount;
		}
		MinecraftCase minecraftCase = ChestManager.getCase(args[0]);
		if (minecraftCase == null) {
			executor.sendMessage(ColorUtil.text2Color("&4Skrzynia o nazwie &6&l" + args[0]+ " nie istnieje"));
			return;
		}
		minecraftCase.openCase(executor,amount);
		return;
	}
}
