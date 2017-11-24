package io.github.ranolp.latte.compiler.backend.howtomakeparser.tree

import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.backend.howtomakeparser.SingleSyntaxPart
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

class ParentTree(val name: String, singleSyntaxPart: SingleSyntaxPart<*>? = null, val flag: Int = 0) : LALRTree() {
    val children: MutableList<LALRTree> = mutableListOf()
    private val matcher: ((TokenType) -> Boolean)? = if (singleSyntaxPart != null) singleSyntaxPart!!::match else null
    override fun match(tokens: List<Token>, from: Int, cursor: Int): Node? {
        var cursor = cursor
        val matcher = matcher
        if (matcher == null || (matcher(tokens[cursor].type) && tokens.size > cursor + 1).also { if (it) cursor++ }) {
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

    fun reduce() {

    }

    override fun debug(
            depth: Int): String = " " * depth + "$name${flag.flagToString} -> \n" + children.joinToString("\n") {
        it.debug(depth + 1)
    }
}

class ChildTree(singleSyntaxPart: SingleSyntaxPart<*>, val name: String = "Unnamed", val flag: Int) : LALRTree() {
    val matcher: (TokenType) -> Boolean = singleSyntaxPart::match
    val debugName: String = singleSyntaxPart.debug()
    val generator = singleSyntaxPart.mapping ?: throw IllegalArgumentException("Syntax does not have generator")

    override fun match(tokens: List<Token>, from: Int,
            cursor: Int): Node? = if (matcher(tokens[cursor].type)) generator(tokens.subList(from, cursor)) else null

    override fun debug(depth: Int) = " " * depth + "$debugName${flag.flagToString} -> $name"
}

fun SyntaxPart<*>.toLALRTree(): LALRTree = when (this) {
    is SingleSyntaxPart<*> -> {
        val generator = mapping ?: throw IllegalArgumentException("Syntax does not have generator")
        ChildTree(this, name, flag)
    }
    is Syntax -> {
        val parent = ParentTree(head.debug(), head, flag)
        var current: ParentTree = parent
        for (tail in tails.dropLast(1)) {
            val temp = ParentTree(tail.name, tail, tail.flag)
            current.children += temp
            current = temp
        }
        val last = tails.last()
        val generator = mapping ?: throw IllegalArgumentException("Syntax does not have generator")
        current.children += ChildTree(last, name, last.flag)
        parent
    }
}