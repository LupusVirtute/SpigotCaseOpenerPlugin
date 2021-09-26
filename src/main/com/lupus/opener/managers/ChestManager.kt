package com.lupus.opener.managers

import com.lupus.opener.chests.MinecraftCase
import com.lupus.opener.CaseOpener
import com.lupus.opener.chests.MinecraftKey
import com.lupus.opener.runnables.ChestSave
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*

object ChestManager {
    private val mcCases = HashMap<String, MinecraftCase>()
    private val mcCaseLocation = HashMap<Location, String>()
    fun clear() {
        mcCaseLocation.clear()
        mcCases.clear()
    }

    var highestWeight = 0
    fun addCase(mcCase: MinecraftCase) {
        if (mcCase.caseWeight > highestWeight) {
            highestWeight = mcCase.caseWeight
        }
        mcCases[mcCase.name?.toLowerCase() ?: ""] = mcCase
    }

    val randomCase: MinecraftCase
        get() {
            var highestWeight = 0
            for (mcCase in allCases) {
                if (highestWeight < mcCase.caseWeight) highestWeight = mcCase.caseWeight
            }
            highestWeight /= 100
            var leastWeight: MinecraftCase = allCases.random()
            val rnd = Random()
            for (mcCase in allCases) {
                val random: Int = rnd.nextInt(mcCase.caseWeight) + highestWeight
                if (random > mcCase.caseWeight) {
                    return mcCase
                }
                if (leastWeight == null) leastWeight =
                    mcCase else if (leastWeight.caseWeight > mcCase.caseWeight) leastWeight = mcCase
            }
            return leastWeight
        }

    fun addCaseLocation(loc: Location, name: String?) {
        mcCaseLocation[loc] = name!!.toLowerCase()
    }

    fun getCase(name: String?): MinecraftCase? {
        return mcCases[name!!.toLowerCase()]
    }

    operator fun contains(chest: String): Boolean {
        return mcCases.containsKey(chest.toLowerCase())
    }

    fun getKeysForPlayer(p: Player): Array<MinecraftKey?> {
        val c: Collection<MinecraftCase> = mcCases.values
        val keys = arrayOfNulls<MinecraftKey>(c.size)
        var i = 0
        for (minecraftCase in c) {
            keys[i] = MinecraftKey(minecraftCase.name, minecraftCase.getKeyAmount(p))
            i++
        }
        return keys
    }

    fun getCaseFromLocation(location: Location): MinecraftCase? {
        return mcCases[mcCaseLocation[location]]
    }

    fun removeCaseLocation(block: Block): Boolean {
        return removeCaseLocation(block.location)
    }

    fun removeCaseLocation(loc: Location): Boolean {
        val r = mcCaseLocation.remove(loc)
        return r != null
    }

    val all: Set<String>
        get() = mcCases.keys
    val allCases: Collection<MinecraftCase>
        get() = mcCases.values

    fun saveAll(async: Boolean) {
        if (async) {
            CaseOpener.mainPlugin.let { ChestSave(mcCases).runTaskAsynchronously(it) }
        } else {
            ChestSave(mcCases).run()
        }
    }
}