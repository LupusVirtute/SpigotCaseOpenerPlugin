package com.lupus.opener.commands.sub.admin;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.LupusCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetAccountCMD extends LupusCommand {
	static CommandMeta meta = new CommandMeta().
		addPermission("case.admin.key.reset").
		setName("reset").
		setUsage(usage("/case reset","[player]")).
		setDescription(colorText("&6Resetujesz konto graczowi")).
		setArgumentAmount(1);
	public ResetAccountCMD() {
		super(meta);
	}

	@Override
	public void run(CommandSender commandSender, ArgumentList argumentList) throws Exception {
		var player = argumentList.getArg(OfflinePlayer.class,0);
		if (player == null){
			throw new Exception("Player offline");
		}
		for (MinecraftCase mcCase : ChestManager.getAllCases()) {
			mcCase.setKey(player.getUniqueId(),0);
		}
		var mrq = new MessageReplaceQuery().
				addQuery("player", player.getName());
		commandSender.sendMessage(Message.COMMAND_KEYS_RESET.toString(mrq));
		mrq.addQuery("player",commandSender.getName());
		if (player.isOnline())
			player.getPlayer().sendMessage(Message.COMMAND_KEYS_RESET_PLAYER.toString(mrq));

	}
}
