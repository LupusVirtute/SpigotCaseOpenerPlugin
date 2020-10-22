package com.lupus.opener.commands.sub.admin;






import com.lupus.command.framework.commands.LupusCommand;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.commands.CaseCMD;
import com.lupus.utils.ColorUtil;
import org.bukkit.entity.Player;

public class HelpCMD extends PlayerCommand {
	String message = ColorUtil.text2Color("&4--- &6Case CMDS &4---\n");

	public HelpCMD() {
		super("help",
				usage("/case help"),
				"Pomoc",
				0);
	}

	@Override
	public void run(Player executor, String[] args) {
		executor.sendMessage(message);
		for (LupusCommand command : new CaseCMD().getSubCommands()) {
			executor.sendMessage(command.getUsageDesc());
		}
		return;
	}

}
