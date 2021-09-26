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
import net.luckperms.api.node.Node
import org.apache.commons.lang.math.NumberUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.ArrayList

class BuyCaseGUI(player: Player) : Paginator(Message.BUY_CASE_INVENTORY_NAME.toString()) {
    override fun onClose(player: Player) {}

    companion object {
        var df2 = DecimalFormat("#.###")
        fun getPrice(p: Player, price: Double): Double {
            var price = price
            val node: Node = Node.builder("case.premium").value(true).build()
            val api: LuckPerms = CaseOpener.luckPermsAPI ?: return price
            val user = api.userManager.getUser(p.uniqueId) ?: return price
            val nodes: Collection<Node> =
                user.resolveDistinctInheritedNodes(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build())
            for (node1 in nodes) {
                if (node1.key.contains(node.key)) {
                    val key = node1.key.replace(node.key, "").replace(".", "")
                    if (NumberUtils.isNumber(key)) {
                        val i = key.toDouble() * 10.0
                        val rest = price / i
                        price -= rest
                    }
                }
            }
            return price
        }
    }

    init {
        for (mcCase in ChestManager.allCases) {
            val its = ItemStack(Material.CHEST)
            var price = mcCase.price
            if (player.hasPermission("case.premium")) {
                price = getPrice(player, price)
            }
            ItemUtility.setItemTitle(its, TextUtility.color(mcCase!!.officialName1))
            val meta = its.itemMeta
            val mrq = MessageReplaceQuery().addQuery("price", df2.format(price))
            val messages: Array<String> =
                Message.BUY_CASE_PRICE_LORE.toString(mrq).split("\\n".toRegex()).toTypedArray()
            val lore: List<String> = ArrayList(Arrays.asList(*messages))
            meta.lore = lore
            its.setItemMeta(meta)
            addItemStack(SelectableCommand(its, "skrzynie kupklucz " + mcCase.name + " 1"))
        }
    }
}