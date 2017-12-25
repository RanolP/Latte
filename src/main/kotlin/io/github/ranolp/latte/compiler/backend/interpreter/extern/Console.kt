package io.github.ranolp.latte.compiler.backend.interpreter.extern

import io.github.ranolp.latte.compiler.backend.core.ast.LiteralNode
import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.backend.core.objects.LExternFunction
import io.github.ranolp.latte.compiler.backend.interpreter.LatteVM

object PrintFunction : LExternFunction("print") {
    override fun run(latteVM: LatteVM, arguments: List<Node>?) {
        val it = arguments?.get(0)
        if (it is LiteralNode) {
            print(it.value)
        } else {
            print(it?.debug())
        }
    }
}

object PrintlnFunction : LExternFunction("println") {
    override fun run(latteVM: LatteVM, arguments: List<Node>?) {
        val it = arguments?.get(0)
        if (it is LiteralNode) {
            println(it.value)
        } else {
            println(it?.debug())
        }
    }
}
