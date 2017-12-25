package io.github.ranolp.latte.compiler.backend.core.objects

import io.github.ranolp.latte.compiler.backend.core.ast.ImportNode
import io.github.ranolp.latte.compiler.backend.interpreter.LatteVM

class LImport(val packageName: String?, val destination: String, val asName: String? = null) : LValue {
    companion object {
        operator fun invoke(importNode: ImportNode) = LImport(importNode.packageName,
                importNode.destination,
                importNode.asName)
    }

    fun toFunctions(latteVM: LatteVM): Set<LFunctionStructure> = latteVM.getPackageLevelFunctions(LPackage(packageName)).filter { destination == "*" || it.signature.name == destination }.toSet()

    override fun toString(): String = "LImport(${if (packageName == null) "" else "$packageName."}$destination${if (asName == null) "" else "as $asName"})"
}
