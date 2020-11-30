package com.lupus.opener.commands.sub.admin;

import com.lupus.command.framework.commands.LupusCommand;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.utils.ColorUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class EditWeightCMD extends LupusCommand {
	public EditWeightCMD() {
		super("editweight", usage("/case editweight","[case] [newweight]"), ColorUtil.text2Color("&4&lEdytuje wage skrzyni"),2);
	}

	@Override
	public void run(CommandSender commandSender, String[] args) {
		if (!commandSender.hasPermission("case.admin"))
			return;
		MinecraftCase minecraftCase = ChestManager.getCase(args[0]);
		if (minecraftCase == null) {
			commandSender.sendMessage(ColorUtil.text2Color("&4Skrzynia o nazwie &6&l" + args[0]+ " nie istnieje"));
			return;
		}
		if (NumberUtils.isNumber(args[1])){
			commandSender.sendMessage(ColorUtil.text2Color("&4&lTo nie jest numer padalcu"));
			return;
		}
		int weight = Integer.parseInt(args[1]);
		minecraftCase.setWeight(weight);
		commandSender.sendMessage(ColorUtil.text2Color("&a&lUstawiono nową wagę skrzyni poprawnie"));
	}
}
