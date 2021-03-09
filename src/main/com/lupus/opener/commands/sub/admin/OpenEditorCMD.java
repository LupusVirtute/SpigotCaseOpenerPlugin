package com.lupus.opener.commands.sub.admin;


import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.gui.ChestList;
import com.lupus.opener.messages.Message;
import org.bukkit.entity.Player;

public class OpenEditorCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("editor").
			setUsage(usage("/case editor")).
			setDescription(colorText("&9Otwiera edytor skrzyń")).
			setArgumentAmount(0);
	public OpenEditorCMD(){
		super(meta);
	}
	@Override
	public void run(Player executor, ArgumentList args) {
		ChestList list = new ChestList(Message.CHEST_LIST_INVENTORY_NAME.toString(),executor);
		list.open(executor);
	}
}
