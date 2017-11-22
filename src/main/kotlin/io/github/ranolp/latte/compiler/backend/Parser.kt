package io.github.ranolp.latte.compiler.backend

import io.github.ranolp.latte.compiler.backend.core.ast.DecimalNode
import io.github.ranolp.latte.compiler.backend.core.ast.IntNode
import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.backend.core.ast.StringNode
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType
import io.github.ranolp.latte.compiler.core.TokenType.*

object Parser {
    val int = `INTEGER`(::IntNode)
    val decimal = `DECIMAL`(::DecimalNode)
    val string = `STRING`(::StringNode)

    private class Data(predicate: (List<Token>, Int) -> Node?) {
        operator fun plus(tokenType: TokenType) {

        }
    }

    operator fun TokenType.invoke(task: (Token) -> Node) {
        TODO("LALR")
    }



    fun parse(tokens: List<Token>): Node? {
        TODO("Implements a parser")
    }
}

