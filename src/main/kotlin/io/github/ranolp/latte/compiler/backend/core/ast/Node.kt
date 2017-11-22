package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.core.Token

abstract class Node(val name: String, val token: Token, val skip: Int) {
    internal open fun debug(depth: Int = 0): String = indents(depth) + name + ": " + token.data

    protected fun indents(depth: Int): String {
        val result = StringBuilder()
        for (j in 0..depth) {
            result.append(" ")
        }
        return result.toString()
    }
}