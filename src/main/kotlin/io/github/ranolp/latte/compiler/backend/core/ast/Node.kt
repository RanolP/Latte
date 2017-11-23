package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token

abstract class Node(val name: String, val token: Token, val skip: Int) {
    internal open fun debug(depth: Int = 0): String = " " * depth + name + ": " + token.data
}