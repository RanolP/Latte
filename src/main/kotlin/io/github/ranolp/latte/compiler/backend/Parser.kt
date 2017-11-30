package io.github.ranolp.latte.compiler.backend

import io.github.ranolp.latte.compiler.backend.core.ast.*
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType
import io.github.ranolp.latte.compiler.core.TokenType.*

object Parser {
    private fun List<*>.safe(cursor: Int): Boolean = cursor < size

    // literal

    private fun int(token: Token): Node? = if (token.type === INTEGER) IntNode(token) else null
    private fun decimal(token: Token): Node? = if (token.type === DECIMAL) DecimalNode(token) else null
    private fun string(token: Token): Node? = if (token.type === STRING) StringNode(token) else null

    private fun packageDeclaration(tokens: List<Token>, cursor: Int): Node? {
        // todo: package declaration can has a annotation
        return if (tokens[cursor].type === PACKAGE) {
            val names = resolveIdentifiers(tokens, cursor + 1, DOT)
            if (names.isEmpty()) {
                throw ParseError("Package name must not be empty")
            }
            PackageNode(tokens[cursor], names)
        } else {
            null
        }
    }

    private fun endOfLine(tokens: List<Token>, cursor: Int): Boolean = when (tokens[cursor].type) {
        SEMICOLON, LINEFEED -> true
        else -> false
    }

    private fun resolveIdentifiers(tokens: List<Token>, cursor: Int, separator: TokenType): List<Token> {
        val result = mutableListOf<Token>()
        var i = cursor
        loop@ while (tokens.safe(i) && tokens[i].type === IDENTIFIER) {
            result += tokens[i]
            if (tokens.safe(i + 1)) {
                val curr = tokens[i + 1]
                when (curr.type) {
                    SEMICOLON, LINEFEED -> break@loop
                    separator -> {
                        result += tokens[++i]
                    }
                    else -> throw ParseError("Unresolvable token found: ${curr.data}")
                }
            }
            i++
        }
        return result
    }

    private fun importDeclaration(tokens: List<Token>, cursor: Int): Node? {
        return if (tokens[cursor].type === IMPORT) {
            val names = resolveIdentifiers(tokens, cursor + 1, DOT)
            val i = cursor + 1 + names.size
            val asToken = if (tokens[i].type == AS) {
                if (tokens[i + 1].type !== IDENTIFIER) {
                    throw ParseError("Import local renaming requires one identifier")
                }
                tokens[i + 1]
            } else {
                null
            }
            if (names.isEmpty()) {
                throw ParseError("Imported name must not be empty")
            }
            if (!endOfLine(tokens, cursor)) {
                throw ParseError("Import statement not ends in a line.")
            }
            ImportNode(tokens[cursor], names, asToken)
        } else {
            null
        }
    }

    fun parse(tokens: List<Token>): Node? {
        val pkg = packageDeclaration(tokens, 0)
        return pkg
    }
}

