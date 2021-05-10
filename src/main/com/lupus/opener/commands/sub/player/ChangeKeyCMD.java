package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.entity.Player;

public class ChangeKeyCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("zamienklucz").
			setUsage(usage("/skrzynie zamienklucz","[zskrzyni] [naskrzynie] [ilosc]")).
			setDescription(colorText("&6&lNarazie brak spisu ile na jaka skrzynie trzeba"))
			.setArgumentAmount(3);
	public ChangeKeyCMD() {
		super(meta);
	}

	@Override
	protected void run(Player player, ArgumentList args) throws Exception {
		String chestFromName = args.getArg(String.class,0);
		String chestToName = args.getArg(String.class,1);
		int amount = args.getArg(int.class,2);


		MinecraftCase mcCase = ChestManager.getCase(chestFromName);
		if (mcCase == null){
			var mrq = new MessageReplaceQuery().
					addQuery("chest",chestFromName);
			player.sendMessage(Message.CASE_GIVEN_DONT_EXISTS.toString(mrq));
			return;
		}
		MinecraftCase forCase = ChestManager.getCase(chestToName);
		if (forCase == null){
			var mrq = new MessageReplaceQuery().
					addQuery("chest",chestToName);
			player.sendMessage(Message.CASE_GIVEN_DONT_EXISTS.toString(mrq));
			return;
		}
		if (forCase.getCaseWeight() < mcCase.getCaseWeight())
			return;

		int weightOfCase = (mcCase.getCaseWeight()/16);
		int neededAmount = (forCase.getCaseWeight() / weightOfCase)*amount;
		if (neededAmount > mcCase.getKeyAmount(player)){
			var mrq = new MessageReplaceQuery().
					addQuery("amount",neededAmount+"");
			player.sendMessage(Message.COMMAND_CHANGE_KEY_NEED.toString(mrq));
			return;
		}
		mcCase.removeKey(player,neededAmount);
		forCase.giveKey(player,amount);
		var mrq = new MessageReplaceQuery().
				addQuery("needed_amount",neededAmount+"").
				addQuery("chest",chestFromName).
				addQuery("amount",amount+"").
				addQuery("chest2",chestToName);
		player.sendMessage(Message.COMMAND_CHANGE_KEY_SUCCESS.toString(mrq));
	}
}
