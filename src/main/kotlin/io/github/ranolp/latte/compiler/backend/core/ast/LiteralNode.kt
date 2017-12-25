package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType

sealed class LiteralNode(name: String, token: Token) : StatementNode(name, token) {
    abstract val value: Any?
}

sealed class NumberNode(name: String, token: Token) : LiteralNode(name, token) {
    abstract override val value: Number
}

class IntNode(token: Token) : NumberNode("Int", token) {
    override val value: Int = token.data.toInt()
    override fun debug(depth: Int): String = " " * depth + "Int($value)"
}

class BooleanNode(token: Token) : LiteralNode("Boolean", token) {
    override val value: Boolean = token.type === TokenType.TRUE
    override fun debug(depth: Int): String = " " * depth + "Boolean($value)"
}

class DecimalNode(token: Token) : NumberNode("Decimal", token) {
    override val value: Double = token.data.toDouble()
    override fun debug(depth: Int): String = " " * depth + "Decimal($value)"
}

class StringNode(token: Token) : LiteralNode("String", token) {
    override val value: String = token.data
    override fun debug(depth: Int): String = " " * depth + "String($value)"
}
