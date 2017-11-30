package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token

class IntNode(token: Token) : Node("Int", token, 1) {
    val value: Int = token.data.toInt()
    override fun debug(depth: Int): String = " " * depth + "Int($value)"
}

class DecimalNode(token: Token) : Node("Decimal", token, 1) {
    val value: Double = token.data.toDouble()
    override fun debug(depth: Int): String = " " * depth + "Decimal($value)"
}

class StringNode(token: Token) : Node("String", token, 1) {
    val value: String = token.data
    override fun debug(depth: Int): String = " " * depth + "String($value)"
}