package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token

sealed class TopLevelNode(name: String, token: Token) : Node(name, token)

sealed class ParentNode() {

}

class FunctionNode(token: Token, parent: ParentNode?, val functionName: String, val arguments: List<Node>, val children: List<Node>) : TopLevelNode(
        "Function",
        token) {
    override fun debug(depth: Int): String = " " * depth + "FunctionNode($functionName)"
}