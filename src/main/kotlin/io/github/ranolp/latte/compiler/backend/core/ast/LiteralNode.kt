package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType

sealed class LiteralNode(name: String, token: Token) : Node(name, token)

class IntNode(token: Token) : LiteralNode("Int", token) {
    val value: Int = token.data.toInt()
    override fun debug(depth: Int): String = " " * depth + "Int($value)"
}
class BooleanNode(token: Token) : LiteralNode("Int", token) {
    val value: Boolean = token.type === TokenType.TRUE
    override fun debug(depth: Int): String = " " * depth + "Boolean($value)"
}

class DecimalNode(token: Token) : LiteralNode("Decimal", token) {
    val value: Double = token.data.toDouble()
    override fun debug(depth: Int): String = " " * depth + "Decimal($value)"
}

class StringNode(token: Token) : LiteralNode("String", token) {
    val value: String = token.data
    override fun debug(depth: Int): String = " " * depth + "String($value)"
}