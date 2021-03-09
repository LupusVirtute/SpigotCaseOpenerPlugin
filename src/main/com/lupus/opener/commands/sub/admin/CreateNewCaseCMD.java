package com.lupus.opener.commands.sub.admin;


import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.chests.CaseItemHolder;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CreateNewCaseCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("create").
			setUsage(usage("/case create","[name] [officialName] [price] [weight]")).
			setDescription(colorText("&9Tworzy nową skrzynie\n" +
					"&c[name] &5- &9Nazwa Skrzyni\n" +
					"&c[officialName] &5- &9Nazwa &4&LKTÓRĄ KOLORUJESZ KURWA\n" +
					"&c[price] &5- &9Cena skrzyni\n" +
					"&c[weight] &5- &9Waga skrzyni to jest jak ważna jest\n")).
			setArgumentAmount(4);
	public CreateNewCaseCMD() {
		super(meta);
	}

	@Override
	public void run(Player executor, ArgumentList args) throws Exception {
		String chest = args.getArg(String.class,0);
		String chestName = args.getArg(String.class,1);
		Float price = args.getArg(float.class,2);
		Integer weight = args.getArg(int.class,3);


		if (ChestManager.contains(chest)){
			executor.sendMessage(colorText(Message.CASE_EXISTS.toString()));
			return;
		}

		ChestManager.addCase(new MinecraftCase(
				chest,
				chestName,
				price,
				weight,
				new CaseItemHolder(new ArrayList<>())
		));
		executor.sendMessage(Message.COMMAND_CREATE_SUCCESSFUL.toString());
		return;
	}

}
