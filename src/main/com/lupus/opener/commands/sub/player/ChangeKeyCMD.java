package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.GeneralMessages;
import com.lupus.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChangeKeyCMD extends PlayerCommand {
	public ChangeKeyCMD() {
		super("zamienklucz", usage("/zamienklucz","[zskrzyni] [naskrzynie] [ilosc]"), ColorUtil.text2Color("&6&l"), 3);
	}

	@Override
	protected void run(Player player, String[] args) {
		MinecraftCase mcCase = ChestManager.getCase(args[0]);
		if (mcCase == null){
			player.sendMessage(ColorUtil.text2Color(GeneralMessages.LOGO.toString() +" &4&l"+args[0]+" &c&lTo nie skrzynia"));
			return;
		}
		MinecraftCase forCase = ChestManager.getCase(args[1]);
		if (forCase == null){
			player.sendMessage(ColorUtil.text2Color(GeneralMessages.LOGO.toString() +" &4&l"+args[1]+" &c&lTo nie skrzynia"));
			return;
		}
		if (forCase.getCaseWeight() < mcCase.getCaseWeight())
			return;
		int amount = Integer.parseInt(args[2]);
		int weightOfCase = (mcCase.getCaseWeight()/16);
		int neededAmount = (forCase.getCaseWeight() / weightOfCase)*amount;
		if (neededAmount > mcCase.getKeyAmount(player)){
			player.sendMessage(ColorUtil.text2Color(GeneralMessages.LOGO.toString()+" &4&lNie posiadasz tyle kluczy Potrzebujesz:" + neededAmount));
			return;
		}
		mcCase.removeKey(player,neededAmount);
		forCase.giveKey(player,amount);

		player.sendMessage(ColorUtil.text2Color("&9Zamieniłeś &6"+neededAmount+" &9kluczy z "+mcCase.getOfficialName()+" na &6"+amount+" Kluczy z "+forCase.getOfficialName()));
	}
}
