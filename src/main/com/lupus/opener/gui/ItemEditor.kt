package com.lupus.opener.gui

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
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ItemEditor(
    invName: String?,
    var index: Int,
    var mcCase: MinecraftCase
) : GUI(invName, 27) {
    var value = 1
    var weightMax: Int
    var itemEdited: ItemStack? = null
    var info = ItemStack(Material.BLUE_STAINED_GLASS_PANE)
    fun updateInfoMeta() {
        var chance = value.toFloat() / weightMax.toFloat()
        chance *= 100f
        val mrq = MessageReplaceQuery().addQuery("chance", format.format(chance.toDouble()))
            .addQuery("chest", mcCase.officialName1 ?: "")
        val messages: Array<String> = Message.ITEM_EDITOR_INFO_LORE.toString(mrq).split("\\n".toRegex()).toTypedArray()
        ItemUtility.setItemLore(info, messages)
        inv.setItem(13, info)
    }

    override fun onClickedItemNull(player: Player, e: InventoryClickEvent) {
        click(player, e)
    }

    override fun click(player: Player, e: InventoryClickEvent) {
        if (e.rawSlot >= inventory.size) {
            e.isCancelled = false
        }
        val clickedItem = e.currentItem
        val clickedSlot = e.rawSlot
        when (clickedSlot) {
            21 -> {
                mcCase.openCaseEditor(player)
                return
            }
            22 -> {
                if (e.click.isRightClick) {
                    if (clickedItem == null) return
                    InventoryUtility.addItemStackToPlayerInventory(player, e.currentItem)
                }
                if (e.click.isLeftClick && e.cursor != null) {
                    inv.setItem(22, ItemStack(e.cursor!!))
                    e.view.cursor = ItemStack(Material.AIR)
                }
                return
            }
            23 -> {
                updateCase()
                mcCase.openCaseEditor(player)
                return
            }
        }
        if (clickedItem == null) return
        if (SkullUtility.isThisItemNumberSkull(ItemStack(clickedItem))) {
            val pow = Math.pow(10.0, Math.abs(clickedSlot % 9 - 8).toDouble())
            if (e.click.isLeftClick) {
                addValue(pow.toInt())
            } else if (e.click.isRightClick) {
                addValue((-pow).toInt())
            }
            return
        }
        return
    }

    fun addValue(valueAmount: Int) {
        value += valueAmount
        if (value >= MAX_VALUE) value = 1 else if (value <= 0) {
            value = 1
        }
        SkullUtility.intToSkullConverter(inv, value, 0, 8)
        updateInfoMeta()
    }

    override fun onClose(p: Player) {}
    fun updateCase() {
        val item = inv.getItem(22) ?: return
        mcCase.setItemAt(CaseItem(ItemStack(item), value), index)
    }

    companion object {
        const val MAX_VALUE = 1000000000
        var format = DecimalFormat("#.#####")
    }

    init {
        val itemAt = mcCase.dropTable?.getItemAt(index)
        weightMax = mcCase.dropTable?.maxWeight ?: 0
        var itemWeight = 0
        value = 1
        if (itemAt != null) {
            itemEdited = itemAt.getItem()
            itemWeight = itemAt.weight
            if (itemWeight > 0 && itemWeight < MAX_VALUE) {
                value = itemWeight
            }
        }
        SkullUtility.intToSkullConverter(inv, value, 0, 8)
        var meta: ItemMeta
        val accept = ItemStack(Material.GREEN_STAINED_GLASS_PANE)
        meta = accept.itemMeta
        meta.setDisplayName(Message.ITEM_EDITOR_ACCEPT_NAME.toString())
        accept.itemMeta = meta
        inv.setItem(23, accept)
        val cancel = ItemStack(Material.RED_STAINED_GLASS_PANE)
        meta = cancel.itemMeta
        meta.setDisplayName(Message.ITEM_EDITOR_DENY_NAME.toString())
        accept.itemMeta = meta
        inv.setItem(21, cancel)
        inv.setItem(22, itemEdited)
        meta = info.itemMeta
        meta.setDisplayName(Message.ITEM_EDITOR_INFO_NAME.toString())
        info.itemMeta = meta
        inv.setItem(13, info)
        updateInfoMeta()
    }
}