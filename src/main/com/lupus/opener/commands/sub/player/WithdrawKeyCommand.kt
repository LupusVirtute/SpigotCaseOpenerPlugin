package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.command.framework.commands.arguments.UInteger;
import com.lupus.gui.utils.InventoryUtility;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class WithdrawKeyCommand extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("wyplac").
			setUsage("/skrzynie wyplac [skrzynia] [ilosc]").
			setDescription("Wypłaca ci klucz z skrzyni na item").
			setArgumentAmount(2)
			;
	public WithdrawKeyCommand() {
		super(meta);
	}

	@Override
	protected void run(Player player, ArgumentList args) throws Exception {
		var mcCase = args.getArg(MinecraftCase.class,0);
		var amount = args.getArg(UInteger.class,1).getInteger();
		ItemStack itemStack = mcCase.turnKeyIntoItemStack(player,amount);
		if (itemStack == null) {
			player.sendMessage(Message.NOT_ENOUGH_KEYS.toString());
			return;
		}
		InventoryUtility.addItemStackToPlayerInventory(player,itemStack);
	}

	@NotNull
	@Override
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length >= 1){
			String arg = args[0];
			var caseSet = new HashSet<>(ChestManager.getAll());
			caseSet.removeIf(o->!o.startsWith(arg));
			return new ArrayList<>(caseSet);
		}
		return super.tabComplete(sender, alias, args);
	}
}
