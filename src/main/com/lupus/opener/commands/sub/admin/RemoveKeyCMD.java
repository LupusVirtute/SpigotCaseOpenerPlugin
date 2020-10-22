package com.lupus.opener.commands.sub.admin;








import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.GeneralMessages;
import com.lupus.utils.ColorUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RemoveKeyCMD extends PlayerCommand {
	public RemoveKeyCMD(){
		super(
				"removekey",
				usage("/case removekey","[case] [name] [ilosc]"),
				ColorUtil.text2Color("&6Zabierasz klucz graczowi &b&l[name] &6do skrzyni &b&l[case] &6i z iloscia &b&l[ilosc]"),
				3
		);
	}
	@Override
	public void run(Player executor, String[] args) {
		if (!executor.hasPermission("case.admin.key.remove")) {
			executor.sendMessage(GeneralMessages.INSUFFICIENT_PERMISSIONS.toString());
			return;
		}
		MinecraftCase mcCase = ChestManager.getCase(args[0]);
		if (mcCase == null) {
			executor.sendMessage(ColorUtil.text2Color("&4&lNie ma takiej skrzyni"));
			return;
		}
		Player player2nd = Bukkit.getPlayerExact(args[1]);
		if (player2nd == null) {
			executor.sendMessage(GeneralMessages.PLAYER_OFFLINE.toString());
			return;
		}
		if (!NumberUtils.isNumber(args[2])){
			executor.sendMessage(ColorUtil.text2Color("&6&l"+args[2] + " &4To nie liczba"));
			return;
		}
		if (!mcCase.hasKey(player2nd)) {
			executor.sendMessage(ColorUtil.text2Color("&4&lGosciu juz nie ma kluczy odpusc mu"));
			return;
		}
		int amount = Integer.parseInt(args[2]);
		if (mcCase.getKeyAmount(player2nd) < amount){
			amount = mcCase.getKeyAmount(player2nd);
		}
		mcCase.removeKey(player2nd,amount);
		player2nd.sendMessage(
				ColorUtil.text2Color("&aAdmin &6" + executor.getName()+" &aZabrał ci &6" +amount +"&a kluczy do " +mcCase.getOfficialName())
		);
		executor.sendMessage(ColorUtil.text2Color("&6Zabrałeś graczowi &a"+player2nd.getName()+" &b"+amount+"  &6kluczy do "+mcCase.getOfficialName()+" &6skrzyni"));
		return;
	}
}
