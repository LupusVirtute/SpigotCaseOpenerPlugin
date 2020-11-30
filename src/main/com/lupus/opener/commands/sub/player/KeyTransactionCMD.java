package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.GeneralMessages;
import com.lupus.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KeyTransactionCMD extends PlayerCommand {
	public KeyTransactionCMD(){
		super("dajklucz",
				usage("/dajklucz","[skrzynia] [gracz] [ilosc]"),3);
	}
	@Override
	public void run(Player player, String[] args) {
		MinecraftCase mcCase = ChestManager.getCase(args[0]);
		if (mcCase == null){
			player.sendMessage(ColorUtil.text2Color(GeneralMessages.LOGO.toString() +" &4&l"+args[0]+" &c&lTo nie skrzynia"));
			return;
		}
		Player player2 = Bukkit.getPlayerExact(args[1]);
		if (player2 == null){
			player.sendMessage(GeneralMessages.PLAYER_OFFLINE.toString());
			return;
		}
		int amount = Integer.parseInt(args[2]);
		if (amount <= 0) {
			player.sendMessage(ColorUtil.text2Color(GeneralMessages.LOGO.toString()+" &4&lIlosc musi być większa od zera"));
			return;
		}
		if (amount > mcCase.getKeyAmount(player)){
			player.sendMessage(ColorUtil.text2Color(GeneralMessages.LOGO.toString()+" &4&lNie posiadasz tyle kluczy"));
			return;
		}
		mcCase.removeKey(player,amount);
		mcCase.giveKey(player2,amount);

		player.sendMessage(ColorUtil.text2Color("&9Wysłałeś &6"+amount+" &9kluczy do &a"+args[1]));
		player2.sendMessage(ColorUtil.text2Color("&9Dostałeś &6"+amount+" &9kluczy od &a"+player.getName()));

	}
}
