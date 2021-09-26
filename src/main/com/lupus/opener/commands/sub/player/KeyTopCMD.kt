package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.gui.TopKeysGUI;
import org.bukkit.entity.Player;

public class KeyTopCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("kluczetop").
			setUsage(usage("/skrzynie kluczetop")).
			setDescription("&aPokazuje top kluczy");
	public KeyTopCMD(){
		super(meta);
	}
	@Override
	protected void run(Player player, ArgumentList argumentList) throws Exception {
		TopKeysGUI gui = new TopKeysGUI(player);
		gui.open(player);
	}
}
