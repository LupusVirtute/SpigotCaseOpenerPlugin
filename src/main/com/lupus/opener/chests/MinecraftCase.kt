package com.lupus.opener.chests;

import com.lupus.gui.utils.ItemUtility;
import com.lupus.gui.utils.NBTUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.gui.utils.InventoryUtility;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.utils.MinecraftCaseUtils;
import com.lupus.opener.gui.CaseItemList;
import com.lupus.opener.gui.OpeningCase;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.managers.OpenerManager;
import com.lupus.opener.messages.Message;
import com.lupus.opener.messages.MessageReplaceQuery;
import com.lupus.opener.runnables.ChestOpener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

public class MinecraftCase implements ConfigurationSerializable {
	Map<UUID,Integer> keys;
	List<Location> chests = new ArrayList<>();
	HashMap<UUID,Integer> keyRedeemMap = new HashMap<>();
	CaseItemHolder dropTable;
	ItemStack icon;

	String name;
	String officialName;
	int totalAmountOfKeys = 0;
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
			if (map.containsKey("cobblexLocations")){
				cobblexLocations = (List<Location>) map.get("cobblexLocations");
			}
			if (map.containsKey("registeredCobblex")){
				var tempStringList =  (List<String>) map.get("registeredCobblex");
				registeredCobblex = new ArrayList<>();
				for (String s : tempStringList) {
					registeredCobblex.add(UUID.fromString(s));
				}
			}
			if(map.containsKey("dropTable")){
				dropTable = (CaseItemHolder)map.get("dropTable");
			}else{
				dropTable = new CaseItemHolder(new ArrayList<>());
			}
			if (map.containsKey("icon")){
				Object icon = map.get("icon");
				if (icon instanceof ItemStack)
					this.icon = (ItemStack) icon;
				else
					this.icon = new ItemStack(Material.CHEST);
			}else{
				icon = new ItemStack(Material.CHEST);
			}
			if(map.containsKey("keys")){
				keys = new TreeMap<>();
				if (map.get("keys") instanceof List) {
					List<PlayerKey> tempKeys = (List<PlayerKey>) map.get("keys");
					for (PlayerKey tempKey : tempKeys) {
						keys.put(tempKey.player,tempKey.amount);
						totalAmountOfKeys += tempKey.amount;
					}
				}
			}else{
				keys = new TreeMap<>();
			}
			if(map.containsKey("keyRedeemMap")) {
				Object encapsulated = map.get("keyRedeemMap");
				if (encapsulated instanceof List){
					List<PlayerKey> list = (List<PlayerKey>) encapsulated;
					for (PlayerKey playerKey : list) {
						keyRedeemMap.put(playerKey.player,playerKey.amount);
					}
				}
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
		this.icon = new ItemStack(Material.CHEST);
		keys = new TreeMap<>();
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
		forceOpen(player);
	}
	public void forceOpen(Player player){
		var opener = OpenerManager.getPlayerOpeningCase(player);
		if (opener != null)
			opener.award(player);
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
		return hasKey(p.getUniqueId());
	}
	public boolean hasKey(UUID player){
		if (!keys.containsKey(player)) {
			keys.put(player,0);
			return false;
		}
		int amount = keys.get(player);
		return amount > 0;
	}
	public int getKeyAmount(Player p){
		return getKeyAmount(p.getUniqueId());
	}
	public int getKeyAmount(UUID player){
		if (!keys.containsKey(player)) {
			keys.put(player,0);
		}
		return keys.get(player);
	}
	public void giveKey(UUID player,int amount){
		if (!keys.containsKey(player))
			keys.put(player,0);
		keys.put(player,keys.get(player)+amount);

		totalAmountOfKeys += amount;
	}
	public void setKey(Player player,int amount){
		setKey(player.getUniqueId(),amount);
	}
	public void setKey(UUID player,int amount){
		if (!keys.containsKey(player))
			return;
		var totalKeys = keys.get(player);
		keys.put(player,amount);
		totalKeys -= amount;
		totalAmountOfKeys -= totalKeys;
	}
	public ItemStack turnKeyIntoItemStack(Player player,int amount){
		return turnKeyIntoItemStack(player.getUniqueId(),amount);
	}
	public ItemStack turnKeyIntoItemStack(UUID player,int amount) {
		if (!hasKey(player))
			return null;
		if (getKeyAmount(player) < amount)
			return null;

		removeKey(player, amount);


		ItemStack itemStack = new ItemStack(Material.TRIPWIRE_HOOK);
		var mrq = new MessageReplaceQuery().
				addQuery("case",this.getOfficialName()).
				addQuery("amount",amount+"");
		itemStack = ItemUtility.setItemTitle(itemStack,Message.ITEM_KEY_FORMAT_TEMPLATE.toString(mrq));
		itemStack = ItemUtility.setItemLore(itemStack,Message.ITEM_KEY_LORE_TEMPLATE.toString(mrq).split("\n"));

		itemStack.addUnsafeEnchantment(Enchantment.LUCK,1);

		UUID keyUUID = UUID.randomUUID();
		itemStack = NBTUtility.setNBTDataValue(itemStack,"Key",keyUUID.toString());
		itemStack = NBTUtility.setNBTDataValue(itemStack,"CaseKey",getName());
		keyRedeemMap.put(keyUUID,amount);
		return itemStack;
	}

	public int redeemKey(Player player,ItemStack item){
		return redeemKey(player.getUniqueId(),item);
	}
	public int redeemKey(UUID player,ItemStack item){
		if (item == null)
			return -1;
		if (!NBTUtility.hasNBTTag(item,"Key")) {
			return -1;
		}
		Player onlinePlayer = Bukkit.getPlayer(player);
		if (onlinePlayer == null)
			return -2;
		if (!onlinePlayer.getInventory().contains(item)) {
			return -2;
		}
		onlinePlayer.getInventory().remove(item);

		String keyStringUID = NBTUtility.getNBTValue(item,"Key",String.class);
		UUID keyUID = UUID.fromString(keyStringUID);
		Integer amount = keyRedeemMap.get(keyUID);
		if (amount == null)
			return -3;
		keyRedeemMap.remove(keyUID);
		var mrq = new MessageReplaceQuery().
				addQuery("amount",amount+"").
				addQuery("player","SYSTEM").
				addQuery("chest",this.getOfficialName());
		onlinePlayer.sendMessage(Message.KEY_SEND_SUCCESS_RECEIVER.toString(mrq));
		giveKey(player,amount);
		return amount;
	}
	public void removeKey(UUID player,int amount){
		if(keys.containsKey(player)){
			keys.put(player,keys.get(player)-amount);
		}
	}
	public void removeKey(Player p,int amount){
		removeKey(p.getUniqueId(),amount);
		totalAmountOfKeys -= amount;
	}
	public void giveKey(Player p,int amount){
		giveKey(p.getUniqueId(),amount);
	}
	public ItemStack giveCase(){
		ItemStack chest = new ItemStack(Material.CHEST);
		NBTUtility.setNBTDataValue(chest,"case",name);
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
		return TextUtility.color(officialName);
	}
	public void addItem(@NotNull ItemStack item,int weight){
		dropTable.addItem(item,weight);
	}
	public CaseItem getRandomItem(){
		return dropTable.getRandomItem().clone();
	}
	public ItemStack getItemRepresentation(Player caller){
		final DecimalFormat df2 = new DecimalFormat("#.##");
		ItemStack chest = new ItemStack(icon);
		ItemUtility.setItemTitle(
				chest,
				TextUtility.color(getOfficialName())
		);

		var mrq = new MessageReplaceQuery().
				addQuery("price",df2.format(getPrice())).
				addQuery("weight",weight+"").
				addQuery("amount",totalAmountOfKeys+"");

		String[] playerMessages = Message.CHEST_LIST_PLAYER_LORE.toString(mrq).split("\\n");
		List<String> lore = new ArrayList<>(Arrays.asList(playerMessages));
		if (caller != null)
			addAdminInfo(caller,mrq,lore);
		ItemUtility.setItemLore(chest,lore);
		return chest;
	}
	private void addAdminInfo(Player caller,MessageReplaceQuery mrq,List<String> lore){
		if (caller.hasPermission("case.admin")){
			String[] adminMessages = Message.CHEST_LIST_ADMIN_LORE.toString(mrq).split("\\n");
			lore.addAll(Arrays.asList(adminMessages));
		}
	}
	public void setIcon(ItemStack itemStack){
		if (itemStack == null)
			return;
		icon = itemStack;
	}
	List<UUID> registeredCobblex = new ArrayList<>();
	List<Location> cobblexLocations = new ArrayList<>();
	public ItemStack getCobblex(){
		ItemStack itemStack = new ItemStack(Material.MOSSY_COBBLESTONE);
		ItemUtility.setItemTitleAndLore(itemStack,
				Message.COBBLEX_TITLE.toString(),
				Arrays.asList(Message.COBBLEX_LORE.toString().split("\n"))
		);
		NBTUtility.setNBTDataValue(itemStack,"Cobblex",this.getName());
		var uid = UUID.randomUUID();
		registeredCobblex.add(uid);
		NBTUtility.setNBTDataValue(itemStack,"CobblexUID",uid.toString());
		return itemStack;
	}
	public void putDownCobblex(ItemStack itemStack,Location location){
		var uidString = NBTUtility.getNBTValue(itemStack,"CobblexUID",String.class);
		if (uidString == null)
			return;
		UUID uuid = UUID.fromString(uidString);
		if (!registeredCobblex.contains(uuid)) {
			StringBuilder builder = new StringBuilder();
			builder.append(location.getX()).
					append(' ').
					append(location.getY()).
					append(' ').
					append(location.getZ());
			Bukkit.broadcast(builder.toString()+" XYZ - Prawdopodobne Kopiowanie Cobblexa","case.moderator");
		}
		else {
			registeredCobblex.remove(uuid);
			cobblexLocations.add(location);
		}
	}
	public boolean breakCobblex(Player player,Location location){
		if (!cobblexLocations.contains(location)){
			return false;
		}
		cobblexLocations.remove(location);
		this.forceOpen(player);
		return true;
	}

	@Override
	public Map<String,Object> serialize(){
		Map<String,Object> serializedMap = new HashMap<>();
		serializedMap.put("name",name);
		serializedMap.put("officialName",officialName);
		serializedMap.put("price",price);
		serializedMap.put("weight",weight);
		serializedMap.put("icon",icon);
		List<String> stringCobblexUUID = new ArrayList<>();
		for (UUID cobblex : registeredCobblex) {
			stringCobblexUUID.add(cobblex.toString());
		}
		serializedMap.put("registeredCobblex",stringCobblexUUID);
		serializedMap.put("cobblexLocations",cobblexLocations);
		serializedMap.put("dropTable",dropTable);
		List<PlayerKey> serializedPlayerKeys = new ArrayList<>();
		for (Map.Entry<UUID,Integer> entry : keys.entrySet()){
			serializedPlayerKeys.add(
				new PlayerKey(entry.getKey(),entry.getValue())
			);
		}
		List<PlayerKey> redeemKeysMap = new ArrayList<>();
		for (Map.Entry<UUID, Integer> uuidIntegerEntry : keyRedeemMap.entrySet()) {
			redeemKeysMap.add(
					new PlayerKey(uuidIntegerEntry.getKey(),uuidIntegerEntry.getValue())
			);
		}
		serializedMap.put("keys",serializedPlayerKeys);
		serializedMap.put("locations",chests);
		serializedMap.put("keyRedeemMap",redeemKeysMap);

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
			ex.printStackTrace();
		}
	}



	//////// TOP LOGIC ////////

	List<Map.Entry<UUID,Integer>> topCache = new LinkedList<>();

	public void forceTopUpdate(boolean async){
		if (async) {
			// I know it should be somwhere else but i am fucking lazy AF
			Bukkit.getScheduler().runTaskAsynchronously(
					CaseOpener.getMainPlugin(),
					()->forceTopUpdate(false)
			);
			return;
		}
		topCache = new LinkedList<>(keys.entrySet());
		topCache.sort(MinecraftCaseUtils::sortCompare);
	}
	public List<Map.Entry<UUID,Integer>> getTopKeys(){
		if (topCache.size() <= 0) {
			forceTopUpdate(true);
			return null;
		}
		return topCache;
	}
}