package com.lupus.opener.chests;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Only for HashMap serialization
 */
public class PlayerKey implements ConfigurationSerializable {
	UUID player;
	int amount;
	public PlayerKey(Player player,int amountKeys){
		this(player.getUniqueId(),amountKeys);
	}
	public PlayerKey(UUID player,int amountKeys){
		this.player = player;
		this.amount = amountKeys;
	}
	public PlayerKey(Map<String,Object> map){
		player = UUID.fromString((String)map.get("UUID"));
		amount = (int)map.get("amount");
	}
	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = new HashMap<>();
		map.put("UUID",player.toString());
		map.put("amount",amount);
		return map;
	}
}
