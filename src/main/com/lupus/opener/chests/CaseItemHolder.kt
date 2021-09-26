package com.lupus.opener.chests

import com.lupus.gui.TopPyramidGUI
import com.lupus.opener.chests.MinecraftCase
import com.lupus.gui.SelectableItem
import com.lupus.gui.IGUI
import com.lupus.gui.utils.SkullUtility
import com.lupus.gui.utils.ItemUtility
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
import com.lupus.opener.chests.PlayerKey
import com.lupus.opener.gui.CaseItemList
import com.lupus.opener.runnables.ChestOpener
import java.lang.StringBuilder
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
import net.milkbowl.vault.economy.Economy
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import java.lang.Exception
import java.util.*

class CaseItemHolder : ConfigurationSerializable {
    var items: MutableList<CaseItem>? = null
    var maxWeight = 0

    constructor(items: MutableList<CaseItem>?) {
        this.items = items
        recalculateWeightMax()
    }

    constructor(map: Map<String?, Any?>) {
        items = if (map.containsKey("items")) {
            try {
                map["items"] as MutableList<CaseItem>?
            } catch (ex: Exception) {
                ArrayList()
            }
        } else {
            ArrayList()
        }
        recalculateWeightMax()
    }

    fun recalculateWeightMax(): Int {
        maxWeight = 0
        for (i in items!!.indices) {
            maxWeight += items!![i].weight
        }
        return maxWeight
    }

    fun setItemAt(it: CaseItem, index: Int) {
        if (index < items!!.size && index >= 0) {
            maxWeight -= items!![index].weight
            items!![index] = it
            maxWeight += it.weight
        } else addItem(it)
    }

    fun getItemAt(index: Int): CaseItem? {
        return if (items!!.size <= index) {
            null
        } else items!![index]
    }

    fun addItem(item: CaseItem) {
        items!!.add(item)
        maxWeight += item.weight
    }

    fun addItem(item: ItemStack, weight: Int) {
        addItem(CaseItem(item, weight))
        maxWeight += weight
    }

    fun getItems(): Array<CaseItem> {
        return items!!.toTypedArray()
    }

    val itemCount: Int
        get() = items!!.size
    val randomItem: CaseItem?
        get() {
            if (items!!.size == 0) {
                return null
            }
            val rnd = Random()
            var chance = rnd.nextInt(maxWeight)
            var stack: CaseItem? = null
            for (i in items!!.indices) {
                chance -= items!![i].weight
                if (chance < 0) {
                    stack = CaseItem(items!![i])
                    break
                }
            }
            chance = rnd.nextInt(100)
            if (chance < 10 && stack != null) {
                val itemStack: ItemStack = CaseItem.addStarTrack(stack.getItem())
                stack.setItem(itemStack)
            }
            return stack
        }

    override fun serialize(): Map<String, Any> {
        val serializedMap: MutableMap<String, Any> = HashMap()
        serializedMap["items"] = items!!
        return serializedMap
    }
}