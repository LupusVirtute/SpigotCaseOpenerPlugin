package com.lupus.opener.commands.sub.admin;


import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.gui.utils.InventoryUtility;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetCaseCMD extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("get").
			setUsage(usage("/case get","[nazwa] [ilosc]")).
			setDescription(colorText("&9Dostajesz skrzynie z ktorej bedzie mozna otwierac")).
			addPermission("case.admin.get").
			setArgumentAmount(2);
	public GetCaseCMD() {
		super(meta);
	}

	@Override
	public void run(Player executor, ArgumentList args) throws Exception {
		String chestName = args.getArg(String.class,0);
		Integer stackAmount = args.getArg(int.class,1);

		boolean exists = ChestManager.contains(chestName);
		if (!exists){
			var mrq = new MessageReplaceQuery().
					addQuery("chest",chestName);
			executor.sendMessage(colorText(Message.CASE_GIVEN_DONT_EXISTS.toString()));
			return;
		}
		MinecraftCase mcCase = ChestManager.getCase(chestName);
		ItemStack stack = mcCase.giveCase();
		stack.setAmount(stackAmount);
		InventoryUtility.addItemStackToPlayerInventory(executor,stack);
		executor.sendMessage(colorText(Message.COMMAND_GET_CASE_SUCCESS.toString()));
	}
}
