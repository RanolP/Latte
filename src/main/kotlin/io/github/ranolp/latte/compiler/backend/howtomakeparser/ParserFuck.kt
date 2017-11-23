package io.github.ranolp.latte.compiler.backend.howtomakeparser

import io.github.ranolp.latte.compiler.backend.core.ast.DecimalNode
import io.github.ranolp.latte.compiler.backend.core.ast.IntNode
import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.backend.core.ast.StringNode
import io.github.ranolp.latte.compiler.backend.howtomakeparser.tree.ParentTree
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType.*

fun main(args: Array<String>) {
    val int = INTEGER(::IntNode, "Int")
    val decimal = DECIMAL(::DecimalNode, "Decimal")
    val string = STRING(::StringNode, "String")

    val typeDecl = IDENTIFIER + COLON + IDENTIFIER

    val packageDecl = (PACKAGE + IDENTIFIER.more)("Package") { null }
    val variableDecl = (LET + MUT.able + typeDecl.able + (ASSIGN /* + expression */).able)("Variable") { null }
    val functionDecl = (FN + IDENTIFIER + (LEFT_BRACKET + typeDecl.zeroOrMore + RIGHT_BRACKET).able /* + (block or expression) */)("Function") { null }

    println(int.debug())
    println(decimal.debug())
    println(string.debug())
    println(packageDecl.debug())
    println(variableDecl.debug())
    println(functionDecl.debug())
}

object ParserFuck {
    val root = ParentTree("root")


    fun parse(tokens: List<Token>): Node? {
        return null
    }
}