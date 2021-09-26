package com.lupus.opener.listeners

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
import net.milkbowl.vault.economy.Economy
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class BlockManipulationListener : Listener {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val p = e.player.player ?: return
        val block = e.block
        if (block.type == Material.MOSSY_COBBLESTONE) {
            for (mcCase in ChestManager.allCases) {
                if (mcCase!!.breakCobblex(e.player, block.location)) {
                    e.isDropItems = false
                    return
                }
            }
        }
        if (block == null) {
            return
        }
        if (!p.hasPermission("case.admin.break")) {
            return
        }
        if (isTimeForDestroy) {
            val b = ChestManager.removeCaseLocation(block)
            if (b) p.sendMessage(Message.CASE_BREAKED.toString())
        }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.HAND) {
            if (e.item != null) {
                val mcCase = MinecraftCaseUtils.getKeyRedeemCase(e.item)
                if (mcCase != null) {
                    val b: Int = mcCase.redeemKey(e.player, e.item)
                    if (b > -4) e.isCancelled = true
                    if (b == -3) Bukkit.broadcast(e.player.name + " - Prawdopodobne Kopiowanie kluczy",
                        "case.moderator")
                }
            }
        }
        if (e.hasBlock()) {
            val p = e.player ?: return
            if (p.hasPermission("case.admin.break") && isTimeForDestroy) {
                return
            }
            if (e.clickedBlock != null && e.action == Action.RIGHT_CLICK_BLOCK) {
                val mcCase = ChestManager.getCaseFromLocation(
                    e.clickedBlock!!.location
                )
                if (mcCase != null) {
                    e.isCancelled = true
                    mcCase.openCase(p, 1)
                }
            }
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent?) {
        if (e == null) return
        val item = e.itemInHand
        if (item.type == Material.MOSSY_COBBLESTONE) {
            val mcCase = MinecraftCaseUtils.getCobblex(item)
            if (mcCase != null) mcCase.putDownCobblex(item, e.blockPlaced.location)
        }
        if (!e.player.hasPermission("case.admin.place")) return
        val data: String?
        data = NBTUtility.getNBTValue(item, "case", String::class.java)
        if (data == null) {
            return
        }
        val mcCase = ChestManager.getCase(data)
        if (mcCase != null) {
            mcCase.addChestLocation(e.blockPlaced.location.clone())
            e.player.sendMessage(Message.CASE_PLACED_PROPERLY.toString())
        }
    }

    companion object {
        var isTimeForDestroy = false
    }
}