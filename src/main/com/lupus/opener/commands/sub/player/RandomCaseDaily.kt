package com.lupus.opener.commands.sub.player;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.command.framework.commands.arguments.ArgumentList;
import com.lupus.opener.CaseOpener;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.managers.ChestManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.bukkit.ChatColor.RED;

public class RandomCaseDaily extends PlayerCommand {
	static CommandMeta meta = new CommandMeta().
			setName("dajskrzynie").
			setUsage(usage("/dajskrzynie")).
			setDescription(colorText("&6Dostajesz darmową skrzynie")).
			addPermission("case.freecase");

	public RandomCaseDaily() {
		super(meta);
	}

	private static final long DAY = 1000*60*60*24;
								//   MS  M  H  D
	@Override
	protected void run(Player player, ArgumentList argumentList) throws Exception {
		List<MetadataValue> meta = player.getMetadata("freecase");
		if (meta == null || meta.size() <= 0){
			FixedMetadataValue metaValue = new FixedMetadataValue(CaseOpener.getMainPlugin(),0L);
			player.setMetadata("freecase",metaValue);
			if (meta == null)
				meta = new ArrayList<>();
			meta.add(metaValue);
		}
		var currentTimestamp = Instant.now().toEpochMilli();
		var metaValue = meta.get(0);
		if (metaValue == null) {
			return;
		}
		var epochTime = metaValue.asLong();
		if (epochTime + DAY <= currentTimestamp){
			reward(player,currentTimestamp);
			player.sendMessage(RED+"Dostałeś skrzynie gratulacje!");
		}else{
			player.sendMessage(RED+"Już dzisiaj dostałeś Skrzynie");
		}

	}
	void reward(Player player,long epochTimeStamp){
		player.setMetadata("freecase",new FixedMetadataValue(CaseOpener.getMainPlugin(),epochTimeStamp));
		var res = ChestManager.getAllCases();
		MinecraftCase chosenOne = null;
		int highestWeight = 0;
		for (MinecraftCase re : res) {
			if (highestWeight < re.getCaseWeight())
				highestWeight = re.getCaseWeight();
		}
		Random rnd = new Random();
		int random = rnd.nextInt(highestWeight);
		for (MinecraftCase re : res) {
			if (random <= re.getCaseWeight()){
				chosenOne = re;
				break;
			}
		}
		player.sendMessage(Component.text(chosenOne.getOfficialName()));
		chosenOne.giveKey(player,1);
	}
}
