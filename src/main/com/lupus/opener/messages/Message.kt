package com.lupus.opener.messages

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
import net.milkbowl.vault.economy.Economy
import net.luckperms.api.LuckPermsProvider

enum class Message {
    LOGO, DESTROY_MESSAGE_ON, DESTROY_MESSAGE_OFF, CASE_EXISTS, CASE_GIVEN_DONT_EXISTS, COMMAND_CREATE_SUCCESSFUL, COMMAND_EDIT_WEIGHT_SUCCESS, COMMAND_GET_CASE_SUCCESS, COMMAND_GIVE_KEY_SUCCESS_MESSAGE_PLAYER, COMMAND_GIVE_KEY_SUCCESS_MESSAGE_ADMIN, COMMAND_TAKE_KEY_FAIL_NO_KEYS_LEFT, COMMAND_TAKE_KEY_SUCCESS_MESSAGE_PLAYER, COMMAND_TAKE_KEY_SUCCESS_MESSAGE_ADMIN, COMMAND_BUY_KEY_BOUGHT, COMMAND_CHANGE_KEY_NEED, COMMAND_CHANGE_KEY_SUCCESS, COMMAND_KEYS_TOP, COMMAND_KEYS_BOTTOM, COMMAND_KEYS_KEY, COMMAND_KEYS_RESET, QUANTITY_MORE_THAN_ZERO, NOT_ENOUGH_KEYS, KEY_SEND_SUCCESS_SENDER, KEY_SEND_SUCCESS_RECEIVER, BUY_CASE_INVENTORY_NAME, BUY_CASE_PRICE_LORE, DROP_CHANCE_LORE, CHEST_LIST_ADMIN_LORE, CHEST_LIST_PLAYER_LORE, CHEST_LIST_INVENTORY_NAME, ITEM_EDITOR_ACCEPT_NAME, ITEM_EDITOR_DENY_NAME, ITEM_EDITOR_INFO_NAME, ITEM_EDITOR_INFO_LORE, WINNER_MESSAGE, CASE_OPENING_BACKGROUND_GLASS_LORE, CASE_OPENING_BACKGROUND_GLASS_NAME, CASE_OPENING_POINTER_GLASS_LORE, CASE_OPENING_POINTER_GLASS_NAME, STATTRACK_KILLS_FORMATING, STATTRACK_KILLS_NAME, NULL_ITEM_IN_HAND, STATTRACK_ITEM_SET_PROPERLY, NO_KEY, ALERADY_OPENING, SAVING_INIT, INSUFFICIENT_MONEY, TOP_KEYS_MAIN_INVENTORY_NAME, SAVING_END, CASE_PLACED_PROPERLY, CASE_HAS_0_KEYS, CASE_IS_CALCULATING_TOP, SELECTABLE_TOP_FILLER_NAME, SELECTABLE_TOP_FILLER_LORE, UNKNOWN_NICKNAME, CASE_BREAKED, PLAYER_CASE_KEY_FORMAT, PLAYER_PLACE_TEMPLATE, ITEM_KEY_FORMAT_TEMPLATE, ITEM_KEY_LORE_TEMPLATE, COMMAND_KEYS_RESET_PLAYER, COBBLEX_TITLE, COBBLEX_LORE, ICON_SET;

    private var text: String
    private fun setText(text: String) {
        var text: String? = text ?: return
        text = TextUtility.color(text)
        this.text = text.replace("\\n", "\n")
    }

    override fun toString(): String {
        return text
    }

    fun toString(query: MessageReplaceQuery): String {
        return toString(query.toMap())
    }

    fun toString(replacementMap: Map<String, String>): String {
        var copiedText = text
        for (replacementEntry in replacementMap!!.entries) {
            val key = StringBuilder().append(REPLACEMENT_CHAR).append(replacementEntry.key).append(REPLACEMENT_CHAR)
                .toString()
            var value = replacementEntry.value
            if (value == null) value = ""
            copiedText = copiedText.replace(key, value)
        }
        return copiedText
    }

    companion object {
        private const val REPLACEMENT_CHAR = '%'
        fun load() {
            CaseOpener.mainPlugin.saveResource("Messages.yml", false)
            val config = ConfigUtility.getConfig(CaseOpener.mainPlugin, "Messages.yml")
            for (value in values()) {
                value.setText(config.getString(value.name)!!)
            }
        }
    }

    init {
        text = name
    }
}