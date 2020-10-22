package com.lupus.opener.commands.sub.admin;








import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.chests.CaseItemHolder;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.utils.ColorUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CreateNewCaseCMD extends PlayerCommand {
	public CreateNewCaseCMD() {
		super("create",
				usage("/case create","[name] [officialName] [price] [weight]"),
				ColorUtil.text2Color("&9Tworzy nową skrzynie\n" +
						"&c[name] &5- &9Nazwa Skrzyni\n" +
						"&c[officialName] &5- &9Nazwa &4&LKTÓRĄ KOLORUJESZ KURWA\n" +
						"&c[price] &5- &9Cena skrzyni\n" +
						"&c[weight] &5- &9Waga skrzyni to jest jak ważna jest\n")
				, 4);
	}

	@Override
	public void run(Player executor, String[] args) {
		if (ChestManager.contains(args[0])){
			executor.sendMessage(ColorUtil.text2Color("&4&lSkrzynia istnieje"));
			return;
		}
		if (!NumberUtils.isNumber(args[2])){
			executor.sendMessage(ChatColor.DARK_RED+"Podaj realną cene");
			return;
		}
		if (!NumberUtils.isNumber(args[3])){
			executor.sendMessage(ChatColor.DARK_RED+"Podaj realną wagę lub jak ważna ta skrzynia jest");
			return;
		}
		ChestManager.addCase(new MinecraftCase(
				args[0],
				args[1],
				Float.parseFloat(args[2]),
				Integer.parseInt(args[3]),
				new CaseItemHolder(new ArrayList<>())
		));
		executor.sendMessage(ChatColor.GREEN+"Poprawnie stworzono skrzynie");
		return;
	}

}
