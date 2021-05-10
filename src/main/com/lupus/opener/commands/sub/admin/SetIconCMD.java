package com.lupus.opener.commands.sub.admin;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.messages.Message;
import org.bukkit.entity.Player;

public class SetIconCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("seticon").
			setUsage("/case seticon [case]").
			setArgumentAmount(1);
	public SetIconCMD() {
		super(meta);
	}

	@Override
	protected void run(Player player, ArgumentList argumentList) throws Exception {
		var mcCase = argumentList.getArg(MinecraftCase.class,0);

		var item = player.getInventory().getItemInMainHand();
		mcCase.setIcon(item);
		player.sendMessage(Message.ICON_SET.toString());
	}
}
