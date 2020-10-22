package com.lupus.opener.commands;

import com.lupus.command.framework.commands.LupusCommand;
import com.lupus.command.framework.commands.SupCommand;
import com.lupus.opener.commands.sub.player.BuyKeyCMD;
import com.lupus.opener.commands.sub.player.KeyTransactionCMD;
import com.lupus.opener.commands.sub.player.KeysCMD;

public class PlayerSupCommand extends SupCommand {

	public PlayerSupCommand() {
		super(
				"skrzynki",
				"/skrzynki",
				"Główna cmd od skrzyń dla gracza",
				1,
				new LupusCommand[] {
						new KeysCMD(),
						new BuyKeyCMD(),
						new KeyTransactionCMD()
					}
				);
	}
}
