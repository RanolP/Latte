package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token

sealed class TopLevelNode(name: String, token: Token) : Node(name, token)

sealed class ParentNode() {

}

class FunctionNode(token: Token, val parent: ParentNode?, val functionName: String, val arguments: List<StatementNode>, val children: List<StatementNode>) : TopLevelNode(
        "Function",
        token) {
    override fun debug(depth: Int): String = " " * depth + "FunctionNode($functionName)\n" + " " * (depth + 1) + "Arguments\n" + arguments.joinToString(
            "\n") {
        it.debug(depth + 2)
    } + " " * (depth + 1) + "Codes\n" + children.joinToString("\n") {
        it.debug(depth + 2)
    }
}
