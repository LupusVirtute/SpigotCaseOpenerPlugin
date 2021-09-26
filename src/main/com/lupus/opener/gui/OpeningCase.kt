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
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.ArrayList

class OpeningCase(chestOfName: String,var items: Array<ItemStack>, winnerIndex: Int, winnerPercentage: Float) :
    GUI(chestOfName, 27) {
    var winnerIndex: Int
    var currentIndex = 0
    var exit: ItemStack
    var playerWantsOut = false
    var winnerPercentage: Float
    val distanceToWinner: Int
        get() = winnerIndex - (currentIndex + 5)

    override fun click(player: Player, e: InventoryClickEvent) {
        if (e.currentItem == null) return
        if (e.currentItem!!.isSimilar(exit)) {
            award(player)
        }
        return
    }

    fun award(p: Player?) {
        if (p == null) {
            return
        }
        if (playerWantsOut) {
            removePlayerCaseOpening(p)
            p.closeInventory()
            return
        }
        playerWantsOut = true
        InventoryUtility.addItemStackToPlayerInventory(p, winner)
        if (winnerPercentage < 0.01) {
            setUpFireWork(p.location)
            val mrq =
                MessageReplaceQuery().addQuery("player", p.name).addQuery("item_name", ItemUtility.getItemName(
                    winner)).addQuery("amount", winner.amount.toString() + "")
            Bukkit.broadcastMessage(Message.WINNER_MESSAGE.toString(mrq))
        }
        OpenerManager.setPlayerOpener(p, null)
        p.playSound(p.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
    }

    val winner: ItemStack
        get() = items[winnerIndex].item

    fun removePlayerCaseOpening(p: Player) {
        if (OpenerManager.getPlayerOpeningCase(p) != null) OpenerManager.setPlayerOpener(p, null)
    }

    fun doABarrelRoll(p: Player): Boolean {
        if (playerWantsOut) {
            removePlayerCaseOpening(p)
            return true
        }
        if (distanceToWinner <= 0) {
            award(p)
        }
        currentIndex++
        if (currentIndex >= items.size) currentIndex = 0
        for (i in 0..8) {
            var index = currentIndex
            if (index + i >= items.size) {
                index -= items.size
            }
            index += i
            inv.setItem(i + 9, items[index].item)
        }
        p.updateInventory()
        p.playSound(p.location,
            Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
            1.0f,
            (distanceToWinner % items.size).toFloat() / 10f)
        return playerWantsOut
    }

    override fun onClose(p: Player) {
        if (!playerWantsOut) award(p)
        removePlayerCaseOpening(p)
    }

    companion object {
        fun setUpFireWork(loc: Location) {
            loc.add(0.0, 2.0, 0.0)
            val effect =
                FireworkEffect.builder().flicker(true).trail(true).with(FireworkEffect.Type.BALL_LARGE).withColor(
                    Color.AQUA, Color.GREEN).build()
            val firework = loc.world.spawnEntity(loc, EntityType.FIREWORK) as Firework
            val meta = firework.fireworkMeta
            meta.addEffect(effect)
            firework.fireworkMeta = meta
            firework.velocity = Vector(0.1, 10.0, 0.0)
        }
    }

    /**
     * @param chestOfName - official name of chest
     * @param items - item to show
     * @param winnerIndex - winning index
     */
    init {
        this.winnerPercentage = winnerPercentage
        this.winnerIndex = winnerIndex
        exit = ItemStack(Material.RED_STAINED_GLASS_PANE)
        val meta = exit.itemMeta
        meta.setDisplayName(Message.CASE_OPENING_BACKGROUND_GLASS_NAME.toString())
        var messages: Array<String?> =
            Message.CASE_OPENING_BACKGROUND_GLASS_LORE.toString().split("\\n".toRegex()).toTypedArray()
        var lore: List<String> = ArrayList(Arrays.asList(*messages))
        meta.lore = lore
        exit.setItemMeta(meta)
        val pointer = ItemStack(Material.BLUE_STAINED_GLASS_PANE)
        ItemUtility.setItemTitle(pointer, Message.CASE_OPENING_POINTER_GLASS_NAME.toString())
        messages = Message.CASE_OPENING_POINTER_GLASS_LORE.toString().split("\\n".toRegex()).toTypedArray()
        lore = ArrayList(Arrays.asList(*messages))
        ItemUtility.setItemLore(pointer, lore)
        pointer.setItemMeta(meta)
        for (i in 0..8) {
            inv.setItem(i, exit)
        }
        for (i in 18..26) {
            inv.setItem(i, exit)
        }
        inv.setItem(22, pointer)
        inv.setItem(4, pointer)
    }
}