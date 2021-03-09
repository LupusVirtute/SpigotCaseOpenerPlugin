package com.lupus.opener.chests;

import com.lupus.gui.utils.NBTUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.gui.utils.nbt.InventoryUtility;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.gui.CaseItemList;
import com.lupus.opener.gui.OpeningCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.managers.OpenerManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.runnables.ChestOpener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class MinecraftCase implements ConfigurationSerializable {
	HashMap<UUID,Integer> keys;
	List<Location> chests = new ArrayList<>();
	CaseItemHolder dropTable;
	Material icon;

	String name;
	String officialName;

	double price;
	int weight;

	public MinecraftCase(Map<String,Object> map)
	{
			if(map.containsKey("name")){
				name = (String) map.get("name");
			}
			if(map.containsKey("officialName")){
				officialName = (String) map.get("officialName");
			}
			if(map.containsKey("price")){
				price = (double) map.get("price");
			}
			if(map.containsKey("weight")){
				weight = (int) map.get("weight");
			}
			if(map.containsKey("dropTable")){
				dropTable = (CaseItemHolder)map.get("dropTable");
			}else{
				dropTable = new CaseItemHolder(new ArrayList<>());
			}
			if (map.containsKey("icon")){
				icon = Material.getMaterial((String)map.get("icon"));
			}else{
				icon = Material.CHEST;
			}
			if(map.containsKey("keys")){
				keys = new HashMap<>();
				if (map.get("keys") instanceof List) {
					List<PlayerKey> tempKeys = (List<PlayerKey>) map.get("keys");
					for (PlayerKey tempKey : tempKeys) {
						keys.put(tempKey.player,tempKey.amount);
					}
				}
			}else{
				keys = new HashMap<>();
			}
			if(map.containsKey("locations")){
				Object obj = map.get("locations");
				if (obj instanceof List) {
					chests = (List<Location>)obj;
					if (chests == null)
						chests = new ArrayList<>();
					for (int i=0;i<chests.size() ;i++ ) {
							ChestManager.addCaseLocation(chests.get(i),name);
						}
				}
			}else{
				chests = new ArrayList<>();
			}
	}

	public MinecraftCase(
			String name,
			String officialName,
			float price,
			int weight,
			CaseItemHolder holder) {

		this.dropTable = holder;
		this.name = name;
		this.officialName = officialName;
		this.price = price;
		this.weight = weight;
		this.icon = Material.CHEST;
		keys = new HashMap<>();
		chests = new ArrayList<>();
	}
	public boolean openCaseEditor(Player player) {
		player.closeInventory();
		CaseItemList caseItemList = new CaseItemList(this,player);
		player.openInventory(caseItemList.getInventory());
		return true;
	}
	public void openCase(Player player,int amount){
		if (!hasKey(player)){
			player.sendMessage(Message.NO_KEY.toString());
			return;
		}
		if (amount > 1){
			if (getKeyAmount(player) < amount) {
				player.sendMessage(Message.NOT_ENOUGH_KEYS.toString());
				return;
			}
			removeKey(player, amount);
			for (int i=0;i<amount;i++){
				InventoryUtility.addItemStackToPlayerInventory(player,getRandomItem().getItem());
			}
			return;
		}
		if (OpenerManager.getPlayerOpeningCase(player) != null){
			player.sendMessage(Message.ALERADY_OPENING.toString());
			return;
		}

		removeKey(player,1);
		Random rnd = new Random();
		ItemStack[] itms = new ItemStack[rnd.nextInt(55)+30];
		for (int i=0,j=itms.length-1;i<j;i++){
			itms[i] = new ItemStack(getRandomItem().getItem());
		}
		CaseItem winner =  new CaseItem(getRandomItem());
		itms[itms.length-1] = winner.getItem().clone();
		int weightMax = dropTable.recalculateWeightMax();

		float winnerPercentage = ((float)winner.getWeight())/((float)weightMax);
		OpeningCase mcCase = new OpeningCase(TextUtility.color(officialName),itms,itms.length-1,winnerPercentage);

		OpenerManager.setPlayerOpener(player,mcCase);
		player.openInventory(mcCase.getInventory());

		new ChestOpener(mcCase,player).runTask(CaseOpener.getMainPlugin());
	}
	public String getName(){
		return name;
	}
	public int getCaseWeight(){
		return weight;
	}
	public double getPrice(){
		return price;
	}
	public boolean hasKey(Player p){
		if (!keys.containsKey(p.getUniqueId())) {
			keys.put(p.getUniqueId(),0);
			return false;
		}
		int amount = keys.get(p.getUniqueId());
		return amount > 0;
	}
	public int getKeyAmount(Player p){
		if (!keys.containsKey(p.getUniqueId())) {
			keys.put(p.getUniqueId(),0);
		}
		return keys.get(p.getUniqueId());
	}
	public void giveKey(UUID player,int amount){
		if (keys.containsKey(player))
			keys.put(player,keys.get(player)+amount);
		else
			keys.put(player,amount);
	}
	public void removeKey(UUID player,int amount){
		if(keys.containsKey(player)){
			keys.put(player,keys.get(player)-amount);
		}
	}
	public void removeKey(Player p,int amount){
		removeKey(p.getUniqueId(),amount);
	}
	public void giveKey(Player p,int amount){
		giveKey(p.getUniqueId(),amount);
	}
	public ItemStack giveCase(){
		ItemStack chest = new ItemStack(Material.CHEST);
		chest = NBTUtility.setNBTDataValue(chest,"case",name);
		return chest;
	}
	public void addChestLocation(Location location){
		ChestManager.addCaseLocation(location,name);
		if (chests == null)
			chests = new ArrayList<>();
		chests.add(location);
	}
	public CaseItemHolder getDropTable(){return dropTable;}
	public CaseItem[] getItems(){
		return dropTable.getItems();
	}
	public void setItemAt(CaseItem it, int index){
		dropTable.setItemAt(it,index);
	}
	public void setOfficialName(@NotNull String officialName) {
		this.officialName = officialName;
	}
	public String getOfficialName(){
		return officialName;
	}
	public void addItem(@NotNull ItemStack item,int weight){
		dropTable.addItem(item,weight);
	}
	public CaseItem getRandomItem(){
		return dropTable.getRandomItem().clone();
	}
	@Override
	public Map<String,Object> serialize(){
		Map<String,Object> serializedMap = new HashMap<>();
		serializedMap.put("name",name);
		serializedMap.put("officialName",officialName);
		serializedMap.put("price",price);
		serializedMap.put("weight",weight);
		serializedMap.put("icon",icon.name());

		serializedMap.put("dropTable",dropTable);
		List<PlayerKey> serializedPlayerKeys = new ArrayList<>();
		for (Map.Entry<UUID,Integer> entry : keys.entrySet()){
			serializedPlayerKeys.add(
				new PlayerKey(entry.getKey(),entry.getValue())
			);
		}
		serializedMap.put("keys",serializedPlayerKeys);
		serializedMap.put("locations",chests);

		return serializedMap;
	}
	public void setWeight(int weight){
		if (weight < 0)
			return;
		this.weight = weight;
	}
	public void save(){
		File chestFile = new File(CaseOpener.getMainDataFolder() + "/chests/" + name + ".yml");
		if (!chestFile.exists()) {
			try {
				chestFile.getParentFile().mkdir();
				chestFile.createNewFile();
			}
			catch(Exception ex){
				return;
			}
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(chestFile);
		config.set("Chest",null);
		config.set("Chest",this);
		try {
			config.save(chestFile);
		}catch(Exception ex){
			Bukkit.getLogger().info(ex.toString());
		}
	}
}