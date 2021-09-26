package com.lupus.opener.commands.sub.admin

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
import com.lupus.command.framework.commands.arguments.ArgumentList
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
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.lang.Exception

class GiveKeyCMD : LupusCommand(meta) {
    @Throws(Exception::class)
    override fun run(executor: CommandSender, args: ArgumentList) {
        val chest = args.getArg(String::class.java, 0)
        val playerName = args.getArg(String::class.java, 1)
        val amount = args.getArg(Int::class.javaPrimitiveType, 2)
        if (chest == "*") {
            giveAllCasesTo(executor, args)
            return
        }
        val mcCase = ChestManager.getCase(chest)
        if (mcCase == null) {
            val mpq = MessageReplaceQuery().addQuery("chest", chest)
            executor.sendMessage(colorText(Message.CASE_GIVEN_DONT_EXISTS.toString(mpq)))
            return
        }
        if (playerName == "*") {
            giveCaseToAll(executor, args)
            return
        }
        val player2nd = Bukkit.getPlayerExact(playerName)
        var uuid: UUID? = null
        uuid = if (player2nd == null) {
            val player = Bukkit.getOfflinePlayer(playerName)
            player.uniqueId
        } else player2nd.uniqueId
        mcCase.giveKey(uuid, amount)
        val mrq = MessageReplaceQuery().addQuery("player", executor.name).addQuery("amount", amount.toString())
            .addQuery("chest", mcCase.officialName1 ?: "")
        player2nd?.sendMessage(
            Message.COMMAND_GIVE_KEY_SUCCESS_MESSAGE_PLAYER.toString(mrq)
        )
        mrq.addQuery("player", playerName)
        executor.sendMessage(Message.COMMAND_GIVE_KEY_SUCCESS_MESSAGE_ADMIN.toString(mrq))
    }

    private fun giveAllCasesTo(executor: CommandSender, args: ArgumentList) {
        for (theCase in ChestManager.allCases) {
            val chest = theCase.name
            val argsBetter = Arrays.copyOf(args.toTypedArray(), args.size)
            argsBetter[0] = chest
            executeAsync(executor, argsBetter, CaseOpener.mainPlugin)
        }
    }

    private fun giveCaseToAll(executor: CommandSender, args: ArgumentList) {
        for (p in Bukkit.getOnlinePlayers()) {
            val playerName = p.name
            val argsBetter = Arrays.copyOf(args.toTypedArray(), args.size)
            argsBetter[1] = playerName
            executeAsync(executor, argsBetter, CaseOpener.mainPlugin)
        }
    }

    companion object {
        var meta =
            CommandMeta().setName("givekey").setUsage(usage("/case givekey", "[case] [name] [ilosc]")).setDescription(
                colorText("&6Dajesz klucz graczowi &b&l[name] &6do skrzyni &b&l[case] &6i z iloscia &b&l[ilosc]"))
                .addPermission("case.admin.key.add").setArgumentAmount(3)
    }
}