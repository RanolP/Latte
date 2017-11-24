package io.github.ranolp.latte.compiler.backend.howtomakeparser

import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType

sealed class SyntaxPart<out T : SyntaxPart<T>> {
    var mapping: ((List<Token>) -> Node?)? = null
    var name: String = "Unnamed"
    var flag: Int = 0
    abstract val self: T
    val able: T
        get() {
            val self = self
            self.flag = flag or Flags.ABLE.id
            return self
        }
    val zeroOrMore: T
        get() {
            val self = self
            self.flag = flag or Flags.ZERO_OR_MORE.id
            return self
        }
    val more: T
        get() {
            val self = self
            self.flag = flag or Flags.MORE.id
            return self
        }

    fun flagged(flags: Flags) = flag.flagged(flags)
    val flagged
        get() = flag.flagged

    val flagToString: String
        get() = flag.flagToString

    abstract fun debug(): String

    abstract operator fun plus(syntaxPart: SyntaxPart<*>): Syntax

    operator fun invoke(name: String? = null, mapper: (List<Token>) -> Node?): T {
        if (name != null) {
            this.name = name
        }
        mapping = mapper
        return self
    }
}

private fun SyntaxPart<*>.toList(): List<SimpleSyntax> = when (this) {
    is Syntax -> parts
    is SimpleSyntax -> listOf(this)
}

class Syntax(val head: SimpleSyntax, val tails: List<SimpleSyntax>) : SyntaxPart<Syntax>() {
    val parts by lazy {
        listOf(head) + tails
    }
    override val self by lazy {
        Syntax(head, tails.map { it.self }).also {
            it.mapping = mapping
            it.flag = flag
            it.name = name
        }
    }

    constructor(left: SimpleSyntax, right: SyntaxPart<*>) : this(left, right.toList())


    override fun debug(): String = parts.joinToString(" ") { it.debug() }.let {
        if (flagged) "($it)$flagToString" else it + if (mapping != null) " → $name" else ""
    }

    override fun plus(syntaxPart: SyntaxPart<*>): Syntax = Syntax(head, tails + syntaxPart.toList())
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Syntax) return false

        return head == other.head && tails == other.tails && flag == other.flag
    }

    override fun hashCode(): Int = 31 * head.hashCode() + tails.hashCode()
}


class SimpleSyntax(val tokenType: TokenType) : SyntaxPart<SimpleSyntax>() {
    override val self by lazy {
        SimpleSyntax(tokenType).also {
            it.mapping = mapping
            it.name = name
            it.flag = flag
        }
    }

    override fun debug(): String = "$tokenType$flagToString" + if (mapping != null) " → $name" else ""

    override fun plus(syntaxPart: SyntaxPart<*>): Syntax = Syntax(this, syntaxPart)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleSyntax) return false

        return tokenType == other.tokenType && flag == other.flag
    }

    override fun hashCode(): Int = 31 * tokenType.hashCode() + flag
}