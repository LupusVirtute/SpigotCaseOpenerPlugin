package com.lupus.opener.commands.sub.admin;


import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.listeners.BlockManipulationListener;
import com.lupus.opener.messages.Message;
import org.bukkit.entity.Player;

public class AllowDestructionCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("destroy").
			setUsage(usage("/case destroy","[yes/no]")).
			setDescription(colorText("&6Pozwala lub nie pozwala na niszczenie miejsc skrzyń &a&lyes&0&l/&4&lno")).
			addPermission("case.admin.remove").
			setArgumentAmount(1);
	public AllowDestructionCMD() {
		super(meta);
	}

	@Override
	protected void run(Player executor, ArgumentList args) throws Exception {
		String argument = args.getArg(String.class,0);
		if (argument.contains("yes")) {
			BlockManipulationListener.isTimeForDestroy = true;
			executor.sendMessage(colorText(Message.DESTROY_MESSAGE_ON.toString()));
		} else {
			BlockManipulationListener.isTimeForDestroy = false;
			executor.sendMessage(colorText(Message.DESTROY_MESSAGE_OFF.toString()));
		}
	}
}
