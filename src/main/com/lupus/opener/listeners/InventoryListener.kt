package com.lupus.opener.listeners

import com.lupus.opener.managers.ChestManager
import com.lupus.opener.chests.utils.MinecraftCaseUtils
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.ItemStack

class InventoryListener : Listener {
    @EventHandler
    fun onCraftingInventory(e: PrepareItemCraftEvent) {
        val inv = e.inventory
        if (qualifiesAsCobblex(inv.matrix)) inv.result = ChestManager.randomCase?.cobblex
    }

    @EventHandler
    fun onInventoryInteract(e: InventoryClickEvent) {
        if (e.slot != InventoryType.SlotType.RESULT.ordinal) {
            return
        }
        if (e.clickedInventory !is CraftingInventory) {
            return
        }
        val inv = e.clickedInventory as CraftingInventory
        val res = inv.getResult()
        if (MinecraftCaseUtils.getCobblex(res) == null) {
            return
        }
        if (!qualifiesAsCobblex(inv.getMatrix())) return
        inv.remove(Material.COBBLESTONE)
        val player = e.viewers[0]
        inv.setResult(res)
        if (player != null) {
            (player as Player?)!!.updateInventory()
        }
    }

    fun qualifiesAsCobblex(matrix: Array<ItemStack>): Boolean {
        var incrementer = 0
        for (item in matrix) {
            if (item == null) return false
            if (item.type == Material.COBBLESTONE) {
                if (item.amount == 64) {
                    incrementer++
                }
            }
        }
        return incrementer >= 9
    }
}