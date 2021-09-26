package com.lupus.opener.chests

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
import com.lupus.opener.messages.Message
import com.lupus.opener.messages.MessageReplaceQuery
import net.milkbowl.vault.economy.Economy
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import java.util.ArrayList

class CaseItem : ConfigurationSerializable, Cloneable {
    public override fun clone(): CaseItem {
        return CaseItem(this)
    }

    private var item: ItemStack? = null
    val weight: Int

    constructor(item: CaseItem?) {
        weight = item!!.weight
        this.item = ItemStack(item.getItem())
    }

    constructor(item: ItemStack, weight: Int) {
        this.item = item
        this.weight = weight
    }

    constructor(map: Map<String?, Any?>) {
        if (map.containsKey("item")) {
            item = map["item"] as ItemStack?
        } else ItemStack(Material.STICK)
        weight = if (map.containsKey("weight")) {
            map["weight"] as Int
        } else 0
    }

    fun getItem(): ItemStack {
        return ItemStack(item!!)
    }

    fun setItem(item: ItemStack) {
        this.item = item.clone()
    }

    override fun serialize(): Map<String, Any> {
        val serializedMap: MutableMap<String, Any> = HashMap()
        serializedMap["item"] = item!!
        serializedMap["weight"] = weight
        return serializedMap
    }

    companion object {
        var applicableStarTracks = arrayOf(
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
            Material.STONE_PICKAXE)

        fun addStarTrack(starTrack: ItemStack): ItemStack {
            var starTrack = starTrack
            val starTrackMat = starTrack.type
            var applicable = false
            for (i in applicableStarTracks.indices) {
                if (applicableStarTracks[i] == starTrackMat) {
                    applicable = true
                    break
                }
            }
            if (!applicable) return starTrack
            starTrack = ItemStack(starTrack)
            starTrack = NBTUtility.setNBTDataValue(starTrack, "StarKiller", 0)
            val meta = starTrack.itemMeta
            var mrq = MessageReplaceQuery().addQuery("name", ItemUtility.getItemName(starTrack))
            meta.setDisplayName(Message.STATTRACK_KILLS_NAME.toString(mrq))
            var lore = meta.lore
            if (lore == null) {
                lore = ArrayList()
            }
            mrq = MessageReplaceQuery().addQuery("amount", "0")
            val statTrackMessages: Array<String> =
                Message.STATTRACK_KILLS_FORMATING.toString(mrq).split("\\n".toRegex()).toTypedArray()
            lore.addAll(Arrays.asList(*statTrackMessages))
            meta.lore = lore
            starTrack.setItemMeta(meta)
            return starTrack
        }
    }
}