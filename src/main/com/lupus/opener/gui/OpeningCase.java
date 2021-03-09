package com.lupus.opener.gui;


import com.lupus.gui.GUI;
import com.lupus.gui.utils.ItemUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.gui.utils.nbt.InventoryUtility;
import com.lupus.opener.managers.OpenerManager;
import com.lupus.opener.messages.GeneralMessages;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpeningCase extends GUI {
	int winnerIndex;
	ItemStack[] items;
	int currentIndex;
	ItemStack exit;
	boolean playerWantsOut;
	float winnerPercentage;
	static Firework f;
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

		exit = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta meta = exit.getItemMeta();
		meta.setDisplayName(Message.CASE_OPENING_BACKGROUND_GLASS_NAME.toString());

		String[] messages = Message.CASE_OPENING_BACKGROUND_GLASS_LORE.toString().split("\\n");
		List<String> lore = new ArrayList<>(Arrays.asList(messages));

		meta.setLore(lore);
		exit.setItemMeta(meta);

		ItemStack pointer = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
		ItemUtility.setItemTitle(pointer,Message.CASE_OPENING_POINTER_GLASS_NAME.toString());
		messages = Message.CASE_OPENING_POINTER_GLASS_LORE.toString().split("\\n");
		lore = new ArrayList<>(Arrays.asList(messages));

		ItemUtility.setItemLore(pointer,lore);

		pointer.setItemMeta(meta);

		for (int i=0;i<9;i++){
			inv.setItem(i,exit);
		}
		for (int i=18;i<27;i++){
			inv.setItem(i,exit);
		}

		inv.setItem(22, pointer);
		inv.setItem(4, pointer);
	}
	public int getDistanceToWinner(){
		return winnerIndex-(currentIndex+5);
	}

	@Override
	public void click(Player player, InventoryClickEvent e) {
		if(e.getCurrentItem() == null)
			return;
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
			removePlayerCaseOpening(p);
			p.closeInventory();
			return;
		}
		playerWantsOut = true;
		InventoryUtility.addItemStackToPlayerInventory(p,getWinner());
		if (winnerPercentage < 0.01){
			setUpFireWork(p);
			f.detonate();
			var mrq = new MessageReplaceQuery().
					addQuery("player",p.getName()).
					addQuery("item_name",ItemUtility.getItemName(getWinner())).
					addQuery("amount",getWinner().getAmount()+"");

			Bukkit.broadcastMessage(Message.WINNER_MESSAGE.toString(mrq));
		}
		OpenerManager.setPlayerOpener(p,null);
		p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1.0f,1.0f);
	}
	static void setUpFireWork(Player p){

		f = (Firework) p.getWorld().spawn(p.getLocation().add(0,2,0), Firework.class);
		FireworkMeta meta = f.getFireworkMeta();
		meta.addEffect(FireworkEffect.builder().flicker(true).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).build());
		meta.addEffect(FireworkEffect.builder().flicker(true).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).build());
		meta.addEffect(FireworkEffect.builder().flicker(true).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.AQUA).build());
		meta.addEffect(FireworkEffect.builder().flicker(true).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.YELLOW).build());
		try {
			Class<?> entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
			Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
			Object firework = craftFireworkClass.cast(f);
			Method handle = firework.getClass().getMethod("getHandle");
			Object entityFirework = handle.invoke(firework);
			Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
			Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
			ticksFlown.setAccessible(true);
			ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
			ticksFlown.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	static Class<?> getClass(String prefix, String nmsClassString) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String name = prefix + version + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
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
	public void removePlayerCaseOpening(Player p){
		if(OpenerManager.getPlayerOpeningCase(p) != null)
			OpenerManager.setPlayerOpener(p,null);
	}
	public boolean doABarrelRoll(Player p){

		if (playerWantsOut) {
			removePlayerCaseOpening(p);
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
		if (!playerWantsOut)
			award(p);
		removePlayerCaseOpening(p);
	}
}
