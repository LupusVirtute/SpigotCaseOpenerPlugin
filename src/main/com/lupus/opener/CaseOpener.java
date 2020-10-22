package com.lupus.opener;

import com.lupus.command.framework.commands.ILupusCommand;
import com.lupus.opener.chests.CaseItem;
import com.lupus.opener.chests.CaseItemHolder;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.chests.PlayerKey;
import com.lupus.opener.commands.CaseCMD;
import com.lupus.opener.commands.PlayerSupCommand;
import com.lupus.opener.commands.sub.player.BuyKeyCMD;
import com.lupus.opener.commands.sub.player.KeyTransactionCMD;
import com.lupus.opener.commands.sub.player.KeysCMD;
import com.lupus.opener.listeners.BlockManipulationListener;
import com.lupus.opener.managers.ChestManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.File;

@Plugin(name="LupusCaseOpener", version="1.0-SNAPSHOT")
@Description(desc = "Simple case opener")
@Author(name = "LupusVirtute")
@Website(url="github.com/PuccyDestroyerxXx")

@Dependency(plugin = "Vault")
@Dependency(plugin = "LupusCommandFramework")
@Dependency(plugin = "MCGUIFramework")
@Dependency(plugin = "LupusUtils")
@Dependency(plugin = "LupusDrop")

@Commands({
	@org.bukkit.plugin.java.annotation.command.Command(name = "case",desc = "admin case manager",permission = "case.admin"),
	@org.bukkit.plugin.java.annotation.command.Command(name = "skrzynki",desc = "player case command",permission = "case.player"),
	@org.bukkit.plugin.java.annotation.command.Command(name = "klucze",desc = "player key command",permission = "case.player"),
	@org.bukkit.plugin.java.annotation.command.Command(name = "dajklucz",desc = "player give key command",permission = "case.player")
})
public class CaseOpener extends JavaPlugin {
	static File dataFolder;
	static JavaPlugin plugin;
	private static Economy econ = null;
	public static File getMainDataFolder(){
		return dataFolder;
	}

	public static JavaPlugin getMainPlugin() {
		return plugin;
	}
	public static Economy getEconomy(){
		return econ;
	}

	@Override
	public void onEnable() {
		Bukkit.getLogger().info("Loading CaseOpener");
		if (!setupEconomy() ) {
			getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		loadSerializedClasses();
		Bukkit.getLogger().info("Hooked into Vault");
		plugin = this;
		dataFolder = this.getDataFolder();
		getServer().getPluginManager().registerEvents(new BlockManipulationListener(),plugin);
		Bukkit.getLogger().info("Started Loading Chests");
		loadChests();
		Bukkit.getLogger().info("Chests loaded");
		Bukkit.getLogger().info("amount:"+ChestManager.getAll().size());
	}
	@Override
	public void onDisable(){
		ChestManager.saveAll(false);
	}
	public static void loadChests() {
		File chestDir = new File(dataFolder+"/chests");
		Bukkit.getLogger().info(chestDir.getPath());
		File[] chestFiles = chestDir.listFiles();
		if(chestFiles == null)
			return;
		for (File chestFile : chestFiles) {
			FileConfiguration file = YamlConfiguration.loadConfiguration(chestFile);
			MinecraftCase minecraftCase = (MinecraftCase)file.get("Chest");
			ChestManager.addCase(minecraftCase);
		}
	}
	private void loadSerializedClasses() {
		ConfigurationSerialization.registerClass(MinecraftCase.class);
		ConfigurationSerialization.registerClass(CaseItemHolder.class);
		ConfigurationSerialization.registerClass(CaseItem.class);
		ConfigurationSerialization.registerClass(PlayerKey.class);
	}
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	public ILupusCommand[] lupusCommands = {
		new CaseCMD(),
		new PlayerSupCommand(),
		new KeyTransactionCMD(),
		new KeysCMD(),
		new BuyKeyCMD(),
	};
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmd = command.getName().toLowerCase();
		for (ILupusCommand lupusCommand : lupusCommands) {
			if (lupusCommand.isMatch(cmd)) {
				lupusCommand.execute(sender, args);
				break;
			}
		}
		return super.onCommand(sender, command, label, args);
	}
}
