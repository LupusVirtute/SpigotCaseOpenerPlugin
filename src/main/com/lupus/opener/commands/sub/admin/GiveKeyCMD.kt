package com.lupus.opener.commands.sub.admin;


import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.LupusCommand;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class GiveKeyCMD extends LupusCommand {
	static CommandMeta meta = new CommandMeta().
			setName("givekey").
			setUsage(usage("/case givekey","[case] [name] [ilosc]")).
			setDescription(colorText("&6Dajesz klucz graczowi &b&l[name] &6do skrzyni &b&l[case] &6i z iloscia &b&l[ilosc]")).
			addPermission("case.admin.key.add").
			setArgumentAmount(3);
	public GiveKeyCMD() {
		super(meta);
	}

	@Override
	public void run(CommandSender executor, ArgumentList args) throws Exception {
		String chest = args.getArg(String.class,0);
		String playerName = args.getArg(String.class,1);
		Integer amount = args.getArg(int.class,2);

		if(chest.equals("*")){
			giveAllCasesTo(executor, args);
			return;
		}

		MinecraftCase mcCase = ChestManager.getCase(chest);
		if (mcCase == null) {
			var mpq = new MessageReplaceQuery().
					addQuery("chest",chest);
			executor.sendMessage(colorText(Message.CASE_GIVEN_DONT_EXISTS.toString(mpq)));
			return;
		}

		if (playerName.equals("*")) {
			giveCaseToAll(executor, args);
			return;
		}

		Player player2nd = Bukkit.getPlayerExact(playerName);
		UUID uuid = null;
		if (player2nd == null) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
			uuid = player.getUniqueId();
		}
		else
			uuid = player2nd.getUniqueId();

		mcCase.giveKey(uuid,amount);
		var mrq = new MessageReplaceQuery().
				addQuery("player",executor.getName()).
				addQuery("amount",String.valueOf(amount)).
				addQuery("chest",mcCase.getOfficialName());

		if (player2nd != null) {
			player2nd.sendMessage(
					Message.COMMAND_GIVE_KEY_SUCCESS_MESSAGE_PLAYER.toString(mrq)
			);
		}

		mrq.addQuery("player", playerName);
		executor.sendMessage(Message.COMMAND_GIVE_KEY_SUCCESS_MESSAGE_ADMIN.toString(mrq));
	}
	private void giveAllCasesTo(CommandSender executor, ArgumentList args){
		for(MinecraftCase theCase : ChestManager.getAllCases()){
			String chest = theCase.getName();
			String[] argsBetter = Arrays.copyOf(args.toArray(new String[0]),args.size());
			argsBetter[0] = chest;
			this.executeAsync(executor,argsBetter, CaseOpener.getMainPlugin());
		}
	}
	private void giveCaseToAll(CommandSender executor, ArgumentList args){
		for(Player p : Bukkit.getOnlinePlayers()){
			String playerName = p.getName();
			String[] argsBetter = Arrays.copyOf(args.toArray(new String[0]),args.size());
			argsBetter[1] = playerName;
			this.executeAsync(executor,argsBetter, CaseOpener.getMainPlugin());
		}
	}
}
