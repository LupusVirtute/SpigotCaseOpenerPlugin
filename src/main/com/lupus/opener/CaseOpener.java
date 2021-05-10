package com.lupus.opener;

import com.lupus.command.framework.commands.arguments.ArgumentType;
import com.lupus.gui.utils.ItemUtility;
import com.lupus.gui.utils.NBTUtility;
import com.lupus.opener.chests.CaseItem;
import com.lupus.opener.chests.CaseItemHolder;
import com.lupus.opener.chests.MinecraftCase;
import com.lupus.opener.chests.PlayerKey;
import com.lupus.opener.listeners.BlockManipulationListener;
import com.lupus.opener.listeners.InventoryListener;
import com.lupus.opener.listeners.PvEListener;
import com.lupus.opener.managers.ChestManager;
import com.lupus.opener.messages.Message;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.File;
import java.util.Arrays;

@Plugin(name="LupusCaseOpener", version="1.0-SNAPSHOT")
@Description(value = "Simple case opener")
@Author(value = "LupusVirtute")
@Website(value = "github.com/PuccyDestroyerxXx")

@Dependency(value = "Vault")
@Dependency(value = "LupusCommandFramework")
@Dependency(value = "MCGUIFramework")
@Dependency(value = "LuckPerms")
@ApiVersion(value =  ApiVersion.Target.v1_15)

public class CaseOpener extends JavaPlugin {
	static File dataFolder;
	static JavaPlugin plugin;
	static LuckPerms api;
	private static Economy econ = null;
	public static File getMainDataFolder(){
		return dataFolder;
	}
	public static LuckPerms getLuckPermsAPI(){
		if (api == null)
			api = LuckPermsProvider.get();
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
		getServer().getPluginManager().registerEvents(new InventoryListener(),plugin);

		info("Started Loading Chests");
		loadChests();
		info("Chests loaded");
		info("amount:"+ChestManager.getAll().size());
		ArgumentType.addArgumentTypeInterpreter(new ArgumentType(MinecraftCase.class,(arg)-> ChestManager.getCase(arg[0])));
	}
	public static NamespacedKey NamespacedKey(String key){
		return new NamespacedKey(getMainPlugin(),key);
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
			MinecraftCase minecraftCase = (MinecraftCase) file.get("Chest");

			if (minecraftCase != null) {
				ChestManager.addCase(minecraftCase);
				minecraftCase.forceTopUpdate(true);
			}

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
}
