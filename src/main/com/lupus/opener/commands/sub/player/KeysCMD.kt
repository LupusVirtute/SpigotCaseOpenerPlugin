package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.chests.MinecraftKey;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.entity.Player;

public class KeysCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("klucze").
			setUsage(usage("/skrzynie klucze")).
			setDescription("Pokazuje ilość kluczy").
			setArgumentAmount(0);


	public KeysCMD() {
		super(meta);
	}

	@Override
	protected void run(Player player, ArgumentList strings) {
		MinecraftKey[] keys = ChestManager.getKeysForPlayer(player);
		player.sendMessage(Message.COMMAND_KEYS_TOP.toString());
		for (MinecraftKey key : keys) {
			String name = ChestManager.getCase(key.chest).getOfficialName();
			var mrq = new MessageReplaceQuery().
					addQuery("chest", name).
					addQuery("amount", key.amount+"");
			player.sendMessage(Message.COMMAND_KEYS_KEY.toString(mrq));
		}
		player.sendMessage(Message.COMMAND_KEYS_BOTTOM.toString());

	}
}
