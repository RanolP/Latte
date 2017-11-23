package io.github.ranolp.latte.compiler.backend.howtomakeparser.tree

import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType

sealed class LALRTree {
    abstract fun match(tokens: List<Token>, from: Int = 0, cursor: Int = 0): Node?
    abstract fun debug(depth: Int = 0): String
}

class ParentTree(val name: String) : LALRTree() {
    val children: MutableList<LALRTree> = mutableListOf()
    override fun match(tokens: List<Token>, from: Int, cursor: Int): Node? {
        for (child in children) {
            child.match(tokens, cursor)?.let {
                return it
            }
        }
        return null
    }

    override fun debug(depth: Int): String = " " * depth + "$name -> \n" + children.joinToString("\n") {
        it.debug(depth + 1)
    }
}

class ChildTree(val tokenType: TokenType, val generator: (List<Token>) -> Node?,
        val name: String = "Unnamed") : LALRTree() {
    override fun match(tokens: List<Token>, from: Int,
            cursor: Int): Node? = if (tokens[cursor].type == tokenType) generator(tokens.subList(from,
            cursor)) else null

    override fun debug(depth: Int) = " " * depth + "$tokenType -> $name"
}