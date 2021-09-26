package com.lupus.opener.chests

import com.lupus.gui.utils.ItemUtility
import com.lupus.gui.utils.TextUtility
import java.text.DecimalFormat
import com.lupus.opener.managers.ChestManager
import com.lupus.opener.CaseOpener
import com.lupus.gui.utils.InventoryUtility
import com.lupus.opener.gui.OpeningCase
import com.lupus.opener.managers.OpenerManager
import com.lupus.opener.chests.utils.MinecraftCaseUtils
import com.lupus.gui.utils.NBTUtility
import com.lupus.opener.gui.CaseItemList
import com.lupus.opener.runnables.ChestOpener
import java.lang.StringBuilder
import java.lang.Runnable
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
import com.lupus.opener.messages.Message
import com.lupus.opener.messages.MessageReplaceQuery
import net.kyori.adventure.text.TextComponent
import net.milkbowl.vault.economy.Economy
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.lang.Exception
import java.util.*

class MinecraftCase : ConfigurationSerializable {
    var keys: MutableMap<UUID?, Int>? = null
    var chests: MutableList<Location> = ArrayList()
    var keyRedeemMap = HashMap<UUID?, Int>()
    var dropTable: CaseItemHolder? = null
    private var icon: ItemStack = ItemStack(Material.STICK)
    var name: String? = null
    var officialName1: String = ""
    fun getOfficialName(): String? {
        return TextUtility.color(officialName1)
    }
    var totalAmountOfKeys = 0
    var price = 0.0
    var caseWeight = 0

    constructor(map: Map<String?, Any?>) {
        if (map.containsKey("name")) {
            name = map["name"] as String?
        }
        if (map.containsKey("officialName")) {
            officialName1 = map["officialName"] as String
        }
        if (map.containsKey("price")) {
            price = map["price"] as Double
        }
        if (map.containsKey("weight")) {
            caseWeight = map["weight"] as Int
        }
        if (map.containsKey("cobblexLocations")) {
            cobblexLocations = map["cobblexLocations"] as MutableList<Location>
        }
        if (map.containsKey("registeredCobblex")) {
            val tempStringList = map["registeredCobblex"] as List<String>
            registeredCobblex = ArrayList()
            for (s in tempStringList) {
                registeredCobblex.add(UUID.fromString(s))
            }
        }
        if (map.containsKey("dropTable")) {
            dropTable = map["dropTable"] as CaseItemHolder
        } else {
            dropTable = CaseItemHolder(ArrayList<CaseItem>())
        }
        if (map.containsKey("icon")) {
            val icon = map["icon"]
            if (icon is ItemStack) this.icon = icon else this.icon = ItemStack(Material.CHEST)
        } else {
            icon = ItemStack(Material.CHEST)
        }
        if (map.containsKey("keys")) {
            keys = TreeMap()
            if (map["keys"] is List<*>) {
                val tempKeys = map["keys"] as List<PlayerKey>?
                for (tempKey in tempKeys!!) {
                    (keys as TreeMap<UUID?, Int>)[tempKey.player] = tempKey.amount
                    totalAmountOfKeys += tempKey.amount
                }
            }
        } else {
            keys = TreeMap()
        }
        if (map.containsKey("keyRedeemMap")) {
            val encapsulated = map["keyRedeemMap"]
            if (encapsulated is List<*>) {
                val list = encapsulated as List<PlayerKey>
                for (playerKey in list) {
                    keyRedeemMap[playerKey.player] = playerKey.amount
                }
            }
        }
        if (map.containsKey("locations")) {
            val obj = map["locations"]
            if (obj is List<*>) {
                chests = obj as MutableList<Location>
                if (chests == null) chests = ArrayList()
                for (i in chests.indices) {
                    ChestManager.addCaseLocation(chests[i], name)
                }
            }
        } else {
            chests = ArrayList()
        }
    }

    constructor(
        name: String?,
        officialName: String?,
        price: Float,
        weight: Int,
        holder: CaseItemHolder?
    ) {
        dropTable = holder
        this.name = name
        this.officialName1 = officialName ?: ""
        this.price = price.toDouble()
        caseWeight = weight
        icon = ItemStack(Material.CHEST)
        keys = TreeMap()
        chests = ArrayList()
    }

    fun openCaseEditor(player: Player?): Boolean {
        player!!.closeInventory()
        val caseItemList = CaseItemList(this, player)
        player.openInventory(caseItemList.inventory)
        return true
    }

    fun openCase(player: Player, amount: Int) {
        if (!hasKey(player)) {
            player.sendMessage(Message.NO_KEY.toString())
            return
        }
        if (amount > 1) {
            if (getKeyAmount(player) < amount) {
                player.sendMessage(Message.NOT_ENOUGH_KEYS.toString())
                return
            }
            removeKey(player, amount)
            for (i in 0 until amount) {
                InventoryUtility.addItemStackToPlayerInventory(player, randomItem!!.getItem())
            }
            return
        }
        if (OpenerManager.getPlayerOpeningCase(player) != null) {
            player.sendMessage(Message.ALERADY_OPENING.toString())
            return
        }
        removeKey(player, 1)
        forceOpen(player)
    }

    fun forceOpen(player: Player) {
        val opener = OpenerManager.getPlayerOpeningCase(player)
        if (opener != null) opener.award(player)
        val rnd = Random()
        val itms = arrayOfNulls<ItemStack>(rnd.nextInt(55) + 30) as Array<ItemStack>
        var i = 0
        val j = itms.size - 1
        while (i < j) {
            itms[i] = ItemStack(randomItem!!.getItem())
            i++
        }
        val winner = CaseItem(randomItem)
        itms[itms.size - 1] = winner.getItem().clone()
        val weightMax = dropTable!!.recalculateWeightMax()
        val winnerPercentage = winner.weight as Float / weightMax.toFloat()
        val mcCase = OpeningCase(TextUtility.color(officialName1), itms, itms.size - 1, winnerPercentage)
        OpenerManager.setPlayerOpener(player, mcCase)
        player.openInventory(mcCase.inventory)
        ChestOpener(mcCase, player).runTask(CaseOpener.mainPlugin)
    }

    fun hasKey(p: Player): Boolean {
        return hasKey(p.uniqueId)
    }

    fun hasKey(player: UUID?): Boolean {
        if (!keys!!.containsKey(player)) {
            keys!![player] = 0
            return false
        }
        val amount = keys!![player]!!
        return amount > 0
    }

    fun getKeyAmount(p: Player): Int {
        return getKeyAmount(p.uniqueId)
    }

    fun getKeyAmount(player: UUID?): Int {
        if (!keys!!.containsKey(player)) {
            keys!![player] = 0
        }
        return keys!![player]!!
    }

    fun giveKey(player: UUID?, amount: Int) {
        if (!keys!!.containsKey(player)) keys!![player] = 0
        keys!![player] = keys!![player]!! + amount
        totalAmountOfKeys += amount
    }

    fun setKey(player: Player, amount: Int) {
        setKey(player.uniqueId, amount)
    }

    fun setKey(player: UUID?, amount: Int) {
        if (!keys!!.containsKey(player)) return
        var totalKeys = keys!![player]
        keys!![player] = amount
        totalKeys = totalKeys?.minus(amount)
        if (totalKeys != null) {
            totalAmountOfKeys -= totalKeys
        }
    }

    fun turnKeyIntoItemStack(player: Player, amount: Int): ItemStack? {
        return turnKeyIntoItemStack(player.uniqueId, amount)
    }

    fun turnKeyIntoItemStack(player: UUID?, amount: Int): ItemStack? {
        if (!hasKey(player)) return null
        if (getKeyAmount(player) < amount) return null
        removeKey(player, amount)
        var itemStack = ItemStack(Material.TRIPWIRE_HOOK)
        val mrq =
            MessageReplaceQuery().addQuery("case", officialName1).addQuery("amount", amount.toString() + "")
        itemStack = ItemUtility.setItemTitle(itemStack, Message.ITEM_KEY_FORMAT_TEMPLATE.toString(mrq))
        itemStack = ItemUtility.setItemLore(itemStack,
            Message.ITEM_KEY_LORE_TEMPLATE.toString(mrq).split("\n".toRegex()).toTypedArray())
        itemStack.addUnsafeEnchantment(Enchantment.LUCK, 1)
        val keyUUID = UUID.randomUUID()
        itemStack = NBTUtility.setNBTDataValue(itemStack, "Key", keyUUID.toString())
        itemStack = NBTUtility.setNBTDataValue(itemStack, "CaseKey", name)
        keyRedeemMap[keyUUID] = amount
        return itemStack
    }

    fun redeemKey(player: Player, item: ItemStack?): Int {
        return redeemKey(player.uniqueId, item)
    }

    fun redeemKey(player: UUID?, item: ItemStack?): Int {
        if (item == null) return -1
        if (!NBTUtility.hasNBTTag(item, "Key")) {
            return -1
        }
        val onlinePlayer = Bukkit.getPlayer(player!!) ?: return -2
        if (!onlinePlayer.inventory.contains(item)) {
            return -2
        }
        onlinePlayer.inventory.remove(item)
        val keyStringUID = NBTUtility.getNBTValue(item, "Key", String::class.java)
        val keyUID = UUID.fromString(keyStringUID)
        val amount = keyRedeemMap[keyUID] ?: return -3
        keyRedeemMap.remove(keyUID)
        val mrq = MessageReplaceQuery().addQuery("amount", amount.toString() + "").addQuery("player", "SYSTEM")
            .addQuery("chest", officialName1)
        onlinePlayer.sendMessage(Message.KEY_SEND_SUCCESS_RECEIVER.toString(mrq))
        giveKey(player, amount)
        return amount
    }

    fun removeKey(player: UUID?, amount: Int) {
        if (keys!!.containsKey(player)) {
            keys!![player] = keys!![player]!! - amount
        }
    }

    fun removeKey(p: Player, amount: Int) {
        removeKey(p.uniqueId, amount)
        totalAmountOfKeys -= amount
    }

    fun giveKey(p: Player, amount: Int) {
        giveKey(p.uniqueId, amount)
    }

    fun giveCase(): ItemStack {
        val chest = ItemStack(Material.CHEST)
        NBTUtility.setNBTDataValue(chest, "case", name)
        return chest
    }

    fun addChestLocation(location: Location) {
        ChestManager.addCaseLocation(location, name)
        if (chests == null) chests = ArrayList()
        chests.add(location)
    }

    val items: Array<CaseItem>
        get() = dropTable!!.getItems()

    fun setItemAt(it: CaseItem, index: Int) {
        dropTable!!.setItemAt(it, index)
    }

    fun addItem(item: ItemStack, weight: Int) {
        dropTable!!.addItem(item, weight)
    }

    val randomItem: CaseItem?
        get() = dropTable?.randomItem?.clone()

    fun getItemRepresentation(caller: Player?): ItemStack {
        val df2 = DecimalFormat("#.##")
        val chest = ItemStack(icon!!)
        ItemUtility.setItemTitle(
            chest,
            TextUtility.color(officialName1)
        )
        val mrq =
            MessageReplaceQuery().addQuery("price", df2.format(price)).addQuery("weight", caseWeight.toString() + "")
                .addQuery("amount", totalAmountOfKeys.toString() + "")
        val playerMessages: Array<String> =
            Message.CHEST_LIST_PLAYER_LORE.toString(mrq).split("\\n".toRegex()).toTypedArray()
        val lore: MutableList<String> = ArrayList(Arrays.asList(*playerMessages))
        caller?.let { addAdminInfo(it, mrq, lore) }
        ItemUtility.setItemLore(chest, lore)
        return chest
    }

    private fun addAdminInfo(caller: Player, mrq: MessageReplaceQuery, lore: MutableList<String>) {
        if (caller.hasPermission("case.admin")) {
            val adminMessages = Message.CHEST_LIST_ADMIN_LORE.toString(mrq).split("\\n".toRegex()).toTypedArray()
            lore.addAll(Arrays.asList(*adminMessages))
        }
    }

    fun setIcon(itemStack: ItemStack?) {
        if (itemStack == null) return
        icon = itemStack
    }

    var registeredCobblex: MutableList<UUID> = ArrayList()
    var cobblexLocations: MutableList<Location>? = ArrayList()
    val cobblex: ItemStack
        get() {
            val itemStack = ItemStack(Material.MOSSY_COBBLESTONE)
            ItemUtility.setItemTitleAndLore(itemStack,
                Message.COBBLEX_TITLE.toString(),
                Arrays.asList(*Message.COBBLEX_LORE.toString().split("\n".toRegex()).toTypedArray())
            )
            NBTUtility.setNBTDataValue(itemStack, "Cobblex", name)
            val uid = UUID.randomUUID()
            registeredCobblex.add(uid)
            NBTUtility.setNBTDataValue<Any>(itemStack, "CobblexUID", uid.toString())
            return itemStack
        }

    fun putDownCobblex(itemStack: ItemStack?, location: Location) {
        val uidString = NBTUtility.getNBTValue(itemStack, "CobblexUID", String::class.java)
            ?: return
        val uuid = UUID.fromString(uidString)
        if (!registeredCobblex.contains(uuid)) {
            val builder = StringBuilder()
            builder.append(location.x).append(' ').append(location.y).append(' ').append(location.z)
            Bukkit.broadcast("$builder XYZ - Prawdopodobne Kopiowanie Cobblexa", "case.moderator")
        } else {
            registeredCobblex.remove(uuid)
            cobblexLocations!!.add(location)
        }
    }

    fun breakCobblex(player: Player, location: Location): Boolean {
        if (!cobblexLocations!!.contains(location)) {
            return false
        }
        cobblexLocations!!.remove(location)
        forceOpen(player)
        return true
    }

    override fun serialize(): Map<String, Any> {
        val serializedMap: MutableMap<String, Any> = HashMap()
        serializedMap["name"] = name!!
        serializedMap["officialName"] = officialName1!!
        serializedMap["price"] = price
        serializedMap["weight"] = caseWeight
        serializedMap["icon"] = icon!!
        val stringCobblexUUID: MutableList<String> = ArrayList()
        for (cobblex in registeredCobblex) {
            stringCobblexUUID.add(cobblex.toString())
        }
        serializedMap["registeredCobblex"] = stringCobblexUUID
        serializedMap["cobblexLocations"] = cobblexLocations!!
        serializedMap["dropTable"] = dropTable!!
        val serializedPlayerKeys: MutableList<PlayerKey> = ArrayList()
        for ((key, value) in keys!!) {
            serializedPlayerKeys.add(
                PlayerKey(key, value)
            )
        }
        val redeemKeysMap: MutableList<PlayerKey> = ArrayList()
        for ((key, value) in keyRedeemMap) {
            redeemKeysMap.add(
                PlayerKey(key, value)
            )
        }
        serializedMap["keys"] = serializedPlayerKeys
        serializedMap["locations"] = chests
        serializedMap["keyRedeemMap"] = redeemKeysMap
        return serializedMap
    }

    fun setWeight(weight: Int) {
        if (weight < 0) return
        caseWeight = weight
    }

    fun save() {
        val chestFile: File = File(CaseOpener.mainDataFolder.toString() + "/chests/" + name + ".yml")
        if (!chestFile.exists()) {
            try {
                chestFile.parentFile.mkdir()
                chestFile.createNewFile()
            } catch (ex: Exception) {
                return
            }
        }
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(chestFile)
        config["Chest"] = null
        config["Chest"] = this
        try {
            config.save(chestFile)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    //////// TOP LOGIC ////////
    var topCache: List<Map.Entry<UUID?, Int>> = LinkedList()
    fun forceTopUpdate(async: Boolean) {
        if (async) {
            // I know it should be somwhere else but i am fucking lazy AF
            Bukkit.getScheduler().runTaskAsynchronously(
                CaseOpener.mainPlugin,
                Runnable { forceTopUpdate(false) }
            )
            return
        }
        topCache = LinkedList<Map.Entry<UUID?, Int>>(keys!!.entries)
        (topCache as LinkedList<Map.Entry<UUID?, Int>>).sortWith(Comparator { obj: Map.Entry<UUID?, Int?>?, o1: Map.Entry<UUID?, Int?>? ->
            MinecraftCaseUtils.sortCompare(obj!!, o1 as Map.Entry<UUID, Int>)
        })
    }

    val topKeys: List<Map.Entry<UUID?, Int>>?
        get() {
            if (topCache.size <= 0) {
                forceTopUpdate(true)
                return null
            }
            return topCache
        }
}