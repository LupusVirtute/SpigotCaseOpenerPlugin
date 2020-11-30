package com.lupus.opener.chests;
import com.lupus.utils.ColorUtil;
import com.lupus.utils.ItemStackUtil;
import com.lupus.utils.NBTEditor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseItem implements ConfigurationSerializable,Cloneable {
	static Material[] applicableStarTracks = new Material[]{
		Material.DIAMOND_SWORD,
		Material.DIAMOND_SHOVEL,
		Material.DIAMOND_AXE,
		Material.DIAMOND_HOE,
		Material.DIAMOND_PICKAXE,
		Material.WOODEN_SWORD,
		Material.WOODEN_AXE,
		Material.WOODEN_SHOVEL,
		Material.WOODEN_HOE,
		Material.WOODEN_PICKAXE,
		Material.GOLDEN_SWORD,
		Material.GOLDEN_SHOVEL,
		Material.GOLDEN_AXE,
		Material.GOLDEN_HOE,
		Material.GOLDEN_PICKAXE,
		Material.IRON_SWORD,
		Material.IRON_SHOVEL,
		Material.IRON_AXE,
		Material.IRON_HOE,
		Material.IRON_PICKAXE,
		Material.STONE_SWORD,
		Material.STONE_SHOVEL,
		Material.STONE_AXE,
		Material.STONE_HOE,
		Material.STONE_PICKAXE,

	};

	@Override
	protected CaseItem clone() {
		return new CaseItem(this);
	}

	private ItemStack item;
	private final int weight;
	public CaseItem(CaseItem item)
	{
		weight = item.getWeight();
		this.item = new ItemStack(item.getItem());
	}
	public CaseItem(@NotNull ItemStack item,int weight){
		this.item = item;
		this.weight = weight;
	}
	public CaseItem(Map<String,Object> map){
		if (map.containsKey("item")) {
			item = (ItemStack)map.get("item");
			
		}
		else 
			new ItemStack(Material.STICK);

		if (map.containsKey("weight")) {
			weight = (int)map.get("weight");
		}
		else
			weight = 0;
	}

	public static ItemStack addStarTrack(ItemStack starTrack){
		Material starTrackMat = starTrack.getType();
		boolean applicable = false;
		for (int i=0;i<applicableStarTracks.length;i++){
			if (applicableStarTracks[i].equals(starTrackMat)){
				applicable = true;
				break;
			}
		}
		if (!applicable)
			return starTrack;
		starTrack = new ItemStack(starTrack);
		starTrack = NBTEditor.set(starTrack,0,"StarKiller");
		ItemMeta meta = starTrack.getItemMeta();
		meta.setDisplayName(ColorUtil.text2Color(ItemStackUtil.getItemStackName(starTrack)+" &4&lStarKiller"));
		List<String> lore = meta.getLore();
		if (lore == null) {
			lore = new ArrayList<>();
		}
		lore.add(ColorUtil.text2Color("&b&lStarKiller"));
		lore.add(ColorUtil.text2Color("&cKills : &40"));
		meta.setLore(lore);
		starTrack.setItemMeta(meta);
		return starTrack;
	}
	public ItemStack getItem(){
		return new ItemStack(item);
	}
	public int getWeight(){
		return weight;
	}
	public void setItem(@NotNull ItemStack item){
		this.item = item.clone();
	}
	@Override
	public Map<String,Object> serialize(){
		Map<String,Object> serializedMap = new HashMap<>();
		serializedMap.put("item",item);
		serializedMap.put("weight",weight);
		return serializedMap;
	}
}