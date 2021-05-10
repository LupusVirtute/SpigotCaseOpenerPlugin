package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.command.framework.commands.arguments.UInteger;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class KeyTransactionCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().setName("dajklucz").
			setUsage(usage("/skrzynie dajklucz","[skrzynia] [gracz] [ilosc]")).
			setArgumentAmount(3);
	public KeyTransactionCMD(){
		super(meta);
	}
	@Override
	public void run(Player player, ArgumentList args) throws Exception{
		MinecraftCase mcCase = args.getArg(MinecraftCase.class,0);
		OfflinePlayer player2 = args.getArg(OfflinePlayer.class,1);
		int amount = args.getArg(UInteger.class,2).getInteger();

		if (amount > mcCase.getKeyAmount(player)){
			player.sendMessage(Message.NOT_ENOUGH_KEYS.toString());
			return;
		}
		mcCase.removeKey(player,amount);
		mcCase.giveKey(player2.getUniqueId(),amount);

		var mrq = new MessageReplaceQuery().
				addQuery("chest",mcCase.getOfficialName()).
				addQuery("amount",amount+"").
				addQuery("player",player2.getName());
		player.sendMessage(Message.KEY_SEND_SUCCESS_SENDER.toString(mrq));
		mrq.addQuery("player",player.getName());
		if (player2.isOnline())
			player2.getPlayer().sendMessage(Message.KEY_SEND_SUCCESS_RECEIVER.toString(mrq));

	}
}
