package io.github.ranolp.latte.compiler.backend.interpreter

import io.github.ranolp.latte.compiler.backend.core.ast.FunctionNode
import io.github.ranolp.latte.compiler.backend.core.ast.LatteFileNode
import io.github.ranolp.latte.compiler.backend.core.objects.LFunction
import io.github.ranolp.latte.compiler.backend.core.objects.LPackage

object LatteFileInterpreter {
    fun interpret(latteVM: LatteVM, latteFileNode: LatteFileNode) {
        val pkg = LPackage(latteFileNode.packageNode)
        for (topLevelNode in latteFileNode.topLevelObjects) {
            when (topLevelNode) {
                is FunctionNode -> {
                    latteVM += pkg to LFunction(topLevelNode)
                }
            }
        }

        latteVM.getPackageLevelFunctions(pkg).firstOrNull { it.signature.name == "main" }?.let {
            it(latteVM)
        }
    }
}
