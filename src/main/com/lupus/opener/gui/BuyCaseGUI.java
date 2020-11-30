package com.lupus.opener.gui;

import com.lupus.gui.Paginator;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.gui.selectables.SelectableCommand;
import com.lupus.opener.managers.ChestManager;
import com.lupus.utils.ColorUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BuyCaseGUI extends Paginator {
	static DecimalFormat df2 = new DecimalFormat("#.###");
	public BuyCaseGUI(Player player) {
		super(ColorUtil.text2Color("&1&lKup Klucz"));
		for (MinecraftCase mcCase : ChestManager.getAllCases()) {
			ItemStack its = new ItemStack(Material.CHEST);
			double price = mcCase.getPrice();
			if (player.hasPermission("case.premium")){
				price = getPrice(player,price);
			}
			ItemMeta meta = its.getItemMeta();
			meta.setDisplayName(ColorUtil.text2Color(mcCase.getOfficialName()));
			List<String> lore = new ArrayList<>();
			lore.add(
				ColorUtil.text2Color(
					"Cena: "+df2.format(price)
				)
			);
			meta.setLore(lore);
			its.setItemMeta(meta);
			addItemStack(new SelectableCommand(its,"kupklucz "+mcCase.getName()+" 1"));
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
		Collection<Node> nodes = user.getNodes();
		for (Node node1 : nodes) {
			if (node1.getKey().contains(node.getKey())) {
				String key = node1.getKey().replace(node.getKey(),"").replace(".","");
				if (NumberUtils.isNumber(key)){
					double i = Double.parseDouble(key)/10d;
					price *= i;
				}
			}
		}
		return price;
	}
	@Override
	public void onClose(Player player) {

	}
}
