package io.github.ranolp.latte.compiler.backend.howtomakeparser.tree

import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.backend.howtomakeparser.SimpleSyntax
import io.github.ranolp.latte.compiler.backend.howtomakeparser.Syntax
import io.github.ranolp.latte.compiler.backend.howtomakeparser.SyntaxPart
import io.github.ranolp.latte.compiler.backend.howtomakeparser.flagToString
import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType

sealed class LALRTree {
    abstract fun match(tokens: List<Token>, from: Int = 0, cursor: Int = 0): Node?
    abstract fun debug(depth: Int = 0): String
}

class ParentTree(val name: String, val tokenType: TokenType? = null, val flag: Int = 0) : LALRTree() {
    val children: MutableList<LALRTree> = mutableListOf()
    override fun match(tokens: List<Token>, from: Int, cursor: Int): Node? {
        var cursor = cursor
        if (tokenType == null || (tokens[cursor].type == tokenType && tokens.size > cursor + 1).also { if (it) cursor++ }) {
            for (child in children) {
                child.match(tokens, cursor)?.let {
                    return it
                }
            }
        }
        return null
    }

    operator fun plusAssign(syntaxPart: SyntaxPart<*>) {
        children += syntaxPart.toLALRTree()
    }

    override fun debug(
            depth: Int): String = " " * depth + "$name${flag.flagToString} -> \n" + children.joinToString("\n") {
        it.debug(depth + 1)
    }
}

class ChildTree(val tokenType: TokenType, val generator: (List<Token>) -> Node?, val name: String = "Unnamed",
        val flag: Int) : LALRTree() {
    override fun match(tokens: List<Token>, from: Int,
            cursor: Int): Node? = if (tokens[cursor].type == tokenType) generator(tokens.subList(from,
            cursor)) else null

    override fun debug(depth: Int) = " " * depth + "$tokenType${flag.flagToString} -> $name"
}

fun SyntaxPart<*>.toLALRTree(): LALRTree = when (this) {
    is SimpleSyntax -> {
        val generator = mapping ?: throw IllegalArgumentException("Syntax does not have generator")
        ChildTree(tokenType, generator, name, flag)
    }
    is Syntax -> {
        val parent = ParentTree(head.tokenType.toString(), head.tokenType, flag)
        var current: ParentTree = parent
        for (tail in tails.dropLast(1)) {
            val temp = ParentTree(tail.name, tail.tokenType, tail.flag)
            current.children += temp
            current = temp
        }
        val last = tails.last()
        val generator = mapping ?: throw IllegalArgumentException("Syntax does not have generator")
        current.children += ChildTree(last.tokenType, generator, name, last.flag)
        parent
    }
}