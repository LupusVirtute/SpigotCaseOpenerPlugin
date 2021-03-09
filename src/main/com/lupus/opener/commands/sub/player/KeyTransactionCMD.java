package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class KeyTransactionCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().setName("dajklucz").
			setUsage(usage("/dajklucz","[skrzynia] [gracz] [ilosc]")).
			setArgumentAmount(3);
	public KeyTransactionCMD(){
		super(meta);
	}
	@Override
	public void run(Player player, ArgumentList args) throws Exception{
		String chest = args.getArg(String.class,0);
		OfflinePlayer player2 = args.getArg(OfflinePlayer.class,1);
		int amount = args.getArg(int.class,2);

		MinecraftCase mcCase = ChestManager.getCase(chest);
		if (mcCase == null){
			var mrq = new MessageReplaceQuery().
					addQuery("chest",chest);
			player.sendMessage(Message.CASE_GIVEN_DONT_EXISTS.toString(mrq));
			return;
		}

		if (amount <= 0) {
			player.sendMessage(Message.QUANTITY_MORE_THAN_ZERO.toString());
			return;
		}

		if (amount > mcCase.getKeyAmount(player)){
			player.sendMessage(Message.NOT_ENOUGH_KEYS.toString());
			return;
		}
		mcCase.removeKey(player,amount);
		mcCase.giveKey(player2.getUniqueId(),amount);

		var mrq = new MessageReplaceQuery().
				addQuery("chest",chest).
				addQuery("amount",amount+"").
				addQuery("player",player2.getName());
		player.sendMessage(Message.KEY_SEND_SUCCESS_SENDER.toString(mrq));
		mrq.addQuery("player",player.getName());
		if (player2.isOnline())
			player2.getPlayer().sendMessage(Message.KEY_SEND_SUCCESS_RECEIVER.toString(mrq));

	}
}
