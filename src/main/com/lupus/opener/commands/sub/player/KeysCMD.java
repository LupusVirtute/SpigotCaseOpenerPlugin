package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.chests.MinecraftKey;
import com.lupus.opener.managers.ChestManager;
import com.lupus.utils.ColorUtil;
import com.lupus.utils.Usage;
import org.bukkit.entity.Player;

public class KeysCMD extends PlayerCommand {
	public KeysCMD() {
		super(
				"klucze",
				Usage.usage("/klucze"),
				"Pokazuje ilość kluczy",
				0);
	}

	@Override
	protected void run(Player player, String[] strings) {
		MinecraftKey[] keys = ChestManager.getKeysForPlayer(player);
		player.sendMessage(ColorUtil.text2Color("&6&l----- &e&lKlucze  &e-----"));
		for (MinecraftKey key : keys) {
			player.sendMessage(
					ColorUtil.text2Color(
							"&6"+
						key.chest + " &5: &9" + key.amount
					)
			);
		}
	}
}
