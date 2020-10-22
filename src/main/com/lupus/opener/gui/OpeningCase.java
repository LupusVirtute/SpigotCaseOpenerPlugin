package com.lupus.opener.gui;


import com.lupus.gui.GUI;
import com.lupus.opener.managers.OpenerManager;
import com.lupus.opener.messages.GeneralMessages;
import com.lupus.utils.ColorUtil;
import com.lupus.utils.ItemStackUtil;
import com.lupus.utils.PlayerRelated;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class OpeningCase extends GUI {
	int winnerIndex;
	ItemStack[] items;
	int currentIndex;
	ItemStack exit;
	boolean playerWantsOut;
	float winnerPercentage;
	/**
	 * @param chestOfName - official name of chest
	 * @param items - item to show
	 * @param winnerIndex - winning index
	 */
	public OpeningCase(String chestOfName,ItemStack[] items,int winnerIndex,float winnerPercentage) {
		super(chestOfName, 27);
		this.items = items;
		this.winnerPercentage = winnerPercentage;
		this.winnerIndex = winnerIndex;
		exit = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 5);
		ItemMeta meta = exit.getItemMeta();
		meta.setDisplayName(ColorUtil.text2Color(GeneralMessages.LOGO.toString()));
		List<String> lore = new ArrayList<>();
		lore.add(ColorUtil.text2Color("&aOtwieranie skrzynki..."));
		lore.add(ColorUtil.text2Color("&4Jak chcesz tylko dostac item to nacisnij na to szkło"));
		meta.setLore(lore);
		exit.setItemMeta(meta);

		ItemStack compass = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 14);
		meta = compass.getItemMeta();
		meta.setDisplayName(ColorUtil.text2Color(GeneralMessages.LOGO.toString()));
		lore = new ArrayList<>();
		lore.add(ColorUtil.text2Color("&aOtwieranie skrzynki..."));
		lore.add(ColorUtil.text2Color("&4Wskaznik..."));
		meta.setLore(lore);
		compass.setItemMeta(meta);

		for (int i=0;i<9;i++){
			inv.setItem(i,exit);
		}
		for (int i=18;i<27;i++){
			inv.setItem(i,exit);
		}
		inv.setItem(22,compass);
		inv.setItem(4,compass);
	}
	public int getDistanceToWinner(){
		return winnerIndex-(currentIndex+5);
	}

	@Override
	public void click(Player player, InventoryClickEvent e) {
		if (e.getCurrentItem().isSimilar(exit)){
			award(player);
		}
		return;
	}
	public void award(Player p){
		if (p == null){
			return;
		}
		if (playerWantsOut) {
			p.closeInventory();
			return;
		}
		playerWantsOut = true;
		PlayerRelated.addItemToPlayerInventory(p,getWinner());
		if (winnerPercentage < 0.02)
			Bukkit.broadcastMessage(ColorUtil.text2Color(
					GeneralMessages.LOGO.toString()+"&cGracz &6"+p.getName()+" &cotworzyl skrzynie i\n"
					+"&cWygrał "+ ItemStackUtil.getItemStackName(getWinner()) +" &b"
							+ getWinner().getAmount() + "x"
			));
		OpenerManager.setPlayerOpener(p,null);
		p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1.0f,1.0f);
	}
	public ItemStack getWinner(){
		return items[winnerIndex];
	}
	public int getWinnerIndex(){
		return winnerIndex;
	}
	public int getCurrentIndex(){
		return currentIndex;
	}
	public boolean doABarrelRoll(Player p){
		if (playerWantsOut) {
			return true;
		}
		if (getDistanceToWinner() <= 0) {
			award(p);
		}
		currentIndex++;
		if (currentIndex >= items.length)
			currentIndex = 0;
		for (int i=0;i<9;i++){
			int index = currentIndex;
			if ((index+i) >= items.length){
				index -= items.length;
			}
			index += i;
			inv.setItem(i+9,items[index]);
		}
		p.updateInventory();
		p.playSound(p.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1.0f,(float)(getDistanceToWinner()%items.length)/10f);
		return playerWantsOut;
	}
	@Override
	public void onClose(Player p){
		if (playerWantsOut)
			award(p);
		return;
	}
}
