package com.lupus.opener.commands.sub.admin;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.LupusCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.command.CommandSender;

public class EditWeightCMD extends LupusCommand {
	static CommandMeta meta = new CommandMeta().
			addPermission("case.admin").
			setName("editweight").
			setDescription(colorText("&4&lEdytuje wage skrzyni")).
			setUsage(usage("/case editweight","[case] [newweight]")).
			setArgumentAmount(2);
	public EditWeightCMD() {
		super(meta);
	}

	@Override
	public void run(CommandSender commandSender, ArgumentList args) throws Exception {
		String chest = args.getArg(String.class,0);
		int weight = args.getArg(int.class,1);

		MinecraftCase minecraftCase = ChestManager.getCase(chest);
		if (minecraftCase == null) {
			var mrq = new MessageReplaceQuery().
					addQuery("chest",chest);
			commandSender.sendMessage(colorText(Message.CASE_GIVEN_DONT_EXISTS.toString(mrq)));
			return;
		}
		minecraftCase.setWeight(weight);
		commandSender.sendMessage(colorText(Message.COMMAND_EDIT_WEIGHT_SUCCESS.toString()));
	}
}
