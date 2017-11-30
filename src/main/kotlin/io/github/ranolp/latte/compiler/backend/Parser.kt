package io.github.ranolp.latte.compiler.backend

import io.github.ranolp.latte.compiler.backend.core.ast.*
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType
import io.github.ranolp.latte.compiler.core.TokenType.*

private typealias Test = List<String>
object Parser {
    private data class ParseContext(val tokens: List<Token>, var cursor: Int) {
        val size: Int
            get() = tokens.size
        val current: Token
            get() = get(cursor)
        val hasNext: Boolean
            get() = safe(cursor + 1)
        val next: Token
            get() = get(cursor + 1)
        val isEOL: Boolean
            get() = when (current.type) {
                SEMICOLON, LINEFEED -> true
                else -> false
            }

        fun safe(c: Int = cursor): Boolean = c < size
        operator fun get(index: Int): Token = tokens[index]
        fun current(tokenType: TokenType): Boolean = current.type === tokenType
        fun next(tokenType: TokenType): Boolean = next.type === tokenType
        fun cursorNext(): Int = ++cursor

        fun skipWhitespace() {
            while (isEOL) {
                cursorNext()
            }
        }
    }

    private fun List<*>.safe(cursor: Int): Boolean = cursor < size

    // literal

    private fun int(token: Token): Node? = if (token.type === INTEGER) IntNode(token) else null
    private fun decimal(token: Token): Node? = if (token.type === DECIMAL) DecimalNode(token) else null
    private fun string(token: Token): Node? = if (token.type === STRING) StringNode(token) else null

    private fun packageDeclaration(parseContext: ParseContext): PackageNode? {
        // todo: package declaration can has a annotation
        return if (parseContext.current(PACKAGE)) {
            val start = parseContext.current
            parseContext.cursorNext()
            val names = resolveIdentifiers(parseContext, DOT)
            if (names.isEmpty()) {
                throw ParseError("Package name must not be empty")
            }
            PackageNode(start, names)
        } else {
            null
        }
    }

    private fun resolveIdentifiers(parseContext: ParseContext, separator: TokenType): List<Token> {
        val result = mutableListOf<Token>()
        loop@ while (parseContext.safe() && parseContext.current(IDENTIFIER)) {
            result += parseContext.current
            if (parseContext.hasNext) {
                val curr = parseContext.next
                when (curr.type) {
                    SEMICOLON, LINEFEED -> break@loop
                    separator -> {
                        result += parseContext[parseContext.cursorNext()]
                    }
                    else -> {
                        // loop will break
                    }
                }
            }
            parseContext.cursorNext()
        }
        if(parseContext.current(IDENTIFIER)) {
            parseContext.cursorNext()
        }
        return result
    }

    private fun importDeclaration(parseContext: ParseContext): ImportNode? {
        return if (parseContext.current(IMPORT)) {
            val start = parseContext.current
            parseContext.cursorNext()
            val names = resolveIdentifiers(parseContext, DOT)
            if (names.isEmpty()) {
                throw ParseError("Imported name must not be empty")
            }
            val asToken = if (parseContext.current(AS)) {
                if (!parseContext.next(IDENTIFIER)) {
                    throw ParseError("Import local renaming requires one identifier")
                }
                val result = parseContext.next
                parseContext.cursor += 2
                result
            } else {
                null
            }
            if (!parseContext.isEOL) {
                throw ParseError("Import statement not ends in a line.")
            }
            ImportNode(start, names, asToken)
        } else {
            null
        }
    }

    fun parse(tokens: List<Token>): Node? {
        val context = ParseContext(tokens, 0)
        context.skipWhitespace()
        // Preamble
        val pkg = packageDeclaration(context)
        context.skipWhitespace()
        val imports = mutableListOf<ImportNode>()
        var import: ImportNode? = importDeclaration(context)
        while (import !== null) {
            imports += import
            context.skipWhitespace()
            import = importDeclaration(context)
        }

        // Top-level objects
        // class
        // object
        // package-level function
        // package-level property
        // package-level typealias

        if (context.current(EOF)) {
            // throw ParseError("Unused tokens found")
        }

        return LatteFileNode(tokens.last(), tokens.size, pkg, imports)
    }
}

