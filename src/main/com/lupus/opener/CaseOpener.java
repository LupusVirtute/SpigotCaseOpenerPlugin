package com.lupus.opener;

import com.lupus.opener.chests.CaseItem;
import com.lupus.opener.chests.CaseItemHolder;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.chests.PlayerKey;
import com.lupus.opener.listeners.BlockManipulationListener;
import com.lupus.opener.listeners.PvEListener;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.File;

@Plugin(name="LupusCaseOpener", version="1.0-SNAPSHOT")
@Description(value = "Simple case opener")
@Author(value = "LupusVirtute")
@Website(value = "github.com/PuccyDestroyerxXx")

@Dependency(value = "Vault")
@Dependency(value = "LupusCommandFramework")
@Dependency(value = "MCGUIFramework")
@Dependency(value = "LuckPerms")
@ApiVersion(value =  ApiVersion.Target.v1_15)

/*@Commands({
	@org.bukkit.plugin.java.annotation.command.Command(name = "case",desc = "admin case manager",permission = "case.admin"),
	@org.bukkit.plugin.java.annotation.command.Command(name = "skrzynki",desc = "player case command",permission = "case.player"),
	@org.bukkit.plugin.java.annotation.command.Command(name = "klucze",desc = "player key command",permission = "case.player"),
	@org.bukkit.plugin.java.annotation.command.Command(name = "dajklucz",desc = "player give key command",permission = "case.player"),
	@org.bukkit.plugin.java.annotation.command.Command(name = "kupklucz",desc = "player give key command",permission = "case.player"),
	@org.bukkit.plugin.java.annotation.command.Command(name = "zamienklucz",desc = "player give key command",permission = "case.player"),
})*/
public class CaseOpener extends JavaPlugin {
	static File dataFolder;
	static JavaPlugin plugin;
	static LuckPerms api;
	private static Economy econ = null;
	public static File getMainDataFolder(){
		return dataFolder;
	}
	public static LuckPerms getLuckPermsAPI(){
		return api;
	}

	public static JavaPlugin getMainPlugin() {
		return plugin;
	}
	public static Economy getEconomy(){
		return econ;
	}

	@Override
	public void onEnable() {
		info("Loading CaseOpener");
		plugin = this;
		dataFolder = this.getDataFolder();
		info("Hooking into vault");
		if (!setupEconomy() ) {
			getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		info("Hooked into Vault successfully");
		info("Setting up luck perms");
		setupLuckPerms();
		info("Luck Perms API set up");

		info("Loading serialized classes");
		loadSerializedClasses();
		info("Serialized classes loaded");

		info("Loading messages");
		Message.load();
		info("All messages loaded!");

		getServer().getPluginManager().registerEvents(new BlockManipulationListener(),plugin);
		getServer().getPluginManager().registerEvents(new PvEListener(),plugin);

		info("Started Loading Chests");
		loadChests();
		info("Chests loaded");
		info("amount:"+ChestManager.getAll().size());

	}
	public static void info(String info){
		Bukkit.getLogger().info(info);
	}
	@Override
	public void onDisable(){
		ChestManager.saveAll(false);
	}
	public static void loadChests() {
		ChestManager.clear();
		File chestDir = new File(dataFolder+"/chests");
		info(chestDir.getPath());
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
	void setupLuckPerms(){
		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (provider != null) {
			api = provider.getProvider();
		}
	}
/*
	public ILupusCommand[] lupusCommands = {
		new CaseCMD(),
		new PlayerSupCommand(),
		new KeyTransactionCMD(),
		new KeysCMD(),
		new BuyKeyCMD(),
		new ChangeKeyCMD(),
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
	*/
}
