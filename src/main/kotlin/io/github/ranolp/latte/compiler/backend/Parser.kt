package io.github.ranolp.latte.compiler.backend

import io.github.ranolp.latte.compiler.backend.core.ast.DecimalNode
import io.github.ranolp.latte.compiler.backend.core.ast.IntNode
import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.backend.core.ast.StringNode
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType
import io.github.ranolp.latte.compiler.core.TokenType.*

object Parser {

    @JvmStatic
    fun main(args: Array<String>) {
        val int = INTEGER(::IntNode, "IntNode")
        val decimal = DECIMAL(::DecimalNode, "DecimalNode")
        val string = STRING(::StringNode, "StringNode")
        val typeDecl = IDENTIFIER + COLON + IDENTIFIER
        val expression = Syntax()
        val block = Syntax()

        val varDecl = LET + MUT.able + typeDecl + (ASSIGN + expression).able
        val funcDecl = FN + IDENTIFIER + (LEFT_BRACKET + typeDecl.zeroOrMore + RIGHT_BRACKET).able + block
        val packageDecl = PACKAGE + IDENTIFIER.more

        println(int.debug())
        println(decimal.debug())
        println(string.debug())
        println(typeDecl)
        println(varDecl)
        println(funcDecl)
        println(packageDecl)
    }


    private enum class Flags(val id: Int) {
        ABLE(0b1),
        ZERO_OR_MORE(0b10),
        MORE(0b100);

        interface Flagged<out T : Flagged<T>> {
            var flag: Int
            val self: T
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

            fun flagged(flags: Flags) = flag and flags.id == flags.id
            val flagged
                get() = flag != 0
            val flagToString
                get() = if (flagged(Flags.ABLE)) "?" else "" + if (flagged(Flags.ZERO_OR_MORE)) "*" else "" + if (flagged(
                        Flags.MORE)) "+" else ""
        }
    }

    private interface SyntaxPart {
        val self: SyntaxPart
    }

    private class SyntaxToken(val tokenType: TokenType) : Flags.Flagged<SyntaxToken>, SyntaxPart {
        override var flag = 0
        override val self: SyntaxToken
            get() = SyntaxToken(tokenType).also {
                it.flag = flag
            }
        var tree: ChildTree? = null


        fun asLALR(initializer: (Token) -> Node?, name: String = "Unnamed", register: Boolean = false): ChildTree {
            val tree = this.tree
            return if (tree == null) {
                val child = ChildTree(tokenType, { initializer(it[0]) }, name)
                if (register) {
                    root.children += child
                }
                this.tree = child
                child
            } else tree
        }

        operator fun plus(syntaxToken: SyntaxToken): Syntax = Syntax().also {
            it.parts.add(this)
            it.parts.add(syntaxToken)
        }

        operator fun plus(tokenType: TokenType): Syntax = Syntax().also {
            it.parts.add(this)
            it.parts.add(SyntaxToken(tokenType))
        }

        operator fun plus(syntax: Syntax): Syntax = Syntax().also {
            it.parts.add(this)
            it.parts.add(syntax)
        }

        override fun toString(): String = "$tokenType$flagToString"
    }

    private class Syntax : Flags.Flagged<Syntax>, SyntaxPart {
        override var flag = 0
        override val self: Syntax
            get() = Syntax().also {
                it.flag = flag
                it.parts.addAll(parts.map { it.self })
            }
        val parts = mutableListOf<SyntaxPart>()

        fun asLALR(initializer: (Token) -> Node?, name: String = "Unnamed", register: Boolean = false): ParentTree {
           TODO()
        }

        operator fun plus(syntaxToken: SyntaxToken): Syntax = Syntax().also {
            it.parts.add(this)
            it.parts.add(syntaxToken)
        }

        operator fun plus(tokenType: TokenType): Syntax = Syntax().also {
            it.parts.add(this)
            it.parts.add(SyntaxToken(tokenType))
        }

        operator fun plus(syntax: Syntax): Syntax = Syntax().also {
            it.parts.add(this)
            it.parts.add(syntax)
        }

        override fun toString(): String = (if (flagged) "(" else "") + parts.joinToString(" ") + (if (flagged) ")" else "") + flagToString
    }

    private operator fun TokenType.invoke(task: (Token) -> Node, name: String = "Unnamed") = SyntaxToken(this).asLALR(
            task,
            name)

    private operator fun TokenType.plus(tokenType: TokenType) = SyntaxToken(this) + tokenType
    private operator fun TokenType.plus(syntaxToken: SyntaxToken) = SyntaxToken(this) + syntaxToken
    private operator fun TokenType.plus(syntax: Syntax) = SyntaxToken(this) + syntax

    private val TokenType.able: SyntaxToken
        get() = SyntaxToken(this).able
    private val TokenType.zeroOrMore: SyntaxToken
        get() = SyntaxToken(this).zeroOrMore
    private val TokenType.more: SyntaxToken
        get() = SyntaxToken(this).more

    private interface LALRTree {
        fun match(tokens: List<Token>, from: Int = 0, cursor: Int = 0): Node?
        fun debug(depth: Int = 0): String
    }

    private open class ParentTree(val name: String = "Unnamed") : LALRTree {
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

    private class ChildTree(val tokenType: TokenType, val generator: (List<Token>) -> Node?,
            val name: String = "Unnamed") : LALRTree {
        override fun match(tokens: List<Token>, from: Int,
                cursor: Int): Node? = if (tokens[cursor].type == tokenType) generator(tokens.subList(from,
                cursor)) else null

        override fun debug(depth: Int) = " " * depth + "$tokenType -> $name"
    }

    private val root = ParentTree()

    fun parse(tokens: List<Token>): Node? = root.match(tokens)
}

