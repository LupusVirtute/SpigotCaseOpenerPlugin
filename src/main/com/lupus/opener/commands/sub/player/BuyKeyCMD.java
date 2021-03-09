package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.BuyCaseGUI;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class BuyKeyCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("kupklucz").
			setUsage(usage("/kupklucz","[skrzynia] [ilosc]")).
			setDescription("&6Kupujesz klucz").
			setArgumentAmount(0);
	public BuyKeyCMD(){
		super(meta);
	}
	@Override
	public void run(Player executor, ArgumentList args) throws Exception {
		if (args.size() < 2){
			BuyCaseGUI gui = new BuyCaseGUI(executor);
			gui.setPage(0);
			gui.open(executor);
			return;
		}
		String chestName = args.getArg(String.class,0);
		int amount = args.getArg(int.class,1);


		if (!ChestManager.contains(chestName)) {
			var mrq = new MessageReplaceQuery().
					addQuery("chest",chestName);
			executor.sendMessage(Message.CASE_GIVEN_DONT_EXISTS.toString(mrq));
			return;
		}

		MinecraftCase mcCase = ChestManager.getCase(chestName);


		double price = (mcCase.getPrice()*amount);
		price = BuyCaseGUI.getPrice(executor,price);

		double kesz = CaseOpener.getEconomy().getBalance(executor);

		DecimalFormat df2 = new DecimalFormat("#.##");

		if ((price > kesz)) {
			var mrq = new MessageReplaceQuery().
					addQuery("price",df2.format(price));
			executor.sendMessage(Message.INSUFFICIENT_MONEY.toString(mrq));
			return;
		}

		CaseOpener.getEconomy().withdrawPlayer(executor,price);
		mcCase.giveKey(executor,amount);
		var mrq = new MessageReplaceQuery().
				addQuery("amount",amount+"").
				addQuery("chest", mcCase.getOfficialName()).
				addQuery("price", df2.format(price));
		executor.sendMessage(Message.COMMAND_BUY_KEY_BOUGHT.toString(mrq));
	}

}
