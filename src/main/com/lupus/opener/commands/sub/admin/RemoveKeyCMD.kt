package com.lupus.opener.commands.sub.admin;


import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class RemoveKeyCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			addPermission("case.admin.key.remove").
			setName("removekey").
			setUsage(usage("/case removekey","[case] [name] [ilosc]")).
			setDescription(colorText("&6Zabierasz klucz graczowi &b&l[name] &6do skrzyni &b&l[case] &6i z iloscia &b&l[ilosc]")).
			setArgumentAmount(3);

	public RemoveKeyCMD(){
		super(meta);
	}
	@Override
	public void run(Player executor, ArgumentList args) throws Exception {
		String chestName = args.getArg(String.class,0);
		Player player2nd = args.getArg(Player.class,1);
		int amount = args.getArg(int.class,2);
		if(chestName.equals("*")){
			giveAllCasesTo(executor, args);
			return;
		}
		MinecraftCase mcCase = ChestManager.getCase(chestName);
		if (mcCase == null) {
			var mrq = new MessageReplaceQuery().
					addQuery("chest",chestName);
			executor.sendMessage(Message.CASE_GIVEN_DONT_EXISTS.toString(mrq));
			return;
		}
		if (args.get(1).equals("*")) {
			giveCaseToAll(executor, args);
			return;
		}
		if (!mcCase.hasKey(player2nd)) {
			executor.sendMessage(colorText(Message.COMMAND_TAKE_KEY_FAIL_NO_KEYS_LEFT.toString()));
			return;
		}
		if (mcCase.getKeyAmount(player2nd) < amount){
			amount = mcCase.getKeyAmount(player2nd);
		}
		mcCase.removeKey(player2nd,amount);
		var mrq = new MessageReplaceQuery().
				addQuery("player",executor.getName()).
				addQuery("amount",String.valueOf(amount)).
				addQuery("chest",mcCase.getOfficialName());
		player2nd.sendMessage(
				Message.COMMAND_TAKE_KEY_SUCCESS_MESSAGE_PLAYER.toString(mrq)
		);
		mrq.addQuery("player",player2nd.getName());
		executor.sendMessage(Message.COMMAND_TAKE_KEY_SUCCESS_MESSAGE_ADMIN.toString(mrq));
	}
	private void giveAllCasesTo(Player executor, ArgumentList args){
		for(MinecraftCase theCase : ChestManager.getAllCases()){
			String chest = theCase.getName();
			String[] argsBetter = Arrays.copyOf(args.toArray(new String[0]),args.size());
			argsBetter[0] = chest;
			this.executeAsync(executor,argsBetter, CaseOpener.getMainPlugin());
		}
	}
	private void giveCaseToAll(Player executor, ArgumentList args){
		for(Player p : Bukkit.getOnlinePlayers()){
			String playerName = p.getName();
			String[] argsBetter = Arrays.copyOf(args.toArray(new String[0]),args.size());
			argsBetter[1] = playerName;
			this.executeAsync(executor,argsBetter, CaseOpener.getMainPlugin());
		}
	}
}
