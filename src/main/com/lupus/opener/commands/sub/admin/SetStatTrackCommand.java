package com.lupus.opener.commands.sub.admin;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.chests.CaseItem;
import com.lupus.opener.messages.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SetStatTrackCommand extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("stattrack").
			setDescription("Sets stat track on item held in hand").
			setUsage(usage("/case stattrack")).
			setArgumentAmount(0);
	public SetStatTrackCommand(){
		super(meta);
	}
	@Override
	protected void run(Player player, ArgumentList argumentList) throws Exception {
		PlayerInventory inventory = player.getInventory();
		ItemStack item = inventory.getItemInMainHand();
		if (item == null || item.getType() == Material.AIR){
			player.sendMessage(Message.NULL_ITEM_IN_HAND.toString());
			return;
		}
		item = CaseItem.addStarTrack(item);
		inventory.setItemInMainHand(item);
		player.sendMessage(Message.STATTRACK_ITEM_SET_PROPERLY.toString());
	}
}
