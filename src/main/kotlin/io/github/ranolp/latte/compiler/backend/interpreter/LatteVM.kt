package io.github.ranolp.latte.compiler.backend.interpreter

import io.github.ranolp.latte.compiler.backend.core.objects.LFunctionStructure
import io.github.ranolp.latte.compiler.backend.core.objects.LImport
import io.github.ranolp.latte.compiler.backend.core.objects.LPackage
import io.github.ranolp.latte.compiler.backend.core.objects.LScope
import io.github.ranolp.latte.compiler.backend.interpreter.extern.PrintFunction
import io.github.ranolp.latte.compiler.backend.interpreter.extern.PrintlnFunction

class LatteVM {
    private val packageLevelFunctions = mutableMapOf<LPackage, MutableSet<LFunctionStructure>>()
    private val scopes = mutableListOf<LScope>()
    private val defaultImports: Set<LImport>
    val imports: Set<LImport>
        get() = defaultImports + (if (scopes.size > 0) scopes.last().imports else emptySet())
    val functions: Set<LFunctionStructure>
        get() = packageLevelFunctions.values.flatten().toSet()

    init {
        plusAssign(LPackage("latte.lang") to PrintFunction)
        plusAssign(LPackage("latte.lang") to PrintlnFunction)

        defaultImports = setOf(LImport("latte.lang", "*"))
    }

    operator fun plusAssign(pair: Pair<LPackage, LFunctionStructure>) {
        val set = packageLevelFunctions.getOrPut(pair.first, ::mutableSetOf)
        if (set.any { it.signature == pair.second.signature }) {
            // function redefine
            throw VMError("redefine function ${pair.second.signature}")
        }
        set += pair.second
    }

    fun getPackageLevelFunctions(lPackage: LPackage): Set<LFunctionStructure> {
        return packageLevelFunctions.getOrPut(lPackage, ::mutableSetOf)
    }

    fun inscope(scope: LScope) {
        scopes += scope
    }

    fun scope(index: Int = 0): LScope {
        if (scopes.size <= index) {
            throw VMError("Scope out of index")
        }
        return scopes[scopes.size - index - 1]
    }

    fun outscope() {
        scopes.removeAt(scopes.size - 1)
    }
}
