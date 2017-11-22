package io.github.ranolp.latte.compiler.backend

import io.github.ranolp.latte.compiler.core.Token

data class ParseContext(val tokens: List<Token>) {
    fun require(run: ParseContext.() -> Unit) {

    }
}