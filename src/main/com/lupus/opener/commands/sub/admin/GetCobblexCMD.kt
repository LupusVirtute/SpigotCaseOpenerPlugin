package com.lupus.opener.commands.sub.admin;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.gui.utils.InventoryUtility;
import com.lupus.opener.chests.MinecraftCase;
import org.bukkit.entity.Player;

public class GetCobblexCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("cobblex").
			setUsage(usage("/case cobblex","[case]")).
			setDescription(colorText("&6Dostajesz cobblex'a")).
			addPermission("case.admin.key.add").
			setArgumentAmount(0);
	public GetCobblexCMD() {
		super(meta);
	}
	@Override
	protected void run(Player player, ArgumentList argumentList) throws Exception {
		var mcCase = argumentList.getArg(MinecraftCase.class,0);
		if (mcCase == null)
			return;
		var item = mcCase.getCobblex();
		InventoryUtility.addItemStackToPlayerInventory(player,item);
	}
}
