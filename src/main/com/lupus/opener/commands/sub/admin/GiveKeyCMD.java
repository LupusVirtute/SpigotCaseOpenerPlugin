package com.lupus.opener.commands.sub.admin;








import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.GeneralMessages;
import com.lupus.utils.ColorUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GiveKeyCMD extends PlayerCommand {
	public GiveKeyCMD() {
		super("givekey",
				usage("/case givekey","[case] [name] [ilosc]"),
				ColorUtil.text2Color("&6Dajesz klucz graczowi &b&l[name] &6do skrzyni &b&l[case] &6i z iloscia &b&l[ilosc]"),
				3);
	}

	@Override
	public void run(Player executor, String[] args) {
		if (!executor.hasPermission("case.admin.key.add")) {
			executor.sendMessage(GeneralMessages.INSUFFICIENT_PERMISSIONS.toString());
			return;
		}
		if(args[0].equals("*")){
			for(MinecraftCase theCase : ChestManager.getAllCases()){
				args[0] = theCase.getName();
				String[] argsBetter = Arrays.copyOf(args,args.length);
				this.executeAsync(executor,argsBetter, CaseOpener.getMainPlugin());
			}
			return;
		}
		MinecraftCase mcCase = ChestManager.getCase(args[0]);
		if (mcCase == null) {
			executor.sendMessage(ColorUtil.text2Color("&4&lNie ma takiej skrzyni"));
			return;
		}
		if (args[1].equals("*")) {
			for(Player p : Bukkit.getOnlinePlayers()){
				args[1] = p.getName();
				String[] argsBetter = Arrays.copyOf(args,args.length);
				this.executeAsync(executor,argsBetter, CaseOpener.getMainPlugin());
			}
			return;
		}
		Player player2nd = Bukkit.getPlayerExact(args[1]);
		if (player2nd == null) {
			executor.sendMessage(GeneralMessages.PLAYER_OFFLINE.toString());
			return;
		}
		if (!NumberUtils.isNumber(args[2])){
			executor.sendMessage(ColorUtil.text2Color("&6&l"+args[3] + " &4To nie liczba"));
			return;
		}
		int amount = Integer.parseInt(args[2]);
		mcCase.giveKey(player2nd,amount);
		player2nd.sendMessage(
				ColorUtil.text2Color("&aAdmin &6" + executor.getName()+" &aDal ci &6" +amount +"&a kluczy do " +mcCase.getOfficialName())
		);
		executor.sendMessage(ColorUtil.text2Color("&6Dałeś graczowi &a"+player2nd.getName()+" &b"+amount+"  &6kluczy do "+mcCase.getOfficialName()+" &6skrzyni"));
		return;
	}
}
