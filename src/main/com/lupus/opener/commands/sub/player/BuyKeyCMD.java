package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.BuyCaseGUI;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.EconomyMessages;
import com.lupus.utils.ColorUtil;
import com.lupus.utils.Usage;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class BuyKeyCMD extends PlayerCommand {
	public BuyKeyCMD(){
		super("kupklucz", Usage.usage("/kupklucz","[skrzynia] [ilosc]"),"&6Kupujesz klucz",0);
	}
	@Override
	public void run(Player executor, String[] args) {
		if (args.length < 2){
			BuyCaseGUI gui = new BuyCaseGUI(executor);
			gui.setPage(0);
			gui.open(executor);
			return;
		}
		if (!ChestManager.contains(args[0])) {
			executor.sendMessage(ColorUtil.text2Color("&cSkrzynia nie istnieje"));
			return;
		}
		if (!NumberUtils.isNumber(args[1])){
			executor.sendMessage(ColorUtil.text2Color(args[1]+"&cTo nie jest numer"));
			return;
		}
		MinecraftCase mcCase = ChestManager.getCase(args[0]);
		int amount = Integer.parseInt(args[1]);
		double price = (mcCase.getPrice()*amount);
		price = BuyCaseGUI.getPrice(executor,price);
		double kesz = CaseOpener.getEconomy().getBalance(executor);
		DecimalFormat df2 = new DecimalFormat("#.##");
		if ((price > kesz)) {
			executor.sendMessage(EconomyMessages.INSUFFICIENT_MONEY.toString(df2.format(price)));
			return;
		}
		CaseOpener.getEconomy().withdrawPlayer(executor,price);
		mcCase.giveKey(executor,amount);
		executor.sendMessage(ColorUtil.text2Color("&aKupiłeś &6" + amount + "&a kluczy do "+ mcCase.getOfficialName()));
		executor.sendMessage(ColorUtil.text2Color("&9Za &6"+df2.format(price)));
	}

}
