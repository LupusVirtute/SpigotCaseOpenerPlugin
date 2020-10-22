package com.lupus.opener.commands.sub.admin;








import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.utils.ColorUtil;
import com.lupus.utils.PlayerRelated;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetCaseCMD extends PlayerCommand {
	public GetCaseCMD() {
		super("get",
				usage("/case get","[nazwa] [ilosc]"),
				ColorUtil.text2Color("&9Dostajesz skrzynie z ktorej bedzie mozna otwierac"),
				2);
	}

	@Override
	public void run(Player executor, String[] args) {
		if (!executor.hasPermission("case.admin.get"))
			return;
		if (!NumberUtils.isNumber(args[1])){
			executor.sendMessage(ChatColor.DARK_RED+"Jak nie wiesz jak pisać liczby to wypierdalaj");
			return;
		}
		boolean exists = ChestManager.contains(args[0]);
		if (!exists){
			executor.sendMessage(ColorUtil.text2Color("&4&lCase nie istnieje debilu"));
			return;
		}
		MinecraftCase mcCase = ChestManager.getCase(args[0]);
		ItemStack stack = mcCase.giveCase();
		stack.setAmount(Integer.parseInt(args[1]));
		PlayerRelated.addItemToPlayerInventory(executor,stack);
		executor.sendMessage(ColorUtil.text2Color("&4&lMasz i sie baw smierdzielu"));

		return;
	}
}
