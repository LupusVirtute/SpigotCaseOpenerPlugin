package com.lupus.opener.gui;

import com.lupus.gui.Paginator;
import com.lupus.gui.utils.ItemUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.selectables.SelectableCommand;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BuyCaseGUI extends Paginator {
	static DecimalFormat df2 = new DecimalFormat("#.###");
	public BuyCaseGUI(Player player) {
		super(Message.BUY_CASE_INVENTORY_NAME.toString());
		for (MinecraftCase mcCase : ChestManager.getAllCases()) {
			ItemStack its = new ItemStack(Material.CHEST);
			double price = mcCase.getPrice();

			if (player.hasPermission("case.premium")){
				price = getPrice(player,price);
			}
			ItemUtility.setItemTitle(its,TextUtility.color(mcCase.getOfficialName()));

			ItemMeta meta = its.getItemMeta();

			var mrq = new MessageReplaceQuery().
					addQuery("price",df2.format(price));
			String[] messages = Message.BUY_CASE_PRICE_LORE.toString(mrq).split("\\n");

			List<String> lore = new ArrayList<>(Arrays.asList(messages));

			meta.setLore(lore);
			its.setItemMeta(meta);
			addItemStack(new SelectableCommand(its,"skrzynie kupklucz "+mcCase.getName()+" 1"));
		}
	}
	public static double getPrice(Player p,double price){
		Node node = Node.
				builder("case.premium").
				value(true).
				build();
		LuckPerms api = CaseOpener.getLuckPermsAPI();
		if (api == null)
			return price;
		User user = api.getUserManager().getUser(p.getUniqueId());
		if (user == null)
			return price;
		Collection<Node> nodes = user.resolveDistinctInheritedNodes(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build());
		for (Node node1 : nodes) {
			if (node1.getKey().contains(node.getKey())) {
				String key = node1.getKey().replace(node.getKey(),"").replace(".","");
				if (NumberUtils.isNumber(key)){
					double i = Double.parseDouble(key)*10d;
					double rest = (price / i);
					price -= rest;
				}
			}
		}
		return price;
	}
	@Override
	public void onClose(Player player) {

	}
}
