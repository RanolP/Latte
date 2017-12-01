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
        val before: Token
            get() = get(cursor - 1)
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
        fun current(tokenType: TokenType, cause: String): Token = if (current(tokenType)) {
            get(cursorNext())
        } else throw ParseError(cause)

        fun next(tokenType: TokenType, cause: String): Token = if (next(tokenType)) {
            cursorNext()
            current
        } else throw ParseError(cause)

        fun cursorNext(): Int = ++cursor
        fun <T> cursorNext(execution: ParseContext.() -> T): T {
            val result = execution()
            cursorNext()
            return result
        }

        fun skipWhitespace() {
            while (isEOL) {
                cursorNext()
            }
        }

        operator fun <T : Node> invoke(run: (ParseContext) -> T?): T? {
            val from = cursor
            val result = run(this)
            if (result === null) {
                cursor = from
                return null
            }
            return result
        }
    }

    // literal

    private fun int(token: Token): Node? = if (token.type === INTEGER) IntNode(token) else null
    private fun decimal(token: Token): Node? = if (token.type === DECIMAL) DecimalNode(token) else null
    private fun string(token: Token): Node? = if (token.type === STRING) StringNode(token) else null

    // Preamble

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

    private fun importDeclaration(parseContext: ParseContext): ImportNode? {
        return if (parseContext.current(IMPORT)) {
            val start = parseContext.current
            parseContext.cursorNext()
            val names = resolveIdentifiers(parseContext, DOT)
            if (names.isEmpty()) {
                throw ParseError("Imported name must not be empty")
            }
            val asToken = if (parseContext.current(AS)) {
                parseContext.cursorNext { next(IDENTIFIER, "Import local renaming requires a identifier") }
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

    private fun topLevelObjects(parseContext: ParseContext): TopLevelNode? {
        return parseContext { functionDeclaration(it) }
    }

    private fun functionDeclaration(parseContext: ParseContext, parent: ParentNode? = null): FunctionNode? {
        val modifier = accessModifier(parseContext)
        return if (parseContext.current(FN)) {
            val token = parseContext.current
            val name = parseContext.next(IDENTIFIER, "Function name must be a identifier").data
            val arguments: List<Node> = if (parseContext.current(LEFT_BRACKET)) {
                // argument
                emptyList()
            } else emptyList()
            val childrens: List<Node> = if (parseContext.current(ASSIGN)) {
                emptyList()
            } else {
                emptyList()
            }
            FunctionNode(token, parent, name, arguments, childrens)
        } else {
            null
        }
    }

    // Utilities

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
        if (parseContext.current(IDENTIFIER)) {
            parseContext.cursorNext()
        }
        return result
    }

    private fun accessModifier(parseContext: ParseContext): Token? = when (parseContext.current.type) {
        PUBLIC, PROTECTED, PRIVATE -> {
            parseContext.cursorNext()
            parseContext.before
        }
        else -> {
            null
        }
    }


    fun parse(tokens: List<Token>): Node? {
        val context = ParseContext(tokens, 0)
        context.skipWhitespace()
        // Preamble
        val pkg = context(this::packageDeclaration)
        context.skipWhitespace()
        val imports = mutableListOf<ImportNode>()
        var import: ImportNode? = context(this::importDeclaration)
        while (import !== null) {
            imports += import
            context.skipWhitespace()
            import = context(this::importDeclaration)
        }
        val topLevelNodes = mutableListOf<TopLevelNode>()
        context.skipWhitespace()
        var node: TopLevelNode? = context(this::topLevelObjects)
        while (node !== null) {
            topLevelNodes += node
            context.skipWhitespace()
            node = context(this::topLevelObjects)
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

        return LatteFileNode(tokens.last(), pkg, imports, topLevelNodes)
    }
}

