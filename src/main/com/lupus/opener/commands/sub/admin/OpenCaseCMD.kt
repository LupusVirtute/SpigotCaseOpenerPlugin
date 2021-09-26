package com.lupus.opener.commands.sub.admin;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.entity.Player;

public class OpenCaseCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			addPermission("case.admin.open").
			setName("open").
			setUsage(usage("/case open","[name]")).
			setDescription(colorText("&6Otwierasz skrzynie test&5O&bw&5O")).
			setArgumentAmount(1);
	public OpenCaseCMD(){
		super(meta);
	}
	@Override
	public void run(Player executor, ArgumentList args) throws Exception {
		String chestName = args.getArg(String.class,0);
		int amount = 1;
		try{
			amount = args.getArg(int.class,1);
			amount = amount <= 0 ? 1 : amount;
		}
		catch (Exception ignored){}


		MinecraftCase minecraftCase = ChestManager.getCase(chestName);
		if (minecraftCase == null) {
			var mrq = new MessageReplaceQuery().
					addQuery("chest",chestName);
			executor.sendMessage(colorText(Message.CASE_GIVEN_DONT_EXISTS.toString(mrq)));
			return;
		}
		minecraftCase.openCase(executor,amount);
	}
}
