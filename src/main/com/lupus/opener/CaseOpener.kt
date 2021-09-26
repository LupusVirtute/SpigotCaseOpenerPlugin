package com.lupus.opener

import com.lupus.gui.TopPyramidGUI
import com.lupus.opener.chests.MinecraftCase
import java.util.UUID
import com.lupus.gui.SelectableItem
import com.lupus.gui.IGUI
import com.lupus.gui.utils.SkullUtility
import com.lupus.gui.utils.ItemUtility
import java.util.Arrays
import com.lupus.opener.gui.top.GUITopCase
import com.lupus.gui.utils.TextUtility
import com.lupus.gui.PlayerSelectableItem
import com.lupus.opener.gui.ItemEditor
import com.lupus.gui.Paginator
import java.text.DecimalFormat
import com.lupus.opener.managers.ChestManager
import com.lupus.opener.gui.selectables.SelectableCase
import net.luckperms.api.LuckPerms
import com.lupus.opener.CaseOpener
import net.luckperms.api.query.QueryOptions
import net.luckperms.api.query.QueryMode
import com.lupus.opener.gui.BuyCaseGUI
import com.lupus.opener.gui.selectables.SelectableCommand
import com.lupus.gui.GUI
import com.lupus.opener.gui.BuyKeysCMD
import com.lupus.gui.utils.InventoryUtility
import com.lupus.opener.chests.CaseItem
import com.lupus.opener.gui.selectables.SelectableTop
import com.lupus.opener.gui.TopKeysGUI
import com.lupus.opener.gui.OpeningCase
import com.lupus.opener.managers.OpenerManager
import com.lupus.opener.chests.CaseItemHolder
import com.lupus.opener.gui.selectables.SelectableItemEditor
import com.lupus.opener.chests.utils.MinecraftCaseUtils
import com.lupus.gui.utils.NBTUtility
import java.util.HashMap
import java.util.TreeMap
import com.lupus.opener.chests.PlayerKey
import com.lupus.opener.gui.CaseItemList
import com.lupus.opener.runnables.ChestOpener
import java.lang.StringBuilder
import java.util.LinkedList
import java.lang.Runnable
import com.lupus.command.framework.commands.PlayerCommand
import com.lupus.opener.commands.sub.admin.GetCaseCMD
import kotlin.Throws
import com.lupus.command.framework.commands.LupusCommand
import com.lupus.command.framework.commands.CommandMeta
import com.lupus.opener.commands.sub.admin.GiveKeyCMD
import com.lupus.opener.commands.sub.admin.SetIconCMD
import com.lupus.opener.commands.sub.admin.OpenCaseCMD
import com.lupus.opener.commands.sub.admin.ReloadAllCMD
import com.lupus.opener.commands.sub.admin.RemoveKeyCMD
import com.lupus.opener.commands.sub.admin.SaveCasesCMD
import com.lupus.opener.commands.sub.admin.EditWeightCMD
import com.lupus.opener.commands.sub.admin.GetCobblexCMD
import com.lupus.opener.commands.sub.admin.OpenEditorCMD
import com.lupus.opener.gui.ChestList
import com.lupus.opener.commands.sub.admin.ResetAccountCMD
import com.lupus.opener.commands.sub.admin.CreateNewCaseCMD
import com.lupus.opener.commands.sub.admin.AllowDestructionCMD
import com.lupus.opener.listeners.BlockManipulationListener
import com.lupus.opener.commands.sub.admin.SetStatTrackCommand
import com.lupus.opener.commands.sub.player.KeysCMD
import com.lupus.opener.chests.MinecraftKey
import com.lupus.opener.commands.sub.player.BuyKeyCMD
import com.lupus.opener.commands.sub.player.KeyTopCMD
import com.lupus.opener.commands.sub.player.ChangeKeyCMD
import com.lupus.opener.commands.sub.player.RandomCaseDaily
import java.time.Instant
import com.lupus.opener.commands.sub.player.GetCraftedCobblex
import com.lupus.opener.commands.sub.player.KeyTransactionCMD
import com.lupus.command.framework.commands.arguments.UInteger
import com.lupus.opener.commands.sub.player.WithdrawKeyCommand
import java.lang.IllegalArgumentException
import java.util.HashSet
import com.lupus.command.framework.commands.SupCommand
import com.lupus.command.framework.commands.PlayerSupCommand
import com.lupus.opener.commands.PlayerCaseCommand
import com.lupus.opener.runnables.ChestSave
import com.lupus.gui.utils.ConfigUtility
import org.bukkit.plugin.java.annotation.plugin.author.Author
import org.bukkit.plugin.java.annotation.plugin.Website
import org.bukkit.plugin.java.annotation.plugin.ApiVersion
import com.lupus.opener.listeners.PvEListener
import com.lupus.opener.listeners.InventoryListener
import com.lupus.command.framework.commands.arguments.ArgumentRunner
import com.lupus.command.framework.commands.arguments.ArgumentType
import com.lupus.opener.messages.Message
import net.milkbowl.vault.economy.Economy
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.annotation.dependency.Dependency
import org.bukkit.plugin.java.annotation.dependency.DependsOn
import org.bukkit.plugin.java.annotation.plugin.Description
import org.bukkit.plugin.java.annotation.plugin.Plugin
import java.io.File

@Plugin(name = "LupusCaseOpener", version = "1.0-SNAPSHOT")
@Description(value = "Simple case opener")
@Author(value = "LupusVirtute")
@Website(value = "github.com/PuccyDestroyerxXx")
@ApiVersion(value = ApiVersion.Target.v1_15)
class CaseOpener : JavaPlugin() {
    override fun onEnable() {
        info("Loading CaseOpener")
        mainPlugin = this
        mainDataFolder = dataFolder
        info("Hooking into vault")
        if (!setupEconomy()) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", description.name))
            server.pluginManager.disablePlugin(this)
            return
        }
        info("Hooked into Vault successfully")
        info("Setting up luck perms")
        setupLuckPerms()
        info("Luck Perms API set up")
        info("Loading serialized classes")
        loadSerializedClasses()
        info("Serialized classes loaded")
        info("Loading messages")
        Message.Companion.load()
        info("All messages loaded!")
        server.pluginManager.registerEvents(BlockManipulationListener(), mainPlugin as CaseOpener)
        server.pluginManager.registerEvents(PvEListener(), mainPlugin as CaseOpener)
        server.pluginManager.registerEvents(InventoryListener(), mainPlugin as CaseOpener)
        info("Started Loading Chests")
        loadChests()
        info("Chests loaded")
        info("amount:" + ChestManager.allCases.size)
        ArgumentType.addArgumentTypeInterpreter(ArgumentType(
            MinecraftCase::class.java) { arg: Array<String?> -> ChestManager.getCase(arg[0]) })
    }

    override fun onDisable() {
        ChestManager.saveAll(false)
    }

    private fun loadSerializedClasses() {
        ConfigurationSerialization.registerClass(MinecraftCase::class.java)
        ConfigurationSerialization.registerClass(CaseItemHolder::class.java)
        ConfigurationSerialization.registerClass(CaseItem::class.java)
        ConfigurationSerialization.registerClass(PlayerKey::class.java)
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp = server.servicesManager.getRegistration(
            Economy::class.java) ?: return false
        economy = rsp.provider
        return economy != null
    }

    fun setupLuckPerms() {
        val provider = Bukkit.getServicesManager().getRegistration(
            LuckPerms::class.java)
        if (provider != null) {
            api = provider.provider
        }
    }

    companion object {
        var mainDataFolder: File? = null
        lateinit var mainPlugin: CaseOpener
        var api: LuckPerms? = null
        var economy: Economy? = null
            private set
        val luckPermsAPI: LuckPerms?
            get() {
                if (api == null) api = LuckPermsProvider.get()
                return api
            }

        fun NamespacedKey(key: String?): NamespacedKey {
            return NamespacedKey(mainPlugin!!, key!!)
        }

        fun info(info: String?) {
            Bukkit.getLogger().info(info)
        }

        fun loadChests() {
            ChestManager.clear()
            val chestDir = File(mainDataFolder.toString() + "/chests")
            info(chestDir.path)
            val chestFiles = chestDir.listFiles() ?: return
            for (chestFile in chestFiles) {
                val file: FileConfiguration = YamlConfiguration.loadConfiguration(
                    chestFile!!)
                val minecraftCase = file["Chest"] as MinecraftCase?
                if (minecraftCase != null) {
                    ChestManager.addCase(minecraftCase)
                    minecraftCase.forceTopUpdate(false)
                    val chest = minecraftCase.getItemRepresentation(null)
                    val sT = SelectableTop(chest, minecraftCase, null)
                    TopKeysGUI.Companion.selectableTops.add(sT)
                }
            }
        }
    }
}