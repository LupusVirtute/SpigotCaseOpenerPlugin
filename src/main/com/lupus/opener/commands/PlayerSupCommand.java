package com.lupus.opener.commands;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.gui.ChestList;
import com.lupus.opener.messages.Message;
import org.bukkit.entity.Player;

public class PlayerSupCommand extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("skrzynie").
			setUsage(usage("/skrzynie")).
			setDescription("Pokazuje drop i liste itemow").
			setArgumentAmount(0);
	public PlayerSupCommand() {
		super(meta);
	}
	@Override
	protected void run(Player player, ArgumentList args) {
		ChestList list = new ChestList(Message.CHEST_LIST_INVENTORY_NAME.toString(),player);
		list.open(player);
	}
}
