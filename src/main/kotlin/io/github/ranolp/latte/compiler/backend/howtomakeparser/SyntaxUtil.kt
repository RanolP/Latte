package io.github.ranolp.latte.compiler.backend.howtomakeparser

import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType

val TokenType.syntax
    get() = SimpleSyntax(this)
val TokenType.able
    get() = syntax.able
val TokenType.zeroOrMore
    get() = syntax.zeroOrMore
val TokenType.more
    get() = syntax.more

operator fun TokenType.invoke(mapper: (Token) -> Node?, name: String? = null) = syntax(name) { mapper(it[0]) }

operator fun TokenType.plus(syntaxPart: SyntaxPart<*>) = syntax + syntaxPart
operator fun TokenType.plus(tokenType: TokenType) = syntax + tokenType.syntax
operator fun SyntaxPart<*>.plus(tokenType: TokenType) = this + tokenType.syntax
infix fun TokenType.or(tokenType: TokenType) = syntax or tokenType.syntax