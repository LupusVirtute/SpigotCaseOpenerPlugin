package com.lupus.opener.commands;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.LupusCommand;
import com.lupus.command.framework.commands.PlayerSupCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.commands.sub.player.*;
import com.lupus.opener.gui.ChestList;
import com.lupus.opener.messages.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCaseCommand extends PlayerSupCommand {
	static CommandMeta meta = new CommandMeta().
			setName("skrzynie").
			setUsage(usage("/skrzynie")).
			setDescription("Główna komenda dla skrzyń dla gracza").
			setArgumentAmount(0);
	public PlayerCaseCommand() {
		super(meta, new LupusCommand[]{
			new BuyKeyCMD(),
			new ChangeKeyCMD(),
			new KeyTopCMD(),
			new KeyTransactionCMD(),
			new WithdrawKeyCommand(),
		});
	}

	@Override
	protected boolean optionalOperations(CommandSender sender, ArgumentList args) {
		if (sender instanceof Player) {
			if (args.size() < 1){
				Player player = (Player)sender;
				ChestList list = new ChestList(Message.CHEST_LIST_INVENTORY_NAME.toString(),player);
				list.open(player);
				return true;
			}
		}
		return super.optionalOperations(sender, args);
	}
}
