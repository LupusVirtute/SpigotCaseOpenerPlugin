package com.lupus.opener.chests;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CaseItemHolder implements ConfigurationSerializable {
	public List<CaseItem> items;
	int weightMax;
	public CaseItemHolder(List<CaseItem> items){
		this.items = items;
		recalculateWeightMax();
	}
	public CaseItemHolder(Map<String,Object> map){
		if (map.containsKey("items")) {
			try{
				items = (List<CaseItem>)map.get("items");
			}
			catch(Exception ex){
				items = new ArrayList<>();
			}

		}else {
			items = new ArrayList<>();
		}
		recalculateWeightMax();
	}
	public int recalculateWeightMax(){
		weightMax = 0;
		for(int i=0;i<items.size();i++){
			weightMax += items.get(i).getWeight();
		}
		return weightMax;
	}
	public void setItemAt(CaseItem it,int index){
		if(index < items.size() && index >= 0){
			weightMax -= items.get(index).getWeight();
			items.set(index,it);
			weightMax += it.getWeight();
		}
		else
			addItem(it);
	}
	public CaseItem getItemAt(int index){
		if (items.size() <= index){
			return null;
		}
		return items.get(index);
	}

	public void addItem(@NotNull CaseItem item){
		items.add(item);
		weightMax += item.getWeight();
	}
	public void addItem(@NotNull ItemStack item,int weight){
		addItem(new CaseItem(item,weight));
		weightMax += weight;
	}
	public CaseItem[] getItems(){
		CaseItem[] caseItems = new CaseItem[items.size()];
		caseItems = items.toArray(caseItems);
		return caseItems;
	}
	public int getMaxWeight(){
		return weightMax;
	}
	public int getItemCount(){
		return items.size();
	}
	public CaseItem getRandomItem() {
		if(items.size() == 0){
			return null;
		}
		Random rnd = new Random();
		int chance = rnd.nextInt(weightMax);
		CaseItem stack = null;
		for(int i=0;i<items.size();i++){
			chance -= items.get(i).getWeight();
			if(chance < 0){
				stack = new CaseItem(items.get(i));
				break;
			}
		}
		chance = rnd.nextInt(100);
		if (chance < 10 && stack != null){
			ItemStack itemStack = CaseItem.addStarTrack(stack.getItem());
			stack.setItem(itemStack);
		}
		return stack;
	}
	@Override
	public Map<String,Object> serialize(){
		Map<String,Object> serializedMap = new HashMap<>();
		serializedMap.put("items",items);
		return serializedMap;
	}
}