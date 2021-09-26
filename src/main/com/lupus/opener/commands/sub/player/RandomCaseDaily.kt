package com.lupus.opener.commands.sub.player

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
import net.kyori.adventure.text.Component
import net.milkbowl.vault.economy.Economy
import net.luckperms.api.LuckPermsProvider
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import java.lang.Exception
import java.util.*

class RandomCaseDaily : PlayerCommand(meta) {
    //   MS  M  H  D
    @Throws(Exception::class)
    override fun run(player: Player, argumentList: ArgumentList) {
        var meta = player.getMetadata("freecase")
        if (meta == null || meta.size <= 0) {
            val metaValue = FixedMetadataValue(CaseOpener.mainPlugin, 0L)
            player.setMetadata("freecase", metaValue)
            if (meta == null) meta = ArrayList()
            meta.add(metaValue)
        }
        val currentTimestamp = Instant.now().toEpochMilli()
        val metaValue = meta[0] ?: return
        val epochTime = metaValue.asLong()
        if (epochTime + DAY <= currentTimestamp) {
            reward(player, currentTimestamp)
            player.sendMessage(ChatColor.RED.toString() + "Dostałeś skrzynie gratulacje!")
        } else {
            player.sendMessage(ChatColor.RED.toString() + "Już dzisiaj dostałeś Skrzynie")
        }
    }

    fun reward(player: Player, epochTimeStamp: Long) {
        player.setMetadata("freecase", FixedMetadataValue(CaseOpener.mainPlugin, epochTimeStamp))
        val res = ChestManager.allCases
        var chosenOne: MinecraftCase? = null
        var highestWeight = 0
        for (re in res) {
            if (highestWeight < re.caseWeight) highestWeight = re.caseWeight
        }
        val rnd = Random()
        val random = rnd.nextInt(highestWeight)
        for (re in res) {
            if (random <= re.caseWeight) {
                chosenOne = re
                break
            }
        }
        player.sendMessage(Component.text(chosenOne?.officialName1 ?: ""))
        chosenOne?.giveKey(player, 1)
    }

    companion object {
        var meta = CommandMeta().setName("dajskrzynie").setUsage(usage("/dajskrzynie"))
            .setDescription(colorText("&6Dostajesz darmową skrzynie")).addPermission("case.freecase")
        private const val DAY = (1000 * 60 * 60 * 24).toLong()
    }
}